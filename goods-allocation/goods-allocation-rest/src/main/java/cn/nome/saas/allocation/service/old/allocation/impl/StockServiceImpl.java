package cn.nome.saas.allocation.service.old.allocation.impl;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.model.old.allocation.*;
import cn.nome.saas.allocation.repository.old.allocation.dao.StockDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.TaskMapper2;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.StockService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StockServiceImpl implements StockService {
	private static Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

	@Autowired
    StockDOMapper2 stockDOMapper2;

	@Autowired
	TaskMapper2 taskMapper2;

	@Autowired
    AllocationDOMapper2 allocationDOMapper2;

//	@Autowired
//	ExpressRuleService expressRuleService;
//
//	@Autowired
//	MinDisplaySkcRuleService minDisplaySkcRuleService;

//	@Autowired
//	ShopListCache2 ShopListCache2;
//
//	@Autowired
//	MinDisplayCache minDisplayCache;

	@Override
	public Map<String, List<Stock>> getInStockList(Task task) {
		LoggerUtil.info(logger, "getInStockList");

		Map<String, List<Stock>> mapList = Maps.newConcurrentMap();

		List<Stock> list = stockDOMapper2.getInStockList(task.getTask_id());
		for (Stock stock : list) {
			List<Stock> shopStockList = mapList.get(stock.getShopID());
			if (shopStockList == null) {
				shopStockList = Lists.newArrayList();
				mapList.put(stock.getShopID(), shopStockList);
			}
			shopStockList.add(stock);
		}

		return mapList;
	}

	private static String getSqlIn(String field, String ids) {
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = ids.split(",");
			for (int i = 0; i < idArr.length; i++) {
				idArr[i] = "'" + idArr[i] + "'";
			}
			String idStr = field + " in (" + StringUtils.join(idArr, ",") + ")";
			return idStr;
		} else {
			return "";
		}
	}

	private String getTypeSql(Task task) {
		String typeSql = "Categorycode in ('M','W')";
		if (task.getTask_type() == 2)
			typeSql = "Categorycode not in ('M','W')";
		return typeSql;
	}

	private String getInShopSql(Task task) {
		if (task.getArea_type() == 1) {
			return "and " + getSqlIn("a.shopid", task.getIn_shop_ids());
		} else {
			return "";
		}
	}

	@Override
	public void inStock(Task task) {

	}

	@Override
	public void inComplementStock(Task task) {
		String typeSql = getTypeSql(task);
		int period = getPeriod(task);
		String inShopSql = getInShopSql(task);
		List<Stock> list = null;
		if(task.getAllocation_type()== 1) {
			list = allocationDOMapper2.getComplementList(period, inShopSql);
		}else {
			list = allocationDOMapper2.getWithdrawalComplementList(period, inShopSql);
		}
		
		if (list != null && list.size() > 0) {
			LoggerUtil.info(logger, "取数结果为：{0}", list.size());
			Map<String, List<Stock>> shopStockMap = this.getInStockList(task);
			// 分批录入
			List<Stock> tempList = Lists.newArrayList();
			int count = 1;
			for (int i = 0; i < list.size(); i++) {
				Stock stock = list.get(i);
				stock.setTaskId(task.getTask_id());
				stock.setIsComplement(1);
				stock.setAvgSaleAmt(0d);
				stock.setAvgSaleQty(0d);
				if (stock.getStockQty() < 0) {
					stock.setStockQty(0l);
				}
				tempList.add(stock);

				if (i == 5000 * count || i == list.size() - 1) {
					LoggerUtil.info(logger, "取数结果为： inStockList addInStock size{0}", tempList.size());
					if (tempList.size() > 0) {
						// 先处理价格
						processStockPrice(tempList);
						List<Stock> notExistStockList = this.getNotExistInStock(task, tempList, shopStockMap);
						if (notExistStockList.size() > 0)
							stockDOMapper2.addInStock(notExistStockList);
					}
					tempList = Lists.newArrayList();
					++count;
				}
			}
		}
	}

	public List<Stock> getNotExistInStock(Task task, List<Stock> list, Map<String, List<Stock>> shopStockMap) {
		List<Stock> newList = Lists.newArrayList();
		for (Stock stock : list) {
			if (checkIfExist(stock, shopStockMap)) {
				LoggerUtil.info(logger, "齐码SKU有重复数据：{0}", stock);
			} else {
				newList.add(stock);
			}
		}
		return newList;
	}

	private boolean checkIfExist(Stock stock, Map<String, List<Stock>> shopStockMap) {
		List<Stock> list = shopStockMap.get(stock.getShopID());
		if (list != null) {
			for (Stock temp : list) {
				// matcode跟sizeid
				if (temp.getMatCode().equals(stock.getMatCode()) && temp.getSizeID().equals(stock.getSizeID()))
					return true;
			}
		}
		return false;
	}

	@Override
	public void outStock(Task task) {
//		Map<String, List<Stock>> mapList = this.getInStockList(task);
//		Date date1 = new Date();
//
//		int period = getPeriod(task);
//
//		//List<ShopSizeIdInfoDO> lessThanThreesizeIdList = allocationDOMapper2.getSizeIdInfoList(null,null,1,2);
//
//		for (String key : mapList.keySet()) {
//			boolean pass = false;
//			List<Stock> shopStockList = mapList.get(key);
//			LoggerUtil.info(logger, "取数结果为：{0} {1}", key, shopStockList.size());
//			List<String> shopList = null;
//			if (task.getArea_type() == 1) {
//				pass = true;
//				if(!org.springframework.util.StringUtils.isEmpty(task.getOut_shop_ids())) {
//					String[] arr = task.getOut_shop_ids().split(",");
//					shopList = Arrays.asList(arr);
//					LoggerUtil.info(logger, "取数结果为： 固定门店shopList {0}", shopList);
//				}else {
//					LoggerUtil.info(logger, "取数结果为： 跑全国门店shopList {0}", key);
//				}
//
//			} else {
//				shopList = allocationDOMapper2.getCityShop(key);
//				if(shopList.size()>0) {
//					pass = true;
//				}
//				LoggerUtil.info(logger, "取数结果为： shopList {0}", shopList);
//			}
//
//			// 只有有门店和商品数据时才处理
//			if (pass && shopStockList.size() > 0) {
//				List<Stock> outStockList = null;
//				if(task.getAllocation_type()== 1) {
//					outStockList = allocationDOMapper2.getOutStockList(period, shopList, shopStockList);
//				}else {
//					outStockList = allocationDOMapper2.getWithdrawalOutStockList(period, shopList, shopStockList);
//				}
//
//
//				//Set<String> outShopList = outStockList.stream().map(Stock::getShopID).collect(Collectors.toSet());
//				//Set<String> matCodeList = outStockList.stream().map(Stock::getMatCode).collect(Collectors.toSet());
//
//				// 执行最小陈列规则过滤逻辑
//				// outStockList = minDisplaySkcRuleService.minDisplayOutStockMatch(task.getTask_type(), outStockList, outShopList, matCodeList, lessThanThreesizeIdList);
//
//		        Map<String,Integer> minDisplay = minDisplayCache.getMinDisplayMap();
//
//				for (Stock stock : outStockList) {
//					stock.setInShopID(key);
//				}
//
//				LoggerUtil.info(logger, "取数结果为： outStockList size{0}", outStockList.size());
//				if (outStockList.size() > 0) {
//					// 分批录入
//					List<Stock> tempList = Lists.newArrayList();
//					int count = 1;
//					for (int i = 0; i < outStockList.size(); i++) {
//						Stock stock = outStockList.get(i);
//						stock.setTaskId(task.getTask_id());
//						if(task.getAllocation_type()== 1) {
//							minDisplaySkcRuleService.processOutStockMinDisplay(stock, minDisplay, task);
//						}
//						tempList.add(stock);
//						if (i == 5000 * count || i == outStockList.size() - 1) {
//							LoggerUtil.info(logger, "取数结果为： outStockList addOutStock size{0}", tempList.size());
//							if (tempList.size() > 0) {
//								processStockPrice(tempList);
//								stockDOMapper2.addOutStock(tempList);
//							}
//							tempList = Lists.newArrayList();
//							++count;
//						}
//					}
//				}
//			}
//
//
//			// break;
//		}
//		Date date2 = new Date();
//
//		long diff = date2.getTime() - date1.getTime();
//		int minit = (int) diff / 1000 / 60;
//		LoggerUtil.warn(logger, "取数结果为,总共执行 {0}分钟", minit);

	}

	/**
	 * 获取调入门店所需库存、库存总金额
	 * @param task
	 * @return
	 */
	@Override
	public List<StockStat> getInStockStats(Task task) {
		return stockDOMapper2.getInStockStats(getPeriod(task), task.getTask_id());
	}

