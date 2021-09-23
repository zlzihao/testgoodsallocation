package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

public class TaskStore extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

    private String shop_id;
    private String shop_name;
    private int commodity_num;
    private int commodity_price;
    private int shopqty;
    
    
	public int getShopqty() {
		return shopqty;
	}
	public void setShopqty(int shopqty) {
		this.shopqty = shopqty;
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
	public int getCommodity_num() {
		return commodity_num;
	}
	public void setCommodity_num(int commodity_num) {
		this.commodity_num = commodity_num;
	}
	public int getCommodity_price() {
		return commodity_price;
	}
	public void setCommodity_price(int commodity_price) {
		this.commodity_price = commodity_price;
	} 
}    