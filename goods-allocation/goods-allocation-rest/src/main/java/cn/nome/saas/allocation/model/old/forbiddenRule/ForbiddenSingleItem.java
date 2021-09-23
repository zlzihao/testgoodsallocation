package cn.nome.saas.allocation.model.old.forbiddenRule;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * ForbiddenRuleSingle
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public class ForbiddenSingleItem extends ToString {

    public static final int LARGE_TYPE = 1;

    public static final int MIDDLE_TYPE = 2;

    public static final int SMALL_TYPE = 3;

    public static final int SKC_TYPE = 4;

    public static final int SKU_TYPE = 5;

    private Integer id;

    private String ruleName;

    private Integer ruleId;

    private String shopId;

    private String shopCode;

    private String shopName;

    private int type; // 1-大类 2-中类 3-小类 4-skc 5-sku

    private String typeValue;

    private String typeName;

    private Date startDate;

    private Date endDate;

    private String startDateStr;

    private String endDateStr;

    private String remark;

    private String modifiedBy; // 数据修改者

    private Date createdAt;

    private String createdBy;

    private Date updatedAt;

    private String updatedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public static int getType(String typeName) {
        if (typeName.contains("大类")) {
            return LARGE_TYPE;
        } else if(typeName.contains("中类")) {
            return MIDDLE_TYPE;
        } else if(typeName.contains("小类")) {
            return SMALL_TYPE;
        } else if ("skc".equals(typeName)) {
            return SKC_TYPE;
        } else if("sku".equals(typeName)) {
            return SKU_TYPE;
        }
        return SKC_TYPE;
    }

    public static String getTypeName(int type) {
        if (type == LARGE_TYPE) {
            return "大类";
        } else if(type == MIDDLE_TYPE) {
            return "中类";
        } else if(type == SMALL_TYPE) {
            return "小类";
        } else if (type == SKC_TYPE) {
            return "skc";
        } else if (type == SKU_TYPE) {
            return "sku";
        }
        return "sku";
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getStartDateStr() {
        if (this.startDate != null) {
            return DateUtil.format(this.startDate, DateUtil.DATE_ONLY);
        }
        return "";
    }

    public String getEndDateStr() {
        if (this.endDate != null) {
            return DateUtil.format(this.endDate, DateUtil.DATE_ONLY);
        }
        return "";
    }


}
