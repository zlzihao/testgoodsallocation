package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.DisplayDataV2;
import cn.nome.saas.allocation.repository.entity.allocation.DisplayDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DisplayDOMapper {

    List<DisplayDo> displayData();

    /**
     * 获取陈列大类列表
     * @return
     */
    Set<String> getLargeCategoryList();


    /**
     * 获取陈列中类列表
     * @return
     */
    Set<String> getMidCategoryList(@Param("largeCategory") String[] largeCategory);

    /**
     * 获取陈列小类列表
     * @return
     */
    Set<String> getSmallCategoryList(@Param("midCategory") String[] midCategory);

    int batchInsertTab(@Param("importData") List<DisplayDataV2> importData);

    /**
     * 根据参数获取总条数
     * @param param
     * @return
     */
    int getCount(Map<String, Object> param);

    /**
     * 根据参数获取ForbiddenSingleItemDO列表
     * @param param
     * @return
     */
    List<DisplayDo> selectByPage(Map<String, Object> param);
}
