package cn.nome.saas.allocation.model.old.issue;

/**
 * @author chentaikuang
 */
public class IssueCategorySkcData {

    private String ShopID;
    private int TaskId;

    private String CategoryName;
    private String MidCategoryName;

    private int NewSkcCount;//新品SKC数量
    private int ProhibitedSkcCount;//禁配SKC数
    private int ValidSkcCount;//有效SKC数量
    private int KeepSkcCount;//保底SKC数量
    private int CanSkcCount;//可下SKC数量

    private int Status = 0;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

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

    public int getNewSkcCount() {
        return NewSkcCount;
    }

    public void setNewSkcCount(int newSkcCount) {
        NewSkcCount = newSkcCount;
    }

    public int getProhibitedSkcCount() {
        return ProhibitedSkcCount;
    }

    public void setProhibitedSkcCount(int prohibitedSkcCount) {
        ProhibitedSkcCount = prohibitedSkcCount;
    }

    public int getValidSkcCount() {
        return ValidSkcCount;
    }

    public void setValidSkcCount(int validSkcCount) {
        ValidSkcCount = validSkcCount;
    }

    public int getKeepSkcCount() {
        return KeepSkcCount;
    }

    public void setKeepSkcCount(int keepSkcCount) {
        KeepSkcCount = keepSkcCount;
    }

    public int getCanSkcCount() {
        return CanSkcCount;
    }

    public void setCanSkcCount(int canSkcCount) {
        CanSkcCount = canSkcCount;
    }
}
