package cn.nome.saas.cart.model.test;

import cn.nome.saas.cart.model.CartSkuModel;
import cn.nome.saas.cart.model.CartWrap;
import cn.nome.saas.cart.model.ValidSkuModel;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * @author chentaikuang
 */
public class TestMain {

	private static Logger logger = LoggerFactory.getLogger(TestMain.class);
	public static void main(String[] args) {
		//testMap();

		Map<String,Integer> map = new HashMap<>();
		map.put(RandomStringUtils.randomNumeric(10),new Random().nextInt(1000));
		map.put(RandomStringUtils.randomNumeric(10),new Random().nextInt(1000));
		logger.info("map:{}",map);
	}

	private static void testMap() {
//		Map<Integer, String> map = new HashMap<>();
//		Map<Integer, String> addMap = new HashMap<>();
//		addMap.put(123,"444");
//		addMap.put(1234,"444");
//		map.put(123, "456");
//		map.putAll(addMap);
//		addMap.putAll(map);

//		logger.info(map.toString());
//		logger.info(addMap.toString());
//		map.put(456, "789");
//		System.err.println(map.get(Integer.valueOf(123)));//456
//		System.err.println(map.get("456"));//null
//		System.err.println(map.get(456));//789
		
//		List<Integer> list = new ArrayList<>();
//		list.add(123);
//		System.err.println(list);

		String corpId_appId_uid = "2_12_2";
		logger.error("==>>"+Objects.hashCode(corpId_appId_uid));//1520702849
		logger.error("==>>"+Objects.hash(corpId_appId_uid));//1520702880
		
//		String vString = null;
//		if (Constant.REDIS_ERROR_STRING.equals(vString)) {
//			System.err.println("ttt");
//		}else {
//			System.err.println("eeee");
//		}

		String string = "[{\"productSetId\":38,\"skuModels\":[{\"addTime\":1544492330869,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FgcyKHjhbnKMxAUZxXBY1QVPSy9_\",\"name\":\"保湿滋养香氛身体乳\",\"price\":3990,\"productId\":137,\"skuCode\":\"6935677122141\",\"skuId\":1374,\"specVal\":\"波普的芳香\",\"status\":1,\"store\":48}]},{\"productSetId\":0,\"skuModels\":[{\"addTime\":1544492330859,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FgyfMiFzIz-jam1N9wAQmmHBROuj\",\"name\":\"多功能组合棉签 300支\",\"price\":2,\"productId\":115,\"skuCode\":\"6935677121182\",\"skuId\":870,\"specVal\":\"默认值\",\"status\":1,\"store\":300}]},{\"productSetId\":78,\"promoTips\":\"满3免1\",\"promoVo\":{\"condition\":3,\"id\":630,\"name\":\"新满3免1\"},\"skuModels\":[{\"addTime\":1544492330849,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FnV0C7W-RZA_UMZVnXS0ehmVJeQ8\",\"name\":\"博朗电动剃须刀\",\"price\":2,\"productId\":368,\"skuCode\":\"A1B1DDP0001W11F\",\"skuId\":1587,\"specVal\":\"默认值\",\"status\":1,\"store\":100},{\"addTime\":1544492330744,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FkARK7m0reGjVcHTuogJ9IZlTKzr\",\"name\":\"hdmi线\",\"price\":2,\"productId\":371,\"skuCode\":\"A1B1DDP0001W14F\",\"skuId\":1590,\"specVal\":\"默认值\",\"status\":1,\"store\":1},{\"addTime\":1544492330733,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FuhE6fu2sAQVIlckjl32y_2RdcxA\",\"name\":\"洗脸刷\",\"price\":2,\"productId\":370,\"skuCode\":\"A1B1DDP0001W13F\",\"skuId\":1589,\"specVal\":\"默认值\",\"status\":1,\"store\":102}]},{\"productSetId\":76,\"promoTips\":\"满4免1\",\"promoVo\":{\"condition\":4,\"id\":625,\"name\":\"cxl满4免1-下单-请勿动谢谢\"},\"skuModels\":[{\"addTime\":1544492330838,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FpmKODoICaIgGJXMkMacmN8Z7cCw\",\"name\":\"居家棉麻收纳凳 多色\",\"price\":2,\"productId\":197,\"skuCode\":\"6935677107445\",\"skuId\":616,\"specVal\":\"卡其\",\"status\":1,\"store\":199}]},{\"productSetId\":38,\"skuModels\":[{\"addTime\":1544492330828,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FgHHUwnKm9u1OAerPoKKAusC5Ktl\",\"name\":\"移动电源10000mAh 多色\",\"price\":7990,\"productId\":314,\"skuCode\":\"6935677117413\",\"skuId\":1511,\"specVal\":\"树叶\",\"status\":1,\"store\":48}]},{\"productSetId\":36,\"skuModels\":[{\"addTime\":1544492330817,\"count\":1,\"imgUrl\":\"http://storage.nome.com/Fl46J-oe4K9guVfN0RIrJXgVMeYK\",\"name\":\"便携多功能化妆套刷 4件套\",\"price\":1500,\"productId\":103,\"skuCode\":\"6971061015521\",\"skuId\":860,\"specVal\":\"默认值\",\"status\":1,\"store\":240}]},{\"productSetId\":38,\"skuModels\":[{\"addTime\":1544492330807,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FhrixbaADr_k6grQuwAJ0FlBK0l9\",\"name\":\"明净绚丽棉质女短袜 两双装\",\"price\":1500,\"productId\":284,\"skuCode\":\"A1C2G31P0004B20F\",\"skuId\":689,\"specVal\":\"粉蓝\",\"status\":1,\"store\":100}]},{\"productSetId\":33,\"skuModels\":[{\"addTime\":1544492330797,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FqFKgPvr1Go4IDymd8t7kGxyKOTm\",\"name\":\"居家无痕挂钩4枚装\",\"price\":1000,\"productId\":203,\"skuCode\":\"6971061011349\",\"skuId\":1448,\"specVal\":\"默认值\",\"status\":1,\"store\":994}]},{\"productSetId\":38,\"skuModels\":[{\"addTime\":1544492330787,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FkT32YMaQ2J2HyFvVhWO1SSCIDUN\",\"name\":\"明净绚丽棉质女短袜 两双装\",\"price\":1500,\"productId\":284,\"skuCode\":\"A1C2G31P0004D10F\",\"skuId\":690,\"specVal\":\"黑色\",\"status\":1,\"store\":100}]},{\"productSetId\":39,\"skuModels\":[{\"addTime\":1544492330776,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FmG_PTFS1z0ulioVzf_U5TlaScAh\",\"name\":\"北欧凸版字母棒球帽白色\",\"price\":3990,\"productId\":302,\"skuCode\":\"A8A2G20P0001W10F\",\"skuId\":741,\"specVal\":\"白色\",\"status\":1,\"store\":100}]},{\"productSetId\":38,\"skuModels\":[{\"addTime\":1544492330765,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FgDH9sS9SOy0Qf1jH5YPGHov645k\",\"name\":\"明净绚丽棉质女短袜 两双装\",\"price\":1500,\"productId\":284,\"skuCode\":\"A1C2G31P0004D20F\",\"skuId\":691,\"specVal\":\"灰色\",\"status\":1,\"store\":100}]},{\"productSetId\":36,\"skuModels\":[{\"addTime\":1544492330755,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FnID0NkjEmnR9kE2z4dSWs08pUSE\",\"name\":\"滋润清爽香氛护手霜\",\"price\":1500,\"productId\":136,\"skuCode\":\"6935677122783\",\"skuId\":1364,\"specVal\":\"凌晨4点\",\"status\":1,\"store\":149}]},{\"productSetId\":33,\"skuModels\":[{\"addTime\":1544492330723,\"count\":1,\"imgUrl\":\"http://storage.nome.com/FuVUdCHVQnirmrIYNuTLtl_l5GtI\",\"name\":\"柔软护颈枕芯\",\"price\":6990,\"productId\":240,\"skuCode\":\"6935677103348\",\"skuId\":652,\"specVal\":\"白色\",\"status\":1,\"store\":100}]}]";


		CartWrap cartWrap = new CartWrap();
		List<ValidSkuModel> validSkuItems = JSONArray.parseArray(string,ValidSkuModel.class);
		mergeNoPromoSku(cartWrap ,validSkuItems);
	}

