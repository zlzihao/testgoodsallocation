package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.old.allocation.ProhibitedGoods;
import cn.nome.saas.allocation.model.old.allocation.ShopInfoDo;
import cn.nome.saas.allocation.model.old.allocation.Stock;
import cn.nome.saas.allocation.model.old.issue.IssueDetailDistStock;
import cn.nome.saas.allocation.model.old.issue.IssueOutStockRemainDo;
import cn.nome.saas.allocation.model.old.issue.IssueTaskVo;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueRecalcMapper;
import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import cn.nome.saas.allocation.repository.old.vertica.dao.AllocationDOMapper2;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import cn.nome.saas.allocation.service.old.allocation.ProhibitedService;
import cn.nome.saas.allocation.task.*;
import cn.nome.saas.allocation.utils.old.DateProcessUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static cn.nome.saas.allocation.constant.Constant.TAB_GOODS_INFO_VIEW;
import static cn.nome.saas.allocation.constant.old.Constant.TAB_GOODS_INFO;

@Service
public class IssueRecalcService {

    private static Logger logger = LoggerFactory.getLogger(IssueRecalcService.class);

    Lock syncLock = new ReentrantLock();

    @Autowired
    ProhibitedService prohibitedService;

    @Autowired
    AllocationDOMapper2 allocationDOMapper2;

    @Autowired
    IssueDOMapper2 issueDOMapper2;

    @Autowired
    IssueService issueService2;

    @Autowired
    IssueRestService issueRestService;

    @Autowired
    IssueRecalcMapper issueRecalcMapper;

    @Autowired
    RecalcTaskService recalcTaskService;

