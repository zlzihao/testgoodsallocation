package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class IssueDetailDistStock {

    private String MatCode;
    private String SizeID;

    /**
     * 分配库存
     */
    private BigDecimal TotalDistStockQty;

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public String getSizeID() {
        return SizeID;
    }

    public void setSizeID(String sizeID) {
        SizeID = sizeID;
    }

    public BigDecimal getTotalDistStockQty() {
        return TotalDistStockQty;
    }

    public void setTotalDistStockQty(BigDecimal totalDistStockQty) {
        TotalDistStockQty = totalDistStockQty;
    }
}
