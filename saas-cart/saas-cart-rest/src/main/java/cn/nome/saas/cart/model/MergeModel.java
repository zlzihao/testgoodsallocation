package cn.nome.saas.cart.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 刷新购物车
 * 
 * @author chentaikuang
 *
 */
public class MergeModel extends BaseModel {
	private static final long serialVersionUID = 410843439092521384L;
	private Integer uid;

	public MergeModel(Integer uid, Integer appId, Integer corpId) {
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
