package cn.nome.saas.sdc.bigData.repository.entity;

import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class DwsGoalDayShopCategoryDO {
    private Date operationDate;
    private String shopId;
    private String shopCode;
    private String categoryCode;
    private String categoryName;
    private Double goalDay;

    public DwsGoalDayShopCategoryDO() {
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getGoalDay() {
        return goalDay;
    }

    public void setGoalDay(Double goalDay) {
        this.goalDay = goalDay;
    }
}
