package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.model.form.AttributesForm;
import cn.nome.saas.sdc.model.req.AttributeValuesReq;
import cn.nome.saas.sdc.model.req.AttributesReq;
import cn.nome.saas.sdc.model.req.QueryDictionaryReq;
import cn.nome.saas.sdc.model.vo.AttributeTypesVO;
import cn.nome.saas.sdc.model.vo.AttributesVO;
import cn.nome.saas.sdc.repository.entity.AttributesDO;
import cn.nome.saas.sdc.service.AttributeValuesService;
import cn.nome.saas.sdc.service.AttributesService;
import cn.nome.saas.sdc.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Component
public class AttributesServiceManager {

    private AttributesService attributesService;

    private DictionaryService dictionaryService;

    private AttributeValuesService attributeValuesService;

    private AttributeTypesServiceManager attributeTypesServiceManager;

    @Autowired
    public AttributesServiceManager(AttributesService attributesService, DictionaryService dictionaryService,
                                    AttributeValuesService attributeValuesService, AttributeTypesServiceManager attributeTypesServiceManager) {
        this.attributesService = attributesService;
        this.dictionaryService = dictionaryService;
        this.attributeValuesService = attributeValuesService;
        this.attributeTypesServiceManager = attributeTypesServiceManager;
    }

    public List<AttributeTypesVO> filterAttributes(List<Integer> attributeTypes) {
        List<AttributeTypesVO> attributeTypesList = attributeTypesServiceManager.filterAttributeTypes(attributeTypes);
        AttributesReq req = new AttributesReq();
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributesVO> attributesList = attributesService.search(req, null);
        Map<Integer, List<AttributesVO>> attributesMap = attributesList.stream().collect(Collectors.groupingBy(AttributesVO::getAttributeTypeId));
        for (AttributeTypesVO vo : attributeTypesList) {
            if (attributesMap.containsKey(vo.getId())) {
                vo.setAttributes(attributesMap.get(vo.getId()));
            }
        }
        return attributeTypesList;
    }

    public List<AttributesVO> search(AttributesReq req, Page page) {
        QueryDictionaryReq queryDictionaryReq = new QueryDictionaryReq();
        queryDictionaryReq.setDictionaryCode(Constant.ATTRIBUTE_MEDIA_TYPE);
        HashMap<Integer, String> dictionariesMap = dictionaryService.queryMap(queryDictionaryReq);
        List<AttributesVO> listVO = attributesService.search(req, page);
        for (AttributesVO vo : listVO) {
            if (dictionariesMap.containsKey(vo.getAttributeMediaType())) {
                vo.setAttributeMediaName(dictionariesMap.get(vo.getAttributeMediaType()));
            }
            //todo opt performance
            AttributeValuesReq attributeValuesReq = new AttributeValuesReq();
            attributeValuesReq.setAttributeId(vo.getId());
            vo.setValues(attributeValuesService.search(attributeValuesReq));
        }
        return listVO;
    }

    public AttributesForm getDetail(Integer id) {
        AttributesVO attributesVO = attributesService.selectByPrimaryKey(id);
        return BaseConvertor.convert(attributesVO, AttributesForm.class);
    }

    public Integer delete(Integer id) {
        return attributesService.deleteByPrimaryKey(id);
    }

    public void add(AttributesForm form) {
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "属性名称已存在");
        }
        AttributesDO record = BaseConvertor.convert(form, AttributesDO.class);
        attributesService.insertSelective(record);
    }

    public void update(AttributesForm form) {
        if (form.getId() == null) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "ID不能为空");
        }
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "属性名称已存在");
        }
        AttributesDO record = BaseConvertor.convert(form, AttributesDO.class);
        attributesService.update(record);
    }

    private boolean nameExist(AttributesForm form) {
        AttributesReq req = new AttributesReq();
        req.setCorpId(form.getCorpId());
        req.setAttributeTypeId(form.getAttributeTypeId());
        req.setName(form.getName());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        AttributesVO record = attributesService.nameExist(req);
        if (record == null) {
            return false;
        }
        return form.getId() == null || !form.getId().equals(record.getId());
    }
}
