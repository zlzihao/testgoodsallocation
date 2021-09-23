package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.AllocationShop;
import cn.nome.saas.allocation.model.allocation.DemandShop;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.model.rule.NewShopExpress;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationFlowDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationFlowDO;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.OutOfStockGoodsDO;
import cn.nome.saas.allocation.repository.entity.vertical.AllocationGoodsSKC;
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
public class ClothingFlowRuleEngineService {

    private static Logger logger = LoggerFactory.getLogger(ClothingFlowRuleEngineService.class);

    @Autowired
    ShopExpressService shopExpressService;

    @Autowired
    AllocationFlowDOMapper allocationFlowDOMapper;

    @Autowired
    ShopListCache shopListCache ;

    @Value("${clothing.allocation.switch}")
    private int clothingSwitch;


    /**
     * 匹配评分
     * @param demandList
     * @param supplyList
     * @return
     */
    public AllocationShop calcSupplyScoreV2(Task task,List<OutOfStockGoodsDO> demandList, List<OutOfStockGoodsDO> supplyList,Set<String> highRateRejectShop,List<AllocationGoodsSKC> allocationGoodsSKCList,List<NewShopExpress> newShopExpressList) {

        Map<String,List<OutOfStockGoodsDO>> demandMap = demandList.stream().collect(Collectors.groupingBy(OutOfStockGoodsDO::getShopId,Collectors.toList()));
        Map<String,List<OutOfStockGoodsDO>> supplyMap = supplyList.stream().collect(Collectors.groupingBy(OutOfStockGoodsDO::getShopId,Collectors.toList()));

        /**
         * 需求店与供给店逐个匹配，生成"待调拨明细"，计算出匹配分值，排序
         */
        AllocationShop allocationShop = null;
        boolean finish = false;
        Map<String,Set<String>> shopSkipGoodMap = new HashMap<>();

        while (true) {

            if (finish){break;}

            for (String demandShopId : demandMap.keySet()) {

                List<AllocationShop> allocationShopList = new ArrayList<>();
                // 需求店
                List<OutOfStockGoodsDO> demandSubList = demandMap.get(demandShopId);

                Set<String> supplShopList = supplyMap.keySet();

                // 供给店列表
                for (String supplyShopId : supplShopList) {

                    // 供给店与需求店相同时，不执行
                    if (demandShopId.equals(supplyShopId)) {
                        LoggerUtil.warn(logger, "[THE_SAME_SHOP] msg=shopId:{0}", demandShopId);
                        continue;
                    }

                    // 需求、供给店已经在上轮匹配中出现费率过高的情况，直接reject
                    if (highRateRejectShop.contains(demandShopId + "_" + supplyShopId)) {
                        continue;
                    }

                    List<NewShopExpress> expressDOList = newShopExpressList;

                    // 单个供给店所有的商品清单
                    List<OutOfStockGoodsDO> subList = supplyMap.get(supplyShopId);

                    if (shopSkipGoodMap.containsKey(supplyShopId)) {
                        // 剔除掉商品
                        Set<String> skipGoodsMatecodeSet = shopSkipGoodMap.get(supplyShopId);
                        //
                        LoggerUtil.info(logger,"[SKIP_BEFORE] msg=supplyList:{0}",subList.size());
                        subList = subList.stream()
                                .filter(outOfStockGoodsDO -> !skipGoodsMatecodeSet.contains(outOfStockGoodsDO.getMatCode()))
                                .collect(Collectors.toList());
                        LoggerUtil.info(logger,"[SKIP_AFTER] msg=supplyList:{0}",subList.size());
                    }

                    AllocationShop allocationDetail = this.calcAllocationDetail(task, demandShopId, demandSubList, supplyShopId, subList, expressDOList);
                    if (allocationDetail != null) {

                        if (allocationDetail.getRate() <= 0) {
                            // 非法费率（说明费率超过阀值）
                            highRateRejectShop.add(allocationDetail.getDemandShopId() + "_" + allocationDetail.getSupplyShopId());
                            continue;
                        }

                        allocationShopList.add(allocationDetail);
                    }
                }

                if (CollectionUtils.isEmpty(allocationShopList)) {
                    finish = true;
                    allocationShop = null;
                    break;
                }
                /*
                    服装满场下限修复判断
                 */
                // 根据匹配分值排序，首先对匹配分值大的门店进行分配
                allocationShopList =  allocationShopList.stream()
                        .sorted(Comparator.comparing(AllocationShop::getScore).reversed())
                        .collect(Collectors.toList());

                allocationShop = allocationShopList.get(0); // 第一家

                /* =================================

                    增加开关控制，开启的时候，不执行下限修复

                   =================================
                 */
                if (clothingSwitch == 1){
                    finish = true;
                    break;
                }

                LoggerUtil.warn(logger,"===============  [最佳匹配门店] ===================");
                LoggerUtil.warn(logger,"需求店:{0},供给店:{1},需求金额:{2},可调拨金额:{3}",allocationShop.getDemandShopId(),allocationShop.getSupplyShopId(),allocationShop.getDemandAmount(),allocationShop.getAmount());
                String supplyShopId = allocationShop.getSupplyShopId();
                List<AllocationGoodsSKC> allocationGoodsList =  allocationGoodsSKCList.stream()
                        .filter(allocationGoodsSKC -> allocationGoodsSKC.getShopId().equals(supplyShopId))
                        .collect(Collectors.toList());

                if (CollectionUtils.isEmpty(allocationGoodsList)) {
                    return allocationShop;
                }

                long maleCount = allocationShop
                        .getAllocationStockDOList()
                        .stream()
                        .filter(allocationStockDO -> "M".equals(allocationStockDO.getCategoryCode()))
                        .count();

                long femaleCount = allocationShop
                        .getAllocationStockDOList()
                        .stream()
                        .filter(allocationStockDO -> "W".equals(allocationStockDO.getCategoryCode()))
                        .count();

                AllocationGoodsSKC femaleSKC = allocationGoodsList.stream().filter(allocationGoodsSKC -> allocationGoodsSKC.getCategoryCode().equals("W"))
                                            .findFirst().orElse(null);
                AllocationGoodsSKC maleSKC = allocationGoodsList.stream().filter(allocationGoodsSKC -> allocationGoodsSKC.getCategoryCode().equals("M"))
                        .findFirst().orElse(null);
                LoggerUtil.warn(logger,"供给店：{0}",allocationShop.getSupplyShopId());
                LoggerUtil.warn(logger,"女装：{0}有效skc，{1}款无效skc，{2}skc下限",femaleSKC.getSkcCount(),femaleSKC.getInvalidSkcStyle(),femaleSKC.getLowStandardSkcCount());
                LoggerUtil.warn(logger,"男装：{0}有效skc，{1}款无效skc，{2}skc下限",maleSKC.getSkcCount(),maleSKC.getInvalidSkcStyle(),maleSKC.getLowStandardSkcCount());

                if (maleCount > 0 && femaleCount > 0) {
                    boolean maleFlag = this.checkSkcCount(allocationShop,maleSKC,"M");
                    boolean femaleFlag = this.checkSkcCount(allocationShop,femaleSKC,"W");

                    LoggerUtil.warn(logger,"男装满场下限：{0},女装满场下限:{1}",maleFlag,femaleFlag);

                    if (maleFlag && femaleFlag) {
                        finish = true;
                        LoggerUtil.warn(logger,"[男女装尺码下限校验通过] msg=allocationShop:{0}",allocationShop.getSupplyShopId());
                        break;
                    }

                    if (!maleFlag) {
                        Set<String> maleKickGoods = this.getkickSupplyGoods(allocationShop,maleSKC);
                        if (CollectionUtils.isNotEmpty(maleKickGoods)) {
                            if (shopSkipGoodMap.containsKey(allocationShop.getSupplyShopId())) {
                                shopSkipGoodMap.get(allocationShop.getSupplyShopId()).addAll(maleKickGoods);
                            } else {
                                shopSkipGoodMap.put(allocationShop.getSupplyShopId(),maleKickGoods);
                            }
                            LoggerUtil.debug(logger,"[剔除男装商品] msg=shop:{0},matcodeList:{1}",allocationShop.getSupplyShopId(),maleKickGoods);
                        }
                    }
                    if (!femaleFlag) {
                        Set<String> femaleKickGoods = this.getkickSupplyGoods(allocationShop,femaleSKC);
                        if (CollectionUtils.isNotEmpty(femaleKickGoods)) {
                            if (shopSkipGoodMap.containsKey(allocationShop.getSupplyShopId())) {
                                shopSkipGoodMap.get(allocationShop.getSupplyShopId()).addAll(femaleKickGoods);
                            } else {
                                shopSkipGoodMap.put(allocationShop.getSupplyShopId(),femaleKickGoods);
                            }
                            LoggerUtil.debug(logger,"[剔除女装商品] msg=shop:{0},matcodeList:{1}",allocationShop.getSupplyShopId(),femaleKickGoods);
                        } else {

                        }

                    }
                } else if (maleCount > 0) {
                    boolean maleFlag = this.checkSkcCount(allocationShop,maleSKC,"M");
                    if (!maleFlag) {
                        Set<String> maleKickGoods = this.getkickSupplyGoods(allocationShop,maleSKC);
                        if (CollectionUtils.isNotEmpty(maleKickGoods)) {
                            if (shopSkipGoodMap.containsKey(allocationShop.getSupplyShopId())) {
                                shopSkipGoodMap.get(allocationShop.getSupplyShopId()).addAll(maleKickGoods);
                            } else {
                                shopSkipGoodMap.put(allocationShop.getSupplyShopId(),maleKickGoods);
                            }
                            LoggerUtil.debug(logger,"[剔除男装商品] msg=shop:{0},matcodeList:{1}",allocationShop.getSupplyShopId(),maleKickGoods);
                        }
                    } else {
                        finish = true;
                        LoggerUtil.warn(logger,"[男装尺码下限校验通过] msg=allocationShop:{0}",allocationShop.getSupplyShopId());
                        break;
                    }
                } else if (femaleCount > 0) {
                    boolean femaleFlag = this.checkSkcCount(allocationShop,femaleSKC,"W");
                    if (!femaleFlag) {
                        Set<String> femaleKickGoods = this.getkickSupplyGoods(allocationShop,femaleSKC);
                        if (CollectionUtils.isNotEmpty(femaleKickGoods)) {
                            if (shopSkipGoodMap.containsKey(allocationShop.getSupplyShopId())) {
                                shopSkipGoodMap.get(allocationShop.getSupplyShopId()).addAll(femaleKickGoods);
                            } else {
                                shopSkipGoodMap.put(allocationShop.getSupplyShopId(),femaleKickGoods);
                            }
                            LoggerUtil.debug(logger,"[剔除女装商品] msg=shop:{0},matcodeList:{1}",allocationShop.getSupplyShopId(),femaleKickGoods);
                        }

                    } else {
                        finish = true;
                        LoggerUtil.warn(logger,"[女装尺码下限校验通过] msg=allocationShop:{0}",allocationShop.getSupplyShopId());
                        break;
                    }
                }
            }
        }

        return allocationShop;
    }

