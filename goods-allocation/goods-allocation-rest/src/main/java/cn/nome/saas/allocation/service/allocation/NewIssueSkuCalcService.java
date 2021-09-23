package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.vertical.NewIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueNeedStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.repository.entity.allocation.MidCategoryStockDO;
import cn.nome.saas.allocation.utils.CommonUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.*;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * NewIssueSkuCalcService
 *
 * @author Bruce01.fan
 * @date 2019/9/6
 */
@Service
public class NewIssueSkuCalcService {

    private static Logger logger = LoggerFactory.getLogger(NewIssueSkuCalcService.class);

    @Autowired
    NewIssueWarehouseService newIssueWarehouseService;

    @Autowired
    NewIssueSkuCalcDOMapper newIssueSkuCalcDOMapper;
    @Autowired
    NewIssueDOMapper newIssueDOMapper;
    @Autowired
    NewIssueExtraDataMapper newIssueExtraDataMapper;

    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;

    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;
    @Autowired
    WhiteListSingleItemDOMapper whiteListSingleItemDOMapper;

    @Autowired
    ShopInfoCache shopInfoCache;

    private static String loadFilePath = "/tmp/cache/";
//    private static String loadFilePath = "";

    private static BigDecimal ZERO = new BigDecimal(0);

    private ExecutorService addIssueNeedStockPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("add-issue-need-stock-%d").build());