//	/**
//	 * 根据调入店，查询出所有调出店可用库存数大于0的列表
//	 * @param shopId
//	 * @param task
//	 * @return
//	 */
//	@Override
//	public List<StockStat> getOutStockStats(String shopId, Task task) {
//		List<StockStat> stockStatList = stockDOMapper2.getOutStockStats(getPeriod(task), shopId, task.getTask_id());
//
//		List<StockStat> newStockStatList = new ArrayList<>();
//		// 针对非同城，增加物流规则排序
//		if (task.getArea_type() == 0) {
//
//			if (task.getTask_type() == 1 || task.getTask_type() == 2) {
//				stockStatList = expressRuleService.getShippingFree(stockStatList, task.getTask_type());
//			}
//
//			int period = getPeriod(task);
//			newStockStatList = stockStatList.stream().sorted((s1, s2) -> {
//				int o1 = s1.calcExpressSort(period);
//				int o2 = s2.calcExpressSort(period);
//
//				return o2 - o1;
//			}).collect(Collectors.toList());
//
//			return newStockStatList;
//		} else {
//			return stockStatList;
//		}
//	}

	/**
	 * 查询所需库存大于0的调入门店列表
	 * @param inShopId
	 * @param task
	 * @return
	 */
	@Override
	public List<Stock> getInStockList(String inShopId, Task task) {
		return stockDOMapper2.getInStockListByShopId(getPeriod(task), inShopId, task.getTask_id());
	}

	/**
	 * 针对调入店，可调出的门店列表（可用库存大于0）
	 * @param inShopId
	 * @param task
	 * @return
	 */
	@Override
	public List<Stock> getOutStockList(String inShopId, Task task) {
		return stockDOMapper2.getOutStockListByShopId(getPeriod(task), inShopId, task.getTask_id());
	}

	private int getPeriod(Task task) {
		int period = 0;
		if(task.getAllocation_type() == 1) {
			if (task.getTask_type() == 2) {
				// 百货
				period = task.getCommodity_period();
			} else {
				// 默认服装
				period = task.getClothing_period();
			}
		}else {
			period = task.getDays();
		}

		return period;
	}

	/**
	 * 1.查询出需要调入的门店sku信息
	 * 2.
	 * @param task
	 */
