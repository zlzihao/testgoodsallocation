package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.model.allocation.AllocationDetail;
import cn.nome.saas.allocation.model.allocation.AllocationShop;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationFlowDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationFragementalGoodsMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationStockDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.allocation.AllocationStockService;
import cn.nome.saas.allocation.utils.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 零碎商品打包带走
 *
 * @author Bruce01.fan
 * @date 2019/12/10
 */
@Service
public class ClothingFragmentalRuleEngineService {

    private static Logger logger = LoggerFactory.getLogger(ClothingFragmentalRuleEngineService.class);

    @Autowired
    AllocationStockDOMapper allocationStockDOMapper;

    @Autowired
    AllocationFragementalGoodsMapper allocationFragementalGoodsMapper;

    @Autowired
    AllocationStockService allocationStockService;

    @Autowired
    AllocationFlowDOMapper allocationFlowDOMapper;

    @Value("${clothing.allocation.switch}")
    private int clothingSwitch;

    public void handleFragmentalGoods(Task task) {

        /* =================================

            增加开关控制，开启的时候，不执行

           =================================
         */
        if (clothingSwitch == 1) {
            return;
        }

        String tableName = CommonUtil.getTaskTableName("out_of_stock_goods",task.getTaskId(),task.getRunTime());
        // 零散的商品供给列表(所有供给店非有效skc)
        Set<String> supplyShopIdSet = Stream.of(task.getSupplyShopIds().split(",")).collect(Collectors.toSet());
        List<AllocationFragementalGoodsDO> allocationFragementalGoodsDOList =  allocationStockDOMapper.selectFragmentalGoods(task.getTaskId(),tableName,supplyShopIdSet);

        allocationFragementalGoodsDOList.stream().forEach(allocationFragementalGoodsDO -> {
            allocationFragementalGoodsDO.setTaskId(task.getTaskId());
        });
        // 存到表中
        allocationFragementalGoodsMapper.deleteByParam(task.getTaskId());
        allocationFragementalGoodsMapper.batchInsert(allocationFragementalGoodsDOList);

        Set<String> supplyShopKeySet = allocationFragementalGoodsDOList.stream()
                .map(goods->{
                    return goods.getShopId()+":"+goods.getMatCode()+":"+goods.getSizeId();
                }).collect(Collectors.toSet());

        int limitDays = 70; // 70天上限
        int safeDays = task.getInDays(); // 需求天数
        // 获取调入店铺列表(限只参与了调拨的调入店)
        //Set<String> demandShopIdSet = Stream.of(task.getDemandShopIds().split(",")).collect(Collectors.toSet());
        List<AllocationDemandGoodsDO> demandShopGoodsList = allocationStockDOMapper.selectDemandShopGoods(task.getTaskId(),tableName);

        List<AllocationStockDO> allocationStockDOList = new ArrayList<>();
        List<AllocationFragementalGoodsDO> allocationFragementalGoodsDOS = new ArrayList<>();

        // 循环判断
        safeDays += 7;
        while(safeDays <= limitDays) {

            // 计算需求量
            for (AllocationDemandGoodsDO allocationDemandGoodsDO : demandShopGoodsList) {
                int safetyStockQty = (int)Math.round(allocationDemandGoodsDO.getAvgSaleQty() * safeDays);
                int demandStockQty = safetyStockQty - allocationDemandGoodsDO.getStoreQty() - allocationDemandGoodsDO.getAlloctionStockQty();
                allocationDemandGoodsDO.setDemandStockQty(demandStockQty);
            }

            // 按单个sku需求量排序
            Map<String,List<AllocationDemandGoodsDO>> demandMap = new HashMap<>();

            for (AllocationDemandGoodsDO allocationDemandGoodsDO : demandShopGoodsList) {
                String key = allocationDemandGoodsDO.getSupplyShopId()+":"+allocationDemandGoodsDO.getMatCode()+":"+allocationDemandGoodsDO.getSizeId();
                if (!supplyShopKeySet.contains(key)) {
                    continue;
                }

                if (!demandMap.containsKey(key)) {
                    List<AllocationDemandGoodsDO> newList = new ArrayList<>();
                    demandMap.put(key,newList);
                }
                demandMap.get(key).add(allocationDemandGoodsDO);
            }

            for (String shopSku : demandMap.keySet()) {
                // 按需求量从大到小排序
                List<AllocationDemandGoodsDO> list =   demandMap.get(shopSku)
                        .stream()
                        .sorted(Comparator.comparing(AllocationDemandGoodsDO::getDemandStockQty).reversed())
                        .collect(Collectors.toList());

                String supplyShopId = shopSku.split(":")[0];
                String matCode = shopSku.split(":")[1];
                String sizeId = shopSku.split(":")[2];

                AllocationFragementalGoodsDO allocationFragementalGoodsDO = allocationFragementalGoodsDOList
                        .stream()
                        .filter(fragementGoods->fragementGoods.getShopId().equals(supplyShopId) &&
                                fragementGoods.getMatCode().equals(matCode) &&
                                fragementGoods.getSizeId().equals(sizeId))
                        .findFirst()
                        .orElse(null);

                if (allocationFragementalGoodsDO.getRemainStockQty() == 0) {
                    continue;
                }

                // 循环单个sku下所有的需求量列表
                int totalDemandQty = list.stream().collect(Collectors.summingLong(AllocationDemandGoodsDO::getDemandStockQty)).intValue();
                // 所有门店的需求量都吃不完供给量时，继续增加safeDays， 直到safeDays等于70天为止
                if (totalDemandQty - allocationFragementalGoodsDO.getRemainStockQty() >0 || safeDays<70) {
                    continue;
                }

                // 需求量>=供给量 || safeDays==70天
                for (AllocationDemandGoodsDO allocationDemandGoodsDO : list) {
                    int supplyRemainStockQty = allocationFragementalGoodsDO.getRemainStockQty();
                    if (supplyRemainStockQty == 0) {
                        break;
                    }

                    AllocationStockDO allocationStockDO = this.buildAllocationStock(task,allocationFragementalGoodsDO,allocationDemandGoodsDO,safeDays);

                    // 分配量与剩余量间取一个小值
                    int allocationStockQty =  Math.min(allocationStockDO.getAllocationStockQty(),supplyRemainStockQty);
                    // 更新已分配量
                    allocationFragementalGoodsDO.setAlloctionStockQty(allocationFragementalGoodsDO.getAlloctionStockQty() + allocationStockQty);
                    // 更新剩余量
                    allocationFragementalGoodsDO.setRemainStockQty(supplyRemainStockQty - allocationStockDO.getAllocationStockQty());

                    allocationStockDOList.add(allocationStockDO);
                }

                allocationFragementalGoodsDOS.add(allocationFragementalGoodsDO);
            }

            if (safeDays == 70) {
                break;
            }

            safeDays += 7;
            // 控制上限只能到70天
            safeDays = Math.min(safeDays,70);
        }

        if (CollectionUtils.isEmpty(allocationStockDOList)) {
            return;
        }

        LoggerUtil.warn(logger,"[零散服装更新] msg=stock list:{0}",allocationStockDOList.size());

        // 更新零散明细
        allocationFragementalGoodsMapper.batchUpdate(allocationFragementalGoodsDOS);

        // 插入调拨明细数据
        allocationStockService.batchInsert(allocationStockDOList);

        // 生成调拨流向数据
        Map<String,Object> param = new HashMap<>();
        param.put("taskId",task.getTaskId());
        param.put("matchFlag",1);
        List<AllocationDetail> allocationDetails = allocationFlowDOMapper.getAllocationDetail(param);
        Map<String,List<AllocationStockDO>> allocationStockMap = allocationStockDOList.stream().collect(Collectors.groupingBy(stock->{return stock.getShopId()+":"+stock.getSupplyShopId();},Collectors.toList()));

        List<AllocationFlowDO> allocationFlowDOList = new ArrayList<>();
        for (String key : allocationStockMap.keySet()) {
            AllocationDetail allocationDetail = allocationDetails.stream()
                    .filter(detail->detail.getInShop().equals(key.split(":")[0]) && detail.getOutShop().equals(key.split(":")[1]))
                    .findFirst()
                    .orElse(null);
            if (allocationDetail != null) {
                AllocationFlowDO allocationFlowDO = genAllocationFlow(allocationStockMap.get(key), allocationDetail);
                allocationFlowDOList.add(allocationFlowDO);
            }

        }
        if (CollectionUtils.isNotEmpty(allocationFlowDOList)) {
            allocationFlowDOMapper.batchUpdate(allocationFlowDOList);
        }
    }

