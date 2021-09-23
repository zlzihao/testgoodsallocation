package test;

import cn.nome.saas.allocation.scheduler.IssueTabBakScheduler;
import cn.nome.saas.allocation.scheduler.TruncateIssueTabScheduler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SchedulerTest extends ApplicationTests {
    private static Logger logger = LoggerFactory.getLogger(test.SchedulerTest.class);

    @Autowired
    public TruncateIssueTabScheduler truncateIssueTabScheduler;

    @Autowired
    public IssueTabBakScheduler issueTabBakScheduler;

    @Test
    public void truncateIssueTabSchedulerTest() {
        truncateIssueTabScheduler.execute();
        logger.info("truncate scheduler test over");
    }

    @Test
    public void issueTabBakSchedulerTest() {
        issueTabBakScheduler.execute();
        logger.info("bak scheduler test over");
    }

}
