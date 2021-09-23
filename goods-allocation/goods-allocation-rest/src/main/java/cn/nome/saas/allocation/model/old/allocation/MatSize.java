package cn.nome.saas.allocation.model.old.allocation;
import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class MatSize extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;
	
	private String sizeId;
	private String sizeName;
	
	public String getSizeId() {
		return sizeId;
	}
	public void setSizeId(String sizeId) {
		this.sizeId = sizeId;
	}
	public String getSizeName() {
		return sizeName;
	}
	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}


	
}