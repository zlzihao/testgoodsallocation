package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.manager.RegionsServiceManager;
import cn.nome.saas.sdc.model.req.RegionsReq;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.model.vo.RegionsVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/12 11:10
 */
@RestController
@RequestMapping(value = "/admin")
public class RegionsController extends BaseController {

    private RegionsServiceManager regionsServiceManager;

    @Autowired
    public RegionsController(RegionsServiceManager regionsServiceManager) {
        this.regionsServiceManager = regionsServiceManager;
    }

    @RequestMapping(value = "/regions/getList", method = RequestMethod.GET)
    public Result<?> getList() {
        RegionsReq req = new RegionsReq();
        List<RegionsVO> listVo = regionsServiceManager.getList(req);
        PureListVO<RegionsVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVo);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }

}
