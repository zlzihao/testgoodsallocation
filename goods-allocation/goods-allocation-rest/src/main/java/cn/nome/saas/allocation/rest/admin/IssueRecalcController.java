package cn.nome.saas.allocation.rest.admin;


import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.service.allocation.IssueRecalcService;
import cn.nome.saas.allocation.service.allocation.RecalcTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chentaikuang
 */
@RestController
@RequestMapping("/allocation/recalc")
public class IssueRecalcController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IssueRecalcService issueRecalcService;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    RecalcTaskService recalcTaskService;

//    @RequestMapping(value = "/singleShop", method = RequestMethod.GET)
//    @ResponseBody
//    public Result singleShop(@RequestParam("recalcId") int recalcId, @RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
//        asyncTask.shopRecalc(recalcId, taskId, shopId);
//        return ResultUtil.handleSuccessReturn(1);
//    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    public Result add(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return recalcTaskService.add(taskId, shopId);
    }

    @RequestMapping(value = "/delRecalcData", method = RequestMethod.GET)
    @ResponseBody
    public Result delRecalcData(@RequestParam("taskId") int taskId, @RequestParam("shopId") String shopId) {
        return ResultUtil.handleSuccessReturn(issueRecalcService.delRecalcData(taskId, shopId));
    }

    @RequestMapping(value = "/resetStockRemain", method = RequestMethod.GET)
    @ResponseBody
    public Result resetStockRemain(@RequestParam("taskId") int taskId) {
        return ResultUtil.handleSuccessReturn(issueRecalcService.resetStockRemain(taskId));
    }

    @RequestMapping(value = "/cancle", method = RequestMethod.GET)
    @ResponseBody
    public Result cancle(@RequestParam("recalcId") int recalcId) {
        int rst = recalcTaskService.cancle(recalcId);
        if (rst == 0){
            return ResultUtil.handleFailtureReturn();
        }
        return ResultUtil.handleSuccessReturn(rst);
    }

    @RequestMapping(value = "/getPercent", method = RequestMethod.GET)
    @ResponseBody
    public Result getPercent(@RequestParam("recalcIds") String recalcIds) {
        return recalcTaskService.getPercent(recalcIds);
    }

    @RequestMapping(value = "/singleShopRecalcTask", method = RequestMethod.GET)
    @ResponseBody
    public Result singleShopRecalcTask() throws Exception {
        issueRecalcService.schedulerRecalcTask();
        return ResultUtil.handleSuccessReturn(null);
    }
}
