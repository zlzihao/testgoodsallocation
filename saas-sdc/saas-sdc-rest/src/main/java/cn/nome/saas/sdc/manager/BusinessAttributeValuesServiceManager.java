package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.event.BusinessAttributeValueSaveEvent;
import cn.nome.saas.sdc.model.form.BusinessAttributeValuesForm;
import cn.nome.saas.sdc.model.req.*;
import cn.nome.saas.sdc.model.vo.*;
import cn.nome.saas.sdc.repository.entity.BusinessAttributeValuesDO;
import cn.nome.saas.sdc.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Component
public class BusinessAttributeValuesServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessAttributeValuesServiceManager.class);

    private BusinessAttributeValuesService businessAttributeValuesService;

    private SearchBusinessAttributesService searchBusinessAttributesService;

    private AttributeTypesService attributeTypesService;

    private AttributesService attributesService;

    private DictionaryService dictionaryService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public BusinessAttributeValuesServiceManager(
            BusinessAttributeValuesService businessAttributeValuesService,
            SearchBusinessAttributesService searchBusinessAttributesService,
            AttributeTypesService attributeTypesService,
            AttributesService attributesService,
            DictionaryService dictionaryService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.businessAttributeValuesService = businessAttributeValuesService;
        this.searchBusinessAttributesService = searchBusinessAttributesService;
        this.attributeTypesService = attributeTypesService;
        this.attributesService = attributesService;
        this.dictionaryService = dictionaryService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void save(BusinessAttributeValuesForm form) {
        if (form.getAttributeValueId() == null && form.getAttributeValue() == null) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "属性值不能为空");
        }

        BusinessAttributeValuesReq req = new BusinessAttributeValuesReq();
        req.setCorpId(form.getCorpId());
        req.setBusinessType(form.getBusinessType());
        req.setBusinessId(form.getBusinessId());
        req.setAttributeId(form.getAttributeId());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<BusinessAttributeValuesVO> listVO = businessAttributeValuesService.query(req);
        if (listVO.size() > 1) {
            LOGGER.warn(System.out.printf("属性值数据异常，business_type=%d, business_id=%d, attribute_id=%d",
                    form.getBusinessType(), form.getBusinessId(), form.getAttributeId()).toString());
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "该属性值数据异常");
        }
//        String oldAttributeValue;
        BusinessAttributeValuesDO record = BaseConvertor.convert(form, BusinessAttributeValuesDO.class);
        if (listVO.size() > 0) {
            BusinessAttributeValuesVO businessAttributeValuesVO = (BusinessAttributeValuesVO) CollectionUtils.get(listVO, 0);
//            oldAttributeValue = businessAttributeValuesVO.getAttributeValue();
            record.setId(businessAttributeValuesVO.getId());
            record.setCreateUserId(null);
            //更新
            businessAttributeValuesService.updateByPrimaryKeySelective(record);
        } else {
            //新增
//            oldAttributeValue = "";
            businessAttributeValuesService.insertSelective(record);
        }
