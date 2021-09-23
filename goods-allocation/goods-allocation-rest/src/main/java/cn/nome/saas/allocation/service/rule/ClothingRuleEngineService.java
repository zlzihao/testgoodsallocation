package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.GoodsInfoCache;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.model.allocation.AllocationClothingSKC;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationClothingInvalidGoodsMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationClothingResultMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationStockDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.QdIssueDisplayDesginDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.AllocationExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.repository.entity.vertical.AllocationGoodsSKC;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服装齐码规则类
 *
 * @author Bruce01.fan
 * @date 2019/12/4
 */
@Service
public class ClothingRuleEngineService {

    private static Logger logger = LoggerFactory.getLogger(ClothingRuleEngineService.class);

    @Autowired
    AllocationExtraDataMapper allocationExtraDataMapper;

    @Autowired
    QdIssueDisplayDesginDOMapper qdIssueDisplayDesginDOMapper;

    @Autowired
    AllocationStockDOMapper allocationStockDOMapper;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    AllocationClothingResultMapper allocationClothingResultMapper;

    @Autowired
    AllocationClothingInvalidGoodsMapper allocationClothingInvalidGoodsMapper;

    @Value("${clothing.validGoods.switch}")
    private int clothingValidGoodsSwitch;

    /**
     * 查询门店的总库存+库存尺码数
     * @return
     */
    public List<AllocationClothingSKC> getShopClothingSkcInfo(Set<String> shopIdList,List<String> matCodeList) {

        List<AllocationClothingSKC> allocationClothingSKCList = new ArrayList<>();

        // 在售库存
        List<OutOfStockGoodsDO> ofStockGoodsDOList = allocationExtraDataMapper.getShopClothingList(shopIdList,matCodeList,null);
        // 在配库存
        List<OutOfStockGoodsDO> applyList = allocationExtraDataMapper.getShopClothingApplyList(shopIdList,matCodeList,null);

        for (OutOfStockGoodsDO outOfStockGoodsDO : ofStockGoodsDOList) {
            for (OutOfStockGoodsDO apply : applyList) {
                if(outOfStockGoodsDO.getKey().equals(apply.getKey())) {
                    outOfStockGoodsDO.setApplyStockQty(apply.getApplyStockQty());
                }
            }
        }

        Map<String,List<OutOfStockGoodsDO>> map = ofStockGoodsDOList.stream().collect(Collectors.groupingBy(outOfStockGoodsDO->{
            return outOfStockGoodsDO.getShopId()+","+ outOfStockGoodsDO.getMatCode();
        }));

        for (String key : map.keySet()) {
            List<OutOfStockGoodsDO> list = map.get(key);
            AllocationClothingSKC allocationClothingSKC = new AllocationClothingSKC();
            allocationClothingSKC.setShopId(key.split(",")[0]);
            allocationClothingSKC.setMatCode(key.split(",")[1]);
            allocationClothingSKC.setSizeCount(list.size());
            allocationClothingSKC.setTotalStock(list.stream().mapToInt(OutOfStockGoodsDO::getStoreQty).sum());

            allocationClothingSKCList.add(allocationClothingSKC);
        }

        LoggerUtil.debug(logger,"[CLOTHING_SKC] msg = skc list:{0}",allocationClothingSKCList);

        return allocationClothingSKCList;
    }

