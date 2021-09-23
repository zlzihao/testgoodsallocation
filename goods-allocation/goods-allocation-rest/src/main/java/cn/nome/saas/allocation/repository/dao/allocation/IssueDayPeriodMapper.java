package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.IssueDayPeriod;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * IssueDayPeriodMapper
 *
 * @author Bruce01.fan
 * @date 2019/11/27
 */
public interface IssueDayPeriodMapper {

    void clearData();

    Integer addIssueDay(@Param("list")List<IssueDayPeriod> list);

    List<IssueDayPeriod> getIssueDay(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

}
