package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.vertical.QdIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * QdIssueRequireService
 *
 * @author Bruce01.fan
 * @date 2019/8/7
 */
@Service
public class QdIssueRequireService {

    private static Logger logger = LoggerFactory.getLogger(QdIssueRequireService.class);

    @Autowired
    QdIssueDOMapper qdIssueDOMapper;

    @Autowired
    QdIssueSizeScaleDOMapper qdIssueSizeScaleDOMapper;

    @Autowired
    QdIssueDepthSuggestDOMapper qdIssueDepthSuggestDOMapper;

    @Autowired
    DwsDimGoodsDOMapper dwsDimGoodsDOMapper;

    @Autowired
    QdIssueSkcListDOMapper qdIssueSkcListDOMapper;

    @Autowired
    QdIssueExtraDataMapper qdIssueExtraDataMapper;

    ExecutorService updateExecutor = Executors.newFixedThreadPool(4);

    @Autowired
    ForbiddenSingleItemDOMapper forbiddenSingleItemDOMapper;


    /**
     * 新品
     * 计算新品skc数
     * @param taskId
     * @param shopList
     * @param qdIssueDisplayDesginDOList
     * @return
     */
    public List<QdIssueNewSkcStockDO> calcNewSkcStock(int taskId,List<QdIssueShopListDO> shopList,List<QdIssueDisplayDesginDO> qdIssueDisplayDesginDOList,List<String> seasonList) {

        List<QdIssueNewSkcStockDO> qdIssueNewSkcStockDOList = new ArrayList<>();

        for (QdIssueShopListDO qdIssueShopListDO : shopList) {
            for (QdIssueDisplayDesginDO displayDesginDO : qdIssueDisplayDesginDOList) {
                if (qdIssueShopListDO.getShopCode().equals(displayDesginDO.getShopCode())) {
                    QdIssueNewSkcStockDO qdIssueNewSkcStockDO = new QdIssueNewSkcStockDO();

                    qdIssueNewSkcStockDO.setTaskId(taskId);
                    qdIssueNewSkcStockDO.setShopId(qdIssueShopListDO.getShopId());
                    // 新品建议skc数=门店总SKC数 * 新品建议陈列系数
                    qdIssueNewSkcStockDO.setNewSuggestSkc(Math.round(displayDesginDO.getClothingSkc() * displayDesginDO.getNewDisplayRatio()));

                    qdIssueNewSkcStockDOList.add(qdIssueNewSkcStockDO);
                }
            }
        }

        List<QdIssueNewSkcStockDO> shopSalesSkcList = qdIssueExtraDataMapper.getShopSalesList(seasonList);
        List<QdIssueNewSkcStockDO> shopPathSkcList = qdIssueExtraDataMapper.getShopPathList(seasonList);
        List<QdIssueNewSkcStockDO> shopApplySkcList = qdIssueExtraDataMapper.getShopApplyList(seasonList);
        List<QdIssueNewSkcStockDO> shopFbSkcList = forbiddenSingleItemDOMapper.getSkcForbiddenList(seasonList);

        Map<String,List<String>> salesSkcStockMap = shopSalesSkcList
                .parallelStream()
                .collect(Collectors.groupingBy(QdIssueNewSkcStockDO::getShopId,Collectors.mapping(QdIssueNewSkcStockDO::getMatCode,Collectors.toList())));
        // 在途
        Map<String,List<String>> pathSkcStockMap = shopPathSkcList
                .parallelStream()
                .collect(Collectors.groupingBy(QdIssueNewSkcStockDO::getShopId,Collectors.mapping(QdIssueNewSkcStockDO::getMatCode,Collectors.toList())));
        // 在配
        Map<String,List<String>> applySkcStockMap = shopApplySkcList
                .parallelStream()
                .collect(Collectors.groupingBy(QdIssueNewSkcStockDO::getShopId,Collectors.mapping(QdIssueNewSkcStockDO::getMatCode,Collectors.toList())));
        // 禁配
        Map<String,List<String>> fbSkcStockMap = shopFbSkcList
                .parallelStream()
                .collect(Collectors.groupingBy(QdIssueNewSkcStockDO::getShopId,Collectors.mapping(QdIssueNewSkcStockDO::getMatCode,Collectors.toList())));

        // 剔除禁配的skc
        this.kickFbSkc(salesSkcStockMap,fbSkcStockMap);
        this.kickFbSkc(pathSkcStockMap,fbSkcStockMap);
        this.kickFbSkc(applySkcStockMap,fbSkcStockMap);

        // 过滤重复的skc
        Map<String,Integer> newPathSkcStockMap = this.filterDuplicate(salesSkcStockMap,pathSkcStockMap);
        Map<String,Integer> newApplySkcStockMap = this.filterDuplicate(salesSkcStockMap,applySkcStockMap);

        for (QdIssueNewSkcStockDO qdIssueSkcStockDO : qdIssueNewSkcStockDOList) {
            String key = qdIssueSkcStockDO.getShopId();

            // 在售skc数量
            if (salesSkcStockMap.containsKey(key)) {
                qdIssueSkcStockDO.setShopSalesSkc(salesSkcStockMap.get(key).size());
            }
            // 在途
            if (newPathSkcStockMap.containsKey(key)) {
                qdIssueSkcStockDO.setShopPathSkc(newPathSkcStockMap.get(key));
            }
            // 在配
            if (newApplySkcStockMap.containsKey(key)) {
                qdIssueSkcStockDO.setShopApplySkc(newApplySkcStockMap.get(key));
            }
        }

        return  qdIssueNewSkcStockDOList;
    }

