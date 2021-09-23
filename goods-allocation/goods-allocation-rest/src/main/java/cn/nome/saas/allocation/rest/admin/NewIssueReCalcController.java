package cn.nome.saas.allocation.rest.admin;


import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.service.allocation.IssueRecalcService;
import cn.nome.saas.allocation.service.allocation.NewIssueRecalcService;
import cn.nome.saas.allocation.service.allocation.NewIssueService;
import cn.nome.saas.allocation.service.allocation.RecalcTaskService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chentaikuang
 */
@RestController
@RequestMapping("/allocation/new/recalc")
public class NewIssueReCalcController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IssueRecalcService issueRecalcService;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    NewIssueService newIssueService;
    @Autowired
    NewIssueRecalcService newIssueRecalcService;

//    @RequestMapping(value = "/singleShop", method = RequestMethod.GET)
//    @ResponseBody
//    public Result singleShop(@RequestParam("recalcId") int recalcId, @RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
//        asyncTask.shopRecalc(recalcId, taskId, shopId);
//        return ResultUtil.handleSuccessReturn(1);
//    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    public Result add(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return newIssueService.add(taskId, shopId);
    }

//    @RequestMapping(value = "/delRecalcData", method = RequestMethod.GET)
//    @ResponseBody
//    public Result delRecalcData(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
//        return ResultUtil.handleSuccessReturn(issueRecalcService.delRecalcData(taskId, shopId));
//    }
//
//    @RequestMapping(value = "/resetStockRemain", method = RequestMethod.GET)
//    @ResponseBody
//    public Result resetStockRemain(@RequestParam("taskId") int taskId) {
//        return ResultUtil.handleSuccessReturn(issueRecalcService.resetStockRemain(taskId));
//    }

    @RequestMapping(value = "/cancle", method = RequestMethod.GET)
    @ResponseBody
    public Result cancle(@RequestParam("recalcId") int recalcId) {

        String operatorId = AuthUtil.getSessionUserId();
        if (!"90000955".equals(operatorId) && !"90001053".equals(operatorId)) {
            return ResultUtil.handleBizFailtureReturn("12000","您暂无权限执行该操作");
        }

        int rst = newIssueRecalcService.cancel(recalcId);
        if (rst == 0){
            return ResultUtil.handleFailtureReturn();
        }
        return ResultUtil.handleSuccessReturn(rst);
    }

    @RequestMapping(value = "/getPercent", method = RequestMethod.GET)
    @ResponseBody
    public Result getPercent(@RequestParam("recalcIds") String recalcIds) {
        return newIssueRecalcService.getPercent(recalcIds);
    }

    @RequestMapping(value = "/singleShopRecalcTask", method = RequestMethod.GET)
    @ResponseBody
    public Result singleShopRecalcTask() throws Exception {

        String operatorId = AuthUtil.getSessionUserId();
        if (!"90000955".equals(operatorId) && !"90001053".equals(operatorId)) {
            return ResultUtil.handleBizFailtureReturn("12000","您暂无权限执行该操作");
        }

        newIssueService.schedulerReCalcTask();
        return ResultUtil.handleSuccessReturn(null);
    }


}
