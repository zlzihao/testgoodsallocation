package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.model.old.issue.IssueDetailDistStock;
import cn.nome.saas.allocation.model.old.issue.IssueOutStockRemainDo;
import cn.nome.saas.allocation.repository.old.allocation.entity.IssueOutStockDO;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author chentaikuang
 */
public class IssueDetailStockFreedTask extends RecursiveTask<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IssueDetailStockFreedTask.class);

    private IssueService issueService;

    private List<IssueOutStockRemainDo> remainDos;
    private int taskId;
    private String shopId;
    private int start;
    private int end;

    public IssueDetailStockFreedTask(int taskId, String shopId, int start, int end, List<IssueOutStockRemainDo> remainDos, IssueService issueService) {
        this.taskId = taskId;
        this.shopId = shopId;
        this.start = start;
        this.end = end;
        this.remainDos = remainDos;
        this.issueService = issueService;
    }

    @Override
    protected Integer compute() {
        int rst = 0;
        boolean compute = (this.end - this.start) <= 100;
        if (compute) {
            IssueOutStockRemainDo remainDo = null;
            for (int i = start; i < end; i++) {
                try {
                    remainDo = this.remainDos.get(i);
                    rst = issueService.insertStockRemainFreedData(this.taskId, this.shopId, remainDo);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("rst:{},err:{},StockRemainFreed:{}", rst, e.getMessage(), remainDos.toString());
                }
            }
        } else {
            //如果长度大于阈值，则分割为小任务
            int mid = (start + end) / 2;
            IssueDetailStockFreedTask left = new IssueDetailStockFreedTask(this.taskId, this.shopId, this.start, mid, this.remainDos, this.issueService);
            IssueDetailStockFreedTask right = new IssueDetailStockFreedTask(this.taskId, this.shopId, mid, this.end, this.remainDos, this.issueService);
            invokeAll(left, right);
            rst = left.join() + right.join();
        }
        return rst;
    }
}
