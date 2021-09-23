package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

public class Store extends ToString {
    
	private static final long serialVersionUID = -3171660219195919830L;

    private String shop_id;
    private String shop_code;
    private String shop_name;
    
	public String getShop_code() {
		return shop_code;
	}
	public void setShop_code(String shop_code) {
		this.shop_code = shop_code;
	}
	public String getShop_id() {
		return shop_id;
	}
	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

}    