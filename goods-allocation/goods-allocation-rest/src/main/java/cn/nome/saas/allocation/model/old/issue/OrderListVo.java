package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;
import java.util.Date;

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

    private int recalcId;
    private String percent;

    private int status = -1;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRecalcId() {
        return recalcId;
    }

    public void setRecalcId(int recalcId) {
        this.recalcId = recalcId;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

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

    @Override
    public String toString() {
        return "{" +
                "taskId=" + taskId +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", regioneName='" + regioneName + '\'' +
                ", subRegoneName='" + subRegoneName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", packageQty=" + packageQty +
                ", packageVal=" + packageVal +
                ", createdAt='" + createdAt + '\'' +
                ", issueTime='" + issueTime + '\'' +
                ", onRoadDay=" + onRoadDay +
                ", recalcId=" + recalcId +
                ", percent='" + percent + '\'' +
                ", status=" + status +
                '}';
    }
}
