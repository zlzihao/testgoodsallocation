package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.AllocationDetail;
import cn.nome.saas.allocation.model.allocation.AllocationDetailRecord;
import cn.nome.saas.allocation.model.allocation.TaskStoreCommodity;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AllocationStockDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/22
 */
public interface AllocationStockDOMapper {

    List<AllocationStockDO> selectAllocationStockByParam(Map<String,Object> param);

    Integer batchInsert(Map<String,Object> param);

    Integer deleteByParam(Map<String,Object> param);

    List<TaskStoreDO> getTaskStoreList(Map<String,Object> param);

    List<TaskStoreDO> getTaskStoreSupplyList(Map<String,Object> param);

    List<TaskStoreDOV2> getTaskAllocationStoreList(Map<String,Object> param);

    Integer getTaskStoreCommodityCount(Map<String,Object> param);

    List<TaskStoreCommodity> getTaskStoreCommodityList(Map<String,Object> param);

    List<TaskStoreCommodity> getTaskStorePairCommodityList(Map<String,Object> param);

    List<AllocationDetailRecord> selectAllocationDetailList(@Param("taskId")int taskId,@Param("tableName")String tableName);

    List<AllocationDetailRecord> selectClothingAllocationDetailList(@Param("taskId")int taskId,@Param("tableName")String tableName);

    List<AllocationFragementalGoodsDO> selectFragmentalGoods(@Param("taskId")int taskId, @Param("tableName")String tableName,@Param("supplyShopList") Set<String> shopList);

    List<AllocationDemandGoodsDO> selectDemandShopGoods(@Param("taskId")int taskId, @Param("tableName")String tableName);

    List<String> selectAllocationShop(@Param("taskId")int taskId);

    List<String> selectAllocationMatCode(@Param("taskId")int taskId);

    List<AllocationStockDO> selectAllocationStockList(@Param("taskId")int taskId);

}