    /**
     * 老品
     * 计算skc建议占比、建议量
     * 找出有需求的门店列表
     */
    public List<QdIssueSkcStockDO> calcSkcStock(int taskId,List<QdIssueShopListDO> shopList,List<QdIssueDisplayDesginDO> qdIssueDisplayDesginDOList,List<QdIssueCategoryStructureDO> qdIssueCategoryStructureDOList,List<String> seasonList) {

        List<QdIssueSkcStockDO> qdIssueSkcStockDOList = new ArrayList<>();

        // 生成中类初始化数据
        for (QdIssueShopListDO qdIssueShopListDO : shopList) {
            for (QdIssueCategoryStructureDO qdIssueCategoryStructureDO : qdIssueCategoryStructureDOList) {

                // 匹配同区域的
                if (qdIssueShopListDO.getRegionName().equals(qdIssueCategoryStructureDO.getRegionName())) {
                    QdIssueSkcStockDO qdIssueSkcStockDO = new QdIssueSkcStockDO();

                    qdIssueSkcStockDO.setTaskId(taskId); // 任务id
                    qdIssueSkcStockDO.setShopId(qdIssueShopListDO.getShopId());
                    qdIssueSkcStockDO.setCategoryName(qdIssueCategoryStructureDO.getCategoryName());
                    qdIssueSkcStockDO.setMidCategoryName(qdIssueCategoryStructureDO.getMidCategoryName());

                    qdIssueSkcStockDOList.add(qdIssueSkcStockDO);
                }
            }
        }

        // 门店中类在售、在配、在途库存总量
        List<QdIssueSkcStockDO> salesSkcStockDOList = qdIssueExtraDataMapper.getMidCategorySalesList(seasonList);
        List<QdIssueSkcStockDO> pathSkcStockDOList =qdIssueExtraDataMapper.getMidCategoryPathList(seasonList);
        List<QdIssueSkcStockDO> applySkcStockDOList =qdIssueExtraDataMapper.getMidCategoryApplyList(seasonList);
        // 禁配skc数
        List<QdIssueSkcStockDO> fbSkcStockDOList = forbiddenSingleItemDOMapper.getMidCategoryForbiddenList(seasonList);
        // 新品已分配skc数
        List<QdIssueSkcStockDO> newQdIssueSkcStockDOList = qdIssueDOMapper.getNewGoodsHadIssueSkc(taskId);


        // 在售
        Map<String,List<String>> salesSkcStockMap = salesSkcStockDOList
                .parallelStream()
                .collect(Collectors.groupingBy(stock-> {return stock.getShopId()+":"+stock.getMidCategoryName();},Collectors.mapping(QdIssueSkcStockDO::getMatCode,Collectors.toList())));
        // 在途
        Map<String,List<String>> pathSkcStockMap = pathSkcStockDOList
                .parallelStream()
                .collect(Collectors.groupingBy(stock-> {return stock.getShopId()+":"+stock.getMidCategoryName();},Collectors.mapping(QdIssueSkcStockDO::getMatCode,Collectors.toList())));
        // 在配
        Map<String,List<String>> applySkcStockMap = applySkcStockDOList
                .parallelStream()
                .collect(Collectors.groupingBy(stock-> {return stock.getShopId()+":"+stock.getMidCategoryName();},Collectors.mapping(QdIssueSkcStockDO::getMatCode,Collectors.toList())));
        // 禁配
        Map<String,List<String>> fbSkcStockMap = fbSkcStockDOList
                .parallelStream()
                .collect(Collectors.groupingBy(stock-> {return stock.getShopId()+":"+stock.getMidCategoryName();},Collectors.mapping(QdIssueSkcStockDO::getMatCode,Collectors.toList())));

        // 剔除禁配的skc
        this.kickFbSkc(salesSkcStockMap,fbSkcStockMap);
        this.kickFbSkc(pathSkcStockMap,fbSkcStockMap);
        this.kickFbSkc(applySkcStockMap,fbSkcStockMap);

        // 过滤重复的skc
        // 过滤在售
        Map<String,Integer> newPathSkcStockMap = this.filterDuplicate(salesSkcStockMap,pathSkcStockMap);
        // 过滤在售、在途的skc
        Map<String,Integer> newApplySkcStockMap = this.filterDuplicate(salesSkcStockMap,applySkcStockMap);

        for (QdIssueSkcStockDO qdIssueSkcStockDO : qdIssueSkcStockDOList) {
            String key = qdIssueSkcStockDO.getShopId() +":" + qdIssueSkcStockDO.getMidCategoryName();

            // 在售skc数量
            if (salesSkcStockMap.containsKey(key)) {
                qdIssueSkcStockDO.setMidCategorySalesSkc(salesSkcStockMap.get(key).size());
            }

            // 在途
            if (newPathSkcStockMap.containsKey(key)) {
                qdIssueSkcStockDO.setMidCategoryPathSkc(newPathSkcStockMap.get(key));
            }

            // 在配
            if (newApplySkcStockMap.containsKey(key)) {
                qdIssueSkcStockDO.setMidCategoryApplySkc(newApplySkcStockMap.get(key));
            }


            // 老品占比
            for (QdIssueShopListDO shop : shopList) {

                if (shop.getShopId().equals(qdIssueSkcStockDO.getShopId())) {
                    qdIssueSkcStockDO.setShopCode(shop.getShopCode());
                    qdIssueSkcStockDO.setRegionName(shop.getRegionName());
                    qdIssueSkcStockDO.setOldSkcPercentageSuggest(shop.getOldSkcPercentageSuggest());
                }
            }
        }

        // 新品已分配skc数匹配
        for (QdIssueSkcStockDO qdIssueSkcStockDO : qdIssueSkcStockDOList) {
            String key = qdIssueSkcStockDO.getShopId() +":" + qdIssueSkcStockDO.getMidCategoryName();

            for (QdIssueSkcStockDO newQdIssueSkcStockDO : newQdIssueSkcStockDOList) {
                String newKey = newQdIssueSkcStockDO.getShopId() +":" + newQdIssueSkcStockDO.getMidCategoryName();

                if (key.equals(newKey)) {
                    qdIssueSkcStockDO.setNewGoodsHadIssueSkc(newQdIssueSkcStockDO.getNewGoodsHadIssueSkc());
                }
            }
        }

        // 陈列量
        for (QdIssueSkcStockDO qdIssueSkcStockDO : qdIssueSkcStockDOList) {
            for (QdIssueDisplayDesginDO displayDesginDO : qdIssueDisplayDesginDOList) {

                if (qdIssueSkcStockDO.getShopCode().equals(displayDesginDO.getShopCode())) {
                    qdIssueSkcStockDO.setMaleStandardSkc(displayDesginDO.getMaleStandardSkc()); // 男装skc建议陈列
                    qdIssueSkcStockDO.setFemaleStandardSkc(displayDesginDO.getFemaleStandardSkc()); // 女装skc建议陈列
                    qdIssueSkcStockDO.setDisplayRatio(displayDesginDO.getDisplayRatio()); // 陈列系数
                }
            }
        }

        qdIssueSkcStockDOList = qdIssueSkcStockDOList.stream().filter(qdIssueSkcStockDO -> qdIssueSkcStockDO.getShopCode() != null).collect(Collectors.toList());

        //  中类skc数占比
        for (QdIssueSkcStockDO qdIssueSkcStockDO : qdIssueSkcStockDOList) {
            for (QdIssueCategoryStructureDO qdIssueCategoryStructureDO : qdIssueCategoryStructureDOList) {

                if (qdIssueSkcStockDO.getMidCategoryName().equals(qdIssueCategoryStructureDO.getMidCategoryName())
                        && qdIssueSkcStockDO.getRegionName().contains(qdIssueCategoryStructureDO.getRegionName())) {
                    qdIssueSkcStockDO.setCategoryName(qdIssueCategoryStructureDO.getCategoryName());
                    qdIssueSkcStockDO.setMidCategorySuggestSkcPercent(qdIssueCategoryStructureDO.getSkcPercent());

                }
            }
        }
        return qdIssueSkcStockDOList;

    }

