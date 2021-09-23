package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * MidCategoryStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class MidCategoryStockDO extends ToString{

    private int taskId;

    private String categoryName;

    private String midCategoryName;

    private String shopID;

    private Double avgSaleQty;

    private Double displayQty = 0D;

    private Double needQty = 0D;

    private Double displayPercent;

    private int displayDepth;

    /**
     * DisplayQty * DisplayDepth
     */
    private Double displayQtyA;

    /**
     * DisplayQty * DisplayDepth * DisplayPercent
     */
    private Double displayQtyB;

    private int issueDay;

    private int safeDay;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Double getDisplayQty() {
        return displayQty;
    }

    public void setDisplayQty(Double displayQty) {
        this.displayQty = displayQty;
    }

    public Double getNeedQty() {
        return needQty;
    }

    public void setNeedQty(Double needQty) {
        this.needQty = needQty;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public Double getDisplayPercent() {
        return displayPercent;
    }

    public void setDisplayPercent(Double displayPercent) {
        this.displayPercent = displayPercent;
    }

    public int getDisplayDepth() {
        return displayDepth;
    }

    public void setDisplayDepth(int displayDepth) {
        this.displayDepth = displayDepth;
    }

    public Double getDisplayQtyA() {
        return displayQtyA;
    }

    public void setDisplayQtyA(Double displayQtyA) {
        this.displayQtyA = displayQtyA;
    }

    public Double getDisplayQtyB() {
        return displayQtyB;
    }

    public void setDisplayQtyB(Double displayQtyB) {
        this.displayQtyB = displayQtyB;
    }

    public int getIssueDay() {
        return issueDay;
    }

    public void setIssueDay(int issueDay) {
        this.issueDay = issueDay;
    }

//    public String getKey() {
//        return this.getShopID()+":"+this.getCategoryName()+":"+this.getMidCategoryName();
//    }

    public String getShopIdCategoryMidCategoryKey() {
        return this.getShopID()+"_"+this.getCategoryName()+"_"+this.getMidCategoryName();
    }

    public int getSafeDay() {
        return safeDay;
    }

    public void setSafeDay(int safeDay) {
        this.safeDay = safeDay;
    }
}
