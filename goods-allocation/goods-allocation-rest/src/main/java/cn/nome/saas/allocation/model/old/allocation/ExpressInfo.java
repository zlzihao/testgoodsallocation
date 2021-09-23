package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 物流信息
 *
 * @author Bruce01.fan
 * @date 2019/5/31
 */
public class ExpressInfo extends ToString {

    String shopIdA;

    String shopIdB;

    Double shippingFree;

    int shippingDays;

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

    public int getShippingDays() {
        return shippingDays;
    }

    public void setShippingDays(int shippingDays) {
        this.shippingDays = shippingDays;
    }
}
