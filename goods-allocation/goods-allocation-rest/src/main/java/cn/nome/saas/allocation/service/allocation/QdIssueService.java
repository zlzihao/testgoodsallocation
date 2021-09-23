package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.mapper.QdIssueDataMapper;
import cn.nome.saas.allocation.model.allocation.QdIssueConfig;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.vertical.QdIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.rule.QdIssueRequireService;
import cn.nome.saas.allocation.service.rule.WhiteListRuleService;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 秋冬老品配发
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
@Service
public class QdIssueService {

    private static Logger logger = LoggerFactory.getLogger(QdIssueService.class);

    @Autowired
    QdIssueTaskService qdIssueTaskService;

    @Autowired
    QdIssueRequireService qdIssueRequireService;

    @Autowired
    WhiteListRuleService whiteListRuleService;

    @Autowired
    QdIssueSkcListDOMapper qdIssueSkcListDOMapper;

    @Autowired
    QdIssueExtraDataMapper qdIssueExtraDataMapper;

    @Autowired
    QdIssueDOMapper qdIssueDOMapper;

    @Autowired
    QdIssueShopListDOMapper qdIssueShopListDOMapper;

    @Autowired
    QdIssueDisplayDesginDOMapper qdIssueDisplayDesginDOMapper;

    @Autowired
    QdIssueCategoryStructureDOMapper qdIssueCategoryStructureDOMapper;

    @Autowired
    QdIssueDetailDOMapper qdIssueDetailDOMapper;

    @Autowired
    DwsDimGoodsDOMapper dwsDimGoodsDOMapper;

    ExecutorService insertExecutor = Executors.newFixedThreadPool(4);

    /**
     * 秋冬老品配发入口
     */
    public void qdIssueTask(int taskId) {

        QdIssueConfig qdIssueConfig = qdIssueTaskService.getConfig();

        List<String> seasonList = null;
        if (qdIssueConfig.getSeason() != null) {
            seasonList =  Stream.of(qdIssueConfig.getSeason().split(",")).collect(Collectors.toList());
        }

        List<String> matCodeList = qdIssueSkcListDOMapper.getMatCodelist(seasonList);

        List<QdIssueShopListDO> issueShopList = qdIssueShopListDOMapper.getShopList();

        List<QdIssueSkcListDO> qdIssueSkcListDOList = qdIssueSkcListDOMapper.getSkclistByOrder();

        List<WhiteListSingleItemDO> whileList = whiteListRuleService.getQdIssueWhitelist(); // 白名单列表

        List<String> shopIdList = issueShopList.stream().map(QdIssueShopListDO::getShopId).collect(Collectors.toList());


        if (CollectionUtils.isEmpty(matCodeList)) {
            return;
        }

        long start = System.currentTimeMillis();

        // 情况原有的数据
        qdIssueDOMapper.deleteInStock(taskId);
        qdIssueDOMapper.deleteOutStock(taskId);
        qdIssueDOMapper.deleteSkcStock(taskId);
        qdIssueDOMapper.deleteNewSkcStock(taskId);
        qdIssueDetailDOMapper.deleteIssueDetail(taskId);

        // 供给池
        this.processOutStock(taskId,matCodeList,qdIssueSkcListDOList);

        LoggerUtil.info(logger,"[QD_ISSUE] msg = out stock. time:{0}",(System.currentTimeMillis() - start) / 1000);

        start = System.currentTimeMillis();
        // 需求池
        this.processInStock(taskId,matCodeList,shopIdList,qdIssueSkcListDOList,whileList);

        LoggerUtil.info(logger,"[QD_ISSUE] msg = in stock. time:{0}",(System.currentTimeMillis() - start)/ 1000);

        start = System.currentTimeMillis();

        /*****************
         *  新品配发
         *****************/
        List<QdIssueSkcListDO>  newSkcList = qdIssueSkcListDOList.stream().filter(qdIssueSkcListDO -> qdIssueSkcListDO.getIsNews() == 1).collect(Collectors.toList());
        // 计算新品需求量
        List<QdIssueInStockDO> qdIssueInStockDOList = this.processNewRequirement(taskId,issueShopList,newSkcList,qdIssueConfig);
        // 新品配发
        this.newIssueMatch(taskId,issueShopList,newSkcList,qdIssueInStockDOList,qdIssueConfig);

        /*****************
         *  老品配发
         *****************/
        List<QdIssueSkcListDO>  oldSkcList = qdIssueSkcListDOList.stream().filter(qdIssueSkcListDO -> qdIssueSkcListDO.getIsNews() == 0).collect(Collectors.toList());

        // 计算需求量
        List<QdIssueInStockDO> allQdIssueInStockList = this.processRequirement(taskId,issueShopList,oldSkcList,qdIssueConfig);

        LoggerUtil.info(logger,"[QD_ISSUE] msg = requirement. time:{0}",(System.currentTimeMillis() - start)/ 1000);

        start = System.currentTimeMillis();

        // 需求匹配
        this.issueMatch(taskId,issueShopList,qdIssueSkcListDOList,allQdIssueInStockList);

        LoggerUtil.info(logger,"[QD_ISSUE] msg = match. time:{0}",(System.currentTimeMillis() - start)/ 1000);
    }

