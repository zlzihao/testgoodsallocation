package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueOutStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * QdIssueDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
public interface QdIssueTaskDOMapper {

    Integer addTask(@Param("task") QdIssueTaskDO qdIssueTaskDO);

    Integer updateTask(@Param("taskId") int taskId,@Param("taskStatus")int taskStatus);

    Integer updateRemark(@Param("taskId") int taskId,@Param("remark")String remark);

    List<QdIssueTaskDO> taskList(Map<String,Object> param);

    QdIssueTaskDO getRunableTask();

    Integer getRunningTaskCount();

    QdIssueTaskDO getReadyTask();

    /**
     * 是否存在相同时间的任务
     * @return
     */
    QdIssueTaskDO queryByRunTime(@Param("runTime") String runTime);

    Integer deleteById(@Param("taskId") int taskId);
}
