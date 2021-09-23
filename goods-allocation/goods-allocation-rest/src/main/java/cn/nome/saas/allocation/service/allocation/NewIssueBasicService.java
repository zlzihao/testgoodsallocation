package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.GoodsInfoCache;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.vertical.NewIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.old.allocation.ProhibitedService;
import cn.nome.saas.allocation.utils.CommonUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.*;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * NewIssueBasicService
 *
 * @author Bruce01.fan
 * @date 2019/9/6
 */
@Service
public class NewIssueBasicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NewIssueExtraDataMapper newIssueExtraDataMapper;
    @Autowired
    private ApsIssueInStockDataMapper apsIssueInStockDataMapper;
    @Autowired
    private SubWarehouseConfigDOMapper subWarehouseConfigDOMapper;
    @Autowired
    private NewIssueDOMapper newIssueDOMapper;
    @Autowired
    private GoodsInfoDOMapper goodsInfoDOMapper;
    @Autowired
    private ProhibitedService prohibitedService;
    @Autowired
    private NewIssueSkuCalcDOMapper newIssueSkuCalcDOMapper;
    @Autowired
    private ShopInfoDOMapper shopInfoDOMapper;
    @Autowired
    private DwsDimGoodsDOMapper dwsDimGoodsDOMapper;
    @Autowired
    private NewGoodsIssueRangeDetailMapper newGoodsIssueRangeDetailMapper;
    @Autowired
    private IssueSandboxRollingStockMapper issueSandboxRollingStockMapper;
    @Autowired
    ShopListCache shopListCache;
    @Autowired
    GoodsInfoCache goodsInfoCache;
    @Autowired
    ShopInfoCache shopInfoCache;

    private static String loadFilePath = "/tmp/cache/";