    /**
     * 矫正理想库存和可供出的商品库存
     * @param allocationClothingSKCList
     * @param outOfStockGoodsDOList
     */
    public List<OutOfStockGoodsDO> reCalBestQtyForClothing(String shopId,String matCode,List<AllocationClothingSKC> allocationClothingSKCList,List<OutOfStockGoodsDO> outOfStockGoodsDOList) {

        // 需要更新理想库存的列表
        List<OutOfStockGoodsDO> newOutOfStockGoodsDOList = new ArrayList<>();

        // skc下尺码数和库存数
        AllocationClothingSKC allocationClothing = allocationClothingSKCList.stream()
                .filter(allocationClothingSKC -> allocationClothingSKC.getShopId().equals(shopId)
                        && allocationClothingSKC.getMatCode().equals(matCode))
                .findFirst().orElse(null);

        if (allocationClothing == null) {
            return null;
        }

        for (OutOfStockGoodsDO outOfStockGoodsDO :outOfStockGoodsDOList) {

                // 不满足齐码要求的，直接全部调出
                if (allocationClothing.getSizeCount() < 2 || allocationClothing.getTotalStock() <5) {
                    int oldIdealStockQty = outOfStockGoodsDO.getIdealStockQty();
                    int oldSupplyStockQty = outOfStockGoodsDO.getSupplyStockQty();
                    // 理想库存设为0
                    outOfStockGoodsDO.setIdealStockQty(0);
                    //
                    int supplyQty = outOfStockGoodsDO.getStoreQty() - outOfStockGoodsDO.getIdealStockQty();
                    outOfStockGoodsDO.setSupplyStockQty(supplyQty);
                    outOfStockGoodsDO.setInvalidSkcFlag(1); // 无效

                    newOutOfStockGoodsDOList.add(outOfStockGoodsDO);

                    LoggerUtil.warn(logger,"[调拨前不齐码,全部抽空] msg = shopId:{0},oldIdealQty:{1}," +
                            "oldSupplyQty:{2},newIdealQty:{3},newSuppyQty:{4},storeQty:{5},skcInfo:{6}",
                            outOfStockGoodsDO.getShopId(),oldIdealStockQty,oldSupplyStockQty,0,
                            supplyQty,outOfStockGoodsDO.getStoreQty(),allocationClothing);

                } else if(allocationClothing.getTotalStock() ==5 || allocationClothing.getSizeCount() ==2) {
                   // 不做调出
                    int oldIdealStockQty = outOfStockGoodsDO.getIdealStockQty();
                    int oldSupplyStockQty = outOfStockGoodsDO.getSupplyStockQty();
                    outOfStockGoodsDO.setIdealStockQty(outOfStockGoodsDO.getIdealStockQty() + outOfStockGoodsDO.getSupplyStockQty());
                    outOfStockGoodsDO.setSupplyStockQty(0);

                    newOutOfStockGoodsDOList.add(outOfStockGoodsDO);

                    LoggerUtil.warn(logger,"[调拨后不齐码,不再调出] msg = shopId:{0},oldIdealQty:{1}," +
                                    "oldSupplyQty:{2},newIdealQty:{3},newSuppyQty:{4},storeQty:{5},skcInfo:{6}",
                            outOfStockGoodsDO.getShopId(),oldIdealStockQty,oldSupplyStockQty,outOfStockGoodsDO.getIdealStockQty(),
                            0,outOfStockGoodsDO.getStoreQty(),allocationClothing);

                } else {
                    // 判断这个sku调出后，是否还齐码（整个sku抽空）
                    if (outOfStockGoodsDO.getStoreQty().equals(outOfStockGoodsDO.getSupplyStockQty())) {

                        if (allocationClothing.getSizeCount()-1 < 2 || allocationClothing.getTotalStock() - outOfStockGoodsDO.getSupplyStockQty() <= 5) {
                            // 如果只有一件，不调出
                            if (outOfStockGoodsDO.getStoreQty() == 1) {
                                outOfStockGoodsDO.setIdealStockQty(1);
                                outOfStockGoodsDO.setSupplyStockQty(0);

                                newOutOfStockGoodsDOList.add(outOfStockGoodsDO);
                                continue;
                            }

                            // 这个码数不能调空，需要留一件
                            int idealQty = outOfStockGoodsDO.getIdealStockQty();
                            int supplyQty = outOfStockGoodsDO.getSupplyStockQty();

                            int newIdealQty = idealQty + supplyQty - 1 ;
                            int newSupplyQty = 1;

                            outOfStockGoodsDO.setIdealStockQty(newIdealQty);
                            outOfStockGoodsDO.setSupplyStockQty(newSupplyQty);

                            newOutOfStockGoodsDOList.add(outOfStockGoodsDO);

                            // 更新库存数
                            int totalStock = allocationClothing.getTotalStock();
                            allocationClothing.setTotalStock(totalStock - outOfStockGoodsDO.getSupplyStockQty());

                            LoggerUtil.warn(logger,"[调拨后不齐码,保留一件] msg = shopId:{0},oldIdealQty:{1}," +
                                    "oldSupplyQty:{2},newIdealQty:{3},newSuppyQty:{4},storeQty:{5},skcInfo:{6}",outOfStockGoodsDO.getShopId(),
                                    idealQty,supplyQty,newIdealQty,1,outOfStockGoodsDO.getStoreQty(),allocationClothing);

                        } else {
                            // 减掉相应sku的库存数和尺码
                            int sizeCount = allocationClothing.getSizeCount();
                            int totalStock = allocationClothing.getTotalStock();

                            allocationClothing.setSizeCount(sizeCount-1); // 抽空sku，减去一个尺码数
                            allocationClothing.setTotalStock(totalStock - outOfStockGoodsDO.getSupplyStockQty());

                            LoggerUtil.debug(logger,"[CLOTHING_SKC_INFO] msg=shop:{0},matCode:{1},oldSize:{2},oldStock:{3},newSize:{4},newStock:{5}",allocationClothing.getShopId(),allocationClothing.getMatCode(),sizeCount,totalStock,allocationClothing.getSizeCount(),allocationClothing.getTotalStock());
                        }
                    } else {
                        // sku部分库存调出
                        if (allocationClothing.getTotalStock() - outOfStockGoodsDO.getSupplyStockQty() <= 5) {
                            // 这个码数不能调空，需要留一件
                            int idealQty = outOfStockGoodsDO.getIdealStockQty();
                            int supplyQty = outOfStockGoodsDO.getSupplyStockQty();

                            int newIdealQty = idealQty + supplyQty - 1 ;
                            int newSupplyQty = 1;

                            outOfStockGoodsDO.setIdealStockQty(newIdealQty);
                            outOfStockGoodsDO.setSupplyStockQty(newSupplyQty);

                            // 添加到待更新理想库存列表中
                            newOutOfStockGoodsDOList.add(outOfStockGoodsDO);

                            // 更新库存数
                            int totalStock = allocationClothing.getTotalStock();
                            allocationClothing.setTotalStock(totalStock - outOfStockGoodsDO.getSupplyStockQty());
                        } else {
                            int totalStock = allocationClothing.getTotalStock();
                            allocationClothing.setTotalStock(totalStock - outOfStockGoodsDO.getSupplyStockQty());
                        }

                    }

                }
        }

        return newOutOfStockGoodsDOList;
    }

