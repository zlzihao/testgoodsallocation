package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.allocation.Paramater;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskProgressDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskStoreDO;
import cn.nome.saas.allocation.repository.entity.allocation.TaskStoreDOV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * TaskMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public interface TaskDOMapper {

    Integer createTask(TaskDO taskDO);

    Integer updateTask(TaskDO taskDO);

    Integer getRunningTaskCount();

    Integer selectTaskNum(Map<String,Object> param);

    List<TaskDO> getTaskList(Map<String,Object> param);

    TaskDO getTask(int taskId);

    int checkTaskName(@Param("name") String taskName);

    List<Paramater> getCloseTaskList();

    void cancelTask(int taskId);

    List<TaskDO>getNeedRunTaskList(@Param("allocationType") int allocationType);

    Integer updateTaskToRunning(int taskId);

    Integer updateTaskToFinish(int taskId);

    Integer updateTaskToFail(@Param("taskId")int taskId,@Param("remark")String remark);

    Integer updateTaskProcess(@Param("taskId") int taskId,@Param("process") int process);

    List<TaskProgressDO> getTaskProgress(@Param("taskId") int taskId);

    List<Integer> selectAllRejectTask(@Param("taskId") int taskId);

    //int createTaskProgress(TaskProgressDO taskProgressDO);
}
