package cn.nome.saas.sdc.repository.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.sdc.repository.entity.SeasonChangeDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@Repository
public interface SeasonChangeMapper {
    List<SeasonChangeDO> getPageList(@Param("record") SeasonChangeDO record, @Param("page") Page page);

    List<SeasonChangeDO> selectByCondition(@Param("record") SeasonChangeDO record);

    int  pageCount(@Param("record") SeasonChangeDO record);

    List<SeasonChangeDO> getByCondition(@Param("record") SeasonChangeDO record);

    int update(@Param("record") SeasonChangeDO record);

    int batchUpdate(@Param("list") List<SeasonChangeDO> list);

    int batchInsert(@Param("list") List<SeasonChangeDO> list);

    int deleted(@Param("record") SeasonChangeDO record);
}
