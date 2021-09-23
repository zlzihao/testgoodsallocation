package cn.nome.saas.allocation.repository.dao.vertical;


import cn.nome.saas.allocation.model.issue.HuihuoGoodsDo;
import cn.nome.saas.allocation.model.issue.NewIssueExtraStockSkuDo;
import cn.nome.saas.allocation.model.issue.NewIssueInStockDo;
import cn.nome.saas.allocation.model.issue.NewIssueOutStockDo;
import cn.nome.saas.allocation.repository.entity.vertical.MidSalePredictDO;
import org.apache.ibatis.annotations.Param;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * NewIssueExtraDataMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
public interface NewIssueExtraDataMapper {

    /**
     *
     * @param stockId
     */
    List<NewIssueOutStockDo> getIssueOutStockList(@Param("stockId") String stockId);

    /**
     *
     * @param huihuoDate
     */
    List<HuihuoGoodsDo> getHuihuoGoodsByDate(@Param("huihuoDate") Date huihuoDate);

    /**
     *
     * @param type
     * @return
     */
    List<NewIssueInStockDo> getIssueInStockList(@Param("type") int type, @Param("shopIds") Set<String> shopIds);

    /**
     * 沙盘获取instock
     * @param type
     * @param shopIds
     * @return
     */
    List<NewIssueInStockDo> getIssueInStockListSandBox(@Param("type") int type, @Param("shopIds") Set<String> shopIds);

    /**
     * 获取新品
     * @return
     */
    List<NewIssueInStockDo> getNewSkcList(@Param("shopIds") Set<String> shopIds);

    /**
     * 获取销售预测
     * @param endDate
     * @return
     */
    List<MidSalePredictDO> getIssueSalePredict(@Param("endDate") Date endDate);

    /**
     * 获取数仓任务状态
     * @return
     */
    int getDataStockStatus();
}
