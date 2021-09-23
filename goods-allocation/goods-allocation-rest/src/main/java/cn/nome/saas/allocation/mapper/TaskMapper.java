package cn.nome.saas.allocation.mapper;

import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.AllocationTask;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.repository.entity.allocation.TaskDO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class TaskMapper {

    public static Task mapper(String taskData) {

        Task task = new Task();

        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(taskData);

            if (jsonObject.has("taskId")) {
                task.setTaskId(jsonObject.get("taskId").getAsInt());
            }

            if (jsonObject.has("taskName")) {
                task.setTaskName(jsonObject.get("taskName").getAsString());
            }
            if (jsonObject.has("taskType")) {
                task.setTaskType(jsonObject.get("taskType").getAsInt());
            }
            if (jsonObject.has("allocationType")) {
                task.setAllocationType(jsonObject.get("allocationType").getAsInt());
            }
            if (jsonObject.has("rightNow")) {
                task.setRightNow(jsonObject.get("rightNow").getAsInt());
            }
            if (jsonObject.has("runTime")) {
                task.setRunTimeStr(jsonObject.get("runTime").getAsString());
            }

            if (jsonObject.has("areaType")) {
                task.setAreaType(jsonObject.get("areaType").getAsInt());
            }

            // 撤店任务id
            if (jsonObject.has("closeTaskId")) {
                task.setCloseTaskId(jsonObject.get("closeTaskId").getAsInt());
            }

            // 陈列品类名称
            if (jsonObject.has("categoryNames")) {
                task.setCategoryNames(jsonObject.get("categoryNames").getAsString());
            }

            // 年份
            if (jsonObject.has("year")) {
                task.setYear(jsonObject.get("year").getAsString());
            }

            // 季节
            if (jsonObject.has("season")) {
                task.setSeason(jsonObject.get("season").getAsString());
            }

            // 易碎标示
            if (jsonObject.has("breakable")) {
                task.setBreakable(jsonObject.get("breakable").getAsInt());
            }

            // 调入天数
            if (jsonObject.has("inDays")) {
                task.setInDays(jsonObject.get("inDays").getAsInt());
            }

            // 调出天数
            if (jsonObject.has("outDays")) {
                task.setOutDays(jsonObject.get("outDays").getAsInt());
            }

            // 最小起调金额
            if (jsonObject.has("minAllocationPrice")) {
                task.setMinAllocationPrice(jsonObject.get("minAllocationPrice").getAsInt());
            } else {
                task.setMinAllocationPrice(1000);
            }

            // 最高费率
            if (jsonObject.has("maxFeeRatio")) {
                task.setMaxFeeRatio(jsonObject.get("maxFeeRatio").getAsDouble());
            } else {
                task.setMaxFeeRatio(0.05);
            }

            if (jsonObject.has("demandShopIds")) {
                task.setDemandShopIds(jsonObject.get("demandShopIds").getAsString());
            }
            if (jsonObject.has("supplyShopIds")) {
                task.setSupplyShopIds(jsonObject.get("supplyShopIds").getAsString());
            }

        }catch (Exception e) {
        }

        return task;
    }


    public static TaskDO mapper(Task task) {
        TaskDO taskDO  = new TaskDO();
        BeanUtils.copyProperties(task,taskDO);
        return taskDO;
    }

    public static Task mapper(TaskDO taskDO) {
        Task task = new Task();
        BeanUtils.copyProperties(taskDO,task);

        return task;
    }

    public static List<AllocationTask> mapperAllocationTask(List<TaskDO> taskDOList) {
       return taskDOList.stream().map(taskDO -> {
           AllocationTask task = new AllocationTask();

           BeanUtils.copyProperties(taskDO,task);
           task.setCreator(taskDO.getUserName());

           String goodsScope = taskDO.getTaskType() == Constant.CLOTHING_TYPE_TASK ? "服装" : "百货"+"、";
           goodsScope += taskDO.getBreakable() == 0 ? "非易碎" : "易碎";
           if (StringUtils.isNotBlank(taskDO.getYear())) {
               goodsScope += "、" + taskDO.getYear();
           }
           if (StringUtils.isNotBlank(taskDO.getSeason())) {
               goodsScope += "、" + taskDO.getSeason();
           }
           task.setGoodsScope(goodsScope);

            return task;
        }).collect(Collectors.toList());
    }

    public static List<Task> mapper(List<TaskDO> taskDOList) {
        return taskDOList.stream().map(taskDO -> {
            Task task = new Task();
            BeanUtils.copyProperties(taskDO,task);

            return task;
        }).collect(Collectors.toList());
    }
}
