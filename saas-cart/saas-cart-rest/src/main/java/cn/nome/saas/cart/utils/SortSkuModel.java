package cn.nome.saas.cart.utils;

import cn.nome.saas.cart.feign.SkuModel;

import java.util.Comparator;

/**
 * 1、根据sku状态升序
 * 2、根据product状态降序
 * 3、主键id降序
 *
 * @author chentaikuang
 */
public class SortSkuModel implements Comparator<SkuModel> {

	@Override
	public int compare(SkuModel o1, SkuModel o2) {

		if (o1.getSkuStatus() != o2.getSkuStatus()) {
			return o1.getSkuStatus() - o2.getSkuStatus();
		}
		if (o1.getProductStatus() != o2.getProductStatus()) {
			return o2.getProductStatus() - o1.getProductStatus();
		}
		return o2.getSkuId() - o1.getSkuId();

	}

//    public static void main(String[] args) {
//        SkuModel s1 = new SkuModel();
//        s1.setName("s1");
//        s1.setSkuStatus(1);
//        s1.setProductStatus(0);
//        s1.setSkuId(123);
//        SkuModel s2 = new SkuModel();
//        s2.setName("s2");
//        s2.setSkuStatus(0);
//        s1.setProductStatus(1);
//        s2.setSkuId(1);
//
//        List list = new ArrayList<>();
//        list.add(s1);
//        list.add(s2);
//        Collections.sort(list, new SortSkuModel());
//
//        System.err.println(list);
//
//    }

}
