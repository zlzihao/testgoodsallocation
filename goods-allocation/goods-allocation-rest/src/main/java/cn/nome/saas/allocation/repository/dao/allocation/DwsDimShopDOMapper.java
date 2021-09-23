package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.model.issue.DwsDimShopData;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.DwsShopDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DwsDimShopDOMapper {

    List<DwsShopDO> getList();

    public List<DwsDimShopDO> selectShopListById(Map<String, Object> param);

    public List<Map<String,String>> selectShopIdByCode(Map<String, Object> param);

    List<String> selectShopIdByParam(Map<String, Object> param);

    public List<DwsDimShopDO> selectAllShopList();

    List<DwsDimShopDO> selectShopListByRegioneProvinceCity(Map<String,Object> param);

    List<DwsDimShopDO> selectShopListByRegioneProvinceCityIn(Map<String,Object> param);

    List<DwsDimShopData> getShopIdByCode(@Param("shopCodes") List<String> shopCodes);
    int insertBatch(List<DwsDimShopDO> dwsDimShopDOS);

    int insertBatchDataCenterData(List<cn.nome.saas.allocation.repository.entity.vertical.DwsDimShopDO> dwsDimShopDOS);

    int clearAll();

    List<DwsDimShopData> getAllList();
}