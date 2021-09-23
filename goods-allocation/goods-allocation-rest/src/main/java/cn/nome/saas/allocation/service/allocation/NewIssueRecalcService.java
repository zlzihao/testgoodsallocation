package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.issue.IssueReserveDetailDo;
import cn.nome.saas.allocation.repository.dao.allocation.IssueTaskDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ReserveStockSettingsMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.repository.entity.allocation.IssueTaskDO;
import cn.nome.saas.allocation.repository.entity.allocation.ReserveStockSettingsDO;
import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import cn.nome.saas.allocation.utils.old.BizException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.ISSUE_RESERVE_DETAIL_TABLE;

@Service
public class NewIssueRecalcService {

    private static Logger logger = LoggerFactory.getLogger(NewIssueRecalcService.class);

    @Autowired
    NewIssueDOMapper newIssueDOMapper;

    @Autowired
    IssueTaskDOMapper issueTaskDOMapper;
    @Autowired
    ReserveStockSettingsMapper reserveStockSettingsMapper;

    @Autowired
    NewIssueBasicService newIssueBasicService;

    @Autowired
    NewIssueSkuCalcService newIssueSkuCalcService;

    @Autowired
    NewIssueMatchService newIssueMatchService;
    @Autowired
    cn.nome.saas.allocation.service.old.allocation.IssueService issueService;

    /**
     * 线程池
     */
    private ExecutorService issueReCalcMainPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("issue-reCalc-main-%d").build());


    /**
     * 门店重算
     *
     * @param taskId
     * @param shopId
     * @return
     */
    public Result shopReCalc(int reCalcId,int taskId, String shopId) {
        try {
            RecalcTaskDo taskDo = newIssueDOMapper.hasDoingReCalcTask(taskId);
            if (taskDo != null) {
                return ResultUtil.handleSysFailtureReturn("已有任务在执行");
            }
            Integer rst = newIssueDOMapper.updateReCalcStatus(reCalcId);
            if (rst == null || rst == 0){
                return ResultUtil.handleSysFailtureReturn("重算任务运行状态更新异常");
            }

            logger.debug("NewIssue shopReCalc:{},taskId:{},recalcId:{}", shopId, taskId, reCalcId);
            Date start = new Date();

            IssueTaskDO task = newIssueDOMapper.getIssueTask(taskId);

            Set<String> shopIds = new HashSet<>(Collections.singletonList(shopId));
            try {
                logger.info("SINGLE ISSUE START");

                //更新每天配发天数
                issueService.updateIssueDaysByShopId(shopId);

                //跑批预留存
                Map<String, IssueReserveDetailDo> reserveDetailDoMap = null;
                ReserveStockSettingsDO reserveStockSettingsDO = reserveStockSettingsMapper.getLatest();
                if (reserveStockSettingsDO != null && ReserveStockSettingsDO.IS_ENABLE_TRUE.equals(reserveStockSettingsDO.getIsEnable())) {
                    List<IssueReserveDetailDo> list = newIssueDOMapper.getIssueReserveDetail(shopIds);
                    reserveDetailDoMap = list.stream().collect(Collectors.toMap(IssueReserveDetailDo::getShopIdMatCodeSizeNameKey, Function.identity()));
                }

                //in跟out可以同时跑
                CountDownLatch issueMainCount = new CountDownLatch(2);
                issueReCalcMainPool.submit(() -> {
                    try {
                        newIssueBasicService.issueInStock(task, shopIds, true);
                        logger.info("SINGLE ISSUE IN END shopId{0},time:{1}", shopId, new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[SINGLE ISSUE IN] catch exception");
                    } finally {
                        issueMainCount.countDown();
                    }
                });
                issueReCalcMainPool.submit(() -> {
                    try {
                        newIssueBasicService.issueOutStock(task, shopIds, true);
                        logger.info("SINGLE ISSUE OUT END shopId{0},time:{1}", shopId, new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger, "[SINGLE ISSUE OUT] catch exception");
                    } finally {
                        issueMainCount.countDown();
                    }
                });
                issueMainCount.await();
                newIssueDOMapper.updateReCalcTaskPercent(reCalcId, RandomUtils.nextInt(41, 50));
                logger.info("SINGLE ISSUE IN And OUT END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

                newIssueSkuCalcService.calcSKURequirement(task.getId(), shopIds,task.getRunTime(), true, reserveDetailDoMap);
                logger.info("SINGLE ISSUE NEED END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));
                newIssueDOMapper.updateReCalcTaskPercent(reCalcId, RandomUtils.nextInt(71, 75));
                newIssueMatchService.issueDetail(task, shopIds, true);
                logger.info("SINGLE ISSUE DETAIL END : " + new SimpleDateFormat("yyMMdd HHmmss").format(new Date()));

                issueTaskDOMapper.updateTaskStatus(task.getId());
                logger.info("SINGLE ISSUE OVER");
            } catch (Exception e) {
                LoggerUtil.error(e, logger, "[SINGLE ISSUE] catch exception e" );
            }

//
//            //扣除剩余库存
//            this.recalcDeductIssueOutStockRemain(taskId,shopId);
//
//            syncLock.lock();
//            try {
//                this.setInvalidSts(taskId,shopId);
//                LoggerUtil.info(logger, "11.INVALIDSTS：{0}分钟", DateProcessUtil.getMinute(date1));
//                this.setValidSts(taskId,shopId);
//                LoggerUtil.info(logger, "11.VALIDSTS：{0}分钟", DateProcessUtil.getMinute(date1));
//            }finally {
//                syncLock.unlock();
//            }
//
//            this.setRecalcPercent(recalcId,100);

//            LoggerUtil.info(logger, "12.RECALC DONE：{0}分钟", DateProcessUtil.getMinute(start));
            return ResultUtil.handleSuccessReturn(shopId);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "shopRecalc catch exception");
            return null;
        }

    }


    public int cancel(int reCalcId) {

        int rst = 0;

        RecalcTaskDo taskDo = newIssueDOMapper.getReCalcTaskById(reCalcId);
        if (taskDo == null) {
            logger.warn("recalc task no exist:{}", reCalcId);
            return 1;
        }
        if (taskDo.getStatus() == 0) {
            rst = newIssueDOMapper.reCalcTaskCancel(reCalcId);
        } else if (taskDo.getStatus() == 2) {
            //todo 重算状态下怎样取消
            //rst = recalcTaskMapper.cancle(recalcId);
            throw new BizException("任务重算中,暂不支持取消");
        }
        return rst;
    }

    public Result getPercent(String recalcIds) {
        List<String> recalcIdList = Arrays.asList(recalcIds.split(","));
        List<RecalcTaskDo> details = newIssueDOMapper.getReCalcTaskListById(recalcIdList);
        return ResultUtil.handleSuccessReturn(details);
    }


    public int countRecalcTask(int taskId) {
        return newIssueDOMapper.hasReCalcTask(taskId);
    }
}
