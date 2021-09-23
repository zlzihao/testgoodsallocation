package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

public class CompletePlan extends ToString{

	private String matCode;
	private Integer shopCount;

	public String getMatCode() {
		return matCode;
	}

	public void setMatCode(String matCode) {
		this.matCode = matCode;
	}

	public Integer getShopCount() {
		return shopCount;
	}

	public void setShopCount(Integer shopCount) {
		this.shopCount = shopCount;
	}
}

