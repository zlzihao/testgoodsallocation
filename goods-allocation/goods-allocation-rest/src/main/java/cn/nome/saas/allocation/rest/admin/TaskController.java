package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.HttpClientUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.GoodsCategoryTreeCache;
import cn.nome.saas.allocation.feign.api.GuanYuanClient;
import cn.nome.saas.allocation.mapper.TaskMapper;
import cn.nome.saas.allocation.model.allocation.*;
import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskStoreDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskStoreDOV2;
import cn.nome.saas.allocation.service.allocation.AllocationStockService;
import cn.nome.saas.allocation.service.allocation.OutOfStockGoodsService;
import cn.nome.saas.allocation.service.allocation.TaskService;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.service.portal.UserService;
import cn.nome.saas.allocation.utils.AuthUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskController
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
@RestController
@RequestMapping("/allocation")
public class TaskController {

    @Autowired
    TaskService taskService;
    @Autowired
    UserService userService;
    @Autowired
    ShopService shopService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    AllocationStockService allocationStockService;

    @Autowired
    OutOfStockGoodsService outOfStockGoodsService;

    @Autowired
    GuanYuanClient guanYuanClient;

    private static Logger logger = LoggerFactory.getLogger(TaskController.class);

    @RequestMapping(value = "/syncGY", method = RequestMethod.POST)
    @ResponseBody
    public Result syncGY(@RequestBody(required = false) String task_data, HttpServletRequest request) {
        String result_1 = guanYuanClient.syncAllocationTask("w4390f7bc7ed747f399fd484");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        String result_2 =guanYuanClient.syncAllocationDetail("t991d4eb073ad4ee6b5dcdc9");


        LoggerUtil.info(logger,"[GUAN_YUAN] msg=result_1::{0},result_2:{1}",result_1,result_2);

        return ResultUtil.handleSuccessReturn(result_1);
    }

