package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class Paramater extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private String key;
	private String value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}