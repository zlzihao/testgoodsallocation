package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.ShopInfoTask;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShopInfoDOMapper {

    List<ShopInfoData> shopInfoData();

    int batchInsertTab(@Param("importData") List<ShopInfoData> importData);

    /**
     * 更新自定义属性名称
     * @param attrKeys
     * @return
     */
    int updateAttrKeys(@Param("attrKeys") String attrKeys);

    /**
     * 根据id更新
     * @param shopInfoData
     * @return
     */
    int updateById(ShopInfoData shopInfoData);

    /**
     * 根据shopId更新
     * @param shopInfoData 123
     * @return 123
     */
    int updateByShopId(ShopInfoData shopInfoData);

    /**
     * 获取货盘区域列表
     * @return
     */
    List<String> getGoodsAreaList();

    /**
     * 获取门店登记列表
     * @return
     */
    List<String> getShopLvList();

    /**
     * 获取用户管理的门店
     * @return
     */
    List<ShopInfoData> getShopListOfUser(@Param("userName") String userName);

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
    List<ShopInfoData> selectByPage(Map<String, Object> param);

    ShopInfoData selectById(@Param("id")Integer id);

    Integer insertShopTask(@Param("shopId")String shopId);

    List<ShopInfoTask> getLastShopTask();

    Integer checkShopTask(@Param("shopId")String shopId);

    Integer updateShopInfoTaskToReady(@Param("id") int taskId);

    Integer updateShopInfoTaskToRun(@Param("id") int taskId);

    Integer updateShopInfoTaskToReRun(@Param("id") int taskId);

    Integer updateShopInfoTaskFinish(@Param("id") int taskId);

    Integer updateShopInfoTaskRetry(@Param("id") int taskId);

    Integer checkShopTaskRunningTask();

    List<ShopInfoTask> getShopTaskRunningTask();

}
