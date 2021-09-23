package cn.nome.saas.allocation.model.req;

import cn.nome.platform.common.utils.ToString;

/**
 * @author lizihao@nome.com
 */
public class ShopToStockStrReq extends ToString {
    private String shopCode;
    private String shopName;
    private String categoryName;
    private String midCategoryName;

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
}
