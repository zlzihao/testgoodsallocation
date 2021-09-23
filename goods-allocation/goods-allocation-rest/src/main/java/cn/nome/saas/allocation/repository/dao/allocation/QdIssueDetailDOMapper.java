package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDetailDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueInStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueOutStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueSkcStockDO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueDetailDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
public interface QdIssueDetailDOMapper {

    Integer batchInsertIssueDetail(@Param("list") List<QdIssueDetailDO> qdIssueDetailDOList);

    Integer deleteIssueDetail(@Param("taskId")int taskId);
}
