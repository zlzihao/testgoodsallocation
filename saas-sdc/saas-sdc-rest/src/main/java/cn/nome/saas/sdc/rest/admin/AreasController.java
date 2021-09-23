package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.manager.AreasServiceManager;
import cn.nome.saas.sdc.model.form.AreasForm;
import cn.nome.saas.sdc.model.form.IdForm;
import cn.nome.saas.sdc.model.form.ImportForm;
import cn.nome.saas.sdc.model.req.AreasReq;
import cn.nome.saas.sdc.model.vo.AreaOptionVO;
import cn.nome.saas.sdc.model.vo.AreasVO;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@RestController
@RequestMapping("/admin")
public class AreasController extends BaseController {

    private AreasServiceManager areasServiceManager;

    @Autowired
    public AreasController(AreasServiceManager areasServiceManager) {
        this.areasServiceManager = areasServiceManager;
    }

    @RequestMapping(value = "/areas/search", method = RequestMethod.GET)
    public Result<Table<AreasVO>> search(@Valid AreasReq req) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        return ResultUtil.handleSuccessReturn(areasServiceManager.search(req, null), null);
    }

    @RequestMapping(value = "/areas/options", method = RequestMethod.GET)
    public Result<?> options(@Valid AreasReq req) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AreaOptionVO> listVO = areasServiceManager.options(req);
        PureListVO<AreaOptionVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVO);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }

    @RequestMapping(value = "/areas/locations", method = RequestMethod.GET)
    public Result<?> locations() {
        List<String> locations = areasServiceManager.getAllLocations(null, Constant.AREA_TYPE_CHANNEL);
        PureListVO<String> pureListVO = new PureListVO<>();
        pureListVO.setList(locations);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }

    @RequestMapping(value = "/areas/delete", method = RequestMethod.POST)
    public Result<?> delete(@Valid @RequestBody IdForm form) {
        AreasForm updateForm = new AreasForm();
        updateForm.setId(form.getId());
        updateForm.setCorpId(this.getCorpId());
        Long uid = Long.valueOf(this.getUid());
        updateForm.setLastUpdateUserId(uid);
        updateForm.setIsDeleted(Constant.IS_DELETE_TRUE);
        areasServiceManager.softDelete(updateForm);
        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/areas/add", method = RequestMethod.POST)
    public Result<?> add(@Valid @RequestBody AreasForm form) {
        form.setCorpId(this.getCorpId());
        Long uid = Long.valueOf(this.getUid());
        form.setCreateUserId(uid);
        form.setLastUpdateUserId(uid);
        areasServiceManager.add(form);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/areas/importMarkingArea", method = RequestMethod.POST)
    public Result<?> importMarkingArea(@Valid ImportForm form) {
        form.setCorpId(getCorpId());
        form.setUserId(getUid().longValue());
        areasServiceManager.importMarkingArea(form);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/areas/exportMarkingArea", method = {RequestMethod.GET, RequestMethod.POST})
    public void exportMarkingArea(HttpServletResponse response) {
        ResponseUtil.export(response, areasServiceManager.exportMarkingArea(getCorpId()), "店铺中心-营销区域信息");
    }


    @RequestMapping(value = "/areas/update", method = RequestMethod.POST)
    public Result<?> update(@Valid @RequestBody AreasForm form) {
        form.setCorpId(this.getCorpId());
        Long uid = Long.valueOf(this.getUid());
        form.setLastUpdateUserId(uid);
        areasServiceManager.update(form);
        return ResultUtil.handleSuccessReturn();
    }

}
