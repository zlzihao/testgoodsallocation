package cn.nome.saas.allocation.rest.sys;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.service.allocation.IssueDayService;
import cn.nome.saas.allocation.service.allocation.NewIssueMatchService;
import cn.nome.saas.allocation.service.allocation.NewIssueSkuCalcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * NewIssueInternalController
 *
 * @author Bruce01.fan
 * @date 2019/9/11
 */
@RestController
@RequestMapping("/sys/allocation/newIssue")
public class NewIssueInternalController {

    @Autowired
    NewIssueSkuCalcService newIssueSkuCalcService;

    @Autowired
    NewIssueMatchService newIssueMatchService;

    @Autowired
    IssueDayService issueDayService;


//    @RequestMapping(value = "/calcSingleStock", method = RequestMethod.GET)
//    public Result calcSingleStock(@RequestParam("taskId") int taskId,
//                                  @RequestParam("shopId") String shopId) {
//        newIssueSkuCalcService.calcSKURequirement(taskId,shopId);
//        return ResultUtil.handleSuccessReturn();
//    }

    @RequestMapping(value = "/calcIssueDate", method = RequestMethod.GET)
    public Result processCategorySkcData(@RequestParam("startDate") String startDate,
                                         @RequestParam("endDate") String endDate) {

        issueDayService.calcShopIssueDay(startDate,endDate);
        return ResultUtil.handleSuccessReturn();
    }

}