    /**
     * 新品
     * 计算sku的理想库存、实际库存
     * @param taskId
     * @param issueShopList
     * @param demandSkcList
     * @param qdIssueSkcListDOList
     * @return
     */
    public List<QdIssueInStockDO> calcNewSkuRequirement(int taskId,List<QdIssueShopListDO> issueShopList,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<QdIssueNewSkcStockDO> demandSkcList) {

        List<QdIssueInStockDO> allIssueInStockList = new ArrayList<>();

        // skc需求深度
        List<QdIssueDepthSuggestDO> qdIssueCategoryStructureDOList = qdIssueDepthSuggestDOMapper.queryAll();
        // 尺码比例
        List<QdIssueSizeScaleDO> qdIssueSizeScaleDOList = qdIssueSizeScaleDOMapper.getSizeScaleList();
        // 需求门店列表
        List<String> demandShopIdList = null;
        if (demandSkcList == null) {
            demandShopIdList = issueShopList.stream().map(QdIssueShopListDO::getShopId).collect(Collectors.toList());
        } else {
            demandShopIdList = demandSkcList.stream().map(QdIssueNewSkcStockDO::getShopId).collect(Collectors.toList());
        }

        // 门店sku列表数据
        List<QdIssueInStockDO> shopIssueInStockList = qdIssueDOMapper.getNewInStockData(taskId, demandShopIdList);

        Map<String,List<QdIssueInStockDO>> shopNewInStockList = shopIssueInStockList.stream()
                                                                .collect(Collectors.groupingBy(QdIssueInStockDO::getShopId));

        for (String shopId : shopNewInStockList.keySet()) {

            List<QdIssueInStockDO> newInStockList = shopNewInStockList.get(shopId);

            // 填充属性
            this.fillInStockProperties(newInStockList,qdIssueSkcListDOList,issueShopList,qdIssueCategoryStructureDOList);

            // 计算需求量
            this.calcRequireQty(newInStockList,qdIssueSizeScaleDOList);

            allIssueInStockList.addAll(newInStockList);
        }

        // 更新需求量字段
        updateExecutor.submit(()->{
            qdIssueDOMapper.batchUpdateInStockRequirement(allIssueInStockList);
            return "SUCC";
        });

        // 将无需求的数据过滤掉，直接返回需要配发的列表数据
        return allIssueInStockList.parallelStream().filter(qdIssueInStockDO -> qdIssueInStockDO.getDemandQty()>0).collect(Collectors.toList());
    }




