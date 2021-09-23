package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.OrderDetailDo;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.model.old.issue.OrderDetailVo;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.repository.entity.allocation.MidCategoryStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.SubWarehouseShopMappingDO;
import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * IssueDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public interface NewIssueDOMapper {

    /**
     * 清空表
     * @param tableName tableName
     */
    void truncateTable(@Param("tableName") String tableName);

    /**
     * 备份InStock数据
     * @param bakTableName 备份表名
     * @param tableName 源表名
     * @param shopIds 备份的店铺
     * @return 插入条数
     */
    Integer bakInStockData(@Param("bakTableName") String bakTableName, @Param("tableName") String tableName, @Param("shopIds") Set<String> shopIds);

    /**
     * loadFileInStock
     * @param bakTableName
     * @param fullFileName
     * @return
     */
    Integer loadFileInStock(@Param("inTabName") String bakTableName, @Param("fullFileName") String fullFileName);

    /**
     * 删除InStock数据
     * @param tableName 表名
     * @param shopIds 店铺
     * @return 删除条数
     */
    Integer delInStockByShopId(@Param("tableName") String tableName, @Param("shopIds") Set<String> shopIds);

    /**
     * 获取issue_in_stock列表
     * @param tableName
     * @param shopIds
     * @return
     */
    List<NewIssueInStockDo> getIssueInStockList(@Param("tableName")String tableName,@Param("childFlag")Integer childFlag, @Param("shopIds") Set<String> shopIds);

    /**
     * getIssueGoodsList
     * @param shopIds shopIds
     * @return return
     */
    Set<String> getIssueGoodsList(@Param("shopIds") Set<String> shopIds);

    /**
     * 创建供给池表
     * @param tableName
     */
    void createIssueOutStockTab(@Param("tableName") String tableName);

    /**
     * bakIssueOutStock
     * @return
     */
    Integer bakIssueOutStock(@Param("bakTabName") String bakTabName, @Param("tabName") String tabName);

    /**
     * 单店重算返回配发数量
     * @param detailTabName
     * @param outTabName
     * @param shopIds
     * @return
     */
    Integer updOutRemainStock(@Param("detailTabName")String detailTabName, @Param("outTabName")String outTabName, @Param("shopIds") Set<String> shopIds);

    /**
     * 创建需求池表
     * @param tableName
     */
    void createIssueInStockTab(@Param("tableName") String tableName);

    /**
     * 创建issue_goods_data
     * @param tableName
     */
    void createIssueGoodsDataTab(@Param("tableName") String tableName);

    /**
     * 创建配发明细表
     * @param tableName
     */
    void createIssueDetailTab(@Param("tableName") String tableName);

    /**
     * 删除对应表
     * @param tableName
     */
    void dropTableIfExist(@Param("tableName") String tableName);

    /**
     * 供给池
     * @param list
     */
    void addIssueOutStock(@Param("tableName") String tableName, @Param("list") List<NewIssueOutStockDo> list);



    /**
     * 需求池
     * @param list
     */
    void addIssueInStock(@Param("tableName") String tableName, @Param("list") List<NewIssueInStockDo> list);

    /**
     * 获取中类
     * @param tableName
     * @param shopIds
     * @return
     */
    List<MidCategoryStockDO> getMidCategorySale(@Param("tableName")String tableName, @Param("shopIds")Set<String> shopIds);

    /**
     * 获取店铺日均销
     * @param tableName
     * @param shopIds
     * @return
     */
    List<Map<String,Object>> getShopAvg(@Param("tableName")String tableName, @Param("shopIds")Set<String> shopIds);

    /**
     * 根据分仓代码获取outStock列表
     * @param warehouseCode
     * @return
     */
    List<NewIssueOutStockDo> getIssueOutStock(@Param("tableName")String tableName, @Param("warehouseCode")String warehouseCode);


    /**
     * 查询所有店铺所需的sku的数量
     * @param tableName
     * @param shopSet
     * @return
     */
    List<Map<String,Object>> getNeedTotalStockQty(@Param("tableName")String tableName, @Param("shopSet") Set<String> shopSet);

    /**
     * 插入配发明细
     * @param tableName
     * @param list
     */
    void addIssueDetail(@Param("tableName")String tableName, @Param("list") List<NewIssueDetailDo> list);

    /**
     * bak issue detail
     * @param bakTabName
     * @param tabName
     * @param shopIds
     */
    void bakIssueDetail(@Param("bakTabName")String bakTabName, @Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    /**
     * del issue detail
     * @param tabName
     * @param shopIds
     */
    void delIssueDetailByShopId(@Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    /**
     * 批量更新店铺剩余需求量
     * @param tableName
     * @param list
     */
    void batchUpdateIssueNeedStock(@Param("tableName")String tableName, @Param("list") List<BatchUpdateIssueNeedStockDo> list);
    /**
     * 批量更新店铺剩余需求量
     * @param tableName
     * @param list
     */
    void batchUpdateIssueInStock(@Param("tableName")String tableName, @Param("list") List<BatchUpdateIssueInStockDo> list);

    /**
     * 批量更新仓库分配完后剩余库存量
     * @param tableName
     * @param list
     */
    void batchUpdateIssueOutStock(@Param("tableName")String tableName, @Param("list") List<BatchUpdateIssueOutStockDo> list);

    /**
     * 根据shopId获取goodsData表的数量
     * @param goodsDataTabName
     * @param shopId
     * @return
     */
    Integer getGoodsDataCount(@Param("goodsDataTabName")String goodsDataTabName, @Param("shopId") String shopId);

    List<cn.nome.saas.allocation.model.old.issue.OrderDetailDo> getOrderDetail(@Param("inTabName") String inTableName, @Param("outTabName") String outTableName,
                                                                               @Param("needTabName") String needTableName, @Param("detailTabName") String detailTableName,
                                                                               @Param("goodsDataTabName") String goodsDataTabName,
                                                                               @Param("detailParam") OrderDetailReq detailParam);


    List<cn.nome.saas.allocation.model.old.issue.OrderDetailDo> getIssueUndoDetail(@Param("inTabName") String inTableName, @Param("outTabName") String outTableName,
                                                                                   @Param("needTabName") String needTableName, @Param("detailTabName") String detailTableName,
                                                                                   @Param("goodsDataTabName") String goodsDataTabName,
                                                                               @Param("detailParam") OrderDetailReq detailParam);

    /**
     * batch insert goods data
     * @param tableName
     * @param issueGoodsData
     * @return
     */
    int batchInsertGoodsData(@Param("tableName") String tableName, @Param("importData") List<IssueGoodsData> issueGoodsData);

    int insertExportOrderDetails(@Param("orderDetails") List<OrderDetailVo> orderDetails);

    List<String> selectAllShopIds(@Param("tableName") String tableName);

    /**
     * bak goods data
     * @param bakTabName
     * @param tabName
     * @param shopIds
     * @return
     */
    int bakGoodsData(@Param("bakTabName") String bakTabName, @Param("tabName") String tabName, @Param("shopIds") Set<String> shopIds);

    /**
     * del goods data
     * @param tableName
     * @param shopIds
     * @return
     */
    int delGoodsDataByShopId(@Param("tabName") String tableName, @Param("shopIds") Set<String> shopIds);

    Set<String> issueInStockShopIds(@Param("tableName") String tableName);

    List<IssueGoodsData> queryIssueGoodsData(@Param("tableName") String tableName, @Param("shopId") String shopId);
//    List<IssueGoodsData> queryAllIssueGoodsData(@Param("tableName") String tableName, @Param("shopIds") Set<String> shopIds);

//    List<IssueGoodsData> queryGoodsData(@Param("tableName") String tableName, @Param("shopId") String shopId, @Param(value = "matCodes") Set<String> matCodes, @Param(value = "sizeIds") Set<String> sizeIds);

    List<OrderListDo> getOrderList(@Param("tableName") String tableName, @Param("orderListParam") OrderListParam orderListParam);

    int getOrderListCount(@Param("tableName") String tableName, @Param("orderListParam") OrderListParam orderListParam);

    List<MatCategoryDetailDo> getMatCategoryDetail(@Param("inTableName") String inTableName, @Param("detailTableName") String detailTableName,
                                                   @Param("midTableName") String midTableName, @Param("categoryDataTableName") String categoryDataTableName,
                                                   @Param("shopId") String shopId, @Param("categoryName") String categoryName);

    IssueTaskVo getLastReRunTask();

    IssueTaskVo getLastTask();

    IssueTaskDO getIssueTask(@Param("taskId") int taskId);

    List<MatCategoryDetailDo> getMatMidCategoryDetail(@Param("inTableName") String inTableName, @Param("detailTableName") String detailTableName, @Param("midTableName") String midTableName,
                                                      @Param("shopId") String shopId, @Param("categoryName") String categoryName);


    List<NewCategorySkcData> getSkcCountCategorys(@Param("instockTableName") String instockTableName,@Param("shopIdList") Set<String> shopIdList);

    List<NewCategorySkcData> getSkcCountMidCategorys(@Param("instockTableName") String instockTableName,@Param("shopIdList") Set<String> shopIdList);

    List<NewCategorySkcData> calcCategoryCanSkcCount(@Param("instockTableName") String instockTableName,@Param("outstockTableName") String outstockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcCategoryKeepSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcCategoryNewSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcCategoryProhibitedSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcCategoryValidSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcMidCategoryCanSkcCount(@Param("instockTableName") String instockTableName,@Param("outstockTableName") String outstockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcMidCategoryKeepSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcMidCategoryNewSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcMidCategoryProhibitedSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    List<NewCategorySkcData> calcMidCategoryValidSkcCount(@Param("instockTableName") String instockTableName,@Param("shopIdList") List<String> shopIdList);

    Integer createCategoryCountTable(@Param("tableName") String tableName);

    Integer createMidCategoryCountTable(@Param("tableName") String tableName);

    Integer insertCategoryCountData(@Param("tableName") String tableName,@Param("list")List<NewIssueCategorySkcData> list);

    /**
     * 备份CategoryCountData
     * @param bakTabName
     * @param tabName
     * @param shopIds
     * @return
     */
    Integer bakCategoryCountData(@Param("bakTabName")String bakTabName, @Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    /**
     * 删除CategoryCountData
     * @param tableName 表名
     * @param shopIds 店铺
     * @return 删除条数
     */
    Integer delCategoryCountDataByShopId(@Param("tableName") String tableName, @Param("shopIds") Set<String> shopIds);

    Integer insertMidCategoryCountData(@Param("tableName") String tableName,@Param("list")List<NewIssueCategorySkcData> list);

    /**
     * 备份MidCategoryCountData
     * @param bakTabName
     * @param tabName
     * @param shopIds
     * @return
     */
    Integer bakMidCategoryCountData(@Param("bakTabName")String bakTabName, @Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    /**
     * 删除MidCategoryCountData
     * @param tableName 表名
     * @param shopIds 店铺
     * @return 删除条数
     */
    Integer delMidCategoryCountDataByShopId(@Param("tableName") String tableName, @Param("shopIds") Set<String> shopIds);

    /**
     * 增加配发任务
     * @param task
     * @return
     */
    int addTask(@Param("task") IssueTask task);

    /*配发分类 start */
    List<String> categoryList(@Param("tableName") String tableName, @Param("shopId") String shopId);

    List<String> allMidCategorys(@Param("tableName") String tableName, @Param("shopId") String shopId);

    List<String> midCategorys(@Param("tableName") String tableName, @Param("categoryName") String categoryName, @Param("shopId") String shopId);

    List<String> allSmallCategorys(@Param("tableName") String tableName, @Param("shopId") String shopId);

    List<String> smallCategorys(@Param("tableName") String tableName, @Param("midCategoryName") String midCategoryName, @Param("shopId") String shopId);
    /*配发分类 end */

    List<ShopDisplayDesignView> loadAllDisplayDesignV();

    Integer getRunningIssueTask();
    List<ShopDisplayDesignView> loadAllDisplayDesignV(@Param("shopIds") Set<String> shopIds);

    /**
     * 获取可重算任务
     * @param taskId
     * @return
     */
    RecalcTaskDo getValidReCalcTask(@Param("taskId")  int taskId);

    /**
     * 是否有正在重算任务
     * @param taskId
     * @return
     */
    RecalcTaskDo hasDoingReCalcTask(@Param("taskId") int taskId);

    /**
     * 更新重算状态
     * @param recalcId
     * @return
     */
    Integer updateReCalcStatus(@Param("recalcId") int recalcId);

    /**
     * 店铺是否有重算中任务
     * @param taskId
     * @param shopId
     * @return
     */
    Integer shopReCalcCount(@Param("taskId") int taskId, @Param("shopId") String shopId);

    /**
     * 是否有重算任务
     * @param taskId
     * @return
     */
    public Integer hasReCalcTask(@Param("taskId") int taskId);

    /**
     * 增加重算任务
     * @param taskDo
     * @return
     */
    Integer addReCalcTask(@Param("taskDo") RecalcTaskDo taskDo);

    /**
     * 更新重算完成状态
     * @param recalcId
     * @return
     */
    Integer updateReCalcStatusFin(@Param("recalcId") int recalcId);

    /**
     * getReCalcTaskById
     * @param recalcId
     * @return
     */
    RecalcTaskDo getReCalcTaskById(@Param("recalcId") int recalcId);

    /**
     * getReCalcTaskListById
     * @param recalcIds
     * @return
     */
    List<RecalcTaskDo> getReCalcTaskListById(@Param("recalcIds")  List<String> recalcIds);

    /**
     * reCalcTaskCancel
     * @param recalcId
     * @return
     */
    Integer reCalcTaskCancel(@Param("recalcId") int recalcId);


    /**
     * updateReCalcTaskPercent
     * @param recalcId
     * @param percentNum
     */
    void updateReCalcTaskPercent(@Param("recalcId") int recalcId, @Param("percentNum") int percentNum);

    /**
     * batchInsertWarehouseShop
     * @param list list
     * @return Integer
     */
    Integer batchInsertWarehouseShop(@Param("list") List<SubWarehouseShopMappingDO> list);

    /**
     * addIssueSandboxDetail
     * @param list list
     * @return Integer
     */
    Integer addIssueSandboxDetail(@Param("list") List<IssueSandboxDetailDo> list);

    Integer addNewIssueSandboxDetail(@Param("list") List<NewIssueSandboxDetailDo> list);

    /**
     * getIssueSandBoxDetail
     * @param taskId
     * @return
     */
    List<IssueSandboxDetailDo> getIssueSandBoxDetail(@Param("taskId") Integer taskId);


    /**
     *
     * @return
     */
    List<HuihuoGoodsDo> getHuihuoGoods();

    /**
     * 插入配发明细
     * @param list
     */
    void addIssueReserveDetail(@Param("list") List<IssueReserveDetailDo> list);

    /**
     * 获取配发明细
     * @param shopIds
     */
    List<IssueReserveDetailDo> getIssueReserveDetail(@Param("shopIds") Set<String> shopIds);

    /**
     * addIssueSandboxShopStock
     * @param list list
     * @return Integer
     */
    Integer addIssueSandboxShopStock(@Param("list") List<IssueSandboxShopStockDo> list);

    /**
     * getIssueSandboxShopStock
     * @param taskId
     * @return
     */
    List<IssueSandboxShopStockDo> getIssueSandboxShopStock(@Param("taskId") Integer taskId);
}
