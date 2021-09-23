package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

public class ProhibitedGoods extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	public static final String whiteListForbiddenDesc = "白名单禁配";

	//1:大类;2:中类;3:小类;4:商品
	private int type;
	private String AreaId;
	private String shopId;
	private String typeId;
	private String matCode;
	private String ruleName;
	private Integer minQty;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAreaId() {
		return AreaId;
	}
	public void setAreaId(String areaId) {
		AreaId = areaId;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getMatCode() {
		return matCode;
	}
	public void setMatCode(String matCode) {
		this.matCode = matCode;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Integer getMinQty() {
		return minQty;
	}

	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}
}