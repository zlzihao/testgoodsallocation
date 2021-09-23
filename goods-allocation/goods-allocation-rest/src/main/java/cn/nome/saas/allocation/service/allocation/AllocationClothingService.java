package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.HttpClientUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.model.allocation.AllocationClothingSKC;
import cn.nome.saas.allocation.model.allocation.AllocationShop;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.model.rule.NewShopExpress;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.vertical.AllocationExtraDataMapper;
import cn.nome.saas.allocation.repository.dao.vertical.QdIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.repository.entity.vertical.AllocationGoodsSKC;
import cn.nome.saas.allocation.constant.*;
import cn.nome.saas.allocation.service.rule.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 服装调拨
 *
 * @author Bruce01.fan
 * @date 2019/12/2
 */
@Service
public class AllocationClothingService {

    private static Logger logger = LoggerFactory.getLogger(AllocationClothingService.class);


    @Autowired
    OutOfStockGoodsService outOfStockGoodsService;

    @Autowired
    RequireRuleEngineService requireRuleEngineService;

    @Autowired
    FlowRuleEngineService flowRuleEngineService;

    @Autowired
    AllocationStockService allocationStockService;

    @Autowired
    ClothingTaskDOMapper taskService;

    @Autowired
    ForbiddenRuleService forbiddenRuleService;

    @Autowired
    AllocationFlowDOMapper allocationFlowDOMapper;

    @Autowired
    AllocationStockDOMapper allocationStockDOMapper;

    @Autowired
    QdIssueExtraDataMapper qdIssueExtraDataMapper;

    @Autowired
    ShopListCache shopListCache ;

    @Autowired
    AllocationExtraDataMapper allocationExtraDataMapper;

    @Autowired
    ClothingRuleEngineService clothingRuleEngineService;

    @Autowired
    ClothingFlowRuleEngineService clothingFlowRuleEngineService;

    @Autowired
    ClothingFragmentalRuleEngineService clothingFragmentalRuleEngineService;

    // 计算理想库存
    ExecutorService calcBestMatchPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    // 插入
    ExecutorService insertPool = Executors.newSingleThreadExecutor();

    List<Future> calcIdealQtyFutureList = null;

    @Autowired
    ShopExpressService shopExpressService;

    /**
     * 计算服装齐码调拨
     * @param taskId
     */
    @Async("taskExecutor")
    public void runClothingAllocationTask(int taskId) {
        Task task = null;

        TaskDO taskDO = taskService.getTask(taskId);

        if (taskDO != null) {

            task = new Task();
            BeanUtils.copyProperties(taskDO, task);

        } else {
            List<TaskDO> taskDOList = taskService.getNeedRunTaskList();

            if (CollectionUtils.isNotEmpty(taskDOList)) {
                long runningTaskCount = taskDOList.stream().filter(t -> t.getTaskStatus() == 2).count();
                if (runningTaskCount == 0) {
                    //  取第一个任务开始执行
                    taskDO = taskDOList.stream().filter(t -> t.getTaskStatus() == 1).findFirst().orElse(null);
                    task = new Task();
                    BeanUtils.copyProperties(taskDO, task);
                    taskId = taskDO.getTaskId();
                }
            }
        }

        if (task != null) {
            //
            taskService.updateTaskToRunning(task.getTaskId());

            try {
                // 计算理想库存
                this.calcRequirement(task);

                LoggerUtil.info(logger,"[完成理想库存计算] msg=taskId:{0}",task.getTaskId());

                // 生成调拨明细
                int succCount = this.allocation(task);

                LoggerUtil.info(logger,"[完成调拨明细] msg=taskId:{0}",task.getTaskId());

                if (succCount == 0) {
                    taskService.updateTaskToFail(task.getTaskId(),"调出店铺不满足运费要求");
                    return;
                }

                // 针对非有效skc的零散服装处理
                clothingFragmentalRuleEngineService.handleFragmentalGoods(task);

                LoggerUtil.info(logger,"[完成零散服装计算] msg=taskId:{0}",task.getTaskId());

                // 生成调拨效果数据
                clothingRuleEngineService.generateClothingEffectResult(taskId);

                LoggerUtil.info(logger,"[生成调拨效果] msg=taskId:{0}",task.getTaskId());

                taskService.updateTaskToFinish(task.getTaskId());

                // 触发官远同步任务
                this.clickSyncToGY();

                LoggerUtil.info(logger,"[完成调拨] msg=taskId:{0}",task.getTaskId());

            } catch (Exception e) {
                LoggerUtil.error(e,logger,"[ALLOCATION_ERROR]");
                taskService.updateTaskToFail(taskId,"系统异常，请重跑任务");
                throw e;
            }
        }
    }


