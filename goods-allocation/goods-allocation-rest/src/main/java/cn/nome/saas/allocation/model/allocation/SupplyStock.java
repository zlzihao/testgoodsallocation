package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * SupplyStock
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class SupplyStock extends ToString {

    private String inshopid;

    private String shopID;

    private String shopName;

    //private int needStockQtyInt; // 需求库存

    //private int usableStockQty; // 可调拨库存

    private double avgSaleQty;

    private double avgSaleAmt;

    private int sku; // sku数量

    private double contributeAmt; // 贡献金额

    public String getInshopid() {
        return inshopid;
    }

    public void setInshopid(String inshopid) {
        this.inshopid = inshopid;
    }

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

    /*public int getNeedStockQtyInt() {
        return needStockQtyInt;
    }

    public void setNeedStockQtyInt(int needStockQtyInt) {
        this.needStockQtyInt = needStockQtyInt;
    }

    public int getUsableStockQty() {
        return usableStockQty;
    }

    public void setUsableStockQty(int usableStockQty) {
        this.usableStockQty = usableStockQty;
    }*/

    public double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public double getAvgSaleAmt() {
        return avgSaleAmt;
    }

    public void setAvgSaleAmt(double avgSaleAmt) {
        this.avgSaleAmt = avgSaleAmt;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public double getContributeAmt() {
        return contributeAmt;
    }

    public void setContributeAmt(double contributeAmt) {
        this.contributeAmt = contributeAmt;
    }
}
