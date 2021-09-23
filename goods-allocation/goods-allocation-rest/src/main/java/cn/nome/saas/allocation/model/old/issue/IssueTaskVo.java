package cn.nome.saas.allocation.model.old.issue;

import java.util.Date;

public class IssueTaskVo {
    private Date runTime;
    private int taskId;
    //重算标记，0无，1 重算进行中
    private int reRun = 0;

    private Date updateTime;

    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getReRun() {
        return reRun;
    }

    public void setReRun(int reRun) {
        this.reRun = reRun;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
