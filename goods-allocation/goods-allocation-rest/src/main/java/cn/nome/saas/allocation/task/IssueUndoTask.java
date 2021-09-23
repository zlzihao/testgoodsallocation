package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RecursiveTask;

public class IssueUndoTask extends RecursiveTask<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IssueUndoTask.class);

    private int taskId;
    private int start;
    private int end;

    private IssueService issueService;
    private List<String> shopIds;

    public IssueUndoTask(List<String> shopIds, int taskId, int start, int end, IssueService issueService) {
        this.shopIds = shopIds;
        this.start = start;
        this.end = end;
        this.issueService = issueService;
        this.taskId = taskId;
    }


    @Override
    protected Integer compute() {
        int rst = 0;
        boolean compute = (this.end - this.start) <= 5;
        if (compute) {
            for (int i = start; i < end; i++) {
                String shopId = shopIds.get(i);
                rst = issueService.batchInsertUndoData(this.taskId, shopId);
                logger.debug("shopId:{},rst:{}", shopId, rst);
            }
        } else {
            //如果长度大于阈值，则分割为小任务
            int mid = (start + end) / 2;
            IssueUndoTask left = new IssueUndoTask(this.shopIds, this.taskId, this.start, mid, this.issueService);
            IssueUndoTask right = new IssueUndoTask(this.shopIds, this.taskId, mid, this.end, this.issueService);
            invokeAll(left, right);
            rst = left.join() + right.join();
        }
        return rst;
    }
}
