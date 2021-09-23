package cn.nome.saas.allocation.repository.dao.vertical;

import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.repository.entity.vertical.AllocationGoodsSKC;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * QdIssueExtraDataMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/6
 */
public interface AllocationExtraDataMapper {

    public List<OutOfStockGoodsDO> getRejectOutStockList(@Param("shopId")String shopId);

    public List<OutOfStockGoodsDO> getAvaliableSaleQty(@Param("shopIdList")List<String> shopIdList);

    public List<OutOfStockGoodsDO> getAvaliableSaleQtyByParam(@Param("shopIdList")Set<String> shopIdList,@Param("matCodeList")Set<String> matCodeList);

    public List<OutOfStockGoodsDO> getInStockBetweenSeven(@Param("shopIdList")Set<String> shopIdList);

    public List<OutOfStockGoodsDO> getOutStockBetweenFourteen(@Param("shopIdList")Set<String> shopIdList);

    public List<OutOfStockGoodsDO> getShopClothingList(@Param("shopIdList")Set<String> shopIdList,@Param("matCodeList")List<String> matCodeList,@Param("categoryCodeSet") Set<String> categoryCodeSet);

    public List<OutOfStockGoodsDO> getShopClothingApplyList(@Param("shopIdList")Set<String>  shopIdList,@Param("matCodeList")List<String> matCodeList,@Param("categoryCodeSet") Set<String> categoryCodeSet);

    public List<AllocationGoodsSKC> getShopClothingSKC(@Param("shopIdList")Set<String>  shopIdList);

    public List<AllocationGoodsSKC> getShopApplyClothingSKC(@Param("shopIdList")Set<String>  shopIdList);

}
