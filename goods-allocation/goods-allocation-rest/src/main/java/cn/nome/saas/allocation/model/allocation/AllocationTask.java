package cn.nome.saas.allocation.model.allocation;

import java.util.Date;

/**
 * AllocationTaskVo
 *
 * @author Bruce01.fan
 * @date 2019/8/28
 */
public class AllocationTask {

    private int taskId;

    private String taskName;

    private int allocationType;

    private String creator;

    private Integer taskStatus; // 1-未执行 2-运行中 3-已完成

    private int inDays; // 调入天数

    private int outDays; // 调出天数

    private Integer taskProcess;

    private String goodsScope;

    private Date runTime;

    private Date createTime;

    private String failMsg;

    private String remark;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(int allocationType) {
        this.allocationType = allocationType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getInDays() {
        return inDays;
    }

    public void setInDays(int inDays) {
        this.inDays = inDays;
    }

    public int getOutDays() {
        return outDays;
    }

    public void setOutDays(int outDays) {
        this.outDays = outDays;
    }

    public Integer getTaskProcess() {
        return taskProcess;
    }

    public void setTaskProcess(Integer taskProcess) {
        this.taskProcess = taskProcess;
    }

    public String getGoodsScope() {
        return goodsScope;
    }

    public void setGoodsScope(String goodsScope) {
        this.goodsScope = goodsScope;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
