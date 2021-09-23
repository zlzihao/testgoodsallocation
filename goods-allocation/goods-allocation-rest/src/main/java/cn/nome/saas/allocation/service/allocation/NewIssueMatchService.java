package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.model.old.allocation.MatcodeSaleRank;
import cn.nome.saas.allocation.model.old.allocation.SizeCountData;
import cn.nome.saas.allocation.model.old.issue.IssueGoodsData;
import cn.nome.saas.allocation.model.old.issue.NewCategorySkcData;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.task.newIssue.CategorySkcCountTask;
import cn.nome.saas.allocation.task.newIssue.MidCategorySkcCountTask;
import cn.nome.saas.allocation.utils.CommonUtil;
import cn.nome.saas.allocation.utils.IssueDayUtil;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.*;

/**
 * NewIssueMatchService
 *
 * @author Bruce01.fan
 * @date 2019/9/6
 */
@Service
public class NewIssueMatchService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NewIssueDOMapper newIssueDOMapper;
    @Autowired
    private NewIssueWarehouseService newIssueWarehouseService;
    @Autowired
    private GoodsInfoDOMapper goodsInfoDOMapper;
    @Autowired
    private DwsDimGoodsDOMapper dwsDimGoodsDOMapper;
    @Autowired
    private NewIssueSkuCalcDOMapper newIssueSkuCalcDOMapper;
    @Autowired
    private SubWarehouseConfigDOMapper subWarehouseConfigDOMapper;
    @Autowired
    AllocationDOMapper2 allocationDOMapper2;
    @Autowired
    IssueDOMapper2 issueDOMapper2;
    @Autowired
    NewGoodsIssueRangeDetailMapper newGoodsIssueRangeDetailMapper;
    @Autowired
    ShopInfoCache shopInfoCache;
    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;

    private static BigDecimal ZERO = new BigDecimal(0);

    /**
     * 线程池
     */
    private ExecutorService issueDetailPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-detail-%d").build());
    private ExecutorService issueDetailPriorityPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-detail-priority-%d").build());

    private ForkJoinPool fjPool = new ForkJoinPool(4);

    public void issueDetail(IssueTaskDO task, Set<String> shopIds) {
        issueDetail(task, shopIds, false);
    }

    /**
     * detail calc
     * @param task
     * @param shopIds
     * @param reCalc
     */
    void issueDetail(IssueTaskDO task, Set<String> shopIds, boolean reCalc) {
        try {

            String inTabName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());
            String detailTabName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, task.getId(),task.getRunTime());
            String goodsDataTabName = CommonUtil.getTaskTableName(ISSUE_GOODS_DATA_TABLE_PREFIX, task.getId(),task.getRunTime());

            String categoryTabName = CommonUtil.getTaskTableName(Constant.ISSUE_CATEGORY_DATA_TABLE_PREFIX, task.getId(),task.getRunTime());
            String midCategoryTabName = CommonUtil.getTaskTableName(Constant.ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX, task.getId(),task.getRunTime());
            if (!reCalc) {
                newIssueDOMapper.dropTableIfExist(detailTabName);
                newIssueDOMapper.createIssueDetailTab(detailTabName);

                newIssueDOMapper.dropTableIfExist(goodsDataTabName);
                newIssueDOMapper.createIssueGoodsDataTab(goodsDataTabName);

                newIssueDOMapper.dropTableIfExist(categoryTabName);
                newIssueDOMapper.dropTableIfExist(midCategoryTabName);

                newIssueDOMapper.createCategoryCountTable(categoryTabName);
                newIssueDOMapper.createMidCategoryCountTable(midCategoryTabName);

                //插入仓库店铺对应表
                batchInsertWarehouseShop();
            } else {
                //单店重算
                String bakDetailTabName = CommonUtil.getTaskTableName(BAK_ISSUE_DETAIL_TABLE_PREFIX, task.getId(), task.getRunTime());
                String bakGoodsDataTabName = CommonUtil.getTaskTableName(BAK_ISSUE_GOODS_DATA_TABLE_PREFIX, task.getId(), task.getRunTime());
                String bakCategoryTabName = CommonUtil.getTaskTableName(Constant.BAK_ISSUE_CATEGORY_DATA_TABLE_PREFIX, task.getId(), task.getRunTime());
                String bakMidCategoryTabName = CommonUtil.getTaskTableName(Constant.BAK_ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX, task.getId(), task.getRunTime());
                // 创建新表
                if (newIssueSkuCalcDOMapper.checkTableExists(bakDetailTabName) == 0) {
                    newIssueDOMapper.createIssueDetailTab(bakDetailTabName);
                }
                if (newIssueSkuCalcDOMapper.checkTableExists(bakGoodsDataTabName) == 0) {
                    newIssueDOMapper.createIssueGoodsDataTab(bakGoodsDataTabName);
                }
                if (newIssueSkuCalcDOMapper.checkTableExists(bakCategoryTabName) == 0) {
                    newIssueDOMapper.createCategoryCountTable(bakCategoryTabName);
                }
                if (newIssueSkuCalcDOMapper.checkTableExists(bakMidCategoryTabName) == 0) {
                    newIssueDOMapper.createMidCategoryCountTable(bakMidCategoryTabName);
                }

                newIssueDOMapper.bakIssueDetail(bakDetailTabName, detailTabName, shopIds);
                newIssueDOMapper.delIssueDetailByShopId(detailTabName, shopIds);

                newIssueDOMapper.bakGoodsData(bakGoodsDataTabName, goodsDataTabName, shopIds);
                newIssueDOMapper.delGoodsDataByShopId(goodsDataTabName, shopIds);

                newIssueDOMapper.bakCategoryCountData(bakCategoryTabName, categoryTabName, shopIds);
                newIssueDOMapper.delCategoryCountDataByShopId(categoryTabName, shopIds);

                newIssueDOMapper.bakMidCategoryCountData(bakMidCategoryTabName, midCategoryTabName, shopIds);
                newIssueDOMapper.delMidCategoryCountDataByShopId(midCategoryTabName, shopIds);
            }

            List<SubWarehouseConfigDO>  subWarehouseConfigDos = subWarehouseConfigDOMapper.selectByPage(new HashMap<>(0));

            Map<Integer, List<SubWarehouseConfigDO>> priorityMap = new HashMap<>();
            for (SubWarehouseConfigDO subWarehouseConfigDo : subWarehouseConfigDos) {
                List<SubWarehouseConfigDO> configList = priorityMap.get(subWarehouseConfigDo.getPriority());
                if (configList == null) {
                    priorityMap.put(subWarehouseConfigDo.getPriority(), new ArrayList<>(Collections.singletonList(subWarehouseConfigDo)));
                } else {
                    configList.add(subWarehouseConfigDo);
                }
            }

            //商品信息
            List<GoodsInfoDO> goodsInfoDos = goodsInfoDOMapper.selectGoodsList();
            Map<String, GoodsInfoDO> goodsInfoDoMap = goodsInfoDos.stream().collect(Collectors.toMap(GoodsInfoDO::getMatCode, Function.identity()));
            List<DwsDimGoodsDO> dwsDimGoodsDos = dwsDimGoodsDOMapper.getList();
            Map<String, BigDecimal> dimGoodsPriceMap = dwsDimGoodsDos.stream().collect(Collectors.toMap(DwsDimGoodsDO::getMatCode,
                    dwsDimGoodsDO -> {
                        if (dwsDimGoodsDO.getQuotePrice() == null) {
                            return ZERO;
                        }
                        return dwsDimGoodsDO.getQuotePrice();
                    }));

            //获取新品计划需要配发的数量
            List<NewGoodsIssueRangeDetailDO> newGoodsIssueRangeDetailDOS = newGoodsIssueRangeDetailMapper.getIssueNumBySaleTime();
            Map<String, Integer> issueNumMap = getNewGoodsIssueNumMap(newGoodsIssueRangeDetailDOS);
