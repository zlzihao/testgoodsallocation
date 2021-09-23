package cn.nome.saas.allocation.model.old;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class GuanyuanToken extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String domainId;
	private String externalUserId;
	private long timestamp;
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	public String getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}