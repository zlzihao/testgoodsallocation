package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.model.issue.IssueInStock;
import cn.nome.saas.allocation.model.issue.IssueOutStock;
import cn.nome.saas.allocation.model.issue.IssueUndoData;
import cn.nome.saas.allocation.repository.entity.allocation.IssueNeedStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.MidCategoryStockDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueInStockDO;
import cn.nome.saas.allocation.repository.entity.vertical.IssueOutStockDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * IssueDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public interface IssueDOMapper {

    Integer addIssueInStock(@Param("list") List<IssueInStock> list,@Param("tableName")String tableName);

    void addIssueOutStock(@Param("list") List<IssueOutStock> list);

    List<MidCategoryStockDO> getMidCategorySale(@Param("taskId") int taskId,@Param("tableName")String tableName);

    List<Map<String,Object>> getShopAvg(@Param("taskId") int taskId,@Param("tableName")String tableName);

    List<MidCategoryStockDO> getMidMidCategoryQty(@Param("taskId") int taskId, @Param("shopIds") List<String> shopIds, @Param("tabName") String tabName,@Param("tableName")String tableName);

    void midMidCategoryQty(List<MidCategoryStockDO> list);

    List<IssueNeedStockDO> getNeedSkuStock(@Param("taskId") int taskId, @Param("shopId") String shopId, @Param("tabName") String tabName, @Param("tableName")String tableName);

    void addNeedSkuStock(@Param("list")List<IssueNeedStockDO> list);

    List<IssueOutStock> getStockSku(@Param("taskId") int taskId);


    void addEnoughStockSku(@Param("taskId") int taskId, @Param("matCode") String matCode,
                           @Param("sizeId") String sizeId);

    void addNotEnoughStockSku(@Param("taskId") int taskId, @Param("matCode") String matCode,
                              @Param("sizeId") String sizeId, @Param("stockQty") long stockQty);

    List<IssueUndoData> getIssueUndoData(@Param("taskId") int taskId, @Param("shopId") String shopId,@Param("tableName")String tableName);

    void createNewIssueInStock(@Param("tableName")String tableName);

    //void createNewIssueDetail(String tableName);

}
