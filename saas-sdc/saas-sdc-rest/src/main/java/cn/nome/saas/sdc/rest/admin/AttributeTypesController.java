package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.manager.AttributeTypesServiceManager;
import cn.nome.saas.sdc.model.form.AttributeTypesForm;
import cn.nome.saas.sdc.model.form.IdForm;
import cn.nome.saas.sdc.model.req.AttributeTypesReq;
import cn.nome.saas.sdc.model.vo.AttributeTypesVO;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@RestController
@RequestMapping("/admin")
public class AttributeTypesController extends BaseController {

    private AttributeTypesServiceManager attributeTypesServiceManager;

    @Autowired
    public AttributeTypesController(AttributeTypesServiceManager attributeTypesServiceManager) {
        this.attributeTypesServiceManager = attributeTypesServiceManager;
    }

    @RequestMapping(value = "/attributeTypes/add", method = RequestMethod.POST)
    public Result<?> add(@Valid @RequestBody AttributeTypesForm form) {
        form.setCorpId(this.getCorpId());
        Long uid = Long.valueOf(this.getUid());
        form.setCreateUserId(uid);
        form.setLastUpdateUserId(uid);
        this.attributeTypesServiceManager.add(form);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/attributeTypes/delete", method = RequestMethod.POST)
    public Result<?> delete(@Valid @RequestBody IdForm form) {
        AttributeTypesForm updateForm = new AttributeTypesForm();
        updateForm.setId(form.getId());
        updateForm.setCorpId(this.getCorpId());
        updateForm.setLastUpdateUserId(Long.valueOf(this.getUid()));
        updateForm.setIsDeleted(Constant.IS_DELETE_TRUE);
        attributeTypesServiceManager.update(updateForm);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/attributeTypes/update", method = RequestMethod.POST)
    public Result<?> update(@Valid @RequestBody AttributeTypesForm form) {
        form.setCorpId(this.getCorpId());
        form.setLastUpdateUserId(Long.valueOf(this.getUid()));
        attributeTypesServiceManager.update(form);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/attributeTypes/search", method = RequestMethod.GET)
    public Result<Table<AttributeTypesVO>> search(AttributeTypesReq req, Page page) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        return ResultUtil.handleSuccessReturn(attributeTypesServiceManager.search(req, page), page);
    }

    @RequestMapping(value = "/attributeTypes/all", method = RequestMethod.GET)
    public Result<?> all(AttributeTypesReq req) {
        req.setCorpId(getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributeTypesVO> listVO = attributeTypesServiceManager.search(req, null);
        PureListVO<AttributeTypesVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVO);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }
}
