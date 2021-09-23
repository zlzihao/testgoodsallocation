package cn.nome.saas.search.repository.dao;

import cn.nome.saas.search.repository.entity.SearchLogDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 搜索日志表
 */
@Repository
public interface SearchLogMapper {

    int deleteByPrimaryKey(@Param("id") Integer id);

    int insertSelective(SearchLogDO record);

    SearchLogDO selectByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(SearchLogDO record);

    List<SearchLogDO> getLogBeforeDays(@Param("beforeDays") int beforeDays);
}
