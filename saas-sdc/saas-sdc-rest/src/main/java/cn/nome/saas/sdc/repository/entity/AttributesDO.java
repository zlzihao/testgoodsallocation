package cn.nome.saas.sdc.repository.entity;


import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/9 15:16
 */
public class AttributesDO extends ToString {


    /**
     *
     **/
    private Integer id;


    /**
     * 企业ID
     **/
    private Integer corpId;

    /**
     * 属性类型ID
     **/
    private Integer attributeTypeId;

    /**
     * 属性名
     **/
    private String name;

    /**
     * 属性媒体类型
     **/
    private Integer attributeMediaType;

    /**
     * 是否必填，1-必填，0-非必填
     **/
    private Integer isRequired;

    /**
     * 排序，默认为0，数字越大越靠前
     **/
    private Integer sortOrder;

    /**
     * 输入提示
     */
    private String inputTips;

    /**
     * 是否可编辑，1-可编辑，0-不可编辑
     */
    private Integer isEditable;

    /**
     * 是否启用，1-启用，0-停用
     **/
    private Integer isEnable;

    /**
     * 创建时间
     **/
    private Date createdAt;

    /**
     * 更新时间
     **/
    private Date updatedAt;

    /**
     * 创建用户ID
     **/
    private Long createUserId;

    /**
     * 最后更新用户ID
     **/
    private Long lastUpdateUserId;

    /**
     * 是否删除，1-已删除，0-未删除
     **/
    private Integer isDeleted;

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

    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    public String getInputTips() {
        return inputTips;
    }

    public void setInputTips(String inputTips) {
        this.inputTips = inputTips;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}
