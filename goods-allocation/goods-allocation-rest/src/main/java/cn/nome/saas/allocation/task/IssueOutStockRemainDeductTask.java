package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.model.old.issue.IssueDetailDistStock;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author chentaikuang
 */
public class IssueOutStockRemainDeductTask extends RecursiveTask<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IssueOutStockRemainDeductTask.class);

    private IssueService issueService;

    private List<IssueDetailDistStock> detailDistStocks;
    private int taskId;
    private String shopId;
    private int start;
    private int end;

    public IssueOutStockRemainDeductTask(int taskId, String shopId, int start, int end, List<IssueDetailDistStock> detailDistStocks, IssueService issueService) {
        this.taskId = taskId;
        this.shopId = shopId;
        this.start = start;
        this.end = end;
        this.detailDistStocks = detailDistStocks;
        this.issueService = issueService;
    }

    @Override
    protected Integer compute() {
        int rst = 0;
        boolean compute = (this.end - this.start) <= 100;
        if (compute) {
            IssueDetailDistStock detailDistStock = null;
            for (int i = start; i < end; i++) {
                try {
                    detailDistStock = this.detailDistStocks.get(i);
                    rst = issueService.deductStockRemainData(this.taskId, this.shopId, detailDistStock);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("rst:{},err:{},deductStockRemainData:{}", rst, e.getMessage(), detailDistStocks.toString());
                }
            }
        } else {
            //如果长度大于阈值，则分割为小任务
            int mid = (start + end) / 2;
            IssueOutStockRemainDeductTask left = new IssueOutStockRemainDeductTask(this.taskId, this.shopId, this.start, mid, this.detailDistStocks, this.issueService);
            IssueOutStockRemainDeductTask right = new IssueOutStockRemainDeductTask(this.taskId, this.shopId, mid, this.end, this.detailDistStocks, this.issueService);
            invokeAll(left, right);
            rst = left.join() + right.join();
        }
        return rst;
    }
}
