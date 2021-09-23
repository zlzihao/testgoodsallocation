package cn.nome.saas.allocation.repository.dao.vertical;

import cn.nome.saas.allocation.feign.model.DisplayPlan;
import cn.nome.saas.allocation.repository.entity.vertical.DwsDimGoodsMaterialSizeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DwsGoodsMaterialSizeDOMapper {

    List<DwsDimGoodsMaterialSizeDO> selectGoodsListByMatCode(@Param("matCodes") List<String> matCodes);

}