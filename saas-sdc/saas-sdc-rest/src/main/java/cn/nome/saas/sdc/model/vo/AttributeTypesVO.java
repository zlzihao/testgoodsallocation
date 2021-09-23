package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 13:50
 */
public class AttributeTypesVO extends ToString {

    private Integer id;

    private Integer corpId;

    private Integer sourceTypeId;

    private Integer classificationTypeId;

    private String classificationTypeName;

    private String name;

    private Integer sortOrder;

    private Integer isEnable;

    private List<AttributesVO> attributes;

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

    public Integer getSourceTypeId() {
        return sourceTypeId;
    }

    public void setSourceTypeId(Integer sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public List<AttributesVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesVO> attributes) {
        this.attributes = attributes;
    }
}
