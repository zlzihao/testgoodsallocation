package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ImportConfigDOMapper {
    List<ImportConfigDO> queryByTplCode(String tplCode);

    int insertSql(@Param("insertSql") String insertSql);

    int deleteSql(@Param("deleteSql") String deleteSql);
}
