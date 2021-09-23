package cn.nome.saas.sdc.bigData.model;

/**
 * @author lizihao@nome.com
 */
public class DwsDimShopStatusVO {
    private String shopCode;
    private String shopName;
    private String ShopState;

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

    public String getShopState() {
        return ShopState;
    }

    public void setShopState(String shopState) {
        ShopState = shopState;
    }
}
