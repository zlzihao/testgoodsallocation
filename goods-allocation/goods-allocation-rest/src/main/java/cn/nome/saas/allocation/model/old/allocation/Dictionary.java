package cn.nome.saas.allocation.model.old.allocation;
import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class Dictionary extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String type;
	private String paraKey;
	private String paraValue;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParaKey() {
		return paraKey;
	}
	public void setParaKey(String paraKey) {
		this.paraKey = paraKey;
	}
	public String getParaValue() {
		return paraValue;
	}
	public void setParaValue(String paraValue) {
		this.paraValue = paraValue;
	}
	
	

}