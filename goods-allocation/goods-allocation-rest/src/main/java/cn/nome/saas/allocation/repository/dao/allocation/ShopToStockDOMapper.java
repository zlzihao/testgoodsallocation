package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.req.ShopToStockReq;
import cn.nome.saas.allocation.repository.entity.allocation.ShopToStockDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShopToStockDOMapper {

    /**
     * 根据参数获取总条数
     *
     * @param param
     * @return
     */
    int getCount(Map<String, Object> param);


    /**
     * 根据参数获取ForbiddenSingleItemDO列表
     *
     * @param param
     * @return
     */
    List<ShopToStockDo> selectByPage(Map<String, Object> param);

    int batchInsertTab(@Param("importData") List<ShopToStockDo> importData);

    int batchInsertByOperate(@Param("importData") List<ShopToStockDo> importData);


    List<ShopToStockDo> selectByCondition(@Param("record") ShopToStockReq record, @Param("page") Page page);

    int pageCount(@Param("record") ShopToStockReq record);

    int batchUpdate(@Param("list") List<ShopToStockDo> list);

    List<ShopToStockDo> selectByStatus(@Param("record") ShopToStockReq record);


    List<ShopToStockDo> selectByOperate(@Param("operateId") Integer operateId, @Param("page") Page page);


    List<ShopToStockDo> selectByNotRead(@Param("record") ShopToStockDo record);

    Integer delete(@Param("list") List<Integer> list);


    Integer commit(@Param("list") List<ShopToStockDo> list);


    Integer updateByRead(@Param("shopCode") String shopCode);

    Integer batchUpdateNewStatusByOperate(List<ShopToStockDo> list);

}
