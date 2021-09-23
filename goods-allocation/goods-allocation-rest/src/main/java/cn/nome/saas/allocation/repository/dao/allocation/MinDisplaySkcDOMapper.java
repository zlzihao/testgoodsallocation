package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.MinDisplaySkcDO;
import cn.nome.saas.allocation.model.issue.MinDisplaySkcData;
import java.util.Map;
import java.util.List;

/**
 * MinDisplaySkcDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
public interface MinDisplaySkcDOMapper {

    List<MinDisplaySkcDO> getMinDisplaySkcList();

    /**
     * 根据参数获取总条数
     * @param param
     * @return
     */
    int getCount(Map<String, Object> param);

    /**
     * 根据参数获取MinDisplaySkcDO列表
     * @param param
     * @return
     */
    List<MinDisplaySkcData> selectByPage(Map<String, Object> param);
}
