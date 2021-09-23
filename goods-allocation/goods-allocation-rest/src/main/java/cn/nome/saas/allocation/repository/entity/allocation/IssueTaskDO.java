package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * IssueTaskDO
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public class IssueTaskDO {

    public static final Integer TASK_TYPE_RECALC_RESERVE = 0;
    public static final Integer TASK_TYPE_UNRECALC_RESERVE = 1;

    private int id;
    private int taskStatus;

    /**
     * 任务类型, 0-重跑预留, 1-不重跑预留
     */
    private int taskType;

    private String name;
    private Date runTime;
    private Date createdAt;
    private Date updatedAt;
    private String remark;
    private String operator;
    private int ready;

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getReady() {
        return ready;
    }

    public void setReady(int ready) {
        this.ready = ready;
    }
}
