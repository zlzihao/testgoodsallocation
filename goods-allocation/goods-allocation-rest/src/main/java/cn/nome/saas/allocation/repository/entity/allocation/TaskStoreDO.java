package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * TaskStoreDO
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class TaskStoreDO extends ToString {

    private String shopId;

    private String shopName;

    private int commodityNum;

    private int commodityPrice;

    private int shopQty;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(int commodityNum) {
        this.commodityNum = commodityNum;
    }

    public int getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(int commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public int getShopQty() {
        return shopQty;
    }

    public void setShopQty(int shopQty) {
        this.shopQty = shopQty;
    }
}
