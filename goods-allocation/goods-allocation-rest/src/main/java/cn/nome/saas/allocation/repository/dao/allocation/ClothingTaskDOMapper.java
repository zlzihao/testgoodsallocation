package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.allocation.Paramater;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskProgressDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * TaskMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public interface ClothingTaskDOMapper {

    /**
     * 服装齐码任务
     */
    TaskDO getTask(int taskId);

    List<TaskDO> getNeedRunTaskList();

    Integer updateTaskToRunning(int taskId);

    Integer updateTaskToFail(@Param("taskId") int taskId, @Param("remark") String remark);

    Integer updateTaskProcess(@Param("taskId") int taskId, @Param("process") int process);

    Integer updateTaskToFinish(int taskId);

    List<Integer> selectAllRejectTask(@Param("taskId") int taskId);

    int checkTaskName(@Param("name") String taskName);

    Integer createTask(TaskDO taskDO);

    Integer updateTask(TaskDO taskDO);

    List<TaskDO> getTaskList(Map<String,Object> param);

    void cancelTask(int taskId);

    Integer selectTaskNum(Map<String,Object> param);
}
