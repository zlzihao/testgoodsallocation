package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.NewIssueInStockDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zengdewu@nome.com
 */
public interface ApsIssueInStockDataMapper {

    List<NewIssueInStockDo> getIssueInStockList(@Param("shopIds") Set<String> shopIds);

}