//    private static String loadFilePath = "";

    private static BigDecimal ZERO = new BigDecimal(0);
    private static BigDecimal ONE = new BigDecimal(1);

    /**
     * 线程池
     */
    private ExecutorService issueInStockThreadFactoryPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-in-stock-%d").build());
    private ExecutorService addIssueInStockPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("add-issue-in-stock-%d").build());
    private ExecutorService addNewSkcInStockPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("add-new-skc-in-stock-%d").build());

    /**
     * 供给池计算
     * @param task
     */
    public void issueOutStock(IssueTaskDO task) {
        try {
            List<SubWarehouseConfigDO> subWarehouseConfigDos = subWarehouseConfigDOMapper.selectByPage(new HashMap<>());
            Set<String> warehouseCodes = subWarehouseConfigDos.stream().map(SubWarehouseConfigDO::getWarehouseCode).collect(Collectors.toSet());
            String tableName = CommonUtil.getTaskTableName(ISSUE_OUT_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());
            newIssueDOMapper.dropTableIfExist(tableName);
            newIssueDOMapper.createIssueOutStockTab(tableName);
            for (String warehouseCode : warehouseCodes) {
                List<NewIssueOutStockDo> newIssueOutStockDos = newIssueExtraDataMapper.getIssueOutStockList(warehouseCode);
                if (newIssueOutStockDos != null && newIssueOutStockDos.size() > 0) {
                    LoggerUtil.info(logger, "取数结果为：{0}", newIssueOutStockDos.size());

                    // 分批录入
                    List<NewIssueOutStockDo> tempList;
                    int count = 0;
                    int size = 5000;
                    AtomicInteger threadCount = new AtomicInteger(0);
                    List<Future> futureList = new ArrayList<>();
                    while(true) {
                        tempList = newIssueOutStockDos.stream().skip(count * size).limit(size).collect(Collectors.toList());
                        if (tempList.size() == 0) {
                            break;
                        }
                        List<NewIssueOutStockDo> finalList = tempList;
                        Future future = issueInStockThreadFactoryPool.submit(() -> {
                            try {
                                newIssueDOMapper.addIssueOutStock(tableName, finalList);
                            } catch (Exception e) {
                                LoggerUtil.error(e, logger, "[addIssueOutStock] catch exception");
                            } finally {
                                threadCount.getAndIncrement();
                            }
                            return "OK";
                        });
                        futureList.add(future);
                        ++count;
                    }

                    for (Future future : futureList) {
                        if (future.isDone() && !future.isCancelled()){continue;}
                        future.get();
                    }
                }
            }

            LoggerUtil.info(logger, "[issueOutStock] issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueOutStock] catch exception");
        }

    }

    /**
     * 供给池计算 重算
     * @param task
     */
    public void issueOutStock(IssueTaskDO task, Set<String> shopIds, boolean reCalc) {
        try {
            //是否需要全部备份
//            //单店重算
            String bakTableName = CommonUtil.getTaskTableName(BAK_ISSUE_OUT_STOCK_TABLE_PREFIX, task.getId(), task.getRunTime());
            String tableName = CommonUtil.getTaskTableName(ISSUE_OUT_STOCK_TABLE_PREFIX, task.getId(), task.getRunTime());
//            // 创建新表
            if (newIssueSkuCalcDOMapper.checkTableExists(bakTableName) == 0) {
                newIssueDOMapper.createIssueOutStockTab(bakTableName);
            }
            newIssueDOMapper.bakIssueOutStock(bakTableName, tableName);
//            newIssueDOMapper.delInStockByShopId(tableName, shopIds);

            String detailTabName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, task.getId(),task.getRunTime());
            String outTabName = CommonUtil.getTaskTableName(ISSUE_OUT_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());
            newIssueDOMapper.updOutRemainStock(detailTabName, outTabName, shopIds);

            LoggerUtil.info(logger, "[issueOutStock] " + (reCalc ? "reCalc" : "") + "issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueOutStock] " + (reCalc ? "reCalc" : "") + " catch exception");
        }

    }

    /**
     * issueOutStockSandBox
     * @param issueHuihuoDateMap issueHuihuoDateMap
     * @param issueOutMap issueOutMap
     * @param issueDetailDateMap issueDetailDateMap
     * @param issueArriveMappingMap issueArriveMappingMap
     * @param calcDate calcDate
     * @param first first
     */
    public void issueOutStockSandBox(Map<Date, Map<String, HuihuoGoodsDo>> issueHuihuoDateMap,
                                     Map<String, Map<String, NewIssueOutStockDo>> issueOutMap, Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailDateMap, Map<Date, Map<String, Date>> issueArriveMappingMap,
                                     Calendar calcDate, boolean first) {
        try {
            List<SubWarehouseConfigDO> subWarehouseConfigDos = subWarehouseConfigDOMapper.selectByPage(new HashMap<>());
            Set<String> warehouseCodes = subWarehouseConfigDos.stream().map(SubWarehouseConfigDO::getWarehouseCode).collect(Collectors.toSet());

            if (first) {
                for (String warehouseCode : warehouseCodes) {
                    List<NewIssueOutStockDo> newIssueOutStockDos = newIssueExtraDataMapper.getIssueOutStockList(warehouseCode);

                    Map<String, NewIssueOutStockDo> warehouseOutMap = new HashMap<>();
                    issueOutMap.put(warehouseCode, warehouseOutMap);
                    for (NewIssueOutStockDo newIssueOutStockDo : newIssueOutStockDos) {
                        newIssueOutStockDo.setRemainStockQty(newIssueOutStockDo.getStockQty());
                        warehouseOutMap.put(newIssueOutStockDo.getMatCodeSizeNameKey(), newIssueOutStockDo);
                    }
                }
            } else {
                //1. 获取数仓回货数量
                Map<String, HuihuoGoodsDo> huihuoMap = issueHuihuoDateMap.get(calcDate.getTime());

                //2. 上个日期的发货数量 Map<Date, Map<shopID, List<NewIssueDetailDo>>>
                Calendar calYesterday = (Calendar) calcDate.clone();
                calYesterday.add(Calendar.DATE, -1);
                //matcode配发的数量Map<warehouseCode_MatCode_SizeId, num>
                Map<String, BigDecimal> issueMatCodeMap = new HashMap<>();
                Map<String, List<NewIssueDetailDo>> issueMap = issueDetailDateMap.get(calYesterday.getTime());
                //获取上个日期发货的店铺
                if  (issueArriveMappingMap.get(calYesterday.getTime()) != null) {
                    Set<String> issueShopIds = issueArriveMappingMap.get(calYesterday.getTime()).keySet();
                    for (String issueShopId : issueShopIds) {
                        List<NewIssueDetailDo> issueList;
                        if ((issueList = issueMap.get(issueShopId)) != null) {
                            for (NewIssueDetailDo newIssueDetailDo : issueList) {
                                String key = newIssueDetailDo.getWarehouseMatCodeSizeNameKey();
                                issueMatCodeMap.put(key, issueMatCodeMap.get(key) == null ? newIssueDetailDo.getQty() : issueMatCodeMap.get(key).add(newIssueDetailDo.getQty()));
                            }
                        }
                    }
                }
                //批量更新
                //分仓上期末库存 = 上期初值 - 配补总数量
                //分仓本期初库存 = 上期末库存 + 本轮回货库存
                //批量更新 分仓库存减去上期配补总数量再加上本轮回货库存  qty与剩余库存remainQty
                String key;Integer initQty;BigDecimal issueQty, huihuoQty;;
                HuihuoGoodsDo huihuoGoodsDo;
                for (Map<String, NewIssueOutStockDo> value : issueOutMap.values()) {
                    for (NewIssueOutStockDo newIssueOutStockDo : value.values()) {
                        key = newIssueOutStockDo.getWarehouseMatCodeSizeNameKey();
                        initQty = newIssueOutStockDo.getStockQty();
                        if ((issueQty = issueMatCodeMap.get(key)) != null) {
                            initQty -= issueQty.intValue();
                        }
                        if (huihuoMap != null && (huihuoGoodsDo = huihuoMap.get(key)) != null && (huihuoQty = huihuoGoodsDo.getHuihuoQty()) != null) {
                            initQty += huihuoQty.intValue();
                        }
                        newIssueOutStockDo.setStockQty(initQty);
                        newIssueOutStockDo.setRemainStockQty(initQty);
                    }
                }

                //回货的是新品 则新增到outStockMap
                if (huihuoMap != null) {
                    for (HuihuoGoodsDo value : huihuoMap.values()) {
                        Map<String, NewIssueOutStockDo> issueOutWarehouseMap;
                        if ((issueOutWarehouseMap = issueOutMap.get(value.getWarehouseCode())) != null && issueOutWarehouseMap.get(value.getMatCodeSizeNameKey()) == null) {
                            NewIssueOutStockDo newIssueOutStockDo = new NewIssueOutStockDo();
                            newIssueOutStockDo.setWarehouseCode(value.getWarehouseCode());
                            newIssueOutStockDo.setMatCode(value.getMatCode());
                            newIssueOutStockDo.setSizeName(value.getSizeName());
                            newIssueOutStockDo.setStockQty(value.getHuihuoQty().intValue());
                            newIssueOutStockDo.setRemainStockQty(value.getHuihuoQty().intValue());
                            issueOutWarehouseMap.put(value.getMatCodeSizeNameKey(), newIssueOutStockDo);
                        }
                    }
                }
            }

            //沙盘记录日志
            Integer total = 0;
            for (Map.Entry<String, Map<String, NewIssueOutStockDo>> entry : issueOutMap.entrySet()) {
                Map<String, NewIssueOutStockDo> map = entry.getValue();
                Integer warehouseTotal = 0;
                for (NewIssueOutStockDo value : map.values()) {
                    total += value.getRemainStockQty();
                    warehouseTotal += value.getRemainStockQty();
//                    if ("NM001275".equals(value.getWarehouseCode()) && value.getRemainStockQty() > 0) {
//                        LoggerUtil.info(logger, "[warehouseGoods]:" + value.getWarehouseMatCodeSizeIdKey() + ":" + value.getRemainStockQty() + ":" +  calcDate.getTime());
//                    }
                }
                LoggerUtil.info(logger, "[warehouse warehouseTotal]:" + entry.getKey() + ":" + warehouseTotal + ":" +  calcDate.getTime());
            }
            LoggerUtil.info(logger, "[warehouse total]:" + total + ":" +  calcDate.getTime());
            LoggerUtil.info(logger, "[issueOutStockSandBox] issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueOutStockSandBox] catch exception");
        }

    }