//            Map<String, NewGoodsIssueRangeDetailDO> issueNumMap = newGoodsIssueRangeDetailDOS.stream().collect(Collectors.toMap(data -> data.getShopId() + "_" + data.getMatCode() + "_" + data.getSizeId(), Function.identity(), (v1, v2) -> v1));

            Map<String, Double> smallSumAvgMap = newIssueSkuCalcDOMapper.getSmallSumAvg(inTabName, shopIds).stream()
                    .collect(Collectors.toMap(data -> data.getShopId() + "_" + data.getCategoryName() + "_" + data.getMidCategoryName() + "_" + data.getSmallCategoryName(), IssueSumAvgDO::getSumAvg, (v1, v2) -> v2));
            Map<String, Double> midSumAvgMap = newIssueSkuCalcDOMapper.getMidSumAvg(inTabName, shopIds).stream()
                    .collect(Collectors.toMap(data -> data.getShopId() + "_" + data.getCategoryName() + "_" + data.getMidCategoryName(), IssueSumAvgDO::getSumAvg, (v1, v2) -> v2));
            Map<String, Double> bigSumAvgMap = newIssueSkuCalcDOMapper.getBigSumAvg(inTabName, shopIds).stream()
                    .collect(Collectors.toMap(data -> data.getShopId() + "_" + data.getCategoryName(), IssueSumAvgDO::getSumAvg, (v1, v2) -> v2));
            Map<String, Double> allSumAvgMap = newIssueSkuCalcDOMapper.getAllSumAvg(inTabName, shopIds).stream()
                    .collect(Collectors.toMap(IssueSumAvgDO::getShopId, IssueSumAvgDO::getSumAvg, (v1, v2) -> v2));

            List<Integer> priorityList = new ArrayList<>(priorityMap.keySet());
            priorityList.sort(Integer::compareTo);
            for (Integer priority : priorityList) {
                //同一优先级的店铺同步处理, 原则上同一优先级店铺不会重叠处理
                CountDownLatch detailPriorityCount = new CountDownLatch(priorityMap.get(priority).size());
                for (SubWarehouseConfigDO subWarehouseConfigDO : priorityMap.get(priority)) {
                    Set<String> finalShopIds = shopIds;
                    issueDetailPriorityPool.submit(() -> {
                        try {
                            issueDetail(task, subWarehouseConfigDO, finalShopIds, goodsInfoDoMap, dimGoodsPriceMap, smallSumAvgMap, midSumAvgMap, bigSumAvgMap, allSumAvgMap, issueNumMap);
                            LoggerUtil.info(logger, "[issueDetailSubWarehouseConfigDOId] done,subWarehouseConfigDOId:{0}", subWarehouseConfigDO.getId());
                        } catch (Exception e) {
                            LoggerUtil.error(e, logger, "[issueDetail detailPriorityCount] catch exception,subWarehouseConfigDOId:{0}", subWarehouseConfigDO.getId());
                        } finally {
                            detailPriorityCount.countDown();
                        }
                    });
                }
                detailPriorityCount.await();
                LoggerUtil.info(logger, "[issueDetailPriority] done,priority:{0}", priority);
            }

            LoggerUtil.info(logger, "[issueDetail] issue calculate task over!");

        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueDetail] catch exception");
            logger.error("[issueDetail] catch exception", e);
        }
    }

    /**
     * detail计算
     * @param shopIds shopIds
     * @param dwsDimShopDOMap dwsDimShopDOMap
     * @param issueArriveMappingMap issueArriveMappingMap
     * @param issueOutMap issueOutMap
     * @param issueInStockShopIdMap issueInStockShopIdMap
     * @param issueNeedShopIdMap issueNeedShopIdMap
     * @param issueDetailDateMap issueDetailDateMap
     * @param calBegin calBegin
     * @param first first
     */
    void issueDetailSandBox(Set<String> shopIds,
                            Map<String, DwsDimShopDO> dwsDimShopDOMap,
                            Map<Date, Map<String, Date>> issueArriveMappingMap,
                            Map<String, Map<String, NewIssueOutStockDo>> issueOutMap,
                            Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap,
                            Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap,
                            Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailDateMap,
                            Map<String, IssueReserveDetailDo> issueDetailReserveDateMap,
                            IssueSandBoxTask issueSandBoxTask,
                            Calendar calBegin, boolean first) {
        try {
            if (first) {
                //插入仓库店铺对应表
                batchInsertWarehouseShop();
            }

            List<SubWarehouseConfigDO>  subWarehouseConfigDos = subWarehouseConfigDOMapper.selectByPage(new HashMap<>(0));

            Map<Integer, List<SubWarehouseConfigDO>> priorityMap = new HashMap<>();
            for (SubWarehouseConfigDO subWarehouseConfigDo : subWarehouseConfigDos) {
                List<SubWarehouseConfigDO> configList = priorityMap.get(subWarehouseConfigDo.getPriority());
                if (configList == null) {
                    priorityMap.put(subWarehouseConfigDo.getPriority(), new ArrayList<>(Collections.singletonList(subWarehouseConfigDo)));
                } else {
                    configList.add(subWarehouseConfigDo);
                }
            }

            //商品信息
            List<GoodsInfoDO> goodsInfoDos = goodsInfoDOMapper.selectGoodsList();
            Map<String, GoodsInfoDO> goodsInfoDoMap = goodsInfoDos.stream().collect(Collectors.toMap(GoodsInfoDO::getMatCode, Function.identity()));
            List<DwsDimGoodsDO> dwsDimGoodsDos = dwsDimGoodsDOMapper.getList();
            Map<String, BigDecimal> dimGoodsPriceMap = dwsDimGoodsDos.stream().collect(Collectors.toMap(DwsDimGoodsDO::getMatCode,
                    dwsDimGoodsDO -> {
                        if (dwsDimGoodsDO.getQuotePrice() == null) {
                            return ZERO;
                        }
                        return dwsDimGoodsDO.getQuotePrice();
                    }));
            Map<String, Double> smallSumAvgMap = new HashMap<>(),
                    midSumAvgMap = new HashMap<>(),
                    bigSumAvgMap = new HashMap<>(),
                    allSumAvgMap = new HashMap<>();
            for (Map<String, NewIssueInStockDo> issueInStockMap : issueInStockShopIdMap.values()) {
                for (NewIssueInStockDo newIssueInStockDo : issueInStockMap.values()) {
                    if (newIssueInStockDo.getAvgSaleQty() == null) {
                        System.out.println(newIssueInStockDo);
                    }
                    Double avgSaleQty = newIssueInStockDo.getAvgSaleQty().doubleValue();
                    Double smallSumAvg, midSumAvg, bigSumAvg, akkSumAvg;
                    String shopIdBigMidSmallKey = newIssueInStockDo.getShopIdBigMidSmallCategoryKey();
                    smallSumAvgMap.put(shopIdBigMidSmallKey, (smallSumAvg = smallSumAvgMap.get(shopIdBigMidSmallKey)) == null ? avgSaleQty :  smallSumAvg + avgSaleQty);

                    String shopIdBigMidKey = newIssueInStockDo.getShopIdBigMidCategoryKey();
                    midSumAvgMap.put(shopIdBigMidKey, (midSumAvg = midSumAvgMap.get(shopIdBigMidKey)) == null ? avgSaleQty :  midSumAvg + avgSaleQty);

                    String shopIdBigKey = newIssueInStockDo.getShopIdBigCategoryKey();
                    bigSumAvgMap.put(shopIdBigKey, (bigSumAvg = bigSumAvgMap.get(shopIdBigKey)) == null ? avgSaleQty :  bigSumAvg + avgSaleQty);

                    String shopIdKey = newIssueInStockDo.getShopId();
                    allSumAvgMap.put(shopIdKey, (akkSumAvg = allSumAvgMap.get(shopIdKey)) == null ? avgSaleQty :  akkSumAvg + avgSaleQty);
                }
            }

            List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.shopInfoData();
            Map<String, ShopInfoData> shopInfoDataMap = shopInfoDataList.stream().collect(Collectors.toMap(ShopInfoData::getShopID, Function.identity()));

            List<Integer> priorityList = new ArrayList<>(priorityMap.keySet());
            priorityList.sort(Integer::compareTo);

            //Map<ShopId, List<NewIssueDetailDo>> 多线程操作 使用ConcurrentHashMap
            Map<String, List<NewIssueDetailDo>> issueDetailMap = new ConcurrentHashMap<>();
            for (Integer priority : priorityList) {
                //同一优先级的店铺同步处理, 原则上同一优先级店铺不会重叠处理
                CountDownLatch detailPriorityCount = new CountDownLatch(priorityMap.get(priority).size());
                for (SubWarehouseConfigDO subWarehouseConfigDO : priorityMap.get(priority)) {
                    issueDetailPriorityPool.submit(() -> {
                        try {
                            issueDetailSandBox(subWarehouseConfigDO, shopIds, goodsInfoDoMap, dimGoodsPriceMap,
                                    smallSumAvgMap, midSumAvgMap, bigSumAvgMap, allSumAvgMap, shopInfoDataMap, issueOutMap, issueNeedShopIdMap, issueDetailMap);
                            LoggerUtil.info(logger, "[issueDetailSubWarehouseConfigDOId] done,subWarehouseConfigDOId:{0}", subWarehouseConfigDO.getId());
                        } catch (Exception e) {
                            LoggerUtil.error(e, logger, "[issueDetail detailPriorityCount] catch exception,subWarehouseConfigDOId:{0}", subWarehouseConfigDO.getId());
                        } finally {
                            detailPriorityCount.countDown();
                        }
                    });
                }
                detailPriorityCount.await();
                LoggerUtil.info(logger, "[issueDetailPriority] done,priority:{0}", priority);
            }

            //插入配发数, 配发货值
            Date issueDate = calBegin.getTime();
            Map<String, Date> issueShopIdMap = issueArriveMappingMap.get(issueDate);
            if (issueShopIdMap != null) {
                //沙盘计算
                if (IssueSandBoxTask.CALC_TYPE_SANDBOX.equals(issueSandBoxTask.getCalcType())) {
                    for (String shopId : issueShopIdMap.keySet()) {
                        List<NewIssueDetailDo> issueDetailDos = issueDetailMap.get(shopId);
                        if (CollectionUtils.isEmpty(issueDetailDos)) {
                            continue;
                        }
                        List<NewIssueSandboxDetailDo> batchInsertList = new ArrayList<>();
                        DwsDimShopDO dwsDimShopDO = dwsDimShopDOMap.get(shopId);
                        if (dwsDimShopDO == null) {
                            continue;
                        }
                        String shopName = dwsDimShopDO.getShopName();
                        String shopCode = dwsDimShopDO.getShopCode();
                        for (NewIssueDetailDo issueDetailDo : issueDetailDos) {
                            NewIssueSandboxDetailDo newIssueSandboxDetailDo = new NewIssueSandboxDetailDo();
                            newIssueSandboxDetailDo.setShopId(shopId);
                            newIssueSandboxDetailDo.setShopCode(shopCode);
                            newIssueSandboxDetailDo.setShopName(shopName);
                            newIssueSandboxDetailDo.setTaskId(issueSandBoxTask.getTaskId());
                            newIssueSandboxDetailDo.setIssueNum(issueDetailDo.getQty());
                            newIssueSandboxDetailDo.setMatCode(issueDetailDo.getMatCode());
                            newIssueSandboxDetailDo.setSizeName(issueDetailDo.getSizeName());
                            newIssueSandboxDetailDo.setIssueDate(issueDate);
                            batchInsertList.add(newIssueSandboxDetailDo);
                        }
                        if (batchInsertList.size() > 0) {
                            newIssueDOMapper.addNewIssueSandboxDetail(batchInsertList);
                        }
                    }


                } else if (IssueSandBoxTask.CALC_TYPE_RESERVE.equals(issueSandBoxTask.getCalcType())) {//预留存计算
                    for (String shopId : issueShopIdMap.keySet()) {
                        List<NewIssueDetailDo> issueDetailDos = issueDetailMap.get(shopId);
                        if (issueDetailDos != null && issueDetailDos.size() > 0) {
                            for (NewIssueDetailDo issueDetailDo : issueDetailDos) {
                                //配发数量=0不保存
                                if (issueDetailDo.getQty().compareTo(ZERO) == 0) {
                                    continue;
                                }
                                String key = issueDetailDo.getShopIdMatCodeSizeNameKey();
                                IssueReserveDetailDo issueReserveDetailDo = issueDetailReserveDateMap.get(key);
                                if (issueReserveDetailDo == null) {
                                    issueReserveDetailDo = new IssueReserveDetailDo(issueDetailDo.getInShopId(), issueDetailDo.getMatCode(), issueDetailDo.getSizeName(), issueDetailDo.getQty());
                                    issueDetailReserveDateMap.put(key, issueReserveDetailDo);
                                } else {
                                    issueReserveDetailDo.setIssueReserveNum(issueReserveDetailDo.getIssueReserveNum().add(issueDetailDo.getQty()));
                                }
                            }
                        }
                    }
                }
            }
            issueDetailDateMap.put(calBegin.getTime(), issueDetailMap);

            LoggerUtil.info(logger, "[issueDetail] issue calculate task over!");

            //沙盘记录日志
//            List<NewIssueDetailDo> l1 = issueDetailDateMap.get(calBegin.getTime()).get("NM000006");
//            l1 = l1.stream().sorted(Comparator.comparing(NewIssueDetailDo::getMatCode).reversed()).collect(Collectors.toList());
//            for (NewIssueDetailDo newIssueDetailDo : l1) {
//                if ("ACD2B00516Y1".equals(newIssueDetailDo.getMatCode())) {
//                    LoggerUtil.info(logger, "calcData:newIssueDetailDo:" + newIssueDetailDo + ":" +  calBegin.getTime());
//                }
//            }
            LoggerUtil.info(logger, "OVer");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueDetail] catch exception");
            logger.error("[issueDetail] catch exception", e);
        }
    }

    public void processCategorySkcData(int taskId) {
        IssueTaskDO issueTask = newIssueDOMapper.getIssueTask(taskId);

        Set<String> shopIds = newIssueDOMapper.issueInStockShopIds(CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, issueTask.getId(),issueTask.getRunTime()));

        this.processCategorySkcData(issueTask.getId(),shopIds,issueTask.getRunTime(), false);
    }

    public Integer batchInsertWarehouseShop() {

        List<SubWarehouseShopMappingDO> subWarehouseShopMappingDoS = new ArrayList<>();

        List<SubWarehouseConfigDO> subWarehouseConfigDos = subWarehouseConfigDOMapper.selectByPage(new HashMap<>(0));

        for (SubWarehouseConfigDO subWarehouseConfigDo : subWarehouseConfigDos) {

            Set<String> subWarehouseShopSet = newIssueWarehouseService.getShopMapByConfigId(subWarehouseConfigDo.getId());
            Integer priority = subWarehouseConfigDo.getPriority();
            String warehouseCode = subWarehouseConfigDo.getWarehouseCode();
            for (String shopId : subWarehouseShopSet) {
                SubWarehouseShopMappingDO subWarehouseShopMappingDO = new SubWarehouseShopMappingDO();
                subWarehouseShopMappingDO.setPriority(priority);
                subWarehouseShopMappingDO.setWarehouseCode(warehouseCode);
                subWarehouseShopMappingDO.setShopId(shopId);
                subWarehouseShopMappingDoS.add(subWarehouseShopMappingDO);
            }

        }
        //清空后插入
        newIssueDOMapper.truncateTable(SUB_WAREHOUSE_SHOP_MAPPING_PREFIX);
        return newIssueDOMapper.batchInsertWarehouseShop(subWarehouseShopMappingDoS);
    }

    private void issueDetail(IssueTaskDO task, SubWarehouseConfigDO subWarehouseConfigDO, Set<String> shopIds, Map<String, GoodsInfoDO> goodsInfoDoMap, Map<String, BigDecimal> dimGoodsPriceMap,
                             Map<String, Double> smallSumAvgMap, Map<String, Double> midSumAvgMap, Map<String, Double> bigSumAvgMap, Map<String, Double> allSumAvgMap, Map<String, Integer> issueNumMap) {
        String inTabName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());
        String issueNeedTableName = CommonUtil.getTaskTableName(ISSUE_NEED_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());
        String issueDetailTableName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, task.getId(),task.getRunTime());
        String issueOutStockTableName = CommonUtil.getTaskTableName(ISSUE_OUT_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());

        //HashSet<ShopId>
        Set<String> subWarehouseShopSet = newIssueWarehouseService.getShopMapByConfigId(subWarehouseConfigDO.getId());
        //为null时全部重算
        if (shopIds != null) {
            subWarehouseShopSet = Sets.intersection(subWarehouseShopSet, shopIds);
        }
        //未获取对应店铺时直接返回
        if (subWarehouseShopSet.size() <= 0) {
            return;
        }

        List<IssueSumAvgDO> matCodeList = newIssueSkuCalcDOMapper.getDistinctMatCode(issueNeedTableName, subWarehouseShopSet);

        //分批跑matCodeList, 1000一组
        List<IssueSumAvgDO> tempMatCodeList;
        int matCodeCount = 0;
        int matCodePageSize = 1000;

        Map<String, NewIssueOutStockDo> outStockQtyMap = null;
        while(true) {
            tempMatCodeList = matCodeList.stream().skip(matCodeCount * matCodePageSize).limit(matCodePageSize).collect(Collectors.toList());
            if (tempMatCodeList.size() == 0) {
                break;
            }

            List<String> needMatCodeList = new ArrayList<>();
            List<String> needSizeNameList = new ArrayList<>();
            for (IssueSumAvgDO issueSumAvgDo : tempMatCodeList) {
                needMatCodeList.add(issueSumAvgDo.getMatCode());
                needSizeNameList.add(issueSumAvgDo.getSizeName());
            }

            List<IssueNeedStockDO> issueNeedStockDos = newIssueSkuCalcDOMapper.getIssueNeedStockList(issueNeedTableName, inTabName, subWarehouseShopSet, needMatCodeList, needSizeNameList);
            //未获取到店铺需要库存直接返回
            if (issueNeedStockDos.size() <= 0) {
                return;
            }

            if (outStockQtyMap == null) {
                List<NewIssueOutStockDo> newIssueOutStockDos  = newIssueDOMapper.getIssueOutStock(issueOutStockTableName, subWarehouseConfigDO.getWarehouseCode());
                outStockQtyMap = newIssueOutStockDos.stream().collect(Collectors.toMap(NewIssueOutStockDo::getMatCodeSizeNameKey, Function.identity()));
            }

            //判断仓库是否足够配发round之后的包数
            Map<String, BigDecimal> skuMidPackageMap = new HashMap<>();
            Set<String> notEnoughSku = new HashSet<>();
            //matCode维度的IssueNeedStockDO列表  Map<[MatCode_SizeID], List<IssueNeedStockDO>>
            Map<String, List<IssueNeedStockDO>> matCodeIssueNeedStockListMap = new HashMap<>();
            for (IssueNeedStockDO issueNeedStockDO : issueNeedStockDos) {
                if ("NM000068".equals(issueNeedStockDO.getShopId()) && "A8A2G28P0004D1".equals(issueNeedStockDO.getMatCode())) {
                    System.out.println(1);
                }
                String key = issueNeedStockDO.getMatCodeSizeNameKey();
                //库存中能提供的库存数量
                NewIssueOutStockDo newIssueOutStockDo;
                //实际需要库存 = 计算需要库存 - 店铺库存
                double actualNeedQty = 0;
                //仓库中无此sku 不需计算
                // 实际需求数量小于0(新品铺货计划的商品除外) 不需计算
                if ((newIssueOutStockDo = outStockQtyMap.get(key)) == null ||
                        (!issueNumMap.containsKey(issueNeedStockDO.getShopIdMatCodeSizeNameKey()) && (actualNeedQty = issueNeedStockDO.getRemainNeedQty() - issueNeedStockDO.getTotalStockQty()) <= 0)
//                        || notEnoughSku.contains(key)
                ) {
                    continue;
                }

                //新品未完成首配 对比上市时间 获取配发量
                Integer issueNum;
                if ((issueNum = issueNumMap.get(issueNeedStockDO.getShopIdMatCodeSizeNameKey())) != null) {
                    actualNeedQty = issueNum;
                }

                // 取出当前sku下所有门店的需求列表
                List<IssueNeedStockDO> issueNeedStockDoList = matCodeIssueNeedStockListMap.get(key);
                if (issueNeedStockDoList == null) {
                    matCodeIssueNeedStockListMap.put(key, new ArrayList<>(Arrays.asList(issueNeedStockDO)));
                } else {
                    issueNeedStockDoList.add(issueNeedStockDO);
                    matCodeIssueNeedStockListMap.put(key, issueNeedStockDoList);
                }

                Integer outStockQty = newIssueOutStockDo.getRemainStockQty();
                //单个中包的matCode数量
                Integer matCodePackageNum = goodsInfoDoMap.get(issueNeedStockDO.getMatCode()).getMinPackageQty();
                //库存总量的中包数量
                int outStockPackageNum = outStockQty / matCodePackageNum;

                // 将仓库中不够分配的sku拧到notEnoughSku集合中
                if (!skuMidPackageMap.containsKey(key)) {
                    BigDecimal packageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(matCodePackageNum), 0, RoundingMode.HALF_UP);
                    skuMidPackageMap.put(key, packageNum);
                    if (packageNum.compareTo(new BigDecimal(outStockPackageNum)) > 0) {
                        notEnoughSku.add(key);
                    }
                } else {
                    BigDecimal packageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(matCodePackageNum), 0, RoundingMode.HALF_UP);
                    BigDecimal totalPackageNum = packageNum.add(skuMidPackageMap.get(key));
                    skuMidPackageMap.put(key, totalPackageNum);
                    if (totalPackageNum.compareTo(new BigDecimal(outStockPackageNum)) > 0) {
                        notEnoughSku.add(key);
                    }
                }
            }

            List<NewIssueDetailDo> newIssueDetailDos = new ArrayList<>();
            //更新need_stock 剩余需要库存
            List<BatchUpdateIssueNeedStockDo> batchUpdateIssueNeedStockDos = new ArrayList<>();
            //更新out_stock 剩余库存
            List<BatchUpdateIssueOutStockDo> batchUpdateIssueOutStockDos = new ArrayList<>();

            for (Map.Entry<String, List<IssueNeedStockDO>> entry : matCodeIssueNeedStockListMap.entrySet()) {
                List<IssueNeedStockDO> issueNeedStockDoList = entry.getValue();
                GoodsInfoDO goodsInfoDo = goodsInfoDoMap.get(issueNeedStockDoList.get(0).getMatCode());
                BigDecimal quotePrice = dimGoodsPriceMap.get(issueNeedStockDoList.get(0).getMatCode());

                //分配完后最终的库存  保存分配完后剩余库存
                Integer finalOutStockQty = outStockQtyMap.get(entry.getKey()).getRemainStockQty();

                String key = entry.getKey();
                if (notEnoughSku.contains(key)) {
                    //不足时需要排序
                    String skuKey, smallKey, midKey, bigKey;
                    for (IssueNeedStockDO issueNeedStockDo : issueNeedStockDoList) {
                        skuKey = issueNeedStockDo.getShopIdMatCodeSizeNameKey();
                        smallKey = issueNeedStockDo.getShopIdCategoryMidSmallCategoryKey();
                        midKey = issueNeedStockDo.getShopIdCategoryMidCategoryKey();
                        bigKey = issueNeedStockDo.getShopIdCategory();
                        //issueNumMap能获取到表示未首配完成
                        issueNeedStockDo.setIssueFin(issueNumMap.get(skuKey) != null ? 0 : 1);
                        issueNeedStockDo.setAvgSaleQty(issueNeedStockDo.getAvgSaleQty() == null ? 0d : issueNeedStockDo.getAvgSaleQty());
                        issueNeedStockDo.setSmallSumAvg(smallSumAvgMap.get(smallKey) == null ? 0d : smallSumAvgMap.get(smallKey));
                        issueNeedStockDo.setMidSumAvg(midSumAvgMap.get(midKey) == null ? 0d : midSumAvgMap.get(midKey));
                        issueNeedStockDo.setBigSumAvg(bigSumAvgMap.get(bigKey) == null ? 0d : bigSumAvgMap.get(bigKey));
                        issueNeedStockDo.setAllSumAvg(allSumAvgMap.get(issueNeedStockDo.getShopId()) == null ? 0d : allSumAvgMap.get(issueNeedStockDo.getShopId()));
                    }
                    issueNeedStockDoList.sort(new IssueNeedStockDO());
//                    LoggerUtil.debug(logger, "[库存不足分配]:足够实际分配:key:"+ key + "仓库代码:" + subWarehouseConfigDO.getWarehouseCode() + "数量:" + issueNeedStockDoList.size());
                }
                for (IssueNeedStockDO issueNeedStockDO : issueNeedStockDoList) {
                    if ("NM000068".equals(issueNeedStockDO.getShopId()) && "A8A2G28P0004D1".equals(issueNeedStockDO.getMatCode())) {
                        System.out.println(1);
                    }

                    //实际需要库存 = 计算需要库存 - 店铺库存
                    double actualNeedQty = issueNeedStockDO.getRemainNeedQty() - issueNeedStockDO.getTotalStockQty();

                    NewIssueDetailDo newIssueDetailDo = new NewIssueDetailDo();
                    //新品未完成首配 对比上市时间 获取配发量
                    Integer issueNum;
                    if ((issueNum = issueNumMap.get(issueNeedStockDO.getShopIdMatCodeSizeNameKey())) != null) {
                        actualNeedQty = issueNum;
                    }


                    newIssueDetailDo.setInShopId(issueNeedStockDO.getShopId());
                    newIssueDetailDo.setSizeId(issueNeedStockDO.getSizeId());
                    newIssueDetailDo.setSizeName(issueNeedStockDO.getSizeName());
                    newIssueDetailDo.setMatCode(issueNeedStockDO.getMatCode());
                    newIssueDetailDo.setCategoryName(issueNeedStockDO.getCategoryName());
                    newIssueDetailDo.setMidCategoryName(issueNeedStockDO.getMidCategoryName());

                    newIssueDetailDo.setQuotePrice(quotePrice);
                    newIssueDetailDo.setNeedQty(new BigDecimal(actualNeedQty));
                    newIssueDetailDo.setMinPackageQty(goodsInfoDo.getMinPackageQty());
                    newIssueDetailDo.setSmallCategoryName(goodsInfoDo.getSmallCategoryName());
                    newIssueDetailDo.setMatName(goodsInfoDo.getMatName());
                    newIssueDetailDo.setWarehouseCode(subWarehouseConfigDO.getWarehouseCode());

                    BigDecimal actualIssuePackageNum = ZERO;
                    BigDecimal issueQty = ZERO;
                    //实际计算配发包数, 剩余库存满足一个包时计算, 否则直接分配0
                    if (finalOutStockQty >= goodsInfoDo.getMinPackageQty()) {
                        actualIssuePackageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(goodsInfoDo.getMinPackageQty()), 0, RoundingMode.HALF_UP);
                        issueQty = actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty()));
                        if (finalOutStockQty - issueQty.intValue() >= 0) {
//                    actualIssuePackageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(goodsInfoDo.getMinPackageQty()), 0, RoundingMode.HALF_UP);
//                            LoggerUtil.debug(logger, "[需求量计算]:足够实际分配:"+ key + "仓库代码:" + subWarehouseConfigDO.getWarehouseCode() + "店铺ID:" + issueNeedStockDO.getShopId() + ":(getRemainNeedQty:" +
//                                    issueNeedStockDO.getRemainNeedQty() + " - getTotalStockQty:" + issueNeedStockDO.getTotalStockQty() + ") / getMinPackageQty:" +goodsInfoDo.getMinPackageQty());
                            newIssueDetailDo.setIsEnough(1);
                        } else {
                            //不足时直接down
                            actualIssuePackageNum = new BigDecimal(finalOutStockQty / goodsInfoDo.getMinPackageQty());
                            issueQty = actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty()));
