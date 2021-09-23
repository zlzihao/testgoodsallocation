package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.allocation.DemandStock;
import cn.nome.saas.allocation.model.allocation.DemandStockDetail;
import cn.nome.saas.allocation.model.allocation.SupplyStock;
import cn.nome.saas.allocation.repository.entity.allocation.OutOfStockGoodsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * OutOfStockGoodsDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/20
 */
public interface OutOfStockGoodsDOMapper {

    List<Map<String,String>> selectSkuList(Map<String,Object> param);

    List<OutOfStockGoodsDO> selectStockGoodsBySku(Map<String,Object> param);

    List<OutOfStockGoodsDO> selectStockGoodsByShop(@Param("tableName")String tableName,@Param("shopIdList")List<String> shopIdList);

    /**
     * 需求池
     * @param param
     * @return
     */
    List<OutOfStockGoodsDO> selectDemandList(Map<String,Object> param);

    /**
     * 供给池
     * @param param
     * @return
     */
    List<OutOfStockGoodsDO> selectSupplyList(Map<String,Object> param);

    Integer batchUpdateSaleQty(@Param("tableName")String tableName,@Param("list") List<OutOfStockGoodsDO> list);

    Integer checkTableExists(@Param("tableName")String tableName);

    void clearAll(@Param("tableName")String tableName);

    Integer createNewTable(@Param("tableName") String tableName);

    void addIndex(@Param("tableName") String tableName);

    Integer insertSelective(Map<String,Object> param);

    Integer updateSelective(Map<String,Object> param);

    int getDemandStockStatsCount(Map<String,Object> param);

    List<DemandStock> getDemandStockStats(Map<String,Object> param);

    int getSupplyShopCount(Map<String,Object> param);

    int getDemandStockDetailCount(Map<String,Object> param);

    List<DemandStockDetail> getDemandStockDetailList(Map<String,Object> param);

    int getSupplyStockStatsCount(Map<String,Object> param);

    List<SupplyStock> getSupplyStockStats(Map<String,Object> param);

    List<String> getAllMatCodeList(Map<String,Object> param);

}
