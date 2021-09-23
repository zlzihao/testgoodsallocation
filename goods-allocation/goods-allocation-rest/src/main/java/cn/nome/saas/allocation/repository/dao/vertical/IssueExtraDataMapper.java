package cn.nome.saas.allocation.repository.dao.vertical;

import cn.nome.saas.allocation.repository.entity.vertical.IssueInStockDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueOutStockDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueRejectSupplyDO;
import cn.nome.saas.allocation.repository.entity.vertical.MidSalePredictDO;
import org.apache.ibatis.annotations.Param;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * IssueExtraDataMapper
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public interface IssueExtraDataMapper {

    List<IssueInStockDO> getIssueInStockList(@Param("type") int type, @Param("period") int period);

    List<IssueInStockDO> getNewSkcList();

    List<IssueOutStockDO> getIssueOutStockList();

    /**
     * 获取撤店供应列表
     * @param shopIdList
     * @return
     */
    List<IssueRejectSupplyDO> getIssueRejectSupplyList(@Param("shopIdList") Set<String> shopIdList);

}
