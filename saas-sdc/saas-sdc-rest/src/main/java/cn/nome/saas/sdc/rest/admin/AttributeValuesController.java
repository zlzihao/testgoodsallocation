package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.manager.AttributeValuesServiceManager;
import cn.nome.saas.sdc.model.form.AttributeValuesForm;
import cn.nome.saas.sdc.model.form.IdForm;
import cn.nome.saas.sdc.model.req.AttributeValuesReq;
import cn.nome.saas.sdc.model.vo.AttributeValuesVO;
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
public class AttributeValuesController extends BaseController {

    private AttributeValuesServiceManager attributeValuesServiceManager;

    @Autowired
    public AttributeValuesController(AttributeValuesServiceManager attributeValuesServiceManager) {
        this.attributeValuesServiceManager = attributeValuesServiceManager;
    }

    @RequestMapping(value = "/attributeValues/search", method = RequestMethod.GET)
    public Result<?> search(AttributeValuesReq req) {
        req.setCorpId(this.getCorpId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributeValuesVO> listVO = attributeValuesServiceManager.search(req);

        PureListVO<AttributeValuesVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVO);

        return ResultUtil.handleSuccessReturn(pureListVO);
    }

    @RequestMapping(value = "/attributeValues/delete", method = RequestMethod.POST)
    public Result<?> delete(@Valid @RequestBody IdForm form) {
        AttributeValuesForm updateForm = new AttributeValuesForm();
        updateForm.setId(form.getId());
        Long uid = Long.valueOf(this.getUid());
        updateForm.setLastUpdateUserId(uid);
        updateForm.setCorpId(this.getCorpId());
        updateForm.setIsDeleted(Constant.IS_DELETE_TRUE);
        attributeValuesServiceManager.update(updateForm);
        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/attributeValues/add", method = RequestMethod.POST)
    public Result<?> add(@Valid @RequestBody AttributeValuesForm form) {
        Long uid = Long.valueOf(this.getUid());
        form.setCreateUserId(uid);
        form.setLastUpdateUserId(uid);
        form.setCorpId(this.getCorpId());
        attributeValuesServiceManager.add(form);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/attributeValues/update", method = RequestMethod.POST)
    public Result<?> update(@Valid @RequestBody AttributeValuesForm form) {
        Long uid = Long.valueOf(this.getUid());
        form.setLastUpdateUserId(uid);
        form.setCorpId(this.getCorpId());
        attributeValuesServiceManager.update(form);
        return ResultUtil.handleSuccessReturn();
    }
}