//                            LoggerUtil.debug(logger, "[需求量计算]:不足实际分配:"+ key + "仓库代码:" + subWarehouseConfigDO.getWarehouseCode() + "店铺ID:" + issueNeedStockDO.getShopId() +
//                                    "actualIssuePackageNum:" + actualIssuePackageNum + ":finalOutStockQty:" +
//                                    finalOutStockQty + " / MinPackageQty:" + goodsInfoDo.getMinPackageQty());
                            newIssueDetailDo.setIsEnough(0);
                        }
                    } else {
//                        LoggerUtil.debug(logger, "[需求量计算]:不足实际分配:"+ key + "仓库代码:" + subWarehouseConfigDO.getWarehouseCode() + "店铺ID:" + issueNeedStockDO.getShopId() + ":finalOutStockQty:" +
//                                finalOutStockQty + " < goodsInfoDo.getMinPackageQty():" + goodsInfoDo.getMinPackageQty());
                        newIssueDetailDo.setIsEnough(0);
                    }

                    finalOutStockQty -= issueQty.intValue();
                    newIssueDetailDo.setQty(issueQty);
                    newIssueDetailDo.setPackageQty(actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty())));
                    newIssueDetailDo.setOrderPackage(actualIssuePackageNum.intValue());
                    newIssueDetailDo.setStatus(0);

                    batchUpdateIssueNeedStockDos.add(new BatchUpdateIssueNeedStockDo(issueNeedStockDO.getId(), new BigDecimal(issueNeedStockDO.getRemainNeedQty()).subtract(actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty())))));
                    newIssueDetailDos.add(newIssueDetailDo);
                }

                //分完所有店铺后 MatCode_SizeID维度的剩余库存  更新仓库剩余库存
                batchUpdateIssueOutStockDos.add(new BatchUpdateIssueOutStockDo(outStockQtyMap.get(entry.getKey()).getId(), new BigDecimal(finalOutStockQty)));
            }


            LoggerUtil.info(logger, "[issueDetail]|final size,newIssueDetailDos={0}, batchUpdateIssueNeedStockDos={1}, batchUpdateIssueOutStockDos={2}",
                    newIssueDetailDos.size(), batchUpdateIssueNeedStockDos.size(), batchUpdateIssueOutStockDos.size());

            AtomicInteger threadCount = new AtomicInteger(0);
            int pageSize = 5000;
            // 分批录入
            // TODO 可以改成CountDownLatch方式
            List<NewIssueDetailDo> newIssueDetailDoTempList;
            int detailCount = 0;
            while(true) {
                newIssueDetailDoTempList = newIssueDetailDos.stream().skip(detailCount * pageSize).limit(pageSize).collect(Collectors.toList());
                if (newIssueDetailDoTempList.size() == 0) {
                    break;
                }
                List<NewIssueDetailDo> finalDetailList = newIssueDetailDoTempList;
                issueDetailPool.submit(() -> {
                    try {
                        newIssueDOMapper.addIssueDetail(issueDetailTableName, finalDetailList);
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[issueDetail] catch exception");
                        logger.error("[issueDetail] catch exception", e);
                    } finally {
                        threadCount.getAndIncrement();
                    }
                });
                ++detailCount;
            }

            List<BatchUpdateIssueNeedStockDo> needTempList;
            int needCount = 0;
            while(true) {
                needTempList = batchUpdateIssueNeedStockDos.stream().skip(needCount * pageSize).limit(pageSize).collect(Collectors.toList());
                if (needTempList.size() == 0) {
                    break;
                }
                List<BatchUpdateIssueNeedStockDo> finalNeedList = needTempList;
                issueDetailPool.submit(() -> {
                    try {
                        newIssueDOMapper.batchUpdateIssueNeedStock(issueNeedTableName, finalNeedList);
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[batchUpdateIssueNeedStock] catch exception");
                    } finally {
                        threadCount.getAndIncrement();
                    }
                });
                ++needCount;
            }

            List<BatchUpdateIssueOutStockDo> outTempList;
            int outCount = 0;
            while(true) {
                outTempList = batchUpdateIssueOutStockDos.stream().skip(outCount * pageSize).limit(pageSize).collect(Collectors.toList());
                if (outTempList.size() == 0) {
                    break;
                }
                List<BatchUpdateIssueOutStockDo> finalOutList = outTempList;
                issueDetailPool.submit(() -> {
                    try {
                        newIssueDOMapper.batchUpdateIssueOutStock(issueOutStockTableName, finalOutList);
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[batchUpdateIssueOutStock] catch exception");
                    } finally {
                        threadCount.getAndIncrement();
                    }
                });
                ++outCount;
            }

            while (threadCount.get() != (detailCount + needCount + outCount)) {
                try {
                    LoggerUtil.info(logger, "[issueDetailSingle] threadCount.get() != (detailCount + needCount + outCount, sleep 5 s," +
                            " threadCount.get()={0}, detailCount + needCount + outCount={1}", threadCount.get(), detailCount + needCount + outCount);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ++matCodeCount;
        }

    }

    /**
     * detail 详细计算
     * @param subWarehouseConfigDO subWarehouseConfigDO
     * @param shopIds shopIds
     * @param goodsInfoDoMap goodsInfoDoMap
     * @param dimGoodsPriceMap dimGoodsPriceMap
     * @param smallSumAvgMap smallSumAvgMap
     * @param midSumAvgMap midSumAvgMap
     * @param bigSumAvgMap bigSumAvgMap
     * @param allSumAvgMap allSumAvgMap
     * @param shopInfoDataMap shopInfoDataMap
     * @param issueOutMap issueOutMap
     * @param issueNeedShopIdMap issueNeedShopIdMap
     * @param issueDetailMap issueDetailMap
     */
    private void issueDetailSandBox(SubWarehouseConfigDO subWarehouseConfigDO, Set<String> shopIds, Map<String, GoodsInfoDO> goodsInfoDoMap, Map<String, BigDecimal> dimGoodsPriceMap,
                             Map<String, Double> smallSumAvgMap, Map<String, Double> midSumAvgMap, Map<String, Double> bigSumAvgMap, Map<String, Double> allSumAvgMap,
                             Map<String, ShopInfoData> shopInfoDataMap,
                             Map<String, Map<String, NewIssueOutStockDo>> issueOutMap, Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap, Map<String, List<NewIssueDetailDo>> issueDetailMap) {
        Set<String> subWarehouseShopSet = newIssueWarehouseService.getShopMapByConfigId(subWarehouseConfigDO.getId());
        //为null时全部重算
        if (shopIds != null) {
            subWarehouseShopSet = Sets.intersection(subWarehouseShopSet, shopIds);
        }
        //未获取对应店铺时直接返回
        if (subWarehouseShopSet.size() <= 0) {
            return;
        }

        Map<String, NewIssueOutStockDo> outStockQtyMap = issueOutMap.get(subWarehouseConfigDO.getWarehouseCode());
        //获取仓库下需配发的商品
        List<IssueNeedStockDO> issueNeedStockDos = new ArrayList<>();
        for (Map.Entry<String, Map<String, IssueNeedStockDO>> entry : issueNeedShopIdMap.entrySet()) {
            if (subWarehouseShopSet.contains(entry.getKey())) {
                issueNeedStockDos.addAll(entry.getValue().values());
            }
        }
        //未获取到店铺需要库存直接返回
        if (issueNeedStockDos.size() <= 0) {
            return;
        }

        //判断仓库是否足够配发round之后的包数
        Map<String, BigDecimal> skuMidPackageMap = new HashMap<>();
        Set<String> notEnoughSku = new HashSet<>();
        //matCode维度的IssueNeedStockDO列表  Map<[MatCode_SizeID], List<IssueNeedStockDO>>
        Map<String, List<IssueNeedStockDO>> matCodeIssueNeedStockListMap = new HashMap<>();
        for (IssueNeedStockDO issueNeedStockDO : issueNeedStockDos) {
            String key = issueNeedStockDO.getMatCodeSizeNameKey();
            //库存中能提供的库存数量
            NewIssueOutStockDo newIssueOutStockDo;
            //实际需要库存 = 计算需要库存 - 店铺库存
            double actualNeedQty;
            //仓库中无此sku 或 实际需求数量小于0 不需计算
            if ((newIssueOutStockDo = outStockQtyMap.get(key)) == null
                    || (actualNeedQty = issueNeedStockDO.getRemainNeedQty() - issueNeedStockDO.getTotalStockQty()) <= 0
//                        || notEnoughSku.contains(key)
            ) {
                continue;
            }

            // 取出当前sku下所有门店的需求列表
            List<IssueNeedStockDO> issueNeedStockDoList = matCodeIssueNeedStockListMap.get(key);
            if (issueNeedStockDoList == null) {
                matCodeIssueNeedStockListMap.put(key, new ArrayList<>(Arrays.asList(issueNeedStockDO)));
            } else {
                issueNeedStockDoList.add(issueNeedStockDO);
                matCodeIssueNeedStockListMap.put(key, issueNeedStockDoList);
            }

            Integer outStockQty = newIssueOutStockDo.getRemainStockQty();
            //单个中包的matCode数量
            Integer matCodePackageNum = goodsInfoDoMap.get(issueNeedStockDO.getMatCode()).getMinPackageQty();
            //库存总量的中包数量
            int outStockPackageNum = outStockQty / matCodePackageNum;

            // 将仓库中不够分配的sku拧到notEnoughSku集合中
            if (!skuMidPackageMap.containsKey(key)) {
                BigDecimal packageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(matCodePackageNum), 0, RoundingMode.HALF_UP);
                skuMidPackageMap.put(key, packageNum);
                if (packageNum.compareTo(new BigDecimal(outStockPackageNum)) > 0) {
                    notEnoughSku.add(key);
                }
            } else {
                BigDecimal packageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(matCodePackageNum), 0, RoundingMode.HALF_UP);
                BigDecimal totalPackageNum = packageNum.add(skuMidPackageMap.get(key));
                skuMidPackageMap.put(key, totalPackageNum);
                if (totalPackageNum.compareTo(new BigDecimal(outStockPackageNum)) > 0) {
                    notEnoughSku.add(key);
                }
            }
        }

        for (Map.Entry<String, List<IssueNeedStockDO>> entry : matCodeIssueNeedStockListMap.entrySet()) {
            if (entry.getKey().startsWith("ACD2B00516Y1")) {
                System.out.println(1);
            }
            List<IssueNeedStockDO> issueNeedStockDoList = entry.getValue();
            GoodsInfoDO goodsInfoDo = goodsInfoDoMap.get(issueNeedStockDoList.get(0).getMatCode());
            BigDecimal quotePrice = dimGoodsPriceMap.get(issueNeedStockDoList.get(0).getMatCode());

            //分配完后最终的库存  保存分配完后剩余库存
            NewIssueOutStockDo newIssueOutStockDo = outStockQtyMap.get(entry.getKey());
            Integer finalOutStockQty = newIssueOutStockDo.getRemainStockQty();

            String key = entry.getKey();
            if (notEnoughSku.contains(key)) {
                //不足时需要排序
                String smallKey, midKey, bigKey;
                for (IssueNeedStockDO issueNeedStockDo : issueNeedStockDoList) {
                    smallKey = issueNeedStockDo.getShopId() + "_" + issueNeedStockDo.getCategoryName() + "_" + issueNeedStockDo.getMidCategoryName() + "_" + issueNeedStockDo.getSmallCategoryName();
                    midKey = issueNeedStockDo.getShopId() + "_" + issueNeedStockDo.getCategoryName() + "_" + issueNeedStockDo.getMidCategoryName();
                    bigKey = issueNeedStockDo.getShopId() + "_" + issueNeedStockDo.getCategoryName();
                    String shopLv;
                    if (shopInfoDataMap.get(issueNeedStockDo.getShopId()) != null && (shopLv = shopInfoDataMap.get(issueNeedStockDo.getShopId()).getShopLevel()) != null) {
                        shopLv = "-".equals(shopLv) ? "B" : shopLv;
                        issueNeedStockDo.setShopLevel(shopLv);
                    } else {
                        issueNeedStockDo.setShopLevel("Z");
                    }

                    if (issueNeedStockDo.getIssueFin() == null) {
                        issueNeedStockDo.setIssueFin(1);
                    }
                    issueNeedStockDo.setAvgSaleQty(issueNeedStockDo.getAvgSaleQty() == null ? 0d : issueNeedStockDo.getAvgSaleQty());
                    issueNeedStockDo.setSmallSumAvg(smallSumAvgMap.get(smallKey) == null ? 0d : smallSumAvgMap.get(smallKey));
                    issueNeedStockDo.setMidSumAvg(midSumAvgMap.get(midKey) == null ? 0d : midSumAvgMap.get(midKey));
                    issueNeedStockDo.setBigSumAvg(bigSumAvgMap.get(bigKey) == null ? 0d : bigSumAvgMap.get(bigKey));
                    issueNeedStockDo.setAllSumAvg(allSumAvgMap.get(issueNeedStockDo.getShopId()) == null ? 0d : allSumAvgMap.get(issueNeedStockDo.getShopId()));
                }
                issueNeedStockDoList.sort(new IssueNeedStockDO());
            }
            for (IssueNeedStockDO issueNeedStockDO : issueNeedStockDoList) {

                //实际需要库存 = 计算需要库存 - 店铺库存
                double actualNeedQty = issueNeedStockDO.getRemainNeedQty() - issueNeedStockDO.getTotalStockQty();

                NewIssueDetailDo newIssueDetailDo = new NewIssueDetailDo();

                newIssueDetailDo.setInShopId(issueNeedStockDO.getShopId());
//                newIssueDetailDo.setSizeId(issueNeedStockDO.getSizeId());
                newIssueDetailDo.setSizeName(issueNeedStockDO.getSizeName());
                newIssueDetailDo.setMatCode(issueNeedStockDO.getMatCode());
                newIssueDetailDo.setQuotePrice(quotePrice == null ? ZERO : quotePrice);
                newIssueDetailDo.setWarehouseCode(subWarehouseConfigDO.getWarehouseCode());

                BigDecimal actualIssuePackageNum = ZERO;
                BigDecimal issueQty = ZERO;
                //实际计算配发包数, 剩余库存满足一个包时计算, 否则直接分配0
                if (finalOutStockQty >= goodsInfoDo.getMinPackageQty()) {
                    actualIssuePackageNum = new BigDecimal(actualNeedQty).divide(new BigDecimal(goodsInfoDo.getMinPackageQty()), 0, RoundingMode.HALF_UP);
                    issueQty = actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty()));
                    if (finalOutStockQty - issueQty.intValue() < 0) {
                        //不足时直接down
                        actualIssuePackageNum = new BigDecimal(finalOutStockQty / goodsInfoDo.getMinPackageQty());
                        issueQty = actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty()));
                    }
                }

                finalOutStockQty -= issueQty.intValue();
                newIssueDetailDo.setQty(issueQty);
                //更新needStock的remainNeedQty
                issueNeedStockDO.setRemainNeedQty(new BigDecimal(issueNeedStockDO.getRemainNeedQty()).subtract(actualIssuePackageNum.multiply(new BigDecimal(goodsInfoDo.getMinPackageQty()))).doubleValue());

                //插入issueDetail
                List<NewIssueDetailDo> newIssueDetailDoList = issueDetailMap.computeIfAbsent(issueNeedStockDO.getShopId(), k -> new ArrayList<>());
                newIssueDetailDoList.add(newIssueDetailDo);
                issueDetailMap.put(issueNeedStockDO.getShopId(), newIssueDetailDoList);
            }

            //更新仓库里的剩余库存
            newIssueOutStockDo.setRemainStockQty(finalOutStockQty);
        }
    }


    /**
     * 汇集商品字段信息
     * @param taskId taskId
     * @param shopIds 需要计算的shopId集合
     * @param runTime 运行时间
     * @param reCalc 是否重算
     */
    public void processIssueGoodsData(int taskId, Set<String> shopIds, Date runTime, boolean reCalc) {
        List<ShopDisplayDesignView> shopDisplayDesignDataList = newIssueDOMapper.loadAllDisplayDesignV(shopIds);
        Map<String, ShopDisplayDesignView> shopDisplayDesignDataMap = shopDisplayDesignDataList.stream().collect(Collectors.toMap(data -> data.getShopId()+"_"+data.getCategoryName()+"_"+data.getMidCategoryName(), Function.identity(), (v1, v2) -> v1));
//        List<IssueGoodsData> issueGoodsDatas = newIssueDOMapper.queryAllIssueGoodsData(CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId,runTime), shopIds);
        //Map<shopId, List<IssueGoodsData>>
//        Map<String, List<IssueGoodsData>> issueGoodsDataMap = new HashMap<>(shopIds.size());
//        for (IssueGoodsData issueGoodsData : issueGoodsDatas) {
//            String key = issueGoodsData.getShopID();
//            List<IssueGoodsData> issueGoodsDataList = issueGoodsDataMap.get(key);
//            if (issueGoodsDataList == null) {
//                issueGoodsDataList = new ArrayList<>();
//                issueGoodsDataList.add(issueGoodsData);
//                issueGoodsDataMap.put(key, issueGoodsDataList);
//            } else {
//                issueGoodsDataList.add(issueGoodsData);
//            }
//        }

        List<SizeCountData> sizeCountDataAllList = allocationDOMapper2.getAllSizeCount(shopIds);
        //Map<shopId, List<SizeCountData>>
        Map<String, List<SizeCountData>> sizeCountDataMap = new HashMap<>(shopIds.size());
        for (SizeCountData sizeCountData : sizeCountDataAllList) {
            String key = sizeCountData.getShopId();
            List<SizeCountData> sizeCountDataList = sizeCountDataMap.get(key);
            if (sizeCountDataList == null) {
                sizeCountDataList = new ArrayList<>();
                sizeCountDataList.add(sizeCountData);
                sizeCountDataMap.put(key, sizeCountDataList);
            } else {
                sizeCountDataList.add(sizeCountData);
            }
        }

        List<MatcodeSaleRank> allNationalRanks = allocationDOMapper2.getAllBhSkcNationalRank();

        List<Future> list = new ArrayList<>();
        for (String shopId : shopIds) {
            Future future = issueDetailPool.submit(()-> {
                batchInsertGoodsData(taskId, shopId,runTime,shopDisplayDesignDataMap,
                        //issueGoodsDataMap,
                        sizeCountDataMap, allNationalRanks);
                return "ok";
            });
            list.add(future);
        }

        for(Future future : list) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理大类、中类skc统计信息
     * @param taskId taskId
     * @param shopIds shopIds
     * @param runTime runTime
     * @param reCalc reCalc
     */
    public void processCategorySkcData(int taskId, Set<String> shopIds, Date runTime, boolean reCalc) {
        String inStockTable = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId,runTime);

        List<NewCategorySkcData> shopCategoryList = newIssueDOMapper.getSkcCountCategorys(inStockTable,shopIds);
        List<NewCategorySkcData> shopMidCategoryList = newIssueDOMapper.getSkcCountMidCategorys(inStockTable,shopIds);

        Map<String,List<String>> shopCategoryMap = shopCategoryList.stream().collect(Collectors.groupingBy(NewCategorySkcData::getShopId,Collectors.mapping(NewCategorySkcData::getCategoryName,Collectors.toList())));
        Map<String,List<String>> shopMidCategoryMap = shopMidCategoryList.stream().collect(Collectors.groupingBy(NewCategorySkcData::getShopId,Collectors.mapping(data-> data.getCategoryName()+":"+data.getMidCategoryName(),Collectors.toList())));

        // 计算大类skc统计信息
        CategorySkcCountTask categorySkcCountTask = new CategorySkcCountTask(taskId,shopIds,0,shopIds.size(),newIssueDOMapper,issueDetailPool,shopCategoryMap,runTime);
        fjPool.submit(categorySkcCountTask);

        // 计算中类skc统计信息
        MidCategorySkcCountTask midCategorySkcCountTask = new MidCategorySkcCountTask(taskId,shopIds,0,shopIds.size(),newIssueDOMapper,issueDetailPool,shopMidCategoryMap,runTime);
        fjPool.submit(midCategorySkcCountTask);
    }

