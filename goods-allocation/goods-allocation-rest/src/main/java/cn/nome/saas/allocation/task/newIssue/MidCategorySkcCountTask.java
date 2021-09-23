package cn.nome.saas.allocation.task.newIssue;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.old.issue.NewCategorySkcData;
import cn.nome.saas.allocation.model.old.issue.NewIssueCategorySkcData;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.utils.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * MidCategorySkcCountTask
 *
 * @author Bruce01.fan
 * @date 2019/9/28
 */
public class MidCategorySkcCountTask extends RecursiveTask<Integer> {

    private Logger logger = LoggerFactory.getLogger(MidCategorySkcCountTask.class);

    NewIssueDOMapper newIssueDOMapper;

    int start;

    int end;

    Set<String> shopIdList;

    Map<String, List<String>> shopCategoryMap;

    ExecutorService insertService;

    int taskId;

    Date runTime;

    public MidCategorySkcCountTask(int taskId, Set<String> shopId, int start, int end, NewIssueDOMapper newIssueDOMapper, ExecutorService insertService, Map<String, List<String>> shopCategoryMap, Date runTime) {
        this.taskId = taskId;
        this.start = start;
        this.end = end;
        this.shopIdList = shopId;
        this.newIssueDOMapper = newIssueDOMapper;
        this.insertService = insertService;
        this.shopCategoryMap = shopCategoryMap;
        this.runTime = runTime;
    }

    @Override
    protected Integer compute() {
        LoggerUtil.info(logger,"[MID_CATEGORY_SKU_COUNT] shopSize:{0}",this.shopIdList.size());

        boolean compute = (this.end - this.start) <= 100;
        Integer succCount = 0;
        if (compute) {
            List<String> shopsList = shopIdList.stream().skip(start).limit(end-start).collect(Collectors.toList());
            succCount = calcIssueMidCategorySkcData(shopsList);
        } else {
            // fork
            int mid = (start + end) / 2;
            MidCategorySkcCountTask left = new MidCategorySkcCountTask(taskId,shopIdList,start,mid,newIssueDOMapper,insertService,this.shopCategoryMap,runTime);
            MidCategorySkcCountTask right = new MidCategorySkcCountTask(taskId,shopIdList,mid,end,newIssueDOMapper,insertService,this.shopCategoryMap,runTime);

            invokeAll(left, right);
            succCount += left.join() + right.join();
        }

        return succCount;
    }

    public Integer calcIssueMidCategorySkcData(List<String> shopIdList) {

        String inStockTableName = CommonUtil.getTaskTableName(Constant.ISSUE_IN_STOCK_TABLE_PREFIX, taskId,runTime);
        String outStockTableName = CommonUtil.getTaskTableName(Constant.ISSUE_OUT_STOCK_TABLE_PREFIX, taskId,runTime);

        Long start = System.currentTimeMillis();

        List<NewCategorySkcData> canSkuList = newIssueDOMapper.calcMidCategoryCanSkcCount(inStockTableName, outStockTableName, shopIdList);
        List<NewCategorySkcData> keepSkuList = newIssueDOMapper.calcMidCategoryKeepSkcCount(inStockTableName, shopIdList);
        List<NewCategorySkcData> newSkuList = newIssueDOMapper.calcMidCategoryNewSkcCount(inStockTableName, shopIdList);
        List<NewCategorySkcData> prohibitSkuList = newIssueDOMapper.calcMidCategoryProhibitedSkcCount(inStockTableName, shopIdList);
        List<NewCategorySkcData> validSkuList = newIssueDOMapper.calcMidCategoryValidSkcCount(inStockTableName, shopIdList);

        // 按门店分组
        Map<String, Long> canSkcMap = canSkuList.stream().collect(Collectors.toMap(NewCategorySkcData::getMidKey, NewCategorySkcData::getCount));
        Map<String, Long> keepSkcMap = keepSkuList.stream().collect(Collectors.toMap(NewCategorySkcData::getMidKey, NewCategorySkcData::getCount));
        Map<String, Long> newSkcMap = newSkuList.stream().collect(Collectors.toMap(NewCategorySkcData::getMidKey, NewCategorySkcData::getCount));
        Map<String, Long> prohibitSkcMap = prohibitSkuList.stream().collect(Collectors.toMap(NewCategorySkcData::getMidKey, NewCategorySkcData::getCount));
        Map<String, Long> vaildSkcMap = validSkuList.stream().collect(Collectors.toMap(NewCategorySkcData::getMidKey, NewCategorySkcData::getCount));

        List<NewIssueCategorySkcData> list = new ArrayList<>();
        for (String shopId : shopIdList) {

            if (!shopCategoryMap.containsKey(shopId)) {
                continue;
            }

            List<String> categoryList = shopCategoryMap.get(shopId);

            List<NewIssueCategorySkcData> subList = categoryList.parallelStream().map(mixCategoryName -> {

                String key = shopId + ":" + mixCategoryName;
                NewIssueCategorySkcData newIssueCategorySkcData = new NewIssueCategorySkcData();
                newIssueCategorySkcData.setShopID(shopId);
                newIssueCategorySkcData.setCategoryName(mixCategoryName.split(":")[0]);
                newIssueCategorySkcData.setMidCategoryName(mixCategoryName.split(":")[1]);
                newIssueCategorySkcData.setCanSkcCount(canSkcMap.containsKey(key) ? canSkcMap.get(key) : 0L);
                newIssueCategorySkcData.setKeepSkcCount(keepSkcMap.containsKey(key) ? keepSkcMap.get(key) : 0L);
                newIssueCategorySkcData.setNewSkcCount(newSkcMap.containsKey(key) ? newSkcMap.get(key) : 0L);
                newIssueCategorySkcData.setProhibitedSkcCount(prohibitSkcMap.containsKey(key) ? prohibitSkcMap.get(key) : 0L);
                newIssueCategorySkcData.setValidSkcCount(vaildSkcMap.containsKey(key) ? vaildSkcMap.get(key) : 0L);

                return newIssueCategorySkcData;
            }).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(subList)) {
                list.addAll(subList);
            }
        }

        Long end = System.currentTimeMillis();

        LoggerUtil.info(logger,"[MID_CATEGORY_SKU_COUNT] time:{0}",(end - start) / 1000);

        // 异步插入db
        String tableName = CommonUtil.getTaskTableName(Constant.ISSUE_MIDCATAGORY_DATA_TABLE_PREFIX, taskId,runTime);
        insertService.execute(()->{
            newIssueDOMapper.insertMidCategoryCountData(tableName,list);
        });

        return list.size();
    }
}
