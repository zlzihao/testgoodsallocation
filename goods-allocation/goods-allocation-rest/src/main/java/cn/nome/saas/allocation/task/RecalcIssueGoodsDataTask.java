package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author chentaikuang
 */
public class RecalcIssueGoodsDataTask extends RecursiveTask<Integer> {

    private static Logger logger = LoggerFactory.getLogger(RecalcIssueGoodsDataTask.class);

    private IssueService issueService;

    private List<String> shopIds;
    private int taskId;
    private int start;
    private int end;

    public RecalcIssueGoodsDataTask(List<String> shopIds, int taskId, int start, int end, IssueService issueService) {
        this.shopIds = shopIds;
        this.taskId = taskId;
        this.start = start;
        this.end = end;
        this.issueService = issueService;
    }

    @Override
    protected Integer compute() {
        int rst = 0;
        boolean compute = (this.end - this.start) <= 100;
        if (compute) {
            for (int i = start; i < end; i++) {
                String shopId = this.shopIds.get(i);
                rst = issueService.batchInsertRecalcGoodsData(this.taskId, shopId);
                logger.debug("shopId:{},rst:{}", shopId, rst);
            }
        } else {
            //如果长度大于阈值，则分割为小任务
            int mid = (start + end) / 2;
            RecalcIssueGoodsDataTask left = new RecalcIssueGoodsDataTask(this.shopIds, this.taskId, this.start, mid, this.issueService);
            RecalcIssueGoodsDataTask right = new RecalcIssueGoodsDataTask(this.shopIds, this.taskId, mid, this.end, this.issueService);
            invokeAll(left, right);
            rst = left.join() + right.join();
        }
        return rst;
    }
}
