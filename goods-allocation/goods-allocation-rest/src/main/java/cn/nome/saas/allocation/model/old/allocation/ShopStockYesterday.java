package cn.nome.saas.allocation.model.old.allocation;

import java.math.BigDecimal;

public class ShopStockYesterday {

    private String ShopID;
    private String CategoryName;
    private String MidCategoryName;
    private BigDecimal ShopStockQtyYd;
    private BigDecimal ShopStockAmtYd;
    /**
     * 在店库存
     */
    private BigDecimal OnlyShopStockQtyYd;
    /**
     * 在店库存
     */
    private BigDecimal OnlyShopStockAmtYd;

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getMidCategoryName() {
        return MidCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        MidCategoryName = midCategoryName;
    }

    public BigDecimal getShopStockQtyYd() {
        return ShopStockQtyYd;
    }

    public void setShopStockQtyYd(BigDecimal shopStockQtyYd) {
        ShopStockQtyYd = shopStockQtyYd;
    }

    public BigDecimal getShopStockAmtYd() {
        return ShopStockAmtYd;
    }

    public void setShopStockAmtYd(BigDecimal shopStockAmtYd) {
        ShopStockAmtYd = shopStockAmtYd;
    }

    public BigDecimal getOnlyShopStockQtyYd() {
        return OnlyShopStockQtyYd;
    }

    public void setOnlyShopStockQtyYd(BigDecimal onlyShopStockQtyYd) {
        OnlyShopStockQtyYd = onlyShopStockQtyYd;
    }

    public BigDecimal getOnlyShopStockAmtYd() {
        return OnlyShopStockAmtYd;
    }

    public void setOnlyShopStockAmtYd(BigDecimal onlyShopStockAmtYd) {
        OnlyShopStockAmtYd = onlyShopStockAmtYd;
    }
}
