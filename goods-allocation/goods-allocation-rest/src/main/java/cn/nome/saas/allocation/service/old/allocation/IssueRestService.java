package cn.nome.saas.allocation.service.old.allocation;

import cn.nome.saas.allocation.model.issue.IssueOutStock;
import cn.nome.saas.allocation.model.old.allocation.Area;
import cn.nome.saas.allocation.model.old.allocation.Dictionary;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.*;

import java.util.List;
import java.util.Map;

public interface IssueRestService {
	List<String> getRegioneBusNameList();
	List<String> getSubRegioneBusNameList(String regioneBusName);
	List<Area> getCityList(String subRegioneBusName);
	List<Dictionary> getDictionaryList();

	OrderListWrap getOrderList(OrderListReq orderListReq);

	Map getDictByType(String type);

	OrderDetailWrap getOrderDetail(OrderDetailReq detailReq);

	IssueTaskVo getLastTask();

	IssueTask getTaskById(int taskId);

	IssueTaskVo getLastReRunTask();

	List<OrderListVo> getOrderByIds(Integer integer, String shopIds);

	MatDetailWrap getMatDetail(int taskId, String shopId, String categoryName);

	List<MatDetailTree> downloadMatDetail(int taskId, List<String> shopIdList);

	MatDetailWrap getMatMidCategoryDetail(int taskId, String shopId, String categoryName);

	OrderModifyWrap modifyOrderSku(OrderSkuModifyReq orderSkuModifyReq);

	ShopUserNameWrap getUserNames();

	/**
	 * 根据表名清空表数据
	 *
	 * @param tabName
	 * @return
	 */
	int truncateTab(String tabName);

	List<String> categoryList(int taskId, String shopId);

	List<String> midCategoryList(int taskId, String shopId, String categoryName);

	List<String> smallCategoryList(int taskId, String shopId, String midCategoryName);

	Map<String, String> issueTimeList();

    IssueTask createTask(int runStatus);

    TaskDataWrap checkTaskData(int status);

    List<IssueGoodsData> getIssueGoodsData(int taskId, String shopID);

	int insertGoodsData(List<IssueGoodsData> issueGoodsData);

    int delGoodsData(int taskId, String shopID);

    List<String> issueInStockShopIds(int taskId);

    void bakIssueTab();

    List<String> getSkcCategorys(int taskId, String shopId);

    List<String> getRecalcSkcCategorys(int taskId, String shopId);

    Map<String, Integer> categoryCanSkcCount(int taskId, String shopId, List<String> categorys);

	 Map<String, Integer> categoryKeepSkcCount(int taskId, String shopId, List<String> categorys);

	 Map<String, Integer> categoryNewSkcCount(int taskId, String shopId, List<String> categorys);

	 Map<String, Integer> categoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys);

	 Map<String, Integer> categoryValidSkcCount(int taskId, String shopId, List<String> categorys);

	Integer insertCategoryCountData(List<IssueCategorySkcData> categorySkcData);

    Map<String, List<CategorySkcData>> midCategoryCanSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String, List<CategorySkcData>> midCategoryKeepSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String, List<CategorySkcData>> midCategoryNewSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String, List<CategorySkcData>> midCategoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String, List<CategorySkcData>> midCategoryValidSkcCount(int taskId, String shopId, List<String> categorys);

	Integer insertMidCategoryCountData(List<IssueCategorySkcData> midCategorySkcData);

    Integer syncIssueTime();

	IssueOutStock getIssueOutStock(int taskId, String matCode, String sizeId);

	List<cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO> issueOutStock(int taskId);

	IssueDetailDistStock issueDeatilDistStock(int taskId, String matCode, String sizeID);

	List<IssueDetailDistStock> getDetailStock(int taskId, String shopId);

    IssueOutStockRemainDo getRemainStock(int taskId, String matCode, String sizeID);

	int addRecalcRemainStock(IssueOutStockRemainDo remainStock);

    List<IssueGoodsData> getRecalcIssueGoodsData(int taskId, String shopID);

    Map<String,Integer> recalcCategoryCanSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,Integer> recalcCategoryKeepSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,Integer> recalcCategoryNewSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,Integer> recalcCategoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,Integer> recalcCategoryValidSkcCount(int taskId, String shopId, List<String> categorys);

	Integer insertRecalcCategoryCountData(List<IssueCategorySkcData> categorySkcData);

    Map<String,List<CategorySkcData>> recalcMidCategoryCanSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,List<CategorySkcData>> recalcMidCategoryKeepSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,List<CategorySkcData>> recalcMidCategoryNewSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,List<CategorySkcData>> recalcMidCategoryProhibitedSkcCount(int taskId, String shopId, List<String> categorys);

	Map<String,List<CategorySkcData>> recalcMidCategoryValidSkcCount(int taskId, String shopId, List<String> categorys);

	Integer insertRecalcMidCategoryCountData(List<IssueCategorySkcData> midCategorySkcData);

    List<IssueOutStockRemainDo> issueOutStockRemainStock(int taskId);

	IssueDetailDistStock issueDeatilShopDistStock(int taskId, String shopId, String matCode, String sizeID);

	int delRecalcGoodsData(int taskId, String shopID);

    List<IssueDetailDistStock> getRecalcDetailStock(int taskId, String shopId);

	IssueOutStockRemainDo getRecalcRemainStock(int taskId, String matCode, String sizeID);

    int getIssueDay(int roadDays, String issueTime, int dayWeek);

    List getMidCategoryPercentAvgSaleQty(int taskId, String shopId, int status);
}
