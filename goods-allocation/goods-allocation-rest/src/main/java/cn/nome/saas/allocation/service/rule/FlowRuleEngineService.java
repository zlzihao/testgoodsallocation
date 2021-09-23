package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.AllocationShop;
import cn.nome.saas.allocation.model.allocation.DemandShop;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.model.rule.NewExpress;
import cn.nome.saas.allocation.model.rule.NewShopExpress;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationFlowDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 流向规则计算引擎
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
@Service
public class FlowRuleEngineService {

    private static Logger logger = LoggerFactory.getLogger(FlowRuleEngineService.class);

    @Autowired
    ShopExpressService shopExpressService;

    @Autowired
    AllocationFlowDOMapper allocationFlowDOMapper;


    @Autowired
    ShopListCache shopListCache ;


    //@Value("${express.rate}")
    //private double expressRate;

    ExecutorService calcScorePool = Executors.newFixedThreadPool(8);

    /**
     * 计算需求金额&排名
     * @param demandList
     */
    public List<OutOfStockGoodsDO> calcDemandAmount(Task task, List<OutOfStockGoodsDO> demandList,List<String> skipShopIdList) {

        List<DemandShop> demandShopList = new ArrayList<>();

        Map<String,List<OutOfStockGoodsDO>> demandStockMap = demandList.stream().collect(Collectors.groupingBy(OutOfStockGoodsDO::getShopId,Collectors.toList()));

        // 计算需求总金额
        for (String shopId : demandStockMap.keySet()) {

             double amount = demandStockMap.get(shopId).stream().collect(Collectors.summingDouble(demand->{
                 int demandQty = demand.getRemainStockQty() == null ? demand.getDemandStockQty() : demand.getRemainStockQty();
               return (demandQty * demand.getQuotePrice());
            }));

            if (amount >= task.getMinAllocationPrice())  {
                DemandShop demandShop = new DemandShop();
                demandShop.setShopId(shopId);
                demandShop.setDemandAmount(amount);
                demandShop.setDemandList(demandStockMap.get(shopId));
                demandShopList.add(demandShop);
            }
        }

        // 按店铺需求总金额排名,拿排名第一的
        demandShopList = demandShopList.stream()
                .sorted(Comparator.comparing(DemandShop::getDemandAmount).reversed()).collect(Collectors.toList());
        DemandShop demandShop = null;

        for (DemandShop demand : demandShopList) {
            if (!skipShopIdList.contains(demand.getShopId())) {
                demandShop = demand;
                break;
            }
        }


        return demandShop != null ? demandShop.getDemandList().stream().filter(demand->demand.getRemainStockQty()== null || demand.getRemainStockQty()>0).collect(Collectors.toList()) : null;

    }

    /**
     * 匹配评分
     * @param demandList
     * @param supplyList
     * @return
     */
    public AllocationShop calcSupplyScore(Task task,List<OutOfStockGoodsDO> demandList, List<OutOfStockGoodsDO> supplyList,Set<String> highRateRejectShop) {

        Set<String> shopIdList = demandList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());
        Set<String> supplyShopIdList = supplyList.stream().map(OutOfStockGoodsDO::getShopId).collect(Collectors.toSet());

        shopIdList.addAll(supplyShopIdList);

        List<String> provinceList = shopListCache.getShopList().stream()
                .filter(shop->shopIdList.contains(shop.getShopId()))
                .map(DwsDimShopDO::getProvinceName)
                .distinct()
                .collect(Collectors.toList());

        List<NewShopExpress> newShopExpressList = shopExpressService.selectNewExpressByProvince(provinceList);

        Long start = System.currentTimeMillis();

        Map<String,List<OutOfStockGoodsDO>> demandMap = demandList.parallelStream().collect(Collectors.groupingBy(OutOfStockGoodsDO::getShopId,Collectors.toList()));
        Map<String,List<OutOfStockGoodsDO>> supplyMap = supplyList.parallelStream().collect(Collectors.groupingBy(OutOfStockGoodsDO::getShopId,Collectors.toList()));

