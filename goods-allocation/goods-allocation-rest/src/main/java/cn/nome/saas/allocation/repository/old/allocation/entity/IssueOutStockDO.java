package cn.nome.saas.allocation.repository.old.allocation.entity;

public class IssueOutStockDO {

    private String MatCode;

    private String SizeID;

    private Long StockQty = 0L;

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

    public Long getStockQty() {
        return StockQty;
    }

    public void setStockQty(Long stockQty) {
        StockQty = stockQty;
    }
}
