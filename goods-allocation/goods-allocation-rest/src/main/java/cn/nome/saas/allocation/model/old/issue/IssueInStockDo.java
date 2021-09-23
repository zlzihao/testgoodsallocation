package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

public class IssueInStockDo {

    private String ShopID;
    private int TaskId;
    private String MatCode;
    private String SizeID;
    private BigDecimal QuotePrice;
    private String SizeName;
    private BigDecimal AvgSaleAmt;
    private BigDecimal AvgSaleQty;
    private BigDecimal StockQty = new BigDecimal(0);
    private BigDecimal PathStockQty = new BigDecimal(0);
    private BigDecimal MoveQty = new BigDecimal(0);
    private BigDecimal TotalStockQty = new BigDecimal(0);
    private BigDecimal WarehouseStockQty;
    private int NeedStockQtyInt;
    private int IsNew;
    private int IsProhibited;

    private String YearNo;
    private String SeasonName;
    private String RuleName;
    private BigDecimal MinQty;
    private int IsEliminate;

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

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

    public BigDecimal getQuotePrice() {
        return QuotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        QuotePrice = quotePrice;
    }

    public String getSizeName() {
        return SizeName;
    }

    public void setSizeName(String sizeName) {
        SizeName = sizeName;
    }

    public BigDecimal getAvgSaleAmt() {
        return AvgSaleAmt;
    }

    public void setAvgSaleAmt(BigDecimal avgSaleAmt) {
        AvgSaleAmt = avgSaleAmt;
    }

    public BigDecimal getAvgSaleQty() {
        return AvgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        AvgSaleQty = avgSaleQty;
    }

    public BigDecimal getStockQty() {
        return StockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        StockQty = stockQty;
    }

    public BigDecimal getPathStockQty() {
        return PathStockQty;
    }

    public void setPathStockQty(BigDecimal pathStockQty) {
        PathStockQty = pathStockQty;
    }

    public BigDecimal getMoveQty() {
        return MoveQty;
    }

    public void setMoveQty(BigDecimal moveQty) {
        MoveQty = moveQty;
    }

    public BigDecimal getTotalStockQty() {
        return TotalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        TotalStockQty = totalStockQty;
    }

    public BigDecimal getWarehouseStockQty() {
        return WarehouseStockQty;
    }

    public void setWarehouseStockQty(BigDecimal warehouseStockQty) {
        WarehouseStockQty = warehouseStockQty;
    }

    public int getNeedStockQtyInt() {
        return NeedStockQtyInt;
    }

    public void setNeedStockQtyInt(int needStockQtyInt) {
        NeedStockQtyInt = needStockQtyInt;
    }

    public int getIsNew() {
        return IsNew;
    }

    public void setIsNew(int isNew) {
        IsNew = isNew;
    }

    public int getIsProhibited() {
        return IsProhibited;
    }

    public void setIsProhibited(int isProhibited) {
        IsProhibited = isProhibited;
    }

    public String getYearNo() {
        return YearNo;
    }

    public void setYearNo(String yearNo) {
        YearNo = yearNo;
    }

    public String getSeasonName() {
        return SeasonName;
    }

    public void setSeasonName(String seasonName) {
        SeasonName = seasonName;
    }

    public String getRuleName() {
        return RuleName;
    }

    public void setRuleName(String ruleName) {
        RuleName = ruleName;
    }

    public BigDecimal getMinQty() {
        return MinQty;
    }

    public void setMinQty(BigDecimal minQty) {
        MinQty = minQty;
    }

    public int getIsEliminate() {
        return IsEliminate;
    }

    public void setIsEliminate(int isEliminate) {
        IsEliminate = isEliminate;
    }
}