    /**
     * 获取门店有效skc数和skc下限数
     * @param shopIdSet
     * @return
     */
    public List<AllocationGoodsSKC> getClothingSKC(int taskId,Set<String> shopIdSet,Set<String> categoryCodeSet,boolean saveFlag) {

        List<AllocationGoodsSKC> allocationGoodsSKCList = new ArrayList<>();
        // 获取门店skc下限
        List<QdIssueDisplayDesginDO> qdIssueDisplayDesginDOList = qdIssueDisplayDesginDOMapper.getShopDisplayDesignListByShop(shopIdSet);
        // 门店有效skc数量(门店中所有的matcode参与计算)
        Map<String,List<OutOfStockGoodsDO>> goodsMap = this.getGoodMap(shopIdSet,null,categoryCodeSet);

        //  无效的skc数
        List<OutOfStockGoodsDO> validGoodsList = new ArrayList<>();
        List<OutOfStockGoodsDO> invalidGoodsList = new ArrayList<>();

        for (String key : goodsMap.keySet()) {
            List<OutOfStockGoodsDO> shopStockGoodsList = goodsMap.get(key);

            // 无效skc(尺码个数<2)
            if (shopStockGoodsList.size()<2) {
                invalidGoodsList.addAll(shopStockGoodsList);
                LoggerUtil.info(logger,"[INVALID_SKC_SIZE] msg=shop:{0}",key);
                continue;
            }

            // 无效skc 库存<5
            if (shopStockGoodsList.stream().mapToLong(OutOfStockGoodsDO::getStoreQty).sum() < 5) {
                invalidGoodsList.addAll(shopStockGoodsList);
                LoggerUtil.info(logger,"[INVALID_SKC_STOCK] msg=shop:{0}",key);
                continue;
            }

            validGoodsList.addAll(shopStockGoodsList);
            // 有效skc
            OutOfStockGoodsDO firstGoods = shopStockGoodsList.get(0);
            String shopId = firstGoods.getShopId();
            String categoryCode = firstGoods.getCategoryCode();

            AllocationGoodsSKC allocationGoodsSKCDO = allocationGoodsSKCList.stream()
                    .filter(allocationGoodsSKC -> allocationGoodsSKC.getShopId().equals(shopId)
                            && allocationGoodsSKC.getCategoryCode().equals(categoryCode))
                    .findFirst()
                    .orElse(null);

            if (allocationGoodsSKCDO == null) {
                allocationGoodsSKCDO = new AllocationGoodsSKC();
                allocationGoodsSKCDO.setShopId(shopId);
                allocationGoodsSKCDO.setCategoryCode(categoryCode);
                allocationGoodsSKCList.add(allocationGoodsSKCDO);
            }

            // skc加一
            allocationGoodsSKCDO.setSkcCount(allocationGoodsSKCDO.getSkcCount() + 1);
        }

        // 计算skc下限、上限
        for (QdIssueDisplayDesginDO allocationDisplayDesignDO : qdIssueDisplayDesginDOList) {

            for (AllocationGoodsSKC allocationGoodsSKC : allocationGoodsSKCList) {
                if (allocationDisplayDesignDO.getShopId().equals(allocationGoodsSKC.getShopId())) {
                    if ("M".equals(allocationGoodsSKC.getCategoryCode())) {
                        allocationGoodsSKC.setLowStandardSkcCount((int)Math.round(allocationDisplayDesignDO.getMaleStandardSkc() * 0.8));
                        allocationGoodsSKC.setStandardSkcCount(allocationDisplayDesignDO.getMaleStandardSkc());
                        allocationGoodsSKC.setHighStandardSkcCount((int)Math.round(allocationDisplayDesignDO.getMaleStandardSkc() * allocationDisplayDesignDO.getDisplayRatio()));
                    } else if ("W".equals(allocationGoodsSKC.getCategoryCode())) {
                        allocationGoodsSKC.setLowStandardSkcCount((int)Math.round(allocationDisplayDesignDO.getFemaleStandardSkc() * 0.8));
                        allocationGoodsSKC.setStandardSkcCount(allocationDisplayDesignDO.getFemaleStandardSkc());
                        allocationGoodsSKC.setHighStandardSkcCount((int)Math.round(allocationDisplayDesignDO.getFemaleStandardSkc() * allocationDisplayDesignDO.getDisplayRatio()));
                    }
                }
            }
        }

        // 计算无效skc款数、件数
        this.calcInvalidSkc(invalidGoodsList,allocationGoodsSKCList);


        //
        // 插入有效&非有效sku信息（调拨后）
        if (saveFlag) {
            this.batchSaveGoods(taskId, validGoodsList, invalidGoodsList, 1);
        }

        return allocationGoodsSKCList;
    }