    /**
     * 门店重算
     *
     * @param taskId
     * @param shopId
     * @return
     */
    public Result shopRecalc(int recalcId,int taskId, String shopId) {
        try {
            RecalcTaskDo taskDo = recalcTaskService.hasDoingTask(taskId);
            if (taskDo != null) {
                return ResultUtil.handleSysFailtureReturn("已有任务在执行");
            }
            Integer rst = recalcTaskService.updateRunSts(recalcId);
            if (rst == null || rst == 0){
                return ResultUtil.handleSysFailtureReturn("重算任务运行状态更新异常");
            }

            logger.debug("shopRecalc:{},taskId:{},recalcId:{}", shopId, taskId, recalcId);
            Date start = new Date();
            // 取到禁品数据
            Map<String, Map<String, ProhibitedGoods>> prohibitedGoods = getShopProhibitedGoods(taskId, shopId);
            Date date1 = new Date();
            //获取该门店配发旧品
            // this.recalcIssueInStock(taskId, shopId, Constant.CATEGORY_FZ, prohibitedGoods);
            this.recalcIssueInStock(taskId, shopId, Constant.CATEGORY_BH, prohibitedGoods);
            LoggerUtil.info(logger, "1.recalcIssueInStock生成：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(2,10));

            date1 = new Date();
            //获取新品
            this.recalcIssueInNewSkcStock(taskId, shopId, prohibitedGoods);
            LoggerUtil.info(logger, "2.recalcIssueInNewSkcStock：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(11,20));

            //释放原配发占有的总仓库存
            this.recalcFreedIssueOutStockRemain(taskId, shopId);
            LoggerUtil.info(logger, "3.recalcFreedIssueOutStockRemain：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(21,30));

            ShopInfoDo shopInfoDo = issueDOMapper2.getShop(shopId);
            //getHaveChild:0有,1无
            boolean hasChildShopFlag = shopInfoDo.getHaveChild() == 0;

            // 中类数量生成
            date1 = new Date();
            this.recalcIssueMidCategoryQty(taskId, shopId, hasChildShopFlag);
            LoggerUtil.info(logger, "4.recalcIssueMidCategoryQty：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(31,40));

            // 生成商品需求量
            date1 = new Date();
            this.recalcIssueNeedStock(taskId, shopId, hasChildShopFlag);
            LoggerUtil.info(logger, "5.recalcIssueNeedStock：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(41,50));

            // 总仓库存足够供货的商品处理
            date1 = new Date();
            this.recalcIssueEnoughStock(taskId, shopId);
            LoggerUtil.info(logger, "6.recalcIssueEnoughStock：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(51,60));

            date1 = new Date();
            // 总仓库存不足供货商品处理：按比例分配
            this.recalcIssueNotEnoughStock(taskId, shopId);
            LoggerUtil.info(logger, "7.recalcIssueNotEnoughStock：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(61,70));

            date1 = new Date();
            this.recalcIssueUndo(taskId, shopId);
            LoggerUtil.info(logger, "8.recalcIssueUndo：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(71,80));

            date1 = new Date();
            this.recalcIssueGoodsData(taskId, shopId);
            LoggerUtil.info(logger, "9.recalcIssueGoodsData：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(81,90));

            date1 = new Date();
            this.recalcCategorySkcCount(taskId, shopId);
            LoggerUtil.info(logger, "10.recalcCategorySkcCount：{0}分钟", DateProcessUtil.getMinute(date1));

            this.setRecalcPercent(recalcId,RandomUtils.nextInt(91,95));

            //扣除剩余库存
            this.recalcDeductIssueOutStockRemain(taskId,shopId);

            syncLock.lock();
            try {
                this.setInvalidSts(taskId,shopId);
                LoggerUtil.info(logger, "11.INVALIDSTS：{0}分钟", DateProcessUtil.getMinute(date1));
                this.setValidSts(taskId,shopId);
                LoggerUtil.info(logger, "11.VALIDSTS：{0}分钟", DateProcessUtil.getMinute(date1));
            }finally {
                syncLock.unlock();
            }

            this.setRecalcPercent(recalcId,100);

            LoggerUtil.info(logger, "12.RECALC DONE：{0}分钟", DateProcessUtil.getMinute(start));
            return ResultUtil.handleSuccessReturn(shopId);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "shopRecalc catch exception");
            return null;
        }

    }

    public void recalcDeductIssueOutStockRemain(int taskId, String shopId) {
        List<IssueDetailDistStock> recalcDetails = issueRestService.getRecalcDetailStock(taskId, shopId);
        IssueOutStockRemainDeductTask task = new IssueOutStockRemainDeductTask(taskId,shopId, 0, recalcDetails.size(), recalcDetails, issueService2);
        ForkJoinPool pool = new ForkJoinPool(4);
        Integer rst = pool.invoke(task);
        logger.info("PROCESS REMAIN STOCK DEDUCT DONE! taskId:{},rst:{}", taskId, rst);
        pool.shutdown();
    }

    private void setRecalcPercent(int recalcId, int percentNum) {
        recalcTaskService.setRecalcPercent(recalcId,percentNum);
    }

    public synchronized void setInvalidSts(int taskId, String shopId) {

        int rst = issueDOMapper2.invalidIssueUndo(taskId, shopId);
        logger.info("invalidIssueUndo rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.invalidIssueOutStockRemain(taskId);
        logger.info("invalidIssueOutStockRemain rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.invalidIssueNeedStock(taskId, shopId);
        logger.info("invalidIssueNeedStock rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.invalidIssueMidCategoryData(taskId, shopId);
        logger.info("invalidIssueMidCategoryData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.invalidIssueMidCategoryQty(taskId, shopId);
        logger.info("invalidIssueMidCategoryQty rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.invalidIssueInStock(taskId, shopId);
        logger.info("invalidIssueInStock rst:{},shopId:{},taskId:{}", rst, shopId, taskId);


        rst = issueDOMapper2.invalidIssueDetail(taskId, shopId);
        logger.info("invalidIssueDetail rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.invalidIssueCategoryData(taskId, shopId);
        logger.info("invalidIssueCategoryData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);


        rst = issueDOMapper2.invalidIssueGoodsData(taskId, shopId);
        logger.info("invalidIssueGoodsData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

    }

    public synchronized void setValidSts(int taskId, String shopId) {

        int rst = issueDOMapper2.validIssueUndo(taskId, shopId);
        logger.info("validIssueUndo rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueOutStockRemain(taskId);
        logger.info("validIssueOutStockRemain rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueNeedStock(taskId, shopId);
        logger.info("validIssueNeedStock rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueMidCategoryData(taskId, shopId);
        logger.info("validIssueMidCategoryData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueMidCategoryQty(taskId, shopId);
        logger.info("validIssueMidCategoryQty rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueInStock(taskId, shopId);
        logger.info("validIssueInStock rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueDetail(taskId, shopId);
        logger.info("validIssueDetail rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueCategoryData(taskId, shopId);
        logger.info("validIssueCategoryData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);

        rst = issueDOMapper2.validIssueGoodsData(taskId, shopId);
        logger.info("validIssueGoodsData rst:{},shopId:{},taskId:{}", rst, shopId, taskId);
    }

    /**
     * 重算配发需求
     * @param taskId
     * @param shopId
     * @param hasChildShopFlag
     */
    public void recalcIssueNeedStock(int taskId, String shopId, boolean hasChildShopFlag) {
        if (hasChildShopFlag) {
            issueDOMapper2.recalcNeedSkuStock(taskId, shopId, TAB_GOODS_INFO);
        } else {
            issueDOMapper2.recalcNeedSkuStock(taskId, shopId, TAB_GOODS_INFO_VIEW);
        }
    }

    /**
     * 重算中类需求
     * @param taskId
     * @param shopId
     * @param hasChildShopFlag
     */
    public void recalcIssueMidCategoryQty(int taskId, String shopId, boolean hasChildShopFlag) {
        if (hasChildShopFlag) {
            issueDOMapper2.recalcMidCategoryQty(taskId, shopId, TAB_GOODS_INFO);
        } else {
            //hasChild非0则归纳为替换的门店
            issueDOMapper2.recalcMidCategoryQty(taskId, shopId, TAB_GOODS_INFO_VIEW);
        }
    }

    public void recalcFreedIssueOutStockRemain(int taskId, String shopId) {

        List<IssueOutStockRemainDo> remainStocks = issueRestService.issueOutStockRemainStock(taskId);

        IssueDetailStockFreedTask task = new IssueDetailStockFreedTask(taskId,shopId, 0, remainStocks.size(), remainStocks, issueService2);
        ForkJoinPool pool = new ForkJoinPool(4);
        Integer rst = pool.invoke(task);
        logger.info("PROCESS REMAIN STOCK FREED DONE! taskId:{},rst:{}", taskId, rst);
        pool.shutdown();

    }

    public void recalcCategorySkcCount(int taskId, String shopId) {
        List<String> shopIds = new ArrayList<>();
        shopIds.add(shopId);
        RecalcCategorySkcDataTask task = new RecalcCategorySkcDataTask(shopIds, taskId, 0, shopIds.size(), issueService2);
        ForkJoinPool pool = new ForkJoinPool(4);
        Integer rst = pool.invoke(task);
        logger.info("PROCESS RECALC CATEGORY SKC COUNT DONE! taskId:{},rst:{}", taskId, rst);
        pool.shutdown();
    }

    public void recalcIssueGoodsData(int taskId, String shopId) {
        List<String> shopIds = new ArrayList<>();
        shopIds.add(shopId);
        RecalcIssueGoodsDataTask task = new RecalcIssueGoodsDataTask(shopIds, taskId, 0, shopIds.size(), issueService2);
        ForkJoinPool pool = new ForkJoinPool(4);
        Integer rst = pool.invoke(task);
        logger.info("PROCESS RECALC ISSUE GOODS DATA DONE! taskId:{},rst:{}", taskId, rst);
        pool.shutdown();
    }

    public void recalcIssueUndo(int taskId, String shopId) {
        List<String> shopIds = new ArrayList<>();
        shopIds.add(shopId);
        RecalcIssueUndoTask undoTask = new RecalcIssueUndoTask(shopIds, taskId, 0, shopIds.size(), issueService2);
        ForkJoinPool pool = new ForkJoinPool(4);
        int rst = pool.invoke(undoTask);
        logger.info("PROCESS RECALC ISSUE UNDO DONE! taskId:{},rst:{}", taskId, rst);
        pool.shutdown();
    }

    public void recalcIssueNotEnoughStock(int taskId, String shopId) {
        List<IssueOutStockRemainDo> list = issueDOMapper2.getRecalcNotEnoughStockSku(taskId, shopId);
        if (list != null) {
            LoggerUtil.info(logger, "总仓不足够分配的SKU数量：{0}", list.size());
            for (IssueOutStockRemainDo stock : list) {
                LoggerUtil.debug(logger, "处理配发：{0}", stock);
                issueDOMapper2.addRecalcNotEnoughStockSku(taskId, shopId, stock.getMatCode(), stock.getSizeID(),
                        stock.getStockQty());
            }
        }
    }

    public void recalcIssueEnoughStock(int taskId, String shopId) {
        List<IssueOutStockRemainDo> list = issueDOMapper2.getRecalcEnoughStockSku(taskId, shopId);
        if (list != null) {
            LoggerUtil.info(logger, "总仓足够分配的SKU数量：{0}", list.size());
            for (IssueOutStockRemainDo stock : list) {
                LoggerUtil.debug(logger, "处理配发：{0}", stock);
                issueDOMapper2.addRecalcEnoughStockSku(taskId, shopId, stock.getMatCode(), stock.getSizeID());
            }
        }
    }

    private Map<String, Stock> getAvgByShopId(int taskId, String shopId) {
        List<Stock> list = issueDOMapper2.getShopAvg(taskId);
        Map map = Maps.newHashMap();
        if (list != null) {
            for (Stock temp : list) {
                if (shopId.equals(temp.getShopID())) {
                    map.put(temp.getShopID(), temp);
                    break;
                }
            }
        }
        return map;
    }

    private String getMidName(Stock stock) {
        return stock.getShopID() + "-" + stock.getCategoryName() + "-" + stock.getMidCategoryName();
    }

    public void recalcIssueInNewSkcStock(int taskId, String shopId, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods) {
        List<Stock> newSkcList = allocationDOMapper2.getRecalcShopNewSkcList(shopId);
        List<Stock> midCategorySaleAmtList = issueDOMapper2.getRecalcShopMidCategorySale(taskId, shopId);
        LoggerUtil.info(logger, "重算新品查询结果为：{0},{1}", newSkcList.size(), midCategorySaleAmtList.size());


        Map<String, Stock> goodsMap = issueService2.getGoodsInfo();
        Map<String, Stock> shopMap = this.getAvgByShopId(taskId, shopId);
        if (newSkcList != null) {
            if (midCategorySaleAmtList != null) {
                Map<String, Stock> midMap = Maps.newHashMap();
                for (Stock temp : midCategorySaleAmtList) {
                    midMap.put(getMidName(temp), temp);
                }

                for (Stock temp : newSkcList) {
                    Stock goods = goodsMap.get(temp.getMatCode());
                    if (goods != null) {
                        temp.setMidCategoryCode(goods.getMidCategoryCode());
                        temp.setMidCategoryName(goods.getMidCategoryName());
                        temp.setCategoryCode(goods.getCategoryCode());
                        temp.setCategoryName(goods.getCategoryName());
                    }
                    temp.setTaskId(taskId);
                    temp.setIsNew(1);

                    temp.setIsEliminate(temp.getMatTypeName() == null ? 0 : "淘汰".equals(temp.getMatTypeName()) ? 1 : 0);

                    String key = getMidName(temp);
                    Stock mid = midMap.get(key);
                    if (mid != null) {
                        temp.setAvgSaleQty(mid.getAvgSaleQty());
                    } else {
                        // 如果中类没有日均销数据，就按门店平均日均销来设置
                        Stock shop = shopMap.get(temp.getShopID());
                        if (shop != null) {
                            temp.setAvgSaleQty(shop.getAvgSaleQty());
                        } else {
                            temp.setAvgSaleQty(0.5d);
                        }
                    }
                    Map<String, ProhibitedGoods> matCodeMap;
                    ProhibitedGoods prohibitedGoodsDo;
                    if ((matCodeMap = prohibitedGoods.get(temp.getShopID())) != null && (prohibitedGoodsDo = matCodeMap.get(temp.getMatCode())) != null) {
                        temp.setRuleName(prohibitedGoodsDo.getRuleName());
                        //若有保底数量时, 保存保底策略数量
                        if (prohibitedGoodsDo.getMinQty() != null) {
                            temp.setMinQty(prohibitedGoodsDo.getMinQty());
                        } else {
                            //保留禁配数量0与禁配标志
                            temp.setMinQty(0);
                            temp.setIsProhibited(1);
                        }
                    }
                    temp.setStockQty(0L);
                    temp.setPathStockQty(0L);
                    temp.setMoveQty(temp.getMoveQty() == null ? 0L : temp.getMoveQty());

                    long totalStockQty = temp.getStockQty();
                    if (temp.getMoveQty() != null) {
                        totalStockQty += temp.getMoveQty();
                    }
                    temp.setTotalStockQty(totalStockQty);
                }

                // 分批录入
                List<Stock> tempList = Lists.newArrayList();
                int count = 1;
                for (int i = 0; i < newSkcList.size(); i++) {
                    Stock stock = newSkcList.get(i);
                    tempList.add(stock);

                    if (i == 5000 * count || i == newSkcList.size() - 1) {
                        LoggerUtil.info(logger, "取数结果为： recalcIssueInNewSkcStock size{0}", tempList.size());
                        if (tempList.size() > 0) {
                            issueDOMapper2.addRecalcIssueInStock(tempList);
                        }
                        tempList = Lists.newArrayList();
                        ++count;
                    }
                }

            }
        }
    }

    public void recalcIssueInStock(int taskId, String shopId, int type, Map<String, Map<String, ProhibitedGoods>> prohibitedGoods) {

        List<Stock> list = allocationDOMapper2.getShopIssueInStocks(this.getTypeSql(type), shopId);

        if (list != null && list.size() > 0) {
            LoggerUtil.info(logger, "取数结果为：{0}", list.size());

            // 分批录入
            List<Stock> tempList = Lists.newArrayList();
            int count = 1;
            for (int i = 0; i < list.size(); i++) {
                Long totalStockQty = 0l;
                Stock stock = list.get(i);
                stock.setTaskId(taskId);
                if (stock.getStockQty() == null || stock.getStockQty() < 0) {
                    stock.setStockQty(0l);
                }
                totalStockQty = stock.getStockQty();
                if (stock.getPathStockQty() != null) {
                    totalStockQty = totalStockQty + stock.getPathStockQty();
                }
                if (stock.getMoveQty() != null) {
                    totalStockQty = totalStockQty + stock.getMoveQty();
                }
                stock.setTotalStockQty(totalStockQty);
                Map<String, ProhibitedGoods> matCodeMap;
                ProhibitedGoods prohibitedGoodsDo;
                if ((matCodeMap = prohibitedGoods.get(stock.getShopID())) != null && (prohibitedGoodsDo = matCodeMap.get(stock.getMatCode())) != null) {
                    stock.setRuleName(prohibitedGoodsDo.getRuleName());
                    //若有保底数量时, 保存保底策略数量
                    if (prohibitedGoodsDo.getMinQty() != null) {
                        stock.setMinQty(prohibitedGoodsDo.getMinQty());
                    } else {
                        //保留禁配数量0与禁配标志
                        stock.setMinQty(0);
                        stock.setIsProhibited(1);
                    }
                }

                stock.setIsEliminate(stock.getMatTypeName() == null ? 0 : "淘汰".equals(stock.getMatTypeName()) ? 1 : 0);

                tempList.add(stock);

                if (i == 5000 * count || i == list.size() - 1) {
                    LoggerUtil.info(logger, "取数结果为： RecalcIssueInStock size{0}", tempList.size());
                    if (tempList.size() > 0) {
                        // 先处理价格
                        issueDOMapper2.addRecalcIssueInStock(tempList);
                    }
                    tempList = Lists.newArrayList();
                    ++count;
                }
            }
        }
    }

    private String getTypeSql(int type) {
        // 服装
        String typeSql = "and Categorycode in ('M','W')";
        if (type == 2) {
            // 百货
            typeSql = "and Categorycode not in ('M','W')";
        }
        return typeSql;
    }

    /**
     * 根据门店ID获取门店禁品
     *
     * @param taskId
     * @param shopId
     * @return
     */
    public Map<String, Map<String, ProhibitedGoods>> getShopProhibitedGoods(int taskId, String shopId) {
        Map<String, Map<String, ProhibitedGoods>> map = prohibitedService.getProhibitedGoods();
        if (map == null) {
            return Collections.emptyMap();
        }
        Map<String, ProhibitedGoods> targetMap = map.get(shopId);
        Map<String, Map<String, ProhibitedGoods>> singleShopMap = new HashMap<>(1);
        singleShopMap.put(shopId, targetMap);
        return singleShopMap;
    }

    /**
     * 删除重算数据
     * @param taskId
     * @param shopId
     * @return
     */
    public Result<Integer> delRecalcData(int taskId, String shopId) {
        int rst = 0;
        issueRecalcMapper.del_issue_undo(shopId,taskId);
        issueRecalcMapper.del_issue_out_stock_remain(taskId);
        issueRecalcMapper.del_issue_need_stock(shopId,taskId);
        issueRecalcMapper.del_issue_midcategory_data(shopId,taskId);
        issueRecalcMapper.del_issue_mid_category_qty(shopId,taskId);
        issueRecalcMapper.del_issue_in_stock(shopId,taskId);
        issueRecalcMapper.del_issue_goods_data(shopId,taskId);
        issueRecalcMapper.del_issue_detail(shopId,taskId);
        issueRecalcMapper.del_issue_category_data(shopId,taskId);
        return ResultUtil.handleSuccessReturn(rst);
    }

    /**
     * 重置剩余库存
     * @param taskId
     * @return
     */
    public Result<Integer> resetStockRemain(int taskId) {
        int rst = issueRecalcMapper.del_issue_out_stock_remain_by_taskId(taskId);
        issueService2.processRemainStock(taskId);
        return ResultUtil.handleSuccessReturn(rst);
    }

    public void schedulerRecalcTask() {
        IssueTaskVo task = issueRestService.getLastTask();
        if (task == null) {
            logger.warn("SCHEDULER RECALC SINGLE SHOP getLastTask Null");
            return;
        }
        int taskId = task.getTaskId();

        //是否有正在执行的任务
        RecalcTaskDo doingTask = recalcTaskService.hasDoingTask(taskId);
        if (doingTask != null && doingTask.getStatus() == cn.nome.saas.allocation.constant.old.Constant.STATUS_RECALC) {
            logger.warn("SCHEDULER RECALC SINGLE SHOP hasDoingTask:{}", doingTask.getId());
            return;
        }

        RecalcTaskDo recalcTaskDo = recalcTaskService.getOneValidTask(taskId);
        if (recalcTaskDo == null) {
            logger.info("SCHEDULER RECALC SINGLE SHOP getOneValidTask Null");
            return;
        }
        Result rst = shopRecalc(recalcTaskDo.getId(), recalcTaskDo.getTaskId(), recalcTaskDo.getShopId());
        if (rst != null && Constant.SUCCESS.equals(rst.getCode())){
            recalcTaskService.taskFinishStatus(recalcTaskDo.getId());
            logger.info("SCHEDULER RECALC SINGLE SHOP DONE:{}", recalcTaskDo.getId());
        }
    }
}