    /**
     * 老品
     * 计算sku的理想库存、实际库存
     */
    public List<QdIssueInStockDO> calcSkuRequirement(int taskId,List<QdIssueShopListDO> issueShopList,List<QdIssueSkcStockDO> demandSkcList,List<QdIssueSkcListDO> qdIssueSkcListDOList) {

        // skc需求深度
        List<QdIssueDepthSuggestDO> qdIssueCategoryStructureDOList = qdIssueDepthSuggestDOMapper.queryAll();

        // 尺码比例
        List<QdIssueSizeScaleDO> qdIssueSizeScaleDOList = qdIssueSizeScaleDOMapper.getSizeScaleList();

        //
        Map<String,List<String>> shopMidCategoryMap = demandSkcList.stream().collect(Collectors.groupingBy(QdIssueSkcStockDO::getShopId,Collectors.mapping(QdIssueSkcStockDO::getMidCategoryName,Collectors.toList())));

        List<QdIssueInStockDO> allIssueInStockList = new ArrayList<>();

        for (String shopId : shopMidCategoryMap.keySet()) {

            List<String> midCategoryNameList = shopMidCategoryMap.get(shopId);

            List<String> shopIdList = Stream.of(shopId).collect(Collectors.toList());
            List<String> subMatCodeList = qdIssueSkcListDOMapper.getMatCodeByMidCategory(midCategoryNameList);


            // 门店sku列表数据
            List<QdIssueInStockDO> shopIssueInStockList = qdIssueDOMapper.getInStockData(taskId, shopIdList,subMatCodeList);

            // 填充属性
            this.fillInStockProperties(shopIssueInStockList,qdIssueSkcListDOList,issueShopList,qdIssueCategoryStructureDOList);

            // 计算需求量
            this.calcRequireQty(shopIssueInStockList,qdIssueSizeScaleDOList);

            // 更新需求量字段
            updateExecutor.submit(()->{
                qdIssueDOMapper.batchUpdateInStockRequirement(shopIssueInStockList);
                return "SUCC";
            });

            allIssueInStockList.addAll(shopIssueInStockList);

        }

        // 将无需求的数据过滤掉，直接返回需要配发的列表数据
        return allIssueInStockList.parallelStream().filter(qdIssueInStockDO -> qdIssueInStockDO.getDemandQty()>0).collect(Collectors.toList());
    }