    public void calcRequirement(Task task) {
        Long requirementStart = System.currentTimeMillis();
        // 需求计算
        try {
            caclSkuBestStock(task);
        } catch (Exception e) {
            LoggerUtil.error(e,logger,"[CALC_BEST_QTY] ERROR");
        }
        Long requirementEnd = System.currentTimeMillis();

        LoggerUtil.info(logger,"[CALC_REQUIRE] msg= time:{0}",(requirementEnd-requirementStart) / 1000 / 60);
    }

    public int allocation(Task task) {

        // 判断计算理想库存所有任务是否都完成了
        if (CollectionUtils.isNotEmpty(calcIdealQtyFutureList)) {
            for (Future future : calcIdealQtyFutureList) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LoggerUtil.info(logger,"[CALC_BEST_STOCK] msg=ALL CALC BEST STOCK ENDING!!");
        }

        taskService.updateTaskProcess(task.getTaskId(),30);

        // 插入执行

        List<String> shopIdList = new ArrayList<>();
        List<String> supplyShopIdList = new ArrayList<>();

        if (StringUtils.isNotBlank(task.getDemandShopIds())) {
            shopIdList = Stream.of(task.getDemandShopIds().split(",")).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(task.getSupplyShopIds())) {
            supplyShopIdList = Stream.of(task.getSupplyShopIds().split(",")).collect(Collectors.toList());
        }

        outOfStockGoodsService.addIndex(task); // 查询之前添加索引（为了提高插入理想库存的性能，索引在插入完成再创建）

        allocationFlowDOMapper.deleteByParam(task.getTaskId());

        // 针对撤店涉及的商品，不支持调出(排除点发回总仓的)
        List<AllocationStockDO> closeAlloctionList = null;
        if (task.getCloseTaskId() > 0) {

            Integer taskId = new Integer(task.getCloseTaskId());

            List<Integer> taskIdList = taskService.selectAllRejectTask(taskId);

            Map<String,Object> param = new HashMap<>();
            param.put("taskIdList",taskIdList);
            param.put("rejectShopId","NM001275");

            closeAlloctionList = allocationStockDOMapper.selectAllocationStockByParam(param);
        }

        Long startTime = System.currentTimeMillis();
        // 需求队列
        List<OutOfStockGoodsDO> demandList = new ArrayList<>();
        int offset = 0 ;
        while(true) {
            List<OutOfStockGoodsDO>  subList = outOfStockGoodsService.getDemandStockList(task, shopIdList,offset);

            if (CollectionUtils.isNotEmpty(subList)) {
                demandList.addAll(subList);
            }else {
                break;
            }
            offset++;
        }
        Long demandEnd = System.currentTimeMillis();

        LoggerUtil.info(logger,"[DEMAND] msg=demand size:{0},time:{1}",demandList.size(),(demandEnd-startTime)/1000);

        // 供给队列
        List<OutOfStockGoodsDO> supplyList = outOfStockGoodsService.getSupplyStockList(task,supplyShopIdList,null);

        // 剔除掉撤店的sku列表
        supplyList = excludeCloseSkuList(task.getAllocationType(),supplyList,closeAlloctionList);


        taskService.updateTaskProcess(task.getTaskId(),60);

        // 流向计算
        int succCount = this.calcBestAllocationMatch(task,demandList,supplyList);

        long qty_3 = supplyList.stream().collect(Collectors.summingLong(OutOfStockGoodsDO::getSupplyStockQty));

        //若为撤店需求, 增加流向至总仓
        if (Task.ALLOCATION_TYPE_REJECT == task.getAllocationType()) {
            succCount += calcBestAllocationMatchReject(task, supplyShopIdList, supplyList,closeAlloctionList);
        }

        Long end = System.currentTimeMillis();

        LoggerUtil.info(logger,"[ALLOCATION] msg=time:{0}",(end - startTime) / 1000);

        return succCount;
    }