//        applicationEventPublisher.publishEvent(new BusinessAttributeValueSaveEvent(this, record, oldAttributeValue));
    }

    public List<SearchBusinessAttributesVO> search(SearchBusinessAttributesReq req) {
        return searchBusinessAttributesService.search(req);
    }

    List<SearchBusinessAttributesVO> getBusinessAttributes(Integer corpId, Integer businessId, Integer businessType, Integer sourceTypeId) {
        //attribute types
        AttributeTypesReq attributeTypesReq = new AttributeTypesReq();
        attributeTypesReq.setCorpId(corpId);
        attributeTypesReq.setSourceTypeId(sourceTypeId);
        attributeTypesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributeTypesVO> attributeTypesVOS = attributeTypesService.search(attributeTypesReq, null);

        //attributes
        AttributesReq attributesReq = new AttributesReq();
        attributesReq.setCorpId(corpId);
        attributesReq.setIsEnable(Constant.IS_ENABLE_TRUE);
        attributesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributesVO> attributesVOS = attributesService.search(attributesReq, null);

        //business attributes
        BusinessAttributeValuesReq businessAttributeValuesReq = new BusinessAttributeValuesReq();
        businessAttributeValuesReq.setCorpId(corpId);
        businessAttributeValuesReq.setBusinessType(businessType);
        businessAttributeValuesReq.setBusinessId(businessId);
        businessAttributeValuesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<BusinessAttributeValuesVO> businessAttributeValuesVOS = businessAttributeValuesService.query(businessAttributeValuesReq);

        //dictionaries
        QueryDictionaryReq queryDictionaryReq = new QueryDictionaryReq();
        queryDictionaryReq.setDictionaryCode(Constant.ATTRIBUTE_TYPE_CLASSIFICATION_TYPE);
        List<DictionaryVO> dictionaries = dictionaryService.query(queryDictionaryReq);
        Map<Integer, String> dictionariesNameMap = dictionaries.stream().collect(Collectors.toMap(DictionaryVO::getId, DictionaryVO::getName));
        Map<Integer, Integer> dictionariesSortMap = dictionaries.stream().collect(Collectors.toMap(DictionaryVO::getId, DictionaryVO::getSortOrder));

        List<SearchBusinessAttributesVO> searchBusinessAttributesVOS = new ArrayList<>();
        SearchBusinessAttributesVO searchBusinessAttributesVO = new SearchBusinessAttributesVO();
        for (AttributeTypesVO attributeTypesVO : attributeTypesVOS) {
            searchBusinessAttributesVO.setAttributeTypeId(attributeTypesVO.getId());
            searchBusinessAttributesVO.setAttributeTypeName(attributeTypesVO.getName());
            searchBusinessAttributesVO.setAttributeTypeSortOrder(attributeTypesVO.getSortOrder());
            searchBusinessAttributesVO.setClassificationTypeId(attributeTypesVO.getClassificationTypeId());
            searchBusinessAttributesVO.setClassificationTypeName(dictionariesNameMap.getOrDefault(attributeTypesVO.getClassificationTypeId(), ""));
            searchBusinessAttributesVO.setClassificationTypeSortOrder(dictionariesSortMap.getOrDefault(attributeTypesVO.getClassificationTypeId(), 0));

            //init
            searchBusinessAttributesVO.setAttributeId(0);
            searchBusinessAttributesVO.setAttributeName("");
            searchBusinessAttributesVO.setAttributeSortOrder(0);
            searchBusinessAttributesVO.setAttributeMediaType(0);
            searchBusinessAttributesVO.setIsEditable(0);
            searchBusinessAttributesVO.setBusinessId(businessId);
            searchBusinessAttributesVO.setBusinessType(businessType);
            searchBusinessAttributesVO.setAttributeValueId(0);
            searchBusinessAttributesVO.setAttributeValue("");

            boolean hasAttributes = false;
            for (AttributesVO attributesVO : attributesVOS) {
                if (!attributesVO.getAttributeTypeId().equals(attributeTypesVO.getId())) {
                    continue;
                }
                hasAttributes = true;
                searchBusinessAttributesVO.setAttributeId(attributesVO.getId());
                searchBusinessAttributesVO.setAttributeName(attributesVO.getName());
                searchBusinessAttributesVO.setAttributeSortOrder(attributesVO.getSortOrder());
                searchBusinessAttributesVO.setAttributeMediaType(attributesVO.getAttributeMediaType());
                searchBusinessAttributesVO.setIsEditable(attributesVO.getIsEditable());
                boolean hasAttributeValues = false;
                for (BusinessAttributeValuesVO businessAttributeValuesVO : businessAttributeValuesVOS) {
                    if (!attributesVO.getId().equals(businessAttributeValuesVO.getAttributeId())) {
                        continue;
                    }
                    hasAttributeValues = true;
                    searchBusinessAttributesVO.setAttributeValueId(businessAttributeValuesVO.getAttributeValueId());
                    searchBusinessAttributesVO.setAttributeValue(businessAttributeValuesVO.getAttributeValue());

                    SearchBusinessAttributesVO target = new SearchBusinessAttributesVO();
                    BeanUtils.copyProperties(searchBusinessAttributesVO, target);
                    searchBusinessAttributesVOS.add(target);
                    break;
                }
                if (!hasAttributeValues) {
                    searchBusinessAttributesVO.setAttributeValueId(0);
                    searchBusinessAttributesVO.setAttributeValue("");
                    SearchBusinessAttributesVO target = new SearchBusinessAttributesVO();
                    BeanUtils.copyProperties(searchBusinessAttributesVO, target);
                    searchBusinessAttributesVOS.add(target);
                }
            }

            if (!hasAttributes) {
                SearchBusinessAttributesVO target = new SearchBusinessAttributesVO();
                BeanUtils.copyProperties(searchBusinessAttributesVO, target);
                searchBusinessAttributesVOS.add(target);
            }
        }
        //sort
        searchBusinessAttributesVOS.sort((prev, next) -> {
            int classificationTypeSortOrderDiff = prev.getClassificationTypeSortOrder().compareTo(next.getClassificationTypeSortOrder());
            if (classificationTypeSortOrderDiff == 0) {
                int attributeTypeSortOrderDiff = prev.getAttributeTypeSortOrder().compareTo(next.getAttributeTypeSortOrder());
                if (attributeTypeSortOrderDiff == 0) {
                    return prev.getAttributeSortOrder().compareTo(next.getAttributeSortOrder());
                }
                return attributeTypeSortOrderDiff;
            }
            return classificationTypeSortOrderDiff;
        });
        return searchBusinessAttributesVOS;
    }

    public List<BusinessAttributeValuesVO> query(BusinessAttributeValuesReq req) {
        return businessAttributeValuesService.query(req);
    }
}
