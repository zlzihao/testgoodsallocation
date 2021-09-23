package cn.nome.saas.allocation.service.old.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.old.allocation.*;

import java.util.List;
import java.util.Map;

public interface StockService {
	
	Map<String, List<Stock>> getInStockList(Task task);
	
	void inStock(Task task);
	
	//齐码
	void inComplementStock(Task task);
	
	void outStock(Task task);
	
	
	List<StockStat> getInStockStats(Task task);
//	List<StockStat> getOutStockStats(String shopId, Task task);
	
	List<Stock> getInStockList(String shopId, Task task);
	List<Stock> getOutStockList(String inShopId, Task task);
	
	
//	void allocation(Task task);
	
	
//	List<StockStat> inStockStatsPage(InStockReq inStockReq, Page page);
//	List<StockStat> outStockStatsPage(InStockReq inStockReq, Page page);
//	List<Stock> inStockDetailPage(InStockReq inStockReq, Page page);
	
	/**
	 * 取得中类列表
	 * @param inStockReq
	 * @return
	 */
	List<Category> getMidCategory(InStockReq inStockReq);
	/**
	 * 取得小类列表
	 * @param inStockReq
	 * @return
	 */
	List<Category> getSmallCategory(InStockReq inStockReq);
	
	
	List<TaskProgress> getTaskProgress(int taskId);
	
	List<Paramater> getSeasonList();
	List<Paramater> getYearNoList();
	List<Shop> getShopList();
	
//	void prohibitedGoods(int taskId);
}
