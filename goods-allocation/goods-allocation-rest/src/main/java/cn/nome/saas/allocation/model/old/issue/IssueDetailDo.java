package cn.nome.saas.allocation.model.old.issue;

public class IssueDetailDo {

    private int TaskId;
    private String InShopID;
    private String MatCode;
    private String SizeID;
    private Double Qty;
    private Double PackageQty;
    private Double QuotePrice;
    private int OrderPackage;
    private int MinPackageQty;

    private int IsIssue = 0;

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

    public Double getQty() {
        return Qty;
    }

    public void setQty(Double qty) {
        Qty = qty;
    }

    public Double getPackageQty() {
        return PackageQty;
    }

    public void setPackageQty(Double packageQty) {
        PackageQty = packageQty;
    }

    public Double getQuotePrice() {
        return QuotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
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

    public int getIsIssue() {
        return IsIssue;
    }

    public void setIsIssue(int isIssue) {
        IsIssue = isIssue;
    }
}
