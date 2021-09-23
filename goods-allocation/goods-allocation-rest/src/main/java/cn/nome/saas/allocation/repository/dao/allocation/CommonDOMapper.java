package cn.nome.saas.allocation.repository.dao.allocation;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface CommonDOMapper {

    Map<String, String> showTableExists(@Param("tableName") String tableName);

    void truncateTable(@Param("tableName") String tableName);

    void createAndCopyTab(@Param("batTableName") String batTableName, @Param("tableName") String tableName);

    void selectAndInsert(@Param("batTableName") String batTableName, @Param("tableName") String tableName);
}
