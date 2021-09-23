package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.saas.allocation.model.old.issue.OperateLog;
import org.apache.ibatis.annotations.Param;

public interface IssueOperateLogMapper2 {
    int saveLog(@Param("operateLog") OperateLog operateLog);
}