package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

import java.util.List;


public class DepartmentList extends ToString{
	
	private static final long serialVersionUID = -419223484222315424L;
	
	private int errcode;
	private String errmsg;
	private List<Department> department;
	/**
	 * @return the errcode
	 */
	public int getErrcode() {
		return errcode;
	}
	/**
	 * @param errcode the errcode to set
	 */
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	/**
	 * @return the errmsg
	 */
	public String getErrmsg() {
		return errmsg;
	}
	/**
	 * @param errmsg the errmsg to set
	 */
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	/**
	 * @return the departmentList
	 */
	public List<Department> getDepartment() {
		return department;
	}
	/**
	 * @param departmentList the departmentList to set
	 */
	public void setDepartment(List<Department> department) {
		this.department = department;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	

}
