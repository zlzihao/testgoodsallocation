package cn.nome.saas.sdc.model.vo;


import cn.nome.platform.common.utils.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class AttributesVO extends ToString {

    private Integer id;

    private Integer corpId;

    private Integer attributeTypeId;

    private String name;

    private Integer attributeMediaType;

    private String attributeMediaName;

    private Integer isRequired;

    private Integer sortOrder;

    private String inputTips;

    private Integer isEditable;

    private Integer isEnable;

    private Date createdAt;

    private Date updatedAt;

    private Long createUserId;

    private Long lastUpdateUserId;

    private List<AttributeValuesVO> values;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getAttributeTypeId() {
        return attributeTypeId;
    }

    public void setAttributeTypeId(Integer attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAttributeMediaType() {
        return attributeMediaType;
    }

    public void setAttributeMediaType(Integer attributeMediaType) {
        this.attributeMediaType = attributeMediaType;
    }

    public String getAttributeMediaName() {
        return attributeMediaName;
    }

    public void setAttributeMediaName(String attributeMediaName) {
        this.attributeMediaName = attributeMediaName;
    }

    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getInputTips() {
        return inputTips;
    }

    public void setInputTips(String inputTips) {
        this.inputTips = inputTips;
    }

    public Integer getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Integer isEditable) {
        this.isEditable = isEditable;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public List<AttributeValuesVO> getValues() {
        return values;
    }

    public void setValues(List<AttributeValuesVO> values) {
        this.values = values;
    }
}
