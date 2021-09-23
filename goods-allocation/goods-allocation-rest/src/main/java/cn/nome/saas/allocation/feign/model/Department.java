package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

public class Department extends ToString{
	
	private static final long serialVersionUID = -419223484222315424L;
	
	private int id;
	private String name;
	private int parentid;
	private int order;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the parentid
	 */
	public int getParentid() {
		return parentid;
	}
	/**
	 * @param parentid the parentid to set
	 */
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	

}

