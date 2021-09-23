package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class DemandStockDetail extends ToString {

    private int taskId;

    private String shopID;

    private String shopName;

    private String matCode;

    private String matName;

    private String sizeID;

    private String sizeName;

    private String categoryName;

    private String categoryCode;

    private String midCategoryName;

    private String midCategoryCode;

    private String smallCategoryName;

    private Integer skcQty;

    private Integer stockQty;

    private Integer pathStockQty;

    private Integer moveQty;

    private Integer warehouseStockQty;

    private Integer needStockQtyInt;

    private Double avgSaleQty;

    private Double avgSaleAmt;

    private Double quotePrice;

    private Integer avgSaleAmtRank;

    private Double  amt;


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
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

    public Integer getSkcQty() {
        return skcQty;
    }

    public void setSkcQty(Integer skcQty) {
        this.skcQty = skcQty;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Integer getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(Integer pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public Integer getMoveQty() {
        return moveQty;
    }

    public void setMoveQty(Integer moveQty) {
        this.moveQty = moveQty;
    }

    public Integer getWarehouseStockQty() {
        return warehouseStockQty;
    }

    public void setWarehouseStockQty(Integer warehouseStockQty) {
        this.warehouseStockQty = warehouseStockQty;
    }

    public Integer getNeedStockQtyInt() {
        return needStockQtyInt;
    }

    public void setNeedStockQtyInt(Integer needStockQtyInt) {
        this.needStockQtyInt = needStockQtyInt;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public Double getAvgSaleAmt() {
        return avgSaleAmt;
    }

    public void setAvgSaleAmt(Double avgSaleAmt) {
        this.avgSaleAmt = avgSaleAmt;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public Integer getAvgSaleAmtRank() {
        return avgSaleAmtRank;
    }

    public void setAvgSaleAmtRank(Integer avgSaleAmtRank) {
        this.avgSaleAmtRank = avgSaleAmtRank;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}
