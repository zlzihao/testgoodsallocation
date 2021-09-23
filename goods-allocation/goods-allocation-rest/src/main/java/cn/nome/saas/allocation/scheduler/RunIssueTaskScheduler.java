package cn.nome.saas.allocation.scheduler;

import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*@Component
public class RunIssueTaskScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${scheduler.run.issue.switch:0}")
    private int runIssueSwitch;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    IssueRestService issueRestService;

    @Scheduled(cron = "0 30 8 * * ? ")
    //每X秒执行
//    @Scheduled(cron = "0/5 * * * * ?")
    public void execute() {

        LOGGER.debug("SCHEDULER RUN ISSUE TASK SWITCH:{}", runIssueSwitch);
        if (runIssueSwitch == 0) {
            return;
        }
        IssueTask task = issueRestService.createTask(0);
        task.setOperator("scheduler");
        asyncTask.runIssueTask(task);
        LOGGER.debug("SCHEDULER RUN ISSUE TASK DONE!");
    }
}*/