//    /**
//     * 处理大类、中类skc统计信息
//     * @param taskId
//     * @param shopIds
//     */
//    public void processUndoData(int taskId,List<String> shopIds,Date runTime) {
//
//        if (shopIds == null || shopIds.isEmpty()) {
//            List<cn.nome.saas.allocation.model.old.allocation.ShopInfoDo> shopInfoDos = issueDOMapper2.shops();
//            shopIds = shopInfoDos.stream().map(ShopInfoDo::getShopID).collect(Collectors.toList());
//        }
//
//        IssueUndoTask undoTask = new IssueUndoTask(shopIds, taskId, 0, shopIds.size(), this);
//
//        String inStockTable = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId,runTime);
//        String tableName = CommonUtil.getTaskTableName(Constant.ISSUE_CATEGORY_DATA_TABLE_PREFIX, taskId,runTime);
//        String midTableName = CommonUtil.getTaskTableName(Constant.ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX, taskId,runTime);
//
//        newIssueDOMapper.dropTableIfExist(tableName);
//        newIssueDOMapper.dropTableIfExist(midTableName);
//
//        newIssueDOMapper.createCategoryCountTable(tableName);
//        newIssueDOMapper.createMidCategoryCountTable(midTableName);
//
//        List<NewCategorySkcData> shopCategoryList = newIssueDOMapper.getSkcCountCategorys(inStockTable,shopIds);
//        List<NewCategorySkcData> shopMidCategoryList = newIssueDOMapper.getSkcCountMidCategorys(inStockTable,shopIds);
//
//        Map<String,List<String>> shopCategoryMap = shopCategoryList.stream().collect(Collectors.groupingBy(NewCategorySkcData::getShopId,Collectors.mapping(NewCategorySkcData::getCategoryName,Collectors.toList())));
//        Map<String,List<String>> shopMidCategoryMap = shopMidCategoryList.stream().collect(Collectors.groupingBy(NewCategorySkcData::getShopId,Collectors.mapping(data->{return data.getCategoryName()+":"+data.getMidCategoryName();},Collectors.toList())));
//
//        // 计算大类skc统计信息
//        CategorySkcCountTask categorySkcCountTask = new CategorySkcCountTask(taskId,shopIds,0,shopIds.size(),newIssueDOMapper,issueDetailPool,shopCategoryMap,runTime);
//        fjPool.invoke(categorySkcCountTask);
//
//        // 计算中类skc统计信息
//        MidCategorySkcCountTask midCategorySkcCountTask = new MidCategorySkcCountTask(taskId,shopIds,0,shopIds.size(),newIssueDOMapper,issueDetailPool,shopMidCategoryMap,runTime);
//        fjPool.invoke(midCategorySkcCountTask);
//    }

    public int batchInsertGoodsData(int taskId, String shopID,Date runTime,Map<String, ShopDisplayDesignView> shopDisplayDesignDataMap,
                                    //Map<String, List<IssueGoodsData>> issueGoodsDataMap,
                                    Map<String, List<SizeCountData>> sizeCountDataMap, List<MatcodeSaleRank> allNationalRanks) {
        int rst = 0;
        List<IssueGoodsData> issueGoodsData = newIssueDOMapper.queryIssueGoodsData(CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId,runTime),shopID);
