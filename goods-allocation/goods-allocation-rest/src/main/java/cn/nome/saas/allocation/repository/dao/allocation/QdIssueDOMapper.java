package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
public interface QdIssueDOMapper {

    Integer batchInsertOutStock(@Param("list") List<QdIssueOutStockDO> qdIssueOutStockDOList);

    Integer batchInsertInStock(@Param("list") List<QdIssueInStockDO> qdIssueInStockDOList);


    Integer batchInsertSkcStock(@Param("list") List<QdIssueSkcStockDO> list);

    Integer batchInsertNewSkcStock(@Param("list") List<QdIssueNewSkcStockDO> list);

    List<QdIssueInStockDO> getInStockData(@Param("taskId")int taskId,@Param("shopIdList")List<String> shopIdList,@Param("matCodeList")List<String> matCodeList);

    Integer batchUpdateInStockRequirement(@Param("list")List<QdIssueInStockDO> list);

    Integer UpdateInStockRequirement(@Param("bean")QdIssueInStockDO qdIssueInStockDO);

    List<QdIssueInStockDO> getInStockQty(@Param("taskId")int taskId,@Param("matCode")String matCode);

    List<QdIssueOutStockDO> getOutStockData(@Param("taskId")int taskId);

    Integer deleteInStock(@Param("taskId")int taskId);

    Integer deleteOutStock(@Param("taskId")int taskId);

    Integer deleteSkcStock(@Param("taskId")int taskId);

    Integer deleteNewSkcStock(@Param("taskId")int taskId);

    List<QdIssueSkcStockDO> getSkcStockList(@Param("taskId")int taskId);

    List<QdIssueNewSkcStockDO> getNewSkcStockList(@Param("taskId")int taskId);

    Integer batchUpdateSkcIssueSkc(@Param("list") List<QdIssueSkcStockDO> list);

    Integer batchUpdateNewSkcIssueSkc(@Param("list") List<QdIssueNewSkcStockDO> list);


    Integer updateOutStockIssueQty(@Param("taskId")int taskId,@Param("matCode")String matCode,@Param("sizeId")String sizeId,@Param("qty")long qty);

    Integer batchUpdateOutStockIssueQty(@Param("list") List<QdIssueOutStockDO> list);

    List<QdIssueInStockDO> getNewInStockData(@Param("taskId")int taskId,@Param("shopIdList")List<String> shopIdList);

    List<QdIssueSkcStockDO> getNewGoodsHadIssueSkc(@Param("taskId")int taskId);

}
