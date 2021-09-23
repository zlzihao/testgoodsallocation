package cn.nome.saas.sdc.model.req;

import cn.nome.platform.common.utils.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/17 14:08
 */
public class SearchBusinessAttributesReq extends ToString {


    private Integer corpId;

    private String attributeName;

    @NotNull(message = "业务ID不能为空")
    private Integer businessId;

    @NotNull(message = "业务类型不能为空")
    private Integer businessType;

    @NotNull(message = "归属类型不能为空")
    private Integer sourceTypeId;

    private String businessIdsStr;

    private Integer isDeleted;

    private String attributeValue;

    private List<Integer> attributeTypes;

    private List<String> attributeNames;

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
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

    public Integer getSourceTypeId() {
        return sourceTypeId;
    }

    public void setSourceTypeId(Integer sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    public String getBusinessIdsStr() {
        return businessIdsStr;
    }

    public void setBusinessIdsStr(String businessIdsStr) {
        this.businessIdsStr = businessIdsStr;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public List<Integer> getAttributeTypes() {
        return attributeTypes;
    }

    public void setAttributeTypes(List<Integer> attributeTypes) {
        this.attributeTypes = attributeTypes;
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }
}
