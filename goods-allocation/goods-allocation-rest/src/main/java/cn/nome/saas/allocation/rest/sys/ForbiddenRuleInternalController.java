package cn.nome.saas.allocation.rest.sys;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.rule.ForbiddenSingleRuleByTypeResult;
import cn.nome.saas.allocation.service.rule.ForbiddenRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * NewIssueInternalController
 *
 * @author Bruce01.fan
 * @date 2019/9/11
 */
@RestController
@RequestMapping("/sys/allocation/forbiddenRule")
public class ForbiddenRuleInternalController {

    @Autowired
    ForbiddenRuleService forbiddenRuleService;



    //获取分类禁配门店
    @RequestMapping(value = "/getRuleForCategory", method = RequestMethod.GET)
    public Result<?> getRuleForCategory(@RequestParam(value="type", required=true) int type,
                                        @RequestParam(value="typeValue", required=true) String typeValue) {
            return ResultUtil.handleSuccessReturn(forbiddenRuleService.getRuleForCategory(type, typeValue));

    }

}
