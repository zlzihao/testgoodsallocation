package cn.nome.saas.cart.model;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class BaseModel extends ToString {

	private static final long serialVersionUID = 3636152291308741250L;
    
	private Integer corpId;
	private Integer appId;
	
	public Integer getCorpId() {
		return corpId;
	}
	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
	
	
}