package cn.nome.saas.allocation.rest.admin;


import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ReserveStockSettings;
import cn.nome.saas.allocation.model.form.ReserveStockSettingsForm;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.repository.dao.allocation.IssueTaskDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.repository.entity.allocation.ReserveStockSettingsDO;
import cn.nome.saas.allocation.service.allocation.*;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.old.ExcelUtil2;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO.TASK_TYPE_RECALC_RESERVE;

@RestController
@RequestMapping("/allocation/new")
public class NewIssueRestController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NewIssueBasicService newIssueBasicService;
    @Autowired
    private NewIssueSkuCalcService newIssueSkuCalcService;
    @Autowired
    private NewIssueMatchService newIssueMatchService;
    @Autowired
    private NewIssueService newIssueService;
    @Autowired
    private NewIssueRecalcService newIssueRecalcService;
    @Autowired
    private NewIssueDOMapper newIssueDOMapper;
    @Autowired
    private IssueTaskDOMapper issueTaskDOMapper;
    @Autowired
    private NewIssueSandBoxService newIssueSandBoxService;
    @Autowired
    private ReserveStockSettingsService reserveStockSettingsService;

    @Autowired
    AsyncTask asyncTask;

    @RequestMapping(value = "/reRunIssueTask", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask(@RequestParam(value = "remark", required = false, defaultValue = "") String remark,
                                 @RequestParam(value = "taskId", required = false, defaultValue = "-1") int taskId,
                                 @RequestParam(value = "ready", required = false, defaultValue = "1") int ready,
                                 @RequestParam(value = "taskType", required = false, defaultValue = "0") int taskType){

        String operatorId = AuthUtil.getSessionUserId();
        if (!"90000955".equals(operatorId) && !"90001053".equals(operatorId) && !"90001381".equals(operatorId)) {
            return ResultUtil.handleBizFailtureReturn("12000","您暂无权限执行该操作");
        }

        try {
            try {

                IssueTaskVo taskVo = newIssueService.getLastTask();
                if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
                    //存在重算任务在跑
                    return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
                } else {
                    //是否有单店重算
                    int recalcCount = newIssueRecalcService.countRecalcTask(taskVo.getTaskId());
                    if (recalcCount > 0) {
                        return ResultUtil.handleSysFailtureReturn("存在单店重算任务(" + recalcCount + "个)，不可操作全局重算");
                    }
                }
                IssueTaskDO task;
                if (taskId > 0) {
                    task = newIssueService.getTaskById(taskId);
                } else {
                    task = newIssueService.createTask(2, taskType, remark, ready);
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

    @RequestMapping(value = "/reRunIssueTask0", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask0(@RequestParam(value = "remark", required = false, defaultValue = "") String remark,
                                  @RequestParam(value = "ready", required = false, defaultValue = "1") int ready,
                                  @RequestParam(value = "taskId", required = false, defaultValue = "-1") int taskId) throws Exception {
        try {
            IssueTaskVo taskVo = newIssueService.getLastTask();
            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
                //存在重算任务在跑
                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
            } else {
                //是否有单店重算
                int recalcCount = newIssueRecalcService.countRecalcTask(taskVo.getTaskId());
                if (recalcCount > 0) {
                    return ResultUtil.handleSysFailtureReturn("存在单店重算任务(" + recalcCount + "个)，不可操作全局重算");
                }
            }
            IssueTaskDO task;
            if (taskId > 0) {
                task = newIssueService.getTaskById(taskId);
            } else {
                task = newIssueService.createTask(2, TASK_TYPE_RECALC_RESERVE, remark, ready);
            }

//            IssueTaskVo taskVo = newIssueService.getLastTask();
//            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
//                //存在重算任务在跑
//                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
//            }
//            IssueTaskDO task = new IssueTaskDO();
//            BeanUtils.copyProperties(taskVo, task);
            newIssueBasicService.issueInStock(task, task.getRemark() == null || "RERUN".equals(task.getRemark()) || "RUN".equals(task.getRemark()) ? null : Arrays.stream(task.getRemark().split(",")).collect(Collectors.toSet()), false);
            newIssueBasicService.issueOutStock(task);
//            newIssueSkuCalcService.calcSKURequirement(1, new HashSet<>(Arrays.asList("NM000193", "NM000076", "NM000068")));
//            newIssueMatchService.issueDetail();
//            newIssueMatchService.processIssueGoodsData(1, new ArrayList<>(Arrays.asList("NM000193", "NM000076", "NM000068")));
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/reRunIssueTask1", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask1() throws Exception {
        try {
//            IssueTaskVo taskVo = issueRestService.getLastTask();
//            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
//                //存在重算任务在跑
//                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
//            }else {
//                //是否有单店重算
//                int recalcCount = recalcTaskService.countRecalcTask(taskVo.getTaskId());
//                if (recalcCount > 0){
//                    return ResultUtil.handleSysFailtureReturn("存在单店重算任务(" + recalcCount + "个)，不可操作全局重算");
//                }
//            }
//            IssueTask task = issueRestService.createTask(0);
//            asyncTask.runIssueTask(task);
            IssueTaskVo taskVo = newIssueDOMapper.getLastReRunTask();
//            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
//                //存在重算任务在跑
//                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
//            }
            IssueTaskDO task = new IssueTaskDO();
            task.setId(taskVo.getTaskId());
            task.setRemark(taskVo.getRemark());
            task.setRunTime(taskVo.getRunTime());
            newIssueSkuCalcService.calcSKURequirement(task.getId(), task.getRemark() == null || "RERUN".equals(task.getRemark()) || "RUN".equals(task.getRemark()) ? null : Arrays.stream(task.getRemark().split(",")).collect(Collectors.toSet()), task.getRunTime(), false, null);
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/reRunIssueTask2", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask2() throws Exception {
        try {
//            IssueTaskVo taskVo = issueRestService.getLastTask();
//            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
//                //存在重算任务在跑
//                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
//            }else {
//                //是否有单店重算
//                int recalcCount = recalcTaskService.countRecalcTask(taskVo.getTaskId());
//                if (recalcCount > 0){
//                    return ResultUtil.handleSysFailtureReturn("存在单店重算任务(" + recalcCount + "个)，不可操作全局重算");
//                }
//            }
//            IssueTask task = issueRestService.createTask(0);
//            asyncTask.runIssueTask(task);
            IssueTaskVo taskVo = newIssueDOMapper.getLastReRunTask();
//            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
//                //存在重算任务在跑
//                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
//            }
            IssueTaskDO task = new IssueTaskDO();
            task.setId(taskVo.getTaskId());
            task.setRemark(taskVo.getRemark());
            task.setRunTime(taskVo.getRunTime());
            newIssueMatchService.issueDetail(task, task.getRemark() == null || "RERUN".equals(task.getRemark()) || "RUN".equals(task.getRemark()) ? null : Arrays.stream(task.getRemark().split(",")).collect(Collectors.toSet()));
            issueTaskDOMapper.updateTaskStatus(task.getId());
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(null);
    }

    /**
     * 获取配补汇总
     *
     * @param orderListReq
     * @return
     */
    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
    @ResponseBody
    public Result getOrderList(@RequestBody OrderListReq orderListReq) {
        OrderListWrap rst = null;
        try {
            rst = newIssueService.getOrderList(orderListReq);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "getOrderList err:{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(rst);
    }

    /**
     * 获取店铺配补详情
     *
     * @param orderDetailReq
     * @return
     */
    @RequestMapping(value = "/orderDetail", method = RequestMethod.POST)
    @ResponseBody
    public Result orderDetail(@RequestBody OrderDetailReq orderDetailReq) {
        OrderDetailWrap rst = null;
        try {
            rst = newIssueService.getOrderDetail(orderDetailReq);
        } catch (Exception e) {
            LoggerUtil.error(logger, "orderDetail err:{0}", e);
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(rst);
    }

    /**
     * 下载配补明细
     *
     * @param taskId
     * @param shopId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/downloadOrderDetail")
    public String downloadOrderDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, HttpServletRequest request, HttpServletResponse response) {

        try {
            OrderDetailReq detailReq = new OrderDetailReq();
            detailReq.setShopId(shopId);
            detailReq.setTaskId(taskId);
            OrderDetailWrap rst = newIssueService.getOrderDetail(detailReq);
            List<OrderDetailVo> rows = rst.getOrderDetailVo();

            String fileName = new StringBuffer("配补明细-").append(rst.getShopName()).append("-").append(DateUtil.format(new Date(), Constant.DATE_PATTERN_3)).append(".xls").toString();
            ExcelUtil2.exportOrderDetail(fileName, rows, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(logger, "downloadOrderDetail err:{0}", e.getMessage());
        }
        return "886";
    }

    /**
     * 通过跳转链接的方式批量下载配补明细
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/downloadOrderDetailMulti/link")
    @ResponseBody
    public String downloadOrderDetailMultiByLink(
        @RequestParam int taskId,
        @RequestParam String shopIds,
        @RequestParam(value = "categoryName", required = false) String categoryName,
        HttpServletRequest request,
        HttpServletResponse response) {

        LoggerUtil.info(logger, "downloadOrderDetailMultiByLink, taskId: {0}, shopIds: {1}, categoryName: {2}",
            taskId, shopIds, categoryName);

        try {
            List<String> shopIdList = JSONObject.parseObject(shopIds, new TypeReference<List<String>>() {}.getType());
            OrderDetailReq orderDetailReq = new OrderDetailReq();
            orderDetailReq.setTaskId(taskId);
            orderDetailReq.setShopIds(shopIdList);
            orderDetailReq.setCategoryName(categoryName);

            List<OrderDetailVo> rows = newIssueService.getOrderDetailMulti(orderDetailReq);
            LoggerUtil.info(logger, "downloadOrderDetailMulti, rows count: {0}", rows != null ? rows.size() : 0);

            String fileName = new StringBuffer("批量导出").append(DateUtil.format(new Date(), Constant.DATE_PATTERN_5)).append(".csv").toString();
            // ExcelUtil2.exportOrderDetail(fileName, rows, request, response);
            ExcelUtil2.exportOrderDetailCSV(fileName, rows, request, response);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "downloadOrderDetail err:{0}", e.getMessage());
        }
        return "886";
    }

    /**
     * 下载配补明细
     * @param orderDetailReq
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/downloadOrderDetailMulti", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String downloadOrderDetailMulti(@RequestBody OrderDetailReq orderDetailReq, HttpServletRequest request, HttpServletResponse response) {

        LoggerUtil.info(logger, "downloadOrderDetailMulti, orderDetailReq: {0}", JSONObject.toJSONString(orderDetailReq));

        try {
            List<OrderDetailVo> rows = newIssueService.getOrderDetailMulti(orderDetailReq);
            LoggerUtil.info(logger, "downloadOrderDetailMulti, rows count: {0}", rows != null ? rows.size() : 0);

            String fileName = new StringBuffer("批量导出").append(DateUtil.format(new Date(), Constant.DATE_PATTERN_5)).append(".csv").toString();
            // ExcelUtil2.exportOrderDetail(fileName, rows, request, response);
            ExcelUtil2.exportOrderDetailCSV(fileName, rows, request, response);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "downloadOrderDetail err:{0}", e.getMessage());
        }
        return "886";
    }

    @RequestMapping(value = "/getMatDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result getMatDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, @RequestParam(value = "categoryName", required = false) String categoryName) {
        return ResultUtil.handleSuccessReturn(newIssueService.getMatDetail(taskId, shopId, categoryName));
    }

    @RequestMapping(value = "/getMatMidCategoryDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result getMatMidCategoryDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, @RequestParam(value = "categoryName") String categoryName) {
        return ResultUtil.handleSuccessReturn(newIssueService.getMatMidCategoryDetail(taskId, shopId, categoryName));
    }

    @RequestMapping(value = "/getLastTask", method = RequestMethod.GET)
    @ResponseBody
    public Result getLastTask() {
        return ResultUtil.handleSuccessReturn(newIssueService.getLastTask());
    }

    @RequestMapping(value = "/categoryList", method = RequestMethod.GET)
    @ResponseBody
    public Result categoryList(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(newIssueService.categoryList(taskId, shopId));
    }

    @RequestMapping(value = "/midCategoryList", method = RequestMethod.GET)
    @ResponseBody
    public Result midCategoryList(@RequestParam(value = "categoryName", required = false) String categoryName, @RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(newIssueService.midCategoryList(taskId, shopId, categoryName));
    }

    @RequestMapping(value = "/smallCategoryList", method = RequestMethod.GET)
    @ResponseBody
    public Result smallCategoryList(@RequestParam(value = "midCategoryName", required = false) String midCategoryName, @RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(newIssueService.smallCategoryList(taskId, shopId, midCategoryName));
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

    @RequestMapping(value = "/saveReserveStockSettings", method = RequestMethod.POST)
    public Result<?> saveReserveStockSettings(@Valid @RequestBody ReserveStockSettingsForm form) {
        ReserveStockSettingsDO reserveStockSettingsDO = new ReserveStockSettingsDO();
        reserveStockSettingsDO.setId(form.getId());
        reserveStockSettingsDO.setIsEnable(form.getIsEnable());
        reserveStockSettingsDO.setReserveDate(form.getReserveDate());
        reserveStockSettingsDO.setUseSalePredict(form.getUseSalePredict());
        reserveStockSettingsService.update(reserveStockSettingsDO);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/getReserveStockSettings", method = RequestMethod.GET)
    public Result<ReserveStockSettings> getReserveStockSettings() {
        ReserveStockSettings record = reserveStockSettingsService.getLatestWithInit();
        return ResultUtil.handleSuccessReturn(record);
    }
}
