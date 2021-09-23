package cn.nome.saas.allocation.repository.old.allocation.entity;

import java.math.BigDecimal;

public class IssueUndoDO {

    private int TaskId;
    private String InShopID;
    private String MatName;
    private String MatCode;
    private String SizeID;
    private String SizeName;

    private BigDecimal Qty;
    private BigDecimal NeedQty;
    private BigDecimal PackageQty;
    private BigDecimal QuotePrice;
    private int OrderPackage;
    private int MinPackageQty;

    private String CategoryName;
    private String MidCategoryName;
    private String SmallCategoryName;

    private int IsIssue = 1;
    private int IsNew;
    private int IsProhibited;


    private BigDecimal AvgSaleQty = new BigDecimal(0);
    private BigDecimal StockQty = new BigDecimal(0);
    private BigDecimal TotalStockQty = new BigDecimal(0);

    private String SeasonName;
    private String YearNo;

    private BigDecimal PathStockQty = new BigDecimal(0);
    private BigDecimal MoveQty = new BigDecimal(0);

    private String RuleName;
    private BigDecimal MinQty;

    private int Status = 0;

    //sku日均销中类占比
    private BigDecimal PercentCategory = new BigDecimal(0);

    private int IsEliminate;

    public BigDecimal getPercentCategory() {
        return PercentCategory;
    }

    public void setPercentCategory(BigDecimal percentCategory) {
        PercentCategory = percentCategory;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public String getInShopID() {
        return InShopID;
    }

    public void setInShopID(String inShopID) {
        InShopID = inShopID;
    }

    public String getMatName() {
        return MatName;
    }

    public void setMatName(String matName) {
        MatName = matName;
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

    public String getSizeName() {
        return SizeName;
    }

    public void setSizeName(String sizeName) {
        SizeName = sizeName;
    }

    public BigDecimal getQty() {
        return Qty;
    }

    public void setQty(BigDecimal qty) {
        Qty = qty;
    }

    public BigDecimal getNeedQty() {
        return NeedQty;
    }

    public void setNeedQty(BigDecimal needQty) {
        NeedQty = needQty;
    }

    public BigDecimal getPackageQty() {
        return PackageQty;
    }

    public void setPackageQty(BigDecimal packageQty) {
        PackageQty = packageQty;
    }

    public BigDecimal getQuotePrice() {
        return QuotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        QuotePrice = quotePrice;
    }

    public int getOrderPackage() {
        return OrderPackage;
    }

    public void setOrderPackage(int orderPackage) {
        OrderPackage = orderPackage;
    }

    public int getMinPackageQty() {
        return MinPackageQty;
    }

    public void setMinPackageQty(int minPackageQty) {
        MinPackageQty = minPackageQty;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getMidCategoryName() {
        return MidCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        MidCategoryName = midCategoryName;
    }

    public String getSmallCategoryName() {
        return SmallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        SmallCategoryName = smallCategoryName;
    }

    public int getIsIssue() {
        return IsIssue;
    }

    public void setIsIssue(int isIssue) {
        IsIssue = isIssue;
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

    public BigDecimal getTotalStockQty() {
        return TotalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        TotalStockQty = totalStockQty;
    }

    public String getSeasonName() {
        return SeasonName;
    }

    public void setSeasonName(String seasonName) {
        SeasonName = seasonName;
    }

    public String getYearNo() {
        return YearNo;
    }

    public void setYearNo(String yearNo) {
        YearNo = yearNo;
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
