package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.Paramater;
import cn.nome.saas.allocation.model.issue.IssueInStock;
import cn.nome.saas.allocation.model.issue.IssueOutStock;
import cn.nome.saas.allocation.model.issue.IssueUndoData;
import cn.nome.saas.allocation.repository.entity.allocation.IssueNeedStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSumAvgDO;
import cn.nome.saas.allocation.repository.entity.allocation.MidCategoryStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.WarehouseRecordDO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * NewIssueSkuCalcDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/9/09
 */
public interface NewIssueSkuCalcDOMapper {

    List<Paramater> getPriorityCount();

    List<WarehouseRecordDO> getWarehoseConfigIdByPriority(@Param("priority") int priority);

    List<MidCategoryStockDO> getMidCategoryAvgQty(Map<String,Object> param);

    List<MidCategoryStockDO> getCategoryDisplayData(@Param("shopIds")Set<String> shopIds);

    Integer insertMidCategoryQty(@Param("tableName")String tableName,@Param("list") List<MidCategoryStockDO> list);

    /**
     * 备份MidCategoryQty
     * @param bakTabName
     * @param tabName
     * @param shopIds
     * @return
     */
    Integer bakMidCategoryQty(@Param("bakTabName")String bakTabName, @Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    /**
     * 删除MidCategoryQty数据
     * @param tableName 表名
     * @param shopIds 店铺
     * @return 删除条数
     */
    Integer delMidCategoryQtyByShopId(@Param("tableName") String tableName, @Param("shopIds") Set<String> shopIds);

    Integer insertNeedSkuStock(@Param("tableName")String tableName,@Param("list") List<IssueNeedStockDO> list);

    /**
     * loadFileNeedStock
     * @param needTableName
     * @param fullFileName
     * @return
     */
    Integer loadFileNeedStock(@Param("needTabName") String needTableName, @Param("fullFileName") String fullFileName);
    /**
     * 备份 need_stock
     * @param bakTabName
     * @param tabName
     * @param shopIds
     * @return
     */
    Integer bakNeedStock(@Param("bakTabName")String bakTabName,@Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    /**
     * del need_stock
     * @param tabName
     * @param shopIds
     * @return
     */
    Integer delNeedStockByShopId(@Param("tabName")String tabName, @Param("shopIds")Set<String> shopIds);

    Integer createMidCategoryTable(@Param("tableName")String tableName);

    Integer createIssueNeedStockTable(@Param("tableName")String tableName);

    Integer checkTableExists(@Param("tableName")String tableName);

    Integer delete(@Param("tableName")String tableName);

    Integer deleteByShopId(@Param("tableName")String tableName,@Param("shopIdList") Set<String> shopIds);

    List<MidCategoryStockDO> geMidCategoryQty(@Param("tableName")String tableName,@Param("shopIdList")Set<String> shopIdList);

    List<IssueNeedStockDO> getSkuStockQty(Map<String,Object> param);

    List<IssueNeedStockDO> getIssueNeedStockList(@Param("needTabName")String needTabName, @Param("inTabName")String inTableName,
                                                 @Param("shopIds") Set<String> shopIds, @Param("matCodes") List<String> matCodes, @Param("sizeNames") List<String> needSizeNames);

    List<IssueSumAvgDO> getSmallSumAvg(@Param("inTabName")String inTableName,@Param("shopIds") Set<String> shopIds);
    List<IssueSumAvgDO> getMidSumAvg(@Param("inTabName")String inTableName,@Param("shopIds") Set<String> shopIds);
    List<IssueSumAvgDO> getBigSumAvg(@Param("inTabName")String inTableName,@Param("shopIds") Set<String> shopIds);
    List<IssueSumAvgDO> getAllSumAvg(@Param("inTabName")String inTableName, @Param("shopIds") Set<String> shopIds);

    List<IssueSumAvgDO> getDistinctMatCode(@Param("needTabName")String needTableName, @Param("shopIds") Set<String> shopIds);

    Integer updateWarehouseStatus(@Param("id")int id,@Param("status") int status);

    Integer updateTableStatus(@Param("tableName")String tableName,@Param("shopId") String shopId,@Param("status") int status,@Param("oldStatus") int oldStatus);

}
