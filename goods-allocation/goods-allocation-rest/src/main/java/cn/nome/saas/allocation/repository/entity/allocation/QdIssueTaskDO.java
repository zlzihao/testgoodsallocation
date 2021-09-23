package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * IssueTaskDO
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public class QdIssueTaskDO {

    private int id;
    private int taskStatus;
    private String name;
    private Date runTime;
    private Date createdAt;
    private Date updatedAt;
    private String remark;
    private String operator;
    private String tplCode;

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

    public String getTplCode() {
        return tplCode;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }
}
