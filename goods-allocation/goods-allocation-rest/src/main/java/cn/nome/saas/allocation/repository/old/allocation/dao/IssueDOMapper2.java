package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.model.old.allocation.ShopInfoDo;
import cn.nome.saas.allocation.model.old.allocation.Stock;
import cn.nome.saas.allocation.model.old.issue.IssueOutStockRemainDo;
import cn.nome.saas.allocation.model.old.issue.IssueUndoData;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;


public interface IssueDOMapper2 {
	int addTask(@Param("task") IssueTask task);

	void addIssueInStock(@Param("list") List<Stock> list);

	void addIssueOutStock(@Param("list") List<Stock> list);

//	void addIssueDetail(@Param("list") List<IssueDetail> list);

	void midMidCategoryQty(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("tabName") String tabName);

	void addNeedSkuStock(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("tabName") String tabName);

	List<String> getShopIdList();

	List<Stock> getEnoughStockSku(@Param("taskId") int taskId);

	List<Stock> getNotEnoughStockSku(@Param("taskId") int taskId);

	void addEnoughStockSku(@Param("taskId") int taskId, @Param("matCode") String matCode,
                           @Param("sizeId") String sizeId);

	void addNotEnoughStockSku(@Param("taskId") int taskId, @Param("matCode") String matCode,
                              @Param("sizeId") String sizeId, @Param("stockQty") long stockQty);
	
	List<Stock> getMidCategorySale(@Param("taskId") int taskId);
	
	List<Stock> getGoodsInfo();
	
	List<ProhibitedGoods> getProhibitedGoodsList();
	
//	void updateProhibitedGoods(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("list") List<String> list);
	
	List<Stock> getShopAvg(@Param("taskId") int taskId);

    void updateTaskStatus(@Param("taskId") int taskId);

	List<ShopInfoDo> getShops();

	List<IssueUndoData> getIssueUndoData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	List<ShopInfoDo> shops();

	/**
	 * 获取商品matCode列表
	 * @return
	 */
	List<String> getGoodsMatCodeList();

    void addIssueRemainStock(@Param("remainDo") IssueOutStockRemainDo remainDo);

    void addRecalcIssueInStock(@Param("list") List<Stock> list);

	List<Stock> getRecalcShopMidCategorySale(@Param("taskId") int taskId, @Param("shopId") String shopId);

	ShopInfoDo getShop(@Param("shopId") String shopId);

	void recalcMidCategoryQty(@Param("taskId")int taskId, @Param("shopId") String shopId, @Param("tabName") String tabName);

	void recalcNeedSkuStock(@Param("taskId")int taskId,@Param("shopId") String shopId,@Param("tabName") String tabName);

	List<IssueOutStockRemainDo> getRecalcEnoughStockSku(@Param("taskId")int taskId,@Param("shopId") String shopId);

	void addRecalcEnoughStockSku(@Param("taskId")int taskId,@Param("shopId") String shopId, @Param("matCode") String matCode,
								 @Param("sizeId") String sizeId);

	List<IssueOutStockRemainDo> getRecalcNotEnoughStockSku(@Param("taskId")int taskId,@Param("shopId") String shopId);

	void addRecalcNotEnoughStockSku(@Param("taskId")int taskId,@Param("shopId") String shopId, @Param("matCode") String matCode,
									@Param("sizeId") String sizeId,@Param("stockQty") BigDecimal stockQty);

	List<IssueUndoData> getIssueRecalcUndoData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	//状态修改start

	int invalidIssueGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueCategoryData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueCategoryData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueDetail(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueDetail(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueInStock(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueInStock(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueMidCategoryQty(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueMidCategoryQty(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueMidCategoryData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueMidCategoryData(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueNeedStock(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueNeedStock(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int invalidIssueOutStockRemain(@Param("taskId") int taskId);

	int validIssueOutStockRemain(@Param("taskId") int taskId);

	int invalidIssueUndo(@Param("taskId") int taskId, @Param("shopId") String shopId);

	int validIssueUndo(@Param("taskId") int taskId, @Param("shopId") String shopId);

	//状态修改end
	int deductStockRemain(@Param("stockQty") BigDecimal remainStockQty,@Param("id") int id);


	List<Stock> getIssueNeedStockList(@Param("taskId") int taskId,@Param("shopId") String shopId, @Param("status") int status );

}