package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.model.allocation.AllocationDetail;
import cn.nome.saas.allocation.model.allocation.AllocationDetailList;
import cn.nome.saas.allocation.model.allocation.AllocationDetailRecord;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationFlowDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationStockDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ClothingTaskDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.utils.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClothingTaskService
 *
 * @author Bruce01.fan
 * @date 2019/12/16
 */
@Service
public class ClothingTaskService {

    @Autowired
    ShopService shopService;

    @Autowired
    ClothingTaskDOMapper clothingTaskDOMapper;

    @Autowired
    AllocationStockDOMapper allocationStockDOMapper;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    AllocationFlowDOMapper allocationFlowDOMapper;

    public Result getAllocationDetailList(int taskId, String city, Integer price, String keyWord, Page page) {

        TaskDO taskDO = clothingTaskDOMapper.getTask(taskId);
        if (taskDO == null) {
            return ResultUtil.handleFailtureReturn("10001","当前taskId无效，请更换后再查询");
        }

        Map<String,Object> param = new HashMap<>();

        param.put("taskId",taskId);
        param.put("price",price);
        if (StringUtils.isNotBlank(keyWord)) {
            param.put("keyword", keyWord);
        }
        //param.put("page",page);

        if (StringUtils.isNotBlank(city)) {
            List<DwsDimShopDO> dwsDimShopDOList =  shopListCache.getShopList();

            List<String> shopList = dwsDimShopDOList.stream().filter(shop->shop.getCityCode().equals(city)).map(DwsDimShopDO::getShopId).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(shopList)) {
                return ResultUtil.handleSuccessReturn();
            }

            param.put("shopIdList",shopList);
        }

        List<AllocationDetail> allocationDetailList = new ArrayList<>();
        AllocationDetail summaryDetail = allocationFlowDOMapper.getAllocationSummary(param);

        if (summaryDetail == null) {
            return ResultUtil.handleSuccessReturn();
        }

        summaryDetail.setInShop(summaryDetail.getInShop()+"家");
        summaryDetail.setOutShop(summaryDetail.getOutShop()+"家");

        allocationDetailList.add(summaryDetail);

        AllocationDetailList allocationDetailResult = new AllocationDetailList();

        param.put("matchFlag",1);
        List<AllocationDetail> list = allocationFlowDOMapper.getAllocationDetail(param);

        param.put("matchFlag",0);
        List<AllocationDetail> failList = allocationFlowDOMapper.getAllocationDetail(param);

        if (CollectionUtils.isNotEmpty(list)) {
            allocationDetailList.addAll(list);
            allocationDetailResult.setList(allocationDetailList);
        }
        if (CollectionUtils.isNotEmpty(failList)) {
            failList.forEach(allocationDetail -> {
                if (allocationDetail.getFailFlag() == 1) {
                    allocationDetail.setFailMsg("金额低于最小起调金额");
                }
                if (allocationDetail.getFailFlag() == 2) {
                    allocationDetail.setFailMsg("费率低于最高费率");
                }
            });
            allocationDetailResult.setFailMsg("调拨失败店铺：起调金额 < "+taskDO.getMinAllocationPrice() +" 或 费率 > "+ (taskDO.getMaxFeeRatio()*100)+"%");
            allocationDetailResult.setFailList(failList);
        }

        //allocationDetailResult.setTotalPage(page.getTotalPage());
        //allocationDetailResult.setTotal(page.getTotalRecord());

        return  ResultUtil.handleSuccessReturn(allocationDetailResult);
    }

    public void downloadAllocationDetailList(int taskId, HttpServletRequest request, HttpServletResponse response) {

        TaskDO taskDO = clothingTaskDOMapper.getTask(taskId);


        List<AllocationDetailRecord> list =  allocationStockDOMapper.selectClothingAllocationDetailList(taskId, CommonUtil.getTaskTableName("out_of_stock_goods",taskId,taskDO.getRunTime()));

        list.forEach(allocationDetailRecord -> {

            ShopInfoData demandShopInfo = shopService.getShopInfoById(allocationDetailRecord.getDemandShopId());
            ShopInfoData supplyShopInfo = shopService.getShopInfoById(allocationDetailRecord.getSupplyShopId());

            if (demandShopInfo != null) {
                allocationDetailRecord.setDemandShopCode(demandShopInfo.getShopCode());
                allocationDetailRecord.setDemandShopLevel(demandShopInfo.getShopLevel());
            }
            if (supplyShopInfo != null) {
                allocationDetailRecord.setSupplyShopCode(supplyShopInfo.getShopCode());
                allocationDetailRecord.setSupplyShopLevel(supplyShopInfo.getShopLevel());
            }

            // 可售天数
            if (allocationDetailRecord.getDemandAvgSalesQty() > 0) {
                BigDecimal demandSalesDay = new BigDecimal(allocationDetailRecord.getAfterStockQty() / allocationDetailRecord.getDemandAvgSalesQty());
                allocationDetailRecord.setDemandRemainSalesDay(demandSalesDay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            if (allocationDetailRecord.getSupplyAvgSalesQty() > 0) {
                BigDecimal supplySalesDay = new BigDecimal(allocationDetailRecord.getSupplyRemainStockQty() / allocationDetailRecord.getSupplyAvgSalesQty());
                allocationDetailRecord.setSupplyRemainSalesDays(supplySalesDay.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
            }
        });

        try {
            cn.nome.saas.allocation.utils.ExcelUtil.exportAllocationDetailData(taskDO, list, request, response);
        } catch (Exception e) {
        }

    }
}
