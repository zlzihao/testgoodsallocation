package cn.nome.saas.cart.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chentaikuang
 */
@ApiModel("购物车数量")
public class CountModel extends BaseModel{

	private static final long serialVersionUID = -5464120987933705241L;
	@ApiModelProperty("用户ID")
	private int uid;
	
	public CountModel(Integer uid, Integer appId, Integer corpId) {
		this.setUid(uid);
		this.setAppId(appId);
		this.setCorpId(corpId);
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	
}
