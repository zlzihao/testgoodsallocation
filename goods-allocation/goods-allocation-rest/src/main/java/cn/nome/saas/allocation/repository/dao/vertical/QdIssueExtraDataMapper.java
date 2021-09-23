package cn.nome.saas.allocation.repository.dao.vertical;

import cn.nome.saas.allocation.repository.entity.allocation.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueExtraDataMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
public interface QdIssueExtraDataMapper {

    List<QdIssueInStockDO> getQdIssueInStockList(@Param("matCodeList")List<String> skcList,@Param("shopIdList")List<String> shopIdList);

    List<QdIssueInStockDO> getQdApplyQtyList(@Param("matCodeList")List<String> skcList);

    List<QdIssueOutStockDO> getQdIssueOutStockList(@Param("matCodeList")List<String> skcList);

    List<QdGoodsInfoDO> getGoodsInfo(@Param("matCodeList")List<String> skcList);

    //List<QdIssueSkcStockDO> getMidCategorySalesSkcList();

    //List<QdIssueSkcStockDO> getMidCategoryPathSkcList();

    //List<QdIssueSkcStockDO> getMidCategoryApplySkcList();

    List<QdIssueSkcStockDO> getMidCategorySalesList(@Param("seasonList")List<String> seasonList);

    List<QdIssueSkcStockDO> getMidCategoryPathList(@Param("seasonList")List<String> seasonList);

    List<QdIssueSkcStockDO> getMidCategoryApplyList(@Param("seasonList")List<String> seasonList);

    List<QdIssueNewSkcStockDO> getShopSalesList(@Param("seasonList")List<String> seasonList);

    List<QdIssueNewSkcStockDO> getShopPathList(@Param("seasonList")List<String> seasonList);

    List<QdIssueNewSkcStockDO> getShopApplyList(@Param("seasonList")List<String> seasonList);
}