	private static void mergeNoPromoSku(CartWrap cartWrap, List<ValidSkuModel> validSkuItems) {

		List<CartSkuModel> tempSkuModels = new ArrayList<>();
		List<ValidSkuModel> convertValidSkus = new ArrayList<>();

		int size = validSkuItems.size();
		Iterator<ValidSkuModel> validSkusItr = validSkuItems.iterator();
		while (validSkusItr.hasNext()) {

			ValidSkuModel skuModel = validSkusItr.next();

			if (skuModel.getPromoVo() == null || StringUtils.isBlank(skuModel.getPromoTips())) {
				tempSkuModels.addAll(skuModel.getSkuModels());
			} else {
				ValidSkuModel promoValidSkuModel = new ValidSkuModel();
				BeanUtils.copyProperties(skuModel, promoValidSkuModel);

				if (!tempSkuModels.isEmpty()) {
					ValidSkuModel noPromoSku = new ValidSkuModel();
					noPromoSku.setProductSetId(0);
					List<CartSkuModel> copyList = new ArrayList<>(tempSkuModels);
					noPromoSku.setSkuModels(copyList);
					convertValidSkus.add(noPromoSku);
					tempSkuModels.clear();
				}
				convertValidSkus.add(promoValidSkuModel);
			}
		}

		if (!tempSkuModels.isEmpty()) {
			ValidSkuModel noPromoSku = new ValidSkuModel();
			noPromoSku.setProductSetId(0);
			List<CartSkuModel> copyList = new ArrayList<>(tempSkuModels);
			noPromoSku.setSkuModels(copyList);
			convertValidSkus.add(noPromoSku);
		}

		cartWrap.setValidSkus(convertValidSkus);
		logger.info(cartWrap.toString());
	}
}
