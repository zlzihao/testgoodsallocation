package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.manager.GuanYuanServiceManager;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/2/11 11:49
 */

@RestController
@RequestMapping("/admin")
public class GuanYuanController extends BaseController {

    private GuanYuanServiceManager guanYuanServiceManager;

    @Autowired
    public GuanYuanController(GuanYuanServiceManager guanYuanServiceManager) {
        this.guanYuanServiceManager = guanYuanServiceManager;
    }

    @RequestMapping(value = "/guanYuan/genSsoUrl", method = RequestMethod.GET)
    public Result<?> genSsoUrl() {
        return ResultUtil.handleSuccessReturn(guanYuanServiceManager.genSsoUrl(String.format("%d", getUid())));
    }
}
