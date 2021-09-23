package cn.nome.saas.allocation.model.old.issue;

import java.util.Date;

public class TaskDataWrap {

    private int taskId;
    private Date endTime;
    private Date startTime;
    private int costTime;

    private int inStockCount;
    private int outStockCount;
    private int needStockCount;
    private int midCategoryCount;
    private int detailCount;
    private int undoCount;
    private int goodsDataCount;

    private int categoryDataCount;
    private int midCategoryDataCount;

    public int getCategoryDataCount() {
        return categoryDataCount;
    }

    public void setCategoryDataCount(int categoryDataCount) {
        this.categoryDataCount = categoryDataCount;
    }

    public int getMidCategoryDataCount() {
        return midCategoryDataCount;
    }

    public void setMidCategoryDataCount(int midCategoryDataCount) {
        this.midCategoryDataCount = midCategoryDataCount;
    }

    public int getGoodsDataCount() {
        return goodsDataCount;
    }

    public void setGoodsDataCount(int goodsDataCount) {
        this.goodsDataCount = goodsDataCount;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getInStockCount() {
        return inStockCount;
    }

    public void setInStockCount(int inStockCount) {
        this.inStockCount = inStockCount;
    }

    public int getOutStockCount() {
        return outStockCount;
    }

    public void setOutStockCount(int outStockCount) {
        this.outStockCount = outStockCount;
    }

    public int getNeedStockCount() {
        return needStockCount;
    }

    public void setNeedStockCount(int needStockCount) {
        this.needStockCount = needStockCount;
    }

    public int getMidCategoryCount() {
        return midCategoryCount;
    }

    public void setMidCategoryCount(int midCategoryCount) {
        this.midCategoryCount = midCategoryCount;
    }

    public int getDetailCount() {
        return detailCount;
    }

    public void setDetailCount(int detailCount) {
        this.detailCount = detailCount;
    }

    public int getUndoCount() {
        return undoCount;
    }

    public void setUndoCount(int undoCount) {
        this.undoCount = undoCount;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }
}
