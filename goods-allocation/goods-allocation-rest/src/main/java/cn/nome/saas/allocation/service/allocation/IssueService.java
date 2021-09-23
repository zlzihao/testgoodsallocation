package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.model.old.allocation.ShopInfoDo;
import cn.nome.saas.allocation.model.old.allocation.Stock;
import cn.nome.saas.allocation.model.old.issue.IssueOutStockRemainDo;
import cn.nome.saas.allocation.repository.dao.allocation.IssueDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.IssueUndoDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ShopInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.IssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueNeedStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.IssueUndoDO;
import cn.nome.saas.allocation.repository.entity.allocation.MidCategoryStockDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueInStockDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueOutStockDO;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.service.old.allocation.ProhibitedService;
import cn.nome.saas.allocation.service.rule.ForbiddenRuleService;
import cn.nome.saas.allocation.utils.old.DateProcessUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.TAB_GOODS_INFO_VIEW;
import static cn.nome.saas.allocation.constant.old.Constant.TAB_GOODS_INFO;

/**
 * IssueService
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
@Service
public class IssueService {

    private static Logger logger = LoggerFactory.getLogger(IssueService.class);

    ExecutorService issueThreadPool = Executors.newFixedThreadPool(4);

    ExecutorService insertThreadPool = Executors.newFixedThreadPool(2);

    @Autowired
    IssueTaskService issueTaskService;

    @Autowired
    ForbiddenRuleService forbiddenRuleService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    IssueExtraDataMapper issueExtraDataMapper;

    @Autowired
    IssueDOMapper issueDOMapper;

    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;

    @Autowired
    IssueUndoDOMapper issueUndoDOMapper;

    @Autowired
    ProhibitedService prohibitedService;

    @Autowired
    AllocationDOMapper2 allocationDOMapper2;

    @Autowired
    IssueDOMapper2 issueDOMapper2;

    @Autowired
    cn.nome.saas.allocation.service.old.allocation.IssueService issueService2;

    public void runIssueTask(IssueTask task) {

        long start = System.currentTimeMillis();

        int taskId = issueTaskService.addTask(task);
        //int taskId = 3;
        task.setId(taskId);

        List<Future> futureList = new ArrayList<>();

        // 创建新表
        String issueInStockTable = this.getTaskTableName(taskId,"issue_in_stock");

        issueDOMapper.createNewIssueInStock(issueInStockTable);

        // 供给池

        OutStockTask outStockTask = new OutStockTask(task);
        Future<String> outStockFuture = issueThreadPool.submit(outStockTask);
        futureList.add(outStockFuture);


        // 需求池
        // 老品
        InStockTask clothingInStockTask = new InStockTask(task,7,1,issueInStockTable); // 服装
        InStockTask marketInStockTask = new InStockTask(task,7,2,issueInStockTable); // 百货

        Future<String> clothingFuture = issueThreadPool.submit(clothingInStockTask);
        Future<String> marketFuture = issueThreadPool.submit(marketInStockTask);
        futureList.add(clothingFuture);
        futureList.add(marketFuture);

        this.waitForTaskDone(futureList);

        // 新品（新品需要依赖老品计算的数据，所以需要等线程池执行完）
        this.newSkcInStockProcess(task,issueInStockTable);



        List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.shopInfoData();

        // 中类数量生成
        MidCategoryQtyTask midCategoryQtyTask = new MidCategoryQtyTask(task.getId(),shopInfoDataList,issueInStockTable);
        Future<String> midCategoryQtyFuture = issueThreadPool.submit(midCategoryQtyTask);
        futureList.add(midCategoryQtyFuture);


        // 生成商品需求量
        NewSkuStockTask newSkuStockTask = new NewSkuStockTask(task.getId(),shopInfoDataList,issueInStockTable);
        Future<String> newSkuStockFuture = issueThreadPool.submit(newSkuStockTask);
        futureList.add(newSkuStockFuture);

        this.waitForTaskDone(futureList);


        // 总仓库存足够供货的商品处理
        // 总仓库存不足供货商品处理：按比例分配
        this.processSkuStock(task);

        this.issueUndoProcess(task,shopInfoDataList,issueInStockTable);

        long end = System.currentTimeMillis();

        LoggerUtil.info(logger,"[ISSUE_TASK] msg = total time:{0}",(end - start) / 1000 / 60 );
    }

    /**
     * 需求池-老品
     */
    class InStockTask implements Callable<String> {

        private IssueTask task;
        private int period;
        private int type;
        private String issueInStockTable;

        public InStockTask(IssueTask task,int period,int type,String issueInStockTable) {
            this.task = task;
            this.period = period;
            this.type = type;
            this.issueInStockTable = issueInStockTable;
        }

        @Override
        public String call() {

            Long start = System.currentTimeMillis();

            List<IssueInStockDO> inStockList = issueExtraDataMapper.getIssueInStockList(type,period);

            try {
                if (CollectionUtils.isNotEmpty(inStockList)) {

                    Set<String> matCodeList = inStockList.stream().map(IssueInStockDO::getMatCode).collect(Collectors.toSet());
                    // 查询禁配清单
                    List<Map<String, String>> forbiddenSingleItemDOList = forbiddenRuleService.getForbiddenDetailList(matCodeList.stream().collect(Collectors.toSet()));

                    int size = 5000;
                    int offset = 0;

                    // 批量插入
                    while (true) {
                        List<IssueInStockDO> subInStockList = inStockList.parallelStream().skip(offset * size).limit(size).collect(Collectors.toList());

                        if (CollectionUtils.isEmpty(subInStockList)) {
                            break;
                        }

                        List<IssueInStock> list = subInStockList.stream().map(stock -> {

                            IssueInStock issueInStock = new IssueInStock();

                            BeanUtils.copyProperties(stock, issueInStock);

                            issueInStock.setTaskId(task.getId());
                            if (stock.getStockQty() == null || stock.getStockQty() < 0) {
                                issueInStock.setStockQty(0L);
                            }

                            Long totalStockQty = stock.getStockQty();
                            if (stock.getPathStockQty() != null) {
                                totalStockQty = totalStockQty + stock.getPathStockQty();
                            }
                            if (stock.getMoveQty() != null) {
                                totalStockQty = totalStockQty + stock.getMoveQty();
                            }
                            issueInStock.setTotalStockQty(totalStockQty);

                            // 是否为禁品判断
                            if (forbiddenRuleService.checkIfIsForbiddenProduct(forbiddenSingleItemDOList, stock.getShopID(), stock.getMatCode())) {
                                issueInStock.setIsProhibited(1);
                            }

                            return issueInStock;

                        }).collect(Collectors.toList());

                        issueDOMapper.addIssueInStock(list,issueInStockTable);

                        offset++;
                    }
                }
            }catch (Exception e) {
                LoggerUtil.error(e,logger,"ISSUE_IN_STOCK");
            }

            Long end = System.currentTimeMillis();

            LoggerUtil.info(logger,"[ISSUE_TASK] msg = in stock.type:{0} time:{1}",type,(end - start) /  1000);

            return "SUCCESS";
        }
    }

    /**
     * 新品
     * @param issueTask
     */
    private void newSkcInStockProcess(IssueTask issueTask,String issueInStockTable) {

        Long start = System.currentTimeMillis();

        List<IssueInStockDO> newSkuInStockList = issueExtraDataMapper.getNewSkcList(); // 新品
        List<MidCategoryStockDO> midCategoryStockList = issueDOMapper.getMidCategorySale(issueTask.getId(),issueInStockTable); // 中类日均销售
        Map<String,GoodsInfoDO> goodsMap = goodsService.getGoodsInfo(); //
        Map<String, Double> shopMap = this.getShopAvgMap(issueTask.getId(),issueInStockTable); // 门店日均销售


        if (CollectionUtils.isNotEmpty(newSkuInStockList)) {
            if (CollectionUtils.isNotEmpty(midCategoryStockList)) {
                Set<String> matCodeList = newSkuInStockList.stream().map(IssueInStockDO::getMatCode).collect(Collectors.toSet());
                // 查询禁配清单
                List<Map<String, String>> forbiddenSingleItemDOList = forbiddenRuleService.getForbiddenDetailList(matCodeList.stream().collect(Collectors.toSet()));

                int size = 5000;
                int offset = 0;

                // 批量
                while (true) {
                    List<IssueInStockDO> subNewSkuInStockList = newSkuInStockList.parallelStream().skip(offset * size).limit(size).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(subNewSkuInStockList)) {
                        break;
                    }

                    List<IssueInStock> list = subNewSkuInStockList.stream().map(newSkcStock -> {

                        IssueInStock issueInStock = new IssueInStock();

                        BeanUtils.copyProperties(newSkcStock,issueInStock);
                        issueInStock.setIsNew(1);

                        GoodsInfoDO goodsInfoDO = goodsMap.get(newSkcStock.getMatCode());

                        if (goodsInfoDO != null) {
                            issueInStock.setCategoryName(goodsInfoDO.getCategoryName());
                            issueInStock.setMidCategoryCode(goodsInfoDO.getMidCategoryName());
                        }

                        MidCategoryStockDO midCategoryStock = midCategoryStockList.stream().filter(midCategoryStockDO -> {
                            return midCategoryStockDO.getShopID().equals(issueInStock.getShopID())
                                    && midCategoryStockDO.getCategoryName().equals(issueInStock.getCategoryName())
                                    && midCategoryStockDO.getMidCategoryName().equals(issueInStock.getMidCategoryName());
                        }).findFirst().orElse(null);

                        if (midCategoryStock != null) {
                            issueInStock.setAvgSaleQty(midCategoryStock.getAvgSaleQty());
                        } else {
                            Double shopAvgSale = shopMap.get(issueInStock.getShopID());
                            if (shopAvgSale != null) {
                                issueInStock.setAvgSaleQty(shopAvgSale);
                            } else {
                                issueInStock.setAvgSaleQty(0.5D);
                            }
                        }

                        // 是否为禁品判断
                        if (forbiddenRuleService.checkIfIsForbiddenProduct(forbiddenSingleItemDOList,newSkcStock.getShopID(),newSkcStock.getMatCode()) ) {
                            issueInStock.setIsProhibited(1);
                        }

                        return issueInStock;

                    }).collect(Collectors.toList());

                    // 插入
                    issueDOMapper.addIssueInStock(list,issueInStockTable);

                    offset++;
                }

            }
        }

        Long end = System.currentTimeMillis();

        LoggerUtil.info(logger,"[ISSUE_TASK] msg = new skc in stock. time:{0}",(end - start) /  1000);
    }

    /**
     * 供给池任务
     */
    class OutStockTask implements Callable<String> {

        private IssueTask task;

        public  OutStockTask(IssueTask issueTask) {
            this.task = issueTask;
        }

        @Override
        public String call() throws Exception {

            Long start = System.currentTimeMillis();

            List<IssueOutStockDO> outStockDOList = issueExtraDataMapper.getIssueOutStockList();

            if (CollectionUtils.isNotEmpty(outStockDOList)) {

                int size = 2000;
                int offset = 0;

                while (true) {
                    List<IssueOutStockDO> subList = outStockDOList.stream().skip(offset * size).limit(size).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(subList)) {
                        break;
                    }

                    List<IssueOutStock> list = subList.stream().map(issueOutStockDO -> {
                        IssueOutStock issueOutStock = new IssueOutStock();

                        BeanUtils.copyProperties(issueOutStockDO,issueOutStock);
                        issueOutStock.setTaskId(task.getId());

                        return issueOutStock;
                    }).collect(Collectors.toList());

                    issueDOMapper.addIssueOutStock(list);

                    offset++;
                }
            }

            Long end = System.currentTimeMillis();

            LoggerUtil.info(logger,"[ISSUE_TASK] msg = out stock. time:{0}",(end - start) /  1000);

            return "SUCCESS";
        }
    }


    /**
     * 中类数量计算
     */
    class MidCategoryQtyTask implements Callable<String> {

        int taskId;

        List<ShopInfoData> shopInfoDataList;

        String inStockTableName;

        MidCategoryQtyTask(int taskId,List<ShopInfoData> shopInfoDataList,String inStockTableName) {
            this.taskId = taskId;
            this.shopInfoDataList = shopInfoDataList;
            this.inStockTableName = inStockTableName;
        }

        @Override
        public String call() throws Exception {

            Long start = System.currentTimeMillis();

            List<String> childShopList = shopInfoDataList.stream()
                    .filter(shopInfoData -> shopInfoData.getHaveChild() == 0)
                    .map(ShopInfoData::getShopID).collect(Collectors.toList());

            List<String> noChildShopList = shopInfoDataList.stream()
                    .filter(shopInfoData -> shopInfoData.getHaveChild() == 1)
                    .map(ShopInfoData::getShopID).collect(Collectors.toList());

            List<MidCategoryStockDO> midCategoryStockDOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(childShopList)) {
                List<MidCategoryStockDO> list = issueDOMapper.getMidMidCategoryQty(taskId, childShopList, Constant.TAB_GOODS_INFO,inStockTableName);

                if (CollectionUtils.isNotEmpty(list)) {
                    midCategoryStockDOList.addAll(list);
                }
            }

            if (CollectionUtils.isNotEmpty(noChildShopList)) {
                List<MidCategoryStockDO> list = issueDOMapper.getMidMidCategoryQty(taskId, noChildShopList, TAB_GOODS_INFO_VIEW,inStockTableName);

                if (CollectionUtils.isNotEmpty(list)) {
                    midCategoryStockDOList.addAll(list);
                }
            }


            issueDOMapper.midMidCategoryQty(midCategoryStockDOList);

            Long end = System.currentTimeMillis();

            LoggerUtil.info(logger,"[ISSUE_TASK] msg = calc mid category. time:{0}",(end - start) /  1000);

            return "SUCCESS";
        }
    }



    /**
     * 商品需求量
     * @return
     */
    class NewSkuStockTask implements Callable<String> {

        int taskId;

        List<ShopInfoData> shopInfoDataList;

        String inStockTableName;

        NewSkuStockTask(int taskId,List<ShopInfoData> shopInfoDataList,String inStockTableName) {
            this.taskId = taskId;
            this.shopInfoDataList = shopInfoDataList;
            this.inStockTableName = inStockTableName;
        }

        @Override
        public String call() throws Exception {

            Long start = System.currentTimeMillis();
            List<Future> futureList = new ArrayList<>();

            shopInfoDataList.stream().forEach(shopInfoDo -> {

                List<IssueNeedStockDO>  list = null;
                if (shopInfoDo.getHaveChild() == 0) {
                    list = issueDOMapper.getNeedSkuStock(taskId, shopInfoDo.getShopID(), Constant.TAB_GOODS_INFO,inStockTableName);

                }
                else if (shopInfoDo.getHaveChild() == 1) {
                    list = issueDOMapper.getNeedSkuStock(taskId, shopInfoDo.getShopID(), TAB_GOODS_INFO_VIEW,inStockTableName);
                }

                if (CollectionUtils.isNotEmpty(list)) {
                    List<IssueNeedStockDO>  newList = list;

                    Future<String> future = insertThreadPool.submit(() -> {
                        issueDOMapper.addNeedSkuStock(newList);
                        return "OK";
                    });
                    futureList.add(future);

                }

            });

            for (Future future : futureList) {
                future.get();
            }

            Long end = System.currentTimeMillis();

            LoggerUtil.info(logger,"[ISSUE_TASK] msg = calc new sku sotck. time:{0}",(end - start) /  1000);

            return "SUCCESS";
        }
    }

    /**
     *
     * @param task
     */
    public void processSkuStock(IssueTask task) {

        Long start = System.currentTimeMillis();

        List<IssueOutStock> list = issueDOMapper.getStockSku(task.getId());

        if (CollectionUtils.isNotEmpty(list)) {

            List<IssueOutStock> enoughStockList = list.stream().filter(issueOutStock -> issueOutStock.getRemainQty() >=0).collect(Collectors.toList());

            if (enoughStockList != null) {
                LoggerUtil.info(logger, "[ENOUGH_STOCK] msg = size：{0}", enoughStockList.size());
                for (IssueOutStock stock : enoughStockList) {
                    issueDOMapper.addEnoughStockSku(task.getId(), stock.getMatCode(), stock.getSizeID());
                }
            }


            List<IssueOutStock> notEnoughStockList = list.stream().filter(issueOutStock -> issueOutStock.getRemainQty() <0).collect(Collectors.toList());
            if (notEnoughStockList != null) {
                LoggerUtil.info(logger, "[NOT_ENOUGH_STOCK] msg = size：{0}", notEnoughStockList.size());
                for (IssueOutStock stock : notEnoughStockList) {
                    issueDOMapper.addNotEnoughStockSku(task.getId(), stock.getMatCode(), stock.getSizeID(),
                            stock.getStockQty());
                }
            }

        }

        Long end = System.currentTimeMillis();

        LoggerUtil.info(logger,"[SKU_STOCK] msg = calc sku sotck. time:{0}",(end - start) /  1000);
    }

    private void issueUndoProcess(IssueTask task, List<ShopInfoData> shopInfoDos,String inStockTableName) {
        int taskId = task.getId();
        for (ShopInfoData shopInfoDo : shopInfoDos) {
            String shopId = shopInfoDo.getShopID();
            List<IssueUndoData> issueUndoData = issueDOMapper.getIssueUndoData(taskId, shopId,inStockTableName);
            if (issueUndoData == null || issueUndoData.isEmpty()) {
                continue;
            }

            List issueUndos = issueUndoData.stream().map(data -> {
                IssueUndoDO issueUndoDO = new IssueUndoDO();
                BeanUtils.copyProperties(data,issueUndoDO);
                return issueUndoDO;
            }).collect(Collectors.toList());

            int rst = issueUndoDOMapper.batchInsertTab(issueUndos);

        }
    }

    private Map<String, Double> getShopAvgMap(Integer taskId,String issueInStockTable) {
        List<Map<String,Object>> list = issueDOMapper.getShopAvg(taskId,issueInStockTable);
        Map map = Maps.newHashMap();

        if (list != null) {
            for (Map<String,Object> result : list) {
                String shopId = (String)result.get("ShopId");
                Double avgSaleQty = ((BigDecimal) result.get("AvgSaleQty")).doubleValue();
                map.put(shopId,avgSaleQty);
            }
        }
        return map;
    }

    private void waitForTaskDone(List<Future> futureList) {
        for (Future future : futureList) {
            try {
                if (future.isDone() || future == null){
                    continue;
                }
                future.get();
            } catch (Exception e) {
                LoggerUtil.error(e,logger,"FUTURE_ERROR");
            }
        }

    }

    private String getTaskTableName(int taskId,String tableName) {
        Date date = DateUtil.getCurrentDate();
        String currentDate = DateUtil.format(DateUtil.addDate(date,-1,DateUtil.DAY),DateUtil.DATE_ONLY);
        currentDate = currentDate.replaceAll("-","").substring(2);

        return tableName+"_"+taskId+"_"+ currentDate;
    }

    private String getMidName(Stock stock) {
        return stock.getShopID() + "-" + stock.getCategoryName() + "-" + stock.getMidCategoryName();
    }

}
