package cn.nome.saas.allocation.service.old.allocation.impl;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.model.old.allocation.GoodsAreaLevel;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.repository.dao.allocation.IssueDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewGoodsIssueRangeDetailMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDetailDO;
import cn.nome.saas.allocation.repository.old.allocation.dao.GoodsDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.StockDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.ProhibitedService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ProhibitedServiceImpl implements ProhibitedService {

	private static Logger logger = LoggerFactory.getLogger(ProhibitedServiceImpl.class);

	@Autowired
	StockDOMapper2 stockDOMapper2;

	@Autowired
    GoodsDOMapper2 goodsDOMapper2;

	@Autowired
	IssueDOMapper issueDOMapper;

	@Autowired
	DwsShopDOMapper dwsShopDOMapper;
	@Autowired
	NewGoodsIssueRangeDetailMapper newGoodsIssueRangeDetailMapper;

	@Value("${all.area.name:'全盘'}")
	private String ALL_AREA_NAME;

	@Value("${all.area.level:'华南一[A]华南二[A]华东[A]华中[A]华北[A]西南[A]西北[A]东北[A]中区[A]西区[A]南区[A]北区[A]北区-北[A]华南二-南[A]华南二-中[A]西区-北[A]西区-南[A]'}")
	private String ALL_AREA_LEVEL;

	/**
	 * 将禁品列表放到map中，方便读取
	 * 
	 * @return
	 */
	@Override
	public Map<String, Map<String, ProhibitedGoods>> getProhibitedGoods() {
		return getProhibitedGoods(null);
	}

	/**
	 * getProhibitedGoods
	 * @param shopIds
	 * @return Map<shopId, Map<matCode, ProhibitedGoods>>
	 */
	@Override
	public Map<String, Map<String, ProhibitedGoods>> getProhibitedGoods(Set<String> shopIds) {
		List<ProhibitedGoods> prohibitedGoodsList = stockDOMapper2.getProhibitedGoodsList(shopIds);
		LoggerUtil.info(logger, "禁品总数量：{0}", prohibitedGoodsList.size());
		Map<String, Map<String, ProhibitedGoods>> shopPgMatCodeMap = Maps.newHashMap();
		/**
		 * {
		 *     "shopId1": {
		 *         "matCode1": {ProhibitedGoods},
		 *         "matCode2": {ProhibitedGoods},
		 *         "matCode3": {ProhibitedGoods}
		 *     }
		 * }
		 */
		for (ProhibitedGoods temp : prohibitedGoodsList) {
			Map<String, ProhibitedGoods> mapGoods = shopPgMatCodeMap.computeIfAbsent(temp.getShopId(), k -> Maps.newHashMap());
			mapGoods.putIfAbsent(temp.getMatCode(), temp);
		}

		// 输出禁品日志
		LoggerUtil.info(logger, "[载入禁品日志] 店铺数：{0}", shopPgMatCodeMap.size());
		Set<Map.Entry<String, Map<String, ProhibitedGoods>>> shopPgMatCodeEntries = shopPgMatCodeMap.entrySet();
		for (Map.Entry<String, Map<String, ProhibitedGoods>> shopPgMatCodeEntry: shopPgMatCodeEntries) {
			Map<String, ProhibitedGoods> shopPgMap = shopPgMatCodeEntry.getValue();
			LoggerUtil.info(logger, "[载入禁品日志] 店铺id：{0}, 禁配条数：{1}", shopPgMatCodeEntry.getKey(), shopPgMap != null ? shopPgMap.size(): 0);
		}

		// TODO 去掉白名单限制
		// 获取白名单列表 应该获取所有表名单
		// TODO 为什么不查shopIds
//		List<ProhibitedGoods> whiteList = stockDOMapper2.getWhiteGoodsList(null);
//		LoggerUtil.info(logger, "白名单总数量：{0}", whiteList.size());
//
//		//解析存放map<matCode, <shopId, 对象>>
//		Map<String, Map<String, ProhibitedGoods>> matCodeWgShopMap = Maps.newHashMap();
//		/**
//		 * {
//		 *     "matCode1": {
//		 *         "shopId1": {ProhibitedGoods},
//		 *         "shopId2": {ProhibitedGoods},
//		 *         "shopId3": {ProhibitedGoods}
//		 *     }
//		 * }
//		 */
//		if (whiteList.size() > 0) {
//			for (ProhibitedGoods whiteTemp : whiteList) {
//				Map<String, ProhibitedGoods> mapGoods = matCodeWgShopMap.computeIfAbsent(whiteTemp.getMatCode(), k -> Maps.newHashMap());
//				mapGoods.putIfAbsent(whiteTemp.getShopId(), whiteTemp);
//			}
//		}
//
//		for (Map.Entry<String, Map<String, ProhibitedGoods>> entry: matCodeWgShopMap.entrySet()) {
//			String matCode = entry.getKey();
//			Map<String, ProhibitedGoods> shopWgMap = entry.getValue();
//
//			// 获取所有的shopId, 并取白名单的反集, 则为需要禁配的shopId
//			Set<String> forbiddenShopSet = new HashSet<>(getShopIdSet());
//            Set<String> whiteListShopSet = shopWgMap.keySet();
//			forbiddenShopSet.removeAll(whiteListShopSet);
//
//			// TODO 这步操作的作用只是取一个ruleName固定值吗？
//			ProhibitedGoods whiteListSingleRule = shopWgMap.get(whiteListShopSet.iterator().next());
//			String ruleName = whiteListSingleRule.getRuleName();
//
//			// 取所有shopIds - 白名单shopIds = 白名单外的禁配shopIds
//			// 增加白名单外的禁配到禁配集合中
//			for (String shopId : forbiddenShopSet) {
//				Map<String, ProhibitedGoods> mapGoods = shopPgMatCodeMap.computeIfAbsent(shopId, k -> Maps.newHashMap());
//				ProhibitedGoods prohibitedGoods = new ProhibitedGoods();
//				prohibitedGoods.setShopId(shopId);
//				prohibitedGoods.setMatCode(matCode);
//				prohibitedGoods.setRuleName(ruleName);
//				mapGoods.putIfAbsent(matCode, prohibitedGoods);
//			}
//		}

		//获取新品白名单
		List<NewGoodsIssueRangeDetailDO> newGoodsIssueRangeDetailDos = newGoodsIssueRangeDetailMapper.selectNewGoodsWhiteList();
		//解析存放map<matCodeSizeName, <shopId, 对象>>
		Map<String, Map<String, ProhibitedGoods>> newGoodsWhiteMap = Maps.newHashMap();
		if (newGoodsIssueRangeDetailDos.size() > 0) {
			// TODO 为空，所以不执行
			for (NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailDO : newGoodsIssueRangeDetailDos) {
				Map<String, ProhibitedGoods> mapGoods = newGoodsWhiteMap.computeIfAbsent(newGoodsIssueRangeDetailDO.getMatCodeSizeNameKey(), k -> Maps.newHashMap());
				mapGoods.putIfAbsent(newGoodsIssueRangeDetailDO.getShopId(), null);
			}
		}

		for (String matCodeSizeName : newGoodsWhiteMap.keySet()) {
			// TODO 为空，所以不执行
			//获取所有的shopId, 并取白名单的反集, 则为需要禁配的shopId
			Set<String> newForbiddenShopSet = new HashSet<>(getShopIdSet());
			Set<String> whiteListShopSet = newGoodsWhiteMap.get(matCodeSizeName).keySet();
			newForbiddenShopSet.removeAll(whiteListShopSet);
			ProhibitedGoods whiteListSingleRule = newGoodsWhiteMap.get(matCodeSizeName).get(whiteListShopSet.iterator().next());
			//增加白名单外的禁配到禁配集合中
			for (String shopId : newForbiddenShopSet) {
				Map<String, ProhibitedGoods> mapGoods = shopPgMatCodeMap.computeIfAbsent(shopId, k -> Maps.newHashMap());
				ProhibitedGoods newProhibitedGoods = new ProhibitedGoods();
				newProhibitedGoods.setShopId(shopId);
				newProhibitedGoods.setMatCode(matCodeSizeName);
				newProhibitedGoods.setRuleName("新品禁配");
				mapGoods.putIfAbsent(matCodeSizeName, newProhibitedGoods);
			}
		}

		List<ProhibitedGoods> securityList = stockDOMapper2.getSecurityList();
		if (securityList.size() > 0) {
			for (ProhibitedGoods securityTemp : securityList) {
				Map<String, ProhibitedGoods> mapGoods = shopPgMatCodeMap.computeIfAbsent(securityTemp.getShopId(), k -> Maps.newHashMap());
				mapGoods.putIfAbsent(securityTemp.getMatCode(), securityTemp);
			}
		}

		return shopPgMatCodeMap;
	}

	@Override
	public Map<String, Map<String, ProhibitedGoods>> getProhibitedGoodsByDate(Set<String> shopIds, Date date) {
		List<ProhibitedGoods> list = stockDOMapper2.getProhibitedGoodsListByDate(shopIds, date);
		LoggerUtil.info(logger, "禁品总数量：{0}", list.size());
		Map<String, Map<String, ProhibitedGoods>> map = Maps.newHashMap();
		for (ProhibitedGoods temp : list) {
			Map<String, ProhibitedGoods> mapGoods = map.computeIfAbsent(temp.getShopId(), k -> Maps.newHashMap());
			mapGoods.putIfAbsent(temp.getMatCode(), temp);
		}

		//获取白名单列表 应该获取所有表名单,
//		List<ProhibitedGoods> whiteList = stockDOMapper2.getWhiteGoodsList(shopIds);
		List<ProhibitedGoods> whiteList = stockDOMapper2.getWhiteGoodsListByDate(null, date);
		LoggerUtil.info(logger, "白名单总数量：{0}", whiteList.size());
		//解析存放map<matCode, <shopId, 对象>>
		Map<String, Map<String, ProhibitedGoods>> whiteMap = Maps.newHashMap();
		if (whiteList.size() > 0) {
			for (ProhibitedGoods whiteTemp : whiteList) {
				Map<String, ProhibitedGoods> mapGoods = whiteMap.computeIfAbsent(whiteTemp.getMatCode(), k -> Maps.newHashMap());
				mapGoods.putIfAbsent(whiteTemp.getShopId(), whiteTemp);
			}
		}
		Set<String> forbiddenShopSet;ProhibitedGoods prohibitedGoods;
		for (String matCode : whiteMap.keySet()) {
			//获取所有的shopId, 并取白名单的反集, 则为需要禁配的shopId
			forbiddenShopSet = new HashSet<>(getShopIdSet());
			Set<String> whiteListShopSet = whiteMap.get(matCode).keySet();
			forbiddenShopSet.removeAll(whiteListShopSet);
			ProhibitedGoods whiteListSingleRule = whiteMap.get(matCode).get(whiteListShopSet.iterator().next());
			//增加白名单外的禁配到禁配集合中
			for (String shopId : forbiddenShopSet) {
				Map<String, ProhibitedGoods> mapGoods = map.computeIfAbsent(shopId, k -> Maps.newHashMap());
				prohibitedGoods = new ProhibitedGoods();
				prohibitedGoods.setShopId(shopId);
				prohibitedGoods.setMatCode(matCode);
				prohibitedGoods.setRuleName(whiteListSingleRule.getRuleName());
				mapGoods.putIfAbsent(matCode, prohibitedGoods);
			}
		}

		List<ProhibitedGoods> securityList = stockDOMapper2.getSecurityListByDate(date);
		if (securityList.size() > 0) {
			for (ProhibitedGoods securityTemp : securityList) {
				Map<String, ProhibitedGoods> mapGoods = map.computeIfAbsent(securityTemp.getShopId(), k -> Maps.newHashMap());
				mapGoods.putIfAbsent(securityTemp.getMatCode(), securityTemp);
			}
		}

		return map;
	}

	private static Set<String> SHOP_ID_SET = new HashSet<>();
	private static long SHOP_ID_TIME = 0;

	/**
	 * 获取shopId集合
	 * @return shopId集合
	 */
	private Set<String> getShopIdSet() {
		//数据超过一小时重新从数据库获取
		long nowTime = System.currentTimeMillis();
		if (SHOP_ID_SET.size() == 0 || SHOP_ID_TIME - nowTime > 3600 * 1000) {
			SHOP_ID_SET = new HashSet<>(dwsShopDOMapper.selectShopIdByParam(new HashMap<>(0)));
			SHOP_ID_TIME = nowTime;
		}
		return SHOP_ID_SET;
	}

	/**
	 * 判断是否为禁品
	 * 
	 * @param map
	 * @param shopId
	 * @param matCode
	 * @return
	 */
	@Override
	public boolean checkIfIsProhibited(Map<String, Map<String, ProhibitedGoods>> map, String shopId, String matCode) {
		if (map.get(shopId) != null && map.get(shopId).get(matCode) != null) {
			return true;
		}
		return false;
	}

//	public void prohibitedGoods(int taskId) {
//		List<ProhibitedGoods> list = stockDOMapper2.getProhibitedGoodsList();
//		LoggerUtil.info(logger, "禁品总数量：{0}", list.size());
//
//		if (list != null) {
//			Map<String, List<String>> map = Maps.newHashMap();
//			for (ProhibitedGoods temp : list) {
//				List<String> listString = map.get(temp.getShopId());
//				if (listString == null) {
//					listString = Lists.newArrayList();
//					map.put(temp.getShopId(), listString);
//				}
//				listString.add(temp.getMatCode());
//			}
//
//			for (String key : map.keySet()) {
//				LoggerUtil.info(logger, "禁品更新门店：{0}", key);
//				stockDOMapper2.updateProhibitedGoods(taskId, key, map.get(key));
//			}
//		}
//	}

	@Override
	public void processGoodsArea() {
		LoggerUtil.info(logger, "商品货盘数据处理");
		List<GoodsAreaLevel> goodsList = goodsDOMapper2.getGoodsAreaLevelList();
		List<GoodsAreaLevel> goodsDetailList = Lists.newArrayList();

		for (GoodsAreaLevel goods : goodsList) {
			if (!StringUtils.isEmpty(goods.getLevels())) {
				LoggerUtil.info(logger, "商品货盘数据处理，商品信息:{0}", goods);
				String levels = goods.getLevels();
				if (levels.equals(ALL_AREA_NAME)) {
					levels = ALL_AREA_LEVEL;
				}

				String arr[] = levels.split("\\]");
				for (int i = 0; i < arr.length; i++) {
					String keyValue[] = arr[i].split("\\[");
					GoodsAreaLevel detail = new GoodsAreaLevel();
					detail.setMatCode(goods.getMatCode());
					detail.setCategoryName(goods.getCategoryName());
					detail.setArea(keyValue[0]);
					if (keyValue.length == 1) {
						detail.setLevel("");
						logger.warn("GoodsAreaLevel MatCode:{},levels:{}", goods.getMatCode(), levels);
					} else {
						detail.setLevel(keyValue[1]);
					}
					goodsDetailList.add(detail);
				}
			}
		}
		goodsDOMapper2.deleteGoodsAreaLevel();
		goodsDOMapper2.deleteGoodsAreaLevelDetail();
		LoggerUtil.info(logger, "商品货盘数据处理，生成货盘数据量：{0}", goodsDetailList.size());
		goodsDOMapper2.addGoodsAreaLevel(goodsDetailList);
		goodsDOMapper2.addGoodsAreaLevelDetail();

	}

	@Override
	public Map<String, String> getAllocationProhibitedGoods() {
		LoggerUtil.info(logger, "获取调拨禁品");
		List<String> list = goodsDOMapper2.getAllocationProhibitedGoods();
		LoggerUtil.info(logger, "获取调拨禁品，数量：{0}", list.size());
		Map<String, String> map = Maps.newHashMap();
		for (String temp : list) {
			map.put(temp, temp);
		}
		return map;
	}

}
