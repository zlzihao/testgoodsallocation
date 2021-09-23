package cn.nome.saas.allocation.model.old.allocation;

import java.math.BigDecimal;

public class SkuSaleStock {

    private String ShopID;
    private String MatName;
    private String CategoryName;
    private String MidCategoryName;
    private BigDecimal SaleAmt;
    private BigDecimal SaleQty;

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public String getMatName() {
        return MatName;
    }

    public void setMatName(String matName) {
        MatName = matName;
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

    public BigDecimal getSaleAmt() {
        return SaleAmt;
    }

    public void setSaleAmt(BigDecimal saleAmt) {
        SaleAmt = saleAmt;
    }

    public BigDecimal getSaleQty() {
        return SaleQty;
    }

    public void setSaleQty(BigDecimal saleQty) {
        SaleQty = saleQty;
    }
}