    private AllocationStockDO buildAllocationStock(Task task,AllocationFragementalGoodsDO allocationFragementalGoodsDO,AllocationDemandGoodsDO allocationDemandGoodsDO,int inDays) {
        AllocationStockDO allocationStockDO = new AllocationStockDO();

        int allocationStockQty = Math.min(allocationFragementalGoodsDO.getRemainStockQty(),allocationDemandGoodsDO.getDemandStockQty());

        String currentDate = DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY);
        allocationStockDO.setTaskId(task.getTaskId());
        allocationStockDO.setAllocationDate(currentDate);
        allocationStockDO.setShopId(allocationDemandGoodsDO.getShopId());
        allocationStockDO.setShopName(allocationDemandGoodsDO.getShopName());
        allocationStockDO.setShopLevel(allocationDemandGoodsDO.getShopLevel());
        allocationStockDO.setCityName(allocationDemandGoodsDO.getCityName());
        allocationStockDO.setCategoryCode(allocationDemandGoodsDO.getCategoryCode());
        allocationStockDO.setMatCode(allocationDemandGoodsDO.getMatCode());
        allocationStockDO.setMatName(allocationDemandGoodsDO.getMatName());
        allocationStockDO.setSizeId(allocationDemandGoodsDO.getSizeId());
        allocationStockDO.setSizeCode(allocationDemandGoodsDO.getSizeCode());
        allocationStockDO.setSizeName(allocationDemandGoodsDO.getSizeName());
        allocationStockDO.setBatCode(allocationDemandGoodsDO.getBatCode());
        allocationStockDO.setQuotePrice(allocationFragementalGoodsDO.getQuotePrice());
        allocationStockDO.setAvgSaleQty(allocationDemandGoodsDO.getAvgSaleQty());
        allocationStockDO.setSumSaleQty(allocationDemandGoodsDO.getSumSaleQty());
        allocationStockDO.setStockQty(allocationDemandGoodsDO.getStockQty());
        allocationStockDO.setPathStockQty(allocationDemandGoodsDO.getPathStockQty());
        allocationStockDO.setApplyStockQty(allocationDemandGoodsDO.getApplyStockQty());
        allocationStockDO.setSafeStockQty((int)Math.round(inDays * allocationDemandGoodsDO.getAvgSaleQty()));
        allocationStockDO.setIdealStockQty(allocationDemandGoodsDO.getDemandStockQty());
        allocationStockDO.setDemandStockQty(allocationDemandGoodsDO.getDemandStockQty());
        allocationStockDO.setSupplyShopId(allocationDemandGoodsDO.getSupplyShopId());
        allocationStockDO.setSupplyShopName(allocationFragementalGoodsDO.getShopId());
        allocationStockDO.setSupplyShopLevel(allocationFragementalGoodsDO.getShopLevel());
        allocationStockDO.setSupplyCityName(allocationFragementalGoodsDO.getCityName());
        allocationStockDO.setSupplyAvgSaleQty(allocationFragementalGoodsDO.getAvgSaleQty());
        allocationStockDO.setSupplySumSaleQty(allocationFragementalGoodsDO.getSumSaleQty());
        allocationStockDO.setSupplyStockQty(allocationFragementalGoodsDO.getRemainStockQty());
        allocationStockDO.setAllocationStockQty(allocationStockQty);
        allocationStockDO.setMinDisplayQty(allocationDemandGoodsDO.getMinDisplayQty()); // 最小陈列
        allocationStockDO.setDemandForbiddenFlag(allocationDemandGoodsDO.getForbiddenFlag()); // 调入禁配标示
        allocationStockDO.setSupplyForbiddenFlag(allocationFragementalGoodsDO.getForbiddenFlag()); // 调出禁配标示
        allocationStockDO.setDemandDays(inDays);
        allocationStockDO.setSupplyDays(task.getOutDays());

        return allocationStockDO;
    }

    private AllocationFlowDO genAllocationFlow(List<AllocationStockDO> allocationStockDOList,AllocationDetail allocationDetail) {

        AllocationFlowDO allocationFlowDO = new AllocationFlowDO();
        long qty = allocationStockDOList.stream().collect(Collectors.summingLong(AllocationStockDO::getAllocationStockQty));
        double allocationAmount = allocationStockDOList.stream()
                .mapToDouble(stock->{return stock.getAllocationStockQty() * stock.getQuotePrice();})
                .sum();
        double demandAmount = allocationStockDOList.stream()
                .mapToDouble(stock->{return stock.getDemandStockQty() * stock.getQuotePrice();})
                .sum();

        qty += allocationDetail.getSkuCnt();
        allocationAmount += allocationDetail.getAllocationAmount();
        demandAmount += allocationDetail.getDemandAmount();

        allocationFlowDO.setAllocationQty((int)qty);
        allocationFlowDO.setAllocationAmount(allocationAmount);
        allocationFlowDO.setDemandAmount(demandAmount);
        allocationFlowDO.setId(allocationDetail.getId());

        return allocationFlowDO;
    }
}
