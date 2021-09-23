package cn.nome.saas.allocation.repository.dao.vertical;

import cn.nome.saas.allocation.repository.entity.vertical.DwsStockSkuDDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DwsStockSkuDDOMapper {

   DwsStockSkuDDO selectByParam(@Param("ShopId") String ShopId,@Param("MatCode") String MatCode,@Param("SizeName") String SizeName);

}
