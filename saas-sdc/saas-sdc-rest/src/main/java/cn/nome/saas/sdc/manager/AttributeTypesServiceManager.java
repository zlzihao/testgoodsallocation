package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.model.form.AttributeTypesForm;
import cn.nome.saas.sdc.model.req.AttributeTypesReq;
import cn.nome.saas.sdc.model.req.QueryDictionaryReq;
import cn.nome.saas.sdc.model.vo.AttributeTypesVO;
import cn.nome.saas.sdc.repository.entity.AttributeTypesDO;
import cn.nome.saas.sdc.service.AttributeTypesService;
import cn.nome.saas.sdc.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:57
 */
@Component
public class AttributeTypesServiceManager {
    private AttributeTypesService attributeTypesService;

    private DictionaryService dictionaryService;

    @Autowired
    public AttributeTypesServiceManager(AttributeTypesService attributeTypesService, DictionaryService dictionaryService) {
        this.attributeTypesService = attributeTypesService;
        this.dictionaryService = dictionaryService;
    }

    List<AttributeTypesVO> filterAttributeTypes(List<Integer> attributeTypeIds) {
        AttributeTypesReq req = new AttributeTypesReq();
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributeTypesVO> list = attributeTypesService.search(req, null);
        list.removeIf(item -> !attributeTypeIds.contains(item.getId()));
        return list;
    }

    public List<AttributeTypesVO> search(AttributeTypesReq req, Page page) {
        List<AttributeTypesVO> listVO = attributeTypesService.search(req, page);
        QueryDictionaryReq queryDictionaryReq = new QueryDictionaryReq();

        queryDictionaryReq.setDictionaryCode(Constant.ATTRIBUTE_TYPE_CLASSIFICATION_TYPE);
        HashMap<Integer, String> dictionariesMap = dictionaryService.queryMap(queryDictionaryReq);

        for (AttributeTypesVO vo : listVO) {
            vo.setClassificationTypeName(dictionariesMap.getOrDefault(vo.getClassificationTypeId(), ""));
        }
        return listVO;
    }

    public AttributeTypesForm getDetail(Integer id) {
        AttributeTypesVO attributeTypesVO = attributeTypesService.selectByPrimaryKey(id);
        return BaseConvertor.convert(attributeTypesVO, AttributeTypesForm.class);
    }

    public void add(AttributeTypesForm form) {
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "类型名称已存在");
        }
        AttributeTypesDO record = BaseConvertor.convert(form, AttributeTypesDO.class);
        attributeTypesService.insertSelective(record);
    }

    public void update(AttributeTypesForm form) {
        if (form.getId() == null) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "ID不能为空");
        }
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "类型名称已存在");
        }
        AttributeTypesDO record = BaseConvertor.convert(form, AttributeTypesDO.class);
        attributeTypesService.update(record);
    }

    private boolean nameExist(AttributeTypesForm form) {
        AttributeTypesReq req = new AttributeTypesReq();
        req.setCorpId(form.getCorpId());
        req.setSourceTypeId(form.getSourceTypeId());
        req.setName(form.getName());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        AttributeTypesVO record = attributeTypesService.nameExist(req);
        if (record == null) {
            return false;
        }
        return form.getId() == null || !form.getId().equals(record.getId());
    }
}
