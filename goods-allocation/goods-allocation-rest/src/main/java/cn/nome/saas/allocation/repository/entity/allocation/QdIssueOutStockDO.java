package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * QdIssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueOutStockDO {

    private Integer id;

    private Integer taskId;

    private String categoryName;

    private String midCategoryName;

    private String matCode;

    private String sizeId;

    private String sizeName;

    private long stockQty;

    private long pathQty;

    private long applyQty;

    private long remainQty; // 配发剩余库存数

    private long issueQty; // 配发库存

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
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

    public long getStockQty() {
        return stockQty;
    }

    public void setStockQty(long stockQty) {
        this.stockQty = stockQty;
    }

    public long getPathQty() {
        return pathQty;
    }

    public void setPathQty(long pathQty) {
        this.pathQty = pathQty;
    }

    public long getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(long applyQty) {
        this.applyQty = applyQty;
    }

    public long getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(long remainQty) {
        this.remainQty = remainQty;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public long getIssueQty() {
        issueQty = stockQty - remainQty;

        return issueQty;
    }

    public void setIssueQty(long issueQty) {
        this.issueQty = issueQty;
    }
}