//    /**
//     *
//     * @param task
//     * @param shopIds
//     */
//    public void issueInStock(IssueTaskDO task, Set<String> shopIds) {
//        issueInStock(task, shopIds, false);
//    }

    /**
     * issueInStock
     * @param task
     * @param shopIds
     * @param reCalc
     */
    public void issueInStock(IssueTaskDO task, Set<String> shopIds, boolean reCalc) {
        try {
            logger.info("[issueInStock] getProhibitedGoods start");
            /**
             * 禁配商品
             * 1. 先取禁配表作为基本数据
             * 2. 取白名单，根据白名单排查，白名单之外的所有shopId加入禁配数据
             * 3. 取保底数据，加入禁配数据（因为是putIfAbsent，可能有遗漏）
             */
            Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = prohibitedService.getProhibitedGoods(shopIds);
            logger.info("[issueInStock] getProhibitedGoods end");

            logger.info("[issueInStock] getNotSaleTime start");
            // 新品，应该为空
            List<NewGoodsIssueRangeDetailDO> newGoodsIssueRangeDetailDOS = newGoodsIssueRangeDetailMapper.getNotSaleTime();
            logger.info("[issueInStock] getNotSaleTime end");
            Set<String> notSaleTimeGoodsRange = newGoodsIssueRangeDetailDOS.stream().map(NewGoodsIssueRangeDetailDO::getShopIdMatCodeSizeNameKey).collect(Collectors.toSet());

            String tableName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, task.getId(), task.getRunTime());
            //全部重算
            if (!reCalc) {
                newIssueDOMapper.dropTableIfExist(tableName);
                newIssueDOMapper.createIssueInStockTab(tableName);
            } else {
                //单店重算
                String bakTableName = CommonUtil.getTaskTableName(BAK_ISSUE_IN_STOCK_TABLE_PREFIX, task.getId(), task.getRunTime());
                // 创建新表
                if (newIssueSkuCalcDOMapper.checkTableExists(bakTableName) == 0) {
                    newIssueDOMapper.createIssueInStockTab(bakTableName);
                }
                newIssueDOMapper.bakInStockData(bakTableName, tableName, shopIds);
                newIssueDOMapper.delInStockByShopId(tableName, shopIds);
            }

            //shopIds为空时默认跑全部店铺
            if (shopIds == null) {
                List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.shopInfoData();
                shopIds = shopInfoDataList.stream().map(ShopInfoData::getShopID).collect(Collectors.toSet());
            }

            List<Future> futureList = new ArrayList<>();

            //分批跑inStock
            int inCount = 0;
            int pageSize = 70;
            while(true) {
                Set<String> tempShopIds = shopIds.stream().skip(inCount * pageSize).limit(pageSize).collect(Collectors.toSet());
                if (tempShopIds.size() == 0) {
                    break;
                }
                logger.info(String.format("[issueInStock] inCount = %d, tempShopIds = %s", inCount, JSON.toJSON(tempShopIds)));

                final int index = inCount;

                Future<String> future = addIssueInStockPool.submit(() -> {
                    return issueInStock(prohibitedGoods, notSaleTimeGoodsRange,2, tempShopIds, index);
                });
                ++inCount;
                futureList.add(future);
            }

            List<String> fileNameList = new ArrayList<>();
            for (Future future : futureList) {
                fileNameList.add(future.get().toString());
            }

            logger.info("[issueInStock] unionFileAndLoadDataInStock start");
            unionFileAndLoadDataInStock(fileNameList, tableName);
            logger.info("[issueInStock] unionFileAndLoadDataInStock end");

//            issueInNewSkcStock(task, prohibitedGoods, shopIds);

            LoggerUtil.info(logger, "[issueInStock] issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueInStock] catch exception");
            logger.error("[issueInStock] catch exception", e);
        }

    }

    public String issueInStock(Map<String, Map<String, ProhibitedGoods>> prohibitedGoods, Set<String> notSaleTimeGoodsRange, int type, Set<String> shopIds, int index) {
        List<NewIssueInStockDo> newIssueInStockDos = apsIssueInStockDataMapper.getIssueInStockList(shopIds);
        logger.info(String.format("[issueInStock] shop_ids = %s, count(newIssueInStockDos) = %d", JSON.toJSON(shopIds), newIssueInStockDos.size()));
        List<GoodsInfoDO> goodsInfoDoList = goodsInfoCache.getGoodsInfo();
        logger.info(String.format("[issueInStock] shop_ids = %s, count(goodsInfoDoList) = %d", JSON.toJSON(shopIds), goodsInfoDoList.size()));
        Map<String, GoodsInfoDO> goodsInfoDoMap = goodsInfoDoList.stream().collect(Collectors.toMap(GoodsInfoDO::getMatCode, Function.identity(), (o1, o2) -> o2 ));

        return writeInStockFile(newIssueInStockDos, notSaleTimeGoodsRange, goodsInfoDoMap, prohibitedGoods, index);
    }


    /**
     * 沙盘计算inStock
     * @param first 是否第一次计算
     * @param  calcDate calcDate
     */
    void issueInStockSandBox(IssueSandBoxTask issueSandBoxTask, boolean first, Calendar calcDate) {
        try {
            Set<String> shopIds = issueSandBoxTask.getShopIds();
            Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap = issueSandBoxTask.getIssueInStockShopIdMap();
            Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailMap = issueSandBoxTask.getIssueDetailDateMap();

            //禁配日期
            Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = prohibitedService.getProhibitedGoodsByDate(shopIds, calcDate.getTime());

            //shopIds为空时默认跑全部店铺
            List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.shopInfoData();
            Map<String, ShopInfoData> shopInfoDataMap = shopInfoDataList.stream().collect(Collectors.toMap(ShopInfoData::getShopID, Function.identity()));
            if (shopIds == null) {
                shopIds = shopInfoDataMap.keySet();
            }

            List<Future> futureList = new ArrayList<>();

            //分批跑inStock, 50一组
            int inCount = 0;
            int pageSize = 20;
            while(true) {
                Set<String> tempShopIds = shopIds.stream().skip(inCount * pageSize).limit(pageSize).collect(Collectors.toSet());
                if (tempShopIds.size() == 0) {
                    break;
                }

                Future<String> future = addIssueInStockPool.submit(() -> {
                    return issueInStockSandBoxSub(issueSandBoxTask, prohibitedGoods, tempShopIds,  shopInfoDataMap, first, calcDate);
                });
                ++inCount;
                futureList.add(future);
            }

            List<String> fileNameList = new ArrayList<>();
            for (Future future : futureList) {
                fileNameList.add(future.get().toString());
            }

            //释放掉内存, 只保留7天的detail供计算
            Calendar calLastWeek = (Calendar) calcDate.clone();
            calLastWeek.add(Calendar.DATE, -8);
            issueDetailMap.remove(calLastWeek.getTime());

            for (String shopid : issueInStockShopIdMap.keySet()) {
                if ("NM000006".equals(shopid)) {
                    for (NewIssueInStockDo nn : issueInStockShopIdMap.get(shopid).values()) {
                        if ("ACD2B00516Y1".equals(nn.getMatCode())) {
                            LoggerUtil.info(logger, "calcData:NewIssueInStockDo:" + nn + ":" +  calcDate.getTime());
                        }
                    }
                }
            }

            LoggerUtil.info(logger, "[issueInStock] issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueInStock] catch exception");
        }

    }

    /**
     * issueInStockSandBox具体计算
     * @param prohibitedGoods 禁配Map
     * @param shopIds shopIds
     * @param shopInfoDataMap shopInfoDataMap
     * @param first first
     * @param calcDate calcDate
     * @return 1
     */
    private String issueInStockSandBoxSub(IssueSandBoxTask issueSandBoxTask, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,
                                          Set<String> shopIds, Map<String, ShopInfoData> shopInfoDataMap,
                                          boolean first, Calendar calcDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String d = sdf.format(calcDate.getTime());
        try {
            Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap = issueSandBoxTask.getIssueInStockShopIdMap();
            Map<Date, Map<String, List<NewIssueDetailDo>>> issueDetailDateMap = issueSandBoxTask.getIssueDetailDateMap();
            Map<Date, Map<String, Date>> issueArriveMappingMap = issueSandBoxTask.getIssueArriveMappingMap();
            Map<String, BigDecimal> issueSalePredictMap = issueSandBoxTask.getIssueSalePredictMap();

            if (first) {
                List<GoodsInfoDO> goodsInfoDoList = goodsInfoCache.getGoodsInfo();
                Map<String, GoodsInfoDO> goodsInfoDoMap = goodsInfoDoList.stream().collect(Collectors.toMap(GoodsInfoDO::getMatCode, Function.identity()));
                //货盘  除掉货盘外的商品
                List<GoodsAreaLevelDetailDo> goodsAreaLevelDetailDos = goodsInfoDOMapper.getMatCodeFromLevelDetail(shopIds);
                Set<String> goodsAreaLevelDetailDoSet = goodsAreaLevelDetailDos.stream().map(goodsAreaLevelDetailDo -> goodsAreaLevelDetailDo.getShopId() + "_" + goodsAreaLevelDetailDo.getMatCode()).collect(Collectors.toSet());
                List<NewIssueInStockDo> newIssueInStockDos = newIssueExtraDataMapper.getIssueInStockListSandBox(2, shopIds);
                for (NewIssueInStockDo newIssueInStockDo : newIssueInStockDos) {

                    String key = newIssueInStockDo.getShopIdMatCodeKey();
                    // 不在货盘
                    if (!goodsAreaLevelDetailDoSet.contains(key)) {
                        continue;
                    }
//                    //  淘汰品
//                    if (newIssueInStockDo.getIsEliminate() == 1) {
//                        continue;
//                    }
                    if (newIssueInStockDo.getAvgSaleQty() == null) {
                        newIssueInStockDo.setAvgSaleQty(ZERO);
                    } else if (issueSalePredictMap != null) {
                        //销售预测map, 为null时代表不计算销售预测
                        BigDecimal avgSalePredict = issueSalePredictMap.get(newIssueInStockDo.getShopId()) == null ? ONE : issueSalePredictMap.get(newIssueInStockDo.getShopId());
                        newIssueInStockDo.setAvgSaleQty(newIssueInStockDo.getAvgSaleQty().multiply(avgSalePredict));
                    }
                    GoodsInfoDO goodsInfo = goodsInfoDoMap.get(newIssueInStockDo.getMatCode());
                    if (goodsInfo != null) {
                        //有儿童的店铺 大类名更改为家居
                        ShopInfoData shopInfoData;
                        if ((shopInfoData = shopInfoDataMap.get(newIssueInStockDo.getShopId())) != null &&
                                shopInfoData.getHaveChild() != null && shopInfoData.getHaveChild() == 1 && "儿童".equals(goodsInfo.getCategoryName())) {
                            newIssueInStockDo.setCategoryName("家居");
                        } else {
                            newIssueInStockDo.setCategoryName(goodsInfo.getCategoryName());
                        }
                        newIssueInStockDo.setMidCategoryName(goodsInfo.getMidCategoryName());
                        newIssueInStockDo.setSmallCategoryName(goodsInfo.getSmallCategoryName());
                    }

//                    if (newIssueInStockDo.getStockQty() == null || newIssueInStockDo.getStockQty().compareTo(ZERO) < 0) {
//                        newIssueInStockDo.setStockQty(ZERO);
//                    }
//                    BigDecimal totalStockQty = newIssueInStockDo.getStockQty();
//                    if(newIssueInStockDo.getPathStockQty() != null) {
//                        totalStockQty = totalStockQty.add(newIssueInStockDo.getPathStockQty());
//                    }
//                    if(newIssueInStockDo.getMoveQty() != null) {
//                        totalStockQty = totalStockQty.add(newIssueInStockDo.getMoveQty());
//                    }
                    if (newIssueInStockDo.getTotalStockQty() == null || newIssueInStockDo.getTotalStockQty().compareTo(ZERO) < 0) {
                        newIssueInStockDo.setTotalStockQty(ZERO);
                    }
                    Map<String, ProhibitedGoods> matCodeMap;ProhibitedGoods prohibitedGoodsDo;
                    if ((matCodeMap = prohibitedGoods.get(newIssueInStockDo.getShopId())) != null && (prohibitedGoodsDo = matCodeMap.get(newIssueInStockDo.getMatCode())) != null) {
                        newIssueInStockDo.setRuleName(prohibitedGoodsDo.getRuleName());
                        //若有保底数量时, 保存保底策略数量
                        if (prohibitedGoodsDo.getMinQty() != null) {
                            newIssueInStockDo.setSecurityQty(prohibitedGoodsDo.getMinQty());
                        } else {
                            //保留禁配数量0与禁配标志
                            newIssueInStockDo.setSecurityQty(0);
                            newIssueInStockDo.setIsProhibited(1);
                        }
                    }
                    //插入issueInStock
                    String shopIdKey = newIssueInStockDo.getShopId();
                    Map<String, NewIssueInStockDo> issueInStockMap = issueInStockShopIdMap.computeIfAbsent(shopIdKey, k -> Maps.newHashMap());
                    issueInStockMap.put(newIssueInStockDo.getMatCodeSizeNameKey(), newIssueInStockDo);
                }
            } else {
                //更新in
                //2. 上个日期的发货数量 Map<Date, Map<shopID, List<NewIssueDetailDo>>>
                Calendar calYes = (Calendar) calcDate.clone();
                calYes.add(Calendar.DATE, -1);//昨天
                //Map<shopId, List<NewIssueDetailDo>>
                Map<String, List<NewIssueDetailDo>> issueMap;

                List<DwsDimGoodsDO> dwsDimGoodsDos = dwsDimGoodsDOMapper.getList();
                Map<String, BigDecimal> dimGoodsPriceMap = dwsDimGoodsDos.stream().collect(Collectors.toMap(DwsDimGoodsDO::getMatCode,
                        dwsDimGoodsDO -> {
                            if (dwsDimGoodsDO.getQuotePrice() == null) {
                                return ZERO;
                            }
                            return dwsDimGoodsDO.getQuotePrice();
                        }));
                List<DwsDimShopDO> dwsDimShopDOList =  shopListCache.getShopList();
                Map<String, DwsDimShopDO> shopMap = dwsDimShopDOList.stream().collect(Collectors.toMap(DwsDimShopDO::getShopId, Function.identity()));

                List<IssueSandboxShopStockDo> batchList = new ArrayList<>();
                for (String shopId : shopIds) {
                    //获取上个日期发货的店铺   Map<matCode_sizeId, qty>
                    issueMap = issueDetailDateMap.get(calYes.getTime());
                    Map<String, Long> issueDetailMap = null;
                    if  (issueArriveMappingMap.get(calYes.getTime()) != null && issueArriveMappingMap.get(calYes.getTime()).containsKey(shopId)
                            && issueMap.get(shopId) != null) {
//                        issueDetailMap = issueMap.get(shopId).stream().collect(Collectors.toMap(NewIssueDetailDo::getMatCodeSizeNameKey, NewIssueDetailDo::getQty, (v1, v2) -> v1));
                        issueDetailMap = issueMap.get(shopId).stream().collect(Collectors.groupingBy(NewIssueDetailDo::getMatCodeSizeNameKey, Collectors.summingLong(NewIssueDetailDo::getQtyLongVal)));
                    }
                    //门店上期末库存 = 期初值 - 门店有效日均销
                    //门店期初库存 = 上期期末库存 + 本轮到货的发货量
                    //批量更新 在店库存减去日均销再加上本轮到货的发货数量
                    BigDecimal subAvgQty;
                    //期末总库存值：∑（单品期初库存 - 单品有效日均销 + 单品补货量）
                    //期末库存货值：∑（单品期末库存值*吊牌价）
                    BigDecimal totalStockQty = ZERO, totalStockValue  = ZERO;
                    Map<String, NewIssueInStockDo> issueInStockMap = issueInStockShopIdMap.get(shopId);
                    List<IssueSandboxRollingStockDO> rollingStocks = new ArrayList<>();
                    if (issueInStockMap != null) {
                        for (NewIssueInStockDo newIssueInStockDo : issueInStockMap.values()) {
                            IssueSandboxRollingStockDO rollingStock = new IssueSandboxRollingStockDO();
                            rollingStock.setTaskId(issueSandBoxTask.getTaskId());
                            rollingStock.setIssueDate(calcDate.getTime());
                            rollingStock.setShopCode(newIssueInStockDo.getShopId());
                            rollingStock.setMatCode(newIssueInStockDo.getMatCode());
                            rollingStock.setSizeName(newIssueInStockDo.getSizeName());
                            rollingStock.setAvgSaleQty(newIssueInStockDo.getAvgSaleQty());
                            rollingStock.setTotalStockQty(newIssueInStockDo.getTotalStockQty());
                            rollingStock.setArriveStockQty(ZERO);
                            //期初值 - 门店有效日均销
                            //门店总库存为0 或 减去日均销小于0  则置为0
                            if ((subAvgQty = newIssueInStockDo.getTotalStockQty()).compareTo(ZERO) == 0 ||
                                    (subAvgQty = subAvgQty.subtract(newIssueInStockDo.getAvgSaleQty()).setScale( 1, ROUND_HALF_UP)).compareTo(ZERO ) < 0) {
                                subAvgQty = ZERO;
                            }
                            Long arriveNum;
                            // + 本轮到货的发货量
                            if (issueDetailMap != null && (arriveNum = issueDetailMap.get(newIssueInStockDo.getMatCodeSizeNameKey())) != null) {
                                rollingStock.setArriveStockQty(new BigDecimal(arriveNum));
                                subAvgQty = subAvgQty.add(rollingStock.getArriveStockQty());
                            }
                            rollingStocks.add(rollingStock);
                            //最终数量有变化 更新数据库
                            newIssueInStockDo.setTotalStockQty(subAvgQty);
                            totalStockQty = totalStockQty.add(subAvgQty);
                            BigDecimal quotePrice = dimGoodsPriceMap.get(newIssueInStockDo.getMatCode());
                            if (quotePrice != null) {
                                totalStockValue = totalStockValue.add(subAvgQty.multiply(quotePrice));
                            }
                        }
                    }
//                    issueSandboxRollingStockMapper.addIssueSandboxRollingStock(rollingStocks);
                    DwsDimShopDO dwsDimShopDO = shopMap.get(shopId);
                    if (dwsDimShopDO == null) {
                        continue;
                    }
                    IssueSandboxShopStockDo issueSandboxShopStockDo = new IssueSandboxShopStockDo();
                    issueSandboxShopStockDo.setTaskId(issueSandBoxTask.getTaskId());
                    issueSandboxShopStockDo.setShopId(shopId);
                    issueSandboxShopStockDo.setShopCode(dwsDimShopDO.getShopCode());
                    issueSandboxShopStockDo.setShopName(dwsDimShopDO.getShopName());
                    issueSandboxShopStockDo.setSandboxDate(calcDate.getTime());
                    issueSandboxShopStockDo.setStock(totalStockQty);
                    issueSandboxShopStockDo.setStockValue(totalStockValue);
                    batchList.add(issueSandboxShopStockDo);
                }

                newIssueDOMapper.addIssueSandboxShopStock(batchList);

            }
            return "1";
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueInStockSandBox] catch exception");
            return "1";
        }
    }

    public String writeInStockFile(List<NewIssueInStockDo> newIssueInStockDos, Set<String> notSaleTimeGoodsRange, Map<String, GoodsInfoDO> goodsInfoDoMap, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods, int index) {
        String fileNameSuffix = "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "_" + index + ".txt";
        String fullFileName = "";
        BufferedWriter bw;
        try {
            File file = new File(loadFilePath + "inStock" + fileNameSuffix);
            if(!file.exists()){
                file.createNewFile();
            }
            fullFileName = file.getAbsoluteFile().getPath();
//            FileOutputStream fos = new FileOutputStream(file);
//            FileChannel outChannel = fos.getChannel();
//            ByteBuffer buffer = ByteBuffer.allocate(1024);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            int dosSize = newIssueInStockDos.size();
            for (int i = 0; i < dosSize; i++) {
                NewIssueInStockDo newIssueInStockDo = newIssueInStockDos.get(i);
                //设置大中小类名
                GoodsInfoDO goodsInfo = goodsInfoDoMap.get(newIssueInStockDo.getMatCode());
                if (goodsInfo != null) {
                    newIssueInStockDo.setCategoryName(goodsInfo.getCategoryName());
                    newIssueInStockDo.setMidCategoryName(goodsInfo.getMidCategoryName());
                    newIssueInStockDo.setSmallCategoryName(goodsInfo.getSmallCategoryName());
                } else {
                    //异常数据暂时不保存至文件，原因待查 by zdw 2020-06-18
                    logger.warn(String.format("[writeInStockFile] ShopId = %s, MatCode = %s, SizeName = %s", newIssueInStockDo.getShopId(),newIssueInStockDo.getMatCode(), newIssueInStockDo.getSizeName()));
                    continue;
                }

                if (newIssueInStockDo.getStockQty() == null) {
                    newIssueInStockDo.setStockQty(ZERO);
                }
                BigDecimal totalStockQty = newIssueInStockDo.getStockQty();
                if(newIssueInStockDo.getPathStockQty() != null) {
                    totalStockQty = totalStockQty.add(newIssueInStockDo.getPathStockQty());
                }
                if(newIssueInStockDo.getMoveQty() != null) {
                    totalStockQty = totalStockQty.add(newIssueInStockDo.getMoveQty());
                }
                //MAX(0,在店+在途+在配)
                newIssueInStockDo.setTotalStockQty(totalStockQty.compareTo(ZERO) == -1 ? ZERO : totalStockQty);

                Map<String, ProhibitedGoods> matCodeMap = prohibitedGoods.get(newIssueInStockDo.getShopId());
                if (matCodeMap != null) {
                    // 分别通过matCode和matCode_sizeName两种格式作为key去取数据
                    ProhibitedGoods prohibitedGoodsDo = matCodeMap.get(newIssueInStockDo.getMatCode());
                    if (prohibitedGoodsDo == null) {
                        prohibitedGoodsDo = matCodeMap.get(newIssueInStockDo.getMatCodeSizeNameKey());
                    }
                    if (prohibitedGoodsDo != null) {
                        // 不等于空则说明设置了禁配
                        if (prohibitedGoodsDo.getMinQty() != null) {
                            newIssueInStockDo.setSecurityQty(prohibitedGoodsDo.getMinQty());
                            // TODO 有保底的，为什么IsProhibited不设置为1？
                        } else {
                            //保留禁配数量0与禁配标志
                            newIssueInStockDo.setSecurityQty(0);
                            newIssueInStockDo.setIsProhibited(1);
                        }
                        newIssueInStockDo.setRuleName(prohibitedGoodsDo.getRuleName());
                    }
                }

                //未到上市日期的商品设置日均销为0
                // TODO 为空，不执行
                if (notSaleTimeGoodsRange.contains(newIssueInStockDo.getShopIdMatCodeSizeNameKey())) {
                    newIssueInStockDo.setAvgSaleQty(new BigDecimal(0));
                }

                bw.write(newIssueInStockDo.getLoadString() + (i >= dosSize - 1 ? "" : "\r\n"));
            }

            bw.close();
            LoggerUtil.info(logger, "[issueInStockWriteFile] finish, fullFileName:{0}", fullFileName);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueInStockWriteFile] catch exception");
            logger.error("[issueInStockWriteFile] catch exception", e);
        }

        return fullFileName;
    }

    public void unionFileAndLoadDataInStock(List<String> fileNameList, String tableName) throws IOException {
        //定义输出目录
        String fileNameSuffix = "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".txt";
        String fullFileName = (loadFilePath + "totalInStock" + fileNameSuffix);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fullFileName)), StandardCharsets.UTF_8));

        //合并文件
        for(String fileName : fileNameList) {
            File file = new File(fileName);
            if(file.isFile()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                String line;
                while((line=br.readLine())!=null) {
                    bw.write(line);
                    bw.newLine();
                }
                br.close();
            }
        }
        bw.close();

        int ret = newIssueDOMapper.loadFileInStock(tableName, fullFileName);
        LoggerUtil.info(logger, "[issueInStockLoadFile] finish, fullFileName:{0}, ret:{1}", fullFileName, ret);
    }

    public Integer getDataStockStatus() {
        return newIssueExtraDataMapper.getDataStockStatus();
    }

    public void issueInNewSkcStock(IssueTaskDO task, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods, Set<String> shopIds) {

        //1. update 新品标志



//        String tableName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, task.getId(),task.getRunTime());
//
//        List<NewIssueInStockDo> newSkcList = newIssueExtraDataMapper.getNewSkcList(shopIds);
//        List<MidCategoryStockDO> midCategorySaleAmtList = newIssueDOMapper.getMidCategorySale(tableName, shopIds);
//        LoggerUtil.info(logger, "新品查询结果为：{0},{1}", newSkcList.size(), midCategorySaleAmtList.size());
//
////        List<GoodsInfoDO> goodsInfoDOS = goodsInfoDOMapper.goodsInfoData();
//        List<GoodsInfoDO> goodsInfoDOS = goodsInfoCache.getGoodsInfo();
//        Map<String, GoodsInfoDO> goodsMap = goodsInfoDOS.stream().collect(Collectors.toMap(GoodsInfoDO::getMatCode, Function.identity()));
//
//        List<Map<String,Object>>  shopAvgList = newIssueDOMapper.getShopAvg(tableName, shopIds);
//        Map<String, BigDecimal> shopAvgMap = new HashMap<>(shopAvgList.size());
//        for (Map<String,Object> map : shopAvgList) {
//            shopAvgMap.put(map.get("shopId").toString(), new BigDecimal(map.get("avgSaleQty").toString()));
//        }
//        Map<String, Double> midMap = midCategorySaleAmtList.stream().collect(Collectors.toMap(
//                midCategoryStockDO -> midCategoryStockDO.getShopID() + "-" + midCategoryStockDO.getCategoryName() + "-" + midCategoryStockDO.getMidCategoryName(), MidCategoryStockDO::getAvgSaleQty));
//
//        for (NewIssueInStockDo temp : newSkcList) {
//            GoodsInfoDO goods = goodsMap.get(temp.getMatCode());
//            if (goods != null) {
//                temp.setMidCategoryName(goods.getMidCategoryName());
//                temp.setCategoryName(goods.getCategoryName());
//                temp.setSmallCategoryName(goods.getSmallCategoryName());
//            }
//            temp.setIsNew(1);
//            String key = temp.getShopId() + "-" + temp.getCategoryName() + "-" + temp.getMidCategoryName();
//            if (midMap.get(key) != null) {
//                temp.setAvgSaleQty(new BigDecimal(midMap.get(key)));
//            } else {
//                // 如果中类没有日均销数据，就按门店平均日均销来设置
//                if (shopAvgMap.get(temp.getShopId()) != null) {
//                    temp.setAvgSaleQty(shopAvgMap.get(temp.getShopId()));
//                } else {
//                    temp.setAvgSaleQty(new BigDecimal(0.5d));
//                }
//            }
//            Map<String, ProhibitedGoods> matCodeMap;ProhibitedGoods prohibitedGoodsDo;
//            if ((matCodeMap = prohibitedGoods.get(temp.getShopId())) != null && (prohibitedGoodsDo = matCodeMap.get(temp.getMatCode())) != null) {
//                temp.setRuleName(prohibitedGoodsDo.getRuleName());
//                //若有保底数量时, 保存保底策略数量
//                if (prohibitedGoodsDo.getMinQty() != null) {
//                    temp.setSecurityQty(prohibitedGoodsDo.getMinQty());
//                } else {
//                    //保留禁配数量0与禁配标志
//                    temp.setSecurityQty(0);
//                    temp.setIsProhibited(1);
//                }
//            }
//            temp.setStockQty(ZERO);
//            temp.setPathStockQty(ZERO);
//            //已经查询出来了
//            temp.setMoveQty(temp.getMoveQty() == null ? ZERO : temp.getMoveQty());
//            temp.setTotalStockQty(temp.getStockQty().add(temp.getPathStockQty()).add(temp.getMoveQty()));
//        }
//
//        // 分批录入
//        AtomicInteger newSkcInThreadCount = new AtomicInteger(0);
//        List<NewIssueInStockDo> inTempList;
//        int inCount = 0;
//        int pageSize = 5000;
//        while(true) {
//            inTempList = newSkcList.stream().skip(inCount * pageSize).limit(pageSize).collect(Collectors.toList());
//            if (inTempList.size() == 0) {
//                break;
//            }
//            List<NewIssueInStockDo> finalInList = inTempList;
//            addNewSkcInStockPool.submit(() -> {
//                try {
//                    newIssueDOMapper.addIssueInStock(tableName, finalInList);
//                } catch (Exception e) {
//                    LoggerUtil.error(e, logger, "[batchUpdateIssueNeedStock] catch exception");
//                } finally {
//                    newSkcInThreadCount.getAndIncrement();
//                }
//            });
//            ++inCount;
//        }
//
//        while (newSkcInThreadCount.get() != inCount) {
//            try {
//                LoggerUtil.info(logger, "[addIssueInStockNewSkc] newSkcInThreadCount.get() != inCount, sleep 5 s," +
//                        " newSkcInThreadCount.get()={0}, inCount={2}", newSkcInThreadCount.get(), inCount);
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//            }
//        }
    }

}