//    public void calcSKURequirement(int taskId,Set<String> shopIdList,Date runTime) {
//        calcSKURequirement(taskId, shopIdList, runTime, false);
//    }

    /**
     * needStock calc
     *
     * @param taskId
     * @param shopIdList
     * @param runTime
     * @param reCalc
     */
    public void calcSKURequirement(int taskId, Set<String> shopIdList, Date runTime, boolean reCalc, Map<String, IssueReserveDetailDo> reserveDetailDoMap) {
        try {
            List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.shopInfoData();

            //如果为空时, 默认所有店铺id
            if (shopIdList == null) {
                shopIdList = shopInfoDataList.stream().map(ShopInfoData::getShopID).collect(Collectors.toSet());
            }
            List<GoodsAreaLevelDetailDo> goodsAreaLevelDetailDos = goodsInfoDOMapper.getMatCodeFromLevelDetail(shopIdList);
            Set<String> goodsAreaLevelDetailDoSet = goodsAreaLevelDetailDos.stream().map(GoodsAreaLevelDetailDo -> GoodsAreaLevelDetailDo.getShopId() + "_" + GoodsAreaLevelDetailDo.getMatCode()).collect(Collectors.toSet());

            String midCategoryQtyTableName = CommonUtil.getTaskTableName(ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX, taskId, runTime);
            String skuRequirementTableName = CommonUtil.getTaskTableName(ISSUE_NEED_STOCK_TABLE_PREFIX, taskId, runTime);

            //全部重算
            if (!reCalc) {
                // 创建新表
                if (newIssueSkuCalcDOMapper.checkTableExists(midCategoryQtyTableName) == 0) {
                    newIssueSkuCalcDOMapper.createMidCategoryTable(midCategoryQtyTableName);
                } else {
                    newIssueDOMapper.truncateTable(midCategoryQtyTableName);
                }

                // 创建新表
                if (newIssueSkuCalcDOMapper.checkTableExists(skuRequirementTableName) == 0) {
                    newIssueSkuCalcDOMapper.createIssueNeedStockTable(skuRequirementTableName);
                } else {
                    newIssueDOMapper.truncateTable(skuRequirementTableName);
                }
            } else {//单店重算
                String bakMidCategoryQtyTableName = CommonUtil.getTaskTableName(BAK_ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX, taskId, runTime);
                // 创建新表
                if (newIssueSkuCalcDOMapper.checkTableExists(bakMidCategoryQtyTableName) == 0) {
                    newIssueSkuCalcDOMapper.createMidCategoryTable(bakMidCategoryQtyTableName);
                }
                newIssueSkuCalcDOMapper.bakMidCategoryQty(bakMidCategoryQtyTableName, midCategoryQtyTableName, shopIdList);
                newIssueSkuCalcDOMapper.delMidCategoryQtyByShopId(midCategoryQtyTableName, shopIdList);

                String bakNeedStockTabName = CommonUtil.getTaskTableName(BAK_ISSUE_NEED_STOCK_TABLE_PREFIX, taskId, runTime);
                // 创建新表
                if (newIssueSkuCalcDOMapper.checkTableExists(bakNeedStockTabName) == 0) {
                    newIssueSkuCalcDOMapper.createIssueNeedStockTable(bakNeedStockTabName);
                }
                newIssueSkuCalcDOMapper.bakNeedStock(bakNeedStockTabName, skuRequirementTableName, shopIdList);
                newIssueSkuCalcDOMapper.delNeedStockByShopId(skuRequirementTableName, shopIdList);
            }

            List<String> fileNameList = new ArrayList<>();

            if (Constant.DEBUG_FLAG_LOW_MEMORY) {
                int needCount = 0;
                int pageSize = 50;
                while (true) {
                    Set<String> tempShopIds = shopIdList.stream().skip(needCount * pageSize).limit(pageSize).collect(Collectors.toSet());
                    if (tempShopIds.size() == 0) {
                        break;
                    }

                    logger.info(String.format("[calcSKURequirement[low_memory]] needCount = %d, tempShopIds = %s", needCount, JSON.toJSON(tempShopIds)));

                    try {
                        String fileName = calcMiddleCategoryQty(taskId, tempShopIds, shopInfoDataList,
                            goodsAreaLevelDetailDoSet, runTime, reserveDetailDoMap);
                        fileNameList.add(fileName);
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[calcMiddleCategoryQty] catch exception " + e.getMessage());
                    }
                    ++needCount;
                }

            } else {
                List<Future> futureList = new ArrayList<>();
                //分批跑needStock, 50一组
                //            Set<String> tempShopIds;
                int needCount = 0;
                int pageSize = 70;
                while (true) {
                    Set<String> tempShopIds = shopIdList.stream().skip(needCount * pageSize).limit(pageSize).collect(Collectors.toSet());
                    if (tempShopIds.size() == 0) {
                        break;
                    }

                    logger.info(String.format("[calcSKURequirement] needCount = %d, tempShopIds = %s", needCount, JSON.toJSON(tempShopIds)));

                    Future<String> future = addIssueNeedStockPool.submit(() -> {
                        try {
                            return calcMiddleCategoryQty(taskId, tempShopIds, shopInfoDataList,
                                goodsAreaLevelDetailDoSet, runTime, reserveDetailDoMap);
                        } catch (Exception e) {
                            LoggerUtil.error(e, logger, "[calcMiddleCategoryQty] catch exception " + e.getMessage());
                            return "";
                        }
                    });
                    futureList.add(future);

                    ++needCount;
                }

                for (Future future : futureList) {
                    // TODO fix bug
                    try {
                        fileNameList.add(future.get().toString());
                    } catch (Exception e) {
                        logger.error("[issueNeedStock] fileName is null", e);
                    }
                }
            }

            // TODO 以下为单线程跑法，在测试环境用


            unionFileAndLoadDataNeedStock(fileNameList, skuRequirementTableName);

            LoggerUtil.info(logger, "[issueNeedStock] issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueNeedStock] catch exception");
            logger.error("[issueNeedStock] catch exception", e);
        }
    }

    /**
     * calcSKURequirementSandBox
     *
     * @param shopIdList            shopIdList
     * @param issueInStockShopIdMap issueInStockShopIdMap
     * @param issueNeedShopIdMap    issueNeedShopIdMap
     */
    void calcSKURequirementSandBox(IssueSandBoxTask issueSandBoxTask, Set<String> shopIdList,
                                   Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap,
                                   Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap) {
        try {
            //每次循环先清空上轮数据   Map<shopId, <matCode_sizeId, IssueNeedStockDO>>
            issueNeedShopIdMap.clear();
            List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.shopInfoData();

            //如果为空时, 默认所有店铺id
            if (shopIdList == null) {
                shopIdList = shopInfoDataList.stream().map(ShopInfoData::getShopID).collect(Collectors.toSet());
            }

            List<Future> futureList = new ArrayList<>();
            //分批跑needStock, 50一组
//            Set<String> tempShopIds;
            int needCount = 0;
            int pageSize = 20;
            while (true) {
                Set<String> tempShopIds = shopIdList.stream().skip(needCount * pageSize).limit(pageSize).collect(Collectors.toSet());
                if (tempShopIds.size() == 0) {
                    break;
                }

                Future<String> future = addIssueNeedStockPool.submit(() -> {
                    return calcMiddleCategoryQtySandBox(issueSandBoxTask, tempShopIds, shopInfoDataList, issueInStockShopIdMap, issueNeedShopIdMap);
                });
                futureList.add(future);

                ++needCount;
            }

            List<String> fileNameList = new ArrayList<>();
            for (Future future : futureList) {
                fileNameList.add(future.get().toString());
            }

            //沙盘记录日志
            for (String shopId : issueNeedShopIdMap.keySet()) {
                if ("NM000006".equals(shopId)) {
                    for (IssueNeedStockDO nn : issueNeedShopIdMap.get(shopId).values()) {
                        if ("ACD2B00516Y1".equals(nn.getMatCode())) {
                            LoggerUtil.info(logger, "calcData:IssueNeedStockDO:" + nn);
                        }
                    }
                }
            }

            LoggerUtil.info(logger, "[issueNeedStockSandBox] issue calculate task over!");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueNeedStockSandBox] catch exception");
        }
    }

    public void unionFileAndLoadDataNeedStock(List<String> fileNameList, String skuRequirementTableName) throws IOException {
        //定义输出目录
        String fileNameSuffix = "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".txt";
        String fullFileName = (loadFilePath + "totalNeedStock" + fileNameSuffix);
        BufferedWriter bw = new BufferedWriter(new FileWriter(fullFileName));

        int fileCount = 0;
        int folderConut = 0;
        for (String fileName : fileNameList) {
            if (StringUtils.isEmpty(fileName)) {
                continue;
            }
            File file = new File(fileName);
            if (file.isFile()) {
                fileCount++;
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                }
                br.close();
            } else {
                folderConut++;
            }
        }
        bw.close();

        int ret = newIssueSkuCalcDOMapper.loadFileNeedStock(skuRequirementTableName, fullFileName);
        LoggerUtil.info(logger, "[issueNeedStockWriteFile] finish, fullFileName:{0}, ret:{1}", fullFileName, ret);
    }

    /**
     * 计算中类数
     */
    public String calcMiddleCategoryQty(int taskId, Set<String> shopIdList, List<ShopInfoData> shopInfoDataList,
                                        Set<String> goodsAreaLevelDetailDoSet, Date runTime, Map<String, IssueReserveDetailDo> reserveDetailDoMap) {

        // shopIdList.forEach(id -> logger.warn(String.format("[calcMiddleCategoryQty] calc shop, ShopID = %s", id)));

        String inTabName = CommonUtil.getTaskTableName(ISSUE_IN_STOCK_TABLE_PREFIX, taskId, runTime);
        String midCategoryQtyTableName = CommonUtil.getTaskTableName(ISSUE_MID_CATAGORY_QTY_TABLE_PREFIX, taskId, runTime);

        // 门店对应仓位>0的所在中类商品 && 由保底生成的白名单内的商品
        // TODO 陈列相关，取商品
        Set<String> issueGoodsSet = newIssueDOMapper.getIssueGoodsList(new HashSet<>(shopIdList));
        LoggerUtil.info(logger, "[issueNeedStock] issue issueGoodsSet size:{0}", issueGoodsSet.size());
        // 由保底生成的白名单内的商品
        Set<String> issueWhiteListSet = whiteListSingleItemDOMapper.getIssueWhiteList(shopIdList);
        LoggerUtil.info(logger, "[issueNeedStock] issue issueWhiteListSet size:{0}", issueWhiteListSet.size());
        Set<String> issueGoodsUnionSet = Sets.union(issueGoodsSet, issueWhiteListSet);

        List<MidCategoryStockDO> midCategoryStockList = new ArrayList<>();

        // TODO categoryName有特殊处理，参照Mapper
        List<NewIssueInStockDo> newIssueInStockDoList = newIssueDOMapper.getIssueInStockList(inTabName, 1, shopIdList);

        List<NewIssueInStockDo> issueInStockDoList = new ArrayList<>();
        for (NewIssueInStockDo newIssueInStockDo : newIssueInStockDoList) {
            String key = newIssueInStockDo.getShopIdMatCodeKey();

            // 门店对应仓位>0的所在中类商品 && 由保底生成的白名单内的商品
            if (!issueGoodsUnionSet.contains(key)) {
                logger.warn(String.format("[calcMiddleCategoryQty] 商品不存在, key = %s", key));
                continue;
            }

            // 不在货盘
            if (!goodsAreaLevelDetailDoSet.contains(key)) {
                logger.warn(String.format("[calcMiddleCategoryQty] 不在货盘, key = %s", key));
                continue;
            }

            //  淘汰品
            if (newIssueInStockDo.getIsEliminate() == 1) {
                logger.warn(String.format("[calcMiddleCategoryQty] 淘汰品, key = %s", key));
                continue;
            }

            //  仓库无该商品（淘汰品需求）
            /*
            if (outStockQtyMap.get(newIssueInStockDo.getMatCode() + "_" + newIssueInStockDo.getSizeId()) == null ) {
                continue;
            }
            */

            issueInStockDoList.add(newIssueInStockDo);
        }

        /**
         * 分组
         * {
         *     "shopId_categoryName_midCategoryName": [
         *         {NewIssueInStockDo1},
         *         {NewIssueInStockDo2},
         *         {NewIssueInStockDo3}
         *     ]
         * }
         */
        Map<String, List<NewIssueInStockDo>> midSaleMap = issueInStockDoList.stream()
                .collect(Collectors.groupingBy(NewIssueInStockDo::getShopIdBigMidCategoryKey));

        for (Map.Entry<String, List<NewIssueInStockDo>> entry: midSaleMap.entrySet()) {

            String key = entry.getKey();
            List<NewIssueInStockDo> inStockDos = entry.getValue();

            // LoggerUtil.info(logger, "[issueNeedStock] issue midSaleMap key:{0}", key);
            // shopId_categoryName_midCategoryName 下的所有商品的总日均销之和
            // 中类日均销
            BigDecimal total = ZERO;
            for (NewIssueInStockDo newIssueInStockDo : inStockDos) {
                if (newIssueInStockDo.getAvgSaleQty() != null) {
                    total = total.add(newIssueInStockDo.getAvgSaleQty());
                }
            }

            String[] keys = key.split("_");

            // 如果长度不为3，则为脏数据，原因待查 by zdw 2020-06-18
            // 原因待查 by luke 2021-06-08
            if (keys.length != 3) {
                logger.warn(String.format("[issueNeedStock] midSaleMap invalid key = %s", key));
                continue;
            }

            MidCategoryStockDO midCategoryStockDO = new MidCategoryStockDO();
            midCategoryStockDO.setShopID(keys[0]);
            midCategoryStockDO.setCategoryName(keys[1]);
            midCategoryStockDO.setMidCategoryName(keys[2]);
            midCategoryStockDO.setAvgSaleQty(total.doubleValue());
            midCategoryStockList.add(midCategoryStockDO);
        }

        List<MidCategoryStockDO> categoryDisplayList = newIssueSkuCalcDOMapper.getCategoryDisplayData(shopIdList);

        matchMidCategory(midCategoryStockList, categoryDisplayList);

        if (CollectionUtils.isEmpty(midCategoryStockList) || CollectionUtils.isEmpty(categoryDisplayList)) {
            logger.error(String.format("[calcMiddleCategoryQty] 数量为空, count(midCategoryStockList) = %d, count(categoryDisplayList) = %d", midCategoryStockList.size(), categoryDisplayList.size()));
            return null;
        }

        // 插入中类数量
        newIssueSkuCalcDOMapper.insertMidCategoryQty(midCategoryQtyTableName, midCategoryStockList);

        List<IssueNeedStockDO> list = getNeedStockList(null, issueInStockDoList, goodsAreaLevelDetailDoSet, midCategoryStockList, shopIdList, shopInfoDataList, reserveDetailDoMap);

        return writeNeedStockFile(list);
    }

    /**
     * 计算中类数
     *
     * @param shopIdList            shopIdList
     * @param shopInfoDataList      shopInfoDataList
     * @param issueInStockShopIdMap issueInStockShopIdMap
     * @param issueNeedShopIdMap    issueNeedShopIdMap
     * @return 1
     */
    public String calcMiddleCategoryQtySandBox(IssueSandBoxTask issueSandBoxTask, Set<String> shopIdList, List<ShopInfoData> shopInfoDataList,
                                               Map<String, Map<String, NewIssueInStockDo>> issueInStockShopIdMap,
                                               Map<String, Map<String, IssueNeedStockDO>> issueNeedShopIdMap) {

        try {
            // 门店对应仓位>0的所在中类商品 && 由保底生成的白名单内的商品
            Set<String> issueGoodsSet = newIssueDOMapper.getIssueGoodsList(new HashSet<>(shopIdList));
            LoggerUtil.info(logger, "[issueNeedStockSandBox] issue issueGoodsSet size:{0}", issueGoodsSet.size());
            // 由保底生成的白名单内的商品
            Set<String> issueWhiteListSet = whiteListSingleItemDOMapper.getIssueWhiteList(shopIdList);
            LoggerUtil.info(logger, "[issueNeedStockSandBox] issue issueWhiteListSet size:{0}", issueWhiteListSet.size());
            Set<String> issueGoodsUnionSet = Sets.union(issueGoodsSet, issueWhiteListSet);

            List<MidCategoryStockDO> midCategoryStockList = new ArrayList<>();

            //获取issueInStock
            List<NewIssueInStockDo> newIssueInStockDoList = new ArrayList<>();
            for (String shopId : shopIdList) {
                if (issueInStockShopIdMap.get(shopId) != null) {
                    newIssueInStockDoList.addAll(issueInStockShopIdMap.get(shopId).values());
                }
            }

            List<NewIssueInStockDo> issueInStockDoList = new ArrayList<>();
            for (NewIssueInStockDo newIssueInStockDo : newIssueInStockDoList) {
                String key = newIssueInStockDo.getShopIdMatCodeKey();
                // 门店对应仓位>0的所在中类商品 or 由保底生成的白名单内的商品
                if (!issueGoodsUnionSet.contains(key)) {
                    continue;
                }
                if (new Integer(1).equals(newIssueInStockDo.getIsProhibited())) {
                    continue;
                }
                issueInStockDoList.add(newIssueInStockDo);
            }

            // 分组
            Map<String, List<NewIssueInStockDo>> midSaleMap = issueInStockDoList.stream()
                    .collect(Collectors.groupingBy(NewIssueInStockDo::getShopIdBigMidCategoryKey));

            for (String key : midSaleMap.keySet()) {
                //                LoggerUtil.info(logger, "[issueNeedStock] issue midSaleMap key:{0}", key);
                BigDecimal total = ZERO;
                for (NewIssueInStockDo newIssueInStockDo : midSaleMap.get(key)) {
                    if (newIssueInStockDo.getAvgSaleQty() != null) {
                        total = total.add(newIssueInStockDo.getAvgSaleQty());
                    }
                }

                String[] keys = key.split("_");
                MidCategoryStockDO midCategoryStockDO = new MidCategoryStockDO();
                midCategoryStockDO.setShopID(keys[0]);
                midCategoryStockDO.setCategoryName(keys[1]);
                midCategoryStockDO.setMidCategoryName(keys[2]);
                midCategoryStockDO.setAvgSaleQty(total.doubleValue());
                midCategoryStockList.add(midCategoryStockDO);
            }

            List<MidCategoryStockDO> categoryDisplayList = newIssueSkuCalcDOMapper.getCategoryDisplayData(shopIdList);

            matchMidCategory(midCategoryStockList, categoryDisplayList);

            if (CollectionUtils.isEmpty(midCategoryStockList) || CollectionUtils.isEmpty(categoryDisplayList)) {
                return "";
            }

            if (CollectionUtils.isEmpty(midCategoryStockList)) {
                return "";
            }

            List<IssueNeedStockDO> list = getNeedStockList(issueSandBoxTask.getCalcType(), newIssueInStockDoList, null, midCategoryStockList, shopIdList, shopInfoDataList, null);

            //重新更新issueNeedShopIdMap
            //Map<shopId, <matCode_sizeId, IssueNeedStockDO>>
            for (IssueNeedStockDO issueNeedStockDO : list) {
                Map<String, IssueNeedStockDO> issueNeedSkuMap = issueNeedShopIdMap.computeIfAbsent(issueNeedStockDO.getShopId(), k -> Maps.newHashMap());
                issueNeedSkuMap.put(issueNeedStockDO.getMatCodeSizeNameKey(), issueNeedStockDO);
                issueNeedShopIdMap.put(issueNeedStockDO.getShopId(), issueNeedSkuMap);
            }
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[calcMiddleCategoryQtySandBox] catch exception");
        }
        return "1";
    }

    public String writeNeedStockFile(List<IssueNeedStockDO> list) {
        String fileNameSuffix = "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".txt";
        String fullFileName = "";
        try {
            File file = new File(loadFilePath + "needStock" + fileNameSuffix);
            if (!file.exists()) {
                file.createNewFile();
            }
            fullFileName = file.getAbsoluteFile().getPath();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);

            int dosSize = list.size();
            for (int i = 0; i < dosSize; i++) {
                bw.write(list.get(i).getLoadString() + (i >= dosSize - 1 ? "" : "\r\n"));
            }

            bw.close();
            LoggerUtil.info(logger, "[issueNeedStockWriteFile] finish, fullFileName:{0}", fullFileName);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[issueNeedStockWriteFile] catch exception");
        }
        return fullFileName;
    }

    /**
     * getNeedStockList
     *
     * @param calcType                      跑配发任务是传null，除了沙盒，其他都为null
     * @param newIssueInStockDoList         从数仓拉取的、并做过初步筛选的、需要配补的商品列表
     * @param goodsAreaLevelDetailDoSet     货盘
     * @param midCategoryStockList          中类库存列表（上一步已经计算好了所需量）
     * @param shopIdList                    店铺id列表
     * @param shopInfoDataList              店铺id对应的详细店铺信息
     * @param reserveDetailDoMap            该值永远为null
     * @return
     */
    private List<IssueNeedStockDO> getNeedStockList(Integer calcType,
                                                    List<NewIssueInStockDo> newIssueInStockDoList,
                                                    Set<String> goodsAreaLevelDetailDoSet,
                                                    List<MidCategoryStockDO> midCategoryStockList,
                                                    Set<String> shopIdList,
                                                    List<ShopInfoData> shopInfoDataList,
                                                    Map<String, IssueReserveDetailDo> reserveDetailDoMap) {

        // 中类数
        Map<String, MidCategoryStockDO> newMidCategoryStockMap = midCategoryStockList.stream()
                .filter(midCategoryStockDO -> midCategoryStockDO.getAvgSaleQty() > 0 && shopIdList.contains(midCategoryStockDO.getShopID()))
                .collect(Collectors.toMap(MidCategoryStockDO::getShopIdCategoryMidCategoryKey, Function.identity()));

        List<IssueNeedStockDO> list = new ArrayList<>(newIssueInStockDoList.size());
        for (NewIssueInStockDo newIssueInStockDo : newIssueInStockDoList) {
            if (newIssueInStockDo.getIsProhibited() != 1) {

                // 不在货盘则剔除
                if (goodsAreaLevelDetailDoSet != null
                    && !goodsAreaLevelDetailDoSet.contains(newIssueInStockDo.getShopIdMatCodeKey())) {
                    continue;
                }

                // 淘汰品则剔除
                if (newIssueInStockDo.getIsEliminate() == 1) {
                    continue;
                }

                // TODO 中包数小于1？

                IssueNeedStockDO issueNeedStockDO = new IssueNeedStockDO();
                issueNeedStockDO.setShopId(newIssueInStockDo.getShopId());
                issueNeedStockDO.setCategoryName(newIssueInStockDo.getCategoryName());
                issueNeedStockDO.setMidCategoryName(newIssueInStockDo.getMidCategoryName());
                issueNeedStockDO.setSmallCategoryName(newIssueInStockDo.getSmallCategoryName());
//                issueNeedStockDO.setSizeId(newIssueInStockDo.getSizeId());
                issueNeedStockDO.setSizeName(newIssueInStockDo.getSizeName());
                issueNeedStockDO.setMatCode(newIssueInStockDo.getMatCode());
                issueNeedStockDO.setAvgSaleQty(newIssueInStockDo.getAvgSaleQty() == null ? 0 : newIssueInStockDo.getAvgSaleQty().doubleValue());
//                issueNeedStockDO.setMinQty(newIssueInStockDo.getSecurityQty() == null ? 0 : newIssueInStockDo.getSecurityQty().doubleValue());
                issueNeedStockDO.setTotalStockQty(newIssueInStockDo.getTotalStockQty() == null ? 0 : newIssueInStockDo.getTotalStockQty().doubleValue());

                // TODO 用shopId取第一个
                ShopInfoData shopInfo = shopInfoDataList.stream().filter(shopInfoData -> shopInfoData.getShopID().equals(issueNeedStockDO.getShopId())).findFirst().orElse(null);

                MidCategoryStockDO midCategoryStockDO = newMidCategoryStockMap.get(issueNeedStockDO.getShopIdCategoryMidCategoryKey());
                if (midCategoryStockDO != null) {
                    //单品日均销
                    Double avgSaleQty = issueNeedStockDO.getAvgSaleQty();
                    Double totalSaleQty = midCategoryStockDO.getAvgSaleQty();
                    Double needQty = midCategoryStockDO.getNeedQty();
                    BigDecimal percentCategory = BigDecimal.valueOf(avgSaleQty).divide(new BigDecimal(totalSaleQty), 4, ROUND_HALF_UP);

                    //保底需求量
                    Double securityQty = newIssueInStockDo.getSecurityQty() != null ? newIssueInStockDo.getSecurityQty().doubleValue() : 0;
                    //补货间隔天数
                    int issueDayCount = shopInfo != null ? shopInfo.getIssueDay() : 0;

                    //需求量1 : 最小需求量 : 保底需求量+补货间隔天数及安全天数的预测销量
                    Double needStockQty1;
                    Double minQty = securityQty;
                    if (shopInfo != null) {
                        minQty += (issueDayCount +
                                //若为沙盘或预留存计算则  当安全天数>3时，取3；当安全天数<=3时，使用原值
                                (calcType == null ? shopInfo.getSafeDay() : shopInfo.getSafeDay() > 3 ? 3 : shopInfo.getSafeDay())) * avgSaleQty;
                    }
                    needStockQty1 = minQty;

                    //需求量2 : 最大需求量
                    //1.天数上限：取门店信息表中的上限天数*日均销，没有配置上限的默认为45*日均销。
                    Double maxDayLimit = (calcType == null) ? avgSaleQty * (shopInfo == null || shopInfo.getMaxDays() == 0 ? 45 : shopInfo.getMaxDays()) : avgSaleQty * 45;
                    //20200420 需求地址: http://wiki.nome.com/pages/viewpage.action?pageId=13260439
                    //需求量2修改为: max( 45天的预测销量，保底需求量+补货间隔天数*日均销 )
                    maxDayLimit = Math.max(maxDayLimit, securityQty + issueDayCount * avgSaleQty);


                    //2.预留数量上限：取沙盘计算出的预留配发总量。
                    //3.需求量2 = min（天数上限，预留数量上限+在店+在途+在配）
                    Double needStockQty2;
                    if (reserveDetailDoMap == null) {
                        //非预留存模式
                        needStockQty2 = maxDayLimit;
                    } else {
                        //预留存模式
                        // 预留量上限
                        Double topLimit = reserveDetailDoMap.get(newIssueInStockDo.getShopIdMatCodeSizeNameKey()) == null ? 0 : reserveDetailDoMap.get(newIssueInStockDo.getShopIdMatCodeSizeNameKey()).getIssueReserveNum().doubleValue();
                        // 门店总库存
                        Double storeStockQty = issueNeedStockDO.getTotalStockQty();
                        needStockQty2 = reserveDetailDoMap == null ? (maxDayLimit) : Math.min(maxDayLimit, topLimit + storeStockQty);
                    }
                    //需求量3 : 合理需求量
                    BigDecimal needStockQty3 = BigDecimal.valueOf(needQty).multiply(percentCategory);


//                        LoggerUtil.debug(logger, "[需求量计算]:需求量1:"+ issueNeedStockDO.getShopId()+ "_" + issueNeedStockDO.getMatCode() +"_" + issueNeedStockDO.getSizeId() + ":" +
//                                issueNeedStockDO.getMinQty() + " + (" + shopInfo.getIssueDay() + " + " +shopInfo.getSafeDay() + ") *" + avgSaleQty.toString());
//                        LoggerUtil.debug(logger, "[需求量计算]:需求量2:"+ issueNeedStockDO.getShopId()+ "_" + issueNeedStockDO.getMatCode() +"_" + issueNeedStockDO.getSizeId() + ":" +
//                                avgSaleQty.toString() + " * 45");
//                        LoggerUtil.debug(logger, "[需求量计算]:需求量3:"+ issueNeedStockDO.getShopId()+ "_" + issueNeedStockDO.getMatCode() +"_" + issueNeedStockDO.getSizeId() + ":" +
//                                needQty + " * " + avgSaleQty.toString() + " / " + totalSaleQty);

                    //原有计算方式：
                    //最终需求量=max( 需求量1, min(需求量2,需求量3) )
                    //更新计算方式：
                    //最终需求量=min( 需求量2, max(需求量1,需求量3) )
//                    Double needStockQty = Math.max(Math.min(needStockQty2,needStockQty3.doubleValue()),needStockQty1);
                    Double needStockQty = Math.min(needStockQty2, Math.max(needStockQty1, needStockQty3.doubleValue()));

//                    issueNeedStockDO.setPercentCategory(percentCategory);
//                    issueNeedStockDO.setNeedStockQty1(needStockQty1);
//                    issueNeedStockDO.setNeedStockQty2(needStockQty2);
//                    issueNeedStockDO.setNeedStockQty3(needStockQty3.doubleValue());
//                    issueNeedStockDO.setNeedStockQty(needStockQty);
                    issueNeedStockDO.setRemainNeedQty(needStockQty);
//                    issueNeedStockDO.setTotalSaleQty(totalSaleQty);
//                    issueNeedStockDO.setMinQty(minQty);
//                    issueNeedStockDO.setNeedQty(needQty);
                }

                list.add(issueNeedStockDO);
            }
        }

        return list;
    }

