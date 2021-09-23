package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class NewIssueDetailDo extends ToString {
    private Integer id;
    private String inShopId;
    private String categoryName;
    private String midCategoryName;
    private String smallCategoryName;
    private String matName;
    private String matCode;
    private String sizeId;
    private String sizeName;
    private BigDecimal quotePrice;
    private Integer minPackageQty;
    private BigDecimal qty;
    private BigDecimal needQty;
    private BigDecimal packageQty;
    private Integer orderPackage;
    private Integer status;
    private Integer isEnough;
    private String warehouseCode;
    private String createdAt;
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInShopId() {
        return inShopId;
    }

    public void setInShopId(String inShopId) {
        this.inShopId = inShopId;
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

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public BigDecimal getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        this.quotePrice = quotePrice;
    }

    public Integer getMinPackageQty() {
        return minPackageQty;
    }

    public void setMinPackageQty(Integer minPackageQty) {
        this.minPackageQty = minPackageQty;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public Long getQtyLongVal() {
        return qty.longValue();
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
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

    public Integer getOrderPackage() {
        return orderPackage;
    }

    public void setOrderPackage(Integer orderPackage) {
        this.orderPackage = orderPackage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsEnough() {
        return isEnough;
    }

    public void setIsEnough(Integer isEnough) {
        this.isEnough = isEnough;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getWarehouseMatCodeSizeNameKey() {
        return this.getWarehouseCode() + "_" + this.getMatCode() + "_" + this.getSizeName();
    }

    public String getShopIdMatCodeSizeNameKey() {
        return this.getInShopId() + "_" + this.getMatCode() + "_" + this.getSizeName();
    }

    public String getMatCodeSizeNameKey() {
        return this.getMatCode() + "_" + this.getSizeName();
    }

    @Override
    public String toString() {
        return "NewIssueDetailDo{" +
                "id=" + id +
                ", inShopId='" + inShopId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", midCategoryName='" + midCategoryName + '\'' +
                ", smallCategoryName='" + smallCategoryName + '\'' +
                ", matName='" + matName + '\'' +
                ", matCode='" + matCode + '\'' +
                ", sizeId='" + sizeId + '\'' +
                ", sizeName='" + sizeName + '\'' +
                ", quotePrice=" + quotePrice +
                ", minPackageQty=" + minPackageQty +
                ", qty=" + qty +
                ", needQty=" + needQty +
                ", packageQty=" + packageQty +
                ", orderPackage=" + orderPackage +
                ", status=" + status +
                ", isEnough=" + isEnough +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
