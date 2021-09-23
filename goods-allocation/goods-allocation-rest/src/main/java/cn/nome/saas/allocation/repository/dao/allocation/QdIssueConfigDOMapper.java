package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.repository.entity.allocation.QdIssueConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueConfigDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public interface QdIssueConfigDOMapper {

    List<QdIssueConfigDO> getConfig();

    List<QdIssueConfigDO> getConfigByType(@Param("type")int type);

    Integer insertConfig(@Param("configDO")QdIssueConfigDO configDO);

    Integer updateConfig(@Param("configDO")QdIssueConfigDO configDO);
}
