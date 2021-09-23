package cn.nome.saas.search.repository.dao;

import cn.nome.saas.search.repository.entity.SearchWordConfDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchWordConfMapper {

    int insertSelective(SearchWordConfDO record);

    SearchWordConfDO selectByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(SearchWordConfDO record);

    List<SearchWordConfDO> validWordsByType(@Param("wordType") Integer wordType);

    List<SearchWordConfDO> validPageWordsByType(@Param("wordType") Integer wordType, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    int validCountWordsByType(@Param("wordType") Integer wordType);

    int setRelease(@Param("id") Integer id);

    int setStop(@Param("id") Integer id);
}
