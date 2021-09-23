package cn.nome.saas.sdc.model.form;

import cn.nome.platform.common.utils.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class AttributesForm extends ToString {

    private Integer id;

    private Integer corpId;

    @NotNull(message = "属性类型ID不能为空")
    private Integer attributeTypeId;

    @Length(max = 128, min = 0, message = "属性名字符长度不能超过128")
    private String name;

    @NotNull(message = "属性媒体类型不能为空")
    private Integer attributeMediaType;

    @NotNull(message = "是否必填不能为空")
    private Integer isRequired;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @Length(max = 128, min = 0, message = "输入提示字符长度不能超过128")
    private String inputTips;

    private Integer isEnable;

    private Long createUserId;

    private Long lastUpdateUserId;

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

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
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