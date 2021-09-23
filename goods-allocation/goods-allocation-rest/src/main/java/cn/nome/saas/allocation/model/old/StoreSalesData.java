package cn.nome.saas.allocation.model.old;

import cn.nome.platform.common.utils.ToString;

public class StoreSalesData extends ToString {

	private static final long serialVersionUID = 3636152291308741250L;

    private String shop_id;
    private int item_count;
    private float avg_sale_amt;
	public String getShop_id() {
		return shop_id;
	}
	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}
	public int getItem_count() {
		return item_count;
	}
	public void setItem_count(int item_count) {
		this.item_count = item_count;
	}
	public float getAvg_sale_amt() {
		return avg_sale_amt;
	}
	public void setAvg_sale_amt(float avg_sale_amt) {
		this.avg_sale_amt = avg_sale_amt;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}