package cn.nome.saas.allocation.repository.dao.vertical;


import cn.nome.saas.allocation.repository.entity.vertical.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsShopDO;

import java.util.List;
import java.util.Map;

public interface DwsShopDOMapper {

    
    List<DwsShopDO> getList();

    public List<DwsDimShopDO> selectShopListById(Map<String, Object> param);

    public List<Map<String,String>> selectShopIdByCode(Map<String, Object> param);

    List<String> selectShopIdByParam(Map<String, Object> param);

    public List<DwsDimShopDO> selectAllShopList();

    List<DwsDimShopDO> selectShopListByRegioneProvinceCity(Map<String,Object> param);
}