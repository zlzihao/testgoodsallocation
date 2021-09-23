package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;

public class OrderDetailDo {

    private int taskId;
    private String shopId;
    private String matCode;
    private String matName;
    private String sizeId;
    private String quotePrice;

    private String inStockQty;
    private String outStockQty = "0";
    private int minPackageQty;

    private BigDecimal avgSaleQty = new BigDecimal(0);

    private BigDecimal packageQty = new BigDecimal(0);
    private BigDecimal orderPackage = new BigDecimal(0);

    private String sizeName;

    private String categoryName;
    private String midCategoryName;
    private String smallCategoryName;

    //是否配发商品
    private int IsIssue = 0;
    private int IsNew;
    private int IsProhibited;

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

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(String quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(String inStockQty) {
        this.inStockQty = inStockQty;
    }

    public String getOutStockQty() {
        return outStockQty;
    }

    public void setOutStockQty(String outStockQty) {
        this.outStockQty = outStockQty;
    }

    public int getMinPackageQty() {
        return minPackageQty;
    }

    public void setMinPackageQty(int minPackageQty) {
        this.minPackageQty = minPackageQty;
    }

    public BigDecimal getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public BigDecimal getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(BigDecimal packageQty) {
        this.packageQty = packageQty;
    }

    public BigDecimal getOrderPackage() {
        return orderPackage;
    }

    public void setOrderPackage(BigDecimal orderPackage) {
        this.orderPackage = orderPackage;
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

    public int getIsIssue() {
        return IsIssue;
    }

    public void setIsIssue(int isIssue) {
        IsIssue = isIssue;
    }

    public int getIsNew() {
        return IsNew;
    }

    public void setIsNew(int isNew) {
        IsNew = isNew;
    }

    public int getIsProhibited() {
        return IsProhibited;
    }

    public void setIsProhibited(int isProhibited) {
        IsProhibited = isProhibited;
    }
}
