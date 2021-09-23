package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

public class Token extends ToString{
	
	private static final long serialVersionUID = -419223484222315424L;
	
	private int errcode;
	private String errmsg;
	private String access_token;
	private int expires_in;
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
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	
	
}
