package cn.nome.saas.allocation.repository.dao.vertical;

import cn.nome.platform.common.shard.annotation.Param;
import cn.nome.saas.allocation.repository.entity.vertical.DwsSaleStockMoveDDO;

import java.util.List;
import java.util.Map;

public interface DwsSaleStockMoveDDOMapper {

    DwsSaleStockMoveDDO selectByParam(@Param("ShopId") String ShopId, @Param("MatCode") String MatCode, @Param("SizeName") String SizeName);

}
