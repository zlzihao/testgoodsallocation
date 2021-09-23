package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

public class Task extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

    private int task_id;
    private String task_name;
    private int task_status;
    //类型：服装1，百货2，全部3
    private int task_type;
    //调拨类型：平衡调拨1，撤店调拨2
    private int allocation_type;
    private String user_id;
    private String user_name;
    private Date create_time;
    private Date run_time;
    private int clothing_period;
    private int commodity_period;
    //撤店调拨需求天数
    private int days;
    private int area_type;
    private String out_shop_ids;
    private String in_shop_ids;
    
    
	public int getAllocation_type() {
		return allocation_type;
	}
	public void setAllocation_type(int allocation_type) {
		this.allocation_type = allocation_type;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getArea_type() {
		return area_type;
	}
	public void setArea_type(int area_type) {
		this.area_type = area_type;
	}
	public String getOut_shop_ids() {
		return out_shop_ids;
	}
	public void setOut_shop_ids(String out_shop_ids) {
		this.out_shop_ids = out_shop_ids;
	}
	public String getIn_shop_ids() {
		return in_shop_ids;
	}
	public void setIn_shop_ids(String in_shop_ids) {
		this.in_shop_ids = in_shop_ids;
	}
	public int getClothing_period() {
		return clothing_period;
	}
	public void setClothing_period(int clothing_period) {
		this.clothing_period = clothing_period;
	}
	public int getCommodity_period() {
		return commodity_period;
	}
	public void setCommodity_period(int commodity_period) {
		this.commodity_period = commodity_period;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public int getTask_status() {
		return task_status;
	}
	public void setTask_status(int task_status) {
		this.task_status = task_status;
	}
	public int getTask_type() {
		return task_type;
	}
	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getRun_time() {
		return run_time;
	}
	public void setRun_time(Date run_time) {
		this.run_time = run_time;
	}    
}    