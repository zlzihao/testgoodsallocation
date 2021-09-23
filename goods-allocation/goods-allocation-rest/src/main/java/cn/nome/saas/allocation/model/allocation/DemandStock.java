package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class DemandStock extends ToString {

    private String shopID;

    private String shopName;

    private double totalAmt;

    private int sku;

    private int outShopidQty;

    private int needStockQtyInt;

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(double totalAmt) {
        this.totalAmt = totalAmt;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public int getOutShopidQty() {
        return outShopidQty;
    }

    public void setOutShopidQty(int outShopidQty) {
        this.outShopidQty = outShopidQty;
    }

    public int getNeedStockQtyInt() {
        return needStockQtyInt;
    }

    public void setNeedStockQtyInt(int needStockQtyInt) {
        this.needStockQtyInt = needStockQtyInt;
    }
}
