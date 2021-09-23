package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

public class TaskProgressDO extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	public static final int START_STATUS = 0;
	public static final int CALC_REQUIRMENT_STATUS = 1;
	public static final int FINISH_STATUS = 2;

    private int taskId;

    private int progress;
    private int minute;
    private Date createdTime;
    private String message;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}