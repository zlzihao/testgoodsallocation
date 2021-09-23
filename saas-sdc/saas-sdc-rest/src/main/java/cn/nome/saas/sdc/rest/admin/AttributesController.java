package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.manager.AttributesServiceManager;
import cn.nome.saas.sdc.model.form.AttributesForm;
import cn.nome.saas.sdc.model.form.IdForm;
import cn.nome.saas.sdc.model.req.AttributesReq;
import cn.nome.saas.sdc.model.vo.AttributesVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@RestController
@RequestMapping("/admin")
public class AttributesController extends BaseController {

    private AttributesServiceManager attributesServiceManager;

    @Autowired
    public AttributesController(AttributesServiceManager attributesServiceManager) {
        this.attributesServiceManager = attributesServiceManager;
    }

    @RequestMapping(value = "/attributes/add", method = RequestMethod.POST)
    public Result<?> add(@Valid @RequestBody AttributesForm form) {
        Long uid = Long.valueOf(this.getUid());
        form.setCreateUserId(uid);
        form.setLastUpdateUserId(uid);
        form.setCorpId(this.getCorpId());
        attributesServiceManager.add(form);

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/attributes/search", method = RequestMethod.GET)
    public Result<Table<AttributesVO>> search(AttributesReq req, Page page) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        return ResultUtil.handleSuccessReturn(attributesServiceManager.search(req, page), page);
    }

    @RequestMapping(value = "/attributes/delete", method = RequestMethod.POST)
    public Result<?> delete(@Valid @RequestBody IdForm form) {
        AttributesForm updateForm = new AttributesForm();
        updateForm.setId(form.getId());
        Long uid = Long.valueOf(this.getUid());
        updateForm.setCorpId(this.getCorpId());
        updateForm.setLastUpdateUserId(uid);
        updateForm.setCreateUserId(uid);
        updateForm.setIsDeleted(Constant.IS_DELETE_TRUE);
        attributesServiceManager.update(updateForm);
        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/attributes/update", method = RequestMethod.POST)
    public Result<?> update(@Valid @RequestBody AttributesForm form) {
        Long uid = Long.valueOf(this.getUid());
        form.setCorpId(this.getCorpId());
        form.setLastUpdateUserId(uid);
        form.setCreateUserId(uid);
        attributesServiceManager.update(form);
        return ResultUtil.handleSuccessReturn();
    }
}