    /**
     * 计算每个sku在所有店铺的理想库存
     */
    public void caclSkuBestStock(Task task) {

        if (outOfStockGoodsService.checkTableExists(task)) {
            outOfStockGoodsService.clearAll(task);
        } else {
            outOfStockGoodsService.createNewTable(task);
        }

        Set<String> demandShopIdList = new HashSet<>();
        Set<String> supplyShopIdList = new HashSet<>();


        if (StringUtils.isNotBlank(task.getSupplyShopIds())) {
            supplyShopIdList.addAll(Stream.of(task.getSupplyShopIds().split(",")).collect(Collectors.toSet()));
        }

        if (StringUtils.isNotBlank(task.getDemandShopIds())) {
            if (task.getAllocationType() == Task.ALLOCATION_TYPE_REJECT && supplyShopIdList.size()>0) {
                demandShopIdList.addAll(Stream.of(task.getDemandShopIds().split(","))
                        .filter(shopId -> !supplyShopIdList.contains(shopId))
                        .collect(Collectors.toSet()));
            } else {
                demandShopIdList.addAll(Stream.of(task.getDemandShopIds().split(",")).collect(Collectors.toSet()));
            }
        }

        calcIdealQtyFutureList = new ArrayList<>();

        if (demandShopIdList.containsAll(supplyShopIdList) && task.getInDays() == task.getOutDays()) {
            // 需求&供给一起计算
            this.calcBestQty(task,task.getInDays(),demandShopIdList,calcIdealQtyFutureList);
        } else {
            // 需求和供给门店的安全库存单独计算
            this.calcBestQty(task,task.getInDays(),demandShopIdList,calcIdealQtyFutureList);
            taskService.updateTaskProcess(task.getTaskId(),10);

            //撤店调拨计算供给店
            // 过滤掉与需求相同的门店
            Set<String> subSupplyShopIdList = supplyShopIdList.stream().filter(shopId -> !demandShopIdList.contains(shopId)).collect(Collectors.toSet());
            if (Task.ALLOCATION_TYPE_REJECT == task.getAllocationType()) {
                this.calcBestQtyReject(task,subSupplyShopIdList.stream().findFirst().orElse(null));
            } else {
                this.calcBestQty(task,task.getOutDays(),subSupplyShopIdList,calcIdealQtyFutureList);
            }
        }

        taskService.updateTaskProcess(task.getTaskId(),20);

    }


    private void calcBestQty(Task task,int safeDays,Set<String> shopIdList,List<Future> calcIdealQtyFutureList) {

        int offset = 0;
        int size = 200;

        List<String> allMatCodeList = outOfStockGoodsService.getAllMatCodeList(shopIdList,task);

        List<OutOfStockGoodsDO> inStockShopList = allocationExtraDataMapper.getInStockBetweenSeven(shopIdList); // 14天有调入的
        List<OutOfStockGoodsDO> outStockShopList = allocationExtraDataMapper.getOutStockBetweenFourteen(shopIdList); // 14天调出的


        // 撤店调拨明细
        List<AllocationStockDO> closeAlloctionList = null;
        if (task.getCloseTaskId() > 0) {
            Map<String,Object> param = new HashMap<>();
            Integer taskId = new Integer(task.getCloseTaskId());
            param.put("taskId",taskId);
            closeAlloctionList = allocationStockDOMapper.selectAllocationStockByParam(param);
        }

        // 店铺尺码+库存数数据
        List<AllocationClothingSKC> allocationClothingSKCList = clothingRuleEngineService.getShopClothingSkcInfo(shopIdList,allMatCodeList);

        while(true) {

            Long queryBegin = System.currentTimeMillis();

            List<String> matCodeList = allMatCodeList.stream().skip(offset * size).limit(size).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(matCodeList)) {
                break;
            }

            Map<String,List<OutOfStockGoodsDO>> stockMap =  outOfStockGoodsService.getStockByPage(task.getTaskType(),task.getAllocationType(),matCodeList,shopIdList);
            Long queryEnd = System.currentTimeMillis();

            LoggerUtil.debug(logger,"[QUERY_SKU_STOCK] msg=end. time:{0}",(queryEnd - queryBegin));

            if (stockMap == null || stockMap.size() ==0) {
                offset++;
                continue;
            }

            //  针对执行撤店调拨的门店，要修改对应的库存
            if (CollectionUtils.isNotEmpty(closeAlloctionList)) {
                for (AllocationStockDO allocationStockDO : closeAlloctionList) {
                    for (String key : stockMap.keySet()) {
                        List<OutOfStockGoodsDO> list = stockMap.get(key);
                        for (OutOfStockGoodsDO outOfStockGoodsDO : list) {
                            if (allocationStockDO.getShopId().equals(outOfStockGoodsDO.getShopId())
                                    && allocationStockDO.getMatCode().equals(outOfStockGoodsDO.getMatCode())
                                    && allocationStockDO.getSizeId().equals(outOfStockGoodsDO.getSizeId())) {

                                int newApplyQty = outOfStockGoodsDO.getApplyStockQty() + allocationStockDO.getAllocationStockQty();
                                outOfStockGoodsDO.setApplyStockQty(newApplyQty);
                            }
                        }
                    }
                }
            }

            // 查询禁配规则数据
            List<Map<String,String>> forbiddenSingleItemDOList =  forbiddenRuleService.getForbiddenDetailList(matCodeList.stream().collect(Collectors.toSet()));

            //
            // 查询服装所有的尺码信息
            List<QdGoodsInfoDO> qdGoodsInfoDOList = null;
            if (task.getTaskType() == Constant.CLOTHING_TYPE) {

                qdGoodsInfoDOList = qdIssueExtraDataMapper.getGoodsInfo(matCodeList);
            }

            List<QdGoodsInfoDO> newQdGoodsInfoDOList = qdGoodsInfoDOList;
            Future future = calcBestMatchPool.submit(()-> {

                try {

                    for (String skuKey : stockMap.keySet()) {
                        List<OutOfStockGoodsDO> outOfStockGoodsDOList = stockMap.get(skuKey);

                        // 重新计算安全库存 （安全库存天数 * 日均销）
                        outOfStockGoodsDOList.forEach(outOfStockGoodsDO -> {
                            long qty = Math.round(outOfStockGoodsDO.getAvgSaleQty() * safeDays);
                            outOfStockGoodsDO.setSafeStockQty((int) qty);
                        });

                        requireRuleEngineService.calcForbiddenRule(task.getBreakable(), outOfStockGoodsDOList, forbiddenSingleItemDOList);

                        requireRuleEngineService.calcBestQty(task.getTaskType(), outOfStockGoodsDOList, safeDays, false, newQdGoodsInfoDOList);

                        requireRuleEngineService.calcSercurityRule(outOfStockGoodsDOList);
                        // calc and update demand 、 supply stock qty
                        this.calcDemandAndSupplyStockQty(task, outOfStockGoodsDOList,inStockShopList,outStockShopList);
                        // 针对
                        //clothingRuleEngineService.reCalBestQtyForClothing(allocationClothingSKCList,outOfStockGoodsDOList);
                        // 插入db
                        this.saveOutOfStockList(task,outOfStockGoodsDOList);
                    }
                } catch (Exception e) {
                    LoggerUtil.error(e,logger,"[CALC_BEST_ERROR]");
                }

                return "SUCC";
            });
            calcIdealQtyFutureList.add(future);

            offset++;
        }

