package cn.nome.saas.allocation.scheduler;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueRestDOMapper2;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author chentaikuang
 */
@Component
public class IssueTabBakScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${scheduler.truncate.table.names}")
    private String truncateTabNames;

    @Value("${scheduler.bak.table.switch:0}")
    private int bakTabSwitch;

    @Autowired
    private IssueRestDOMapper2 issueRestDOMapper2;

    //当天23点半
    @Scheduled(cron = "0 30 23 * * ? ")
    //每X秒执行
//    @Scheduled(cron = "0/30 * * * * ?")
//    @Scheduled(cron = "0 0/5 * * * ? ")
    public void execute() {

        LOGGER.debug("SCHEDULER BAK TAB SWITCH:{}", bakTabSwitch);
        if (bakTabSwitch == 0) {
            return;
        }

        LOGGER.debug("SCHEDULER BAK TAB NAMES:{}", truncateTabNames);
        if (StringUtils.isBlank(truncateTabNames)) {
            return;
        }

        String[] tabNames = truncateTabNames.split(",");
        int rst = 0;
        for (String tabName : tabNames) {

            if (StringUtils.isBlank(tabName)) {
                LOGGER.debug("BAK TAB NAME NULL:{}", tabName);
                continue;
            }

            rst = bakTab(tabName);

            LOGGER.debug("BAK {},rst:{}", tabName, rst);
        }
        LOGGER.debug("SCHEDULER BAK TAB DONE!");
    }

    /**
     * 备份数据表
     *
     * @param tabName
     */
    private int bakTab(String tabName) {
        try {
            String dateStr = DateUtil.format(new Date(), "yyMMddHHmmss");
            issueRestDOMapper2.createAndCopyTab((tabName + "_" + dateStr), tabName);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("BAK TAB ERR:{}", e.getMessage());
            return 0;
        }
        return 1;
    }
}