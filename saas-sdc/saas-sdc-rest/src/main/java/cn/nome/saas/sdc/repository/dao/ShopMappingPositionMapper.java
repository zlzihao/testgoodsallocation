package cn.nome.saas.sdc.repository.dao;

import cn.nome.saas.sdc.model.vo.ShopMappingPositionVO;
import cn.nome.saas.sdc.repository.entity.ShopMappingPositionDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@Repository
public interface ShopMappingPositionMapper {

    Integer batchInsert(@Param("list") List<ShopMappingPositionDO> list);

    Integer batchUpdate(@Param("list") List<ShopMappingPositionDO> list);

    List<ShopMappingPositionDO> selectByCondition(@Param("list") List<ShopMappingPositionVO> list);
}
