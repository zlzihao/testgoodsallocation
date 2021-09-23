package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * ShopExpress
 *
 * @author Bruce01.fan
 * @date 2019/5/31
 */
public class ShopExpressDO extends ToString{

    private String shopIdA;

    private String shopIdB;

    private Double shippingFree;

    private Double addShippingFree;

    private int shippingDays;

    public String getShopIdA() {
        return shopIdA;
    }

    public void setShopIdA(String shopIdA) {
        this.shopIdA = shopIdA;
    }

    public String getShopIdB() {
        return shopIdB;
    }

    public void setShopIdB(String shopIdB) {
        this.shopIdB = shopIdB;
    }

    public Double getShippingFree() {
        return shippingFree;
    }

    public void setShippingFree(Double shippingFree) {
        this.shippingFree = shippingFree;
    }

    public Double getAddShippingFree() {
        return addShippingFree;
    }

    public void setAddShippingFree(Double addShippingFree) {
        this.addShippingFree = addShippingFree;
    }

    public int getShippingDays() {
        return shippingDays;
    }

    public void setShippingDays(int shippingDays) {
        this.shippingDays = shippingDays;
    }

    public static ShopExpressDO getDefault() {
        ShopExpressDO shopExpressDO = new ShopExpressDO();

        shopExpressDO.setShippingFree(13D);
        shopExpressDO.setShippingDays(3);
        shopExpressDO.setAddShippingFree(14D);

        return shopExpressDO;
    }
}
