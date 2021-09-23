package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDepthSuggestDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueShopListDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public interface QdIssueDepthSuggestDOMapper {
    int countForDealStatus(Integer isDeal);

    List<QdIssueDepthSuggestDO> queryAll();

    void batchUpdateInfo(@Param("list") List<QdIssueDepthSuggestDO> qdIssueDepthSuggestDOList);

    List<QdIssueDepthSuggestDO> queryAvgDepth();
}
