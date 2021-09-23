package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.AllocationDetail;
import cn.nome.saas.allocation.model.allocation.TaskStoreCommodity;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationFlowDO;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskStoreDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskStoreDOV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * AllocationStockDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/22
 */
public interface AllocationFlowDOMapper {

    Integer batchInsert(@Param("list") List<AllocationFlowDO> allocationFlowDOList);

    Integer batchUpdate(@Param("list") List<AllocationFlowDO> allocationFlowDOList);

    void deleteByParam(@Param("taskId") int taskId);

    AllocationDetail getAllocationSummary(Map<String,Object> param);

    List<AllocationDetail> getAllocationDetail(Map<String,Object> param);
}
