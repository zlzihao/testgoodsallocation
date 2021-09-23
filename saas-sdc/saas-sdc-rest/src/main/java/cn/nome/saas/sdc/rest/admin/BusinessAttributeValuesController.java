package cn.nome.saas.sdc.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.event.AttributeUpdateEvent;
import cn.nome.saas.sdc.manager.BusinessAttributeValuesServiceManager;
import cn.nome.saas.sdc.model.form.BusinessAttributeValuesForm;
import cn.nome.saas.sdc.model.req.SearchBusinessAttributesReq;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.model.vo.SearchBusinessAttributesVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
public class BusinessAttributeValuesController extends BaseController {

    private ApplicationEventPublisher applicationEventPublisher;

    private BusinessAttributeValuesServiceManager businessAttributeValuesServiceManager;

    @Autowired
    public BusinessAttributeValuesController(ApplicationEventPublisher applicationEventPublisher, BusinessAttributeValuesServiceManager businessAttributeValuesServiceManager) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.businessAttributeValuesServiceManager = businessAttributeValuesServiceManager;
    }

    @RequestMapping(value = "/businessAttributeValues/save", method = RequestMethod.POST)
    public Result<?> save(@Valid @RequestBody BusinessAttributeValuesForm form) {
        form.setCorpId(this.getCorpId());
        Long uid = Long.valueOf(this.getUid());
        form.setCreateUserId(uid);
        form.setLastUpdateUserId(uid);
        businessAttributeValuesServiceManager.save(form);
        //省份变更时，自动触发渠道区域更新事件
        if (form.getBusinessType().equals(Constant.BUSINESS_TYPE_SHOP) && form.getAttributeId().equals(Constant.FIXED_ATTRIBUTE_ID_PROVINCE)) {
            applicationEventPublisher.publishEvent(new AttributeUpdateEvent(this, form.getCorpId(), form.getBusinessId()));
        }
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/businessAttributeValues/search", method = RequestMethod.GET)
    public Result<PureListVO> search(@Valid SearchBusinessAttributesReq req) {
        req.setCorpId(this.getCorpId());
        List<SearchBusinessAttributesVO> listVO = businessAttributeValuesServiceManager.search(req);
        PureListVO<SearchBusinessAttributesVO> pureListVO = new PureListVO<>();
        pureListVO.setList(listVO);
        return ResultUtil.handleSuccessReturn(pureListVO);
    }
}
