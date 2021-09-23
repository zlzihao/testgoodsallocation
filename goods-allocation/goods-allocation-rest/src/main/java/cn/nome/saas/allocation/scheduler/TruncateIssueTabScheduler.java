package cn.nome.saas.allocation.scheduler;

import cn.nome.saas.allocation.repository.old.allocation.dao.IssueRestDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chentaikuang
 */
@Component
public class TruncateIssueTabScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${scheduler.truncate.table.names}")
    private String truncateTabNames;
    @Value("${scheduler.truncate.table.switch:0}")
    private int truncateTabSwitch;

    @Autowired
    private IssueRestService issueRestService;

    @Autowired
    private IssueRestDOMapper2 issueRestDOMapper2;

    //每天凌晨1点
    @Scheduled(cron = "0 0 1 * * ? ")
    //每X秒执行
    public void execute() {

        LOGGER.debug("SCHEDULER TRUNCATE TAB SWITCH:{}", truncateTabSwitch);
        if (truncateTabSwitch == 0) {
            return;
        }

        LOGGER.debug("SCHEDULER TRUNCATE TAB NAMES:{}", truncateTabNames);
        if (StringUtils.isBlank(truncateTabNames)) {
            return;
        }

        String[] tabNames = truncateTabNames.split(",");
        int rst = 0;
        for (String tabName : tabNames) {

            if (StringUtils.isBlank(tabName)) {
                LOGGER.debug("TRUNCATE TAB NAME NULL:{}", tabName);
                continue;
            }

            rst = issueRestService.truncateTab(tabName);
            LOGGER.debug("TRUNCATE {},rst:{}", tabName, rst);
        }
        LOGGER.debug("SCHEDULER TRUNCATE TAB DONE!");
    }
}