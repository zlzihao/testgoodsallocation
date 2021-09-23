package cn.nome.saas.allocation.model.excel;

import cn.nome.platform.common.utils.excel.annotation.Column;

/**
 * @author lizihao@nome.com
 */
public class OperateEO {
    @Column(value = "门店编码", num = 0, width = 64)
    private String shopCode;
    @Column(value = "门店名称", num = 1, width = 64)
    private String shopName;
    @Column(value = "陈列大类", num = 2, width = 64)
    private String CategoryName;
    @Column(value = "陈列种类", num = 3, width = 64)
    private String midCategoryName;
    @Column(value = "调整后仓位", num = 4, width = 64)
    private String newStockNum;

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
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getNewStockNum() {
        return newStockNum;
    }

    public void setNewStockNum(String newStockNum) {
        this.newStockNum = newStockNum;
    }
}