//        List<IssueGoodsData> issueGoodsData = issueGoodsDataMap.get(shopID);
        if (issueGoodsData == null || issueGoodsData.isEmpty()) {
            logger.info("insertGoodsData getIssueGoodsData shopID:{},rst:{}", shopID, rst);
            return rst;
        }

        for (IssueGoodsData goodsData : issueGoodsData) {
            String goodsPK = goodsData.getShopID()+"_"+goodsData.getCategoryName()+"_"+goodsData.getMidCategoryName();
            ShopDisplayDesignView shopDisplayDesignData = shopDisplayDesignDataMap.get(goodsPK);
            if (shopDisplayDesignData != null) {
                goodsData.setDisplayQty(shopDisplayDesignData.getDisplay_Qty() == null ? ZERO: shopDisplayDesignData.getDisplay_Qty());
                goodsData.setDisplayPercent(shopDisplayDesignData.getDisplayPercent() == null ? ZERO : shopDisplayDesignData.getDisplayPercent());
            }
        }

        Map<String, IssueGoodsData> matCodeDataMap = issueGoodsData.stream().collect(Collectors.toMap(IssueGoodsData::getMatCode, Function.identity(), (v1, v2) -> v1));
//        Iterator<IssueGoodsData> itr = issueGoodsData.iterator();
//        Map<String, IssueGoodsData> matCodeSizeIdDataMap = new HashMap<>();
//        Map<String, IssueGoodsData> matCodeDataMap = new HashMap<>();
//        Set<String> fzMatcodes = new HashSet<>();
//        Set<String> bhMatcodes = new HashSet<>();
        Set<String> bhMatcodes = matCodeDataMap.keySet();