    /**
     * 生成服装调拨效果
     */
    public void generateClothingEffectResult(int taskId) {

        // 最终参与调拨的所有门店列表
        Set<String> allocationShopSet = this.getAllocationShopList(taskId);

        // 调拨后数据
        List<AllocationStockDO> allocationStockDOList = allocationStockDOMapper.selectAllocationStockList(taskId);
        Set<String> categoryCodeSet = allocationStockDOList.stream().map(AllocationStockDO::getCategoryCode).collect(Collectors.toSet());
        List<String> matCodeList = allocationStockDOList.stream().map(AllocationStockDO::getMatCode).distinct().collect(Collectors.toList());
        // 有效skc
        List<AllocationGoodsSKC> allocationGoodsSKCList = this.getClothingSKC(taskId,allocationShopSet,categoryCodeSet,true);
        // 服装调拨效果
        List<AllocationClothingResultDO> allocationClothingResultDOList = this.generateClothingResultList(allocationGoodsSKCList);
        // 服装调拨效果-调拨后
        this.processAfterAllocationResult(taskId,allocationShopSet,matCodeList,categoryCodeSet,allocationStockDOList,allocationGoodsSKCList,allocationClothingResultDOList);
        // 插入调拨效果数据
        allocationClothingResultMapper.deleteByParam(taskId);
        allocationClothingResultMapper.batchInsert(allocationClothingResultDOList);

    }

    private Set<String> getAllocationShopList(int taskId) {
        Set<String> allocationShopSet = new HashSet<>();
        List<String> shopConcatList = allocationStockDOMapper.selectAllocationShop(taskId);

        if (CollectionUtils.isEmpty(shopConcatList)) {
            LoggerUtil.warn(logger,"[获取调拨门店为空] msg=taskId:{0}",taskId);
            return allocationShopSet;
        }
        for (String concatShop : shopConcatList) {
            allocationShopSet.add(concatShop.split(":")[0]);
            allocationShopSet.add(concatShop.split(":")[1]);
        }

        return allocationShopSet;
    }

    private List<AllocationClothingResultDO> generateClothingResultList(List<AllocationGoodsSKC> allocationGoodsSKCList) {

        List<DwsDimShopDO> dwsDimShopDOS = shopListCache.getShopList();

        return allocationGoodsSKCList.stream().map(allocationGoodsSKC -> {

            AllocationClothingResultDO allocationClothingResultDO = new AllocationClothingResultDO();

            String shopName = dwsDimShopDOS.stream().filter(shop->shop.getShopId().equals(allocationGoodsSKC.getShopId())).map(DwsDimShopDO::getShopName).findFirst().orElse("");

            allocationClothingResultDO.setShopId(allocationGoodsSKC.getShopId());
            allocationClothingResultDO.setShopName(shopName);
            allocationClothingResultDO.setCategoryCode(allocationGoodsSKC.getCategoryCode());
            allocationClothingResultDO.setCategoryName("W".equals(allocationGoodsSKC.getCategoryCode()) ? "女装" : "男装");
            allocationClothingResultDO.setLowSkc(allocationGoodsSKC.getLowStandardSkcCount());
            allocationClothingResultDO.setStandardSkc(allocationGoodsSKC.getStandardSkcCount());
            allocationClothingResultDO.setHighSkc(allocationGoodsSKC.getHighStandardSkcCount());
            BigDecimal skcCountBD = new BigDecimal(allocationGoodsSKC.getSkcCount());
            BigDecimal standSkcCountDB;
            if (allocationGoodsSKC.getStandardSkcCount() > 0) {
                standSkcCountDB = new BigDecimal(allocationGoodsSKC.getStandardSkcCount());
            } else {
                standSkcCountDB = new BigDecimal(1);
            }
            BigDecimal beforeFullRate = skcCountBD.divide(standSkcCountDB, 2, RoundingMode.HALF_UP);
            allocationClothingResultDO.setBeforeFullRate(beforeFullRate.doubleValue());

            allocationClothingResultDO.setBeforeSkc(allocationGoodsSKC.getSkcCount());
            allocationClothingResultDO.setBeforeInvalidStyle(allocationGoodsSKC.getInvalidSkcStyle());
            allocationClothingResultDO.setBeforeInvalidNum(allocationGoodsSKC.getInvaildSkcNum());

            return allocationClothingResultDO;
        }).collect(Collectors.toList());
    }

