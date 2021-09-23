package cn.nome.saas.allocation.task;

import cn.nome.saas.allocation.service.old.allocation.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author chentaikuang
 */
public class CategorySkcDataTask extends RecursiveTask<Integer> {

    private static Logger logger = LoggerFactory.getLogger(CategorySkcDataTask.class);

    private IssueService issueService;

    private List<String> shopInfoDos;
    private int taskId;
    private int start;
    private int end;

    public CategorySkcDataTask(List<String> shopInfoDos, int taskId, int start, int end, IssueService issueService) {
        this.shopInfoDos = shopInfoDos;
        this.taskId = taskId;
        this.start = start;
        this.end = end;
        this.issueService = issueService;
    }

    @Override
    protected Integer compute() {
        int rst = 0;
        boolean compute = (this.end - this.start) <= 5;
        if (compute) {
            String shopId = null;
            for (int i = start; i < end; i++) {
                shopId = this.shopInfoDos.get(i);
                try {
                    rst = issueService.insertCategorySkcData(this.taskId, shopId);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("shopId:{},rst:{},err:{}", shopId, rst, e.getMessage());
                }
                logger.debug("shopId:{},rst:{}", shopId, rst);
            }
        } else {
            //如果长度大于阈值，则分割为小任务
            int mid = (start + end) / 2;
            CategorySkcDataTask left = new CategorySkcDataTask(this.shopInfoDos, this.taskId, this.start, mid, this.issueService);
            CategorySkcDataTask right = new CategorySkcDataTask(this.shopInfoDos, this.taskId, mid, this.end, this.issueService);
            invokeAll(left, right);
            rst = left.join() + right.join();
        }
        return rst;
    }
}
