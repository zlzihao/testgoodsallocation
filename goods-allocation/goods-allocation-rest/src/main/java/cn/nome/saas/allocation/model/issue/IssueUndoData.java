package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * IssueUndoData
 *
 * @author Bruce01.fan
 * @date 2019/7/22
 */
public class IssueUndoData extends ToString{

    private int taskId;

    private String inShopID;

    private String matCode;
    private String sizeID;
    private String sizeName;

    private String categoryName ;
    private String midCategoryName;
    private String smallCategoryName;
    private String matName;

    private BigDecimal quotePrice;
    private int isNew;
    private int isProhibited;

    private BigDecimal avgSaleQty;
    private BigDecimal stockQty;
    private BigDecimal totalStockQty;
    private BigDecimal minPackageQty;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getInShopID() {
        return inShopID;
    }

    public void setInShopID(String inShopID) {
        this.inShopID = inShopID;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
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

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public BigDecimal getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        this.quotePrice = quotePrice;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getIsProhibited() {
        return isProhibited;
    }

    public void setIsProhibited(int isProhibited) {
        this.isProhibited = isProhibited;
    }

    public BigDecimal getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public BigDecimal getStockQty() {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        this.stockQty = stockQty;
    }

    public BigDecimal getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        this.totalStockQty = totalStockQty;
    }

    public BigDecimal getMinPackageQty() {
        return minPackageQty;
    }

    public void setMinPackageQty(BigDecimal minPackageQty) {
        this.minPackageQty = minPackageQty;
    }
}
