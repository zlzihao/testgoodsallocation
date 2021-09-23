package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺需求池DO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class NewIssueInStockDo {

    private Integer id;
    private String shopId;
    private String categoryCode;
    private String matCode;
    private BigDecimal quotePrice;
    private String sizeId;
    private String sizeName;
    private BigDecimal avgSaleAmt;
    private BigDecimal avgSaleQty;
    private BigDecimal stockQty;
    private BigDecimal pathStockQty;
    private BigDecimal moveQty;
    private BigDecimal totalStockQty;
    private Integer isNew = 0;
    private Integer isProhibited = 0;
    private String yearNo;
    private String seasonName;
    private String ruleName;
    private Integer securityQty;
//    private String createdAt;

    /**
     * 大中小类名
     */
    private String categoryName;
    private String midCategoryName;
    private String smallCategoryName;

    /**
     * 是否淘汰品
     */
    private int isEliminate;

    /**
     * 28天日均销
     */
    private BigDecimal saleQty28;
    /**
     * 7天日均销
     */
    private BigDecimal saleQty7;

    public BigDecimal getSaleQty28() {
        return saleQty28;
    }

    public void setSaleQty28(BigDecimal saleQty28) {
        this.saleQty28 = saleQty28;
    }

    public BigDecimal getSaleQty7() {
        return saleQty7;
    }

    public void setSaleQty7(BigDecimal saleQty7) {
        this.saleQty7 = saleQty7;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public BigDecimal getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        this.quotePrice = quotePrice;
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

    public BigDecimal getAvgSaleAmt() {
        return avgSaleAmt;
    }

    public void setAvgSaleAmt(BigDecimal avgSaleAmt) {
        this.avgSaleAmt = avgSaleAmt;
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

    public BigDecimal getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(BigDecimal pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public BigDecimal getMoveQty() {
        return moveQty;
    }

    public void setMoveQty(BigDecimal moveQty) {
        this.moveQty = moveQty;
    }

    public BigDecimal getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        this.totalStockQty = totalStockQty;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public Integer getIsProhibited() {
        return isProhibited;
    }

    public void setIsProhibited(Integer isProhibited) {
        this.isProhibited = isProhibited;
    }

    public String getYearNo() {
        return yearNo;
    }

    public void setYearNo(String yearNo) {
        this.yearNo = yearNo;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getSecurityQty() {
        return securityQty;
    }

    public void setSecurityQty(Integer securityQty) {
        this.securityQty = securityQty;
    }

//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }

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

    public int getIsEliminate() {
        return isEliminate;
    }

    public void setIsEliminate(int isEliminate) {
        this.isEliminate = isEliminate;
    }

//    public String getKey() {
//        return this.shopId + "_" + this.categoryName + "_" + this.midCategoryName;
//    }

    public String getShopIdMatCodeSizeNameKey() {
        return this.getShopId() + "_" + this.getMatCode() + "_" + this.getSizeName();
    }

    public String getMatCodeSizeNameKey() {
        return this.getMatCode() + "_" + this.getSizeName();
    }

    public String getShopIdBigMidSmallCategoryKey() {
        return this.getShopId() + "_" + this.getCategoryName() + "_" + this.getMidCategoryName() + "_" + this.getSmallCategoryName();
    }
    public String getShopIdBigMidCategoryKey() {
        return this.getShopId() + "_" + this.getCategoryName() + "_" + this.getMidCategoryName();
    }
    public String getShopIdBigCategoryKey() {
        return this.getShopId() + "_" + this.getCategoryName();
    }

    public String getShopIdMatCodeKey() {
        return this.getShopId() + "_" + this.getMatCode();
    }

    /**
     * toString, 用于保存文本, 批量插入sql
     * @return
     */
    public String getLoadString() {
        return (shopId == null ? "" : shopId) + "||" +
                (categoryCode == null ? "" : categoryCode) + "||" +
                (matCode == null ? "" : matCode) + "||" +
                (quotePrice == null ? 0 : quotePrice) + "||" +
                (sizeId == null ? "" : sizeId) + "||" +
                (sizeName == null ? "" : sizeName) + "||" +
                (avgSaleAmt == null ? 0 : avgSaleAmt) + "||" +
                (avgSaleQty == null ? 0 : avgSaleQty) + "||" +
                (stockQty == null ? 0 : stockQty) + "||" +
                (pathStockQty == null ? 0 : pathStockQty) + "||" +
                (moveQty == null ? 0 : moveQty) + "||" +
                (totalStockQty == null ? 0 : totalStockQty) + "||" +
                (isNew == null ? 0 : isNew) + "||" +
                (isProhibited == null ? 0 : isProhibited) + "||" +
                (yearNo == null ? "" : yearNo) + "||" +
                (seasonName == null ? "" : seasonName) + "||" +
                (ruleName == null ? "" : ruleName) + "||" +
                (securityQty == null ? 0 : securityQty) + "||" +
                (categoryName == null ? "" : categoryName) + "||" +
                (midCategoryName == null ? "" : midCategoryName) + "||" +
                (smallCategoryName == null ? "" : smallCategoryName) + "||" +
                (isEliminate) + "||" +
                (saleQty28 == null ? 0 : saleQty28) + "||" +
                (saleQty7 == null ? 0 : saleQty7);
    }

    @Override
    public String toString() {
        return "NewIssueInStockDo{" +
                "id=" + id +
                ", shopId='" + shopId + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", matCode='" + matCode + '\'' +
                ", quotePrice=" + quotePrice +
                ", sizeId='" + sizeId + '\'' +
                ", sizeName='" + sizeName + '\'' +
                ", avgSaleAmt=" + avgSaleAmt +
                ", avgSaleQty=" + avgSaleQty +
                ", stockQty=" + stockQty +
                ", pathStockQty=" + pathStockQty +
                ", moveQty=" + moveQty +
                ", totalStockQty=" + totalStockQty +
                ", isNew=" + isNew +
                ", isProhibited=" + isProhibited +
                ", yearNo='" + yearNo + '\'' +
                ", seasonName='" + seasonName + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", securityQty=" + securityQty +
                ", categoryName='" + categoryName + '\'' +
                ", midCategoryName='" + midCategoryName + '\'' +
                ", smallCategoryName='" + smallCategoryName + '\'' +
                ", isEliminate=" + isEliminate +
                ", saleQty28=" + saleQty28 +
                ", saleQty7=" + saleQty7 +
                '}';
    }
}