    /**
     * 剔除禁配规则
     * @param skcStockMap
     * @param fbSkcStockMap
     */
    private void kickFbSkc(Map<String,List<String>> skcStockMap,Map<String,List<String>> fbSkcStockMap) {
         for (String key : skcStockMap.keySet()) {
             List<String> matCodeList = skcStockMap.get(key);
             List<String> fbMatCodeList =fbSkcStockMap.get(key);

             if (fbMatCodeList != null) {

                 List<String> newMatCodeList = matCodeList.stream().filter(matCode -> !fbMatCodeList.contains(matCode)).collect(Collectors.toList());

                 if (matCodeList.size() != newMatCodeList.size()) {
                     LoggerUtil.info(logger, "[KICK_FB_SKC] msg=old:{0},new:{1}", matCodeList.size(), newMatCodeList.size());
                     skcStockMap.put(key, newMatCodeList);
                 }
             }

         }
    }

    /**
     * 过滤重复的在途，在配
     * @param salesSkcStockMap
     * @param otherSkcStockMap
     * @return
     */
    private Map<String,Integer> filterDuplicate(Map<String,List<String>> salesSkcStockMap,Map<String,List<String>> otherSkcStockMap) {
        Map<String,Integer> skcStockMap = new HashMap<>();

        for (String key : otherSkcStockMap.keySet()) {

            if (salesSkcStockMap.containsKey(key)) {
                List<String> salesMatCodeList = salesSkcStockMap.get(key);
                List<String> otherMatCodeList = otherSkcStockMap.get(key);

                int skcCount = otherMatCodeList.size();
                int mixCount = (int) salesMatCodeList.stream().filter(matCode -> otherMatCodeList.contains(matCode)).count();

                if (mixCount > 0) {
                    skcStockMap.put(key, skcCount - mixCount);
                } else {
                    skcStockMap.put(key, skcCount);
                }
            }
        }

        return skcStockMap;
    }

