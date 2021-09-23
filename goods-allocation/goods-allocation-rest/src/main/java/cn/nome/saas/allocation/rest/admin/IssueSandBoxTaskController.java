package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.service.allocation.IssueSandBoxTaskService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * IssueSandBoxTaskController
 *
 * @author Bruce01.fan
 * @date 2019/11/29
 */
@RestController
@RequestMapping("/allocation/sandbox")
public class IssueSandBoxTaskController {

    @Autowired
    IssueSandBoxTaskService issueSandBoxTaskService;

    @RequestMapping(value = "/task/create", method = RequestMethod.POST)
    @ResponseBody
    public Result createTask(@RequestParam("taskName")String taskName,
                             @RequestParam("startDate")String startDate,
                             @RequestParam("endDate")String endDate,
                             @RequestParam("useSalePredict")Integer useSalePredict,
                             HttpServletRequest request) {

        String userId = AuthUtil.getUserid(request);

        return issueSandBoxTaskService.createTask(taskName,startDate,endDate,useSalePredict, userId);
    }

    @RequestMapping(value = "/task/list", method = RequestMethod.GET)
    @ResponseBody
    public Result getTaskList(@RequestParam(value = "keyword",required = false)String keyword,
                             @RequestParam(value = "pageNo",required = false,defaultValue = "1") int pageNo,
                             @RequestParam(value = "pageSize",required = false,defaultValue = "20") int pageSize) {
        Page page = new Page();
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        List<IssueSandBoxTask> list = issueSandBoxTaskService.queryByParam(keyword,page);
        Integer listCount = issueSandBoxTaskService.queryByParamCount(keyword);

        page.setTotalRecord(listCount);
        page.setTotalPage(list.size() % page.getPageSize() == 0 ? list.size() / page.getPageSize() : list.size() / page.getPageSize() + 1);

        return ResultUtil.handleSuccessReturn(list,page);
    }

    @RequestMapping(value = "/task/recalc")
    @ResponseBody
    public Result recalc(@RequestParam("taskId")Integer taskId) {
        issueSandBoxTaskService.recalc(taskId);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/exportDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result exportDetail(@RequestParam("taskId")Integer taskId, HttpServletRequest request, HttpServletResponse response) {
        issueSandBoxTaskService.exportSandBoxData(taskId, request, response);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/exportStockDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result exportStockDetail(@RequestParam("taskId")Integer taskId, HttpServletRequest request, HttpServletResponse response) {
        issueSandBoxTaskService.exportStockDetail(taskId, request, response);
        return ResultUtil.handleSuccessReturn();
    }
}