//    /**
//     * 计算sku需求量
//     */
//    public void calcSkuRequirement(int taskId,Set<String> shopIdList,List<ShopInfoData> shopInfoDataList,List<MidCategoryStockDO> list,
//                                   Set<String> goodsAreaLevelDetailDoSet, Map<String, GoodsInfoDO> goodsInfoDoMap, Map<String, NewIssueOutStockDo> outStockQtyMap,
//                                   boolean reCalc,Date runTime) {
//
//        if (CollectionUtils.isEmpty(list)) {
//            return;
//        }
//
//        String tableName = CommonUtil.getTaskTableName("new_issue_in_stock",taskId,runTime);
//        String skuRequirementTableName = CommonUtil.getTaskTableName(ISSUE_NEED_STOCK_TABLE_PREFIX,taskId,runTime);
//
//        List<String> childShopList = shopInfoDataList.stream()
//                .filter(shopInfoData -> shopInfoData.getHaveChild() == 0 && shopIdList.contains(shopInfoData.getShopID()))
//                .map(ShopInfoData::getShopID).collect(Collectors.toList());
//
//        List<String> noChildShopList = shopInfoDataList.stream()
//                .filter(shopInfoData -> shopInfoData.getHaveChild() == 1 && shopIdList.contains(shopInfoData.getShopID()))
//                .map(ShopInfoData::getShopID).collect(Collectors.toList());
//
//
//        if (CollectionUtils.isNotEmpty(childShopList)) {
//            processSkuNeedStock(taskId, childShopList,tableName,skuRequirementTableName,true,list,
//                    goodsAreaLevelDetailDoSet, goodsInfoDoMap, outStockQtyMap,runTime);
//        }
//
//        if (CollectionUtils.isNotEmpty(noChildShopList)) {
//            processSkuNeedStock(taskId, noChildShopList,tableName,skuRequirementTableName,false,list,
//                    goodsAreaLevelDetailDoSet, goodsInfoDoMap, outStockQtyMap,runTime);
//        }
//
//    }

