package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.GoodsInfoTask;
import cn.nome.saas.allocation.model.issue.GoodsAreaLevelDetailDo;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GoodsInfoDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
public interface GoodsInfoDOMapper {

    List<GoodsInfoDO> selectGoodsList();

    public List<GoodsInfoDO> selectGoodsListByMatCode(@Param("list")List matCodeList);

    List<Map<String,String>> getMatCodeBySmallCategory(@Param("list")List list);

    public List<String> selectMatCodeList(Map<String,Object> param);

    List<GoodsInfoDO> goodsInfoData();

    int batchInsertTab(@Param("importData") List<GoodsInfoDO> importData);

    /**
     * 获取商品陈列大类列表
     * @return
     */
    List<String> getGoodsLargeCategoryList();


    /**
     * 获取商品陈列中类列表
     * @return
     */
    List<String> getGoodsMidCategoryList(@Param("largeCategory") String[] largeCategory);

    /**
     * 获取商品陈列小类列表
     * @return
     */
    List<String> getGoodsSmallCategoryList(@Param("midCategory") String[] midCategory);

    /**
     * 根据参数获取总条数
     * @param param
     * @return
     */
    int getCount(Map<String, Object> param);

    /**
     * 根据参数获取GoodsInfoDO列表
     * @param param
     * @return
     */
    List<GoodsInfoDO> selectByPage(Map<String, Object> param);

    List<GoodsAreaLevelDetailDo> getMatCodeFromLevelDetail(@Param("shopIds") Set<String> shopIds);

    Integer checkGoodsTask(@Param("matCode")String matCode);

    Integer insertGoodsTask(@Param("matCode")String matCode);

    List<GoodsInfoTask> getLastGoodsTask();

    Integer checkGoodsTaskRunningTask();

    Integer updateGoodsInfoTaskToReady(@Param("id") int taskId);

    Integer updateGoodsInfoTaskToRun(@Param("id") int taskId);

    Integer updateGoodsInfoTaskFinish(@Param("id") int taskId);

    Integer updateGoodsInfoTaskToRetry(@Param("id") int taskId);
}
