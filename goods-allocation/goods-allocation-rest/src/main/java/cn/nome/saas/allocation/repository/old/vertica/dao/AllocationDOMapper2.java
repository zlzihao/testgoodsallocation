package cn.nome.saas.allocation.repository.old.vertica.dao;

import cn.nome.saas.allocation.model.old.allocation.*;
import cn.nome.saas.allocation.repository.old.vertica.entity.ShopSizeIdInfoDO;
import cn.nome.saas.allocation.repository.old.vertica.entity.ShopSizeIdQtyInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface AllocationDOMapper2 {
    // 补码
    List<Stock> getComplementList(@Param("period") int period, @Param("inShopSql") String inShopSql);

    List<Stock> getList(@Param("typeSql") String typeSql, @Param("period") int period,
                        @Param("inShopSql") String inShopSql);

    List<Stock> getWithdrawalComplementList(@Param("period") int period, @Param("inShopSql") String inShopSql);

    List<Stock> getWithdrawalList(@Param("typeSql") String typeSql, @Param("period") int period,
                                  @Param("inShopSql") String inShopSql);

//	List<Stock> getIssueList();

    List<Stock> getList(@Param("typeSql") String typeSql, @Param("period") int period);

    List<Stock> getIssueInStockList(@Param("typeSql") String typeSql, @Param("period") int period,@Param("shopSql") String shopSql);

    List<Stock> getIssueOutStockList();

    /**
     * 获取同城下所有门店列表（不包含它本身）
     *
     * @param shopId
     * @return
     */
    List<String> getCityShop(@Param(value = "shopId") String shopId);

    List<Stock> getOutStockList(@Param("period") int period, @Param(value = "shopIdList") List<String> shopIdList,
                                @Param(value = "stockList") List<Stock> stockList);

    List<Stock> getWithdrawalOutStockList(@Param("period") int period, @Param(value = "shopIdList") List<String> shopIdList,
                                          @Param(value = "stockList") List<Stock> stockList);

    List<MatSize> getSizeList();

    List<Stock> getNewSkcList(@Param("shopSql") String shopSql);

//	List<Stock> getMidCategorySaleAmt();

//	@Deprecated
//	List<StockYesterday> getStockYesterday(@Param(value = "shopId") String shopId);

    List<SkuSaleStock> getSaleDay28(@Param(value = "shopId") String shopId);

    List<SkuSaleStock> getSaleDay7(@Param(value = "shopId") String shopId);

    List<ShopSizeIdInfoDO> getSizeIdInfoList(@Param(value = "shopIdList") Set<String> shopIdList,
                                             @Param(value = "matCodeList") Set<String> matCodeList,
                                             @Param(value = "minLatch") int minLatch,
                                             @Param(value = "maxLatch") int maxLatch);

    List<ShopSizeIdQtyInfoDO> getSizeIdQtyList(@Param(value = "shopIdList") Set<String> shopIdList,
                                               @Param(value = "matCodeList") Set<String> matCodeList);

    List<ShopSizeIdQtyInfoDO> getSizeIdQtyListByRemainderQty(@Param(value = "shopIdList") Set<String> shopIdList,
                                                                                                           @Param(value = "matCodeList") Set<String> matCodeList,
                                                                                                           @Param(value = "minLatch") int minLatch,
                                                                                                           @Param(value = "maxLatch") int maxLatch);

    List<ShopStockYesterday> getShopStockYesterday(@Param(value = "shopId") String shopId);

//    List<MatBarCodeImg> batchLastMatBarCodes(@Param(value = "batchSize") int batchSize);

    List<MatBarCodeImg> getMatCodeSizeIdImgs(@Param(value = "param") List<MatcodeSizeId> param);

    List<MatBarCodeImg> loadSkuImg(@Param(value = "batchSize") int batchSize);

//    List<AvgSaleQtyMatCodeSizeId> checkAvgSaleQty60(@Param(value = "shopId") String shopId, @Param(value = "param") Map<String, String> avgSaleQtyMap);
//    List<SaleQtyData> getSale28(@Param(value = "shopId") String shopId, @Param(value = "param") Map<String, String> saleQtyMap);
//    List<SaleQtyData> getSale7(@Param(value = "shopId") String shopId, @Param(value = "param") Map<String, String> saleQtyMap);

    List<SizeCountData> getSizeCount(@Param(value = "shopId") String shopId, @Param(value = "matcodes") Set<String> matcodes);

    List<SizeCountData> getAllSizeCount(@Param(value = "shopIds") Set<String> shopIds);

    List<MatcodeSaleRank> getSkcShopRank(@Param(value = "matcodes") Set<String> matcodes, @Param(value = "shopId") String shopID);

    List<MatcodeSaleRank> getBhSkcNationalRank(@Param(value = "matcodes") Set<String> matcodes, @Param(value = "shopId") String shopID);

    List<MatcodeSaleRank> getAllBhSkcNationalRank();

    List<MatcodeSaleRank> getFzSkcNationalRank(@Param(value = "matcodes") Set<String> matcodes, @Param(value = "shopId") String shopID);

    List<SaleQtyData> getSale28(@Param(value = "shopId") String shopId, @Param(value = "matcodes") Set<String> matcodes, @Param(value = "sizeIds") Set<String> sizeIds);

    List<SaleQtyData> getSale7(@Param(value = "shopId") String shopId, @Param(value = "matcodes") Set<String> matcodes, @Param(value = "sizeIds") Set<String> sizeIds);

//    List<AvgSaleQtyMatCodeSizeId> checkAvgSaleQty60(@Param(value = "shopId") String shopId, @Param(value = "matcodes") Set<String> matcodes, @Param(value = "sizeIds") Set<String> sizeIds);

    List<Stock> getShopIssueInStocks(@Param("typeSql") String typeSql,@Param(value = "shopId") String shopId);

    List<Stock> getRecalcShopNewSkcList(@Param(value = "shopId") String shopId);

    List<SaleQtyMatCodeSizeId> checkSaleQty60(@Param(value = "shopId") String shopId, @Param(value = "matcodes") Set<String> matcodes, @Param(value = "sizeIds") Set<String> sizeIds);
}