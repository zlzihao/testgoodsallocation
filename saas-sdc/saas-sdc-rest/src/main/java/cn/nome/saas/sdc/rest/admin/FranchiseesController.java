package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.manager.FranchiseesServiceManager;
import cn.nome.saas.sdc.model.req.FranchiseesReq;
import cn.nome.saas.sdc.model.vo.FranchiseesVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 13:54
 */
@RestController
@RequestMapping("/admin")
public class FranchiseesController extends BaseController {

    private FranchiseesServiceManager franchiseesServiceManager;

    @Autowired
    public FranchiseesController(FranchiseesServiceManager franchiseesServiceManager) {
        this.franchiseesServiceManager = franchiseesServiceManager;
    }

    @RequestMapping(value = "/franchisees/search", method = RequestMethod.GET)
    public Result<Table<FranchiseesVO>> search(@Valid FranchiseesReq req, Page page) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        return ResultUtil.handleSuccessReturn(franchiseesServiceManager.search(req, page), page);
    }
}
