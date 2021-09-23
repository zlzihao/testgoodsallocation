package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.AllocationClothingInvalidGoods;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationClothingResultDO;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationClothingValidGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AllocationClothingResultMapper
 *
 * @author Bruce01.fan
 * @date 2019/12/12
 */
public interface AllocationClothingInvalidGoodsMapper {

    Integer batchInsertValidGoods(@Param("list") List<AllocationClothingValidGoods> list);

    Integer batchInsert(@Param("list") List<AllocationClothingInvalidGoods> list);

    Integer deleteByParam(@Param("taskId") int taskId,@Param("type") int type);

    Integer deleteValidGoodsByParam(@Param("taskId") int taskId,@Param("type") int type);

    List<AllocationClothingInvalidGoods> selectInvalidGoods(@Param("tableName") String tableName,@Param("taskId") int taskId,@Param("shopId") String shopId);

}