    @RequestMapping(value = "/create_task", method = RequestMethod.POST)
    @ResponseBody
    public Result createTask(@RequestBody(required = false) String task_data,HttpServletRequest request) {

        Task task = TaskMapper.mapper(task_data);

        if (task.getTaskType() < 1 || task.getTaskType() > 3) {
            return ResultUtil.handleFailtureReturn("BIZ", "task_type error");
        }

        try {
           Date rumTime = DateUtil.parse(task.getRunTimeStr(),"yyyy-MM-dd HH:mm:ss");

            task.setRunTime(rumTime);

        } catch (Exception e) {
            return ResultUtil.handleFailtureReturn("BIZ", "run_time format error");
        }

        String userId = AuthUtil.getUserid(request);
        LocalUser user = userService.getUser(userId);

        if (user != null) {
            task.setUserId(userId);
            task.setUserName(user.getUserName());
        } else {
            return ResultUtil.handleFailtureReturn("BIZ", "该用户暂无访问权限");
        }

        // 判断任务名称是否重复
        if (taskService.checkTaskName(task.getTaskName())) {
            return ResultUtil.handleFailtureReturn("BIZ", "当前任务名称已存在，请修改后再试");
        }


        int resultFlag = taskService.createTask(task);

        if (resultFlag != 1) {
            return ResultUtil.handleFailtureReturn("BIZ", "有另外一个任务正在运行，请稍后再试");
        }

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/get_task", method = RequestMethod.GET)
    @ResponseBody
    public Result updateTask(@RequestParam(value = "taskId",required = true) int taskId,HttpServletRequest request) {


        String userId = AuthUtil.getUserid(request);
        LocalUser user = userService.getUser(userId);

        if (user == null) {
            return ResultUtil.handleFailtureReturn("BIZ", "该用户暂无访问权限");
        }

        TaskDO task = taskService.getTask(taskId);

        task.setUserId(userId);
        task.setUserName(user.getUserName());

        return ResultUtil.handleSuccessReturn(task);
    }

    @RequestMapping(value = "/update_task", method = RequestMethod.POST)
    @ResponseBody
    public Result updateTask(@RequestBody(required = false) String task_data,HttpServletRequest request) {

        Task task = TaskMapper.mapper(task_data);


        if (task.getTaskType() < 1 || task.getTaskType() > 3) {
            return ResultUtil.handleFailtureReturn("BIZ", "task_type error");
        }

        try {
            Date rumTime = DateUtil.parse(task.getRunTimeStr(),"yyyy-MM-dd HH:mm:ss");

            task.setRunTime(rumTime);

        } catch (Exception e) {
            return ResultUtil.handleFailtureReturn("BIZ", "run_time format error");
        }

        String userId = AuthUtil.getUserid(request);
        LocalUser user = userService.getUser(userId);

        if (user != null) {
            task.setUserId(userId);
            task.setUserName(user.getUserName());
        } else {
            return ResultUtil.handleFailtureReturn("BIZ", "该用户暂无访问权限");
        }

        int resultFlag = taskService.updateTask(task);

        if (resultFlag != 1) {
            return ResultUtil.handleFailtureReturn("BIZ", "有另外一个任务正在运行，请稍后再试");
        }

        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/task_list", method = RequestMethod.GET)
    @ResponseBody
    public Result taskList(Page page,
                           @RequestParam(value = "allocationType",required = false) Integer allocationType,
                           @RequestParam(value = "keyword",required = false) String keyword
                           ) throws Exception {

        return ResultUtil.handleSuccessReturn(taskService.getTaskList(page,allocationType,keyword),page);
    }

    @RequestMapping(value = "/cancel_task")
    @ResponseBody
    public Result cancelTask(@RequestParam(value = "taskId",required = true) int taskId)
            throws Exception {

        TaskDO taskDO  = taskService.getTask(taskId);

        if (taskDO == null) {
            return ResultUtil.handleFailtureReturn("BIZ", "任务不存在");
        }
        if (taskDO.getTaskStatus() != 1) {
            return ResultUtil.handleFailtureReturn("BIZ", "任务状态错误");
        }

        taskService.cancelTask(taskId);

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result detail(@RequestParam(value = "taskId",required = true) int taskId,
                         @RequestParam(value = "city",required = false) String city,
                         @RequestParam(value = "price",required = false,defaultValue = "0") Integer price,
                         @RequestParam(value = "keyword",required = false) String keyword,
                         @RequestParam(value = "pageNo",required = false,defaultValue = "1") int pageNo,
                         @RequestParam(value = "pageSize",required = false,defaultValue = "1000") int pageSize) {


        Page page = new Page();
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return taskService.getAllocationDetailList(taskId,city,price,keyword,page);
    }

    @RequestMapping(value = "/close_task_list", method = RequestMethod.GET)
    @ResponseBody
    public Result closeTaskList() {

        return ResultUtil.handleSuccessReturn(taskService.getCloseTaskList());
    }

    @RequestMapping(value = "/task_store_list", method = RequestMethod.GET)
    @ResponseBody
    public Result taskStoreList(@RequestParam(value = "task_id") int taskId,
                                  @RequestParam(value = "area_code", defaultValue = "ALL") String areaCode,
                                  @RequestParam(value = "store_keyword", defaultValue = "") String storeKeyword,
                                  @RequestParam(value = "store_type") int store_type,
                                  @RequestParam(value = "price_threshold", defaultValue = "0") int priceThreshold,
                                  @RequestParam(value = "quantity_threshold", defaultValue = "0") int quantityThreshold,
                                  @RequestParam(value = "year", defaultValue = "ALL") String year,
                                  @RequestParam(value = "season", defaultValue = "ALL") String season, Page page) throws Exception {


        List<TaskStoreDO> list = allocationStockService.getTaskStoreList(taskId,areaCode,store_type,priceThreshold,quantityThreshold,year,season);

        if (StringUtils.isNotBlank(storeKeyword)) {
            list = list.stream().filter(taskStoreDO -> {
                String shopName = taskStoreDO.getShopName();
                return shopName.contains(storeKeyword);
            }).collect(Collectors.toList());
        }

        page.setTotalRecord(list.size());
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        int fromIndex = (pageNo - 1) * pageSize;
        int toIndex = pageNo * pageSize;
        int size = list.size();

        fromIndex = fromIndex > size ? size : fromIndex;
        toIndex = toIndex > size ? size : toIndex;

        List<TaskStore> newList = list.subList(fromIndex,toIndex).stream().map(taskStoreDO -> {
            TaskStore taskStore = new TaskStore();

            BeanUtils.copyProperties(taskStoreDO,taskStore);
            return taskStore;
                }
        ).collect(Collectors.toList());


        return ResultUtil.handleSuccessReturn(newList,page);
    }

    @RequestMapping(value = "/task_store_list2", method = RequestMethod.GET)
    @ResponseBody
    public Result task_store_list2(@RequestParam(value = "task_id") int taskId,
                                   @RequestParam(value = "area_code", defaultValue = "ALL") String areaCode,
                                   @RequestParam(value = "store_keyword", defaultValue = "") String storeKeyword,
                                   @RequestParam(value = "price_threshold", defaultValue = "0") int priceThreshold,
                                   @RequestParam(value = "quantity_threshold", defaultValue = "0") int quantityThreshold,
                                   @RequestParam(value = "year", defaultValue = "ALL") String year,
                                   @RequestParam(value = "season", defaultValue = "ALL") String season, Page page) throws Exception {


        List<TaskStoreDOV2>  list =allocationStockService.getTaskAllocationStoreList(taskId,areaCode,priceThreshold,quantityThreshold,year,season);

        if (StringUtils.isNotBlank(storeKeyword)) {
            list = list.stream().filter(taskStoreDO -> {
                String shopName = taskStoreDO.getOutShopName();
                return shopName.contains(storeKeyword);
            }).collect(Collectors.toList());
        }

        page.setTotalRecord(list.size());
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        int fromIndex = (pageNo - 1) * pageSize;
        int toIndex = pageNo * pageSize;
        int size = list.size();

        fromIndex = fromIndex > size ? size : fromIndex;
        toIndex = toIndex > size ? size : toIndex;

        List<TaskStoreV2> newlist = list.subList(fromIndex,toIndex).stream().map(taskStoreDOV2 -> {
            TaskStoreV2 taskStoreV2 = new TaskStoreV2();
            BeanUtils.copyProperties(taskStoreDOV2,taskStoreV2);
            return taskStoreV2;

        }).collect(Collectors.toList());


        return ResultUtil.handleSuccessReturn(newlist,page);

    }


    @RequestMapping(value = "/task_store_commodity_list", method = RequestMethod.GET)
    @ResponseBody
    public Result task_store_commodity_list(@RequestParam(value = "task_id") int task_id,
                                            @RequestParam(value = "shop_id_list") String shop_id_list,
                                            @RequestParam(value = "store_type") int store_type,
                                            @RequestParam(value = "year", defaultValue = "ALL") String year,
                                            @RequestParam(value = "season", defaultValue = "ALL") String season, Page page) throws Exception {

        List<TaskStoreCommodity> list = allocationStockService.getTaskStoreCommodityList(task_id,shop_id_list,store_type,year,season,page);

        return ResultUtil.handleSuccessReturn(list,page);

    }

    @RequestMapping(value = "/task_store_commodity_list2", method = RequestMethod.POST)
    @ResponseBody
    public Result task_store_commodity_list2(@RequestParam(value = "task_id") int task_id,
                                             @RequestParam(value = "year", defaultValue = "ALL") String year,
                                             @RequestParam(value = "season", defaultValue = "ALL") String season,
                                             @RequestBody() String store_id_pair_list) throws Exception {

        String inshopId;
        String outshopId;
        List<TaskStoreCommodity> taskStoreCommodityList = new ArrayList<TaskStoreCommodity>();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonArray pairArray = (JsonArray)jsonParser.parse(store_id_pair_list);

            int pair_num = pairArray.size();
            for (int i = 0; i < pair_num; i++) {

                JsonObject jsonObject = pairArray.get(i).getAsJsonObject();

                inshopId = jsonObject.get("inshop_id").getAsString();
                outshopId = jsonObject.get("outshop_id").getAsString();

                List<TaskStoreCommodity> list = allocationStockService.getTaskStorePairCommodityList(task_id,inshopId,outshopId,year,season);

                if (CollectionUtils.isNotEmpty(list)) {
                    taskStoreCommodityList.addAll(list);
                }
            }

        } catch (Exception e) {
            return ResultUtil.handleFailtureReturn("BIZ", "request body error");
        }

        return ResultUtil.handleSuccessReturn(taskStoreCommodityList);
    }

    @RequestMapping(value = "/download/allocationReport", method = RequestMethod.GET)
    @ResponseBody
    public void downloadAllocationReport(@RequestParam(value = "task_id") int task_id, HttpServletRequest request, HttpServletResponse response) {
        taskService.downloadAllocationDetailList(task_id,request,response);
    }


    @RequestMapping(value = "/area_list", method = RequestMethod.GET)
    @ResponseBody
    public Result area_list() throws Exception {
        return ResultUtil.handleSuccessReturn(shopService.getAreaList());
    }

    @RequestMapping(value = "/inStockStatsPage", method = RequestMethod.GET)
    @ResponseBody
    public Result inStockStatsPage(InStockReq req, Page page) throws Exception {

        List<DemandStock> list = outOfStockGoodsService.demandStockStatsPage(req,page);

        return ResultUtil.handleSuccessReturn(list, page);
    }

    @RequestMapping(value = "/inStockDetailPage", method = RequestMethod.GET)
    @ResponseBody
    public Result inStockDetailPage(InStockReq req, Page page) throws Exception {

        List<DemandStockDetail> list = outOfStockGoodsService.demandStockDetailPage(req,page);
        return ResultUtil.handleSuccessReturn(list, page);
    }

    @RequestMapping(value = "/outStockStatsPage", method = RequestMethod.GET)
    @ResponseBody
    public Result outStockStatsPage(InStockReq req, Page page) throws Exception {
        List<SupplyStock> list = outOfStockGoodsService.getSupplyStockStats(req,page);
        return ResultUtil.handleSuccessReturn(list, page);
    }

    @RequestMapping(value = "/getMidCategory", method = RequestMethod.GET)
    @ResponseBody
    public Result getMidCategory(InStockReq req) throws Exception {

        return ResultUtil.handleSuccessReturn(goodsService.getMidCategory(req.getTaskId()));
    }

    @RequestMapping(value = "/getSmallCategory", method = RequestMethod.GET)
    @ResponseBody
    public Result getSmallCategory(InStockReq req) throws Exception {
        return ResultUtil.handleSuccessReturn(goodsService.getSmallCategory(req.getTaskId(),req.getMidCategoryCode()));
    }

    @RequestMapping(value = "/getTaskProgress", method = RequestMethod.GET)
    @ResponseBody
    public Result getTaskProgress(int taskId) throws Exception {
        return ResultUtil.handleSuccessReturn(taskService.getTaskProgress(taskId));
    }

    @RequestMapping(value = "/getSeasonList", method = RequestMethod.GET)
    @ResponseBody
    public Result getSeasonList() throws Exception {
        return ResultUtil.handleSuccessReturn(goodsService.getSeasonList());
    }

    @RequestMapping(value = "/getYearNoList", method = RequestMethod.GET)
    @ResponseBody
    public Result getYearNoList() throws Exception {
        return ResultUtil.handleSuccessReturn(goodsService.getYearNoList());
    }

    @RequestMapping(value = "/getShopList", method = RequestMethod.GET)
    @ResponseBody
    public Result getShopList() throws Exception {
        return ResultUtil.handleSuccessReturn(shopService.getShopList());
    }

    @RequestMapping(value = "/display/getCategoryTree", method = RequestMethod.GET)
    public Result getCategoryTree(@RequestParam(value = "type",required = false) Integer type){
        List<GoodsCategoryTreeCache> list = goodsService.getGoodsCategoryTree(type);

        return ResultUtil.handleSuccessReturn(list);
    }





}
