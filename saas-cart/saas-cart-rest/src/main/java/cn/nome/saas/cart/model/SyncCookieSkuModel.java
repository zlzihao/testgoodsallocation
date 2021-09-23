package cn.nome.saas.cart.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 同步cookie商品
 * 
 * @author chentaikuang
 *
 */
public class SyncCookieSkuModel extends BaseModel {
	private static final long serialVersionUID = 410843439092521384L;
	private Integer uid;

	public SyncCookieSkuModel(Integer uid, Integer appId, Integer corpId) {
		this.setUid(uid);
		this.setAppId(appId);
		this.setCorpId(corpId);
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

}
