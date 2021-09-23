package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.repository.entity.WarehouseDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@Repository
public interface WareHouseConfigMapper {
    List<WarehouseDO> getPageList(@Param("record") WarehouseDO record, @Param("page") Page page);

    int pageCount(@Param("record") WarehouseDO record);

    List<WarehouseDO> getByProvinceAndWareHouse(@Param("record") WarehouseDO record);

    int update(@Param("record") WarehouseDO record);

    int updateWareHouse(@Param("record") WarehouseDO record);

    int delete(@Param("record") WarehouseDO record);

    int insert(@Param("record") WarehouseDO record);
}
