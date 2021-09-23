package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class Category extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private String CategoryName;
	private String CategoryCode;
	
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	public String getCategoryCode() {
		return CategoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		CategoryCode = categoryCode;
	}

	
}