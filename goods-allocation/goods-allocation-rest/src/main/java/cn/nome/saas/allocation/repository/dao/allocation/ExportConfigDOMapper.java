package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.ExportConfigDO;
import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/15 14:13
 * @description：导出配置
 * @modified By：
 * @version: 1.0.0$
 */
public interface ExportConfigDOMapper {
    List<ExportConfigDO> queryByTplCode(String tplCode);

    List<Map> exeSql(@Param("exeSql") String exeSql);
}
