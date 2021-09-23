package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.*;
import cn.nome.saas.allocation.mapper.TaskMapper;
import cn.nome.saas.allocation.model.allocation.*;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationFlowDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationStockDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.TaskDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.service.rule.ShopExpressService;
import cn.nome.saas.allocation.utils.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TaskService
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
@Service
public class TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    ShopService shopService;


    @Autowired
    TaskDOMapper taskMapper;

    @Autowired
    AllocationStockService allocationStockService;

    @Autowired
    AllocationStockDOMapper allocationStockDOMapper;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    AllocationFlowDOMapper allocationFlowDOMapper;

    public boolean checkTaskName(String taskName) {

        return taskMapper.checkTaskName(taskName) > 0 ? true : false;
    }

    public Integer createTask(Task task) {

        taskMapper.createTask(TaskMapper.mapper(task));

        return 1;
    }

    public Integer  updateTask(Task task) {

        taskMapper.updateTask(TaskMapper.mapper(task));

        return 1;
    }

    public List<AllocationTask> getTaskList(Page page,Integer allocationType,String keyword) {

        Map<String,Object> param = new HashMap<>();

        param.put("allocationType",allocationType);
        param.put("keyword",keyword);

        param.put("page",page);

        if (page != null) {
            int count = taskMapper.selectTaskNum(param);
            page.setTotalRecord(count);
        }

        List<TaskDO> taskDOList = taskMapper.getTaskList(param);

        return TaskMapper.mapperAllocationTask(taskDOList);

    }

    public List<Paramater> getCloseTaskList() {
        return taskMapper.getCloseTaskList();
    }

    List<Task> getNeedRunTaskList(int allocationType) {
        List<TaskDO> taskDOList = taskMapper.getNeedRunTaskList(allocationType);
        return TaskMapper.mapper(taskDOList);
    }

    Integer updateTaskToRunning(int taskId) {
        return taskMapper.updateTaskToRunning(taskId);
    }

    Integer updateTaskToFinish(int taskId) {
        return taskMapper.updateTaskToFinish(taskId);
    }

    Integer updateTaskToFail(int taskId,String msg) {
        return taskMapper.updateTaskToFail(taskId,msg);
    }

    public TaskDO getTask(int taskId) {
        return taskMapper.getTask(taskId);
    }

    public void cancelTask(int taskId) {
        taskMapper.cancelTask(taskId);
    }

    public Result getAllocationDetailList(int taskId, String city, Integer price, String keyWord,Page page) {

        TaskDO taskDO = taskMapper.getTask(taskId);
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


    public List<TaskProgressDO> getTaskProgress(int taskId) {
        return taskMapper.getTaskProgress(taskId);
    }

    public Integer updateTaskProcess(int taskId,int process) {
        return taskMapper.updateTaskProcess(taskId,process);
    }

    public void downloadAllocationDetailList(int taskId, HttpServletRequest request, HttpServletResponse response) {

        TaskDO taskDO = taskMapper.getTask(taskId);


        List<AllocationDetailRecord> list =  allocationStockDOMapper.selectAllocationDetailList(taskId, CommonUtil.getTaskTableName("out_of_stock_goods",taskId,taskDO.getRunTime()));

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
            LoggerUtil.error(e,logger,"[EXPORT_ALLOCATION_DETAIL]");
        }

    }

    List<Integer> selectAllRejectTask( int taskId) {
        return taskMapper.selectAllRejectTask(taskId);
    }


}