    /**
     * 供给池
     * @param taskId
     * @param matCodeList
     */
    public void processOutStock(int taskId,List<String> matCodeList,List<QdIssueSkcListDO> qdIssueSkcListDOList) {

        List<QdIssueOutStockDO> outStockDOList = qdIssueExtraDataMapper.getQdIssueOutStockList(matCodeList);

        int size = 5000;
        int offset = 0;

        if (CollectionUtils.isNotEmpty(outStockDOList)) {

            outStockDOList.forEach(qdIssueOutStockDO -> {
                qdIssueOutStockDO.setTaskId(taskId);

                for (QdIssueSkcListDO qdIssueSkcListDO :qdIssueSkcListDOList) {
                    if (qdIssueOutStockDO.getMatCode().equals(qdIssueSkcListDO.getMatCode())) {
                        qdIssueOutStockDO.setCategoryName(qdIssueSkcListDO.getCategoryName());
                        qdIssueOutStockDO.setMidCategoryName(qdIssueSkcListDO.getMidCategoryName());
                    }
                }

            });

            while (true) {
                List<QdIssueOutStockDO> subList = outStockDOList.stream().skip(size * offset).limit(size).collect(Collectors.toList());

                if (subList.isEmpty()){break;}

                int succCnt = qdIssueDOMapper.batchInsertOutStock(subList);
                offset++;
            }
        }
    }

    /**
     * 需求池
     * @param taskId
     * @param matCodeList
     */
    public void processInStock(int taskId,List<String> matCodeList,List<String> shopIdList,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<WhiteListSingleItemDO> whiteList) {

        List<QdGoodsInfoDO> goodsInfoDOList = qdIssueExtraDataMapper.getGoodsInfo(matCodeList);

        List<QdIssueInStockDO> issueInStockDOList = qdIssueExtraDataMapper.getQdIssueInStockList(matCodeList,shopIdList);
        List<QdIssueInStockDO> issueApplyQtyList =qdIssueExtraDataMapper.getQdApplyQtyList(matCodeList);

        List<Future<String>> futureList = new ArrayList<>();
        // 根据商品信息+门店信息，初始化需求池数据
        //List<QdIssueInStockDO> allIssueInStockDOList = new ArrayList<>();
        for (String shopId : shopIdList) {
            List<QdIssueInStockDO> allIssueInStockDOList = new ArrayList<>();

            for (QdGoodsInfoDO goodsInfoDO :goodsInfoDOList) {

                // 判断是否在白名单中
                boolean access = true;
                if (whiteList != null) {

                   List<String> shoplist = whiteList.parallelStream()
                            .filter(item->item.getTypeValue().equals(goodsInfoDO.getMatCode()))
                            .map(WhiteListSingleItemDO::getShopId)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(shoplist)) {
                        if (shoplist.stream().filter(shop->shop.equals(shopId)).count() <=0) {
                            access = false;
                            LoggerUtil.info(logger,"[HIT_QD_WHITE_LIST] msg = shop:{0},matcode:{1}",shopId,goodsInfoDO.getMatCode());
                        }
                    }

                }

                if (access) {
                    QdIssueInStockDO qdIssueInStockDO = new QdIssueInStockDO();
                    qdIssueInStockDO.setTaskId(taskId);
                    qdIssueInStockDO.setShopId(shopId);
                    qdIssueInStockDO.setMatCode(goodsInfoDO.getMatCode());
                    qdIssueInStockDO.setSizeId(goodsInfoDO.getSizeId());
                    qdIssueInStockDO.setSizeName(goodsInfoDO.getSizeName());

                    allIssueInStockDOList.add(qdIssueInStockDO);
                }
            }

            if (CollectionUtils.isEmpty(allIssueInStockDOList)) {
                LoggerUtil.warn(logger,"[EMPTY_SHOP] msg=shop:{0}",shopId);
                continue;
            }

            List<String> inStockKeys = allIssueInStockDOList.stream().map(QdIssueInStockDO::getKey).collect(Collectors.toList());
            List<QdIssueInStockDO> newIssueInStockDOList = issueInStockDOList.parallelStream().filter(qdIssueSalesInStockDO-> inStockKeys.contains(qdIssueSalesInStockDO.getKey())).collect(Collectors.toList());

            // 异步执行
            Future future = insertExecutor.submit(()->{

                long start = System.currentTimeMillis();

                allIssueInStockDOList.stream().forEach(qdIssueInStockDO-> {

                    // 品类
                    QdIssueSkcListDO qdIssueSkcListDO = qdIssueSkcListDOList.stream()
                            .filter(qdIssueSkcDO->qdIssueInStockDO.getMatCode().equals(qdIssueSkcDO.getMatCode()))
                            .findFirst()
                            .orElse(null);

                    if (qdIssueSkcListDO != null) {
                        qdIssueInStockDO.setCategoryName(qdIssueSkcListDO.getCategoryName());
                        qdIssueInStockDO.setMidCategoryName(qdIssueSkcListDO.getMidCategoryName());
                        qdIssueInStockDO.setIsNews(qdIssueSkcListDO.getIsNews());
                    }

                    // 匹配有销售记录的门店库存
                    QdIssueInStockDO qdIssueSalesInStockDO = newIssueInStockDOList.stream()
                            .filter(qdIssuesalesDO->qdIssueInStockDO.getKey().equals(qdIssuesalesDO.getKey()))
                            .findFirst()
                            .orElse(null);

                    if (qdIssueSalesInStockDO != null) {
                        qdIssueInStockDO.setStockQty(qdIssueSalesInStockDO.getStockQty());
                        qdIssueInStockDO.setPathQty(qdIssueSalesInStockDO.getPathQty());

                    }

                    // 匹配在途库存
                    QdIssueInStockDO qdIssueApplyQty = issueApplyQtyList.stream()
                            .filter(qdIssueApplyDO->qdIssueInStockDO.getKey().equals(qdIssueApplyDO.getKey()))
                            .findFirst()
                            .orElse(null);

                    if (qdIssueApplyQty != null) {
                        qdIssueInStockDO.setApplyQty(qdIssueApplyQty.getApplyQty());
                    }

                });

                // 插入
                qdIssueDOMapper.batchInsertInStock(allIssueInStockDOList);

                long end = System.currentTimeMillis();

                LoggerUtil.info(logger,"[CALC_QTY] msg=time:{0},shop:{1},size:{2}",(end - start) / 1000,allIssueInStockDOList.get(0).getShopId(),allIssueInStockDOList.size());

                return "SUCC";
            });

            futureList.add(future);
        }