    /**
     * 设置instock的相关属性
     * @param shopIssueInStockList
     * @param qdIssueSkcListDOList
     * @param issueShopList
     * @param qdIssueDepthSuggestDOList
     */
    private void fillInStockProperties(List<QdIssueInStockDO> shopIssueInStockList,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<QdIssueShopListDO> issueShopList,List<QdIssueDepthSuggestDO> qdIssueDepthSuggestDOList) {
        shopIssueInStockList.parallelStream().forEach(qdIssueInStockDO->{

            // 商品信息
            /*for (QdIssueSkcListDO qdIssueSkcListDO : qdIssueSkcListDOList) {
                if (qdIssueInStockDO.getMatCode().equals(qdIssueSkcListDO.getMatCode())) {
                    qdIssueInStockDO.setCategoryName(qdIssueSkcListDO.getCategoryName());
                    qdIssueInStockDO.setMidCategoryName(qdIssueSkcListDO.getMidCategoryName());

                    continue;
                }
            }*/


            // 匹配门店大区-areaName
            for (QdIssueShopListDO qdIssueShopListDO : issueShopList) {

                if (qdIssueInStockDO.getShopId().equals(qdIssueShopListDO.getShopId())) {
                    qdIssueInStockDO.setAreaName(qdIssueShopListDO.getAreaName());
                    qdIssueInStockDO.setRegionName(qdIssueShopListDO.getRegionName());
                    qdIssueInStockDO.setProvinceName(qdIssueShopListDO.getProvinceName());
                    qdIssueInStockDO.setBusinessLevel(qdIssueShopListDO.getBusinessLevel());

                    continue;
                }
            }

            // 内外搭
            for (QdIssueSkcListDO qdIssueSkcListDO : qdIssueSkcListDOList) {
                if (qdIssueInStockDO.getMatCode().equals(qdIssueSkcListDO.getMatCode())) {
                    qdIssueInStockDO.setMatName(qdIssueSkcListDO.getMatName());
                    qdIssueInStockDO.setMatchType(qdIssueSkcListDO.getMatchType());
                    qdIssueInStockDO.setModelType(qdIssueSkcListDO.getModelType());

                    continue;
                }
            }

        });

        // 匹配需求深度

        List<QdIssueDepthSuggestDO> matCodeDeptSuggestList = qdIssueDepthSuggestDOList.stream().filter(qdIssueDepthSuggestDO -> "货号".equals(qdIssueDepthSuggestDO.getTypeName())).collect(Collectors.toList());
        List<QdIssueDepthSuggestDO> midDeptSuggestList = qdIssueDepthSuggestDOList.stream().filter(qdIssueDepthSuggestDO -> "中类".equals(qdIssueDepthSuggestDO.getTypeName())).collect(Collectors.toList());
        List<QdIssueDepthSuggestDO> matchDeptSuggestList = qdIssueDepthSuggestDOList.stream().filter(qdIssueDepthSuggestDO -> "内外搭".equals(qdIssueDepthSuggestDO.getTypeName())).collect(Collectors.toList());

        for (QdIssueInStockDO qdIssueInStockDO : shopIssueInStockList) {
            if (qdIssueInStockDO.getMatchType() == null) {continue;}

            QdIssueDepthSuggestDO qdIssueDepthSuggest = matCodeDeptSuggestList.stream().filter(qdIssueDepthSuggestDO->qdIssueInStockDO.getMatCode().equals(qdIssueDepthSuggestDO.getMatchType())
                    && qdIssueInStockDO.getBusinessLevel() == qdIssueDepthSuggestDO.getLevel().intValue())
                    .findFirst().orElse(null);

            if (qdIssueDepthSuggest != null) {
                qdIssueInStockDO.setDepth((double) qdIssueDepthSuggest.getDepth());
                LoggerUtil.debug(logger,"[SKC_DEPTH] msg = type:{0},instock:{1}",qdIssueDepthSuggest.getMatchType(),qdIssueInStockDO);
                continue;
            }

            qdIssueDepthSuggest = midDeptSuggestList.stream().filter(qdIssueDepthSuggestDO->qdIssueInStockDO.getMidCategoryName().equals(qdIssueDepthSuggestDO.getMatchType())
                    && qdIssueInStockDO.getBusinessLevel() == qdIssueDepthSuggestDO.getLevel().intValue())
                    .findFirst().orElse(null);

            if (qdIssueDepthSuggest != null) {
                qdIssueInStockDO.setDepth((double) qdIssueDepthSuggest.getDepth());
                LoggerUtil.debug(logger,"[SKC_DEPTH] msg = type:{0},instock:{1}",qdIssueDepthSuggest.getMatchType(),qdIssueInStockDO);
                continue;
            }

            qdIssueDepthSuggest = matchDeptSuggestList.stream().filter(qdIssueDepthSuggestDO->qdIssueInStockDO.getMatchType().equals(qdIssueDepthSuggestDO.getMatchType())
                    && qdIssueInStockDO.getBusinessLevel() == qdIssueDepthSuggestDO.getLevel().intValue())
                    .findFirst().orElse(null);


            if (qdIssueDepthSuggest != null) {
                qdIssueInStockDO.setDepth((double) qdIssueDepthSuggest.getDepth());
                LoggerUtil.debug(logger,"[SKC_DEPTH] msg = type:{0},instock:{1}",qdIssueDepthSuggest.getMatchType(),qdIssueInStockDO);
                continue;
            }
        }

        LoggerUtil.debug(logger,"[SKC_DEPTH] msg = finish");
    }

