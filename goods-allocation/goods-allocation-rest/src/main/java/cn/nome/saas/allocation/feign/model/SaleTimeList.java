package cn.nome.saas.allocation.feign.model;


import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * 
 **/
public class SaleTimeList extends ToString {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 **/
	private Integer id;

	private Integer displayPlanId;

	private String district;

	private Date saleTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDisplayPlanId() {
		return displayPlanId;
	}

	public void setDisplayPlanId(Integer displayPlanId) {
		this.displayPlanId = displayPlanId;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Date getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(Date saleTime) {
		this.saleTime = saleTime;
	}
}
