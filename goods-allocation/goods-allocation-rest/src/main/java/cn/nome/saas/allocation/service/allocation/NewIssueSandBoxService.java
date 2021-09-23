package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.repository.dao.allocation.IssueDayPeriodMapper;
import cn.nome.saas.allocation.repository.dao.allocation.IssueSandBoxTaskMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.IssueExtraDataMapper;
import cn.nome.saas.allocation.repository.dao.vertical.NewIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueDayPeriod;
import cn.nome.saas.allocation.repository.entity.allocation.IssueNeedStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.repository.entity.vertical.MidSalePredictDO;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewIssueSandBoxService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 线程池
     */
    private ExecutorService issueSandBoxMainPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-sandbox-main-%d").build());

    @Autowired
    private NewIssueBasicService newIssueBasicService;
    @Autowired
    private NewIssueSkuCalcService newIssueSkuCalcService;
    @Autowired
    private NewIssueMatchService newIssueMatchService;
    @Autowired
    private IssueDayPeriodMapper issueDayPeriodMapper;
    @Autowired
    private NewIssueDOMapper newIssueDoMapper;
    @Autowired
    private ShopListCache shopListCache;
    @Autowired
    IssueService issueService;

    @Autowired
    IssueSandBoxTaskMapper issueSandBoxTaskMapper;
    @Autowired
    NewIssueExtraDataMapper newIssueExtraDataMapper;


    /**
     * 沙盘计算
     * @param issueSandBoxTask issueSandBoxTask
     */
    public Map<String, IssueReserveDetailDo> sandBoxCalc(IssueSandBoxTask issueSandBoxTask) {
        int taskId = issueSandBoxTask.getTaskId();
        try {
            Calendar calBegin = Calendar.getInstance();
            calBegin.setTime(issueSandBoxTask.getStartDate());
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(issueSandBoxTask.getEndDate());
            String remark = issueSandBoxTask.getRemark();

            boolean first = true;

            issueSandBoxTaskMapper.updateStatus(taskId, IssueSandBoxTask.RUNNING_STATUS);

            List<DwsDimShopDO> shopDoList = shopListCache.getShopList();
            Map<String, DwsDimShopDO> dwsDimShopDoMap = shopDoList.stream().collect(Collectors.toMap(DwsDimShopDO::getShopId, Function.identity()));

            //回货商品数量 Map<Date, Map<String, HuihuoGoodsDo>>
            Map<Date, Map<String, HuihuoGoodsDo>> issueHuihuoDateMap = new HashMap<>(128);
            //计算类型是沙盘时才计算回货数量
            if (IssueSandBoxTask.CALC_TYPE_SANDBOX.equals(issueSandBoxTask.getCalcType())) {
                List<HuihuoGoodsDo> huihuoList = newIssueDoMapper.getHuihuoGoods();
                for (HuihuoGoodsDo huihuoGoodsDo : huihuoList) {
                    Map<String, HuihuoGoodsDo> issueHuihuoMap = issueHuihuoDateMap.computeIfAbsent(huihuoGoodsDo.getHuihuoDay(), k -> Maps.newHashMap());
                    issueHuihuoMap.put(huihuoGoodsDo.getWarehouseMatCodeSizeNameKey(), huihuoGoodsDo);
                }
            }

            //销售预算Map<String, avg>
            Map<String, BigDecimal> issueSalePredictMap = null;
            if (issueSandBoxTask.getUseSalePredict() == 1) {
                List<MidSalePredictDO> salePredictList = newIssueExtraDataMapper.getIssueSalePredict(calEnd.getTime());
                issueSalePredictMap = salePredictList.stream().collect(Collectors.toMap(MidSalePredictDO::getShopId, MidSalePredictDO::getAvgSalePredict));
            }
            issueSandBoxTask.setIssueSalePredictMap(issueSalePredictMap);

            //outStockMap<warehouseCode, <matCode_sizeId, NewIssueOutStockDo>>
            Map<String, Map<String, NewIssueOutStockDo>> issueOutMap = new HashMap<>(8);
            issueSandBoxTask.setIssueOutMap(issueOutMap);

            //inStock记录表 多线程操作用ConcurrentHashMap Map<shopId, <matCode_sizeId, IssueNeedStockDO>>
            Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap = new ConcurrentHashMap<>(600);
            issueSandBoxTask.setIssueInStockShopIdMap(issueInStockShopIdMap);

            //needStock记录表 多线程操作用ConcurrentHashMap Map<shopId, <matCode_sizeId, IssueNeedStockDO>>
            Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap = new ConcurrentHashMap<>(512);
            issueSandBoxTask.setIssueNeedShopIdMap(issueNeedShopIdMap);

            //detail记录表 发货日期的detailMap<issueDate, <shopId, list>>
            Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailDateMap = new HashMap<>(128);
            issueSandBoxTask.setIssueDetailDateMap(issueDetailDateMap);

            //detail预留存记录表 发货日期的detailReserveMap<shopId_matCode_sizeName, IssueReserveDetailDo>
            Map<String, IssueReserveDetailDo> issueDetailReserveDateMap = new HashMap<>(1024);
            issueSandBoxTask.setIssueDetailReserveDateMap(issueDetailReserveDateMap);

            List<IssueDayPeriod> issueDatList = issueDayPeriodMapper.getIssueDay(calBegin.getTime(), calEnd.getTime());
            //店铺的到货日期发货日期对应Map<arriveDate, <shopId, issueDate>>
            Map<Date, Map<String, Date>> arriveIssueMappingMap = new HashMap<>(128);
            for (IssueDayPeriod issueDayPeriod : issueDatList) {
                Date arriveDateKey = issueDayPeriod.getArriveDate();
                Map<String, Date> map;
                if ((map = arriveIssueMappingMap.get(arriveDateKey)) == null) {
                    map = new HashMap<>(512);
                    map.put(issueDayPeriod.getShopId(), issueDayPeriod.getIssueDate());
                    arriveIssueMappingMap.put(arriveDateKey, map);
                } else {
                    map.put(issueDayPeriod.getShopId(), issueDayPeriod.getIssueDate());
                }
            }
            //店铺的发货日期到货日期对应Map<issueDate, <shopId, arriveDate>>
            Map<Date, Map<String, Date>> issueArriveMappingMap = new HashMap<>(128);
            for (IssueDayPeriod issueDayPeriod : issueDatList) {
                Date issueDateKey = issueDayPeriod.getIssueDate();
                Map<String, Date> map;
                if ((map = issueArriveMappingMap.get(issueDateKey)) == null) {
                    map = new HashMap<>(512);
                    map.put(issueDayPeriod.getShopId(), issueDayPeriod.getArriveDate());
                    issueArriveMappingMap.put(issueDateKey, map);
                } else {
                    map.put(issueDayPeriod.getShopId(), issueDayPeriod.getArriveDate());
                }
            }
            issueSandBoxTask.setIssueArriveMappingMap(issueArriveMappingMap);

            Set<String> shopIds = remark == null || "RERUN".equals(remark) || "RUN".equals(remark) ? null : Arrays.stream(remark.split(",")).collect(Collectors.toSet());
            issueSandBoxTask.setShopIds(shopIds);

            while (calBegin.compareTo(calEnd) <= 0) {
                try {
                    logger.info("RunSandBoxTask Start date = {}-{}-{}", calBegin.get(Calendar.YEAR), calBegin.get(Calendar.MONTH), calBegin.get(Calendar.DATE));

                    //更新每天配发天数
                    issueService.updateIssueDaysByDate(calBegin);

                    //in跟out可以同时跑
                    CountDownLatch issueMainCount = new CountDownLatch(2);
                    boolean finalFirst = first;
                    issueSandBoxMainPool.submit(() -> {
                        try {
                            newIssueBasicService.issueInStockSandBox(issueSandBoxTask, finalFirst, calBegin);
                            logger.info("RunSandBoxTask IN END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                        } catch (Exception e) {
                            LoggerUtil.error(e, logger, "[RunSandBoxTask IN] catch exception");
                        } finally {
                            issueMainCount.countDown();
                        }
                    });
                    issueSandBoxMainPool.submit(() -> {
                        try {
                            newIssueBasicService.issueOutStockSandBox(issueHuihuoDateMap, issueOutMap, issueDetailDateMap, issueArriveMappingMap, calBegin, finalFirst);
                            logger.info("RunSandBoxTask OUT END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                        } catch (Exception e) {
                            LoggerUtil.error(e, logger, "[RunSandBoxTask OUT] catch exception");
                        } finally {
                            issueMainCount.countDown();
                        }
                    });
                    issueMainCount.await();
                    logger.info("RunSandBoxTask IN And OUT END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

                    newIssueSkuCalcService.calcSKURequirementSandBox(issueSandBoxTask, remark == null || "RERUN".equals(remark) || "RUN".equals(remark) ? null : Arrays.stream(remark.split(",")).collect(Collectors.toSet()),
                            issueInStockShopIdMap,
                            issueNeedShopIdMap);
                    logger.info("RunSandBoxTask NEED END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                    newIssueMatchService.issueDetailSandBox(remark == null || "RERUN".equals(remark) || "RUN".equals(remark) ? null : Arrays.stream(remark.split(",")).collect(Collectors.toSet()),
                            dwsDimShopDoMap,
                            issueArriveMappingMap,
                            issueOutMap, issueInStockShopIdMap, issueNeedShopIdMap, issueDetailDateMap, issueDetailReserveDateMap,
                            issueSandBoxTask, calBegin, first);
                    logger.info("RunSandBoxTask DETAIL END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

                    logger.info("RunSandBoxTask OVER" + calBegin.getTime());
                } catch (Exception e) {
                    LoggerUtil.error(e, logger, "[RunSandBoxTask] catch exception e" + calBegin.getTime());
                }

                first = false;
                //逐天递增
                calBegin.add(Calendar.DATE, 1);
            }

            int pageSize = 5000;
            int insertReserveDateCount = issueDetailReserveDateMap.values().size() % pageSize == 0 ? issueDetailReserveDateMap.values().size() / pageSize : issueDetailReserveDateMap.values().size() / pageSize + 1;
            CountDownLatch reserveCountDownLatch = new CountDownLatch(insertReserveDateCount);
            List<IssueReserveDetailDo> tempList;
            int count = 0;
            while(true) {
                tempList = issueDetailReserveDateMap.values().stream().skip(count * pageSize).limit(pageSize).collect(Collectors.toList());
                if (tempList.size() == 0) {
                    break;
                }
                List<IssueReserveDetailDo> finalList = tempList;
                int finalCount = count;
                issueSandBoxMainPool.submit(() -> {
                    try {
                        newIssueDoMapper.addIssueReserveDetail(finalList);
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[batchInsertIssueReserve] catch exception");
                    } finally {
                        reserveCountDownLatch.countDown();
                        logger.info("batchInsertIssueReserve,count: " + finalCount);
                    }
                });
                ++count;
            }
            reserveCountDownLatch.await();

            issueSandBoxTaskMapper.updateStatus(taskId, IssueSandBoxTask.DONE_STATUS);

            return issueDetailReserveDateMap;
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[RunSandBoxTask] catch exception e" );
            issueSandBoxTaskMapper.updateStatus(taskId, IssueSandBoxTask.FAIL_STATUS);
            return null;
        }
//        finally {
//            issueSandBoxTaskMapper.updateStatus(taskId, IssueSandBoxTask.DONE_STATUS);
//        }
    }

}