        // 判断计算理想库存所有任务是否都完成了
        if (CollectionUtils.isNotEmpty(calcIdealQtyFutureList)) {
            for (Future future : calcIdealQtyFutureList) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LoggerUtil.info(logger,"[CALC_BEST_STOCK] msg=ALL CALC BEST STOCK ENDING!!");
        }

        // 单独针对服装做一次矫正
        Map<String,List<OutOfStockGoodsDO>> clothingShopGoodsMap = outOfStockGoodsService.getShopClothingGoodsMap(task,shopIdList,allMatCodeList);

        if (clothingShopGoodsMap == null) {
            return;
        }

        List<OutOfStockGoodsDO> allUpdateSotckGoodsList = new ArrayList<>();
        for(String shopMatCodeKey : clothingShopGoodsMap.keySet()) {
            String shopId = shopMatCodeKey.split(",")[0];
            String matCode = shopMatCodeKey.split(",")[1];
            List<OutOfStockGoodsDO> outOfStockGoodsDOList = clothingShopGoodsMap.get(shopMatCodeKey);
            List<OutOfStockGoodsDO> updateSotckGoodsList = clothingRuleEngineService.reCalBestQtyForClothing(shopId,matCode,allocationClothingSKCList,outOfStockGoodsDOList);
            if (CollectionUtils.isNotEmpty(updateSotckGoodsList)) {
                allUpdateSotckGoodsList.addAll(updateSotckGoodsList);
            }
        }
        outOfStockGoodsService.updateSelective(task,allUpdateSotckGoodsList);

    }

    private void calcBestQtyReject(Task task,String shopId) {

        outOfStockGoodsService.insertRejectOutOfStockGoods(task,shopId);

    }

    /**
     * 流向计算
     * @param demandList
     * @param supplyList
     */
    public Integer calcBestAllocationMatch(Task task,List<OutOfStockGoodsDO> demandList,List<OutOfStockGoodsDO> supplyList) {

        String currentDate = DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY);

        // 删除已生成的数据
        allocationStockService.deleteByParam(task.getTaskId());

        List<String> skipShopId = new ArrayList<>();
        int lastListSize = 0;

        List<AllocationFlowDO> allocationFlowDOList = new ArrayList<>();
        int times = 1;
        Set<String> highRateRejectShop = new HashSet<>(); // 超过费率阀值的门店id列表，目的避免allocation_flow生成重复的数据

        Set<String> supplyShopIdSet = supplyList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());

        // 获取服装skc数
        List<AllocationGoodsSKC> allocationGoodsSKCList = clothingRuleEngineService.getClothingSKC(task.getTaskId(),supplyShopIdSet,null,false);

        Set<String> shopIdList = demandList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());
        shopIdList.addAll(supplyShopIdSet);

        List<String> provinceList = shopListCache.getShopList().stream()
                .filter(shop->shopIdList.contains(shop.getShopId()))
                .map(DwsDimShopDO::getProvinceName)
                .distinct()
                .collect(Collectors.toList());

        List<NewShopExpress> newShopExpressList = shopExpressService.selectNewExpressByProvince(provinceList);

        while (true) {

            // 需求量最大的门店的商品列表（只有一家需求店）
            List<OutOfStockGoodsDO> amountDemandList = flowRuleEngineService.calcDemandAmount(task,demandList,skipShopId);

            if (CollectionUtils.isEmpty(amountDemandList)) {
                break;
            }
            if (lastListSize == amountDemandList.size()) {
                skipShopId.add(amountDemandList.get(0).getShopId());
                continue;
            }
            lastListSize = amountDemandList.size();

            // 挑选出最匹配需求的供给列表 (服装专门的处理逻辑) 1家需求店和多家供给店匹配
            AllocationShop bestMatchSupplyShop = clothingFlowRuleEngineService.calcSupplyScoreV2(task,amountDemandList,supplyList,highRateRejectShop,allocationGoodsSKCList,newShopExpressList);

            if (bestMatchSupplyShop != null) {
                LoggerUtil.info(logger, "[BEST_SHOP] msg=times:{0},demandShop:{1},supplyShop:{2},skuSize:{3}", times,bestMatchSupplyShop.getDemandShopId(),bestMatchSupplyShop.getSupplyShopId(),bestMatchSupplyShop.getAllocationStockDOList().size());
            } else {
                LoggerUtil.info(logger, "[BEST_SHOP_EMPTY] msg=times:{0}", times);
                continue;
            }

            List<AllocationStockDO> allocationStockDOList = this.genAllocationList(task,amountDemandList,supplyList,bestMatchSupplyShop);

            if (CollectionUtils.isEmpty(allocationStockDOList)) {
                continue;
            }

            allocationStockDOList.forEach(allocation->{
                allocation.setAllocationDate(currentDate);
            });

            // 流向追踪数据-用于统计
            AllocationFlowDO allocationFlowDO = this.genAllocationFlow(task.getTaskId(),bestMatchSupplyShop,allocationStockDOList,times,Constant.SUCC_MATCH,Constant.FAIL_REASON_NONE);
            allocationFlowDOList.add(allocationFlowDO);

            double amount = allocationStockDOList.parallelStream().collect(Collectors.summingDouble(allocation -> {
                return (allocation.getAllocationStockQty() * allocation.getQuotePrice());
            }));

            LoggerUtil.warn(logger,"[XXX] a:{0},b:{1},c:{2}",bestMatchSupplyShop.getAmount(),amount,bestMatchSupplyShop.getDemandShopId()+"_"+bestMatchSupplyShop.getSupplyShopId());

            // 插入新数据
            allocationStockService.batchInsert(allocationStockDOList);

            times++;

        }

        // 插入流向数据
        int succCount = 0;
        try {
            if (CollectionUtils.isNotEmpty(allocationFlowDOList)) {
                succCount = allocationFlowDOList.size();
                allocationFlowDOMapper.batchInsert(allocationFlowDOList);
            }
        }catch (Exception e) {
            LoggerUtil.error(e,logger,"[INSERT_FLOW]");
        }

        return succCount;
    }

    /**
     * 撤店流向计算
     * @param supplyShopIdList
     * @param supplyList
     */
    public int calcBestAllocationMatchReject(Task task, List<String> supplyShopIdList, List<OutOfStockGoodsDO> supplyList,List<AllocationStockDO> closeAlloctionList) {

        String currentDate = DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY);

        //获取撤店的所有商品 覆盖已经供应出去的库存
        List<OutOfStockGoodsDO> rejectList = outOfStockGoodsService.getSupplyStockList(task,supplyShopIdList, null);

        // 剔除掉撤店的sku列表
        rejectList = excludeCloseSkuList(task.getAllocationType(),rejectList,closeAlloctionList);

        for (OutOfStockGoodsDO supplyDo : supplyList) {

            for (OutOfStockGoodsDO rejectDo : rejectList) {

                if (supplyDo.getShopId().equals(rejectDo.getShopId()) &&
                        supplyDo.getMatCode().equals(rejectDo.getMatCode()) &&
                        supplyDo.getSizeId().equals(rejectDo.getSizeId())) {
                    if (supplyDo.getRemainStockQty() == null && supplyDo.getSupplyStockQty() > 0) {
                        rejectDo.setSupplyStockQty(supplyDo.getSupplyStockQty());
                    } else if (supplyDo.getRemainStockQty() > 0) {
                        rejectDo.setSupplyStockQty(supplyDo.getRemainStockQty());
                    } else if (supplyDo.getRemainStockQty() != null && supplyDo.getRemainStockQty() == 0) {
                        rejectDo.setSupplyStockQty(0);
                    }
                }
            }
        }

        List<AllocationStockDO> allocationStockDOList = new ArrayList<>();
        OutOfStockGoodsDO demandDo = supplyList.get(0);
        demandDo.setShopId("NM001275");
        demandDo.setShopName("总仓");
        demandDo.setShopLevel("A");
        demandDo.setCityName("");
        demandDo.setAvgSaleQty(0D);
        demandDo.setSumSaleQty(0);
        demandDo.setDemandStockQty(Integer.MAX_VALUE);
        for (OutOfStockGoodsDO supplyDo : rejectList) {
            if (supplyDo.getSupplyStockQty() == 0) {
                continue;
            }
            AllocationStockDO allocationStockDO = flowRuleEngineService.buildAllocationStockDO(demandDo, supplyDo);
            allocationStockDO.setMatName(supplyDo.getMatName());
            allocationStockDO.setMatCode(supplyDo.getMatCode());
            allocationStockDO.setCategoryCode(supplyDo.getCategoryCode());
            allocationStockDO.setSizeId(supplyDo.getSizeId());
            allocationStockDO.setSizeName(supplyDo.getSizeName());
            allocationStockDO.setSizeCode(supplyDo.getSizeCode());
            allocationStockDO.setAllocationStockQty(supplyDo.getSupplyStockQty());
            allocationStockDOList.add(allocationStockDO);
        }

        allocationStockDOList.forEach(allocation->{
            allocation.setAllocationDate(currentDate);
            allocation.setTaskId(task.getTaskId());
        });

        int succCount = 0;
        if (CollectionUtils.isNotEmpty(allocationStockDOList)) {
            // 插入新数据
            insertPool.execute(() -> {
                allocationStockService.batchInsert(allocationStockDOList);
            });

            succCount = allocationStockDOList.size();

            // 流向追踪数据-用于统计
            AllocationStockDO allocationStockDO = allocationStockDOList.get(0);
            AllocationFlowDO allocationFlowDO = new AllocationFlowDO();
            long qty = allocationStockDOList.stream().collect(Collectors.summingLong(AllocationStockDO::getAllocationStockQty));
            double amount = allocationStockDOList.parallelStream().collect(Collectors.summingDouble(allocation -> {
                return (allocation.getAllocationStockQty() * allocation.getQuotePrice());
            }));

            allocationFlowDO.setDemandShopId(allocationStockDO.getShopId());
            allocationFlowDO.setDemandShopName(allocationStockDO.getShopName());
            allocationFlowDO.setSupplyShopId(allocationStockDO.getSupplyShopId());
            allocationFlowDO.setSupplyShopName(allocationStockDO.getSupplyShopName());
            allocationFlowDO.setAllocationQty((int)qty);
            allocationFlowDO.setAllocationAmount(amount);
            allocationFlowDO.setDemandAmount(amount);
            allocationFlowDO.setFee(0D);
            allocationFlowDO.setRate(0D);
            allocationFlowDO.setTaskId(task.getTaskId());
            allocationFlowDO.setMatchOrder(1000);
            allocationFlowDO.setMatchFlag(1); // 成功

            List<AllocationFlowDO> allocationFlowDOList = new ArrayList<>();
            allocationFlowDOList.add(allocationFlowDO);
            allocationFlowDOMapper.batchInsert(allocationFlowDOList);

        }

        return succCount;

        //快递单是否要生成?
