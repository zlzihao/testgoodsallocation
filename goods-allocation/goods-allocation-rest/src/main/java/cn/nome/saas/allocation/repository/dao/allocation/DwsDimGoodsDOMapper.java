package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.Category;
import cn.nome.saas.allocation.model.allocation.Paramater;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimGoodsDO;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimGoodsExDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * DwsDimGoodsDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/2
 */
public interface DwsDimGoodsDOMapper {

    List<Paramater> getSeasonList();

    List<String> getSeasonNameList();

    List<Paramater> getYearNoList();

    List<Category> getSmallCategory(@Param("midCategoryCode") String midCategoryCode, @Param("tableName") String tableName,
                                    @Param("type") int type);

    List<Category> getMidCategory(@Param("tableName") String tableName, @Param("type") int type);

    public List<String> getMatCodeListBy(@Param("year")String year, @Param("season")String season);

    public List<String> getMatCodeLisyByCategoryCode(@Param("categoryCode")String categoryCode,@Param("midCategoryCode")String midCategoryCode,@Param("smallCategoryCode")String smallCategoryCode);

    int insertBatchDataCenterData(List<cn.nome.saas.allocation.repository.entity.vertical.DwsDimGoodsDO> dwsDimGoodsDOS);

    int clearAll();

    List<String> getMatCodeList(Map<String,Object> param);

    List<DwsDimGoodsDO> getList();

    List<DwsDimGoodsDO> getSimpleList();

    List<DwsDimGoodsExDO> queryAvgPriceForQd();
}
