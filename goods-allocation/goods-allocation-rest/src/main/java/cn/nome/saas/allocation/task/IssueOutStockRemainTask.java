package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.model.old.issue.IssueDetailDistStock;
import cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author chentaikuang
 */
public class IssueOutStockRemainTask extends RecursiveTask<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IssueOutStockRemainTask.class);

    private IssueService issueService;

    private List<IssueOutStockDO> issueOutStockDOs;
    private int taskId;
    private int start;
    private int end;

    public IssueOutStockRemainTask(int taskId, int start, int end, List<IssueOutStockDO> issueOutStockDOs, IssueService issueService) {
        this.taskId = taskId;
        this.start = start;
        this.end = end;
        this.issueOutStockDOs = issueOutStockDOs;
        this.issueService = issueService;
    }

    @Override
    protected Integer compute() {
        int rst = 0;
        boolean compute = (this.end - this.start) <= 100;
        if (compute) {
            IssueOutStockDO issueOutStockDO = null;
            for (int i = start; i < end; i++) {
                try {
                    issueOutStockDO = this.issueOutStockDOs.get(i);
                    rst = issueService.insertStockRemainData(this.taskId, issueOutStockDO);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("rst:{},err:{},issueOutStockDO:{}", rst, e.getMessage(), issueOutStockDO.toString());
                }
            }
        } else {
            //如果长度大于阈值，则分割为小任务
            int mid = (start + end) / 2;
            IssueOutStockRemainTask left = new IssueOutStockRemainTask(this.taskId, this.start, mid, this.issueOutStockDOs, this.issueService);
            IssueOutStockRemainTask right = new IssueOutStockRemainTask(this.taskId, mid, this.end, this.issueOutStockDOs, this.issueService);
            invokeAll(left, right);
            rst = left.join() + right.join();
        }
        return rst;
    }
}
