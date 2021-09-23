package cn.nome.saas.sdc.rest.user;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.model.form.SeasonChangeSysForm;
import cn.nome.saas.sdc.model.req.SeasonChangeReq;
import cn.nome.saas.sdc.service.SeasonChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author lizihao@nome.com
 */
@RestController
@RequestMapping("/user")
public class UserSeasonChangeController {
    private final Logger logger = LoggerFactory.getLogger(UserSeasonChangeController.class);

    @Autowired
    private SeasonChangeService seasonChangeService;

    @PostMapping("/season/getParam")
    public Result getParam(@Valid @RequestBody SeasonChangeSysForm form) {
        SeasonChangeReq req = new SeasonChangeReq();
        req.setSeasonsAlternateDay(DateUtil.parse(form.getSeasonsAlternateDay(), "yyyy-MM-dd"));
        return ResultUtil.handleSuccessReturn(seasonChangeService.selectByCondition(req));
    }
}
