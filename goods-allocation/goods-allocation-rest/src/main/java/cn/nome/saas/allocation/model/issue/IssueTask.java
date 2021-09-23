package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

public class IssueTask extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	private int id;
	private int taskStatus;
    private String name;
    private Date runTime;
    private Date createdAt;
    private Date updatedAt;
	private String remark;
	private String operator;
    
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
}