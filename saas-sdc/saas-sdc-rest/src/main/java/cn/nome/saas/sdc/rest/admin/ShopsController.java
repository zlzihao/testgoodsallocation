package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.manager.ShopsServiceManager;
import cn.nome.saas.sdc.model.form.ImportForm;
import cn.nome.saas.sdc.model.form.ShopsForm;
import cn.nome.saas.sdc.model.req.ShopsExportReq;
import cn.nome.saas.sdc.model.req.ShopsReq;
import cn.nome.saas.sdc.model.vo.*;
import cn.nome.saas.sdc.rest.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@RestController
@RequestMapping("/admin")
public class ShopsController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopsController.class);

    private final ShopsServiceManager shopsServiceManager;

    @Autowired
    public ShopsController(ShopsServiceManager shopsServiceManager) {
        this.shopsServiceManager = shopsServiceManager;
    }

    @GetMapping(value = "/shops/issueShops")
    public Result<List<IssueShopVO>> getIssueShops() {
        return ResultUtil.handleSuccessReturn(this.shopsServiceManager.getIssueShops(this.getCorpId()));
    }

    @GetMapping(value = "/shops/toIws")
    public Result<?> toIws() {
        PureListVO<IwsShopsVO> iwsShopsVOPureListVO = new PureListVO<>();
        iwsShopsVOPureListVO.setList(shopsServiceManager.toIws(getCorpId()));
        return ResultUtil.handleSuccessReturn(iwsShopsVOPureListVO);
    }

    @RequestMapping(value = "/shops/search", method = RequestMethod.GET)
    public Result<Table<ShopsVO>> search(ShopsReq req, Page page) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<ShopsVO> list;
        try {
            list = shopsServiceManager.search(req, page);
        } catch (Exception e) {
            LOGGER.error("[ShopsController/search catch exception]", e);
            throw new BusinessException(ReturnType.SYSTEM_FAIL.getType(), ReturnType.SYSTEM_FAIL.getMsg());
        }
        return ResultUtil.handleSuccessReturn(list, page);
    }

    @RequestMapping(value = "/shops/getDetail", method = RequestMethod.GET)
    public Result<?> getDetail(@RequestParam(required = true) Integer id) {
        return ResultUtil.handleSuccessReturn(shopsServiceManager.getDetail(id, this.getCorpId()));
    }

    @RequestMapping(value = "/shops/options", method = RequestMethod.GET)
    public Result<?> getNames() {
        ShopsReq req = new ShopsReq();
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<ShopOptionVO> listVO = shopsServiceManager.queryAll(req);
        PureListVO<ShopOptionVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVO);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }

    @RequestMapping(value = "/shops/update", method = RequestMethod.POST)
    public Result<?> update(@Valid @RequestBody ShopsForm form) {
        Long uid = Long.valueOf(this.getUid());
        form.setLastUpdateUserId(uid);
        form.setCorpId(this.getCorpId());
        shopsServiceManager.update(form);
        return ResultUtil.handleSuccessReturn();
    }

    @PostMapping(value = "/shops/importShops")
    public Result<?> importShops(@Valid ImportForm form) {
        form.setCorpId(getCorpId());
        form.setUserId(getUid().longValue());
        shopsServiceManager.importShops(form);
        return ResultUtil.handleSuccessReturn();
    }

    @GetMapping(value = "/shops/exportShops")
    public void exportShops(HttpServletResponse response, @Valid ShopsExportReq req) {
        req.setCorpId(getCorpId());
        ResponseUtil.export(response, shopsServiceManager.exportShops(req), "店铺中心-店铺属性数据");
    }

    /*
     * @describe 根据门店销售额更新门店状态
     * */

    @GetMapping(value = "/shops/autoStatus")
    public Result<?> autoStatus() {
        shopsServiceManager.autoChangeStatus();
        return ResultUtil.handleSuccessReturn();
    }
}
