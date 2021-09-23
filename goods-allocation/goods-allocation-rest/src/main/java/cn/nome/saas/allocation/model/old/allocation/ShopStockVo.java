package cn.nome.saas.allocation.model.old.allocation;

import java.math.BigDecimal;

public class ShopStockVo {

    private BigDecimal ShopStockQtyYd;
    private BigDecimal ShopStockAmtYd;

    /**
     * 在店库存数量
     */
    private BigDecimal OnlyShopStockQtyYd;
    /**
     * 在店库存金额
     */
    private BigDecimal OnlyShopStockAmtYd;

    /**
     * 总金额
     */
    private BigDecimal totalAmt;
    /**
     * 在店总金额
     */
    private BigDecimal onlyShopStockTotalAmt;
    /**
     * 在店总数量
     */
    private BigDecimal onlyShopStockTotalQty;

    /**
     * 全店库存数量
     */
    private BigDecimal totalStockQty;

    public BigDecimal getShopStockQtyYd() {
        return ShopStockQtyYd;
    }

    public void setShopStockQtyYd(BigDecimal shopStockQtyYd) {
        ShopStockQtyYd = shopStockQtyYd;
    }

    public BigDecimal getShopStockAmtYd() {
        return ShopStockAmtYd;
    }

    public void setShopStockAmtYd(BigDecimal shopStockAmtYd) {
        ShopStockAmtYd = shopStockAmtYd;
    }

    public BigDecimal getOnlyShopStockQtyYd() {
        return OnlyShopStockQtyYd;
    }

    public void setOnlyShopStockQtyYd(BigDecimal onlyShopStockQtyYd) {
        OnlyShopStockQtyYd = onlyShopStockQtyYd;
    }

    public BigDecimal getOnlyShopStockAmtYd() {
        return OnlyShopStockAmtYd;
    }

    public void setOnlyShopStockAmtYd(BigDecimal onlyShopStockAmtYd) {
        OnlyShopStockAmtYd = onlyShopStockAmtYd;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public BigDecimal getOnlyShopStockTotalAmt() {
        return onlyShopStockTotalAmt;
    }

    public void setOnlyShopStockTotalAmt(BigDecimal onlyShopStockTotalAmt) {
        this.onlyShopStockTotalAmt = onlyShopStockTotalAmt;
    }

    public BigDecimal getOnlyShopStockTotalQty() {
        return onlyShopStockTotalQty;
    }

    public void setOnlyShopStockTotalQty(BigDecimal onlyShopStockTotalQty) {
        this.onlyShopStockTotalQty = onlyShopStockTotalQty;
    }

    public BigDecimal getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        this.totalStockQty = totalStockQty;
    }
}