//        Set<String> matCodes = new HashSet<>();
//        Set<String> sizeIds = new HashSet<>();

        String tableName = CommonUtil.getTaskTableName(ISSUE_GOODS_DATA_TABLE_PREFIX, taskId,runTime);

//        while (itr.hasNext()) {
//            IssueGoodsData item = itr.next();

//            matCodes.add(item.getMatCode());
//            sizeIds.add(item.getSizeID());

//            matCodeSizeIdDataMap.put(item.getMatCode() + item.getSizeID(), item);

//            matCodeDataMap.put(item.getMatCode(), item);

//            if ("M".equalsIgnoreCase(item.getCategoryCode()) || "W".equalsIgnoreCase(item.getCategoryCode())){
//                fzMatcodes.add(item.getMatCode());
//            }else {
//                bhMatcodes.add(item.getMatCode());
//            }
//        }
//        //28天
//        List<SaleQtyData> saleQty28Data = allocationDOMapper2.getSale28(shopID, matcodes, sizeIds);
//        if (saleQty28Data != null && !saleQty28Data.isEmpty()) {
//            Iterator<SaleQtyData> itr28 = saleQty28Data.iterator();
//            while (itr28.hasNext()) {
//                SaleQtyData saleQtyData = itr28.next();
//                IssueGoodsData goodsData = matCodeSizeIdDataMap.get(saleQtyData.getMatCode() + saleQtyData.getSizeId());
//                if (goodsData != null) {
//                    goodsData.setSaleQty28(new BigDecimal(saleQtyData.getSaleQty()));
//                }
//            }
//        }
//        //7天
//        List<SaleQtyData> saleQty7Data = allocationDOMapper2.getSale7(shopID, matcodes, sizeIds);
//        if (saleQty7Data != null && !saleQty7Data.isEmpty()) {
//            Iterator<SaleQtyData> itr7 = saleQty7Data.iterator();
//            while (itr7.hasNext()) {
//                SaleQtyData saleQtyData = itr7.next();
//                IssueGoodsData goodsData = matCodeSizeIdDataMap.get(saleQtyData.getMatCode() + saleQtyData.getSizeId());
//                if (goodsData != null) {
//                    goodsData.setSaleQty7(new BigDecimal(saleQtyData.getSaleQty()));
//                }
//            }
//        }
        // 尺码数
