package cn.nome.saas.cart.utils;

import cn.nome.saas.cart.model.CartSkuModel;
import cn.nome.saas.cart.model.ValidSkuModel;

import java.util.Comparator;
import java.util.List;

/**
 * @author chentaikuang
 */
public class SortValidSku implements Comparator<ValidSkuModel> {

	@Override
	public int compare(ValidSkuModel o1, ValidSkuModel o2) {

		/**
		 * 优先排序有优惠活动信息
		 */
		int result = o2.getId().compareTo(o1.getId());
		if(result != 0) {
			return result;
		}
		/**
		 * 取商品集条数最短的作为遍历的次数，避免indexOutException
		 */
		List<CartSkuModel> skuItems1 = o1.getSkuModels();
		List<CartSkuModel> skuItems2 = o2.getSkuModels();
		int size = skuItems1.size();
		if (size > skuItems2.size()) {
			size = skuItems2.size();
		}
		/**
		 * 逐个比较商品集中的各个sku添加时间
		 */
		long n = 0;
		for (int i = 0; i < size; i++) {
			n = skuItems2.get(i).getAddTime() - skuItems1.get(i).getAddTime();
			if (n != 0) {
				return n > 0 ? 1 : -1;
			}
		}
		return 0;

	}
}
