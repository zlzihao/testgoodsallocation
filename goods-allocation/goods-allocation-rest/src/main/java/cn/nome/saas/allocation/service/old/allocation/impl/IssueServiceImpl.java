package cn.nome.saas.allocation.service.old.allocation.impl;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.old.allocation.*;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.repository.dao.allocation.ShopInfoDOMapper;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueUndoDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.StockDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO;
import cn.nome.saas.allocation.repository.old.allocation.entity.IssueUndoDO;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import cn.nome.saas.allocation.service.old.allocation.ProhibitedService;
import cn.nome.saas.allocation.task.CategorySkcDataTask;
import cn.nome.saas.allocation.task.IssueGoodsDataTask;
import cn.nome.saas.allocation.task.IssueOutStockRemainTask;
import cn.nome.saas.allocation.task.IssueUndoTask;
import cn.nome.saas.allocation.utils.IssueDayUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class IssueServiceImpl implements IssueService {
	private static Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

	@Autowired
	IssueDOMapper2 issueDOMapper2;

	@Autowired
	AllocationDOMapper2 allocationDOMapper2;

	@Autowired
	StockDOMapper2 stockDOMapper2;

	@Autowired
	IssueUndoDOMapper2 issueUndoDOMapper2;

	@Autowired
	ProhibitedService prohibitedService;

	@Autowired
	IssueRestService issueRestService;

	@Autowired
	ShopInfoDOMapper shopInfoDOMapper;

	private String TAB_GOODS_INFO = "goods_info";
	private String TAB_GOODS_INFO_VIEW = "goods_info_v";

	/**
	 * 计算理想库存
	 */
	private ThreadFactory issueThreadFactory = new ThreadFactoryBuilder().setNameFormat("issue-%d").build();
	private ExecutorService issueThreadFactoryPool = new ThreadPoolExecutor(4, 4,
			0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), issueThreadFactory);


	private String getTypeSql(int type) {
		// 服装
		String typeSql = "and Categorycode in ('M','W')";
//		String typeSql = "and Categorycode in ('M','W') and aa.ShopID in ('NM000193', 'NM000068', 'NM000084', 'NM000036', 'NM000076', 'NM000528')";
		if (type == 2) {
			// 百货
			typeSql = "and Categorycode not in ('M','W')";
//			typeSql = "and Categorycode not in ('M','W') and aa.ShopID in ('NM000193', 'NM000068', 'NM000084', 'NM000036', 'NM000076', 'NM000528')";
		}
		return typeSql;
	}

	/**
	 * 用于针对某些门店单独计算
	 * @param task
	 * @param period
	 * @param type
	 * @param prohibitedGoods
	 * @param shopIdList
	 */
	private void issueInStock(IssueTask task, int period, int type,
							 Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,List<String> shopIdList) {

		List<Stock> list = allocationDOMapper2.getIssueInStockList(this.getTypeSql(type), period,this.getIssueShopSql(shopIdList));

		if (list != null && list.size() > 0) {
			LoggerUtil.info(logger, "取数结果为：{0}", list.size());

			// 分批录入
			List<Stock> tempList = Lists.newArrayList();
			int count = 1;
			for (int i = 0; i < list.size(); i++) {
				Long totalStockQty = 0l;
				Stock stock = list.get(i);
				stock.setTaskId(task.getId());
				if (stock.getStockQty() == null || stock.getStockQty() < 0) {
					stock.setStockQty(0L);
				}
				totalStockQty = stock.getStockQty();
				if(stock.getPathStockQty() != null) {
					totalStockQty = totalStockQty + stock.getPathStockQty();
				}
				if(stock.getMoveQty() != null) {
					totalStockQty = totalStockQty + stock.getMoveQty();
				}
				stock.setTotalStockQty(totalStockQty);
//				// 是否为禁品判断
//				if (prohibitedService.checkIfIsProhibited(prohibitedGoods, stock.getShopID(), stock.getMatCode())) {
//					stock.setIsProhibited(1);
//				}
				Map<String, ProhibitedGoods> matCodeMap;ProhibitedGoods prohibitedGoodsDo;
				if ((matCodeMap = prohibitedGoods.get(stock.getShopID())) != null && (prohibitedGoodsDo = matCodeMap.get(stock.getMatCode())) != null) {
					stock.setRuleName(prohibitedGoodsDo.getRuleName());
					//若有保底数量时, 保存保底策略数量
					if (prohibitedGoodsDo.getMinQty() != null) {
						stock.setMinQty(prohibitedGoodsDo.getMinQty());
					} else {
						//保留禁配数量0与禁配标志
						stock.setMinQty(0);
						stock.setIsProhibited(1);
					}
				}

				stock.setIsEliminate(stock.getMatTypeName() == null ? 0 : "淘汰".equals(stock.getMatTypeName()) ? 1 : 0);

				tempList.add(stock);

				if (i == 5000 * count || i == list.size() - 1) {
					LoggerUtil.info(logger, "取数结果为： issueInStock addIssueInStock size{0}", tempList.size());
					if (tempList.size() > 0) {
						// 先处理价格
						issueDOMapper2.addIssueInStock(tempList);
					}
					tempList = Lists.newArrayList();
					++count;
				}
			}
		}

	}

	@Override
	public void issueInStock(IssueTask task, int period, int type,
							 Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,int forTest) {



		List<Stock> list = allocationDOMapper2.getIssueInStockList(this.getTypeSql(type), period,this.getIssueShopSql(forTest));



		if (list != null && list.size() > 0) {
			LoggerUtil.info(logger, "取数结果为：{0}", list.size());

			// 分批录入
			List<Stock> tempList = Lists.newArrayList();
			int count = 1;
			for (int i = 0; i < list.size(); i++) {
				Long totalStockQty = 0l;
				Stock stock = list.get(i);
				stock.setTaskId(task.getId());
				if (stock.getStockQty() == null || stock.getStockQty() < 0) {
					stock.setStockQty(0l);
				}
				totalStockQty = stock.getStockQty();
				if(stock.getPathStockQty() != null) {
					totalStockQty = totalStockQty + stock.getPathStockQty();
				}
				if(stock.getMoveQty() != null) {
					totalStockQty = totalStockQty + stock.getMoveQty();
				}
				stock.setTotalStockQty(totalStockQty);
//				// 是否为禁品判断
//				if (prohibitedService.checkIfIsProhibited(prohibitedGoods, stock.getShopID(), stock.getMatCode())) {
//					stock.setIsProhibited(1);
//				}
				Map<String, ProhibitedGoods> matCodeMap;ProhibitedGoods prohibitedGoodsDo;
				if ((matCodeMap = prohibitedGoods.get(stock.getShopID())) != null && (prohibitedGoodsDo = matCodeMap.get(stock.getMatCode())) != null) {
					stock.setRuleName(prohibitedGoodsDo.getRuleName());
					//若有保底数量时, 保存保底策略数量
					if (prohibitedGoodsDo.getMinQty() != null) {
						stock.setMinQty(prohibitedGoodsDo.getMinQty());
					} else {
						//保留禁配数量0与禁配标志
						stock.setMinQty(0);
						stock.setIsProhibited(1);
					}
				}

				stock.setIsEliminate(stock.getMatTypeName() == null ? 0 : "淘汰".equals(stock.getMatTypeName()) ? 1 : 0);

				tempList.add(stock);

				if (i == 5000 * count || i == list.size() - 1) {
					LoggerUtil.info(logger, "取数结果为： issueInStock addIssueInStock size{0}", tempList.size());
					if (tempList.size() > 0) {
						// 先处理价格
						issueDOMapper2.addIssueInStock(tempList);
					}
					tempList = Lists.newArrayList();
					++count;
				}
			}
		}

	}

	private String getMidName(Stock stock) {
		return stock.getShopID() + "-" + stock.getCategoryName() + "-" + stock.getMidCategoryName();
	}

	@Override
	public Map<String, Stock> getGoodsInfo() {
		List<Stock> list = issueDOMapper2.getGoodsInfo();
		Map<String, Stock> map = Maps.newHashMap();
		if (list != null) {
			for (Stock stock : list) {
				map.put(stock.getMatCode(), stock);
			}
		}
		return map;
	}

	/**
	 * 计算指定门店
	 * @param task
	 * @param prohibitedGoods
	 * @param shopIdList
	 */
	private void issueInNewSkcStock(IssueTask task, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,List<String> shopIdList) {
		List<Stock> newSkcList = allocationDOMapper2.getNewSkcList(this.getIssueShopSql(shopIdList));
		List<Stock> midCategorySaleAmtList = issueDOMapper2.getMidCategorySale(task.getId());
		LoggerUtil.info(logger, "新品查询结果为：{0},{1}", newSkcList.size(), midCategorySaleAmtList.size());

		Map<String, Stock> goodsMap = this.getGoodsInfo();
		Map<String, Stock> shopMap = this.getShopAvgMap(task);
		if (newSkcList != null) {
			if (midCategorySaleAmtList != null) {
				Map<String, Stock> midMap = Maps.newHashMap();
				for (Stock temp : midCategorySaleAmtList) {
					midMap.put(getMidName(temp), temp);
				}

				for (Stock temp : newSkcList) {
					Stock goods = goodsMap.get(temp.getMatCode());
					if (goods != null) {
						temp.setMidCategoryCode(goods.getMidCategoryCode());
						temp.setMidCategoryName(goods.getMidCategoryName());
						temp.setCategoryCode(goods.getCategoryCode());
						temp.setCategoryName(goods.getCategoryName());
					}
					temp.setTaskId(task.getId());
					temp.setIsNew(1);
					temp.setIsEliminate(temp.getMatTypeName() == null ? 0 : "淘汰".equals(temp.getMatTypeName()) ? 1 : 0);
					String key = getMidName(temp);
					Stock mid = midMap.get(key);
					if (mid != null) {
						temp.setAvgSaleQty(mid.getAvgSaleQty());
					} else {
						// 如果中类没有日均销数据，就按门店平均日均销来设置
						Stock shop = shopMap.get(temp.getShopID());
						if (shop != null) {
							temp.setAvgSaleQty(shop.getAvgSaleQty());
						} else {
							temp.setAvgSaleQty(0.5d);
						}
					}
//					// 是否为禁品判断
//					if (prohibitedService.checkIfIsProhibited(prohibitedGoods, temp.getShopID(), temp.getMatCode())) {
//						temp.setIsProhibited(1);
//					}
					Map<String, ProhibitedGoods> matCodeMap;ProhibitedGoods prohibitedGoodsDo;
					if ((matCodeMap = prohibitedGoods.get(temp.getShopID())) != null && (prohibitedGoodsDo = matCodeMap.get(temp.getMatCode())) != null) {
						temp.setRuleName(prohibitedGoodsDo.getRuleName());
						//若有保底数量时, 保存保底策略数量
						if (prohibitedGoodsDo.getMinQty() != null) {
							temp.setMinQty(prohibitedGoodsDo.getMinQty());
						} else {
							//保留禁配数量0与禁配标志
							temp.setMinQty(0);
							temp.setIsProhibited(1);
						}
					}
					temp.setStockQty(0L);
					temp.setPathStockQty(0L);
					temp.setMoveQty(temp.getMoveQty() == null ? 0L : temp.getMoveQty());

					long totalStockQty = temp.getStockQty();
					if (temp.getMoveQty() != null) {
						totalStockQty += temp.getMoveQty();
					}
					temp.setTotalStockQty(totalStockQty);
				}

				// 分批录入
				List<Stock> tempList = Lists.newArrayList();
				int count = 1;
				for (int i = 0; i < newSkcList.size(); i++) {
					Stock stock = newSkcList.get(i);
					tempList.add(stock);

					if (i == 5000 * count || i == newSkcList.size() - 1) {
						LoggerUtil.info(logger, "取数结果为： issueInStock addIssueNewInStock size{0}", tempList.size());
						if (tempList.size() > 0) {
							issueDOMapper2.addIssueInStock(tempList);
						}
						tempList = Lists.newArrayList();
						++count;
					}
				}

			}
		}

	}

	@Override
	public void issueInNewSkcStock(IssueTask task, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,int forTest) {
		List<Stock> newSkcList = allocationDOMapper2.getNewSkcList(this.getIssueShopSql(forTest));
		List<Stock> midCategorySaleAmtList = issueDOMapper2.getMidCategorySale(task.getId());
		LoggerUtil.info(logger, "新品查询结果为：{0},{1}", newSkcList.size(), midCategorySaleAmtList.size());

		Map<String, Stock> goodsMap = this.getGoodsInfo();
		Map<String, Stock> shopMap = this.getShopAvgMap(task);
		if (newSkcList != null) {
			if (midCategorySaleAmtList != null) {
				Map<String, Stock> midMap = Maps.newHashMap();
				for (Stock temp : midCategorySaleAmtList) {
					midMap.put(getMidName(temp), temp);
				}

				for (Stock temp : newSkcList) {
					Stock goods = goodsMap.get(temp.getMatCode());
					if (goods != null) {
						temp.setMidCategoryCode(goods.getMidCategoryCode());
						temp.setMidCategoryName(goods.getMidCategoryName());
						temp.setCategoryCode(goods.getCategoryCode());
						temp.setCategoryName(goods.getCategoryName());
					}
					temp.setTaskId(task.getId());
					temp.setIsNew(1);
					temp.setIsEliminate(temp.getMatTypeName() == null ? 0 : "淘汰".equals(temp.getMatTypeName()) ? 1 : 0);
					String key = getMidName(temp);
					Stock mid = midMap.get(key);
					if (mid != null) {
						temp.setAvgSaleQty(mid.getAvgSaleQty());
					} else {
						// 如果中类没有日均销数据，就按门店平均日均销来设置
						Stock shop = shopMap.get(temp.getShopID());
						if (shop != null) {
							temp.setAvgSaleQty(shop.getAvgSaleQty());
						} else {
							temp.setAvgSaleQty(0.5d);
						}
					}
//					// 是否为禁品判断
//					if (prohibitedService.checkIfIsProhibited(prohibitedGoods, temp.getShopID(), temp.getMatCode())) {
//						temp.setIsProhibited(1);
//					}
					Map<String, ProhibitedGoods> matCodeMap;ProhibitedGoods prohibitedGoodsDo;
					if ((matCodeMap = prohibitedGoods.get(temp.getShopID())) != null && (prohibitedGoodsDo = matCodeMap.get(temp.getMatCode())) != null) {
						temp.setRuleName(prohibitedGoodsDo.getRuleName());
						//若有保底数量时, 保存保底策略数量
						if (prohibitedGoodsDo.getMinQty() != null) {
							temp.setMinQty(prohibitedGoodsDo.getMinQty());
						} else {
							//保留禁配数量0与禁配标志
							temp.setMinQty(0);
							temp.setIsProhibited(1);
						}
					}
					temp.setStockQty(0L);
					temp.setPathStockQty(0L);
					temp.setMoveQty(temp.getMoveQty() == null ? 0L : temp.getMoveQty());

					long totalStockQty = temp.getStockQty();
					if (temp.getMoveQty() != null) {
						totalStockQty += temp.getMoveQty();
					}
					temp.setTotalStockQty(totalStockQty);
				}

				// 分批录入
				List<Stock> tempList = Lists.newArrayList();
				int count = 1;
				for (int i = 0; i < newSkcList.size(); i++) {
					Stock stock = newSkcList.get(i);
					tempList.add(stock);

					if (i == 5000 * count || i == newSkcList.size() - 1) {
						LoggerUtil.info(logger, "取数结果为： issueInStock addIssueNewInStock size{0}", tempList.size());
						if (tempList.size() > 0) {
							issueDOMapper2.addIssueInStock(tempList);
						}
						tempList = Lists.newArrayList();
						++count;
					}
				}

			}
		}

	}

	@Override
	public void issueOutStock(IssueTask task) {
		List<Stock> list = allocationDOMapper2.getIssueOutStockList();
		if (list != null && list.size() > 0) {
			LoggerUtil.info(logger, "取数结果为：{0}", list.size());

			// 分批录入
			List<Stock> tempList = Lists.newArrayList();
			int count = 1;
			for (int i = 0; i < list.size(); i++) {
				Stock stock = list.get(i);
				stock.setTaskId(task.getId());
				tempList.add(stock);

				if (i == 5000 * count || i == list.size() - 1) {
					LoggerUtil.info(logger, "取数结果为： issueOutStock addIssueOutStock size{0}", tempList.size());
					if (tempList.size() > 0) {

						issueDOMapper2.addIssueOutStock(tempList);
					}
					tempList = Lists.newArrayList();
					++count;
				}
			}
		}
	}

	@Override
	public void processEnoughStock(IssueTask task) {
		List<Stock> list = issueDOMapper2.getEnoughStockSku(task.getId());
		if (list != null) {
			LoggerUtil.info(logger, "总仓足够分配的SKU数量：{0}", list.size());
			for (Stock stock : list) {
				LoggerUtil.info(logger, "处理配发：{0}", stock);
				issueDOMapper2.addEnoughStockSku(task.getId(), stock.getMatCode(), stock.getSizeID());
			}
		}
	}

	@Override
	public void processNotEnoughStock(IssueTask task) {
		List<Stock> list = issueDOMapper2.getNotEnoughStockSku(task.getId());
		if (list != null) {
			LoggerUtil.info(logger, "总仓不足够分配的SKU数量：{0}", list.size());
			for (Stock stock : list) {
				LoggerUtil.info(logger, "处理配发：{0}", stock);
				issueDOMapper2.addNotEnoughStockSku(task.getId(), stock.getMatCode(), stock.getSizeID(),
						stock.getStockQty());
			}
		}
	}

	public void issueProcessV2(IssueTask task, List<String> shopIdList) {
		// 1.读取禁配规则
		Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = prohibitedService.getProhibitedGoods();
		// 2.抽取老品配发数据
		this.issueInStock(task, 7, 2, prohibitedGoods,shopIdList);
		// 3.抽取新品配发数据
		this.issueInNewSkcStock(task, prohibitedGoods,shopIdList);
		// 4.抽取仓库有库存的商品数据
		this.issueOutStock(task);
		// 5.计算门店NM00001商品中类库存数据
		issueDOMapper2.midMidCategoryQty(task.getId(), "NM00001", TAB_GOODS_INFO);
		// 6.计算门店NM00001每个sku的需求量
		issueDOMapper2.addNeedSkuStock(task.getId(), "NM00001", TAB_GOODS_INFO);
		// 7.总仓库存足够供货的商品处理
		this.processEnoughStock(task);
		// 8.总仓库存不足供货商品处理：按比例分配
		this.processNotEnoughStock(task);
		List<String> shopInfoIds = new ArrayList<>();
		List<String> shopIds = issueRestService.issueInStockShopIds(task.getId());
		// 9. 处理配发商品信息数据
		this.processIssueGoodsData(task.getId(), shopIds);
		// 10.处理类目skc数据
		this.processCategorySkcCount(task.getId(), shopIds);
		// 11.处理配发后剩余库存
		this.processRemainStock(task.getId());
	}


	@Override
	public void issueProcess(IssueTask task, List<String> shopIdList) {

		Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = prohibitedService.getProhibitedGoods();

		this.issueInStock(task, 7, 2, prohibitedGoods,shopIdList);

		this.issueInNewSkcStock(task, prohibitedGoods,shopIdList);

		this.issueOutStock(task);

		List<ShopInfoDo> shopInfoDos = issueDOMapper2.shops().stream().filter(shopInfoDo -> shopIdList.contains(shopInfoDo.getShopID())).collect(Collectors.toList());
		Map<Integer, List<ShopInfoDo>> groupbyHasChild = shopInfoDos.stream().collect(Collectors.groupingBy(ShopInfoDo::getHaveChild));
		List<ShopInfoDo> hasChildShops = groupbyHasChild.get(0);
		List<ShopInfoDo> noHasChildShops = groupbyHasChild.get(1);

		boolean hasChildShopFlag = (hasChildShops != null && !hasChildShops.isEmpty());
		boolean noHasChildShopFlag = (noHasChildShops != null && !noHasChildShops.isEmpty());

		List<String> hasChildShopIds = null;
		if (hasChildShopFlag) {
			hasChildShopIds = hasChildShops.stream().map(shopInfoDo -> shopInfoDo.getShopID()).collect(Collectors.toList());
			logger.info("hasChildShopIds size:{}", hasChildShopIds.size());
		}
		List<String> noHasChildShopIds = null;
		if (noHasChildShopFlag) {
			noHasChildShopIds = noHasChildShops.stream().map(shopInfoDo -> shopInfoDo.getShopID()).collect(Collectors.toList());
			logger.info("noHasChildShopIds size:{}", noHasChildShopIds.size());
		}

		AtomicInteger midMidCategoryQtyCount = new AtomicInteger(0);
		if (hasChildShopFlag) {
			for (String shopId : hasChildShopIds) {
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.midMidCategoryQty(task.getId(), shopId, TAB_GOODS_INFO);
						midMidCategoryQtyCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[midMidCategoryQty] catch exception hasChildShopFlag shopid:{0}", shopId);
					}
				});
			}
		}
		if (noHasChildShopFlag) {
			//hasChild非0则归纳为替换的门店
			for (String shopId : noHasChildShopIds) {
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.midMidCategoryQty(task.getId(), shopId, TAB_GOODS_INFO_VIEW);
						midMidCategoryQtyCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[midMidCategoryQty] catch exception noHasChildShopFlag shopid:{0}", shopId);
					}
				});
			}
		}
		while (hasChildShopIds.size() + noHasChildShopIds.size() != midMidCategoryQtyCount.intValue()) {
			try {
				Thread.sleep(5000);
				LoggerUtil.info(logger, "[midMidCategoryQty] hasChildShopIds.size() + noHasChildShopIds.size() != midMidCategoryQtyCount.intValue() sleep 5 s," +
						" hasChildShopIds.size() + noHasChildShopIds.size()={0}, midMidCategoryQtyCount.intValue()={1}", hasChildShopIds.size() + noHasChildShopIds.size(), midMidCategoryQtyCount.intValue());
			} catch (InterruptedException e) {

			}
		}

		// 生成商品需求量
		//List<String> list = issueDOMapper2.getShopIdList();
		AtomicInteger addNeedSkuStockCount = new AtomicInteger(0);
		int threadCount = 0;
		for (ShopInfoDo shopInfoDo : shopInfoDos) {
			if (shopInfoDo.getHaveChild() == 0) {
				threadCount++;
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.addNeedSkuStock(task.getId(), shopInfoDo.getShopID(), TAB_GOODS_INFO);
						addNeedSkuStockCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[addNeedSkuStock] catch exception shopInfoDo.getHaveChild() == 0 shopid:{0}", shopInfoDo.getShopID());
					}
				});
			}
			if (shopInfoDo.getHaveChild() == 1) {
				threadCount++;
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.addNeedSkuStock(task.getId(), shopInfoDo.getShopID(), TAB_GOODS_INFO_VIEW);
						addNeedSkuStockCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[addNeedSkuStock] catch exception shopInfoDo.getHaveChild() == 1 shopid:{0}", shopInfoDo.getShopID());
					}
				});
			}
			logger.debug("addNeedSkuStock shopId:{}", shopInfoDo.getShopID());
		}
		while (threadCount != addNeedSkuStockCount.intValue()) {
			try {
				Thread.sleep(5000);
				LoggerUtil.info(logger, "[addNeedSkuStock] shopInfoDos.size() != addNeedSkuStockCount.intValue() sleep 5 s," +
						" shopInfoDos.size()={0}, addNeedSkuStockCount.intValue()={1}", shopInfoDos.size(), addNeedSkuStockCount.intValue());
			} catch (InterruptedException e) {

			}
		}

		// 总仓库存足够供货的商品处理
		this.processEnoughStock(task);

		// 总仓库存不足供货商品处理：按比例分配
		this.processNotEnoughStock(task);

		List<String> shopInfoIds = shopInfoDos.stream().map(ShopInfoDo::getShopID).collect(Collectors.toList());
		this.processIssueUndo(task.getId(), shopInfoIds);

		List<String> shopIds = issueRestService.issueInStockShopIds(task.getId());

		this.processIssueGoodsData(task.getId(), shopIds);

		this.processCategorySkcCount(task.getId(), shopIds);

		this.processRemainStock(task.getId());
	}

	@Override
	public void issueProcess(IssueTask task) {

		logger.info("配发处理开始,任务ID:{}", task.getId());
		Date start = new Date();
		// 取到禁品数据
		Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = prohibitedService.getProhibitedGoods();
//		Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = Maps.newHashMap();
		// 需配发商品导入
		Date date1 = new Date();
		//服装的配发可以不用计算，也不用展示出来
//		this.issueInStock(task, 7, 1, prohibitedGoods,task.getForTest());
		this.issueInStock(task, 7, 2, prohibitedGoods,task.getForTest());
		LoggerUtil.info(logger, "0,配发处理，需配发商品导入：{0}分钟", this.getMinute(date1));
		// 新品导入
		date1 = new Date();
		this.issueInNewSkcStock(task, prohibitedGoods,task.getForTest());
		LoggerUtil.info(logger, "1,配发处理，需配发新商品导入：{0}分钟", this.getMinute(date1));
		// 更新禁品
		// date1 = new Date();
		// this.prohibitedGoods(task);
		// LoggerUtil.info(logger, "2,配发处理，更新禁品：{0}分钟", this.getMinute(date1));
		// 总仓库存导入
		date1 = new Date();
		this.issueOutStock(task);
		LoggerUtil.info(logger, "3,配发处理，总仓库存导入：{0}分钟", this.getMinute(date1));

		// 中类数量生成
		date1 = new Date();
		//获取shopInfo全门店数据
		//List<ShopInfoDo> shopInfoDos = issueDOMapper2.getShops();
		List<ShopInfoDo> shopInfoDos = issueDOMapper2.shops();
		Map<Integer, List<ShopInfoDo>> groupbyHasChild = shopInfoDos.stream().collect(Collectors.groupingBy(ShopInfoDo::getHaveChild));
		List<ShopInfoDo> hasChildShops = groupbyHasChild.get(0);
		List<ShopInfoDo> noHasChildShops = groupbyHasChild.get(1);

		boolean hasChildShopFlag = (hasChildShops != null && !hasChildShops.isEmpty());
		boolean noHasChildShopFlag = (noHasChildShops != null && !noHasChildShops.isEmpty());

		List<String> hasChildShopIds = null;
		if (hasChildShopFlag) {
			hasChildShopIds = hasChildShops.stream().map(shopInfoDo -> shopInfoDo.getShopID()).collect(Collectors.toList());
			logger.info("hasChildShopIds size:{}", hasChildShopIds.size());
		}
		List<String> noHasChildShopIds = null;
		if (noHasChildShopFlag) {
			noHasChildShopIds = noHasChildShops.stream().map(shopInfoDo -> shopInfoDo.getShopID()).collect(Collectors.toList());
			logger.info("noHasChildShopIds size:{}", noHasChildShopIds.size());
		}

		AtomicInteger midMidCategoryQtyCount = new AtomicInteger(0);
		if (hasChildShopFlag) {
			for (String shopId : hasChildShopIds) {
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.midMidCategoryQty(task.getId(), shopId, TAB_GOODS_INFO);
						midMidCategoryQtyCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[midMidCategoryQty] catch exception hasChildShopFlag shopid:{0}", shopId);
					}
				});
			}
		}
		if (noHasChildShopFlag) {
			//hasChild非0则归纳为替换的门店
			for (String shopId : noHasChildShopIds) {
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.midMidCategoryQty(task.getId(), shopId, TAB_GOODS_INFO_VIEW);
						midMidCategoryQtyCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[midMidCategoryQty] catch exception noHasChildShopFlag shopid:{0}", shopId);
					}
				});
			}
		}
		while (hasChildShopIds.size() + noHasChildShopIds.size() != midMidCategoryQtyCount.intValue()) {
			try {
				Thread.sleep(5000);
				LoggerUtil.info(logger, "[midMidCategoryQty] hasChildShopIds.size() + noHasChildShopIds.size() != midMidCategoryQtyCount.intValue() sleep 5 s," +
						" hasChildShopIds.size() + noHasChildShopIds.size()={0}, midMidCategoryQtyCount.intValue()={1}", hasChildShopIds.size() + noHasChildShopIds.size(), midMidCategoryQtyCount.intValue());
			} catch (InterruptedException e) {

			}
		}
		LoggerUtil.info(logger, "4,配发处理，中类数量生成：{0}分钟", this.getMinute(date1));

		// 生成商品需求量
		date1 = new Date();
		//List<String> list = issueDOMapper2.getShopIdList();
		AtomicInteger addNeedSkuStockCount = new AtomicInteger(0);
		int threadCount = 0;
		for (ShopInfoDo shopInfoDo : shopInfoDos) {
			if (shopInfoDo.getHaveChild() == 0) {
				threadCount++;
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.addNeedSkuStock(task.getId(), shopInfoDo.getShopID(), TAB_GOODS_INFO);
						addNeedSkuStockCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[addNeedSkuStock] catch exception shopInfoDo.getHaveChild() == 0 shopid:{0}", shopInfoDo.getShopID());
					}
				});
			}
			if (shopInfoDo.getHaveChild() == 1) {
				threadCount++;
				issueThreadFactoryPool.submit(()-> {
					try {
						issueDOMapper2.addNeedSkuStock(task.getId(), shopInfoDo.getShopID(), TAB_GOODS_INFO_VIEW);
						addNeedSkuStockCount.getAndIncrement();
					} catch (Exception e) {
						LoggerUtil.error(e, logger, "[addNeedSkuStock] catch exception shopInfoDo.getHaveChild() == 1 shopid:{0}", shopInfoDo.getShopID());
					}
				});
			}
			logger.debug("addNeedSkuStock shopId:{}", shopInfoDo.getShopID());
		}
		while (threadCount != addNeedSkuStockCount.intValue()) {
			try {
				Thread.sleep(5000);
				LoggerUtil.info(logger, "[addNeedSkuStock] shopInfoDos.size() != addNeedSkuStockCount.intValue() sleep 5 s," +
						" shopInfoDos.size()={0}, addNeedSkuStockCount.intValue()={1}", shopInfoDos.size(), addNeedSkuStockCount.intValue());
			} catch (InterruptedException e) {

			}
		}
		LoggerUtil.info(logger, "5,配发处理，生成商品需求量：{0}分钟", this.getMinute(date1));

		// 总仓库存足够供货的商品处理
		date1 = new Date();
		this.processEnoughStock(task);
		LoggerUtil.info(logger, "6,配发处理，总仓库存足够供货的商品处理：{0}分钟", this.getMinute(date1));
		date1 = new Date();
		// 总仓库存不足供货商品处理：按比例分配
		this.processNotEnoughStock(task);
		LoggerUtil.info(logger, "7,配发处理，总仓库存不足供货商品处理：{0}分钟", this.getMinute(date1));

		List<String> shopInfoIds = shopInfoDos.stream().map(ShopInfoDo::getShopID).collect(Collectors.toList());
		this.processIssueUndo(task.getId(), shopInfoIds);
		LoggerUtil.info(logger, "8,配发处理，未参与配发商品处理：{0}分钟", this.getMinute(date1));

		List<String> shopIds = issueRestService.issueInStockShopIds(task.getId());

		this.processIssueGoodsData(task.getId(), shopIds);
		LoggerUtil.info(logger, "9,配发处理，生成商品信息表：{0}分钟", this.getMinute(date1));

		this.processCategorySkcCount(task.getId(), shopIds);
		LoggerUtil.info(logger, "10,配发处理，生成分类SKC数量统计表：{0}分钟", this.getMinute(date1));

		this.processRemainStock(task.getId());
		LoggerUtil.info(logger, "11,配发处理，生成总仓库存剩余表：{0}分钟", this.getMinute(date1));

		logger.info("配发处理结束,任务ID:{},耗时:{} 分钟", task.getId(), getMinute(start));
	}

	/**
	 * 计算仓库总库存剩余表
	 * @param taskId
	 * @return
	 */
	@Override
	public Integer processRemainStock(int taskId) {
		List<IssueOutStockDO> issueOutStocks = issueRestService.issueOutStock(taskId);
		IssueOutStockRemainTask task = new IssueOutStockRemainTask(taskId, 0, issueOutStocks.size(), issueOutStocks, this);
		ForkJoinPool pool = new ForkJoinPool(4);
		Integer rst = pool.invoke(task);
		logger.info("PROCESS REMAIN STOCK DONE! taskId:{},rst:{}", taskId, rst);
		pool.shutdown();
		return rst;
	}

	@Override
	public Integer processCategorySkcCount(int taskId, List<String> shopIds) {
		shopIds = getInStockShopIds(taskId, shopIds);
		//大类数据
		CategorySkcDataTask task = new CategorySkcDataTask(shopIds, taskId, 0, shopIds.size(), this);
		ForkJoinPool pool = new ForkJoinPool(4);
		Integer rst = pool.invoke(task);
		logger.info("PROCESS CATEGORY SKC COUNT DONE! taskId:{},rst:{}", taskId, rst);
		pool.shutdown();
		return rst;
	}

	/**
	 * 总仓库存减去分配后的剩余库存
	 *
	 * @param taskId
	 * @param issueOutStockDO
	 * @return
	 */
	@Override
	public int insertStockRemainData(int taskId, IssueOutStockDO issueOutStockDO) {
		//配发明细占用库存
		IssueDetailDistStock detailDistStock = issueRestService.issueDeatilDistStock(taskId, issueOutStockDO.getMatCode(), issueOutStockDO.getSizeID());
		BigDecimal distStock = convertIssueDetailDistQty(detailDistStock);
		BigDecimal totalStockQty = new BigDecimal(issueOutStockDO.getStockQty());
		IssueOutStockRemainDo remainDo = new IssueOutStockRemainDo();
		remainDo.setTaskId(taskId);
//		logger.info("old remainStock:{}",remainDo.getStockQty());
		BigDecimal remainStock = totalStockQty.subtract(distStock);
		//小于0
		if (remainStock.compareTo(BigDecimal.ZERO) == -1) {
			remainStock = new BigDecimal(0);
			logger.warn("insertStockRemainData remainStock < 0");
		}
		remainDo.setStockQty(remainStock);
//		logger.info("new remainStock:{}",remainDo.getStockQty());
		remainDo.setMatCode(issueOutStockDO.getMatCode());
		remainDo.setSizeID(issueOutStockDO.getSizeID());
		issueDOMapper2.addIssueRemainStock(remainDo);
		return 1;
	}

	private BigDecimal convertIssueDetailDistQty(IssueDetailDistStock detailDistStock) {
		BigDecimal distStock;
		if (detailDistStock == null || detailDistStock.getTotalDistStockQty() == null){
			distStock = new BigDecimal(0);
		}else {
			distStock = detailDistStock.getTotalDistStockQty();
		}
		return distStock;
	}

	/**
	 * 释放库存 = 剩余库存 + 已分配
	 *
	 * @param taskId
	 * @param shopId
	 * @param remainDo
	 * @return
	 */
	@Override
	public int insertStockRemainFreedData(int taskId, String shopId, IssueOutStockRemainDo remainDo) {
		IssueDetailDistStock detailDistStock = issueRestService.issueDeatilShopDistStock(taskId, shopId, remainDo.getMatCode(), remainDo.getSizeID());

		IssueOutStockRemainDo remainStock = new IssueOutStockRemainDo();
		remainStock.setStatus(Constant.STATUS_RECALC);
		remainStock.setMatCode(remainDo.getMatCode());
		remainStock.setSizeID(remainDo.getSizeID());
		remainStock.setTaskId(taskId);

		BigDecimal freedRemainStock = remainDo.getStockQty();
		if (detailDistStock != null) {
			freedRemainStock = remainDo.getStockQty().add(detailDistStock.getTotalDistStockQty());
			if (freedRemainStock.compareTo(BigDecimal.ZERO) == -1) {
				freedRemainStock = new BigDecimal(0);
				logger.warn("insertStockRemainFreedData freedRemainStock < 0");
			}
		}
		remainStock.setStockQty(freedRemainStock);
		issueDOMapper2.addIssueRemainStock(remainStock);
		return 1;
	}

	@Override
	public int deductStockRemainData(int taskId, String shopId, IssueDetailDistStock detailDistStock) {
		IssueOutStockRemainDo remainStockDo = issueRestService.getRecalcRemainStock(taskId, detailDistStock.getMatCode(), detailDistStock.getSizeID());
		if (remainStockDo == null){
			logger.error("deductStockRemainData remainStockDo Null,matcode:{},sizeId:{}",detailDistStock.getMatCode(),detailDistStock.getSizeID());
			return 0;
		}
		BigDecimal remainStockQty = remainStockDo.getStockQty().subtract(detailDistStock.getTotalDistStockQty());
		//小于0
		if (remainStockQty.compareTo(BigDecimal.ZERO) == -1) {
			remainStockQty = new BigDecimal(0);
			logger.warn("deductStockRemainData remainStockQty < 0");
		}
		return issueDOMapper2.deductStockRemain(remainStockQty, remainStockDo.getID());
	}

	@Override
	public void updateIssueDays() {
		List<cn.nome.saas.allocation.model.issue.ShopInfoData> shopInfos = shopInfoDOMapper.selectByPage(new HashMap<>());
		int rst = 0;
		for (cn.nome.saas.allocation.model.issue.ShopInfoData shopInfo : shopInfos) {
			cn.nome.saas.allocation.model.issue.ShopInfoData data = new cn.nome.saas.allocation.model.issue.ShopInfoData();
			data.setID(shopInfo.getID());
			//data.setIssueDay(IssueDayUtil.getIssueDay(shopInfo.getRoadDay(), shopInfo.getIssueTime(), 0));
			data.setIssueDay(IssueDayUtil.getIssueDayV2(DateUtil.getCurrentDate(),shopInfo.getRoadDay(), shopInfo.getIssueTime()));
			rst = shopInfoDOMapper.updateById(data);
//			logger.info("updateIssueDays shopId:{},rst:{}", shopInfo.getShopID(), rst);
		}
	}

	@Override
	public void updateIssueDaysByShopId(String shopId) {
		List<cn.nome.saas.allocation.model.issue.ShopInfoData> shopInfos = shopInfoDOMapper.selectByPage(new HashMap<>());
		int rst = 0;
		for (cn.nome.saas.allocation.model.issue.ShopInfoData shopInfo : shopInfos) {
			if (shopId.equals(shopInfo.getShopID())) {
				cn.nome.saas.allocation.model.issue.ShopInfoData data = new cn.nome.saas.allocation.model.issue.ShopInfoData();
				data.setID(shopInfo.getID());
				//data.setIssueDay(IssueDayUtil.getIssueDay(shopInfo.getRoadDay(), shopInfo.getIssueTime(), 0));
				data.setIssueDay(IssueDayUtil.getIssueDayV2(DateUtil.getCurrentDate(),shopInfo.getRoadDay(), shopInfo.getIssueTime()));
				rst = shopInfoDOMapper.updateById(data);
//				logger.info("updateIssueDays shopId:{},rst:{}", shopInfo.getShopID(), rst);
				break;
			}
		}
	}

	@Override
	public void updateIssueDaysByDate(Calendar calendar) {
		List<cn.nome.saas.allocation.model.issue.ShopInfoData> shopInfos = shopInfoDOMapper.selectByPage(new HashMap<>());
		int rst = 0;
		for (cn.nome.saas.allocation.model.issue.ShopInfoData shopInfo : shopInfos) {
			cn.nome.saas.allocation.model.issue.ShopInfoData data = new cn.nome.saas.allocation.model.issue.ShopInfoData();
			data.setID(shopInfo.getID());
			//data.setIssueDay(IssueDayUtil.getIssueDay(shopInfo.getRoadDay(), shopInfo.getIssueTime(), 0));
			data.setIssueDay(IssueDayUtil.getIssueDayV2(calendar.getTime(),shopInfo.getRoadDay(), shopInfo.getIssueTime()));
			rst = shopInfoDOMapper.updateById(data);
//			logger.info("updateIssueDays shopId:{},rst:{}", shopInfo.getShopID(), rst);
		}
	}

	private BigDecimal convertIssueRemainStockQty(IssueOutStockRemainDo remainStock) {
		if (remainStock == null || remainStock.getStockQty() == null){
			return new BigDecimal(0);
		}
		return remainStock.getStockQty();
	}

	@Override
	public int batchInsertRecalcUndoData(int taskId, String shopId) {
		List<IssueUndoData> issueUndoData = issueDOMapper2.getIssueRecalcUndoData(taskId, shopId);
		if (issueUndoData == null || issueUndoData.isEmpty()) {
			return 0;
		}

		Map<String, BigDecimal> percentMap = getMidCategoryPercentAvgSaleQty(taskId, shopId,Constant.STATUS_RECALC);
		List<Stock> stocks = issueDOMapper2.getIssueNeedStockList(taskId, shopId, Constant.STATUS_RECALC);

		//Map<String, BigDecimal> needStockMap = stocks.stream().collect(Collectors.toMap(stock -> stock.getMatCode() + "_" + stock.getSizeID(), Stock::getPercentCategory));
		Map<String, List<BigDecimal>> needStockMap =stocks.stream().collect(Collectors.groupingBy(stock -> stock.getMatCode() + "_" + stock.getSizeID(),Collectors.mapping(Stock::getPercentCategory,Collectors.toList())));

		List<IssueUndoDO> issueUndos = convertIssueUndoData(issueUndoData, percentMap, needStockMap);
		int rst = issueUndoDOMapper2.batchInsertTab(issueUndos);
		logger.debug("batchInsertUndoData shopId:{},rst:{}", shopId, rst);
		return 0;
	}

	private Map<String, BigDecimal> getMidCategoryPercentAvgSaleQty(int taskId, String shopId, int status) {
		List<IssueMidCategoryQtyDo> list = issueRestService.getMidCategoryPercentAvgSaleQty(taskId, shopId,status);
		if (list == null || list.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, BigDecimal> map = new HashMap<>();
		Iterator<IssueMidCategoryQtyDo> itr = list.iterator();
		while (itr.hasNext()) {
			IssueMidCategoryQtyDo midCategoryQtyDo = itr.next();
			map.put(midCategoryQtyDo.getCategoryName() + midCategoryQtyDo.getMidCategoryName(), midCategoryQtyDo.getAvgSaleQty());
		}
		return map;
	}

	@Override
	public int batchInsertRecalcGoodsData(int taskId, String shopID) {
		int rst = 0;
		List<IssueGoodsData> issueGoodsData = issueRestService.getRecalcIssueGoodsData(taskId, shopID);
		if (issueGoodsData == null || issueGoodsData.isEmpty()) {
			logger.info("batchInsertRecalcGoodsData getIssueGoodsData shopID:{},rst:{}", shopID, rst);
			return rst;
		}
		Iterator<IssueGoodsData> itr = issueGoodsData.iterator();
		Map<String, IssueGoodsData> matCodeSizeIdDataMap = new HashMap<>();
		Map<String, IssueGoodsData> matCodeDataMap = new HashMap<>();
		Set<String> fzMatcodes = new HashSet<>();
		Set<String> bhMatcodes = new HashSet<>();

		Set<String> matcodes = new HashSet<>();
		Set<String> sizeIds = new HashSet<>();

		while (itr.hasNext()) {
			IssueGoodsData item = itr.next();

			item.setStatus(Constant.STATUS_RECALC);

			matcodes.add(item.getMatCode());
			sizeIds.add(item.getSizeID());

			matCodeSizeIdDataMap.put(item.getMatCode() + item.getSizeID(), item);

			matCodeDataMap.put(item.getMatCode(), item);

			if (isFZCategoryCode(item.getCategoryCode())){
				fzMatcodes.add(item.getMatCode());
			}else {
				bhMatcodes.add(item.getMatCode());
			}
		}
		//28天
		List<SaleQtyData> saleQty28Data = allocationDOMapper2.getSale28(shopID, matcodes, sizeIds);
		if (saleQty28Data != null && !saleQty28Data.isEmpty()) {
			Iterator<SaleQtyData> itr28 = saleQty28Data.iterator();
			while (itr28.hasNext()) {
				SaleQtyData saleQtyData = itr28.next();
				IssueGoodsData goodsData = matCodeSizeIdDataMap.get(saleQtyData.getMatCode() + saleQtyData.getSizeId());
				if (goodsData != null) {
					goodsData.setSaleQty28(new BigDecimal(saleQtyData.getSaleQty()));
				}
			}
		}
		//7天
		List<SaleQtyData> saleQty7Data = allocationDOMapper2.getSale7(shopID, matcodes, sizeIds);
		if (saleQty7Data != null && !saleQty7Data.isEmpty()) {
			Iterator<SaleQtyData> itr7 = saleQty7Data.iterator();
			while (itr7.hasNext()) {
				SaleQtyData saleQtyData = itr7.next();
				IssueGoodsData goodsData = matCodeSizeIdDataMap.get(saleQtyData.getMatCode() + saleQtyData.getSizeId());
				if (goodsData != null) {
					goodsData.setSaleQty7(new BigDecimal(saleQtyData.getSaleQty()));
				}
			}
		}
		// 尺码数
		List<SizeCountData> sizeCountDataList = allocationDOMapper2.getSizeCount(shopID, matCodeDataMap.keySet());
		if (sizeCountDataList != null && !sizeCountDataList.isEmpty()) {

			logger.debug("sizeCountDataList:{}",sizeCountDataList.size());

			Iterator<SizeCountData> sizeCountDataItr = sizeCountDataList.iterator();
			while (sizeCountDataItr.hasNext()) {
				SizeCountData sizeCountData = sizeCountDataItr.next();
				IssueGoodsData goodsData = matCodeDataMap.get(sizeCountData.getMatcode());
				if (goodsData != null) {
					goodsData.setSizeCount(sizeCountData.getCount());
				}
			}
		}

		if (!fzMatcodes.isEmpty()) {
			//skc销售额在本店排名，服装分类计算
			List<MatcodeSaleRank> saleRanks = allocationDOMapper2.getSkcShopRank(fzMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 0);

			//skc销售额在全国排名，服装分类计算
			saleRanks = allocationDOMapper2.getFzSkcNationalRank(fzMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 1);
		}
		if (!bhMatcodes.isEmpty()) {
			//skc销售额在本店排名，百货分类计算
			List<MatcodeSaleRank> saleRanks = allocationDOMapper2.getSkcShopRank(bhMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 0);

			//skc销售额在全国排名，百货分类计算
			saleRanks = allocationDOMapper2.getBhSkcNationalRank(bhMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 1);
		}

		//插入之前删除原数据
		rst = issueRestService.delRecalcGoodsData(taskId,shopID);
		logger.debug("delRecalcGoodsData shopID:{},rst:{}", shopID, rst);

		rst = issueRestService.insertGoodsData(issueGoodsData);
		logger.info("insertRecalcGoodsData shopID:{},rst:{}", shopID, rst);

		return rst;
	}

	@Override
	public int insertRecalcCategorySkcData(int taskId, String shopId) {
		List<String> categorys = issueRestService.getRecalcSkcCategorys(taskId, shopId);
		Integer rst = recalcCategoryCount(taskId, shopId, categorys);
		recalcMidCategoryCount(taskId, shopId, categorys);
		return 0;
	}

	private void recalcMidCategoryCount(int taskId, String shopId, List<String> categorys) {
		Map<String, List<CategorySkcData>> canSkcData = issueRestService.recalcMidCategoryCanSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> keepSkcData =issueRestService.recalcMidCategoryKeepSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> newSkcData = issueRestService.recalcMidCategoryNewSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> prohibitedSkcData = issueRestService.recalcMidCategoryProhibitedSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> validSkcData = issueRestService.recalcMidCategoryValidSkcCount(taskId, shopId, categorys);
		List<IssueCategorySkcData> midCategorySkcData = new ArrayList<>();
		Iterator<String> itr = categorys.iterator();
		String categoryName = null;
		String midCategoryName = null;
		//通过大类取中类
		while (itr.hasNext()) {

			categoryName = itr.next();

			List<CategorySkcData> canSkcMidCategorys = canSkcData.get(categoryName) == null ? Collections.emptyList() : canSkcData.get(categoryName);
			List<CategorySkcData> keepSkcMidCategorys = keepSkcData.get(categoryName) == null ? Collections.emptyList() : keepSkcData.get(categoryName);
			List<CategorySkcData> newSkcMidCategorys = newSkcData.get(categoryName) == null ? Collections.emptyList() : newSkcData.get(categoryName);
			List<CategorySkcData> prohibitedSkcMidCategorys = prohibitedSkcData.get(categoryName) == null ? Collections.emptyList() : prohibitedSkcData.get(categoryName);
			List<CategorySkcData> validSkcMidCategorys = validSkcData.get(categoryName) == null ? Collections.emptyList() : validSkcData.get(categoryName);

			Map<String, Integer> canSkcMidCategoryCount = canSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> keepSkcMidCategoryCount = keepSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> newSkcMidCategoryCount = newSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> prohibitedSkcMidCategoryCount = prohibitedSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> validSkcMidCategoryCount = validSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Iterator<CategorySkcData> midItr = canSkcMidCategorys.iterator();
			while (midItr.hasNext()) {
				CategorySkcData mid = midItr.next();
				midCategoryName = mid.getMidCategoryName();
				IssueCategorySkcData skcData = new IssueCategorySkcData();
				skcData.setTaskId(taskId);
				skcData.setShopID(shopId);
				skcData.setCategoryName(categoryName);
				skcData.setMidCategoryName(midCategoryName);
				skcData.setCanSkcCount(canSkcMidCategoryCount.get(midCategoryName) == null ? 0 : canSkcMidCategoryCount.get(midCategoryName));
				skcData.setKeepSkcCount(keepSkcMidCategoryCount.get(midCategoryName) == null ? 0 : keepSkcMidCategoryCount.get(midCategoryName));
				skcData.setNewSkcCount(newSkcMidCategoryCount.get(midCategoryName) == null ? 0 : newSkcMidCategoryCount.get(midCategoryName));
				skcData.setProhibitedSkcCount(prohibitedSkcMidCategoryCount.get(midCategoryName) == null ? 0 : prohibitedSkcMidCategoryCount.get(midCategoryName));
				skcData.setValidSkcCount(validSkcMidCategoryCount.get(midCategoryName) == null ? 0 : validSkcMidCategoryCount.get(midCategoryName));

				skcData.setStatus(Constant.STATUS_RECALC);

				midCategorySkcData.add(skcData);
			}
		}
		Integer rst = issueRestService.insertRecalcMidCategoryCountData(midCategorySkcData);
		logger.info("insertRecalcMidCategoryCountData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);
	}

	private Integer recalcCategoryCount(int taskId, String shopId, List<String> categorys) {
		Map<String, Integer> canSkcData = issueRestService.recalcCategoryCanSkcCount(taskId, shopId, categorys);
		Map<String, Integer> keepSkcData = issueRestService.recalcCategoryKeepSkcCount(taskId, shopId, categorys);
		Map<String, Integer> newSkcData = issueRestService.recalcCategoryNewSkcCount(taskId, shopId, categorys);
		Map<String, Integer> prohibitedSkcData = issueRestService.recalcCategoryProhibitedSkcCount(taskId, shopId, categorys);
		Map<String, Integer> validSkcData = issueRestService.recalcCategoryValidSkcCount(taskId, shopId, categorys);
		List<IssueCategorySkcData> categorySkcData = new ArrayList<>(categorys.size());
		Iterator<String> itr = categorys.iterator();
		String categoryName = null;
		while (itr.hasNext()) {
			categoryName = itr.next();
			IssueCategorySkcData skcData = new IssueCategorySkcData();
			skcData.setTaskId(taskId);
			skcData.setShopID(shopId);
			skcData.setCategoryName(categoryName);
			skcData.setCanSkcCount(canSkcData.get(categoryName) == null ? 0 : canSkcData.get(categoryName));
			skcData.setKeepSkcCount(keepSkcData.get(categoryName) == null ? 0 : keepSkcData.get(categoryName));
			skcData.setNewSkcCount(newSkcData.get(categoryName) == null ? 0 : newSkcData.get(categoryName));
			skcData.setProhibitedSkcCount(prohibitedSkcData.get(categoryName) == null ? 0 : prohibitedSkcData.get(categoryName));
			skcData.setValidSkcCount(validSkcData.get(categoryName) == null ? 0 : validSkcData.get(categoryName));

			skcData.setStatus(Constant.STATUS_RECALC);

			categorySkcData.add(skcData);
		}
		Integer rst = issueRestService.insertRecalcCategoryCountData(categorySkcData);
		logger.info("insertRecalcCategoryCountData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);
		return rst;
	}

	private List<String> getInStockShopIds(int taskId, List<String> shopIds) {
		if (shopIds == null || shopIds.isEmpty()) {
			shopIds = issueRestService.issueInStockShopIds(taskId);
		}
		return shopIds;
	}

	/**
	 * 汇集商品字段信息
	 *
	 * @param taskId
	 * @param shopIds 空则取全部门店
	 * @return
	 */
	@Override
	public Integer processIssueGoodsData(int taskId, List<String> shopIds) {
		shopIds = getInStockShopIds(taskId, shopIds);
		IssueGoodsDataTask task = new IssueGoodsDataTask(shopIds, taskId, 0, shopIds.size(), this);
		ForkJoinPool pool = new ForkJoinPool(4);
		Integer rst = pool.invoke(task);
		logger.info("PROCESS ISSUE GOODS DATA DONE! taskId:{},rst:{}", taskId, rst);
		pool.shutdown();
		return rst;
	}

	@Override
	public Integer processIssueUndo(int taskId, List<String> shopIds) {
		if (shopIds == null || shopIds.isEmpty()) {
			List<ShopInfoDo> shopInfoDos = issueDOMapper2.shops();
			shopIds = shopInfoDos.stream().map(ShopInfoDo::getShopID).collect(Collectors.toList());
		}
		IssueUndoTask undoTask = new IssueUndoTask(shopIds, taskId, 0, shopIds.size(), this);
		ForkJoinPool pool = new ForkJoinPool(4);
		int rst = pool.invoke(undoTask);
		logger.info("PROCESS ISSUE UNDO DONE! taskId:{},rst:{}", taskId, rst);
		pool.shutdown();
		return rst;
	}

	@Override
	public int batchInsertUndoData(int taskId, String shopId) {
		try {
			List<IssueUndoData> issueUndoData = issueDOMapper2.getIssueUndoData(taskId, shopId);
			if (issueUndoData == null || issueUndoData.isEmpty()) {
				return 0;
			}
			Map<String, BigDecimal> percentMap = getMidCategoryPercentAvgSaleQty(taskId, shopId, Constant.STATUS_VALID);
			List<Stock> stocks = issueDOMapper2.getIssueNeedStockList(taskId, shopId, Constant.STATUS_VALID);
			Map<String, List<BigDecimal>> needStockMap = new HashMap<>();
			for (Stock stock : stocks) {
				needStockMap.putIfAbsent(stock.getMatCode() + "_" + stock.getSizeID(), Stream.of(stock.getPercentCategory()).collect(Collectors.toList()));
			}
			List<IssueUndoDO> issueUndos = convertIssueUndoData(issueUndoData, percentMap, needStockMap);
			int rst = issueUndoDOMapper2.batchInsertTab(issueUndos);
			logger.debug("batchInsertUndoData shopId:{},rst:{}", shopId, rst);
			return rst;
		} catch (Exception e) {
			LoggerUtil.error(e, logger, "batchInsertUndoData, catch exception");
			return 0;
		}

	}

	@Override
	public int batchInsertGoodsData(int taskId, String shopID) {
		int rst = 0;
		List<IssueGoodsData> issueGoodsData = issueRestService.getIssueGoodsData(taskId, shopID);
		if (issueGoodsData == null || issueGoodsData.isEmpty()) {
			logger.info("insertGoodsData getIssueGoodsData shopID:{},rst:{}", shopID, rst);
			return rst;
		}
		Iterator<IssueGoodsData> itr = issueGoodsData.iterator();
		Map<String, IssueGoodsData> matCodeSizeIdDataMap = new HashMap<>();
		Map<String, IssueGoodsData> matCodeDataMap = new HashMap<>();
		Set<String> fzMatcodes = new HashSet<>();
		Set<String> bhMatcodes = new HashSet<>();

		Set<String> matcodes = new HashSet<>();
		Set<String> sizeIds = new HashSet<>();

		while (itr.hasNext()) {
			IssueGoodsData item = itr.next();

			matcodes.add(item.getMatCode());
			sizeIds.add(item.getSizeID());

			matCodeSizeIdDataMap.put(item.getMatCode() + item.getSizeID(), item);

			matCodeDataMap.put(item.getMatCode(), item);

			if (isFZCategoryCode(item.getCategoryCode())){
				fzMatcodes.add(item.getMatCode());
			}else {
				bhMatcodes.add(item.getMatCode());
			}
		}
		//28天
		List<SaleQtyData> saleQty28Data = allocationDOMapper2.getSale28(shopID, matcodes, sizeIds);
		if (saleQty28Data != null && !saleQty28Data.isEmpty()) {
			Iterator<SaleQtyData> itr28 = saleQty28Data.iterator();
			while (itr28.hasNext()) {
				SaleQtyData saleQtyData = itr28.next();
				IssueGoodsData goodsData = matCodeSizeIdDataMap.get(saleQtyData.getMatCode() + saleQtyData.getSizeId());
				if (goodsData != null) {
					goodsData.setSaleQty28(new BigDecimal(saleQtyData.getSaleQty()));
				}
			}
		}
		//7天
		List<SaleQtyData> saleQty7Data = allocationDOMapper2.getSale7(shopID, matcodes, sizeIds);
		if (saleQty7Data != null && !saleQty7Data.isEmpty()) {
			Iterator<SaleQtyData> itr7 = saleQty7Data.iterator();
			while (itr7.hasNext()) {
				SaleQtyData saleQtyData = itr7.next();
				IssueGoodsData goodsData = matCodeSizeIdDataMap.get(saleQtyData.getMatCode() + saleQtyData.getSizeId());
				if (goodsData != null) {
					goodsData.setSaleQty7(new BigDecimal(saleQtyData.getSaleQty()));
				}
			}
		}
		// 尺码数
		List<SizeCountData> sizeCountDataList = allocationDOMapper2.getSizeCount(shopID, matCodeDataMap.keySet());
		if (sizeCountDataList != null && !sizeCountDataList.isEmpty()) {

			logger.debug("sizeCountDataList:{}",sizeCountDataList.size());

			Iterator<SizeCountData> sizeCountDataItr = sizeCountDataList.iterator();
			while (sizeCountDataItr.hasNext()) {
				SizeCountData sizeCountData = sizeCountDataItr.next();
				IssueGoodsData goodsData = matCodeDataMap.get(sizeCountData.getMatcode());
				if (goodsData != null) {
					goodsData.setSizeCount(sizeCountData.getCount());
				}
			}
		}

		if (!fzMatcodes.isEmpty()) {
			//skc销售额在本店排名，服装分类计算
			List<MatcodeSaleRank> saleRanks = allocationDOMapper2.getSkcShopRank(fzMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 0);

			//skc销售额在全国排名，服装分类计算
			saleRanks = allocationDOMapper2.getFzSkcNationalRank(fzMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 1);
		}
		if (!bhMatcodes.isEmpty()) {
			//skc销售额在本店排名，百货分类计算
			List<MatcodeSaleRank> saleRanks = allocationDOMapper2.getSkcShopRank(bhMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 0);

			//skc销售额在全国排名，百货分类计算
			saleRanks = allocationDOMapper2.getBhSkcNationalRank(bhMatcodes, shopID);
			setSkcRank(matCodeDataMap, saleRanks, 1);
		}

		//插入之前删除原数据
		rst = issueRestService.delGoodsData(taskId,shopID);
		logger.debug("insertGoodsData batchDel shopID:{},rst:{}", shopID, rst);

		rst = issueRestService.insertGoodsData(issueGoodsData);
		logger.info("insertGoodsData batchInsert shopID:{},rst:{}", shopID, rst);

		return rst;
	}

	@Override
	public int insertCategorySkcData(int taskId, String shopId) {
		List<String> categorys = issueRestService.getSkcCategorys(taskId, shopId);
		Integer rst = processCategoryCount(taskId, shopId, categorys);
		processMidCategoryCount(taskId, shopId, categorys);
		return rst;
	}

	/**
	 * 中类处理
	 *
	 * @param taskId
	 * @param shopId
	 * @param categorys
	 * @return
	 */
	private Integer processMidCategoryCount(int taskId, String shopId, List<String> categorys) {
		Map<String, List<CategorySkcData>> canSkcData = issueRestService.midCategoryCanSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> keepSkcData = issueRestService.midCategoryKeepSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> newSkcData = issueRestService.midCategoryNewSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> prohibitedSkcData = issueRestService.midCategoryProhibitedSkcCount(taskId, shopId, categorys);
		Map<String, List<CategorySkcData>> validSkcData = issueRestService.midCategoryValidSkcCount(taskId, shopId, categorys);
		List<IssueCategorySkcData> midCategorySkcData = new ArrayList<>();
		Iterator<String> itr = categorys.iterator();
		String categoryName = null;
		String midCategoryName = null;
		//通过大类取中类
		while (itr.hasNext()) {

			categoryName = itr.next();

			List<CategorySkcData> canSkcMidCategorys = canSkcData.get(categoryName) == null ? Collections.emptyList() : canSkcData.get(categoryName);
			List<CategorySkcData> keepSkcMidCategorys = keepSkcData.get(categoryName) == null ? Collections.emptyList() : keepSkcData.get(categoryName);
			List<CategorySkcData> newSkcMidCategorys = newSkcData.get(categoryName) == null ? Collections.emptyList() : newSkcData.get(categoryName);
			List<CategorySkcData> prohibitedSkcMidCategorys = prohibitedSkcData.get(categoryName) == null ? Collections.emptyList() : prohibitedSkcData.get(categoryName);
			List<CategorySkcData> validSkcMidCategorys = validSkcData.get(categoryName) == null ? Collections.emptyList() : validSkcData.get(categoryName);

			Map<String, Integer> canSkcMidCategoryCount = canSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> keepSkcMidCategoryCount = keepSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> newSkcMidCategoryCount = newSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> prohibitedSkcMidCategoryCount = prohibitedSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Map<String, Integer> validSkcMidCategoryCount = validSkcMidCategorys.stream().collect(Collectors.toMap(CategorySkcData::getMidCategoryName, CategorySkcData::getSkcCount));
			Iterator<CategorySkcData> midItr = canSkcMidCategorys.iterator();
			while (midItr.hasNext()) {
				CategorySkcData mid = midItr.next();
				midCategoryName = mid.getMidCategoryName();
				IssueCategorySkcData skcData = new IssueCategorySkcData();
				skcData.setTaskId(taskId);
				skcData.setShopID(shopId);
				skcData.setCategoryName(categoryName);
				skcData.setMidCategoryName(midCategoryName);
				skcData.setCanSkcCount(canSkcMidCategoryCount.get(midCategoryName) == null ? 0 : canSkcMidCategoryCount.get(midCategoryName));
				skcData.setKeepSkcCount(keepSkcMidCategoryCount.get(midCategoryName) == null ? 0 : keepSkcMidCategoryCount.get(midCategoryName));
				skcData.setNewSkcCount(newSkcMidCategoryCount.get(midCategoryName) == null ? 0 : newSkcMidCategoryCount.get(midCategoryName));
				skcData.setProhibitedSkcCount(prohibitedSkcMidCategoryCount.get(midCategoryName) == null ? 0 : prohibitedSkcMidCategoryCount.get(midCategoryName));
				skcData.setValidSkcCount(validSkcMidCategoryCount.get(midCategoryName) == null ? 0 : validSkcMidCategoryCount.get(midCategoryName));
				midCategorySkcData.add(skcData);
			}
		}
		Integer rst = issueRestService.insertMidCategoryCountData(midCategorySkcData);
		logger.info("insertMidCategoryCountData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);
		return rst;
	}

	/**
	 * 大类处理
	 * @param taskId
	 * @param shopId
	 * @param categorys
	 * @return
	 */
	private Integer processCategoryCount(int taskId, String shopId, List<String> categorys) {
		Map<String, Integer> canSkcData = issueRestService.categoryCanSkcCount(taskId, shopId, categorys);
		Map<String, Integer> keepSkcData = issueRestService.categoryKeepSkcCount(taskId, shopId, categorys);
		Map<String, Integer> newSkcData = issueRestService.categoryNewSkcCount(taskId, shopId, categorys);
		Map<String, Integer> prohibitedSkcData = issueRestService.categoryProhibitedSkcCount(taskId, shopId, categorys);
		Map<String, Integer> validSkcData = issueRestService.categoryValidSkcCount(taskId, shopId, categorys);
		List<IssueCategorySkcData> categorySkcData = new ArrayList<>(categorys.size());
		Iterator<String> itr = categorys.iterator();
		String categoryName = null;
		while (itr.hasNext()) {
			categoryName = itr.next();
			IssueCategorySkcData skcData = new IssueCategorySkcData();
			skcData.setTaskId(taskId);
			skcData.setShopID(shopId);
			skcData.setCategoryName(categoryName);
			skcData.setCanSkcCount(canSkcData.get(categoryName) == null ? 0 : canSkcData.get(categoryName));
			skcData.setKeepSkcCount(keepSkcData.get(categoryName) == null ? 0 : keepSkcData.get(categoryName));
			skcData.setNewSkcCount(newSkcData.get(categoryName) == null ? 0 : newSkcData.get(categoryName));
			skcData.setProhibitedSkcCount(prohibitedSkcData.get(categoryName) == null ? 0 : prohibitedSkcData.get(categoryName));
			skcData.setValidSkcCount(validSkcData.get(categoryName) == null ? 0 : validSkcData.get(categoryName));
			categorySkcData.add(skcData);
		}
		Integer rst = issueRestService.insertCategoryCountData(categorySkcData);
		logger.info("insertCategorySkcData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);
		return rst;
	}

	/**
	 * @param matCodeDataMap
	 * @param saleRanks
	 * @param rankType       本店排名0，全国排名1
	 */
	private void setSkcRank(Map<String, IssueGoodsData> matCodeDataMap, List<MatcodeSaleRank> saleRanks, int rankType) {
		if (saleRanks != null && !saleRanks.isEmpty()) {
			Iterator<MatcodeSaleRank> rankItr = saleRanks.iterator();
			while (rankItr.hasNext()) {
				MatcodeSaleRank rank = rankItr.next();
				IssueGoodsData goodsData = matCodeDataMap.get(rank.getMatCode());
				if (goodsData == null) {
					return;
				}
				if (rankType == 0) {
					goodsData.setShopRank(rank.getRankNo());
				} else if (rankType == 1) {
					goodsData.setNationalRank(rank.getRankNo());
				}
			}
		}
	}

	/**
	 * 是否服装大类
	 * @param categoryCode
	 * @return
	 */
	private boolean isFZCategoryCode(String categoryCode) {
		if ("M".equalsIgnoreCase(categoryCode) || "W".equalsIgnoreCase(categoryCode)) {
			return true;
		}
		return false;
	}

	/**
	 * 处理每个门店未参与配发流程的商品
	 *
	 * @param issueUndoData
	 * @param percentMap
	 * @return
	 */
	private List<IssueUndoDO> convertIssueUndoData(List<IssueUndoData> issueUndoData, Map<String, BigDecimal> percentMap, Map<String, List<BigDecimal>> needStockMap) {
		List<IssueUndoDO> issueUndoDOS = new ArrayList<>();
		List<IssueUndoDO> needCheckSaleQty60 = new ArrayList<>();
		boolean hasTotalStock = false;
		boolean hasOutStock = false;

		BigDecimal totalAvgSaleQty = null;

		for (IssueUndoData issueUndo : issueUndoData) {
			IssueUndoDO issueUndoDO = new IssueUndoDO();
			BeanUtils.copyProperties(issueUndo, issueUndoDO);
			if (!needStockMap.containsKey(issueUndo.getMatCode() + "_" + issueUndo.getSizeID())) {
				issueUndoDO.setPercentCategory(new BigDecimal(0));
			} else {
//				totalAvgSaleQty = percentMap.get(issueUndo.getCategoryName() + issueUndo.getMidCategoryName());
//				if (totalAvgSaleQty != null) {
//					issueUndoDO.setPercentCategory(issueUndo.getAvgSaleQty().divide(totalAvgSaleQty, 4, ROUND_HALF_UP));
//				}

				//issueUndoDO.setPercentCategory(needStockMap.get(issueUndo.getMatCode() + "_" + issueUndo.getSizeID()));
				List<BigDecimal>  percentList = needStockMap.get(issueUndo.getMatCode() + "_" + issueUndo.getSizeID());
				if (CollectionUtils.isNotEmpty(percentList)) {
					issueUndoDO.setPercentCategory(percentList.get(0));
				}
			}

			hasTotalStock = (issueUndo.getTotalStockQty() != null && issueUndo.getTotalStockQty().doubleValue() > 0);
			hasOutStock = (issueUndo.getOutStockQty() != null && issueUndo.getOutStockQty().doubleValue() > 0);

			//存在各类库存大于0的情况，无需检查60天日均销条件
			if (hasTotalStock || hasOutStock) {
				issueUndoDOS.add(issueUndoDO);
			} else {
				needCheckSaleQty60.add(issueUndoDO);
			}
		}
		if (needCheckSaleQty60.size() > 0) {
			IssueUndoDO issueUndoDetail = needCheckSaleQty60.get(0);
			String shopId = issueUndoDetail.getInShopID();

			Set<String> matCodes = new HashSet<>();
			Set<String> sizeIds = new HashSet<>();
			Map<String, IssueUndoDO> saleQty60UndoMap = new HashMap<>();
			Iterator<IssueUndoDO> itr = needCheckSaleQty60.iterator();
			while (itr.hasNext()) {
				IssueUndoDO item = itr.next();
				matCodes.add(item.getMatCode());
				sizeIds.add(item.getSizeID());
				saleQty60UndoMap.put(item.getMatCode() + item.getSizeID(), item);
			}
			List<SaleQtyMatCodeSizeId> saleQtyMatCodeSizeIds = allocationDOMapper2.checkSaleQty60(shopId, matCodes,sizeIds);
			if (saleQtyMatCodeSizeIds != null && !saleQtyMatCodeSizeIds.isEmpty()) {
				for (SaleQtyMatCodeSizeId matCodeSizeId : saleQtyMatCodeSizeIds) {
					IssueUndoDO containDo = saleQty60UndoMap.get(matCodeSizeId.getMatCode() + matCodeSizeId.getSizeId());
					if (containDo != null) {
						issueUndoDOS.add(containDo);
					}
				}
			}
		}
		return issueUndoDOS;
	}

	private int getMinute(Date date1) {
		Date date2 = new Date();
		long diff = date2.getTime() - date1.getTime();
		int minute = (int) diff / 1000 / 60;
		if (minute < 1) {
			return 1;
		}
		return minute;
	}

//	@Override
//	public void prohibitedGoods(IssueTask task) {
//		List<ProhibitedGoods> list = stockDOMapper2.getProhibitedGoodsList();
//		LoggerUtil.info(logger, "禁品总数量：", list.size());
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
//				LoggerUtil.info(logger, "禁品更新门店：", key);
//				issueDOMapper2.updateProhibitedGoods(task.getId(), key, map.get(key));
//			}
//		}
//	}

	/**
	 * 获取各店铺日均销
	 *
	 * @param task
	 * @return
	 */
	@Override
	public Map<String, Stock> getShopAvgMap(IssueTask task) {
		List<Stock> list = issueDOMapper2.getShopAvg(task.getId());
		Map map = Maps.newHashMap();
		if (list != null) {
			for (Stock temp : list) {
				map.put(temp.getShopID(), temp);
			}
		}
		return map;
	}

	private String getIssueShopSql(List<String> shopIdList) {

		StringBuffer sql = new StringBuffer("and a.shopid in ('");

		String shopIds = shopIdList.stream().collect(Collectors.joining("','"));

		sql.append(shopIds).append("')");

		return sql.toString();
	}

	private String getIssueShopSql(int test) {
		if (test == 1) {
			return "and a.shopid in ('NM000193', 'NM000076', 'NM000068')";
		}

		//替换门店
		return "and a.shopid in ('NM000654','NM000036','NM000133','NM000134','NM000135','NM000146','NM000087','NM000104','NM000151','NM000049','NM000037','NM000030','NM000044','NM000069','NM000103','NM000108','NM000120','NM000126','NM000132','NM000150','NM000111','NM000123','NM000079','NM000121','NM000122','NM000145','NM000140','NM000153','NM000067','NM000093','NM000144','NM000159','NM000046','NM000028','NM000031','NM000041','NM000045','NM000047','NM000048','NM000052','NM000053','NM000054','NM000055','NM000059','NM000058','NM000062','NM000063','NM000065','NM000066','NM000068','NM000077','NM000080','NM000082','NM000084','NM000089','NM000091','NM000096','NM000098','NM000099','NM000109','NM000119','NM000136','NM000137','NM000138','NM000139','NM000160','NM000092','NM000094','NM000095','NM000060','NM000071','NM000101','NM000076','NM000086','NM000113','NM000043','NM000051','NM000050','NM000070','NM000102','NM000107','NM000141','NM000154','NM000118','NM000040','NM000131','NM000097','NM000100','NM000105','NM000106','NM000001','NM000003','NM000002','NM000006','NM000008','NM000010','NM000012','NM000013','NM000015','NM000016','NM000017','NM000018','NM000019','NM000020','NM000022','NM000032','NM000035','NM000219','NM000223','NM000263','NM000292','NM000338','NM000339','NM000375','NM000382','NM000398','NM000433','NM000434','NM000544','NM000597','NM000174','NM000200','NM000231','NM000288','NM000348','NM000367','NM000383','NM000399','NM000420','NM000489','NM000615','NM000617','NM000633','NM000647','NM000194','NM000265','NM000342','NM000352','NM000432','NM000488','NM000496','NM000509','NM000536','NM000545','NM000551','NM000619','NM000276','NM000373','NM000582','NM000594','NM000643','NM000173','NM000188','NM000192','NM000208','NM000298','NM000362','NM000462','NM000463','NM000503','NM000538','NM000631','NM000165','NM000176','NM000370','NM000425','NM000439','NM000448','NM000578','NM000627','NM000179','NM000184','NM000187','NM000201','NM000450','NM000487','NM000490','NM000516','NM000575','NM000576','NM000598','NM000599','NM000607','NM000610','NM000611','NM000168','NM000185','NM000211','NM000227','NM000240','NM000249','NM000251','NM000261','NM000270','NM000300','NM000336','NM000358','NM000361','NM000372','NM000457','NM000459','NM000461','NM000467','NM000481','NM000486','NM000504','NM000561','NM000566','NM000567','NM000577','NM000628','NM000644','NM000645','NM000170','NM000183','NM000189','NM000210','NM000214','NM000218','NM000260','NM000351','NM000365','NM000470','NM000474','NM000501','NM000528','NM000560','NM000625','NM000380','NM000169','NM000175','NM000195','NM000198','NM000199','NM000204','NM000205','NM000206','NM000215','NM000222','NM000250','NM000252','NM000256','NM000258','NM000259','NM000262','NM000280','NM000291','NM000308','NM000319','NM000343','NM000344','NM000359','NM000364','NM000379','NM000381','NM000385','NM000401','NM000406','NM000407','NM000408','NM000413','NM000414','NM000421','NM000422','NM000423','NM000435','NM000436','NM000437','NM000438','NM000440','NM000452','NM000455','NM000456','NM000460','NM000465','NM000466','NM000475','NM000482','NM000483','NM000508','NM000510','NM000511','NM000512','NM000530','NM000531','NM000535','NM000539','NM000558','NM000563','NM000569','NM000571','NM000573','NM000579','NM000581','NM000586','NM000588','NM000595','NM000600','NM000601','NM000613','NM000618','NM000630','NM000640','NM000167','NM000299','NM000353','NM000357','NM000376','NM000409','NM000589','NM000616','NM000624','NM000202','NM000238','NM000277','NM000331','NM000332','NM000447','NM000453','NM000479','NM000585','NM000317','NM000345','NM000419','NM000480','NM000524','NM000525','NM000533','NM000534','NM000547','NM000549','NM000554','NM000564','NM000574','NM000605','NM000181','NM000234','NM000235','NM000246','NM000264','NM000293','NM000310','NM000312','NM000311','NM000363','NM000410','NM000442','NM000485','NM000498','NM000519','NM000520','NM000318','NM000405','NM000458','NM000473','NM000522','NM000629','NM000641','NM000651','NM000220','NM000330','NM000417','NM000418','NM000441','NM000529','NM000620','NM000632','NM000635','NM000562','NM000186','NM000190','NM000212','NM000236','NM000253','NM000287','NM000340','NM000341','NM000444','NM000471','NM000514','NM000532','NM000556','NM000570','NM000606','NM000612','NM000320','NM000333','NM000386','NM000451','NM000494','NM000517','NM000366','NM000404','NM000242','NM000274','NM000281','NM000286','NM000303','NM000313','NM000314','NM000368','NM000505','NM000603','NM000608','NM000655','NM000662','NM000672','NM000656','NM000661','NM000646','NM000657','NM000671','NM000667','NM000670','NM000658','NM000665','NM000550','NM000659','NM000660','NM000669','NM000649','NM000583','NM000663','NM000668','NM000591','NM000650','NM000443','NM000469')";
	}
}
