package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;

public class OrderDetailVo {

    private String img;
    private int taskId;
    private String matCode;
    private String matName;
    private String smallCategoryName;
    private String quotePrice;
    private String sizeId;
    private String inStockQty;
    private String outStockQty;
    private int minPackageQty;
    private BigDecimal avgSaleQty;
    private BigDecimal soldDay;
    private BigDecimal needQty;

    private BigDecimal packageQty;
    private BigDecimal orderPackage;

    private String midCategoryName;
    private String sizeName;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(String quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
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

    public BigDecimal getSoldDay() {
        return soldDay;
    }

    public void setSoldDay(BigDecimal soldDay) {
        this.soldDay = soldDay;
    }

    public BigDecimal getNeedQty() {
        return needQty;
    }

    public void setNeedQty(BigDecimal needQty) {
        this.needQty = needQty;
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

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
//        if (StringUtils.isBlank(sizeName)) {
//            this.sizeName = this.sizeId;
//        } else {
//            this.sizeName = sizeName;
//        }
        this.sizeName = sizeName;
    }
}
