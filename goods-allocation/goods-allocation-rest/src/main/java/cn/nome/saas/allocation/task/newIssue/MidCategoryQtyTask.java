package cn.nome.saas.allocation.task.newIssue;

import cn.nome.saas.allocation.model.issue.ShopInfoData;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * MidCategoryQtyTask
 *
 * @author Bruce01.fan
 * @date 2019/9/9
 */
public class MidCategoryQtyTask implements Callable<String> {

    int taskId;

    List<ShopInfoData> shopInfoDataList;

    List<String> shopIdList;

    MidCategoryQtyTask(int taskId,List<ShopInfoData> shopInfoDataList,List<String> shopIdList) {
        this.taskId = taskId;
        this.shopInfoDataList = shopInfoDataList;
        this.shopIdList = shopIdList;
    }

    @Override
    public String call() throws Exception {
        return null;
    }
}
