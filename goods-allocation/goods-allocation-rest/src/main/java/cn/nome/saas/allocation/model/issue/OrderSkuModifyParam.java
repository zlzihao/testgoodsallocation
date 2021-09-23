package cn.nome.saas.allocation.model.issue;

public class OrderSkuModifyParam {

    private int taskId;
    private String matCode;
    private String sizeId;
    private String shopId;
    private int count; //打包数
    private int totalQty;

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

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    @Override
    public String toString() {
        return "OrderSkuModifyParam{" +
                "taskId=" + taskId +
                ", matCode='" + matCode + '\'' +
                ", sizeId='" + sizeId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", count=" + count +
                ", totalQty=" + totalQty +
                '}';
    }
}
