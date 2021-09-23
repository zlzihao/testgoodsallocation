package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/17 14:19
 */
public class SearchBusinessAttributesVO extends ToString {

    private Integer attributeId;

    private String attributeName;

    private Integer attributeMediaType;

    private Integer isEditable;

    private Integer attributeTypeId;

    private String attributeTypeName;

    private Integer classificationTypeId;

    private String classificationTypeName;

    private Integer classificationTypeSortOrder;

    private Integer businessId;

    private Integer businessType;

    private Integer attributeValueId;

    private String attributeValue;

    private Integer attributeTypeSortOrder;

    private Integer attributeSortOrder;

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Integer getAttributeMediaType() {
        return attributeMediaType;
    }

    public void setAttributeMediaType(Integer attributeMediaType) {
        this.attributeMediaType = attributeMediaType;
    }

    public Integer getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Integer isEditable) {
        this.isEditable = isEditable;
    }

    public Integer getAttributeTypeId() {
        return attributeTypeId;
    }

    public void setAttributeTypeId(Integer attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public String getAttributeTypeName() {
        return attributeTypeName;
    }

    public void setAttributeTypeName(String attributeTypeName) {
        this.attributeTypeName = attributeTypeName;
    }

    public Integer getClassificationTypeId() {
        return classificationTypeId;
    }

    public void setClassificationTypeId(Integer classificationTypeId) {
        this.classificationTypeId = classificationTypeId;
    }

    public String getClassificationTypeName() {
        return classificationTypeName;
    }

    public void setClassificationTypeName(String classificationTypeName) {
        this.classificationTypeName = classificationTypeName;
    }

    public Integer getClassificationTypeSortOrder() {
        return classificationTypeSortOrder;
    }

    public void setClassificationTypeSortOrder(Integer classificationTypeSortOrder) {
        this.classificationTypeSortOrder = classificationTypeSortOrder;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getAttributeValueId() {
        return attributeValueId;
    }

    public void setAttributeValueId(Integer attributeValueId) {
        this.attributeValueId = attributeValueId;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Integer getAttributeTypeSortOrder() {
        return attributeTypeSortOrder;
    }

    public void setAttributeTypeSortOrder(Integer attributeTypeSortOrder) {
        this.attributeTypeSortOrder = attributeTypeSortOrder;
    }

    public Integer getAttributeSortOrder() {
        return attributeSortOrder;
    }

    public void setAttributeSortOrder(Integer attributeSortOrder) {
        this.attributeSortOrder = attributeSortOrder;
    }
}
