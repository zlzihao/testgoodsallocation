package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.service.allocation.IssueRecalcService;

import java.util.concurrent.RecursiveTask;

public class SetIssueStatusTask extends RecursiveTask<Integer> {

    private IssueRecalcService issueRecalcService;
    private int taskId;
    private String shopId;
    private int setStatus;
    public SetIssueStatusTask(IssueRecalcService issueRecalcService, int taskId, String shopId, int setStatus) {
        this.issueRecalcService = issueRecalcService;
        this.taskId = taskId;
        this.shopId = shopId;
        this.setStatus = setStatus;
    }

    @Override
    protected Integer compute() {
        if (setStatus == Constant.VALID_STS){
            issueRecalcService.setValidSts(taskId,shopId);
        }else {
            issueRecalcService.setInvalidSts(taskId,shopId);
        }
        return 1;
    }
}
