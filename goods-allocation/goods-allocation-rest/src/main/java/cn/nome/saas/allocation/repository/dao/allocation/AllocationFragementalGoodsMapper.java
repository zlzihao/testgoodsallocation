package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.AllocationFragementalGoodsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AllocationFragementalGoodsMapper
 *
 * @author Bruce01.fan
 * @date 2019/12/11
 */
public interface AllocationFragementalGoodsMapper {

    Integer batchInsert(@Param("list") List<AllocationFragementalGoodsDO> list);

    Integer deleteByParam(@Param("taskId") int taskId);

    Integer batchUpdate(@Param("list") List<AllocationFragementalGoodsDO> list);

}
