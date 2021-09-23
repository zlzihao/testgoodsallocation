package cn.nome.saas.search.feign;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

public class ProductTagReq extends ToString {
	
	private List<Integer> product_ids;

	public List<Integer> getProduct_ids() {
		return product_ids;
	}

	public void setProduct_ids(List<Integer> product_ids) {
		this.product_ids = product_ids;
	}

    
}