        /**
         * 需求店与供给店逐个匹配，生成"待调拨明细"，计算出匹配分值，排序
         */
        List<Future<AllocationShop>> futureList = new ArrayList<>();
        List<AllocationShop> allocationShopList = new ArrayList<>();

        for (String demandShopId : demandMap.keySet()) {

            List<OutOfStockGoodsDO> demandSubList = demandMap.get(demandShopId);

            for (String supplyShopId : supplyMap.keySet()) {

                // 供给店与需求店相同时，不执行
                if (demandShopId.equals(supplyShopId)) {
                    LoggerUtil.warn(logger,"[THE_SAME_SHOP] msg=shopId:{0}",demandShopId);
                    continue;
                }

                // 需求、供给店已经在上轮匹配中出现费率过高的情况，直接reject
                if(highRateRejectShop.contains(demandShopId+"_"+supplyShopId)) {
                    continue;
                }

                List<OutOfStockGoodsDO> subList = supplyMap.get(supplyShopId);

                List<NewShopExpress> expressDOList = newShopExpressList;
                Future<AllocationShop> allocationFuture = calcScorePool.submit(new Callable<AllocationShop>() {
                    @Override
                    public AllocationShop call() throws Exception {
                        return calcAllocationDetail(task, demandShopId,demandSubList,supplyShopId, subList, expressDOList);
                    }
                });
                futureList.add(allocationFuture);
            }
        }

        for (Future<AllocationShop> future : futureList) {
            try {
                AllocationShop allocationShop = future.get();
                if (allocationShop != null) {
                    if (allocationShop.getRate() >0) {
                        allocationShopList.add(allocationShop);
                    } else {
                        // 非法费率（说明费率超过阀值）
                        highRateRejectShop.add(allocationShop.getDemandShopId()+"_"+allocationShop.getSupplyShopId());
                    }
                }
            } catch (Exception e) {
            }
        }

        Long end = System.currentTimeMillis();
        LoggerUtil.debug(logger,"[CALC_SCORE] msg=size:{0},time:{1}",allocationShopList.size(),(end-start));