//        List<SizeCountData> sizeCountDataList = allocationDOMapper2.getSizeCount(shopID, matCodeDataMap.keySet());
        List<SizeCountData> sizeCountDataList = sizeCountDataMap.get(shopID);
        if (sizeCountDataList != null && !sizeCountDataList.isEmpty()) {

            logger.debug("sizeCountDataList:{}",sizeCountDataList.size());

            Iterator<SizeCountData> sizeCountDataItr = sizeCountDataList.iterator();
            while (sizeCountDataItr.hasNext()) {
                SizeCountData sizeCountData = sizeCountDataItr.next();
                IssueGoodsData goodsData = matCodeDataMap.get(sizeCountData.getMatcode());
                if (goodsData != null) {
                    goodsData.setSizeCount(sizeCountData.getCount());
                }
            }
        }

        //服装无需计算
//        if (!fzMatcodes.isEmpty()) {
//            //skc销售额在本店排名，服装分类计算
//            List<MatcodeSaleRank> saleRanks = allocationDOMapper2.getSkcShopRank(fzMatcodes, shopID);
//            setSkcRank(matCodeDataMap, saleRanks, 0);
//
//            //skc销售额在全国排名，服装分类计算
//            saleRanks = allocationDOMapper2.getFzSkcNationalRank(fzMatcodes, shopID);
//            setSkcRank(matCodeDataMap, saleRanks, 1);
//        }
        if (!bhMatcodes.isEmpty()) {
            //skc销售额在本店排名，百货分类计算
            List<MatcodeSaleRank> saleRanks = allocationDOMapper2.getSkcShopRank(bhMatcodes, shopID);
            setSkcRank(matCodeDataMap, saleRanks, 0);

            //skc销售额在全国排名，百货分类计算
//            saleRanks = allocationDOMapper2.getBhSkcNationalRank(bhMatcodes, shopID);
            setSkcRank(matCodeDataMap, allNationalRanks, 1);
        }
