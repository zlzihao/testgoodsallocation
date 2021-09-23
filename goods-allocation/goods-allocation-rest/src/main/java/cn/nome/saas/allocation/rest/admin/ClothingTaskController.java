package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.mapper.TaskMapper;
import cn.nome.saas.allocation.model.allocation.AllocationClothingResultVO;
import cn.nome.saas.allocation.model.allocation.AllocationTask;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationClothingInvalidGoodsMapper;
import cn.nome.saas.allocation.repository.dao.allocation.AllocationClothingResultMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ClothingTaskDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationClothingInvalidGoods;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationClothingResultDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import cn.nome.saas.allocation.service.allocation.ClothingTaskService;
import cn.nome.saas.allocation.service.portal.UserService;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.CommonUtil;
import cn.nome.saas.allocation.utils.ExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ClothingTaskController
 *
 * @author Bruce01.fan
 * @date 2019/12/12
 */
@RestController
@RequestMapping("/allocation/clothing")
public class ClothingTaskController {

    @Autowired
    UserService userService;

    @Autowired
    ClothingTaskDOMapper clothingTaskDOMapper;

    @Autowired
    AllocationClothingResultMapper allocationClothingResultMapper;

    @Autowired
    AllocationClothingInvalidGoodsMapper allocationClothingInvalidGoodsMapper;

    @Autowired
    ClothingTaskService clothingTaskService;

    /**
     *  服装调拨相关接口
     */
    @RequestMapping(value = "/create_task", method = RequestMethod.POST)
    @ResponseBody
    public Result createClothingTask(@RequestBody(required = false) String task_data, HttpServletRequest request) {

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
        if (clothingTaskDOMapper.checkTaskName(task.getTaskName()) > 0) {
            return ResultUtil.handleFailtureReturn("BIZ", "当前任务名称已存在，请修改后再试");
        }

        int resultFlag = clothingTaskDOMapper.createTask(TaskMapper.mapper(task));

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

        TaskDO task = clothingTaskDOMapper.getTask(taskId);

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

        int resultFlag = clothingTaskDOMapper.updateTask(TaskMapper.mapper(task));

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

        return ResultUtil.handleSuccessReturn(this.getTaskList(page,allocationType,keyword),page);
    }

    @RequestMapping(value = "/cancel_task")
    @ResponseBody
    public Result cancelTask(@RequestParam(value = "taskId",required = true) int taskId)
            throws Exception {

        TaskDO taskDO  = clothingTaskDOMapper.getTask(taskId);

        if (taskDO == null) {
            return ResultUtil.handleFailtureReturn("BIZ", "任务不存在");
        }
        if (taskDO.getTaskStatus() != 1) {
            return ResultUtil.handleFailtureReturn("BIZ", "任务状态错误");
        }

        clothingTaskDOMapper.cancelTask(taskId);

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/result")
    @ResponseBody
    public Result result(@RequestParam(value = "taskId",required = true) int taskId)
            throws Exception {
        List<AllocationClothingResultDO> allocationClothingResultDOList = allocationClothingResultMapper.selectByTaskId(taskId);
        List<AllocationClothingResultVO> allocationClothingResultVOList = allocationClothingResultDOList.stream()
                .map(allocationClothingResultDO->{
                    AllocationClothingResultVO allocationClothingResultVO = new AllocationClothingResultVO();

                    Double beforeFullRate = allocationClothingResultDO.getBeforeFullRate()  * 100;
                    Double afterFullRate = allocationClothingResultDO.getAfterFullRate() * 100;

                    allocationClothingResultVO.setShopId(allocationClothingResultDO.getShopId());
                    allocationClothingResultVO.setShopName(allocationClothingResultDO.getShopName());
                    allocationClothingResultVO.setCategoryCode(allocationClothingResultDO.getCategoryCode());
                    allocationClothingResultVO.setCategoryName(allocationClothingResultDO.getCategoryName());
                    allocationClothingResultVO.setLowSkc(allocationClothingResultDO.getLowSkc());
                    allocationClothingResultVO.setStandardSkc(allocationClothingResultDO.getStandardSkc());
                    allocationClothingResultVO.setHighSkc(allocationClothingResultDO.getHighSkc());
                    allocationClothingResultVO.setBeforeFullRate(String.format("%.0f",beforeFullRate)+"%");
                    allocationClothingResultVO.setAfterFullRate(String.format("%.0f",afterFullRate)+"%");
                    allocationClothingResultVO.setBeforeSkc(allocationClothingResultDO.getBeforeSkc());
                    allocationClothingResultVO.setAfterSkc(allocationClothingResultDO.getAfterSkc());
                    allocationClothingResultVO.setBeforeInvalidStyle(allocationClothingResultDO.getBeforeInvalidStyle());
                    allocationClothingResultVO.setBeforeInvalidNum(allocationClothingResultDO.getBeforeInvalidNum());
                    allocationClothingResultVO.setAfterInvalidStyle(allocationClothingResultDO.getAfterInvalidStyle());
                    allocationClothingResultVO.setAfterInvalidNum(allocationClothingResultDO.getAfterInvalidNum());

                    return allocationClothingResultVO;
        }).collect(Collectors.toList());

        return ResultUtil.handleSuccessReturn(allocationClothingResultVOList);
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

        return clothingTaskService.getAllocationDetailList(taskId,city,price,keyword,page);
    }

    @RequestMapping(value = "/download/allocationReport", method = RequestMethod.GET)
    @ResponseBody
    public void downloadAllocationReport(@RequestParam(value = "task_id") int task_id, HttpServletRequest request, HttpServletResponse response) {
        clothingTaskService.downloadAllocationDetailList(task_id,request,response);
    }

    @RequestMapping(value = "/exportInvalidGoods")
    public String exportInvalidGoods(@RequestParam(value = "taskId",required = true) int taskId,
                                     @RequestParam(value = "shopId",required = false) String shopId,
                                     HttpServletRequest request, HttpServletResponse response) {

        TaskDO taskDO  = clothingTaskDOMapper.getTask(taskId);

        if (taskDO == null) {
            return "fail";
        }

        String tableName = CommonUtil.getTaskTableName("out_of_stock_goods",taskId,taskDO.getRunTime());

        List<AllocationClothingInvalidGoods> allocationClothingInvalidGoodsList = allocationClothingInvalidGoodsMapper.selectInvalidGoods(tableName,taskId,shopId);

        try {
        ExcelUtil.exportClothingInvalidGoods(allocationClothingInvalidGoodsList,request,response);
        } catch (Exception e) {
            return "fail";
        }
        return "886";

    }


    public List<AllocationTask> getTaskList(Page page, Integer allocationType, String keyword) {

        Map<String,Object> param = new HashMap<>();

        param.put("allocationType",allocationType);
        param.put("keyword",keyword);

        param.put("page",page);

        if (page != null) {
            int count = clothingTaskDOMapper.selectTaskNum(param);
            page.setTotalRecord(count);
        }

        List<TaskDO> taskDOList = clothingTaskDOMapper.getTaskList(param);

        return TaskMapper.mapperAllocationTask(taskDOList);

    }

}
