package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.IssueTask;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import org.apache.ibatis.annotations.Param;

/**
 * IssueTaskDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public interface IssueTaskDOMapper {

    /**
     * 新配发新增任务
     * @param task
     * @return
     */
    int addTask(@Param("task") IssueTaskDO task);


    /**
     * 跑完配发任务后更新任务状态
     * @param taskId
     */
    void updateTaskStatus(@Param("taskId") int taskId);
}
