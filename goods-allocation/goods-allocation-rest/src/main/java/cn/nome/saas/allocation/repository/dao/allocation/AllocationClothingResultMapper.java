package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.AllocationClothingResultDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AllocationClothingResultMapper
 *
 * @author Bruce01.fan
 * @date 2019/12/12
 */
public interface AllocationClothingResultMapper {

    Integer batchInsert(@Param("list") List<AllocationClothingResultDO> list);

    List<AllocationClothingResultDO> selectByTaskId(@Param("taskId")Integer taskId);

    Integer deleteByParam(@Param("taskId") int taskId);
}
