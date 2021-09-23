package cn.nome.saas.allocation.repository.old.vertica.entity;

import cn.nome.platform.common.utils.ToString;

/**
 * ShopSizeIdQtyInfoDO
 *
 * @author Bruce01.fan
 * @date 2019/6/4
 */
public class ShopSizeIdQtyInfoDO extends ToString {

    private String shopId;

    private String matCode;

    private String sizeId;

    private Integer qty; // sku剩余库存数

    private Integer minDisplayQty; // 最小陈列库存数

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getMinDisplayQty() {
        return minDisplayQty;
    }

    public void setMinDisplayQty(Integer minDisplayQty) {
        this.minDisplayQty = minDisplayQty;
    }
}
