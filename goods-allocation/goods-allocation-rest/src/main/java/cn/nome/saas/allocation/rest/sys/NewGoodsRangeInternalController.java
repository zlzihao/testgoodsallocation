package cn.nome.saas.allocation.rest.sys;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.feign.model.MatCodes;
import cn.nome.saas.allocation.service.allocation.NewGoodsRangeMopService;
import cn.nome.saas.allocation.service.allocation.NewGoodsRangeService;
import cn.nome.saas.allocation.service.allocation.NewIssueMatchService;
import cn.nome.saas.allocation.service.allocation.NewIssueSkuCalcService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * NewIssueInternalController
 *
 * @author Bruce01.fan
 * @date 2019/9/11
 */
@RestController
@RequestMapping("/sys/allocation/newGoodsRange")
public class NewGoodsRangeInternalController {

    @Autowired
    NewGoodsRangeService newGoodsRangeService;
    @Autowired
    NewGoodsRangeMopService newGoodsRangeMopService;

    //提供mop完成首配的计划
    @RequestMapping(value = "/getCompletePlan", method = RequestMethod.POST)
    public Result getCompletePlan(@Valid @RequestBody MatCodes matCodes) {
        return ResultUtil.handleSuccessReturn( newGoodsRangeService.getCompletePlan(matCodes));
    }

    //计划完成首配更新状态
    @RequestMapping(value = "/updateCompletePlan", method = RequestMethod.GET)
    public Result updateCompletePlan() {
        newGoodsRangeService.updateCompletePlan();
        return ResultUtil.handleSuccessReturn();
    }

    //同步MOP铺货计划
    @RequestMapping(value = "/syncPlan", method = RequestMethod.GET)
    public Result syncMopRangePlan() {
        newGoodsRangeMopService.syncMopRangePlan();
        return ResultUtil.handleSuccessReturn();
    }

    //同步MOP铺货计划
    @RequestMapping(value = "/getSizeIdFromBD", method = RequestMethod.GET)
    public Result getSizeIdFromBD() {
        newGoodsRangeMopService.getSizeIdFromBD();
        return ResultUtil.handleSuccessReturn();
    }



}
