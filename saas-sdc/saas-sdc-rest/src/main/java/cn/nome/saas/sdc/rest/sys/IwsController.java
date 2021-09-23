package cn.nome.saas.sdc.rest.sys;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.manager.ShopsServiceManager;
import cn.nome.saas.sdc.model.vo.IwsShopsVO;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@RestController
@RequestMapping("/sys")
public class IwsController extends BaseController {

    private ShopsServiceManager shopsServiceManager;

    @Autowired
    public IwsController(ShopsServiceManager shopsServiceManager) {
        this.shopsServiceManager = shopsServiceManager;
    }

    @GetMapping(value = "/iws/shops")
    public Result<?> shops() {
        PureListVO<IwsShopsVO> iwsShopsVOPureListVO = new PureListVO<>();
        iwsShopsVOPureListVO.setList(shopsServiceManager.toIws(getCorpId()));
        return ResultUtil.handleSuccessReturn(iwsShopsVOPureListVO);
    }
}
