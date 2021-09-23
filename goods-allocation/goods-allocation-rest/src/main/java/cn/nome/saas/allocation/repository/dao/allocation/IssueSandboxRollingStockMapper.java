package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.IssueSandboxRollingStockDO;
import cn.nome.saas.allocation.model.issue.IssueSandboxShopStockDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zengdewu@nome.com
 */
public interface IssueSandboxRollingStockMapper {

    Integer addIssueSandboxRollingStock(@Param("list") List<IssueSandboxRollingStockDO> list);

}
