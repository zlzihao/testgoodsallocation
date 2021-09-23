package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.allocation.QdIssueConfig;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.vertical.QdIssueExtraDataMapper;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueConfigDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueOutStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueTaskDO;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.utils.StackUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 秋冬老品配发
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
@Service
public class QdIssueTaskService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    QdIssueTaskDOMapper qdIssueTaskDOMapper;

    @Autowired
    QdIssueService qdIssueService;

    @Autowired
    QdIssueDepthSuggestDOMapper qdIssueDepthSuggestDOMapper;

    @Autowired
    QdIssueCategoryStructureDOMapper qdIssueCategoryStructureDOMapper;

    @Autowired
    QdIssueDetailDOMapper qdIssueDetailDOMapper;

    @Autowired
    QdIssueDOMapper qdIssueDOMapper;

    @Autowired
    QdIssueConfigDOMapper qdIssueConfigDOMapper;


    ExecutorService executor = Executors.newFixedThreadPool(1);

    public Result createTask(String taskName,String runTime) {
        // 任务时间重复校验
        QdIssueTaskDO oldQdIssueTaskDO = qdIssueTaskDOMapper.queryByRunTime(runTime);
        if(oldQdIssueTaskDO != null){
            return ResultUtil.handleSysFailtureReturn("任务时间重复");
        }

        // 生成任务
        QdIssueTaskDO qdIssueTaskDO = new QdIssueTaskDO();

        qdIssueTaskDO.setName(taskName);
        qdIssueTaskDO.setTaskStatus(0);
        qdIssueTaskDO.setRunTime( DateUtil.parse(runTime,"yyyy-MM-dd HH:mm"));
        qdIssueTaskDO.setRemark("READY");
        qdIssueTaskDO.setUpdatedAt(new Date());
        qdIssueTaskDO.setCreatedAt(new Date());
        qdIssueTaskDO.setTplCode("qd_issue_result");
        qdIssueTaskDOMapper.addTask(qdIssueTaskDO);

        return ResultUtil.handleSuccessReturn(qdIssueTaskDO.getId());
    }

    public Integer getRunningTask() {
        return qdIssueTaskDOMapper.getRunningTaskCount();
    }


    public Integer runTask(int taskId) {
        qdIssueTaskDOMapper.updateTask(taskId,1);
        return qdIssueTaskDOMapper.updateRemark(taskId,"RUN");
    }

    public Integer finishTask(int taskId) {
         qdIssueTaskDOMapper.updateTask(taskId,2);
         return qdIssueTaskDOMapper.updateRemark(taskId,"FINISH");
    }

    public List<QdIssueTaskDO> list(String taskName) {

        Map<String,Object> param = new HashMap<>();
        param.put("taskName",taskName);

        return qdIssueTaskDOMapper.taskList(param);
    }

    public Integer deleteByTaskId(int taskId) {
        qdIssueTaskDOMapper.deleteById(taskId);
        qdIssueDOMapper.deleteInStock(taskId);
        qdIssueDOMapper.deleteOutStock(taskId);
        qdIssueDOMapper.deleteSkcStock(taskId);
        qdIssueDOMapper.deleteNewSkcStock(taskId);
        qdIssueDetailDOMapper.deleteIssueDetail(taskId);
        return 1;
    }


    public Result runTask() {

        QdIssueTaskDO qdIssueTaskDO = qdIssueTaskDOMapper.getRunableTask();

        if (qdIssueTaskDO != null) {
            Integer taskId = qdIssueTaskDO.getId();
            boolean flag = this.isIntegrationDataFinish();
            if (!flag) {
                qdIssueTaskDOMapper.updateRemark(taskId,"字段更新任务未执行完成");
                return ResultUtil.handleSuccessReturn("字段更新任务未执行完成");
            }

            // 异步执行
            this.runTask(taskId);

            executor.execute(()->{
                try{
                    // 执行
                    qdIssueService.qdIssueTask(taskId);

                    // 标记完成
                    this.finishTask(taskId);
                }catch (Exception e){
                    LoggerUtil.error(logger,"执行秋冬老品任务报错，异常信息={0}", StackUtil.getStackTrace(e));
                    qdIssueTaskDOMapper.updateTask(taskId,0);
                    qdIssueTaskDOMapper.updateRemark(taskId,e.getMessage().length() > 100?e.getMessage().substring(0,99):e.getMessage());
                }
            });

        }

        return ResultUtil.handleSuccessReturn();

    }

    /**
     * 是否整合数据完毕
     *
     * @return
     */
    public boolean isIntegrationDataFinish(){
        // 获取未处理的深度尺码数据
        int noDealDepthNum = qdIssueDepthSuggestDOMapper.countForDealStatus(0);
        if(noDealDepthNum > 0){
            LoggerUtil.info(logger,"有{0}条深度尺码数据待处理",noDealDepthNum);
            return false;
        }

        // 获取未处理的品类结构数据
        int noDealCateNum = qdIssueCategoryStructureDOMapper.countForDealStatus(0);
        if(noDealDepthNum > 0){
            LoggerUtil.info(logger,"有{0}条品类结构数据待处理",noDealCateNum);
            return false;
        }
        return true;
    }

    public void saveConfig(int type ,String value) {

        QdIssueConfigDO qdIssueConfigDO = new QdIssueConfigDO();

        qdIssueConfigDO.setType(type);
        qdIssueConfigDO.setName(value);

        List<QdIssueConfigDO> qdIssueConfigDOList = qdIssueConfigDOMapper.getConfigByType(type);

        if (CollectionUtils.isEmpty(qdIssueConfigDOList)) {
            qdIssueConfigDOMapper.insertConfig(qdIssueConfigDO);
        } else {
            qdIssueConfigDOMapper.updateConfig(qdIssueConfigDO);
        }

    }

    public QdIssueConfig getConfig() {

        List<QdIssueConfigDO> qdIssueConfigDOList = qdIssueConfigDOMapper.getConfig();

        if (qdIssueConfigDOList == null) {
            return  null;
        }
        QdIssueConfig qdIssueConfig = new QdIssueConfig();

        for (QdIssueConfigDO qdIssueConfigDO : qdIssueConfigDOList) {
            if (qdIssueConfigDO.getType() == 1) {
                qdIssueConfig.setSeason(qdIssueConfigDO.getName());
            } else if (qdIssueConfigDO.getType() == 2) {
                qdIssueConfig.setFullRate(qdIssueConfigDO.getName());
            }
        }

        return  qdIssueConfig;

    }


}
