package cn.nome.saas.allocation.model.old.allocation;

import java.math.BigDecimal;

public class ShopSaleVo {

    /**
     * 销售金额
     */
    private BigDecimal SaleAmt;

    /**
     * 销售数量
     */
    private BigDecimal SaleQty;

    /**
     * 销售总金额
     */
    private BigDecimal totalSaleAmt;
    /**
     * 销售总数量
     */
    private BigDecimal totalSaleQty;

    public BigDecimal getSaleAmt() {
        return SaleAmt;
    }

    public void setSaleAmt(BigDecimal saleAmt) {
        SaleAmt = saleAmt;
    }

    public BigDecimal getSaleQty() {
        return SaleQty;
    }

    public void setSaleQty(BigDecimal saleQty) {
        SaleQty = saleQty;
    }

    public BigDecimal getTotalSaleAmt() {
        return totalSaleAmt;
    }

    public void setTotalSaleAmt(BigDecimal totalSaleAmt) {
        this.totalSaleAmt = totalSaleAmt;
    }

    public BigDecimal getTotalSaleQty() {
        return totalSaleQty;
    }

    public void setTotalSaleQty(BigDecimal totalSaleQty) {
        this.totalSaleQty = totalSaleQty;
    }
}
