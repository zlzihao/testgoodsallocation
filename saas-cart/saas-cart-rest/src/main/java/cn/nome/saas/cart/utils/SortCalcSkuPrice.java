package cn.nome.saas.cart.utils;

import java.util.Comparator;

import cn.nome.saas.cart.feign.SkuModel;

/**
 * 计算sku价格排序
 */
public class SortCalcSkuPrice implements Comparator<SkuModel> {

	@Override
	public int compare(SkuModel o1, SkuModel o2) {
		return o1.getPrice()-o2.getPrice();
	}
}
