package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.old.allocation.*;
import cn.nome.saas.allocation.repository.old.allocation.entity.MinDisplaySkcDO;
import cn.nome.saas.allocation.repository.old.allocation.entity.ShopExpressDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StockDOMapper2 {
	void addInStock(@Param("list") List<Stock> list);

	void addOutStock(@Param("list") List<Stock> list);

	void addAllocationDetail(@Param("list") List<AllocationDetail> list);

	List<Stock> getInStockList(@Param("taskId") int taskId);

	List<StockStat> getInStockStats(@Param("period") int period, @Param("taskId") int taskId);

	List<StockStat> getOutStockStats(@Param("period") int period, @Param("shopId") String shopId,
                                     @Param("taskId") int taskId);

	List<Stock> getInStockListByShopId(@Param("period") int period, @Param("shopId") String inShopId,
                                       @Param("taskId") int taskId);

	List<Stock> getOutStockListByShopId(@Param("period") int period, @Param("shopId") String inShopId,
                                        @Param("taskId") int taskId);

	List<StockStat> inStockStatsPage(@Param("req") InStockReq req, @Param("page") Page page,
                                     @Param("period") int period);

	int inStockStatsCount(@Param("req") InStockReq req, @Param("period") int period);

	List<StockStat> outStockStatsPage(@Param("req") InStockReq req, @Param("page") Page page,
                                      @Param("period") int period);

	int outStockStatsCount(@Param("req") InStockReq req, @Param("period") int period);

	List<Stock> inStockDetailPage(@Param("req") InStockReq req, @Param("page") Page page, @Param("period") int period);

	int inStockDetailCount(@Param("req") InStockReq req, @Param("period") int period);

	List<Goods> getGoodsPrice(@Param("list") List<String> list);

	List<Category> getMidCategory(@Param("taskId") int taskId, @Param("typeSql") String typeSql);

	List<Category> getSmallCategory(@Param("midCategoryCode") String midCategoryCode, @Param("taskId") int taskId,
									@Param("typeSql") String typeSql);

	List<TaskProgress> getTaskProgress(@Param("taskId") int taskId);

	List<Paramater> getSeasonList();

	List<Paramater> getYearNoList();

	List<Shop> getShopList();

	List<ProhibitedGoods> getProhibitedGoodsList(@Param("shopIds") Set<String> shopIds);
	List<ProhibitedGoods> getProhibitedGoodsListByDate(@Param("shopIds") Set<String> shopIds, @Param("date") Date date);

	/**
	 * 获取全局配置的白名单列表
	 * TODO 重算时应该查出所有店铺白名单过滤禁配, 否则有问题, 待验证
	 * @return
	 */
	List<ProhibitedGoods> getWhiteGoodsList(@Param("shopIds") Set<String> shopIds);

	/**
	 * 获取全局配置的白名单列表
	 * @return
	 */
	List<ProhibitedGoods> getWhiteGoodsListByDate(@Param("shopIds") Set<String> shopIds, @Param("date") Date date);

	/**
	 * 获取全局配置的保底列表
	 * @return
	 */
	List<ProhibitedGoods> getSecurityList();

	/**
	 * 获取全局配置的保底列表
	 * @return
	 */
	List<ProhibitedGoods> getSecurityListByDate(@Param("date") Date date);

//	void updateProhibitedGoods(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("list") List<String> list);

	List<ShopExpressDO> getShopExpressList(@Param("listA") Set listA, @Param("listB") Set listB) ;

	List<MinDisplaySkcDO> getMinDisplaySkcList();

	List<Map<String,String>> getMatCodeBySmallCategory(@Param("list") List list);

}