        for (Future future : futureList) {
            try {
                future.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }

        LoggerUtil.info(logger,"[QD_ISSUE_INSTOCK_DONE] msg = finish");

    }

    /**
     * 新品需求量计算
     */
    public List<QdIssueInStockDO> processNewRequirement(int taskId, List<QdIssueShopListDO> issueShopList, List<QdIssueSkcListDO> qdIssueSkcListDOList, QdIssueConfig qdIssueConfig) {

        List<QdIssueDisplayDesginDO> qdIssueDisplayDesginDOList = qdIssueDisplayDesginDOMapper.getShopDisplayDesignList();

        List<QdIssueNewSkcStockDO> demandSkcList = null;

        // 满场率设置为"是"
        if (qdIssueConfig != null && "是".equals(qdIssueConfig.getFullRate())) {
            // 计算skc数
            List<String> seasonList = null;
            if (qdIssueConfig.getSeason() != null) {
                seasonList =  Stream.of(qdIssueConfig.getSeason().split(",")).collect(Collectors.toList());
            }
            List<QdIssueNewSkcStockDO> qdIssueNewSkcStockDOList = qdIssueRequireService.calcNewSkcStock(taskId, issueShopList, qdIssueDisplayDesginDOList, seasonList);

            if (CollectionUtils.isEmpty(qdIssueNewSkcStockDOList)) {
                return null;
            }

            // 落库（同步插入）
            qdIssueDOMapper.batchInsertNewSkcStock(qdIssueNewSkcStockDOList);

            // 返回有需求的门店列表
            demandSkcList = qdIssueNewSkcStockDOList.stream()
                    .filter(qdIssueSkcStockDO -> qdIssueSkcStockDO.getNewIssueSkc()>0)
                    .distinct().collect(Collectors.toList());

            if (CollectionUtils.isEmpty(demandSkcList)) {
                return null;
            }
        } else {
            // 满场率设置为"否"
        }

        // 计算门店每个sku的需求量
        return qdIssueRequireService.calcNewSkuRequirement(taskId,issueShopList,qdIssueSkcListDOList,demandSkcList);
    }


