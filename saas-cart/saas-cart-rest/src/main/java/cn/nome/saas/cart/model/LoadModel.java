package cn.nome.saas.cart.model;

import com.alibaba.fastjson.JSONObject;

public class LoadModel extends BaseModel {
	private static final long serialVersionUID = -3272969809298483067L;
	private Integer uid;

	public LoadModel(Integer uid, Integer appId, Integer corpId) {
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
