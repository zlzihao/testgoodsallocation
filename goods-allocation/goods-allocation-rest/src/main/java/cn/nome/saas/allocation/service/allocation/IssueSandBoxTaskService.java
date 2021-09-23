package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.model.issue.IssueSandboxDetailDo;
import cn.nome.saas.allocation.model.issue.IssueSandboxShopStockDo;
import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.repository.dao.allocation.IssueSandBoxTaskMapper;
import cn.nome.saas.allocation.repository.dao.allocation.NewIssueDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.IssueSandBoxTask;
import cn.nome.saas.allocation.service.portal.UserService;
import cn.nome.saas.allocation.utils.ExcelUtil;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * IssueSandBoxTaskService
 *
 * @author Bruce01.fan
 * @date 2019/11/29
 */
@Service
public class IssueSandBoxTaskService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;
    @Autowired
    IssueSandBoxTaskMapper issueSandBoxTaskMapper;
    @Autowired
    NewIssueDOMapper newIssueDOMapper;

    public Result createTask(String taskName, String startDate, String endDate, Integer useSalePredict, String userId) {

        IssueSandBoxTask issueSandBoxTask = issueSandBoxTaskMapper.getSandBoxTaskByName(taskName);

        if (issueSandBoxTask != null) {
            return ResultUtil.handleBizFailtureReturn("12000","该任务名已经存在，请修改后再提交");
        }

        LocalUser user = userService.getUser(userId);

        issueSandBoxTask = new IssueSandBoxTask();
        issueSandBoxTask.setTaskName(taskName);
        issueSandBoxTask.setCreator(user.getUserName());
        issueSandBoxTask.setStartDate(DateUtil.parse(startDate,DateUtil.DATE_ONLY));
        issueSandBoxTask.setEndDate(DateUtil.parse(endDate,DateUtil.DATE_ONLY));
        issueSandBoxTask.setUseSalePredict(useSalePredict);
        issueSandBoxTask.setStatus(IssueSandBoxTask.READY_STATUS);

        Integer succ = issueSandBoxTaskMapper.addSandBoxTask(issueSandBoxTask);

        if (succ > 0) {
            return ResultUtil.handleSuccessReturn();
        }

        return ResultUtil.handleBizFailtureReturn("12000","创建失败");
    }

    public List<IssueSandBoxTask> queryByParam(String keyWord,Page page) {
        List<IssueSandBoxTask> list = issueSandBoxTaskMapper.querySandBoxTaskList(keyWord,page);
        return list;
    }

    public Integer queryByParamCount(String keyWord) {
        return issueSandBoxTaskMapper.querySandBoxTaskCount(keyWord);
    }

    @Async("taskExecutor")
    public void recalc(int taskId) {
        issueSandBoxTaskMapper.updateStatus(taskId,IssueSandBoxTask.READY_STATUS);
    }

    public void exportSandBoxData(Integer taskId, HttpServletRequest request, HttpServletResponse response) {
        List<IssueSandboxDetailDo> list = newIssueDOMapper.getIssueSandBoxDetail(taskId);

        if (list.size() > 0) {
            try {
                ExcelUtil.exportSandBoxData("", "", list, request, response);
            } catch (Exception e) {
                LoggerUtil.error(e, logger, "exportSandBoxData catch exception");
                throw new BusinessException("12000","导出沙盘计算明细异常");
            }
        } else {
            throw new BusinessException("12000","没有需要导出的沙盘计算明细");
        }
    }

    public void exportStockDetail(Integer taskId, HttpServletRequest request, HttpServletResponse response) {
        List<IssueSandboxShopStockDo> list = newIssueDOMapper.getIssueSandboxShopStock(taskId);

        if (list.size() > 0) {
            try {
                ExcelUtil.exportSandBoxShopStockData("", "", list, request, response);
            } catch (Exception e) {
                LoggerUtil.error(e, logger, "exportSandBoxData catch exception");
                throw new BusinessException("12000","导出沙盘计算库存明细异常");
            }
        } else {
            throw new BusinessException("12000","没有需要导出的沙盘计算库存明细");
        }
    }
}