    /**
     * 老品需求量计算
     */
    public List<QdIssueInStockDO> processRequirement(int taskId,List<QdIssueShopListDO> issueShopList,List<QdIssueSkcListDO> qdIssueSkcListDOList, QdIssueConfig qdIssueConfig) {

        List<QdIssueDisplayDesginDO> qdIssueDisplayDesginDOList = qdIssueDisplayDesginDOMapper.getShopDisplayDesignList();
        // 品类结构
        List<QdIssueCategoryStructureDO> qdIssueCategoryStructureDOList = qdIssueCategoryStructureDOMapper.getQdIssueCategoryStructureDOList();

        List<String> seasonList = null;
        if (qdIssueConfig.getSeason() != null) {
            seasonList =  Stream.of(qdIssueConfig.getSeason().split(",")).collect(Collectors.toList());
        }

        //  计算门店skc可配发量
        List<QdIssueSkcStockDO> qdIssueSkcStockDOList = qdIssueRequireService.calcSkcStock(taskId,issueShopList,qdIssueDisplayDesginDOList,qdIssueCategoryStructureDOList,seasonList);

        if (CollectionUtils.isEmpty(qdIssueSkcStockDOList)) {
            return null;
        }

        // 落库（异步插入）
        insertExecutor.execute(()->{
            try {
                qdIssueDOMapper.batchInsertSkcStock(qdIssueSkcStockDOList);
            }catch (Exception e) {
                LoggerUtil.warn(logger,"[INSERT_SKC] msg=task:{0},list:{1}",taskId,qdIssueSkcStockDOList);
            }
        });

        // 返回有需求的门店列表(计算可分配skc公式在getMidCategoryIssueSkc方法中)
        List<QdIssueSkcStockDO> demandSkcList = qdIssueSkcStockDOList.stream()
                .filter(qdIssueSkcStockDO -> qdIssueSkcStockDO.getMidCategoryIssueSkc()>0)
                .distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(demandSkcList)) {
            return null;
        }

