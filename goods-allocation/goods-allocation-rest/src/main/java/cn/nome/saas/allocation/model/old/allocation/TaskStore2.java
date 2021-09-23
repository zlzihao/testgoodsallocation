package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

public class TaskStore2 extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

    private String inshop_id;
    private String inshop_name;
    private String outshop_id;
    private String outshop_name;
    private int commodity_num;
    private int commodity_price;
	public String getInshop_id() {
		return inshop_id;
	}
	public void setInshop_id(String inshop_id) {
		this.inshop_id = inshop_id;
	}
	public String getInshop_name() {
		return inshop_name;
	}
	public void setInshop_name(String inshop_name) {
		this.inshop_name = inshop_name;
	}
	public String getOutshop_id() {
		return outshop_id;
	}
	public void setOutshop_id(String outshop_id) {
		this.outshop_id = outshop_id;
	}
	public String getOutshop_name() {
		return outshop_name;
	}
	public void setOutshop_name(String outshop_name) {
		this.outshop_name = outshop_name;
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