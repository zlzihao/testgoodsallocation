package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

/**
 * IssueInStock
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class IssueInStock extends ToString{

    private Integer taskId;

    private String categoryName;

    private String categoryCode;

    private String midCategoryName;

    private String midCategoryCode;

    private String smallCategoryName;

    private String shopID;

    private String matCode;

    private String sizeID;

    private String sizeName;

    private Double avgSaleAmt;

    private Double avgSaleQty;

    private Long stockQty = 0L;

    private Long pathStockQty = 0L;

    private Long moveQty = 0L;

    private Double quotePrice;

    private Long totalStockQty = 0L;

    private Long warehouseStockQty;

    private Long needStockQtyInt;

    private int isNew;

    private int IsProhibited =0;

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getMidCategoryCode() {
        return midCategoryCode;
    }

    public void setMidCategoryCode(String midCategoryCode) {
        this.midCategoryCode = midCategoryCode;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getSizeID() {
        return sizeID;
    }

    public void setSizeID(String sizeID) {
        this.sizeID = sizeID;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Long getStockQty() {
        return stockQty;
    }

    public void setStockQty(Long stockQty) {
        this.stockQty = stockQty;
    }

    public Long getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(Long pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public Long getMoveQty() {
        return moveQty;
    }

    public void setMoveQty(Long moveQty) {
        this.moveQty = moveQty;
    }

    public Long getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(Long totalStockQty) {
        this.totalStockQty = totalStockQty;
    }

    public Long getWarehouseStockQty() {
        return warehouseStockQty;
    }

    public void setWarehouseStockQty(Long warehouseStockQty) {
        this.warehouseStockQty = warehouseStockQty;
    }

    public Long getNeedStockQtyInt() {
        return needStockQtyInt;
    }

    public void setNeedStockQtyInt(Long needStockQtyInt) {
        this.needStockQtyInt = needStockQtyInt;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getIsProhibited() {
        return IsProhibited;
    }

    public void setIsProhibited(int isProhibited) {
        IsProhibited = isProhibited;
    }

    public Double getAvgSaleAmt() {
        return avgSaleAmt;
    }

    public void setAvgSaleAmt(Double avgSaleAmt) {
        this.avgSaleAmt = avgSaleAmt;
    }
}
