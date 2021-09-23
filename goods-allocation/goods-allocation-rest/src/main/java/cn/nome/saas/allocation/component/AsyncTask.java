package cn.nome.saas.allocation.component;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.allocation.model.issue.IssueReserveDetailDo;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.OrderDetailReq;
import cn.nome.saas.allocation.model.old.issue.OrderDetailVo;
import cn.nome.saas.allocation.model.old.issue.OrderDetailWrap;
import cn.nome.saas.allocation.repository.dao.allocation.IssueTaskDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.service.allocation.*;
import cn.nome.saas.allocation.service.old.allocation.IssueService;
import cn.nome.saas.allocation.service.old.allocation.ProhibitedService;
import cn.nome.saas.allocation.utils.CommonUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.ISSUE_DETAIL_TABLE_PREFIX;

@Component
public class AsyncTask {

    private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    @Autowired
    private IssueDOMapper2 issueDOMapper2;

    @Autowired
    private IssueTaskDOMapper issueTaskDOMapper;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProhibitedService prohibitedService;

    @Autowired
    private IssueRecalcService issueRecalcService;

    @Autowired
    private RecalcTaskService recalcTaskService;

    @Autowired
    private NewIssueBasicService newIssueBasicService;

    @Autowired
    private NewIssueSkuCalcService newIssueSkuCalcService;

    @Autowired
    private NewIssueMatchService newIssueMatchService;

    @Autowired
    private NewIssueRecalcService newIssueRecalcService;

    @Autowired
    private NewIssueSandBoxService newIssueSandBoxService;

    @Autowired
    private NewIssueService newIssueService;

    @Autowired
    private NewIssueDOMapper newIssueDOMapper;

    /**
     * 线程池
     */
    private ExecutorService issueMainPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-main-%d").build());


