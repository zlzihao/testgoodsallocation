package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.saas.allocation.repository.old.allocation.entity.RecalcTaskDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecalcTaskMapper {

    RecalcTaskDo detail(@Param("taskId") int taskId, @Param("shopId") String shopId);

    List<RecalcTaskDo> validList(@Param("taskId") int taskId);

    Integer shopValidCount(@Param("taskId") int taskId, @Param("shopId") String shopId);

    Integer hasRecalcTask(@Param("taskId") int taskId);

    Integer add(@Param("taskDo") RecalcTaskDo taskDo);

    Integer taskFinishStatus(@Param("recalcId") int recalcId);

    Integer cancle(@Param("recalcId") int recalcId);

    Integer existCount(@Param("recalcId") int recalcId);

    RecalcTaskDo detailById(@Param("recalcId") int recalcId);

    List<RecalcTaskDo> detailByRecalcIds(@Param("recalcIds")  List<String> recalcIds);

    void setRecalcPercent(@Param("recalcId") int recalcId, @Param("percentNum") int percentNum);

    RecalcTaskDo todayOneValidTask(@Param("taskId")  int taskId);

    RecalcTaskDo hasDoingTask(@Param("taskId") int taskId);

    Integer updateRunSts(@Param("recalcId") int recalcId);

    List<RecalcTaskDo> timeoutTask();

    void timeoutCancle(@Param("recalcId") int recalcId);

}

