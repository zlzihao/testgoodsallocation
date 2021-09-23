package cn.nome.saas.allocation.scheduler;

import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.service.allocation.IssueRecalcService;
import cn.nome.saas.allocation.service.allocation.RecalcTaskService;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chentaikuang
 */
/*
@Component
public class RecalcSingleShopScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IssueRecalcService issueRecalcService;

    @Value("${scheduler.recalc.shop.switch:0}")
    private int recalcShopSwitch;

    //每X秒执行
//    @Scheduled(cron = "0/10 * * * * ? ")
    @Scheduled(cron = "0 0/2 * * * ? ")
    public void execute() {

        LOGGER.info("SCHEDULER RECALC SHOP TASK SWITCH:{}", recalcShopSwitch);
        if (recalcShopSwitch == 0) {
            return;
        }
        issueRecalcService.schedulerRecalcTask();
    }
}*/
