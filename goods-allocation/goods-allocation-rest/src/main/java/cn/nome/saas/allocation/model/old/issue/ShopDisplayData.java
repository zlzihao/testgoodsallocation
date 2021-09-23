package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

public class ShopDisplayData {

    private String ShopId;
    private String ShopCode;
    private String CategoryName;
    private String MidCategoryName;
    private BigDecimal DisplayPercent;
    private BigDecimal Display_Qty;

    private String Operator;

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    public String getShopCode() {
        return ShopCode;
    }

    public void setShopCode(String shopCode) {
        ShopCode = shopCode;
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

    public BigDecimal getDisplayPercent() {
        return DisplayPercent;
    }

    public void setDisplayPercent(BigDecimal displayPercent) {
        DisplayPercent = displayPercent;
    }

    public BigDecimal getDisplay_Qty() {
        return Display_Qty;
    }

    public void setDisplay_Qty(BigDecimal display_Qty) {
        Display_Qty = display_Qty;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }
}