//	@Override
//	public void allocation(Task task) {
//		Date date1 = new Date();
//		LoggerUtil.info(logger, "开始数据处理");
//		List<StockStat> inStockStats = this.getInStockStats(task);
//		LoggerUtil.info(logger, "开始数据处理,缺货门店数据量：{0}", inStockStats.size());
//
//		// 查询小于2个尺码的库存信息
//		List<ShopSizeIdQtyInfoDO> storeSizeIdQtyList = null;
//		Map<String,List<ShopSizeIdQtyInfoDO>> sizeIdQtyMap = null;
//
//		if (task.getTask_type() == 1) {
//			Set<String> shopList = inStockStats.stream().map(StockStat::getShopID).collect(Collectors.toSet());
//			storeSizeIdQtyList =  allocationDOMapper2.getSizeIdQtyListByRemainderQty(shopList,null,1,2);
//			sizeIdQtyMap= storeSizeIdQtyList.parallelStream().collect(Collectors.groupingBy(s->{
//				return s.getShopId()+","+s.getMatCode();
//			},Collectors.toList()));
//		}
//
//		Map<String,List<AllocationDetail>> inshopAllocationMap = new HashMap<>();
//
//		Map<String,List<Stock>> stockMap = new HashMap<>();
//		for (StockStat inStockStat : inStockStats) {
//			// 缺货单店处理
//			List<StockStat> outStockStats = this.getOutStockStats(inStockStat.getShopID(), task); // 对调出店贡献排名
//			List<Stock> inStockList = this.getInStockList(inStockStat.getShopID(), task);
//			List<Stock> outStockList = this.getOutStockList(inStockStat.getShopID(), task);
//
//			LoggerUtil.info(logger, "开始数据处理,缺货门店数据量，单缺货门店处理：{0},{1},{2},{3}", inStockStat.getShopID(),
//					outStockStats.size(), inStockList.size(), outStockList.size());
//
//			Map<String, List<Stock>> outStockListMapByShop = this.getOutStockListMapByShop(outStockList);
//			stockMap.put(inStockStat.getShopID(),inStockList);
//
//			List<AllocationDetail> list = new ArrayList<>();
//			for (StockStat stat : outStockStats) {
//				List<Stock> shopStockList = outStockListMapByShop.get(stat.getShopID());
//				if (shopStockList != null) {
//					if (!checkIfStockEnough(inStockList)) {
//
//						List<AllocationDetail> allocationDetailList = generateAllocationDetailList(inStockList,
//								inStockStat.getShopID(), shopStockList, task.getTask_id());
//						LoggerUtil.info(logger, "开始数据处理,缺货门店数据量，单缺货门店处理,生成调拨单数量：{0}", allocationDetailList.size());
//
//						if (allocationDetailList.size() > 0) {
//							processAllocationDetailPrice(allocationDetailList);
//
//							if (!checkIfIgnogre(allocationDetailList)) {
//								list.addAll(allocationDetailList);
//							} else {
//								LoggerUtil.info(logger, "开始数据处理,缺货门店数据量，单缺货门店处理,忽略生成调拨单：{0}", allocationDetailList);
//							}
//						}
//
//					}
//				} else {
//					LoggerUtil.info(logger, "开始数据处理,缺货门店数据量，单缺货门店处理,取到数据为空。。。{0}", stat.getShopID());
//				}
//			}
//
//			/**
//			 * 调拨单明细须做单店插入，因为在下一家门店时，需要将已生成的调拨单排除
//			 */
//			if(task.getAllocation_type()== 1) {
//				List<AllocationDetail> newAllocationDetailList = preAllocationRuleCalc(task,inStockStat.getShopID(),list,sizeIdQtyMap,stockMap);
//				if(CollectionUtils.isNotEmpty(newAllocationDetailList)) {
//					stockDOMapper2.addAllocationDetail(newAllocationDetailList);
//				}
//			}else {
//				if(CollectionUtils.isNotEmpty(list)) {
//					stockDOMapper2.addAllocationDetail(list);
//				}
//			}
//
//
//		}
//
//		Date date2 = new Date();
//
//		long diff = date2.getTime() - date1.getTime();
//		int minit = (int) diff / 1000 / 60;
//		LoggerUtil.warn(logger, "调拨结果为,总共执行 {0}分钟", minit);
//	}

	/**
	 * 生成调拨单前规则处理
	 * @return
	 */
