package cn.nome.saas.allocation.rest.sys;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.service.allocation.RefreshForbiddenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zengdewu@nome.com
 */
@RestController
@RequestMapping("/sys/refreshForbidden")
public class RefreshForbiddenController {
    private final static Logger logger = LoggerFactory.getLogger(RefreshForbiddenController.class);
    private final RefreshForbiddenService refreshForbiddenService;

    @Autowired
    public RefreshForbiddenController(RefreshForbiddenService refreshForbiddenService) {
        this.refreshForbiddenService = refreshForbiddenService;
    }

    @GetMapping("/refresh")
    public Result<?> refresh(@RequestParam("ruleId") Integer ruleId) {
        logger.info("[RefreshForbiddenController] refresh rule id = {}", ruleId);
        refreshForbiddenService.refreshRule(ruleId);
        return ResultUtil.handleSuccessReturn();
    }

    @GetMapping("/autoRefresh")
    public Result<?> autoRefresh() {

        if (Constant.DEBUG_FLAG_STOP_CRONTAB_TASK) {
            logger.info("[DEBUG_FLAG_STOP_CRONTAB_TASK], RefreshForbiddenController.autoRefresh stop");
            return ResultUtil.handleSuccessReturn();
        }

        logger.info("[RefreshForbiddenController] auto refresh");
        refreshForbiddenService.autoRefreshRules();
        return ResultUtil.handleSuccessReturn();
    }


    /**
     *
     * @param type  0：全刷新，1：禁配，3：保底，4：白名单
     * @return 无意义返回
     */
    @GetMapping("/refreshForbiddenAndSecurityAndWhiteList")
    public Result<?> refreshForbiddenAndSecurityAndWhiteList(@RequestParam(defaultValue = "1") Integer type) {
        logger.info("[RefreshForbiddenController] refreshForbiddenAndSecurityAndWhiteList, type: {}", type);
        refreshForbiddenService.refreshForbiddenAndSecurityAndWhiteList(type);
        return ResultUtil.handleSuccessReturn();
    }
}
