package cn.nome.saas.allocation.rest.pub;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.model.allocation.AllocationTaskReq;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.IssueTaskVo;
import cn.nome.saas.allocation.repository.dao.allocation.IssueSandBoxTaskMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import cn.nome.saas.allocation.rest.admin.ForbiddenRuleAdminController;
import cn.nome.saas.allocation.service.allocation.AllocationService;
import cn.nome.saas.allocation.service.allocation.IssueService;
import cn.nome.saas.allocation.service.allocation.IssueTaskService;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.service.allocation.*;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO.TASK_TYPE_RECALC_RESERVE;

/**
 * AllocationController
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
@RestController
@RequestMapping("/public/allocation")
public class AllocationController {

    private static Logger LOGGER = LoggerFactory.getLogger(AllocationController.class);

    @Autowired
    AllocationService allocationService;

    @Autowired
    IssueTaskService issueTaskService;

    @Autowired
    QdIssueTaskService qdIssueTaskService;

    @Autowired
    IssueService issueService;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    IssueRestService issueRestService;

    @Autowired
    QdIssueService qdIssueService;

    @Autowired
    private NewIssueService newIssueService;
    @Autowired
    private IssueSandBoxTaskMapper issueSandBoxTaskMapper;

    @Autowired
    AllocationClothingService allocationClothingService;

    @Autowired
    private IssueDayService issueDayService;

    ExecutorService executor = Executors.newFixedThreadPool(1);

    @RequestMapping(value = "/runTask", method = RequestMethod.GET)
    @ResponseBody
    public Result runTask(@RequestParam(value = "taskId",required = false,defaultValue = "-1") int taskId) throws Exception {

        allocationService.runAllocationTask(taskId);

        return ResultUtil.handleSuccessReturn();
    }

    /**
     * 服装调拨任务
     * @param taskId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/runClothingTask", method = RequestMethod.GET)
    @ResponseBody
    public Result runClothingTask(@RequestParam(value = "taskId",required = false,defaultValue = "-1") int taskId) throws Exception {

        allocationClothingService.runClothingAllocationTask(taskId);

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/runQdIssueTask")
    @ResponseBody
    public Result runTask() {
        return qdIssueTaskService.runTask();

    }

    @RequestMapping(value = "/runQdIssueTaskById", method = RequestMethod.GET)
    @ResponseBody
    public Result runQdIssueTask(@RequestParam(value = "taskId",required = false,defaultValue = "-1") int taskId) throws Exception {

        Integer count = qdIssueTaskService.getRunningTask();
        if (count != null && count > 0) {
            return ResultUtil.handleSysFailtureReturn("有任务正在运行，请稍后再试");
        }

        qdIssueTaskService.runTask(taskId);

        executor.execute(()->{
            qdIssueService.qdIssueTask(taskId);
            this.qdIssueTaskService.finishTask(taskId);
        });

        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/calc", method = RequestMethod.GET)
    @ResponseBody
    public Result calc(@RequestParam("taskId") int taskId,
                       @RequestParam(value = "demandShops",required = false) String demandShops,
                       @RequestParam(value = "supplyShops",required = false) String supplyShops,
                       @RequestParam("type") int type) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setTaskType(type);
        task.setDemandShopIds(demandShops);
        task.setSupplyShopIds(supplyShops);

        allocationService.calcRequirement(task);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/allocation", method = RequestMethod.GET)
    @ResponseBody
    public Result match(@RequestParam(value = "demandShops",required = false) String demandShops,
                        @RequestParam(value = "supplyShops",required = false) String supplyShops,
                        @RequestParam("taskId") int taskId,
                        @RequestParam("type") int type) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setTaskType(type);

        task.setDemandShopIds(demandShops);
        task.setSupplyShopIds(supplyShops);

        allocationService.allocation(task);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/runIssueTask", method = RequestMethod.GET)
    @ResponseBody
    public Result runIssueTask(@RequestParam(value = "forTest",required = false,defaultValue = "0") int forTest) throws Exception {
        IssueTask task = issueRestService.createTask(0);
        task.setForTest(forTest);
        asyncTask.runIssueTask(task);
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/testSaveExportOrderDetails", method = RequestMethod.GET)
    @ResponseBody
    public Result testSaveExportOrderDetails(@RequestParam("taskId") int taskId) throws Exception {
        asyncTask.saveExportOrderDetails(taskId);
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/new/runIssueTask", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask(@RequestParam(value = "remark", required = false, defaultValue = "") String remark,
                                 @RequestParam(value = "taskId", required = false, defaultValue = "-1") int taskId,
                                 @RequestParam(value = "ready", required = false, defaultValue = "1") int ready) throws Exception {
        try {
            try {

                IssueTaskVo taskVo = newIssueService.getLastTask();
                if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
                    //存在重算任务在跑
                    return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
                }

                IssueTaskDO task ;
                if (taskId > 0) {
                    task = newIssueService.getTaskById(taskId);
                } else {
                    task  = newIssueService.createTask(0, TASK_TYPE_RECALC_RESERVE, remark, ready);
                }

                asyncTask.runNewIssueTask(task);
            } catch (Exception e) {
                return ResultUtil.handleSysFailtureReturn(e.getMessage());
            }
            return ResultUtil.handleSuccessReturn(null);

        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }


    @RequestMapping(value = "/new/recalc/singleShopRecalcTask", method = RequestMethod.GET)
    @ResponseBody
    public Result singleShopRecalcTask() throws Exception {
        newIssueService.schedulerReCalcTask();
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/processGoodsArea", method = RequestMethod.GET)
    @ResponseBody
    public Result processGoodsArea() throws Exception {
        asyncTask.processGoodsArea();
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/bakIssueTab", method = RequestMethod.GET)
    @ResponseBody
    public Result bakIssueTab() {
        issueRestService.bakIssueTab();
        return ResultUtil.handleSuccessReturn(1);
    }

    @RequestMapping(value = "/singleShopRecalc", method = RequestMethod.GET)
    @ResponseBody
    public Result schedulerRecalcTask() throws Exception {
        asyncTask.schedulerRecalcTask();
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/syncIssueTime", method = RequestMethod.GET)
    @ResponseBody
    public Result syncIssueTime() {
        return ResultUtil.handleSuccessReturn(issueRestService.syncIssueTime());
    }

    @RequestMapping(value = "/updateIssueDays", method = RequestMethod.GET)
    @ResponseBody
    public Result updateIssueDays() {
        asyncTask.updateIssueDays();
        return ResultUtil.handleSuccessReturn(null);
    }


    @RequestMapping(value = "/sandBoxCalcTest", method = RequestMethod.GET)
    @ResponseBody
    public Result sandBoxCalcTest(@RequestParam(value = "startDateStr", required = false) String startDateStr, @RequestParam("endDateStr") String endDateStr, @RequestParam(value = "remark", required = false) String remark) {
        try {
            asyncTask.runIssueSandBoxTask(new IssueSandBoxTask(9999, new SimpleDateFormat("yyyyMMdd").parse(startDateStr), new SimpleDateFormat("yyyyMMdd").parse(endDateStr), remark));
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(1);
    }

    @RequestMapping(value = "/sandBoxCalcTask", method = RequestMethod.GET)
    @ResponseBody
    public Result sandBoxCalcTask() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("status", Arrays.asList(IssueSandBoxTask.RUNNING_STATUS));
            List<IssueSandBoxTask> taskList = issueSandBoxTaskMapper.selectByParam(param);
            if (taskList.size() > 0) {
                LOGGER.warn("sandBoxCalcTask has calcing task");
                return ResultUtil.handleSuccessReturn(1);
            }

            param.put("status", Arrays.asList(IssueSandBoxTask.READY_STATUS));
            taskList = issueSandBoxTaskMapper.selectByParam(param);
            if (taskList == null || taskList.size() <= 0) {
                LOGGER.warn("sandBoxCalcTask has not calc task");
                return ResultUtil.handleSuccessReturn(1);
            }

            IssueSandBoxTask task = taskList.get(0);

            asyncTask.runIssueSandBoxTask(task);
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(1);
    }

    @RequestMapping(value = "/calcIssueDate", method = RequestMethod.GET)
    public Result processCategorySkcData(@RequestParam("startDate") String startDate,
                                         @RequestParam("endDate") String endDate) {

        issueDayService.calcShopIssueDay(startDate,endDate);
        return ResultUtil.handleSuccessReturn();
    }

}