//	private List<AllocationDetail> preAllocationRuleCalc(Task task,String shopId,List<AllocationDetail> allocationDetailList,Map<String,List<ShopSizeIdQtyInfoDO>> sizeIdQtyMap,Map<String,List<Stock>> stockMap) {
//
//		List<AllocationDetail> newList = new ArrayList<>();
//
//		List<Stock> inStockList = stockMap.get(shopId);
//
//		// 按skc纬度，对调拨明细进行分组
//		Map<String,List<AllocationDetail>> skcAllocationMap = allocationDetailList.stream().collect(Collectors.groupingBy(AllocationDetail::getMatCode,Collectors.toList()));
//
//		// 调拨明细最小陈列匹配
//		List<AllocationDetail> list = minDisplaySkcRuleService.minDisplayAllocationMatch(task.getTask_type(),shopId,inStockList,skcAllocationMap,sizeIdQtyMap);
//		if (CollectionUtils.isNotEmpty(list)) {
//			newList.addAll(list);
//		}
//
//		return newList;
//	}

	/**
	 * 总金额低于1000不生成调拨单
	 * 
	 * @param allocationDetailList
	 * @return
	 */
	private boolean checkIfIgnogre(List<AllocationDetail> allocationDetailList) {
		int totalQty = 0;
		Double totalPrice = 0d;
		// 先计算总数量及总金额
		for (AllocationDetail detail : allocationDetailList) {
			totalQty = totalQty + detail.getQty();
			if (detail.getQuotePrice() != null)
				totalPrice = totalPrice + detail.getQty() * detail.getQuotePrice();
		}
		// 总金额低于1000不生成调拨单
		if (totalPrice < 1000) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否缺货库存里是否需要补货
	 * 
	 * @param inStockList
	 * @return
	 */
	private boolean checkIfStockEnough(List<Stock> inStockList) {
		if (inStockList == null || inStockList.size() == 0)
			return true;
		for (Stock stock : inStockList) {
			if (stock.getNeedStockQtyInt() > 0)
				return false;
		}
		return true;
	}

	private List<AllocationDetail> generateAllocationDetailList(List<Stock> inStockList, String inShopId,
			List<Stock> shopStockList, int taskId) {
		LoggerUtil.info(logger, "开始数据处理,缺货门店数据量，单缺货门店处理,单门店：{0},{1}", inShopId, shopStockList.size());
		List<AllocationDetail> detailList = Lists.newArrayList();
		if (inStockList == null || inStockList.size() == 0)
			return detailList;

		Map<String, Stock> productOutStockList = this.getOutStockMapByProduct(shopStockList);
		// 当商品库存处理
		for (Stock inStock : inStockList) {
			String key = getProductKey(inStock);
			Stock outStock = productOutStockList.get(key);
			AllocationDetail allocationDetail = generateAllocationDetail(inStock, outStock);
			if (allocationDetail != null) {
				allocationDetail.setTaskId(taskId);
				detailList.add(allocationDetail);
			}
		}

		return detailList;
	}

	private AllocationDetail generateAllocationDetail(Stock inStock, Stock outStock) {

		if (outStock != null) {
			// 需要多少库存
			int needStockQtyInt = inStock.getNeedStockQtyInt();
			// 能提供多少库存
			int usableStockQtyInt = outStock.getUsableStockQtyInt();
			if (needStockQtyInt > 0 && usableStockQtyInt > 0) {
				LoggerUtil.info(logger, "开始数据处理,缺货门店数据量，单缺货门店处理,单门店：{0}", outStock);

				// 实际提供多少库存
				int canUseStockQtyInt = needStockQtyInt;
				if (usableStockQtyInt <= needStockQtyInt)
					canUseStockQtyInt = usableStockQtyInt;
				// 剩余库存
				int remainStockQtyInt = needStockQtyInt - canUseStockQtyInt;

				inStock.setNeedStockQtyInt(remainStockQtyInt);

				AllocationDetail allocationDetail = new AllocationDetail();
				allocationDetail.setMatCode(outStock.getMatCode());
				allocationDetail.setSizeID(outStock.getSizeID());
				allocationDetail.setShopID(outStock.getShopID());
				allocationDetail.setInShopID(outStock.getInShopID());
				allocationDetail.setQty(canUseStockQtyInt);
				// 调入
				if (outStock.getAvgSaleAmt() != null)
					allocationDetail.setOutAvgSaleAmt(outStock.getAvgSaleAmt());
				if (outStock.getAvgSaleQty() != null)
					allocationDetail.setOutAvgSaleQty(outStock.getAvgSaleQty());
				if (outStock.getStockQty() != null)
					allocationDetail.setOutStockQty(outStock.getStockQty());
				// 调出
				allocationDetail.setInIsComplement(inStock.getIsComplement());
				if (inStock.getAvgSaleAmt() != null)
					allocationDetail.setInAvgSaleAmt(inStock.getAvgSaleAmt());
				if (inStock.getAvgSaleQty() != null)
					allocationDetail.setInAvgSaleQty(inStock.getAvgSaleQty());
				if (inStock.getStockQty() != null)
					allocationDetail.setInStockQty(inStock.getStockQty());
				if (inStock.getPathStockQty() != null)
					allocationDetail.setInPathStockQty(inStock.getPathStockQty());
				if (inStock.getMoveQty() != null)
					allocationDetail.setInMoveQty(inStock.getMoveQty());

				allocationDetail.setInNeedStockQty(inStock.getRawNeedStockQtyInt());
				// 商品

				return allocationDetail;
			}
		}

		return null;
	}

	private Map<String, List<Stock>> getOutStockListMapByShop(List<Stock> outStockList) {
		Map<String, List<Stock>> map = Maps.newHashMap();
		if (outStockList == null || outStockList.size() == 0)
			return map;

		for (Stock stock : outStockList) {
			List<Stock> shopStock = map.get(stock.getShopID());
			if (shopStock == null) {
				shopStock = Lists.newArrayList();
				map.put(stock.getShopID(), shopStock);
			}
			shopStock.add(stock);
		}
		return map;
	}

	private Map<String, Stock> getOutStockMapByProduct(List<Stock> outStockList) {
		Map<String, Stock> map = Maps.newHashMap();
		if (outStockList == null || outStockList.size() == 0)
			return map;

		for (Stock stock : outStockList) {
			// 商品key组合
			String key = getProductKey(stock);
			map.put(key, stock);
		}
		return map;
	}

	private String getProductKey(Stock stock) {
		return stock.getMatCode() + "_" + stock.getSizeID();
	}

	private void processAllocationDetailPrice(List<AllocationDetail> list) {
		if (list == null && list.size() == 0)
			return;
		List<String> goodsList = Lists.newArrayList();

		for (AllocationDetail d : list) {
			goodsList.add(d.getMatCode());
		}
		List<Goods> priceList = stockDOMapper2.getGoodsPrice(goodsList);
		Map<String, Goods> goodsMap = getGoodsMap(priceList);
		for (AllocationDetail d : list) {
			Goods g = goodsMap.get(d.getMatCode());
			if (g != null) {
				d.setQuotePrice(g.getQuotePrice());
				d.setSeasonName(g.getSeasonName());
				d.setYearNo(g.getYearNo());
				d.setCategoryName(g.getCategoryName());
				d.setMidCategoryName(g.getMidCategoryName());
				d.setSmallCategoryName(g.getSmallCategoryName());
			}
		}
	}

	private void processStockPrice(List<Stock> list) {
		if (list == null && list.size() == 0)
			return;
		List<String> goodsList = Lists.newArrayList();
		Set<String> set = Sets.newLinkedHashSet();

		for (Stock stock : list) {
			set.add(stock.getMatCode());
		}
		goodsList.addAll(set);

		List<Goods> priceList = stockDOMapper2.getGoodsPrice(goodsList);
		Map<String, Goods> goodsMap = getGoodsMap(priceList);
		for (Stock stock : list) {
			Goods g = goodsMap.get(stock.getMatCode());
			if (g != null) {
				stock.setQuotePrice(g.getQuotePrice());
			}
		}
	}

	private Map<String, Goods> getGoodsMap(List<Goods> priceList) {
		Map<String, Goods> goodsMap = Maps.newHashMap();
		if (priceList != null && priceList.size() > 0) {
			for (Goods goods : priceList) {
				goodsMap.put(goods.getMatCode(), goods);
			}
		}
		return goodsMap;
	}

//	@Override
//	public List<StockStat> inStockStatsPage(InStockReq req, Page page) {
//		LoggerUtil.info(logger, "详情页缺货门店统计：{0}, {1}", req, page);
//		Task task = taskMapper2.get_task_by_id(req.getTaskId());
//		if (task == null) {
//			LoggerUtil.info(logger, "找不到相关任务");
//		}
//
//		int count = stockDOMapper2.inStockStatsCount(req, getPeriod(task));
//		if (page != null) {
//			page.setTotalRecord(count);
//			LoggerUtil.info(logger, "详情页缺货门店统计，总数量：{0}", count);
//		}
//
//		List<StockStat> list = stockDOMapper2.inStockStatsPage(req, page, getPeriod(task));
//
//		return list;
//	}
//
//	@Override
//	public List<StockStat> outStockStatsPage(InStockReq req, Page page) {
//		LoggerUtil.info(logger, "详情页供货门店统计：{0}, {1}", req, page);
//		Task task = taskMapper2.get_task_by_id(req.getTaskId());
//		if (task == null) {
//			LoggerUtil.info(logger, "找不到相关任务");
//			return null;
//		}
//
//		int count = stockDOMapper2.outStockStatsCount(req, getPeriod(task));
//		if (page != null) {
//			page.setTotalRecord(count);
//			LoggerUtil.info(logger, "详情页供货门店统计，总数量：{0}", count);
//		}
//
//		List<StockStat> list = stockDOMapper2.outStockStatsPage(req, page, getPeriod(task));
//
//		return list;
//	}
//
//	@Override
//	public List<Stock> inStockDetailPage(InStockReq req, Page page) {
//		LoggerUtil.info(logger, "详情页供货门店明细：{0}, {1}", req, page);
//		Task task = taskMapper2.get_task_by_id(req.getTaskId());
//		if (task == null) {
//			LoggerUtil.info(logger, "找不到相关任务");
//			return null;
//		}
//
//		int count = stockDOMapper2.inStockDetailCount(req, getPeriod(task));
//		if (page != null) {
//			page.setTotalRecord(count);
//			LoggerUtil.info(logger, "详情页供货门店明细，总数量：{0}", count);
//		}
//
//		List<Stock> list = stockDOMapper2.inStockDetailPage(req, page, getPeriod(task));
//
//		Map<String, MatSize> sizeMap = getSizeMap();
//		if (list != null) {
//			for (Stock stock : list) {
//				MatSize matSize = sizeMap.get(stock.getSizeID());
//				if (matSize != null) {
//					stock.setSizeName(matSize.getSizeName());
//				}
//			}
//		}
//		return list;
//	}

	@Override
	public List<Category> getMidCategory(InStockReq req) {
		LoggerUtil.info(logger, "中类列表：{0}, {1}", req);
		Task task = taskMapper2.get_task_by_id(req.getTaskId());
		if (task == null) {
			LoggerUtil.info(logger, "找不到相关任务");
			return null;
		}
		return stockDOMapper2.getMidCategory(task.getTask_id(), getTypeSql(task));
	}

	@Override
	public List<Category> getSmallCategory(InStockReq req) {
		LoggerUtil.info(logger, "小类列表：{0}, {1}", req);
		Task task = taskMapper2.get_task_by_id(req.getTaskId());
		if (task == null) {
			LoggerUtil.info(logger, "找不到相关任务");
			return null;
		}
		return stockDOMapper2.getSmallCategory(req.getMidCategoryCode(), task.getTask_id(), getTypeSql(task));
	}

	@Override
	public List<TaskProgress> getTaskProgress(int taskId) {
		return stockDOMapper2.getTaskProgress(taskId);
	}

	private Map<String, MatSize> getSizeMap() {
		Map<String, MatSize> map = Maps.newHashMap();
		List<MatSize> list = allocationDOMapper2.getSizeList();
		if (list != null) {
			for (MatSize size : list) {
				map.put(size.getSizeId(), size);
			}
		}
		return map;
	}


//	@Override
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
	public List<Paramater> getSeasonList() {
		return stockDOMapper2.getSeasonList();
	}

	@Override
	public List<Paramater> getYearNoList() {
		return stockDOMapper2.getYearNoList();
	}

	@Override
	public List<Shop> getShopList() {
		//return ShopListCache2.getShopList();
		return Collections.emptyList();
	}

}