    /**
     * 调拨后效果数据处理
     */
    private void  processAfterAllocationResult(int taskId,Set<String> shopIdSet,List<String> matCodeList,Set<String> categoryCodeSet,List<AllocationStockDO> allocationStockDOList,List<AllocationGoodsSKC> allocationGoodsSKCList,List<AllocationClothingResultDO> allocationClothingResultDOList) {
        // 当前门店有库存商品数据
        Map<String,List<OutOfStockGoodsDO>> goodsMap = this.getGoodMap(shopIdSet,null,categoryCodeSet);
        Map<String,List<OutOfStockGoodsDO>> afterAllocationGoodsMap = new HashMap<>();

        // 更新调拨后库存信息
        for (String key :goodsMap.keySet()) {
            String shopId = key.split(":")[0];
            String matCode = key.split(":")[1];

            List<OutOfStockGoodsDO> storeSKUList = goodsMap.get(key);
            List<OutOfStockGoodsDO> afterStoreSKUList = new ArrayList<>();

            // 调入商品列表
            List<AllocationStockDO> inStoreAllocationList = allocationStockDOList.stream()
                                 .filter(allocationStockDO -> allocationStockDO.getShopId().equals(shopId) && allocationStockDO.getMatCode().equals(matCode))
                                 .collect(Collectors.toList());
            // 调出商品列表
            List<AllocationStockDO> outStoreAllocationList = allocationStockDOList.stream()
                    .filter(allocationStockDO -> allocationStockDO.getSupplyShopId().equals(shopId) && allocationStockDO.getMatCode().equals(matCode))
                    .collect(Collectors.toList());

            // 无调入，调出记录的
            if (CollectionUtils.isEmpty(inStoreAllocationList) && CollectionUtils.isEmpty(outStoreAllocationList)) {
                afterStoreSKUList.addAll(storeSKUList);
            }

            // 调入的商品库存做加法操作
            if (CollectionUtils.isNotEmpty(inStoreAllocationList)) {

                for (OutOfStockGoodsDO outOfStockGoodsDO : storeSKUList) {

                    AllocationStockDO allocationStockDO = inStoreAllocationList.stream()
                            .filter(inStoreStock->inStoreStock.getSizeId().equals(outOfStockGoodsDO.getSizeId()))
                            .findFirst()
                            .orElse(null);

                    if(allocationStockDO != null) {
                        // 更新
                        int newApplyQty = outOfStockGoodsDO.getApplyStockQty() + allocationStockDO.getAllocationStockQty();
                        outOfStockGoodsDO.setApplyStockQty(newApplyQty);
                        afterStoreSKUList.add(outOfStockGoodsDO);
                    } else {
                        // 不存在调拨池的商品
                        afterStoreSKUList.add(outOfStockGoodsDO);
                    }
                }

                // 新调入
               for (AllocationStockDO allocationStockDO : inStoreAllocationList) {
                   long count = storeSKUList.stream()
                           .filter(outOfStockGoodsDO->outOfStockGoodsDO.getSizeId().equals(allocationStockDO.getSizeId()))
                           .count();
                   if (count == 0) {
                       afterStoreSKUList.add(this.buildOutOfStockGoodsDO(allocationStockDO));
                   }
               }

            }

            // 调出店商品库存做减法操作
            if (CollectionUtils.isNotEmpty(outStoreAllocationList)) {

                for (OutOfStockGoodsDO outOfStockGoodsDO : storeSKUList) {

                    AllocationStockDO allocationStockDO = outStoreAllocationList.stream()
                            .filter(outStoreStock-> outOfStockGoodsDO.getSizeId().equals(outStoreStock.getSizeId()))
                            .findFirst()
                            .orElse(null);

                    if (allocationStockDO != null) {
                        if (outOfStockGoodsDO.getStoreQty() - allocationStockDO.getAllocationStockQty() == 0) {
                            LoggerUtil.warn(logger,"[调空的商品，不计算在内] msg=shopId:{0},matcode:{1},sizeId:{2}",outOfStockGoodsDO.getShopId(),outOfStockGoodsDO.getMatCode(),outOfStockGoodsDO.getSizeId());
                            continue;
                        }
                        int newQty = outOfStockGoodsDO.getStockQty() - allocationStockDO.getAllocationStockQty();
                        outOfStockGoodsDO.setStockQty(newQty);
                        afterStoreSKUList.add(outOfStockGoodsDO);
                    }  else {
                        // 不存在调拨池的商品
                        afterStoreSKUList.add(outOfStockGoodsDO);
                    }
                }
            }
            afterAllocationGoodsMap.put(key,afterStoreSKUList);
        }

        // 有效商品
        List<OutOfStockGoodsDO> validGoodsList = new ArrayList<>();
        // 非有效商品
        List<OutOfStockGoodsDO> invalidGoodsList = new ArrayList<>();
        // 调拨后有效skc列表
        List<AllocationGoodsSKC> afterAllocationGoodsSKCDOList = new ArrayList<>();

        for(String key : afterAllocationGoodsMap.keySet()) {
            List<OutOfStockGoodsDO> shopStockGoodsList = afterAllocationGoodsMap.get(key);

            // 无效skc(尺码个数<2)
            if (shopStockGoodsList.size()<2) {
                invalidGoodsList.addAll(shopStockGoodsList);
                LoggerUtil.info(logger,"[调拨后无效尺码] msg=shop:{0},size:{1}",key,shopStockGoodsList.size());
                continue;
            }

            // 无效skc 库存<5
            if (shopStockGoodsList.stream().mapToLong(OutOfStockGoodsDO::getStoreQty).sum() < 5) {
                invalidGoodsList.addAll(shopStockGoodsList);
                LoggerUtil.info(logger,"[调拨后无效的库存] msg=shop:{0},storeQty:{1}",key,shopStockGoodsList.stream().mapToLong(OutOfStockGoodsDO::getStoreQty).sum());
                continue;
            }

            // 有效skc
            OutOfStockGoodsDO firstGoods = shopStockGoodsList.get(0);
            validGoodsList.addAll(shopStockGoodsList);

            // 有效skc下限
            int standSkc = allocationGoodsSKCList.stream()
                    .filter(allocationGoodsSKC -> allocationGoodsSKC.getShopId().equals(firstGoods.getShopId())
                            && allocationGoodsSKC.getCategoryCode().equals(firstGoods.getCategoryCode()))
                    .map(AllocationGoodsSKC::getStandardSkcCount)
                    .findFirst()
                    .orElse(null);


            AllocationGoodsSKC afterAllocationGoodsSKCDO =afterAllocationGoodsSKCDOList.stream()
                    .filter(allocationGoodsSKC -> allocationGoodsSKC.getShopId().equals(firstGoods.getShopId())
                            && allocationGoodsSKC.getCategoryCode().equals(firstGoods.getCategoryCode()))
                    .findFirst()
                    .orElse(null);

            if (afterAllocationGoodsSKCDO == null) {
                afterAllocationGoodsSKCDO = new AllocationGoodsSKC();
                afterAllocationGoodsSKCDO.setShopId(firstGoods.getShopId());
                afterAllocationGoodsSKCDO.setCategoryCode(firstGoods.getCategoryCode());
                afterAllocationGoodsSKCDO.setStandardSkcCount(standSkc);
                afterAllocationGoodsSKCDOList.add(afterAllocationGoodsSKCDO);
            }

            afterAllocationGoodsSKCDO.setSkcCount(afterAllocationGoodsSKCDO.getSkcCount() + 1);
        }

        // 计算无效skc款数、件数
        this.calcInvalidSkc(invalidGoodsList,afterAllocationGoodsSKCDOList);

        // 调拨后效果处理
        for (AllocationClothingResultDO allocationClothingResultDO : allocationClothingResultDOList) {

            for (AllocationGoodsSKC allocationGoodsSKC : afterAllocationGoodsSKCDOList) {
                if (allocationClothingResultDO.getShopId().equals(allocationGoodsSKC.getShopId())
                        && allocationClothingResultDO.getCategoryCode().equals(allocationGoodsSKC.getCategoryCode())) {

                    BigDecimal skcCountBD = new BigDecimal(allocationGoodsSKC.getSkcCount());
                    BigDecimal standSkcCountDB;
                    if (allocationGoodsSKC.getStandardSkcCount() > 0) {
                        standSkcCountDB = new BigDecimal(allocationGoodsSKC.getStandardSkcCount());
                    } else {
                        standSkcCountDB = new BigDecimal(1);
                    }
                    BigDecimal afterFullRate = skcCountBD.divide(standSkcCountDB, 2, RoundingMode.HALF_UP);
                    allocationClothingResultDO.setAfterFullRate(afterFullRate.doubleValue());

                    allocationClothingResultDO.setAfterSkc(allocationGoodsSKC.getSkcCount());
                    allocationClothingResultDO.setAfterInvalidStyle(allocationGoodsSKC.getInvalidSkcStyle());
                    allocationClothingResultDO.setAfterInvalidNum(allocationGoodsSKC.getInvaildSkcNum());
                }

                allocationClothingResultDO.setTaskId(taskId);
            }
        }

        // 插入有效&非有效sku信息（调拨后）
        this.batchSaveGoods(taskId,validGoodsList,invalidGoodsList,2);

    }

