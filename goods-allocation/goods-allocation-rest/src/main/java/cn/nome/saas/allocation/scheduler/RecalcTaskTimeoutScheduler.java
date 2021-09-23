package cn.nome.saas.allocation.scheduler;

import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import cn.nome.saas.allocation.service.allocation.RecalcTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chentaikuang
 */
@Component
public class RecalcTaskTimeoutScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RecalcTaskService recalcTaskService;

//    @Scheduled(cron = "0/10 * * * * ? ")
    @Scheduled(cron = "0 0/2 * * * ? ")
    public void execute() {
        List<RecalcTaskDo> tasks = recalcTaskService.timeoutTask();
        if (tasks == null){
            return;
        }
        for (RecalcTaskDo recalcTaskDo:tasks) {
            recalcTaskService.timeoutCancle(recalcTaskDo.getId());
        }
    }
}