        // 计算门店每个sku的需求量
        return qdIssueRequireService.calcSkuRequirement(taskId,issueShopList,demandSkcList,qdIssueSkcListDOList);
    }

    /**
     * 新品配发匹配
     * @param taskId
     * @param qdIssueSkcListDOList
     * @param allQdIssueInStockList
     */
    private void newIssueMatch(int taskId,List<QdIssueShopListDO> issueShopList,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<QdIssueInStockDO> allQdIssueInStockList,QdIssueConfig qdIssueConfig) {

        List<QdIssueOutStockDO> qdIssueOutStockDOList = qdIssueDOMapper.getOutStockData(taskId);

        String fullRate = qdIssueConfig.getFullRate(); // 满场率

        // skc区分优先级
        List<QdIssueSkcListDO> firstSkcList =  qdIssueSkcListDOList.stream().filter(issueSkc->issueSkc.getPriorityFlag() == Constant.PRIORITY_YES).collect(Collectors.toList());
        List<QdIssueSkcListDO> normalSkcList =  qdIssueSkcListDOList.stream().filter(issueSkc->issueSkc.getPriorityFlag() != Constant.PRIORITY_YES).collect(Collectors.toList());

        if("是".equals(fullRate)) {
            List<QdIssueNewSkcStockDO> qdIssueNewSkcStockDOList = qdIssueDOMapper.getNewSkcStockList(taskId);
            this.processNewQdIssue(taskId, firstSkcList, allQdIssueInStockList, issueShopList, qdIssueOutStockDOList, qdIssueNewSkcStockDOList, 0);
            this.processNewQdIssue(taskId, normalSkcList, allQdIssueInStockList, issueShopList, qdIssueOutStockDOList, qdIssueNewSkcStockDOList, 1000);
        } else {
            this.processNewQdIssue(taskId, firstSkcList, allQdIssueInStockList, issueShopList, qdIssueOutStockDOList, null, 0);
            this.processNewQdIssue(taskId, normalSkcList, allQdIssueInStockList, issueShopList, qdIssueOutStockDOList, null, 1000);
        }

    }

    /**
     * 老品配发匹配
     * @param taskId
     */
    private void issueMatch(int taskId,List<QdIssueShopListDO> issueShopList,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<QdIssueInStockDO> allQdIssueInStockList) {

        List<QdIssueOutStockDO> qdIssueOutStockDOList = qdIssueDOMapper.getOutStockData(taskId);

        List<QdIssueSkcStockDO> qdIssueSkcStockDOList = qdIssueDOMapper.getSkcStockList(taskId);

        // skc区分优先级
        List<QdIssueSkcListDO> firstSkcList =  qdIssueSkcListDOList.stream().filter(issueSkc->issueSkc.getPriorityFlag() == Constant.PRIORITY_YES).collect(Collectors.toList());
        List<QdIssueSkcListDO> normalSkcList =  qdIssueSkcListDOList.stream().filter(issueSkc->issueSkc.getPriorityFlag() != Constant.PRIORITY_YES).collect(Collectors.toList());

        this.processQdIssue(taskId,firstSkcList,allQdIssueInStockList,issueShopList,qdIssueOutStockDOList,qdIssueSkcStockDOList,0);
        this.processQdIssue(taskId,normalSkcList,allQdIssueInStockList,issueShopList,qdIssueOutStockDOList,qdIssueSkcStockDOList,5000);

    }

    /**
     * 满场率为"是"，需要根据skc数进行配发 （qdIssueNewSkcStockDOList != null）
     * 满场率为"否"，直接配发 （qdIssueNewSkcStockDOList == null）
     * @param taskId
     * @param qdIssueSkcListDOList
     * @param allQdIssueInStockList
     * @param issueShopList
     * @param qdIssueOutStockDOList
     * @param qdIssueNewSkcStockDOList
     * @param skip
     */
    private void processNewQdIssue(int taskId,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<QdIssueInStockDO> allQdIssueInStockList,List<QdIssueShopListDO> issueShopList,List<QdIssueOutStockDO> qdIssueOutStockDOList,List<QdIssueNewSkcStockDO> qdIssueNewSkcStockDOList,int skip) {

        if (CollectionUtils.isEmpty(qdIssueSkcListDOList)) {
            return;
        }

        Map<String,List<QdIssueInStockDO>> shopDemandMap = new TreeMap<>();

        // 计算所有skc下所有门店的需求量
        long start = System.currentTimeMillis();

        qdIssueSkcListDOList.parallelStream().forEach(qdIssueSkcListDO->{

            // skc所有需求门店数据列表(从内存中获取)
            List<QdIssueInStockDO> shopDemandList = allQdIssueInStockList.parallelStream().filter(qdIssueInStockDO -> qdIssueInStockDO.getMatCode().equals(qdIssueSkcListDO.getMatCode())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(shopDemandList)) {

                for (QdIssueInStockDO qdIssueInStockDO : shopDemandList) {

                    // 门店信息
                    for (QdIssueShopListDO qdIssueShopListDO : issueShopList) {
                        if (qdIssueInStockDO.getShopId().equals(qdIssueShopListDO.getShopId())) {
                            qdIssueInStockDO.setRegionName(qdIssueShopListDO.getRegionName());
                            qdIssueInStockDO.setProvinceName(qdIssueShopListDO.getProvinceName());
                        }
                    }

                    // 商品信息
                    //qdIssueInStockDO.setCategoryName(qdIssueSkcListDO.getCategoryName());
                    //qdIssueInStockDO.setMidCategoryName(qdIssueSkcListDO.getMidCategoryName());

                }

                // 排序后的门店列表
                List<QdIssueInStockDO> sortShopDemandList = this.sortByDemand(shopDemandList);

                shopDemandMap.put(qdIssueSkcListDO.getMatCode(), sortShopDemandList);

            }
        });

        long end = System.currentTimeMillis();
        LoggerUtil.info(logger,"[MATCH_INFO] msg=time:{0}",(end-start)/1000);


        if (shopDemandMap.isEmpty()) {
            return;
        }

        // 初始化剩余库存
        qdIssueOutStockDOList.forEach(qdIssueOutStockDO -> {
            qdIssueOutStockDO.setRemainQty(qdIssueOutStockDO.getStockQty());
        });

        // 配发
        int order = 1 + skip;
        for (QdIssueSkcListDO qdIssueSkcListDO : qdIssueSkcListDOList) {
            List<QdIssueInStockDO> shopDemandList =shopDemandMap.get(qdIssueSkcListDO.getMatCode());

            if (CollectionUtils.isEmpty(shopDemandList)) {
                continue;
            }

            List<QdIssueDetailDO> qdIssueDetailDOList = new ArrayList<>();
            Map<String,QdIssueOutStockDO> updateOutStockMap = new HashMap<>();

            List<String> shopIdList = shopDemandList.stream().map(QdIssueInStockDO::getShopId).distinct().collect(Collectors.toList());
            Map<String,List<QdIssueInStockDO>> shopMap = shopDemandList.stream().collect(Collectors.groupingBy(QdIssueInStockDO::getShopId,Collectors.toList()));

            // 按门店纬度进行分配
            int index = 1;
            for (String shopId : shopIdList) {

                List<QdIssueInStockDO> demandList = shopMap.get(shopId);

                // 当前skc满足：在店库存+在配库存+在途库存>5 ，不执行配发
                long skuQty = 0;
                for (QdIssueInStockDO shopDemand : demandList) {
                    skuQty += shopDemand.getStockQty() + shopDemand.getPathQty() + shopDemand.getApplyQty();
                }

                // 门店skc分配数
                QdIssueNewSkcStockDO qdIssueSkcStock = null;
                if (CollectionUtils.isNotEmpty(qdIssueNewSkcStockDOList)) {
                    qdIssueSkcStock = qdIssueNewSkcStockDOList.stream()
                            .filter(qdIssueSkcStockDO -> qdIssueSkcStockDO.getShopId().equals(shopId))
                            .findFirst().orElse(null);
                }

                // 分配库存
                for (QdIssueInStockDO shopDemand : demandList) {

                    QdIssueOutStockDO outStockDO = qdIssueOutStockDOList.stream()
                            .filter(qdIssueOutStockDO -> qdIssueOutStockDO.getMatCode().equals(shopDemand.getMatCode()) && shopDemand.getSizeId().equals(qdIssueOutStockDO.getSizeId()))
                            .findFirst().orElse(null);

                    if (outStockDO != null && outStockDO.getRemainQty() > 0) {

                        long qty = Math.min(outStockDO.getRemainQty(),shopDemand.getDemandQty());

                        shopDemand.setIssueQty(Math.abs(qty));
                    }
                }

                // 齐码
                if (!this.sizeCheck(demandList)) {
                    LoggerUtil.warn(logger,"[SIZE_CHECK] msg=LESS THEN THREE SIZE . shop:{0},matCode:{1}",shopId,qdIssueSkcListDO.getMatCode());
                    continue;
                }

                // 判断skc数是否已经分配满
                if (CollectionUtils.isNotEmpty(qdIssueNewSkcStockDOList)) {
                    if (qdIssueSkcStock == null || qdIssueSkcStock.getNewIssueSkc() == qdIssueSkcStock.getNewHadIssueSkc()) {
                        LoggerUtil.warn(logger, "[NEW_ISSUE_FULL] msg=skc issue enough. shop:{0},matCode:{1}", shopId, qdIssueSkcListDO.getMatCode());
                        continue;
                    }
                }

                int offset = 1;
                // 生成配发明细
                for (QdIssueInStockDO shopDemand : demandList) {

                    for (QdIssueOutStockDO qdIssueOutStockDO : qdIssueOutStockDOList) {

                        if (shopDemand.getIssueQty() > 0) {

                            if (shopDemand.getMatCode().equals(qdIssueOutStockDO.getMatCode())
                                    && shopDemand.getSizeId().equals(qdIssueOutStockDO.getSizeId())) {
                                // 计算分配量
                                long qty = qdIssueOutStockDO.getRemainQty() - shopDemand.getIssueQty();
                                qdIssueOutStockDO.setRemainQty(qty);

                                qdIssueDetailDOList.add(QdIssueDataMapper.mapperTo(taskId,shopDemand,order+"-"+offset));

                                qdIssueOutStockDO.setTaskId(taskId);
                                updateOutStockMap.put(qdIssueOutStockDO.getMatCode()+":"+qdIssueOutStockDO.getSizeId(),qdIssueOutStockDO);
                            }
                        }
                    }
                }

                offset++;

                // 更新skc分配数
                if (CollectionUtils.isNotEmpty(qdIssueNewSkcStockDOList)) {
                    long hadIssueCnt = qdIssueSkcStock.getNewHadIssueSkc();
                    qdIssueSkcStock.setNewHadIssueSkc(hadIssueCnt + 1);
                }

                LoggerUtil.info(logger,"[QD_ISSUE] msg = index:{0},shop:{1}",index,shopId);
                index++;
            }

            // 批量更新
            if (CollectionUtils.isNotEmpty(updateOutStockMap.values())) {
                qdIssueDOMapper.batchUpdateOutStockIssueQty(updateOutStockMap.values().stream().collect(Collectors.toList()));
            }

            // 插入配发数据
            if(CollectionUtils.isNotEmpty(qdIssueDetailDOList)) {
                int succ = qdIssueDetailDOMapper.batchInsertIssueDetail(qdIssueDetailDOList);

            }

            order++;
            LoggerUtil.info(logger,"[ISSUE_DETAIL] msg=matCode:{0},detail:{1}",qdIssueSkcListDO.getMatCode(),qdIssueDetailDOList.size());

        }

        if (CollectionUtils.isNotEmpty(qdIssueNewSkcStockDOList)) {
            // 更新分配skc数
            qdIssueDOMapper.batchUpdateNewSkcIssueSkc(qdIssueNewSkcStockDOList);
        }
    }



    private void processQdIssue(int taskId,List<QdIssueSkcListDO> qdIssueSkcListDOList,List<QdIssueInStockDO> allQdIssueInStockList,List<QdIssueShopListDO> issueShopList,List<QdIssueOutStockDO> qdIssueOutStockDOList,List<QdIssueSkcStockDO> qdIssueSkcStockDOList,int skip) {

        if (CollectionUtils.isEmpty(qdIssueSkcListDOList)) {
            return;
        }

        Map<String,List<QdIssueInStockDO>> shopDemandMap = new TreeMap<>();

        // 计算所有skc下所有门店的需求量
        long start = System.currentTimeMillis();

        qdIssueSkcListDOList.parallelStream().forEach(qdIssueSkcListDO->{

            // skc所有需求门店数据列表(从内存中获取)
            List<QdIssueInStockDO> shopDemandList = allQdIssueInStockList.parallelStream().filter(qdIssueInStockDO -> qdIssueInStockDO.getMatCode().equals(qdIssueSkcListDO.getMatCode())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(shopDemandList)) {

                for (QdIssueInStockDO qdIssueInStockDO : shopDemandList) {

                    // 门店信息
                    for (QdIssueShopListDO qdIssueShopListDO : issueShopList) {
                        if (qdIssueInStockDO.getShopId().equals(qdIssueShopListDO.getShopId())) {
                            qdIssueInStockDO.setRegionName(qdIssueShopListDO.getRegionName());
                            qdIssueInStockDO.setProvinceName(qdIssueShopListDO.getProvinceName());
                        }
                    }

                    // 商品信息
                    qdIssueInStockDO.setCategoryName(qdIssueSkcListDO.getCategoryName());
                    qdIssueInStockDO.setMidCategoryName(qdIssueSkcListDO.getMidCategoryName());

                }

                // 排序后的门店列表
                List<QdIssueInStockDO> sortShopDemandList = this.sortByDemand(shopDemandList);

                shopDemandMap.put(qdIssueSkcListDO.getMatCode(), sortShopDemandList);

            }
        });

        long end = System.currentTimeMillis();
        LoggerUtil.info(logger,"[MATCH_INFO] msg=time:{0}",(end-start)/1000);


        if (shopDemandMap.isEmpty()) {
            return;
        }

        // 初始化剩余库存
        qdIssueOutStockDOList.forEach(qdIssueOutStockDO -> {
            qdIssueOutStockDO.setRemainQty(qdIssueOutStockDO.getStockQty());
        });

        // 配发
        int order = 1 + skip;
        for (QdIssueSkcListDO qdIssueSkcListDO : qdIssueSkcListDOList) {
            List<QdIssueInStockDO> shopDemandList =shopDemandMap.get(qdIssueSkcListDO.getMatCode());

            if (CollectionUtils.isEmpty(shopDemandList)) {
                continue;
            }

            List<QdIssueDetailDO> qdIssueDetailDOList = new ArrayList<>();
            Map<String,QdIssueOutStockDO> updateOutStockMap = new HashMap<>();

            List<String> shopIdList = shopDemandList.stream().map(QdIssueInStockDO::getShopId).distinct().collect(Collectors.toList());
            Map<String,List<QdIssueInStockDO>> shopMap = shopDemandList.stream().collect(Collectors.groupingBy(QdIssueInStockDO::getShopId,Collectors.toList()));

            // 按门店纬度进行分配
            int index = 1;
            for (String shopId : shopIdList) {

                List<QdIssueInStockDO> demandList = shopMap.get(shopId);

                // 当前skc满足：在店库存+在配库存+在途库存>5 ，不执行配发
                long skuQty = 0;
                for (QdIssueInStockDO shopDemand : demandList) {
                    skuQty += shopDemand.getStockQty() + shopDemand.getPathQty() + shopDemand.getApplyQty();
                }

                if (skuQty > 5) {
                    LoggerUtil.warn(logger,"[SKIP_SHOP] msg=shopId:{0},skc:{1}",shopId,qdIssueSkcListDO.getMatCode());
                    continue;
                }

                // 门店skc分配数
                QdIssueSkcStockDO qdIssueSkcStock = qdIssueSkcStockDOList.stream()
                        .filter(qdIssueSkcStockDO -> qdIssueSkcStockDO.getShopId().equals(shopId)
                                && qdIssueSkcStockDO.getMidCategoryName().equals(qdIssueSkcListDO.getMidCategoryName()))
                        .findFirst().orElse(null);

                // 分配库存
                for (QdIssueInStockDO shopDemand : demandList) {

                    QdIssueOutStockDO outStockDO = qdIssueOutStockDOList.stream()
                            .filter(qdIssueOutStockDO -> qdIssueOutStockDO.getMatCode().equals(shopDemand.getMatCode()) && shopDemand.getSizeId().equals(qdIssueOutStockDO.getSizeId()))
                            .findFirst().orElse(null);

                    if (outStockDO != null && outStockDO.getRemainQty() > 0) {

                        long qty = Math.min(outStockDO.getRemainQty(),shopDemand.getDemandQty());

                        shopDemand.setIssueQty(Math.abs(qty));
                    }
                }

                // 齐码
                if (!this.sizeCheck(demandList)) {
                    LoggerUtil.warn(logger,"[SIZE_CHECK] msg=LESS THEN THREE SIZE . shop:{0},matCode:{1}",shopId,qdIssueSkcListDO.getMatCode());
                    continue;
                }

                // 判断skc数是否已经分配满
                if (qdIssueSkcStock == null || qdIssueSkcStock.getMidCategoryHadIssueSkc() == qdIssueSkcStock.getMidCategoryIssueSkc()) {
                    LoggerUtil.warn(logger,"[ISSUE_FULL] msg=skc issue enough. shop:{0},matCode:{1}",shopId,qdIssueSkcListDO.getMatCode());
                    continue;
                }

                int offset = 1;
                // 生成配发明细
                for (QdIssueInStockDO shopDemand : demandList) {

                    for (QdIssueOutStockDO qdIssueOutStockDO : qdIssueOutStockDOList) {

                        if (shopDemand.getIssueQty() > 0) {

                            if (shopDemand.getMatCode().equals(qdIssueOutStockDO.getMatCode())
                                    && shopDemand.getSizeId().equals(qdIssueOutStockDO.getSizeId())) {
                                // 计算分配量
                                long qty = qdIssueOutStockDO.getRemainQty() - shopDemand.getIssueQty();
                                qdIssueOutStockDO.setRemainQty(qty);

                                qdIssueDetailDOList.add(QdIssueDataMapper.mapperTo(taskId,shopDemand,order+"-"+offset));

                                qdIssueOutStockDO.setTaskId(taskId);
                                updateOutStockMap.put(qdIssueOutStockDO.getMatCode()+":"+qdIssueOutStockDO.getSizeId(),qdIssueOutStockDO);
                            }
                        }
                    }
                }

                offset++;

                // 更新skc分配数
                long hadIssueCnt = qdIssueSkcStock.getMidCategoryHadIssueSkc();
                qdIssueSkcStock.setMidCategoryHadIssueSkc(hadIssueCnt+1);

                LoggerUtil.info(logger,"[QD_ISSUE] msg = index:{0},shop:{1}",index,shopId);
                index++;
            }

            // 批量更新
            if (CollectionUtils.isNotEmpty(updateOutStockMap.values())) {
                qdIssueDOMapper.batchUpdateOutStockIssueQty(updateOutStockMap.values().stream().collect(Collectors.toList()));
            }

            // 插入配发数据
            if(CollectionUtils.isNotEmpty(qdIssueDetailDOList)) {
                int succ = qdIssueDetailDOMapper.batchInsertIssueDetail(qdIssueDetailDOList);

            }

            order++;
            LoggerUtil.info(logger,"[ISSUE_DETAIL] msg=matCode:{0},detail:{1}",qdIssueSkcListDO.getMatCode(),qdIssueDetailDOList.size());

        }

        // 更新分配skc数
        qdIssueDOMapper.batchUpdateSkcIssueSkc(qdIssueSkcStockDOList);
    }

    /**
     * 齐码判断
     * @param demandList
     * @return
     */
    private boolean sizeCheck(List<QdIssueInStockDO> demandList) {

        long count = demandList.stream().filter(qdIssueInStockDO -> qdIssueInStockDO.getAfterIssueStockQty()>0).count();
        // 少于3的，不执行配发
        if (count < 3) {
            return false;
        }
        return true;
    }

    /**
     * 按需求量进行排序
     * 区域>省份>城市
     * @param shopDemandList
     * @return
     */
    private List<QdIssueInStockDO> sortByDemand(List<QdIssueInStockDO> shopDemandList) {

        Map<String,Long> regionDemandMap = shopDemandList.stream().collect(Collectors.groupingBy(QdIssueInStockDO::getRegionName,Collectors.summingLong(QdIssueInStockDO::getDemandQty)));

        Map<String,Long> provinceDemandMap = shopDemandList.stream().collect(Collectors.groupingBy(QdIssueInStockDO::getProvinceName,Collectors.summingLong(QdIssueInStockDO::getDemandQty)));

        shopDemandList.forEach(qdIssueInStockDO -> {
            long regionDemandQty = regionDemandMap.get(qdIssueInStockDO.getRegionName());
            long provinceDemandQty = provinceDemandMap.get(qdIssueInStockDO.getProvinceName());

            qdIssueInStockDO.setRegionDemandQty(regionDemandQty);
            qdIssueInStockDO.setProvinceDemandQty(provinceDemandQty);
        });


        //按区域排序
        List<QdIssueInStockDO> newList = new ArrayList<>(shopDemandList.size());

        Map<Long,List<QdIssueInStockDO>> regionMap = shopDemandList.stream()
                 .sorted(Comparator.comparing(QdIssueInStockDO::getRegionDemandQty).reversed())
                 .collect(Collectors.groupingBy(QdIssueInStockDO::getRegionDemandQty,Collectors.toList()));

        regionMap.entrySet().stream()
                .sorted(Map.Entry.<Long,List<QdIssueInStockDO>>comparingByKey().reversed())
                .forEachOrdered(entry->{

                    Map<Long,List<QdIssueInStockDO>> provinceMap = entry.getValue().stream()
                            .sorted(Comparator.comparing(QdIssueInStockDO::getProvinceDemandQty).reversed())
                            .collect(Collectors.groupingBy(QdIssueInStockDO::getProvinceDemandQty,Collectors.toList()));

                    provinceMap.entrySet().stream()
                            .sorted(Map.Entry.<Long,List<QdIssueInStockDO>>comparingByKey().reversed())
                            .forEachOrdered(entry2->{

                                newList.addAll(entry2.getValue().stream()
                                        .sorted(Comparator.comparing(QdIssueInStockDO::getDemandQty).reversed())
                                        .collect(Collectors.toList()));
                            });
                });

        return newList;

    }
}