//        List<AllocationStockDO> allocationStockDOList = this.genAllocationList(task,amountDemandList,supplyList,bestMatchSupplyShop);
//
//        // 流向追踪数据-用于统计
//        AllocationFlowDO allocationFlowDO = this.genAllocationFlow(task.getTaskId(),bestMatchSupplyShop,allocationStockDOList,times);
//        allocationFlowDOList.add(allocationFlowDO);
//
//        // 插入流向数据
//        try {
//            if (CollectionUtils.isNotEmpty(allocationFlowDOList)) {
//                allocationFlowDOMapper.batchInsert(allocationFlowDOList);
//            }
//        }catch (Exception e) {
//            LoggerUtil.error(e,logger,"[INSERT_FLOW]");
//        }
    }

    /**
     * 计算需求、供给库存
     * @param outOfStockGoodsDOList
     */
    private void calcDemandAndSupplyStockQty(Task task,List<OutOfStockGoodsDO> outOfStockGoodsDOList,List<OutOfStockGoodsDO> inStockShopList,List<OutOfStockGoodsDO> outStockShopList) {

        for (OutOfStockGoodsDO stock : outOfStockGoodsDOList) {

            // 计算需求、供给队列
            if (OutOfStockGoodsDO.ENABLE_TYPE == stock.getForbiddenFlag()) {

                /*
                    门店库存<理想库存 (需求：理想库存 - 门店库存)
                    门店库存>理想库存 (供给：门店库存 - 理想库存)
                    门店库存==理想库存 （供给、需求为0）
                 */
                if (stock.getStoreQty() < stock.getIdealStockQty()) {
                    int demandQty = 0;

                    // 14天调出的，不调入
                    if (outStockShopList.stream().filter(inStock->inStock.getKey().equals(stock.getKey())).count() > 0) {
                        demandQty = -1;
                        LoggerUtil.warn(logger,"[REJECT_DEMAND] msg=key:{0}",stock.getKey());
                    } else {
                        demandQty = stock.getIdealStockQty() - stock.getStoreQty();
                    }
                    stock.setDemandStockQty(demandQty);
                    stock.setSupplyStockQty(0);
                } else if  (stock.getStoreQty() > stock.getIdealStockQty()) {
                    int supplyQty = 0;
                    // 7天有调入的，不调出
                    if (inStockShopList.stream().filter(inStock->inStock.getKey().equals(stock.getKey())).count() > 0) {
                        supplyQty = -1;
                        LoggerUtil.warn(logger,"[REJECT_SUPPLY] msg=key:{0}",stock.getKey());
                    } else {
                        supplyQty = stock.getStoreQty() - stock.getIdealStockQty();
                    }

                    stock.setSupplyStockQty(supplyQty);
                    stock.setDemandStockQty(0);
                } else {
                    stock.setDemandStockQty(0);
                    stock.setSupplyStockQty(0);
                }
            }
        }

    }

    private void saveOutOfStockList(Task task,List<OutOfStockGoodsDO> outOfStockGoodsDOList) {
        // 批量插入数据
        int PAGE_SIZE = 1000;
        if (outOfStockGoodsDOList.size()>0 && outOfStockGoodsDOList.size() > PAGE_SIZE) {
            int index = 0;
            while (true) {
                List<OutOfStockGoodsDO>  subList = outOfStockGoodsDOList.stream().skip(index * PAGE_SIZE).limit(PAGE_SIZE).collect(Collectors.toList());
                if (subList == null || subList.isEmpty()) {
                    break;
                }
                outOfStockGoodsService.insertSelective(task,subList);
                index++;
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                }
            }
        } else {
            outOfStockGoodsService.insertSelective(task,outOfStockGoodsDOList);
        }
    }

    private List<AllocationStockDO> genAllocationList(Task task,List<OutOfStockGoodsDO> demandList,List<OutOfStockGoodsDO> supplyList,AllocationShop preAllocationShop) {

        if (preAllocationShop == null) {
            return  null;
        }

        List<AllocationStockDO> preAllocationStockList = preAllocationShop.getAllocationStockDOList();

        // 起调金额判断
        double amount = preAllocationStockList.stream().collect(Collectors.summingDouble(allocation->{
            return (allocation.getAllocationStockQty() * allocation.getQuotePrice());
        }));

        if (amount < task.getMinAllocationPrice()) {

            if (Task.ALLOCATION_TYPE_REJECT != task.getAllocationType()) {
                List<AllocationFlowDO> allocationFlowDOList = new ArrayList<>();
                AllocationFlowDO allocationFlowDO = this.genAllocationFlow(task.getTaskId(), preAllocationShop, preAllocationStockList, -1, Constant.FAIL_MATCH, Constant.FAIL_REASON_FEE);
                allocationFlowDOList.add(allocationFlowDO);
                allocationFlowDOMapper.batchInsert(allocationFlowDOList);
            }

            LoggerUtil.warn(logger,"[LESS_THAN_MIN_LATCH] msg=taskId:{0},demandShop:{1},supplyShop:{2},amount:{3}",task.getTaskId(),preAllocationShop.getDemandShopId(),preAllocationShop.getSupplyShopId(),amount);
            return  null;
        }

        List<AllocationStockDO> list = new ArrayList<>();
        List<AllocationStockDO> excludeList = new ArrayList<>();

        for (AllocationStockDO stock : preAllocationStockList) {

            boolean include = true;
            for (OutOfStockGoodsDO demand : demandList) {
                if (preAllocationShop.getDemandShopId().equals(demand.getShopId())
                        && stock.getMatCode().equals(demand.getMatCode())
                        && stock.getSizeId().equals(demand.getSizeId())) {

                    int demandQty = demand.getRemainStockQty() == null ? demand.getDemandStockQty() : demand.getRemainStockQty();

                    if (demandQty > 0) {
                        demand.setRemainStockQty(demandQty - stock.getAllocationStockQty());
                    } else {
                        // 剩余库存为0，剔除该调拨单
                        include = false;
                    }
                    break;
                }
            }

            for (OutOfStockGoodsDO supply : supplyList) {
                if (preAllocationShop.getSupplyShopId().equals(supply.getShopId())
                        && stock.getMatCode().equals(supply.getMatCode())
                        && stock.getSizeId().equals(supply.getSizeId())) {

                    int supplyQty = supply.getRemainStockQty() == null ? supply.getSupplyStockQty() : supply.getRemainStockQty();

                    if (supplyQty > 0) {
                        supply.setRemainStockQty(supplyQty - stock.getAllocationStockQty());
                    } else {
                        // 剩余库存为0，剔除该调拨单
                        include = false;
                    }
                    break;
                }
            }

            if (include) {
                stock.setTaskId(task.getTaskId());
                list.add(stock);
            } else {
                excludeList.add(stock);
            }
        }

        return list;
    }


    private AllocationFlowDO genAllocationFlow(int taskId,AllocationShop bestMatchSupplyShop,List<AllocationStockDO> allocationStockDOList,int matchOrder,int matchFlag,int reason) {

        AllocationFlowDO allocationFlowDO = new AllocationFlowDO();
        long qty = allocationStockDOList.stream().collect(Collectors.summingLong(AllocationStockDO::getAllocationStockQty));

        List<DwsDimShopDO> shopDOList = shopListCache.getShopList();
        String demandShopName  = shopDOList.stream().filter(shop->shop.getShopId().equals(bestMatchSupplyShop.getDemandShopId())).map(DwsDimShopDO::getShopName).findFirst().orElse(null);
        String supplyShopName  = shopDOList.stream().filter(shop->shop.getShopId().equals(bestMatchSupplyShop.getSupplyShopId())).map(DwsDimShopDO::getShopName).findFirst().orElse(null);

        allocationFlowDO.setDemandShopId(bestMatchSupplyShop.getDemandShopId());;
        allocationFlowDO.setSupplyShopId(bestMatchSupplyShop.getSupplyShopId());
        allocationFlowDO.setDemandShopName(demandShopName);
        allocationFlowDO.setSupplyShopName(supplyShopName);
        allocationFlowDO.setAllocationQty((int)qty);
        allocationFlowDO.setAllocationAmount(bestMatchSupplyShop.getAmount());
        allocationFlowDO.setDemandAmount(bestMatchSupplyShop.getDemandAmount());
        allocationFlowDO.setFee(bestMatchSupplyShop.getFee());
        allocationFlowDO.setRate(bestMatchSupplyShop.getRate());
        allocationFlowDO.setTaskId(taskId);
        allocationFlowDO.setMatchOrder(matchOrder);
        allocationFlowDO.setMatchFlag(matchFlag); // 成功
        allocationFlowDO.setFailReason(reason);

        return allocationFlowDO;
    }


    /**
     * 同步调拨数据到官远
     */
    private void clickSyncToGY() {

        String result_1 = HttpClientUtil.sendGet("http://lh.nome.cn/public-api/data-source/c9bec50dc88f440308f05f50/refresh","token=v6145abca8a3849d2bac7dd3");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        String result_2 = HttpClientUtil.sendGet("http://lh.nome.cn/public-api/data-source/d4670e21e47ce48559a43a96/refresh","token=t991d4eb073ad4ee6b5dcdc9");

        LoggerUtil.info(logger,"[GUAN_YUAN] msg=result_1::{0},result_2:{1}",result_1,result_2);
    }

    /**
     * 排除撤店调拨涉及的skc列表
     * @param allocationType
     * @param supplyList
     * @param closeAlloctionList
     * @return
     */
    private List<OutOfStockGoodsDO>  excludeCloseSkuList(int allocationType,List<OutOfStockGoodsDO> supplyList,List<AllocationStockDO> closeAlloctionList) {

        if (CollectionUtils.isEmpty(supplyList) || CollectionUtils.isEmpty(closeAlloctionList)) {
            return supplyList;
        }

        List<OutOfStockGoodsDO> newList = new ArrayList<>();

        for (OutOfStockGoodsDO outOfStockGoodsDO : supplyList) {
            boolean exists = false;
            for (AllocationStockDO allocationStockDO : closeAlloctionList) {

                if (Task.ALLOCATION_TYPE_REJECT == allocationType) {
                    // 撤店（减去对应的库存）
                    if (outOfStockGoodsDO.getShopId().equals(allocationStockDO.getSupplyShopId()) &&
                            outOfStockGoodsDO.getMatCode().equals(allocationStockDO.getMatCode())
                            && outOfStockGoodsDO.getSizeId().equals(allocationStockDO.getSizeId())) {
                        int qty = outOfStockGoodsDO.getSupplyStockQty() - allocationStockDO.getAllocationStockQty();
                        outOfStockGoodsDO.setSupplyStockQty(qty);
                        outOfStockGoodsDO.setRemainStockQty(qty);
                        if (qty == 0) {
                            exists = true;
                        }
                    }
                } else {
                    // 平衡&归并 （直接将sku剔去）
                    if (outOfStockGoodsDO.getShopId().equals(allocationStockDO.getShopId()) &&
                            outOfStockGoodsDO.getMatCode().equals(allocationStockDO.getMatCode())
                            && outOfStockGoodsDO.getSizeId().equals(allocationStockDO.getSizeId())) {
                        exists = true;
                        LoggerUtil.info(logger,"[CLOSE_SHOP] msg=matcode:{0},sizeId:{1}",allocationStockDO.getMatCode(),allocationStockDO.getSizeId());
                        continue;
                    }
                }

            }

            if (!exists) {
                newList.add(outOfStockGoodsDO);
            }
        }

        return newList;

    }


}
