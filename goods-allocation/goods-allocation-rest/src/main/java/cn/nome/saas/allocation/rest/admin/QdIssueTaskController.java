package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.repository.dao.allocation.QdIssueConfigDOMapper;
import cn.nome.saas.allocation.service.allocation.QdIssueTaskService;
import cn.nome.saas.allocation.service.basic.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * QdIssueTaskController
 *
 * @author Bruce01.fan
 * @date 2019/8/14
 */
@RequestMapping("/allocation/qdIssue")
@RestController
public class QdIssueTaskController {

    @Autowired
    QdIssueTaskService qdIssueTaskService;

    @Autowired
    GoodsService goodsService;


    @RequestMapping(value = "/createTask")
    @ResponseBody
    public Result createTask(@RequestParam("taskName")String taskName,
                             @RequestParam("runTime")String runTime) {
        return qdIssueTaskService.createTask(taskName,runTime);
    }

    @RequestMapping(value = "/taskList")
    @ResponseBody
    public Result taskList(@RequestParam("taskName")String taskName) {

        return ResultUtil.handleSuccessReturn(qdIssueTaskService.list(taskName));
    }

    @RequestMapping(value = "/deleteTask")
    @ResponseBody
    public Result deleteTask(@RequestParam("taskId")int taskId) {

        Integer count = qdIssueTaskService.deleteByTaskId(taskId);

        if (count > 0) {
            return ResultUtil.handleSuccessReturn();
        } else {
            return ResultUtil.handleFailtureReturn("12000","秋冬老品任务删除失败，请稍后再试");
        }

    }

    @RequestMapping(value = "/season/get")
    @ResponseBody
    public Result getSeason() {
        return ResultUtil.handleSuccessReturn(goodsService.getSeasonNameList());
    }

    @RequestMapping(value = "/season/save")
    @ResponseBody
    public Result saveSeason(@RequestParam("season")String season) {
        qdIssueTaskService.saveConfig(1,season);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/fullRate/save")
    @ResponseBody
    public Result saveFullRate(@RequestParam("fullRate")String fullRate) {
        qdIssueTaskService.saveConfig(2,fullRate);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/config")
    @ResponseBody
    public Result getConfig() {
        return ResultUtil.handleSuccessReturn(qdIssueTaskService.getConfig());
    }

}
