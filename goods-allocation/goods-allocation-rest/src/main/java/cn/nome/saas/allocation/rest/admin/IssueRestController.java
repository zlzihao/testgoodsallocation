package cn.nome.saas.allocation.rest.admin;


import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.service.allocation.RecalcTaskService;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import cn.nome.saas.allocation.utils.old.ExcelUtil2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/allocation")
public class IssueRestController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    ShopService shopService;
//
//    @Autowired
//    DictionaryService dictionaryService;
//
//    @RequestMapping(value = "/getRegioneBusNameList", method = RequestMethod.GET)
//    @ResponseBody
//    public Result getRegioneBusNameList() {
//        return ResultUtil.handleSuccessReturn(shopService.getRegioneBusNameList());
//    }
//
//    @RequestMapping(value = "/getSubRegioneBusNameList", method = RequestMethod.GET)
//    @ResponseBody
//    public Result getSubRegioneBusNameList(String regioneBusName) {
//        return ResultUtil.handleSuccessReturn(shopService.getSubRegioneBusNameList(regioneBusName));
//    }
//
//    @RequestMapping(value = "/getCityList", method = RequestMethod.GET)
//    @ResponseBody
//    public Result getCityList(String subRegioneBusName) {
//        return ResultUtil.handleSuccessReturn(shopService.getCityList(subRegioneBusName));
//    }
//
//    @RequestMapping(value = "/getDictionaryList", method = RequestMethod.GET)
//    @ResponseBody
//    public Result getDictionaryList(String subRegioneBusName) {
//        return ResultUtil.handleSuccessReturn(dictionaryService.getDictionaryList());
//    }

    @Autowired
    IssueRestService issueRestService;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    IssueService issueService;

    @Autowired
    RecalcTaskService recalcTaskService;

    @RequestMapping(value = "/getRegioneBusNameList", method = RequestMethod.GET)
    @ResponseBody
    public Result getRegioneBusNameList() {
        return ResultUtil.handleSuccessReturn(issueRestService.getRegioneBusNameList());
    }

    @RequestMapping(value = "/getSubRegioneBusNameList", method = RequestMethod.GET)
    @ResponseBody
    public Result getSubRegioneBusNameList(String regioneBusName) {
        return ResultUtil.handleSuccessReturn(issueRestService.getSubRegioneBusNameList(regioneBusName));
    }

    @RequestMapping(value = "/getCityList", method = RequestMethod.GET)
    @ResponseBody
    public Result getCityList(String subRegioneBusName) {
        return ResultUtil.handleSuccessReturn(issueRestService.getCityList(subRegioneBusName));
    }

    @RequestMapping(value = "/getDictionaryList", method = RequestMethod.GET)
    @ResponseBody
    public Result getDictionaryList(String subRegioneBusName) {
        return ResultUtil.handleSuccessReturn(issueRestService.getDictionaryList());
    }

    // -------------------------------20190521 -------------------

    @RequestMapping(value = "/getLastTask", method = RequestMethod.GET)
    @ResponseBody
    public Result getLastTask() {
        return ResultUtil.handleSuccessReturn(issueRestService.getLastTask());
    }

    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
    @ResponseBody
    public Result getOrderList(@RequestBody OrderListReq orderListReq) {
        OrderListWrap rst = null;
        try {
            rst = issueRestService.getOrderList(orderListReq);
        } catch (Exception e) {
            logger.error("getOrderList err:{}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(rst);
    }

    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result getOrderDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId,
                                 @RequestParam(value = "curPage", required = false) Integer curPage, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        OrderDetailReq detailReq = new OrderDetailReq();
        detailReq.setCurPage(curPage);
        detailReq.setPageSize(pageSize);
        detailReq.setShopId(shopId);
        detailReq.setTaskId(taskId);
        return ResultUtil.handleSuccessReturn(issueRestService.getOrderDetail(detailReq));
    }

    @RequestMapping(value = "/orderDetail", method = RequestMethod.POST)
    @ResponseBody
    public Result orderDetail(@RequestBody OrderDetailReq orderDetailReq) {
        return ResultUtil.handleSuccessReturn(issueRestService.getOrderDetail(orderDetailReq));
    }

    @GetMapping(value = "/downloadOrderList")
    public String downloadOrderList(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<OrderListVo> rows = issueRestService.getOrderByIds(taskId, shopId);
        ExcelUtil2.exportOrderList(rows, request, response);
        return "886";
    }

    @GetMapping(value = "/downloadOrderDetail")
    public String downloadOrderDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, HttpServletRequest request, HttpServletResponse response) throws Exception {

        OrderDetailReq detailReq = new OrderDetailReq();
        detailReq.setShopId(shopId);
        detailReq.setTaskId(taskId);
        OrderDetailWrap rst = issueRestService.getOrderDetail(detailReq);
        List<OrderDetailVo> rows = rst.getOrderDetailVo();

        String fileName = new StringBuffer("配补明细-").append(rst.getShopName()).append("-").append(DateUtil.format(new Date(), Constant.DATE_PATTERN_3)).append(".xls").toString();
        ExcelUtil2.exportOrderDetail(fileName, rows, request, response);
        return "886";
    }

    @RequestMapping(value = "/getDictByType", method = RequestMethod.GET)
    @ResponseBody
    public Result getDictByType(@RequestParam("type") String type) {
        return ResultUtil.handleSuccessReturn(issueRestService.getDictByType(type));
    }

    /**
     * 配发时间列表
     *
     * @return
     */
    @RequestMapping(value = "/issueTimeList", method = RequestMethod.GET)
    @ResponseBody
    public Result issueTimeList() {
        return ResultUtil.handleSuccessReturn(issueRestService.issueTimeList());
    }

    @RequestMapping(value = "/getMatDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result getMatDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, @RequestParam(value = "categoryName", required = false) String categoryName) {
        return ResultUtil.handleSuccessReturn(issueRestService.getMatDetail(taskId, shopId, categoryName));
    }

    @RequestMapping(value = "/getMatMidCategoryDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result getMatMidCategoryDetail(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId, @RequestParam(value = "categoryName") String categoryName) {
        return ResultUtil.handleSuccessReturn(issueRestService.getMatMidCategoryDetail(taskId, shopId, categoryName));
    }

    @RequestMapping(value = "/modifySkuCount", method = RequestMethod.POST)
    @ResponseBody
    public Result modifySkuCount(@RequestBody OrderSkuModifyReq req) {
        try {
            return ResultUtil.handleSuccessReturn(issueRestService.modifyOrderSku(req));
        } catch (Exception e) {
            logger.error("modifySkuCount err:{},taskId:{},shopId:{},matCode:{}", e.getMessage(), req.getTaskId(), req.getShopId(), req.getMatCode());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/getUserNames", method = RequestMethod.GET)
    @ResponseBody
    public Result getUserNames() {
        return ResultUtil.handleSuccessReturn(issueRestService.getUserNames());
    }

    @RequestMapping(value = "/categoryList", method = RequestMethod.GET)
    @ResponseBody
    public Result categoryList(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(issueRestService.categoryList(taskId,shopId));
    }

    @RequestMapping(value = "/midCategoryList", method = RequestMethod.GET)
    @ResponseBody
    public Result midCategoryList(@RequestParam(value = "categoryName", required = false) String categoryName, @RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(issueRestService.midCategoryList(taskId, shopId, categoryName));
    }

    @RequestMapping(value = "/smallCategoryList", method = RequestMethod.GET)
    @ResponseBody
    public Result smallCategoryList(@RequestParam(value = "midCategoryName", required = false) String midCategoryName, @RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(issueRestService.smallCategoryList(taskId, shopId, midCategoryName));
    }

    @RequestMapping(value = "/reRunIssueTask", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask() throws Exception {
        try {
            IssueTaskVo taskVo = issueRestService.getLastTask();
            if (taskVo != null && taskVo.getReRun() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RERUN) {
                //存在重算任务在跑
                return ResultUtil.handleSysFailtureReturn("有重算任务正在执行，请稍后再试");
            }else {
                //是否有单店重算
                int recalcCount = recalcTaskService.countRecalcTask(taskVo.getTaskId());
                if (recalcCount > 0){
                    return ResultUtil.handleSysFailtureReturn("存在单店重算任务(" + recalcCount + "个)，不可操作全局重算");
                }
            }
            IssueTask task = issueRestService.createTask(2);
            asyncTask.runIssueTask(task);
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(null);
    }


    @RequestMapping(value = "/addUndoByShopId", method = RequestMethod.GET)
    @ResponseBody
    public Result addUndoByShopId(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) throws Exception {
        List<String> shopIds = splitShopIds(shopId);
        try {
            issueService.processIssueUndo(taskId, shopIds);
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        return ResultUtil.handleSuccessReturn(null);
    }

    @RequestMapping(value = "/checkTaskData", method = RequestMethod.GET)
    @ResponseBody
    public Result checkTaskData(@RequestParam(value = "status", defaultValue = "1") int status) throws Exception {
        return ResultUtil.handleSuccessReturn(issueRestService.checkTaskData(status));
    }

    @RequestMapping(value = "/processGoodsData", method = RequestMethod.GET)
    @ResponseBody
    public Result processGoodsData(@RequestParam("taskId") int taskId, @RequestParam(value = "shopId", defaultValue = "") String shopId) throws Exception {
        List<String> shopIds = splitShopIds(shopId);
        return ResultUtil.handleSuccessReturn(issueService.processIssueGoodsData(taskId, shopIds));
    }

    private List<String> splitShopIds(String shopId) {
        List<String> shopIds = new ArrayList<>();
        if (StringUtils.isNotBlank(shopId)) {
            if (shopId.indexOf(",") > 0) {
                shopIds.addAll(Arrays.asList(shopId.split(",")));
            } else {
                shopIds.add(shopId);
            }
        }
        return shopIds;
    }

    @RequestMapping(value = "/processCategorySkcCount", method = RequestMethod.GET)
    @ResponseBody
    public Result processCategorySkcCount(@RequestParam("taskId") int taskId, @RequestParam(value = "shopId", defaultValue = "") String shopId) throws Exception {
        List<String> shopIds = splitShopIds(shopId);
        return ResultUtil.handleSuccessReturn(issueService.processCategorySkcCount(taskId, shopIds));
    }

    @RequestMapping(value = "/syncIssueTime", method = RequestMethod.GET)
    @ResponseBody
    public Result syncIssueTime() {
        return ResultUtil.handleSuccessReturn(issueRestService.syncIssueTime());
    }

    @GetMapping(value = "/getOrderByIds")
    public Result<List<OrderListVo>> getOrderByIds(@RequestParam("taskId") int taskId, @RequestParam("shopIds") String shopIds) throws Exception {
        List<OrderListVo> orders = issueRestService.getOrderByIds(taskId, shopIds);
        return ResultUtil.handleSuccessReturn(orders);
    }

    @GetMapping(value = "/getIssueDay")
    public Result getIssueDay(@RequestParam("roadDay") int roadDay, @RequestParam("issueTime") String issueTime
            , @RequestParam(value = "dayWeek", required = false) int dayWeek) throws Exception {
        return ResultUtil.handleSuccessReturn(issueRestService.getIssueDay(roadDay, issueTime, dayWeek));
    }

}
