package cn.nome.saas.sdc.bigData.model;

import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class DwsGoalDayShopCategoryVO {
    private Date operationDate;
    private int shopId;
    private String shopCode;
    private String categoryCode;
    private String categoryName;
    private Long goalDay;

    public DwsGoalDayShopCategoryVO() {
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
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

    public Long getGoalDay() {
        return goalDay;
    }

    public void setGoalDay(Long goalDay) {
        this.goalDay = goalDay;
    }
}