//
//        //插入之前删除原数据
//        rst = issueRestService.delGoodsData(taskId,shopID);
//        logger.info("insertGoodsData batchDel shopID:{},rst:{}", shopID, rst);

//        rst = issueRestDOMapper2.batchInsertGoodsData(issueGoodsData);
        rst = newIssueDOMapper.batchInsertGoodsData(tableName, issueGoodsData);
        logger.info("insertGoodsData batchInsert shopID:{},rst:{}", shopID, rst);

        return rst;
    }

    /**
     * @param matCodeDataMap
     * @param saleRanks
     * @param rankType       本店排名0，全国排名1
     */
    private void setSkcRank(Map<String, IssueGoodsData> matCodeDataMap, List<MatcodeSaleRank> saleRanks, int rankType) {
        if (saleRanks != null && !saleRanks.isEmpty()) {
            Iterator<MatcodeSaleRank> rankItr = saleRanks.iterator();
            while (rankItr.hasNext()) {
                MatcodeSaleRank rank = rankItr.next();
                IssueGoodsData goodsData = matCodeDataMap.get(rank.getMatCode());
                if (goodsData == null) {
                    return;
                }
                if (rankType == 0) {
                    goodsData.setShopRank(rank.getRankNo());
                } else if (rankType == 1) {
                    goodsData.setNationalRank(rank.getRankNo());
                }
            }
        }
    }

    private Map<String, Integer> getNewGoodsIssueNumMap(List<NewGoodsIssueRangeDetailDO> newGoodsIssueRangeDetailDOS) {
        Map<String, Integer> issueMap = new HashMap<>();
        for (NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailDO : newGoodsIssueRangeDetailDOS) {
            String key = newGoodsIssueRangeDetailDO.getShopIdMatCodeSizeNameKey();
            try {
                //判断上市时间与在途天数与到货时间不为空
                if (newGoodsIssueRangeDetailDO.getSaleTime() == null || newGoodsIssueRangeDetailDO.getRoadDay() == null || StringUtils.isBlank(newGoodsIssueRangeDetailDO.getIssueTime())) {
                    issueMap.put(key, 0);
                    continue;
                }
                Calendar calBegin = Calendar.getInstance();
                calBegin.set(Calendar.DAY_OF_WEEK,2);
                calBegin.add(Calendar.DATE, -7);
                Calendar calEnd = Calendar.getInstance();
                calEnd.set(Calendar.DAY_OF_WEEK,2);
                calEnd.add(Calendar.DATE, 7);
                //根据店铺到货时间获取当前时间前后两周的到货日期, 取距离上市日期最近的到货日期
                Calendar calSaleTime = Calendar.getInstance();
                calSaleTime.setTime(newGoodsIssueRangeDetailDO.getSaleTime());
                Calendar recentDate = null;
                for (Calendar calendar : IssueDayUtil.getDayOfWeekWithinDateInterval(calBegin, calEnd, new HashSet<>(IssueDayUtil.convertIssueTimeV2(newGoodsIssueRangeDetailDO.getIssueTime())))) {
                    if (calendar.compareTo(calSaleTime) >= 0) {
                        recentDate = calendar;
                        break;
                    }
                }
                //当天日期 + 在途天数 + 1 >= D(距离上市日期最近的到货日期) , 则配发需求量 = 计划数量, 否则 = 0;
                Calendar calNow = Calendar.getInstance();
                calNow.add(Calendar.DATE, newGoodsIssueRangeDetailDO.getRoadDay() + 1);
                if (recentDate != null && calNow.compareTo(recentDate) >= 0) {
                    issueMap.put(key, newGoodsIssueRangeDetailDO.getNum());
                } else {
                    issueMap.put(key, 0);
                }
            } catch (Exception e) {
                LoggerUtil.info(e, logger, "[issueDetailGetNewGoodsIssueNumMap] catch exception, newGoodsIssueRangeDetailDO:{0}", newGoodsIssueRangeDetailDO);
                issueMap.put(key, 0);
            }
        }
        return issueMap;
//        return newGoodsIssueRangeDetailDOS.stream().collect(Collectors.toMap(data -> data.getShopId() + "_" + data.getMatCode() + "_" + data.getSizeId(),
//                newGoodsIssueRangeDetailDO -> {
//            try {
//                //判断上市时间与在途天数与到货时间不为空
//                if (newGoodsIssueRangeDetailDO.getSaleTime() == null || newGoodsIssueRangeDetailDO.getRoadDay() == null || StringUtils.isBlank(newGoodsIssueRangeDetailDO.getIssueTime())) {
//                    return 0;
//                }
//                Calendar calBegin = Calendar.getInstance();
//                calBegin.set(Calendar.DAY_OF_WEEK,2);
//                calBegin.add(Calendar.DATE, -7);
//                Calendar calEnd = Calendar.getInstance();
//                calEnd.set(Calendar.DAY_OF_WEEK,2);
//                calEnd.add(Calendar.DATE, 7);
//                //根据店铺到货时间获取当前时间前后两周的到货日期, 取距离上市日期最近的到货日期
//                Calendar calSaleTime = Calendar.getInstance();
//                calSaleTime.setTime(newGoodsIssueRangeDetailDO.getSaleTime());
//                Calendar recentDate = null;
//                for (Calendar calendar : IssueDayUtil.getDayOfWeekWithinDateInterval(calBegin, calEnd, new HashSet<>(IssueDayUtil.convertIssueTimeV2(newGoodsIssueRangeDetailDO.getIssueTime())))) {
//                    if (calendar.compareTo(calSaleTime) >= 0) {
//                        recentDate = calendar;
//                        break;
//                    }
//                }
//                //当天日期 + 在途天数 + 1 >= D(距离上市日期最近的到货日期) , 则配发需求量 = 计划数量, 否则 = 0;
//                Calendar calNow = Calendar.getInstance();
//                calNow.add(Calendar.DATE, newGoodsIssueRangeDetailDO.getRoadDay() + 1);
//                if (recentDate != null && calNow.compareTo(recentDate) >= 0) {
//                    return newGoodsIssueRangeDetailDO.getNum();
//                } else {
//                    return 0;
//                }
//            } catch (Exception e) {
//                LoggerUtil.info(e, logger, "[issueDetailGetNewGoodsIssueNumMap] catch exception, newGoodsIssueRangeDetailDO:{0}", newGoodsIssueRangeDetailDO);
//                return 0;
//            }
//        }, (v1, v2) -> v1));
    }

}
