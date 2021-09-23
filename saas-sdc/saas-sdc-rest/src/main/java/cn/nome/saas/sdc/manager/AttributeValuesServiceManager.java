package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.model.form.AttributeValuesForm;
import cn.nome.saas.sdc.model.req.AttributeValuesReq;
import cn.nome.saas.sdc.model.vo.AttributeValuesVO;
import cn.nome.saas.sdc.repository.entity.AttributeValuesDO;
import cn.nome.saas.sdc.service.AttributeValuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Component
public class AttributeValuesServiceManager {

    private AttributeValuesService attributeValuesService;

    @Autowired
    public AttributeValuesServiceManager(AttributeValuesService attributeValuesService) {
        this.attributeValuesService = attributeValuesService;
    }

    public List<AttributeValuesVO> search(AttributeValuesReq req) {
        return attributeValuesService.search(req);
    }

    public AttributeValuesForm getDetail(Integer id) {
        AttributeValuesVO attributeValuesVO = attributeValuesService.selectByPrimaryKey(id);
        return BaseConvertor.convert(attributeValuesVO, AttributeValuesForm.class);
    }

    public Integer delete(Integer id) {
        return attributeValuesService.deleteByPrimaryKey(id);
    }

    public void add(AttributeValuesForm form) {
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "名称已存在");
        }
        AttributeValuesDO record = BaseConvertor.convert(form, AttributeValuesDO.class);
        attributeValuesService.insertSelective(record);
    }

    public void update(AttributeValuesForm form) {
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "名称已存在");
        }
        AttributeValuesDO record = BaseConvertor.convert(form, AttributeValuesDO.class);
        attributeValuesService.update(record);
    }

    private boolean nameExist(AttributeValuesForm form) {
        AttributeValuesReq req = new AttributeValuesReq();
        req.setCorpId(form.getCorpId());
        req.setAttributeId(form.getAttributeId());
        req.setName(form.getName());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        AttributeValuesVO record = attributeValuesService.nameExist(req);
        if (record == null) {
            return false;
        }
        return form.getId() == null || !form.getId().equals(record.getId());
    }
}