//    private void processSkuNeedStock(int taskId, List<String> shopList,String inStockTableName,String skuRequirementTableName,boolean hasChild,List<MidCategoryStockDO> midCategoryStockList,
//                                     Set<String> goodsAreaLevelDetailDoSet, Map<String, GoodsInfoDO> goodsInfoDoMap, Map<String, NewIssueOutStockDo> outStockQtyMap,Date runTime) {
//
//        List<ShopInfoData> shopInfoDataList = shopInfoCache.getShopList();
//
//        String fileNameSuffix = "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".txt";
//        String fullFileName = "";
//        try {
//            File file = new File(loadFilePath + "needStock" + fileNameSuffix);
//            if(!file.exists()){
//                file.createNewFile();
//            }
//            fullFileName = file.getAbsoluteFile().getPath();
//            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
////            BufferedWriter bw = new BufferedWriter(fileWriter);
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
//
//            List<IssueNeedStockDO> list = new ArrayList<>();
//            List<NewIssueInStockDo> newIssueInStockDoList = newIssueDOMapper.getIssueInStockList(CommonUtil.getTaskTableName("new_issue_in_stock",taskId,runTime), hasChild ? 1 : 0, new HashSet<>(shopList));
//            for (NewIssueInStockDo newIssueInStockDo : newIssueInStockDoList) {
//                if (newIssueInStockDo.getIsProhibited() != 1 ) {
//                    //不在货盘 或者 淘汰品并且库存小于一个中包数
//                    if (!goodsAreaLevelDetailDoSet.contains(newIssueInStockDo.getShopId() + "_" + newIssueInStockDo.getMatCode()) ||
//                            // outStockQtyMap.get(newIssueInStockDo.getMatCode() + "_" + newIssueInStockDo.getSizeId()) == null ||
//                            (newIssueInStockDo.getIsEliminate() == 1
//                            )) {
//                        continue;
//                    }
//
//                    IssueNeedStockDO issueNeedStockDO = new IssueNeedStockDO();
//                    issueNeedStockDO.setShopId(newIssueInStockDo.getShopId());
//                    issueNeedStockDO.setCategoryName(newIssueInStockDo.getCategoryName());
//                    issueNeedStockDO.setCategoryName(newIssueInStockDo.getCategoryName());
//                    issueNeedStockDO.setMidCategoryName(newIssueInStockDo.getMidCategoryName());
//                    issueNeedStockDO.setSmallCategoryName(newIssueInStockDo.getSmallCategoryName());
//                    issueNeedStockDO.setSizeId(newIssueInStockDo.getSizeId());
//                    issueNeedStockDO.setSizeName(newIssueInStockDo.getSizeName());
//                    issueNeedStockDO.setMatCode(newIssueInStockDo.getMatCode());
//                    issueNeedStockDO.setAvgSaleQty(newIssueInStockDo.getAvgSaleQty() == null ? 0 : newIssueInStockDo.getAvgSaleQty().doubleValue());
//                    issueNeedStockDO.setMinQty(newIssueInStockDo.getSecurityQty() == null ? 0 : newIssueInStockDo.getSecurityQty().doubleValue());
//                    issueNeedStockDO.setTotalStockQty(newIssueInStockDo.getTotalStockQty() == null ? 0 : newIssueInStockDo.getTotalStockQty().doubleValue());
//                    list.add(issueNeedStockDO);
//                }
//            }
//
//            // 中类数
//            List<MidCategoryStockDO> newMidCategoryStockList = midCategoryStockList.stream()
//                    .filter(midCategoryStockDO -> midCategoryStockDO.getAvgSaleQty() > 0 && shopList.contains(midCategoryStockDO.getShopID()))
//                    .collect(Collectors.toList());
//
//            List<IssueNeedStockDO> newList = new ArrayList<>();
//            for (IssueNeedStockDO issueNeedStockDO : list) {
//
//                ShopInfoData shopInfo = shopInfoDataList.stream().filter(shopInfoData -> shopInfoData.getShopID().equals(issueNeedStockDO.getShopId())).findFirst().orElse(null);
//
//                for (MidCategoryStockDO midCategoryStockDO : newMidCategoryStockList) {
//
//                    if (issueNeedStockDO.getKey().equals(midCategoryStockDO.getKey())) {
//
//                        Double avgSaleQty = issueNeedStockDO.getAvgSaleQty();
//                        Double totalSaleQty = midCategoryStockDO.getAvgSaleQty();
//                        Double needQty = midCategoryStockDO.getNeedQty();
//                        BigDecimal percentCategory = new BigDecimal(avgSaleQty).divide(new BigDecimal(totalSaleQty));
//                        Double needStockQty1 = 0D; // 最小需求量
//                        Double needStockQty2 = avgSaleQty * 45; // 最大需求量
//                        BigDecimal needStockQty3 = new BigDecimal(needQty).multiply(percentCategory); // 合理需求量
//                        Double minQty = 0D;
//
//                        if (issueNeedStockDO.getMinQty() != null) {
//                            minQty = issueNeedStockDO.getMinQty();
//
//                            if (shopInfo != null) {
//                                minQty += (shopInfo.getIssueDay() + shopInfo.getSafeDay()) * avgSaleQty;
//                            }
//                        }
//                        needStockQty1 = minQty;
//
////                        LoggerUtil.debug(logger, "[需求量计算]:需求量1:"+ issueNeedStockDO.getShopId()+ "_" + issueNeedStockDO.getMatCode() +"_" + issueNeedStockDO.getSizeId() + ":" +
////                                issueNeedStockDO.getMinQty() + " + (" + shopInfo.getIssueDay() + " + " +shopInfo.getSafeDay() + ") *" + avgSaleQty.toString());
////                        LoggerUtil.debug(logger, "[需求量计算]:需求量2:"+ issueNeedStockDO.getShopId()+ "_" + issueNeedStockDO.getMatCode() +"_" + issueNeedStockDO.getSizeId() + ":" +
////                                avgSaleQty.toString() + " * 45");
////                        LoggerUtil.debug(logger, "[需求量计算]:需求量3:"+ issueNeedStockDO.getShopId()+ "_" + issueNeedStockDO.getMatCode() +"_" + issueNeedStockDO.getSizeId() + ":" +
////                                needQty + " * " + avgSaleQty.toString() + " / " + totalSaleQty);
//
//                        Double needStockQty = Math.max(Math.min(needStockQty2,needStockQty3.doubleValue()),needStockQty1);
//
//                        issueNeedStockDO.setPercentCategory(percentCategory);
//                        issueNeedStockDO.setNeedStockQty1(needStockQty1);
//                        issueNeedStockDO.setNeedStockQty2(needStockQty2);
//                        issueNeedStockDO.setNeedStockQty3(needStockQty3.doubleValue());
//                        issueNeedStockDO.setNeedStockQty(needStockQty);
//                        issueNeedStockDO.setTotalSaleQty(totalSaleQty);
//                        issueNeedStockDO.setMinQty(minQty);
//                        issueNeedStockDO.setNeedQty(needQty);
//
//                        newList.add(issueNeedStockDO);
//                    }
//                }
//            }
//
//            int dosSize = newList.size();
//            for (int i = 0; i < dosSize; i++) {
//                bw.write(newList.get(i).getLoadString() + (i >= dosSize - 1 ? "" : "\r\n"));
//            }
//
//            bw.close();
//            LoggerUtil.info(logger, "[issueNeedStockWriteFile] finish, fullFileName:{0}", fullFileName);
//        } catch (Exception e) {
//            LoggerUtil.error(e, logger, "[issueNeedStockWriteFile] catch exception");
//        }
//
//        int ret = newIssueSkuCalcDOMapper.loadFileNeedStock(skuRequirementTableName, fullFileName);
//        LoggerUtil.info(logger, "[issueNeedStockWriteFile] finish, fullFileName:{0}, ret:{1}", fullFileName, ret);
//
//    }

    private void matchMidCategory(List<MidCategoryStockDO> list, List<MidCategoryStockDO> categoryDisplayList) {

        for (MidCategoryStockDO stockDO : list) {
            for (MidCategoryStockDO displayDO : categoryDisplayList) {
                // TODO 熟悉业务后考虑用stream()优化这里的写法
                if (stockDO.getShopIdCategoryMidCategoryKey().equals(displayDO.getShopIdCategoryMidCategoryKey())) {

                    // 公式:
                    double displayB = displayDO.getDisplayQtyB() == null ? 0D : displayDO.getDisplayQtyB();
                    stockDO.setDisplayQty(displayB);

                    if (displayDO.getDisplayQty() != null) {
                        double displayQty = displayB + (displayDO.getIssueDay() + displayDO.getSafeDay()) * stockDO.getAvgSaleQty();

                        // TODO 这里没有新需求里的Max.Min那部分，直接取了陈列需求
                        stockDO.setNeedQty(displayQty);
                    } else {
                        stockDO.setNeedQty(0D);
                    }
                }
            }
        }
    }
}
