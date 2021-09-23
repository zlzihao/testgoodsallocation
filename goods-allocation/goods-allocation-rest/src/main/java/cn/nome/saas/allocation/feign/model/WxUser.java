package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

public class WxUser extends ToString{
	
	private static final long serialVersionUID = -419223484222315424L;
	
	private int errcode;
	private String errmsg;
	private String userId;
	private String openId;
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	
	
	
}
