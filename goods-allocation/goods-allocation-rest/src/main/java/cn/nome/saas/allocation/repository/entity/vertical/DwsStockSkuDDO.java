package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DimShop
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public class DwsStockSkuDDO extends ToString{

    private String companyNo;
    private String  shopID;
    private String  stockID;
    private String operationDate;
    private String  matCode;
    private String sizeID;
    private String  sizeName;
    private String color;
    private Integer  stockQty;
    private BigDecimal stockAmt;
    private BigDecimal hcStockAmt;
    private Integer  pathStockQty;
    private BigDecimal pathStockAmt;
    private BigDecimal  hcPathStockAmt;
    private String  hcCurr;
    private String  loadTime;
    private Integer canUseQty;
    private String validdays;
    private String colorName;

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public BigDecimal getStockAmt() {
        return stockAmt;
    }

    public void setStockAmt(BigDecimal stockAmt) {
        this.stockAmt = stockAmt;
    }

    public BigDecimal getHcStockAmt() {
        return hcStockAmt;
    }

    public void setHcStockAmt(BigDecimal hcStockAmt) {
        this.hcStockAmt = hcStockAmt;
    }

    public Integer getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(Integer pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public BigDecimal getPathStockAmt() {
        return pathStockAmt;
    }

    public void setPathStockAmt(BigDecimal pathStockAmt) {
        this.pathStockAmt = pathStockAmt;
    }

    public BigDecimal getHcPathStockAmt() {
        return hcPathStockAmt;
    }

    public void setHcPathStockAmt(BigDecimal hcPathStockAmt) {
        this.hcPathStockAmt = hcPathStockAmt;
    }

    public String getHcCurr() {
        return hcCurr;
    }

    public void setHcCurr(String hcCurr) {
        this.hcCurr = hcCurr;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public Integer getCanUseQty() {
        return canUseQty;
    }

    public void setCanUseQty(Integer canUseQty) {
        this.canUseQty = canUseQty;
    }

    public String getValiddays() {
        return validdays;
    }

    public void setValiddays(String validdays) {
        this.validdays = validdays;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
}