    @Async("taskExecutor")
    public void runNewIssueTask(IssueTaskDO task) {
        try {
            logger.info("RUNNEWISSUETASK START");

            //判断数仓跑批任务进度
//			boolean dataStockFlag = false;
//			while (!dataStockFlag) {
//				if (newIssueBasicService.getDataStockStatus() == 3) {
//					dataStockFlag = true;
//				}
//				//没跑完休眠10秒后再查询
//				if (!dataStockFlag) {
//					Thread.sleep(10000);
//				}
//			}

            Map<String, IssueReserveDetailDo> reserveDetailDoMap = null;
            // TODO 因数据为空，不会执行这一步
            //跑批预留存
//            Map<String, IssueReserveDetailDo> reserveDetailDoMap = null;
//            ReserveStockSettingsDO reserveStockSettingsDO = reserveStockSettingsMapper.getLatest();
//            if (reserveStockSettingsDO != null && ReserveStockSettingsDO.IS_ENABLE_TRUE.equals(reserveStockSettingsDO.getIsEnable())) {
//                //重算不计算预留存时则取上次跑批数据
//                if (task.getTaskType() == TASK_TYPE_UNRECALC_RESERVE) {
//                    List<IssueReserveDetailDo> issueReserveDetailDos = newIssueDOMapper.getIssueReserveDetail(null);
//                    //发货日期的detailReserveMap<shopId_matCode_sizeName, IssueReserveDetailDo>
//                    reserveDetailDoMap = issueReserveDetailDos.stream().collect(Collectors.toMap(IssueReserveDetailDo::getShopIdMatCodeSizeNameKey, Function.identity()));
//                } else {
//                    newIssueDOMapper.truncateTable(ISSUE_RESERVE_DETAIL_TABLE);
//
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                    IssueSandBoxTask issueSandBoxTask = new IssueSandBoxTask();
//                    issueSandBoxTask.setStartDate(sdf.parse(sdf.format(new Date())));
//                    issueSandBoxTask.setEndDate(sdf.parse(reserveStockSettingsDO.getReserveDate()));
//                    issueSandBoxTask.setCalcType(IssueSandBoxTask.CALC_TYPE_RESERVE);
//                    issueSandBoxTask.setRemark(task.getRemark());
//                    issueSandBoxTask.setUseSalePredict(reserveStockSettingsDO.getUseSalePredict());
//                    reserveDetailDoMap = newIssueSandBoxService.sandBoxCalc(issueSandBoxTask);
//
//                    //更新预留存最后一次计算时间
//                    reserveStockSettingsDO.setLastCalcTime(new Date());
//                    reserveStockSettingsMapper.update(reserveStockSettingsDO);
//                }
//            }


            //更新每天配发天数
            issueService.updateIssueDays();

            Set<String> shopIds;
            String remark = task.getRemark();
            if (remark == null || "RERUN".equals(remark) || "RUN".equals(remark)) {
                shopIds = null;
            } else {
                shopIds = Arrays.stream(task.getRemark().split(",")).collect(Collectors.toSet());
            }

            //in跟out可以同时跑
            CountDownLatch issueMainCount = new CountDownLatch(2);
            issueMainPool.submit(() -> {
                try {
                    newIssueBasicService.issueInStock(task, shopIds, false);
                    logger.info("RUNNEWISSUETASK IN END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                } catch (Exception e) {
                    LoggerUtil.error(e, logger, "[RUNNEWISSUETASK IN] catch exception");
                    logger.error("[RUNNEWISSUETASK IN] catch exception", e);
                } finally {
                    issueMainCount.countDown();
                }
            });
            issueMainPool.submit(() -> {
                try {
                    newIssueBasicService.issueOutStock(task);
                    logger.info("RUNNEWISSUETASK OUT END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                } catch (Exception e) {
                    LoggerUtil.error(e, logger, "[RUNNEWISSUETASK OUT] catch exception");
                } finally {
                    issueMainCount.countDown();
                }
            });
            issueMainCount.await();
            logger.info("RUNNEWISSUETASK IN And OUT END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

            newIssueSkuCalcService.calcSKURequirement(
                    task.getId(),
                    shopIds,
                    task.getRunTime(),
                    false,
                    reserveDetailDoMap);
            logger.info("RUNNEWISSUETASK NEED END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

            newIssueMatchService.issueDetail(
                    task,
                    shopIds);
            logger.info("RUNNEWISSUETASK DETAIL END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

            issueTaskDOMapper.updateTaskStatus(task.getId());
            logger.info("RUNNEWISSUETASK OVER");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[runIssueTask] catch exception e");
        }
    }

    @Async("taskExecutor")
    public void newIssueSingleReCalc(int reCalcId, int taskId, String shopId) {
        Result result = newIssueRecalcService.shopReCalc(reCalcId, taskId, shopId);
        if (result != null && cn.nome.saas.allocation.constant.Constant.SUCCESS.equals(result.getCode())) {
            int rst = recalcTaskService.taskFinishStatus(reCalcId);
            logger.info("RECALCTASK taskFinishStatus, rst:{},taskId:{},shopId:{}", rst, taskId, shopId);
        }
        logger.debug("SHOP RECALC OVER");
    }

    @Async("taskExecutor")
    public void runIssueTask(IssueTask task) {
        try {
            issueDOMapper2.addTask(task);

            issueService.issueProcess(task);

            issueDOMapper2.updateTaskStatus(task.getId());
            logger.debug("RUNISSUETASK OVER");
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[runIssueTask] catch exception e");
        }
    }

    @Async("taskExecutor")
    public void runIssueTask(IssueTask task, List<String> shopIdList) {
        issueService.issueProcess(task, shopIdList);
        issueDOMapper2.updateTaskStatus(task.getId());
    }

    @Async("taskExecutor")
    public void processGoodsArea() {
        LoggerUtil.info(logger, "处理货盘商品开始");
        prohibitedService.processGoodsArea();
        LoggerUtil.info(logger, "处理货盘商品结束");
    }

    @Async("taskExecutor")
    public void shopRecalc(int recalcId, int taskId, String shopId) {
        Result result = issueRecalcService.shopRecalc(recalcId, taskId, shopId);
        if (result != null && cn.nome.saas.allocation.constant.Constant.SUCCESS.equals(result.getCode())) {
            int rst = recalcTaskService.taskFinishStatus(recalcId);
            logger.info("RECALCTASK taskFinishStatus, rst:{},taskId:{},shopId:{}", rst, taskId, shopId);
        }
        logger.debug("SHOP RECALC OVER");
    }

    @Async("taskExecutor")
    public void schedulerRecalcTask() {

        issueRecalcService.schedulerRecalcTask();
        logger.debug("SCHEDULER RECALC TASK OVER");
    }

    public void updateIssueDays() {
        issueService.updateIssueDays();
        logger.debug("SCHEDULER UPDATE ISSUE DAYS OVER");
    }

    @Async("taskExecutor")
    public void runIssueSandBoxTask(IssueSandBoxTask issueSandBoxTask) {
        issueSandBoxTask.setCalcType(IssueSandBoxTask.CALC_TYPE_SANDBOX);
        issueSandBoxTask.setUseSalePredict(0);
        newIssueSandBoxService.sandBoxCalc(issueSandBoxTask);
    }

    @Async("taskExecutor")
    public void saveExportOrderDetails(int taskId) {

        logger.info("saveExportOrderDetails, taskId: {}", taskId);
        IssueTaskDO issueTask = newIssueDOMapper.getIssueTask(taskId);

        if (issueTask == null) {
            logger.warn("saveExportOrderDetails, task not found: {}", taskId);
        }

        String detailTabName = CommonUtil.getTaskTableName(ISSUE_DETAIL_TABLE_PREFIX, taskId, issueTask.getRunTime());
        List<String> allShopIds = newIssueDOMapper.selectAllShopIds(detailTabName);
        logger.info("saveExportOrderDetails, get shopIds, count: {}", allShopIds.size());

        for (String shopId : allShopIds) {
            logger.info("saveExportOrderDetails, get shopId: {}", shopId);
            OrderDetailReq req = new OrderDetailReq();
            req.setShopId(shopId);
            req.setTaskId(taskId);
            OrderDetailWrap rst = newIssueService.getOrderDetail(req);
            List<OrderDetailVo> rows = rst.getOrderDetailVo();
            logger.info("saveExportOrderDetails, get records, shopId: {}, records: {}", shopId, rows.size());
            try {
                int count = newIssueDOMapper.insertExportOrderDetails(rows);
                logger.info("saveExportOrderDetails, shopId: {}, successful records: {}", shopId, count);
            } catch (Exception e) {
                LoggerUtil.error(e, logger, "saveExportOrderDetails, catch exception, shopId: {0}", shopId);
            }
        }
    }
}
