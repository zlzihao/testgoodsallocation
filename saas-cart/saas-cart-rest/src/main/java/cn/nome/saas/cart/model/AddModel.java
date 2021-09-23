package cn.nome.saas.cart.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chentaikuang
 */
@ApiModel(value = "购物车添加实体")
public class AddModel extends BaseModel {
	private static final long serialVersionUID = 107143151442307656L;

	@ApiModelProperty("用户ID")
	private int uid;

	@ApiModelProperty("添加SKU")
	private AddSkuModel sku;

	public int getUid() {
		return uid;
	}

	public AddModel(int uid,int appId, int corpId,AddSkuModel sku) {
		this.uid = uid;
		this.setAppId(appId);
		this.setCorpId(corpId);
		this.sku = sku;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public AddSkuModel getSku() {
		return sku;
	}

	public void setSku(AddSkuModel sku) {
		this.sku = sku;
	}
}