    private void batchSaveGoods(int taskId,List<OutOfStockGoodsDO> validGoodsList,List<OutOfStockGoodsDO> invalidGoodsList,int type) {
        // 插入无效的商品明细
        if (CollectionUtils.isNotEmpty(invalidGoodsList)) {

            // 获取无效skcde日均销数据
            Set<String> subShopList = invalidGoodsList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());
            Set<String> matCodeSet = invalidGoodsList.stream().map(OutOfStockGoodsDO::getMatCode).collect(Collectors.toSet());
            List<OutOfStockGoodsDO> outOfStockGoodsDOList = allocationExtraDataMapper.getAvaliableSaleQtyByParam(subShopList,matCodeSet);
            for(OutOfStockGoodsDO invalidGoods : invalidGoodsList) {
                for (OutOfStockGoodsDO avgSalesGoods : outOfStockGoodsDOList) {
                    if (invalidGoods.getKey().equals(avgSalesGoods.getKey())) {
                        invalidGoods.setAvgSaleQty(avgSalesGoods.getAvgSaleQty());
                    }
                }
            }

            allocationClothingInvalidGoodsMapper.deleteByParam(taskId,type);

            List<AllocationClothingInvalidGoods> allocationClothingInvalidGoodsList = invalidGoodsList.stream().map(outOfStockGoodsDO -> {
                AllocationClothingInvalidGoods allocationClothingInvalidGoods = new AllocationClothingInvalidGoods();

                allocationClothingInvalidGoods.setTaskId(taskId);
                allocationClothingInvalidGoods.setShopId(outOfStockGoodsDO.getShopId());
                allocationClothingInvalidGoods.setShopName(outOfStockGoodsDO.getShopName());
                allocationClothingInvalidGoods.setMatCode(outOfStockGoodsDO.getMatCode());
                allocationClothingInvalidGoods.setMatName(outOfStockGoodsDO.getMatName());
                allocationClothingInvalidGoods.setSizeId(outOfStockGoodsDO.getSizeId());
                allocationClothingInvalidGoods.setStockQty(outOfStockGoodsDO.getStockQty());
                allocationClothingInvalidGoods.setPathStockQty(outOfStockGoodsDO.getPathStockQty());
                allocationClothingInvalidGoods.setApplyStockQty(outOfStockGoodsDO.getApplyStockQty());
                allocationClothingInvalidGoods.setAvgSaleQty(outOfStockGoodsDO.getAvgSaleQty());
                allocationClothingInvalidGoods.setAllocationType(type);
                if (outOfStockGoodsDO.getStoreQty() == 0 || outOfStockGoodsDO.getAvgSaleQty() == 0) {
                    allocationClothingInvalidGoods.setSaleDays(0D);
                } else {
                    BigDecimal storeQtyBD = new BigDecimal(outOfStockGoodsDO.getStoreQty());
                    BigDecimal avgSaleQtyBD = new BigDecimal(outOfStockGoodsDO.getAvgSaleQty());
                    allocationClothingInvalidGoods.setSaleDays(storeQtyBD.divide(avgSaleQtyBD,2,BigDecimal.ROUND_HALF_UP).doubleValue());
                }

                return allocationClothingInvalidGoods;
            }).collect(Collectors.toList());

            allocationClothingInvalidGoodsMapper.batchInsert(allocationClothingInvalidGoodsList);
        }

