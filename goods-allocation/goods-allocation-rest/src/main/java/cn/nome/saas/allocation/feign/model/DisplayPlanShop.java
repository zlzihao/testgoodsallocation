package cn.nome.saas.allocation.feign.model;


import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * 
 **/
public class DisplayPlanShop extends ToString {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer displayPlanId;

	private String shopCode;

	private Integer midPackageCount;

	private String shopName;

	private String district;

	private Integer displayStatus;

	private Integer fitsFirstDisplayCount = 0;

	private Integer saleDayCount;

	private Date saleTime;

	private String sizeScaleValue;


	public String getSizeScaleValue() {
		return sizeScaleValue;
	}

	public void setSizeScaleValue(String sizeScaleValue) {
		this.sizeScaleValue = sizeScaleValue;
	}

	public Date getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(Date saleTime) {
		this.saleTime = saleTime;
	}

	public Integer getFitsFirstDisplayCount() {
		return fitsFirstDisplayCount;
	}

	public void setFitsFirstDisplayCount(Integer fitsFirstDisplayCount) {
		this.fitsFirstDisplayCount = fitsFirstDisplayCount;
	}

	public Integer getSaleDayCount() {
		return saleDayCount;
	}

	public void setSaleDayCount(Integer saleDayCount) {
		this.saleDayCount = saleDayCount;
	}

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Integer getDisplayStatus() {
		return displayStatus;
	}

	public void setDisplayStatus(Integer displayStatus) {
		this.displayStatus = displayStatus;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

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

	public Integer getMidPackageCount() {
		return midPackageCount;
	}

	public void setMidPackageCount(Integer midPackageCount) {
		this.midPackageCount = midPackageCount;
	}
}