    private void calcRequireQty(List<QdIssueInStockDO> shopIssueInStockList,List<QdIssueSizeScaleDO> qdIssueSizeScaleDOList) {
        // 计算理论需求、实际需求
        for (QdIssueInStockDO qdIssueInStockDO : shopIssueInStockList) {

            for (QdIssueSizeScaleDO qdIssueSizeScaleDO : qdIssueSizeScaleDOList) {

                try {
                        /*
                         *  毛织类商品需要进行特殊处理
                         *  商品名称(男装毛织T恤) ，尺码比例（大类：男装 中类：毛织T恤）
                          */

                    String catName = qdIssueSizeScaleDO.getCategoryName()+qdIssueSizeScaleDO.getMidCategoryName();
                    if ( (qdIssueInStockDO.getAreaName().equals(qdIssueSizeScaleDO.getAreaName())
                            && qdIssueInStockDO.getMidCategoryName().contains("毛织")
                            && qdIssueInStockDO.getMatName().equals(catName)
                            && qdIssueInStockDO.getSizeName().equals(qdIssueSizeScaleDO.getSizeName())
                            && qdIssueInStockDO.getModelType().equals(qdIssueSizeScaleDO.getModelType())
                    )
                            ||
                            (qdIssueInStockDO.getAreaName().equals(qdIssueSizeScaleDO.getAreaName())
                                    && qdIssueInStockDO.getCategoryName().equals(qdIssueSizeScaleDO.getCategoryName())
                                    && qdIssueInStockDO.getMidCategoryName().equals(qdIssueSizeScaleDO.getMidCategoryName())
                                    && qdIssueInStockDO.getSizeName().equals(qdIssueSizeScaleDO.getSizeName())
                                    && qdIssueInStockDO.getModelType().equals(qdIssueSizeScaleDO.getModelType())
                            )
                            ) {

                        // 理论需求 = 深度 * 比例
                        long idealQty = Math.round(qdIssueInStockDO.getDepth() * qdIssueSizeScaleDO.getPercentage());
                        long demandQty = 0;
                        long totalQty = qdIssueInStockDO.getStockQty() + qdIssueInStockDO.getPathQty() + qdIssueInStockDO.getApplyQty();
                        if (idealQty > 0 && idealQty > totalQty) {
                            demandQty = idealQty - totalQty;
                        }

                        qdIssueInStockDO.setIdealQty(idealQty);
                        qdIssueInStockDO.setDemandQty(demandQty);
                    }
                    continue;
                } catch (Exception e) {
                    LoggerUtil.error(logger,"[ERROR] e:{0}",e.getMessage());
                }
            }
        }
    }
}