        // 插入有效的商品明细
        if (CollectionUtils.isNotEmpty(validGoodsList) && clothingValidGoodsSwitch == 1) {

            // 获取有效skcde日均销数据
            Set<String> subShopList = validGoodsList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());
            Set<String> matCodeSet = validGoodsList.stream().map(OutOfStockGoodsDO::getMatCode).collect(Collectors.toSet());
            List<OutOfStockGoodsDO> outOfStockGoodsDOList = allocationExtraDataMapper.getAvaliableSaleQtyByParam(subShopList,matCodeSet);
            for(OutOfStockGoodsDO validGoods : validGoodsList) {
                for (OutOfStockGoodsDO avgSalesGoods : outOfStockGoodsDOList) {
                    if (validGoods.getKey().equals(avgSalesGoods.getKey())) {
                        validGoods.setAvgSaleQty(avgSalesGoods.getAvgSaleQty());
                    }
                }
            }

            allocationClothingInvalidGoodsMapper.deleteValidGoodsByParam(taskId,type);

            List<AllocationClothingValidGoods> allocationClothingValidGoodsList = validGoodsList.stream().map(outOfStockGoodsDO -> {
                AllocationClothingValidGoods allocationClothingValidGoods = new AllocationClothingValidGoods();

                allocationClothingValidGoods.setTaskId(taskId);
                allocationClothingValidGoods.setShopId(outOfStockGoodsDO.getShopId());
                allocationClothingValidGoods.setShopName(outOfStockGoodsDO.getShopName());
                allocationClothingValidGoods.setMatCode(outOfStockGoodsDO.getMatCode());
                allocationClothingValidGoods.setMatName(outOfStockGoodsDO.getMatName());
                allocationClothingValidGoods.setSizeId(outOfStockGoodsDO.getSizeId());
                allocationClothingValidGoods.setStockQty(outOfStockGoodsDO.getStockQty());
                allocationClothingValidGoods.setPathStockQty(outOfStockGoodsDO.getPathStockQty());
                allocationClothingValidGoods.setApplyStockQty(outOfStockGoodsDO.getApplyStockQty());
                allocationClothingValidGoods.setAvgSaleQty(outOfStockGoodsDO.getAvgSaleQty());
                allocationClothingValidGoods.setAllocationType(type);
                if (outOfStockGoodsDO.getStoreQty() == 0 || outOfStockGoodsDO.getAvgSaleQty() == 0) {
                    allocationClothingValidGoods.setSaleDays(0D);
                } else {
                    BigDecimal storeQtyBD = new BigDecimal(outOfStockGoodsDO.getStoreQty());
                    BigDecimal avgSaleQtyBD = new BigDecimal(outOfStockGoodsDO.getAvgSaleQty());
                    allocationClothingValidGoods.setSaleDays(storeQtyBD.divide(avgSaleQtyBD,2,BigDecimal.ROUND_HALF_UP).doubleValue());
                }

                return allocationClothingValidGoods;
            }).collect(Collectors.toList());