        // 根据匹配分值排序，首先对匹配分值大的门店进行分配
        allocationShopList =  allocationShopList.stream()
                .sorted(Comparator.comparing(AllocationShop::getScore).reversed())
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(allocationShopList)) {
            return allocationShopList.get(0);
        }

        return null;
    }


    private AllocationShop calcAllocationDetail(Task task,String demandShopId,List<OutOfStockGoodsDO> demandSubList,String supplyShopId,
                                                List<OutOfStockGoodsDO> subList,List<NewShopExpress> newShopExpressList) {

        List<DwsDimShopDO> dwsDimShopDOList = shopListCache.getShopList();
        DwsDimShopDO demandShop = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(demandShopId)).findFirst().orElse(null);
        DwsDimShopDO supplyShop = dwsDimShopDOList.stream().filter(shop->shop.getShopId().equals(supplyShopId)).findFirst().orElse(null);

        // 需求店、供给店匹配

        List<AllocationStockDO> detailList = new ArrayList<>();

        for (OutOfStockGoodsDO demand : demandSubList) {
            for (OutOfStockGoodsDO supply : subList) {
                if (demand.getMatCode().equals(supply.getMatCode())
                        && demand.getSizeId().equals(supply.getSizeId())) {
                    // 计算可调拨库存
                    int demandQty = demand.getRemainStockQty() == null ? demand.getDemandStockQty() : demand.getRemainStockQty();
                    int supplyQty = supply.getRemainStockQty() == null ? supply.getSupplyStockQty() : supply.getRemainStockQty();
                    if (demandQty > 0 && supplyQty > 0) {
                        AllocationStockDO allocationStockDO = this.buildAllocationStockDO(demand, supply);
                        detailList.add(allocationStockDO);

                    }

                }
            }
        }

        if (detailList.size() > 0) {
            AllocationShop allocationShop = new AllocationShop();
            allocationShop.setDemandShopId(demandShopId);
            allocationShop.setSupplyShopId(supplyShopId);
            allocationShop.setAllocationStockDOList(detailList);

            // 快递
            for (NewShopExpress newShopExpress : newShopExpressList) {
                /*if ((expressDO.getShopIdA().equals(demandShopId) && expressDO.getShopIdB().equals(supplyShopId))
                        || (expressDO.getShopIdA().equals(supplyShopId) && expressDO.getShopIdB().equals(demandShopId))) {
                    allocationShop.setShopExpressDO(expressDO);
                    break;
                }*/

                String p1 = demandShop.getProvinceName();
                String p2 = supplyShop.getProvinceName();
                String c1 = demandShop.getCityName();
                String c2 = supplyShop.getCityName();

                if ((newShopExpress.getProvince1().equals(p1) && newShopExpress.getProvince2().equals(p2) &&
                        newShopExpress.getCity1().contains(c1) && newShopExpress.getCity2().contains(c2)
                )||
                    (newShopExpress.getProvince1().equals(p2) && newShopExpress.getProvince2().equals(p1) &&
                                newShopExpress.getCity1().contains(c2) && newShopExpress.getCity2().contains(c1))) {

                    allocationShop.setNewShopExpress(newShopExpress);
                }
            }

            // 计算可满足需求金额
            double amount = detailList.parallelStream().collect(Collectors.summingDouble(allocation -> {
                return (allocation.getAllocationStockQty() * allocation.getQuotePrice());
            }));

            // 总可调拨库存
            int totalAllocationQty = detailList.parallelStream().collect(Collectors.summingInt(AllocationStockDO::getAllocationStockQty));
            // 运费
            NewShopExpress expressDO = allocationShop.getNewShopExpress();
            if (expressDO == null) {
                expressDO = NewShopExpress.getDefault();
                LoggerUtil.warn(logger, "[EXPRESS_EMPTY] msg=shopId A:{0},shopId B:{1}", allocationShop.getDemandShopId(), allocationShop.getSupplyShopId());
            }
            double fee = shopExpressService.calcShippingFree(expressDO, task.getTaskType(), totalAllocationQty);
            // 计算费率 = （货品重量*快递费用）/ 可满足需求金额
            double rate = fee / amount;
            double demandAmount = detailList.parallelStream().collect(Collectors.summingDouble(allocation -> {
                return (allocation.getDemandStockQty() * allocation.getQuotePrice());
            }));

            int allocationPeriod = task.getInDays(); //  安全天数

            // 计算匹配分值 （可满足需求金额 - 在途天数 *(∑(需求量 * 吊牌价)/安全天数) - 货品重量*快递费用）
            double score = amount - (expressDO.getSpendDays() * demandAmount) / allocationPeriod - fee;

            // 费率,高于费率阈值不予匹配
            if (rate > task.getMaxFeeRatio()) {

                if (Task.ALLOCATION_TYPE_REJECT != task.getAllocationType()) {
                    List<AllocationFlowDO> allocationFlowDOList = new ArrayList<>();
                    AllocationFlowDO allocationFlowDO = this.genFailAllocationFlow(task.getTaskId(), allocationShop, demandAmount, amount, fee, rate, totalAllocationQty);
                    allocationFlowDOList.add(allocationFlowDO);
                    allocationFlowDOMapper.batchInsert(allocationFlowDOList);
                }

                LoggerUtil.warn(logger,"[HIGH_EXPRESS_RATE] msg = taskId:{0},demandShopId:{1},supplyShopId:{2},demandAmount:{3},supplyAmount:{4},fee:{5},rate:{6},qty:{7}",task.getTaskId(),demandShopId,supplyShopId,demandAmount,amount,fee,rate,totalAllocationQty);

                // 设置一个不合法的费率，用于外层判断
                allocationShop.setRate(-1D);
                return allocationShop;
            }

            allocationShop.setDemandAmount(demandAmount);
            allocationShop.setAmount(amount);
            allocationShop.setRate(rate);
            allocationShop.setScore(score);
            allocationShop.setFee(fee);

            return allocationShop;
        }

        return null;

    }

    private AllocationFlowDO genFailAllocationFlow(int taskId,AllocationShop allocationShop,double demandAmount,double amound,double fee,double rate,int totalAllocationQty) {
        AllocationFlowDO allocationFlowDO = new AllocationFlowDO();

        allocationFlowDO.setTaskId(taskId);

        List<DwsDimShopDO> shopDOList = shopListCache.getShopList();
        String demandShopName  = shopDOList.stream().filter(shop->shop.getShopId().equals(allocationShop.getDemandShopId())).map(DwsDimShopDO::getShopName).findFirst().orElse(null);
        String supplyShopName  = shopDOList.stream().filter(shop->shop.getShopId().equals(allocationShop.getSupplyShopId())).map(DwsDimShopDO::getShopName).findFirst().orElse(null);

        allocationFlowDO.setDemandShopId(allocationShop.getDemandShopId());;
        allocationFlowDO.setSupplyShopId(allocationShop.getSupplyShopId());
        allocationFlowDO.setDemandShopName(demandShopName);
        allocationFlowDO.setSupplyShopName(supplyShopName);
        allocationFlowDO.setAllocationQty(totalAllocationQty);
        allocationFlowDO.setAllocationAmount(amound);
        allocationFlowDO.setDemandAmount(demandAmount);
        allocationFlowDO.setFee(fee);
        allocationFlowDO.setRate(rate);
        allocationFlowDO.setTaskId(taskId);
        allocationFlowDO.setMatchOrder(-1);
        allocationFlowDO.setMatchFlag(Constant.FAIL_MATCH); // 成功
        allocationFlowDO.setFailReason(Constant.FAIL_REASON_RATE);



        return allocationFlowDO;
    }


    public AllocationStockDO buildAllocationStockDO(OutOfStockGoodsDO demand,OutOfStockGoodsDO supply) {
        AllocationStockDO allocationStockDO = new AllocationStockDO();

        BeanUtils.copyProperties(demand,allocationStockDO);

        // 计算调拨库存
        int allocationStockQty = 0;

        int demandQty = (demand.getRemainStockQty() == null || demand.getRemainStockQty()==0) ? demand.getDemandStockQty() : demand.getRemainStockQty();
        int supplyQty = (supply.getRemainStockQty() == null || supply.getRemainStockQty()==0) ? supply.getSupplyStockQty() : supply.getRemainStockQty();

        if (demandQty >= supplyQty) {
            allocationStockQty = supplyQty;
        } else {
            allocationStockQty = demandQty;
        }

        allocationStockDO.setSupplyShopId(supply.getShopId());
        allocationStockDO.setSupplyShopName(supply.getShopName());
        allocationStockDO.setSupplyShopLevel(supply.getShopLevel());
        allocationStockDO.setSupplyCityName(supply.getCityName());
        allocationStockDO.setSupplyAvgSaleQty(supply.getAvgSaleQty());
        allocationStockDO.setSupplySumSaleQty(supply.getSumSaleQty());
        allocationStockDO.setQuotePrice(supply.getQuotePrice());
        allocationStockDO.setSupplyStockQty(supply.getSupplyStockQty());
        allocationStockDO.setAllocationStockQty(allocationStockQty);
        // 新增字段 190726
        allocationStockDO.setMinDisplayQty(demand.getMinDisplayQty()); // 最小陈列
        allocationStockDO.setDemandForbiddenFlag(demand.getForbiddenFlag()); // 调入禁配标示
        allocationStockDO.setSupplyForbiddenFlag(supply.getForbiddenFlag()); // 调出禁配标示


        return allocationStockDO;
    }

}
