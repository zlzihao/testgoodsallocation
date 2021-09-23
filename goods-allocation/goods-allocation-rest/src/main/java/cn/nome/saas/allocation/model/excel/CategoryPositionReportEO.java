package cn.nome.saas.allocation.model.excel;

import cn.nome.platform.common.utils.excel.annotation.Column;

import java.math.BigDecimal;

/**
 * @author lizihao@nome.com
 */
public class CategoryPositionReportEO {
    @Column(num = 0, value = "门店编码", width = 64)
    private String shopCode;
    @Column(num = 1, value = "门店名称", width = 64)
    private String shopName;
    @Column(num = 2, value = "大类名称", width = 64)
    private String categoryName;
    @Column(num = 3, value = "中类名称", width = 64)
    private String midCategoryName;
    @Column(num = 4, value = "最新仓位数", width = 64)
    private BigDecimal newStockNum;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public BigDecimal getNewStockNum() {
        return newStockNum;
    }

    public void setNewStockNum(BigDecimal newStockNum) {
        this.newStockNum = newStockNum;
    }
}
