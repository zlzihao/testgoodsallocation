package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;


/**
 * IssueInStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public class IssueInStockDO extends ToString{

    private String categoryCode;

    private String shopID;

    private String matCode;

    private String sizeID;

    private String sizeName;

    private Double avgSaleQty;

    private Long stockQty;

    private Long pathStockQty = 0L;

    private Long moveQty = 0L;

    private Double quotePrice;

    private String MatTypeName;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categorycode) {
        this.categoryCode = categorycode;
    }

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

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getMatTypeName() {
        return MatTypeName;
    }

    public void setMatTypeName(String matTypeName) {
        MatTypeName = matTypeName;
    }
}
