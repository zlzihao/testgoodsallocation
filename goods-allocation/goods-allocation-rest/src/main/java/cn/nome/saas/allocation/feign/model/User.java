package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

public class User extends ToString{
	
	private static final long serialVersionUID = -419223484222315424L;
	

	private int errcode;
	private int role;
	private String errmsg;
	private String userid;
	private String position;
	private String mobile;
	private String email;
	private String avatar;
	private String name;
	private List<Integer> department;
	private List<String> departmentName;

	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Integer> getDepartment() {
		return department;
	}
	public void setDepartment(List<Integer> department) {
		this.department = department;
	}
	public List<String> getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(List<String> departmentName) {
		this.departmentName = departmentName;
	}
	
	
}
