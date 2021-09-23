package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.IssueInStock;
import cn.nome.saas.allocation.model.issue.IssueOutStock;
import cn.nome.saas.allocation.model.issue.IssueUndoData;
import cn.nome.saas.allocation.repository.entity.allocation.IssueNeedStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.MidCategoryStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.SubWarehouseConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * IssueDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public interface SubWarehouseConfigDOMapper {

    /**
     * 根据参数获取SubWarehouseConfigDO列表
     * @param param
     * @return 1
     */
    List<SubWarehouseConfigDO> selectByPage(Map<String, Object> param);

}
