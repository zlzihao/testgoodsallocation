package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.old.allocation.ShopInfoDo;
import cn.nome.saas.allocation.model.old.issue.IssueTaskVo;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueDOMapper2;
import cn.nome.saas.allocation.repository.old.allocation.dao.RecalcTaskMapper;
import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.old.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author chentaikuang
 */
@Service
public class RecalcTaskService {

    private static Logger logger = LoggerFactory.getLogger(RecalcTaskService.class);

    @Autowired
    @Lazy
    AsyncTask asyncTask;

    @Autowired
    RecalcTaskMapper recalcTaskMapper;

    @Autowired
    IssueRestService issueRestService;

    @Autowired
    IssueDOMapper2 issueDOMapper2;

    public Result getPercent(String recalcIds) {
        List<String> recalcIdList = Arrays.asList(recalcIds.split(","));
        List<RecalcTaskDo> details = recalcTaskMapper.detailByRecalcIds(recalcIdList);
        return ResultUtil.handleSuccessReturn(details);
    }

    public synchronized Result add(int taskId, String shopId) {

        //检查是否有全局重算
        IssueTaskVo taskVo = issueRestService.getLastTask();
        if (taskVo != null) {
            if (taskId < taskVo.getTaskId()) {
                return ResultUtil.handleSysFailtureReturn("该门店重算任务ID过低，请刷新页面再试");
            } else if (taskVo.getReRun() == Constant.STATUS_RERUN) {
                return ResultUtil.handleSysFailtureReturn("当前正在执行全部门店重算，请稍候再试");
            }
        }

        //检查是否有效门店
        ShopInfoDo shopInfoDo = issueDOMapper2.getShop(shopId);
        if (shopInfoDo == null) {
            return ResultUtil.handleSysFailtureReturn("查询门店[" + shopId + "]不存在，添加失败");
        }

        RecalcTaskDo taskDo = new RecalcTaskDo();
        taskDo.setShopId(shopId);
        taskDo.setTaskId(taskId);
        taskDo.setName(Constant.TASK_FLAG_SR + "-" + shopId + "-" + taskId);
        taskDo.setOperator(AuthUtil.getSessionUserId());
        taskDo.setStatus(Constant.STATUS_VALID);

        Integer validCount = recalcTaskMapper.shopValidCount(taskId, shopId);
        if (validCount != null && validCount > 0) {
            return ResultUtil.handleSysFailtureReturn("该门店重算任务已排队中");
        }

        boolean runNow = needRunNow(taskId);

        Integer rst = recalcTaskMapper.add(taskDo);
        if (rst == null || rst == 0) {
            return ResultUtil.handleSysFailtureReturn("该门店重算任务添加失败");
        }

        if (runNow){
            asyncTask.shopRecalc(taskDo.getId(), taskDo.getTaskId(), taskDo.getShopId());
            logger.info("RECALC TASK ADD RUN NOW:{}", taskDo.getId());
        }

        return ResultUtil.handleSuccessReturn(taskDo);
    }

    private boolean needRunNow(int taskId) {
        Integer count = recalcTaskMapper.hasRecalcTask(taskId);
        return count == null || count == 0;
    }

    public int taskFinishStatus(int recalcId) {
        return recalcTaskMapper.taskFinishStatus(recalcId);
    }

    public int cancle(int recalcId) {

        int rst = 0;

        RecalcTaskDo taskDo = recalcTaskMapper.detailById(recalcId);
        if (taskDo == null) {
            logger.warn("recalc task no exist:{}", recalcId);
            return 1;
        }
        if (taskDo.getStatus() == 0) {
            rst = recalcTaskMapper.cancle(recalcId);
        } else if (taskDo.getStatus() == 2) {
            //todo 重算状态下怎样取消
            //rst = recalcTaskMapper.cancle(recalcId);
            throw new BizException("任务重算中,暂不支持取消");
        }
        return rst;
    }

    public void setRecalcPercent(int recalcId, int percentNum) {
        recalcTaskMapper.setRecalcPercent(recalcId, percentNum);
    }

    public boolean hasRecalcTask(int taskId) {
        Integer rst = recalcTaskMapper.hasRecalcTask(taskId);
        if (rst == null || rst == 0) {
            return false;
        }
        return true;
    }

    public int countRecalcTask(int taskId) {
        Integer rst = recalcTaskMapper.hasRecalcTask(taskId);
        return rst == null ? 0 : rst;
    }

    public RecalcTaskDo getOneValidTask(int taskId) {
        return recalcTaskMapper.todayOneValidTask(taskId);
    }

    public RecalcTaskDo hasDoingTask(int taskId) {
        return recalcTaskMapper.hasDoingTask(taskId);
    }

    public Integer updateRunSts(int recalcId) {
        return recalcTaskMapper.updateRunSts(recalcId);
    }

    public List<RecalcTaskDo> timeoutTask() {
        return recalcTaskMapper.timeoutTask();
    }

    public void timeoutCancle(int recalcId) {
        try {
            recalcTaskMapper.timeoutCancle(recalcId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("timeoutCancle :{},recalcId:{}", e.getMessage(), recalcId);
        }
    }
}
