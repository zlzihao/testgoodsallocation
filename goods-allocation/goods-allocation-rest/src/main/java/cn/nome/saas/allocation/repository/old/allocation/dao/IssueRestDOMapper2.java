package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.saas.allocation.model.issue.IssueOutStock;
import cn.nome.saas.allocation.model.old.allocation.Area;
import cn.nome.saas.allocation.model.old.allocation.Dictionary;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IssueRestDOMapper2 {

    List<String> getRegioneBusNameList();

    List<String> getSubRegioneBusNameList(@Param("regioneBusName") String regioneBusName);

    List<String> getAllSubRegioneBusNames();

    List<Area> getCityList(@Param("subRegioneBusName") String subRegioneBusName);

    List<Area> getAllCitys();

    List<Dictionary> getDictionaryList();

    List<Dictionary> getDictionaryByType(@Param("type") String type);

    List<OrderListDo> getOrderList(@Param("orderListParam") OrderListParam orderListParam);

    int getOrderListCount(@Param("orderListParam") OrderListParam orderListParam);

    IssueTaskVo getLastTask();

    IssueTask getIssueTask(@Param("taskId") int taskId);

    List<OrderDetailDo> getOrderDetail(@Param("detailParam") OrderDetailParam detailParam);

    List<OrderListDo> getOrderByIds(@Param("taskId") int taskId, @Param("shopIds") List<String> shopIds);

    ShopInDo getShopById(@Param("shopId") String shopId);

    List<MatCategoryDetailDo> getMatCategoryDetail(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categoryName") String categoryName);

    List<MatCategoryDetailDo> getMatMidCategoryDetail(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categoryName") String categoryName);

    List<String> getAllMidCategory(String categoryName);

    int getTotalOutStock(@Param("matCode") String matCode, @Param("sizeId") String sizeId, @Param("taskId") int taskId);

    int modifySkuPackageQty(@Param("param") OrderSkuModifyParam param);

    int otherShopStockQty(@Param("shopId") String shopId, @Param("matCode") String matCode, @Param("sizeId") String sizeId, @Param("taskId") int taskId);

    GoodsInfoDo getGoodsInfo(@Param("matCode") String matCode);

    IssueDetailDo getDetail(@Param("shopId") String shopId, @Param("matCode") String matCode, @Param("sizeId") String sizeId, @Param("taskId") int taskId);

    IssueInStockDo getIssueInStock(@Param("shopId") String shopId, @Param("matCode") String matCode, @Param("sizeId") String sizeId, @Param("taskId") int taskId);

    List<String> getShopIdByUserId(@Param("userId") String userId);

    UserAdminDo getAdmin(@Param("userId") String userId);

    List<DisplayData> displayData();

    List<GoodsInfoData> goodsInfoData();

    List<ShopDisplayData> shopDisplayData();

    List<ShopInfoData> shopInfoData();

    Map<String, String> showTableExists(@Param("tableName") String tableName);

    void truncateTable(@Param("tableName") String tableName);

    void createAndCopyTab(@Param("batTableName") String batTableName, @Param("tableName") String tableName);

    void selectAndInsert(@Param("batTableName") String batTableName, @Param("tableName") String tableName);

    List<DwsDimShopDo> getShopIdByCode(@Param("shopCodes") List<String> shopCodes);

    int batchInsertShopInfoTab(@Param("importData") List<ShopInfoData> importData);

    int batchInsertShopDisplayDesignTab(@Param("importData") List<ShopDisplayData> importData);

    int batchInsertDisplayTab(@Param("importData") List<DisplayData> importData);

    int batchInsertGoodsInfoTab(@Param("importData") List<GoodsInfoData> importData);

    List<String> getShopUserNames();

    List<String> getSkcByCategory(Map<String, Object> param);

    List<ShopInfoData> getShopBySaleDisplayLv(Map<String, Object> param);

    List<String> getRegioneNameList();

    List<String> getProvinceNameList();

    List<String> getCityNameList();

    List<String> getShopCodeList();

    List<String> getCategoryNameList();

    List<String> getMidCategoryNameList();

    List<String> getSmallCategoryNameList();

    List<String> getShopSaleLvList();

    List<String> getShopDisplayLvList();

    IssueTaskVo getLastReRunTask();

    /*配发分类 start */

    List<String> categoryList(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> allMidCategorys(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> midCategorys(@Param("categoryName") String categoryName, @Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> allSmallCategorys(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> smallCategorys(@Param("midCategoryName") String midCategoryName, @Param("taskId") int taskId, @Param("shopId") String shopId);

    /*配发分类 end */
    IssueTaskVo getTaskByStatus(@Param("status") int status);

    Integer issueInStockCount(@Param("taskId") int taskId);

    Integer issueOutStockCount(@Param("taskId") int taskId);

    Integer issueNeedStockCount(@Param("taskId") int taskId);

    Integer issueMidCategoryCount(@Param("taskId") int taskId);

    Integer issueDetailCount(@Param("taskId") int taskId);

    Integer issueUndoCount(@Param("taskId") int taskId);

    List<IssueGoodsData> queryIssueGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId);

    int batchInsertGoodsData(@Param("importData") List<IssueGoodsData> issueGoodsData);

    int batchDelGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> issueInStockShopIds(@Param("taskId") int taskId);

    Integer issueGoodsDataCount(@Param("taskId") int taskId);

    List<CategorySkcData> categoryNewSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> categoryProhibitedSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> categoryValidSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> categoryCanSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> categoryKeepSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<String> skcCountCategorys(@Param("taskId") int taskId, @Param("shopId") String shopId);

    Integer batchInsertCategoryCountData(@Param("importData") List<IssueCategorySkcData> categorySkcData);

    List<CategorySkcData> midCategoryCanSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> midCategoryValidSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> midCategoryProhibitedSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> midCategoryNewSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> midCategoryKeepSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    Integer batchInsertMidCategoryCountData(@Param("importData") List<IssueCategorySkcData> midCategorySkcData);

    Integer issueCategoryDataCount(@Param("taskId") int taskId);

    Integer issueMidCategoryDataCount(@Param("taskId") int taskId);

//    List<IssueGoodsData> queryGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId,@Param(value = "param") List<MatcodeSizeId> goodsDataMatCodes);

//    List<IssueInStockDo> moreIssueInStock(@Param("taskId") int taskId, @Param("shopId") String shopId,@Param(value = "param") List<MatcodeSizeId> matcodeSizeIds);

    List<IssueGoodsData> queryGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param(value = "matcodes") Set<String> matCodes, @Param(value = "sizeIds") Set<String> sizeIds);

    List<IssueInStockDo> moreIssueInStock(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param(value = "matcodes") Set<String> matCodes, @Param(value = "sizeIds") Set<String> sizeIds);

    List<String> syncIssueTime();

    int batchInsertDicy(@Param("importData") List<Dictionary> addDic);

    IssueOutStock getIssueOutStock(@Param("taskId") int taskId, @Param("matCode") String matCode, @Param("sizeId") String sizeId);

    List<cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO> issueOutStock(@Param("taskId") int taskId);

    IssueDetailDistStock issueDeatilDistStock(@Param("taskId") int taskId, @Param("matCode") String matCode, @Param("sizeId") String sizeId);

    List<IssueDetailDistStock> getDetailStock(@Param("taskId") int taskId, @Param("shopId") String shopId);

    IssueOutStockRemainDo getRemainStock(@Param("taskId") int taskId, @Param("matCode") String matCode, @Param("sizeId") String sizeId);

    int addRecalcRemainStock(@Param("importData") IssueOutStockRemainDo remainStock);

    List<IssueGoodsData> getRecalcIssueGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<String> recalcSkcCountCategorys(@Param("taskId") int taskId, @Param("shopId") String shopId);

    //重算

    List<CategorySkcData> recalcCategoryCanSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcCategoryKeepSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcCategoryProhibitedSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcCategoryNewSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcCategoryValidSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    Integer batchInsertRecalcCategoryCountData(@Param("importData") List<IssueCategorySkcData> categorySkcData);


    List<CategorySkcData> recalcMidCategoryCanSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcMidCategoryKeepSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcMidCategoryNewSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcMidCategoryValidSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    List<CategorySkcData> recalcMidCategoryProhibitedSkcCount(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("categorys") List<String> categorys);

    Integer insertRecalcMidCategoryCountData(@Param("importData") List<IssueCategorySkcData> midCategorySkcData);

    List<IssueOutStockRemainDo> issueOutStockRemainStock(@Param("taskId") int taskId);

    IssueDetailDistStock issueDeatilShopDistStock(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("matCode") String matCode, @Param("sizeId") String sizeId);

    int batchInsertRecalcGoodsData(@Param("importData") List<IssueGoodsData> issueGoodsData);

    int delRecalcGoodsData(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<IssueDetailDistStock> getRecalcDetailStock(@Param("taskId") int taskId, @Param("shopId") String shopId);

    IssueOutStockRemainDo getRecalcRemainStock(@Param("taskId") int taskId, @Param("matCode") String matCode, @Param("sizeId") String sizeId);

    List<IssueOutStockRemainDo> matcodeStockRemain(@Param("taskId") int taskId, @Param(value = "matCodes") Set<String> matCodes, @Param(value = "sizeIds") Set<String> sizeIds);

    List<IssueMidCategoryQtyDo> getMidCategoryPercentAvgSaleQty(@Param("taskId") int taskId, @Param("shopId") String shopId,@Param("status")  int status);
}

