package cn.nome.saas.allocation.service.old.allocation;

import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.model.old.allocation.Stock;
import cn.nome.saas.allocation.model.old.issue.IssueDetailDistStock;
import cn.nome.saas.allocation.model.old.issue.IssueOutStockRemainDo;
import cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface IssueService {

	void issueInStock(IssueTask task, int period, int type, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,int forTest);

	void issueInNewSkcStock(IssueTask task, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods,int forTest);

	void issueOutStock(IssueTask task);

	void processEnoughStock(IssueTask task);

	void processNotEnoughStock(IssueTask task);

	Map<String, Stock> getGoodsInfo() ;
	/**
	 * 配发处理总入口
	 * 
	 * @param task
	 */
	void issueProcess(IssueTask task);

	void issueProcess(IssueTask task,List<String> shopIdList);

//	void prohibitedGoods(IssueTask task);
	
	Map<String,Stock> getShopAvgMap(IssueTask task);

	Integer processIssueUndo(int taskId, List<String> shopIds);

    int batchInsertUndoData(int taskId, String shopId);

	Integer processIssueGoodsData(int taskId, List<String> shopIds);

    int batchInsertGoodsData(int taskId, String shopID);

	int insertCategorySkcData(int taskId, String shopId);

	Integer processRemainStock(int taskId);

	Integer processCategorySkcCount(int taskId, List<String> shopIds);

    int insertStockRemainData(int taskId, IssueOutStockDO stockRemainData);

    int batchInsertRecalcUndoData(int taskId, String shopId);

    int batchInsertRecalcGoodsData(int taskId, String shopId);

    int insertRecalcCategorySkcData(int taskId, String shopId);

	int insertStockRemainFreedData(int taskId, String shopId, IssueOutStockRemainDo issueOutStockDO);

    int deductStockRemainData(int taskId, String shopId, IssueDetailDistStock detailDistStock);

	void updateIssueDays();

	void updateIssueDaysByShopId(String shopId);

	void updateIssueDaysByDate(Calendar calendar);
}
