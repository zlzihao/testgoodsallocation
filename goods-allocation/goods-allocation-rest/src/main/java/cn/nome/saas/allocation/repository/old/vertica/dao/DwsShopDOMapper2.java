package cn.nome.saas.allocation.repository.old.vertica.dao;

import cn.nome.saas.allocation.model.old.OldStoreData;
import cn.nome.saas.allocation.model.old.StoreSalesData;
import cn.nome.saas.allocation.repository.old.vertica.entity.DwsDimShopDO;
import cn.nome.saas.allocation.repository.old.vertica.entity.DwsShopDO;

import java.util.List;
import java.util.Map;

public interface DwsShopDOMapper2 {
    int insert(DwsShopDO record);

    int insertSelective(DwsShopDO record);
    
    List<DwsShopDO> getList();
    
    List<StoreSalesData> selectStoreSalesList(String start_date, String end_date);
    
    List<OldStoreData> selectOldStoresByShopIdList(List<String> list);
    
    List<OldStoreData> selectAllOldStores();

    public List<DwsDimShopDO> selectAllShopList();

    public List<Map<String,String>> selectShopIdByCode(Map<String, Object> param);

    public List<DwsDimShopDO> selectShopListById(Map<String, Object> param);

    List<String> selectShopIdByParam(Map<String, Object> param);

    List<DwsDimShopDO> selectShopListByRegioneProvinceCity(Map<String, Object> param);
}