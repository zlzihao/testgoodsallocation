package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueCategoryStructureDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDepthSuggestDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueShopListDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public interface QdIssueCategoryStructureDOMapper {

    List<QdIssueCategoryStructureDO> getQdIssueCategoryStructureDOList();

    List<QdIssueCategoryStructureDO> queryByIdDeal(@Param("isDeal") Integer isDeal, @Param("limitNum") int limitNum);

    List<QdIssueCategoryStructureDO> queryAllByIsDeal(@Param("isDeal") Integer isDeal);

    void batchUpdateInfo(@Param("list") List<QdIssueCategoryStructureDO> qdIssueDepthSuggestDOList);

    int countForDealStatus(Integer isDeal);

    List<QdIssueCategoryStructureDO> queryHaveDealData(@Param("reginName") String regionName,@Param("categoryName")String categoryName);
}
