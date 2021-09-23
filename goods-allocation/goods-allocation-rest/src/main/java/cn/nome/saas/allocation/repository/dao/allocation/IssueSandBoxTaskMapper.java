package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.repository.entity.allocation.IssueDayPeriod;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * IssueDayPeriodMapper
 *
 * @author Bruce01.fan
 * @date 2019/11/27
 */
public interface IssueSandBoxTaskMapper {

    IssueSandBoxTask getSandBoxTaskByName(@Param("taskName")String taskName);

    Integer addSandBoxTask(IssueSandBoxTask issueSandBoxTask);

    List<IssueSandBoxTask> querySandBoxTaskList(@Param("keyWord")String keyWord, @Param("page")Page page);

    Integer querySandBoxTaskCount(@Param("keyWord")String keyWord);

    List<IssueSandBoxTask> selectByParam(Map<String, Object> param);

    Integer updateStatus(@Param("taskId")int taskId,@Param("status")int status);

}
