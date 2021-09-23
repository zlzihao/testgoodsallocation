package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class IssueRejectSupplyDO extends ToString {

    private String shopId;

    private String matCode;

    private String sizeId;

    /**
     * 可供给库存
     * 将撤店商品的supply_stock_qty（可供给库存）设置为当前的门店库存(stock_qty + path_stock_qty + apply_stock_qty)
     */
    private Integer supplyStockQty;

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

    public Integer getSupplyStockQty() {
        return supplyStockQty;
    }

    public void setSupplyStockQty(Integer supplyStockQty) {
        this.supplyStockQty = supplyStockQty;
    }
}
