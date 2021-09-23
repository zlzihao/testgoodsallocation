package cn.nome.saas.allocation.model.old.issue;

public class OrderSkuModifyReq {

    private int taskId;
    private String matCode;
    private String sizeId;
    private String shopId;
    private int count;
    private int isIssue;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIsIssue() {
        return isIssue;
    }

    public void setIsIssue(int isIssue) {
        this.isIssue = isIssue;
    }
}
