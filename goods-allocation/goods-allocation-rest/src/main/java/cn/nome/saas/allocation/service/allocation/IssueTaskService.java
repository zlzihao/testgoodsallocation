package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.utils.DateUtils;
import cn.nome.saas.allocation.model.issue.IssueTask;
import cn.nome.saas.allocation.repository.dao.allocation.IssueTaskDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * IssueTaskService
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
@Service
public class IssueTaskService {

    @Autowired
    IssueTaskDOMapper issueTaskDOMapper;

    public IssueTask createTask(int runStatus) {
        Date now = new Date();
        IssueTask task = new IssueTask();
        task.setName(DateUtils.toString(now, "yyyy-MM-dd HH:mm:ss"));
        task.setRunTime(now);
        task.setCreatedAt(now);
        task.setTaskStatus(runStatus);
        task.setOperator(AuthUtil.getSessionUserId());

        // 重算重跑任务
        if (runStatus == 2) {
            task.setRemark("RERUN");
        } else {
            task.setRemark("RUN");
        }
        return task;
    }

    public int addTask(IssueTask task) {

        IssueTaskDO issueTaskDO = new IssueTaskDO();
        BeanUtils.copyProperties(task,issueTaskDO);

        issueTaskDOMapper.addTask(issueTaskDO);

        return issueTaskDO.getId();

    }
}
