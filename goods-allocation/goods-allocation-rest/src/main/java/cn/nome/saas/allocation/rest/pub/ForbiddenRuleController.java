package cn.nome.saas.allocation.rest.pub;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.service.rule.ForbiddenRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ForbiddenRuleController
 *
 * @author Bruce01.fan
 * @date 2019/7/5
 */
@RestController
@RequestMapping("/public/forbidden/rule")
public class ForbiddenRuleController {

    @Autowired
    ForbiddenRuleService forbiddenRuleService;

    @RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
    public Result batchInsert() {

        forbiddenRuleService.batchInsert();

        return ResultUtil.handleSuccessReturn();
    }
}
