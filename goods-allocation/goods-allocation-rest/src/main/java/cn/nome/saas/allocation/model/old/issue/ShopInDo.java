package cn.nome.saas.allocation.model.old.issue;

public class ShopInDo {
    private String ShopName;
    private String ShopID;

    private Integer IssueDay;

    private Integer SafeDay;

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public Integer getIssueDay() {
        return IssueDay;
    }

    public void setIssueDay(Integer issueDay) {
        IssueDay = issueDay;
    }

    public Integer getSafeDay() {
        return SafeDay;
    }

    public void setSafeDay(Integer safeDay) {
        SafeDay = safeDay;
    }
}
