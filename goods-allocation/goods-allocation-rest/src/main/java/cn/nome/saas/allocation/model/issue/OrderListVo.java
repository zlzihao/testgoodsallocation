package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;

public class OrderListVo {

    private int taskId;
    private String shopId;
    private String shopName;
    private String regioneName;
    private String subRegoneName;
    private String cityName;
    private BigDecimal packageQty;
    private BigDecimal packageVal;
    private String createdAt;
    private String issueTime;
    private int onRoadDay;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public String getRegioneName() {
        return regioneName;
    }

    public void setRegioneName(String regioneName) {
        this.regioneName = regioneName;
    }

    public String getSubRegoneName() {
        return subRegoneName;
    }

    public void setSubRegoneName(String subRegoneName) {
        this.subRegoneName = subRegoneName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public BigDecimal getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(BigDecimal packageQty) {
        this.packageQty = packageQty;
    }

    public BigDecimal getPackageVal() {
        return packageVal;
    }

    public void setPackageVal(BigDecimal packageVal) {
        this.packageVal = packageVal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public int getOnRoadDay() {
        return onRoadDay;
    }

    public void setOnRoadDay(int onRoadDay) {
        this.onRoadDay = onRoadDay;
    }
}
