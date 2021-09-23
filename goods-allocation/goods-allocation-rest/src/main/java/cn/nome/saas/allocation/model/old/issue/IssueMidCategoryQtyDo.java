package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class IssueMidCategoryQtyDo {

    private String ShopID;
    private String CategoryName;
    private String MidCategoryName;
    private BigDecimal AvgSaleQty;

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

    public BigDecimal getAvgSaleQty() {
        return AvgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        AvgSaleQty = avgSaleQty;
    }
}
