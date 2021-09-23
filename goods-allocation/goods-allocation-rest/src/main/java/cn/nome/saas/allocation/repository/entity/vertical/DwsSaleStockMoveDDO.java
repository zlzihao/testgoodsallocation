package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * DimShop
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public class DwsSaleStockMoveDDO extends ToString{

    private String billDate;
    private String  shopID;
    private String  shopCode;
    private String matCode;
    private String  sizeID;
    private String  sizeName;
    private Integer normalQty;
    private Integer  otherQty;
    private BigDecimal normalAmt;
    private BigDecimal otherAmt;
    private Integer  totalQty;
    private BigDecimal totalAmt;
    private Integer  stockQty;
    private Integer  pathstockQty;
    private Integer  moveQty;

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
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

    public Integer getNormalQty() {
        return normalQty;
    }

    public void setNormalQty(Integer normalQty) {
        this.normalQty = normalQty;
    }

    public Integer getOtherQty() {
        return otherQty;
    }

    public void setOtherQty(Integer otherQty) {
        this.otherQty = otherQty;
    }

    public BigDecimal getNormalAmt() {
        return normalAmt;
    }

    public void setNormalAmt(BigDecimal normalAmt) {
        this.normalAmt = normalAmt;
    }

    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Integer getPathstockQty() {
        return pathstockQty;
    }

    public void setPathstockQty(Integer pathstockQty) {
        this.pathstockQty = pathstockQty;
    }

    public Integer getMoveQty() {
        return moveQty;
    }

    public void setMoveQty(Integer moveQty) {
        this.moveQty = moveQty;
    }
}