            allocationClothingInvalidGoodsMapper.batchInsertValidGoods(allocationClothingValidGoodsList);
        }
    }

    private OutOfStockGoodsDO buildOutOfStockGoodsDO(AllocationStockDO stockDO) {
        OutOfStockGoodsDO newOutOfStockGoodsDO = new OutOfStockGoodsDO();
        newOutOfStockGoodsDO.setShopId(stockDO.getShopId());
        newOutOfStockGoodsDO.setCategoryCode(stockDO.getCategoryCode());
        newOutOfStockGoodsDO.setMatCode(stockDO.getMatCode());
        newOutOfStockGoodsDO.setSizeId(stockDO.getSizeId());
        newOutOfStockGoodsDO.setAvgSaleQty(stockDO.getAvgSaleQty());
        newOutOfStockGoodsDO.setStockQty(stockDO.getStockQty());
        newOutOfStockGoodsDO.setPathStockQty(stockDO.getPathStockQty());
        newOutOfStockGoodsDO.setApplyStockQty(stockDO.getAllocationStockQty());

        return newOutOfStockGoodsDO;
    }

    /**
     * 获取所有有库存的商品列表
     * @param shopIdSet
     * @return
     */
    private Map<String,List<OutOfStockGoodsDO>> getGoodMap(Set<String> shopIdSet,List<String> matCodeList,Set<String> categoryCodeSet) {

        Map<String,List<OutOfStockGoodsDO>> allShopOutStockGoodsMap = new HashMap<>();

        int offsize = 0;
        int size = 50; // 每次查50家门店

        while(true) {

            Set<String> subShopSet = shopIdSet.stream().limit(size).skip(size * offsize).collect(Collectors.toSet());

            if (subShopSet.isEmpty()) {
                break;
            }

            // 在售库存
            List<OutOfStockGoodsDO> ofStockGoodsDOList = allocationExtraDataMapper.getShopClothingList(subShopSet,matCodeList,categoryCodeSet);
            // 在配库存
            List<OutOfStockGoodsDO> applyList = allocationExtraDataMapper.getShopClothingApplyList(shopIdSet,matCodeList,categoryCodeSet);

            for (OutOfStockGoodsDO outOfStockGoodsDO : ofStockGoodsDOList) {
                for (OutOfStockGoodsDO applyStockGoodsDO : applyList) {
                    if (outOfStockGoodsDO.getKey().equals(applyStockGoodsDO.getKey())) {
                        outOfStockGoodsDO.setApplyStockQty(applyStockGoodsDO.getApplyStockQty());
                    }
                }
            }
            Map<String,List<OutOfStockGoodsDO>> subShopMap = ofStockGoodsDOList.parallelStream()
                    .collect(Collectors.groupingBy(stock->{return stock.getShopId()+":"+stock.getMatCode();},Collectors.toList()));

            allShopOutStockGoodsMap.putAll(subShopMap);
            offsize++;
        }

        return allShopOutStockGoodsMap;
    }

    private void calcInvalidSkc(List<OutOfStockGoodsDO> invalidGoodsList,List<AllocationGoodsSKC> allocationGoodsSKCList) {

        if (CollectionUtils.isNotEmpty(invalidGoodsList)) {
            // 按门店分组
            Map<String,List<OutOfStockGoodsDO>> shopGoodsMap = invalidGoodsList.stream().collect(Collectors.groupingBy(OutOfStockGoodsDO::getShopId));

            for (String shopId : shopGoodsMap.keySet()) {
                List<OutOfStockGoodsDO> outOfStockGoodsDOList = shopGoodsMap.get(shopId);
                // 男款数
                int maleStyleCount = (int)outOfStockGoodsDOList.stream()
                        .filter(outOfStockGoodsDO -> "M".equals(outOfStockGoodsDO.getCategoryCode()))
                        .map(OutOfStockGoodsDO::getMatCode).distinct().count();
                // 男件数
                int maleMum = outOfStockGoodsDOList.stream()
                        .filter(outOfStockGoodsDO -> "M".equals(outOfStockGoodsDO.getCategoryCode()))
                        .mapToInt(OutOfStockGoodsDO::getStoreQty).sum();

                // 女款数
                int femaleStyleCount = (int)outOfStockGoodsDOList.stream()
                        .filter(outOfStockGoodsDO -> "W".equals(outOfStockGoodsDO.getCategoryCode()))
                        .map(OutOfStockGoodsDO::getMatCode).distinct().count();
                // 女件数
                int femaleMum = outOfStockGoodsDOList.stream()
                        .filter(outOfStockGoodsDO -> "W".equals(outOfStockGoodsDO.getCategoryCode()))
                        .mapToInt(OutOfStockGoodsDO::getStoreQty).sum();

                AllocationGoodsSKC maleAllocationGoodsSKC = allocationGoodsSKCList.stream()
                        .filter(goodsSkc->goodsSkc.getShopId().equals(shopId) && goodsSkc.getCategoryCode().equals("M"))
                        .findFirst()
                        .orElse(null);
                AllocationGoodsSKC femaleAllocationGoodsSKC = allocationGoodsSKCList.stream()
                        .filter(goodsSkc->goodsSkc.getShopId().equals(shopId) && goodsSkc.getCategoryCode().equals("W"))
                        .findFirst()
                        .orElse(null);
                if (maleAllocationGoodsSKC != null) {
                    // 男装无效skc
                    maleAllocationGoodsSKC.setInvalidSkcStyle(maleStyleCount);
                    maleAllocationGoodsSKC.setInvaildSkcNum(maleMum);
                }
                if (femaleAllocationGoodsSKC != null) {
                    // 女装无效skc
                    femaleAllocationGoodsSKC.setInvalidSkcStyle(femaleStyleCount);
                    femaleAllocationGoodsSKC.setInvaildSkcNum(femaleMum);
                }
            }
        }
    }
}
