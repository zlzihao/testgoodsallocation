package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.ShopDisplayDesignData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ShopDisplayDesignDOMapper {

    List<ShopDisplayDesignData> shopDisplayData();

    int batchInsertTab(@Param("importData") List<ShopDisplayDesignData> importData);

    /**
     * 获取大类列表
     * @return
     */
    List<String> getLargeCategoryList();

    /**
     * 获取中类列表
     * @return
     */
    List<String> getMidCategoryList(@Param("largeCategory") String[] largeCategory);

    /**
     * 根据参数获取总条数
     * @param param
     * @return
     */
    int getCount(Map<String, Object> param);

    /**
     * 根据参数获取ShopDisplayDesignData列表
     * @param param
     * @return
     */
    List<ShopDisplayDesignData> selectByPage(Map<String, Object> param);

    /**
     * 根据用户id获取仓位表
     * @return
     */
    List<ShopDisplayDesignData> getShopDisplayDesignListByUserId(@Param("userId") String userId);

    /**
     * 删除指定的shopId
     * @param shopIdSet
     * @return
     */
    int delByShopIds(@Param("shopIds") Set<String> shopIdSet);
}