    /**
     * 剔除供给店的一些商品，让它满足满场率下限
     */
    private Set<String> getkickSupplyGoods(AllocationShop allocationShop,AllocationGoodsSKC shopSkc) {
        Set<String> shopMatCodeSet = new HashSet<>();
        // 按日均销倒序排序
        List<AllocationStockDO> allocationStockDOList = allocationShop
                .getAllocationStockDOList()
                .stream()
                .sorted(Comparator.comparing(AllocationStockDO::getAvgSaleQty).reversed())
                .collect(Collectors.toList());

        // 差额skc
        int diffCount = shopSkc.getSkcCount() -  shopSkc.getLowStandardSkcCount();

        // 当前门店skc已经小于skc下限 (当前所有商品不参与调拨)
        if (diffCount <= 0) {
            shopMatCodeSet = allocationStockDOList.stream().map(AllocationStockDO::getMatCode).collect(Collectors.toSet());
            return shopMatCodeSet;
        }

        for (AllocationStockDO allocationStockDO : allocationStockDOList) {

            if (diffCount == 0) {
                break;
            }

            if(shopMatCodeSet.contains(allocationStockDO.getMatCode())) {
                continue;
            }

            shopMatCodeSet.add(allocationStockDO.getMatCode());
            diffCount--;

        }

        return shopMatCodeSet;
    }

    /**
     * 检查剩余skc是否满足满场率下限
     * @param allocationShop
     *
     * @param categoryCode
     * @return
     */
    private boolean checkSkcCount(AllocationShop allocationShop,AllocationGoodsSKC shopSkc,String categoryCode) {
        long allocationSkcCount = allocationShop
                .getAllocationStockDOList()
                .stream()
                .filter(allocationStockDO -> categoryCode.equals(allocationStockDO.getCategoryCode()))
                .map(AllocationStockDO::getMatCode)
                .distinct()
                .count();

        int remainSKC = shopSkc.getSkcCount() - (int)(allocationSkcCount);
        return remainSKC > shopSkc.getLowStandardSkcCount() ? true : false;
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
