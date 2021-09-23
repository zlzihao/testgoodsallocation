package cn.nome.saas.allocation.model.old.allocation;

import java.math.BigDecimal;

public class SaleQtyMatCodeSizeId {
    
    private String MatCode;
    private String SizeId;
    private BigDecimal SaleQty;

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public String getSizeId() {
        return SizeId;
    }

    public void setSizeId(String sizeId) {
        SizeId = sizeId;
    }

    public BigDecimal getSaleQty() {
        return SaleQty;
    }

    public void setSaleQty(BigDecimal saleQty) {
        SaleQty = saleQty;
    }
}
