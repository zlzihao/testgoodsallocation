package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author bare
 *
 */
public class Stock extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private Date OperationDate;
	private int TaskId;
	private String InShopID;
	private String ShopID;
	private String StockID;
	private String MatCode;
	private String SizeID;
	private String SizeName;

	private String MatName;
	private String CategoryName;
	private String CategoryCode;
	private String MidCategoryName;
	private String MidCategoryCode;
	private String SmallCategoryName;

	private Long AvgSaleAmtRank;

	private Double AvgSaleQty;
	// 可用库存
	private Double UsableStockQty;
	// 需求库存
	private Double NeedStockQty;
	private Double AvgSaleAmt;

	private Long StockQty;
	private Long PathStockQty = 0l;
	private Long MoveQty = 0l;
	private Long TotalStockQty = 0l;

	private Long WarehouseStockQty;

	private int UsableStockQtyInt;
	private int NeedStockQtyInt;
	private int RawNeedStockQtyInt;
	// 价格
	private Double QuotePrice;
	private Double Amt;

	// 是否齐码商品
	private int IsComplement;
	private int IsNew;
	
	private int IsProhibited =0;
	private String SeasonName;
	private String YearNo;
	
	//剩余码数
	private int ExistSize;
	//当前skc总库存数
	private int SkcQty;
	//总码数
	private int TotalSize;

	/**
	 * 规则名称(禁配, 保底, 白名单)
	 */
	private String ruleName;

	/**
	 * 保底规则策略数量
	 */
	private Integer minQty;

	/**
	 * 货盘等级
	 */
	private String GoodsLevel;
	/**
	 * 28天销量
	 */
	private int SaleQty28;
	/**
	 * 7天销量
	 */
	private int SaleQty7;
	/**
	 * 饱满度目标
	 */
	private BigDecimal DisplayPercent;

	/**
	 * 是否淘汰
	 */
	private String MatTypeName;

	/**
	 * 是否淘汰标志
	 */
	private Integer IsEliminate;

	/**
	 * skc中类占比
	 */
	private BigDecimal PercentCategory;

	public BigDecimal getPercentCategory() {
		return PercentCategory;
	}

	public void setPercentCategory(BigDecimal percentCategory) {
		PercentCategory = percentCategory;
	}

	public int getExistSize() {
		return ExistSize;
	}
	public void setExistSize(int existSize) {
		ExistSize = existSize;
	}
	public int getSkcQty() {
		return SkcQty;
	}
	public void setSkcQty(int skcQty) {
		SkcQty = skcQty;
	}
	public int getTotalSize() {
		return TotalSize;
	}
	public void setTotalSize(int totalSize) {
		TotalSize = totalSize;
	}
	public Long getTotalStockQty() {
		return TotalStockQty;
	}
	public void setTotalStockQty(Long totalStockQty) {
		TotalStockQty = totalStockQty;
	}
	public Date getOperationDate() {
		return OperationDate;
	}
	public void setOperationDate(Date operationDate) {
		OperationDate = operationDate;
	}
	public int getTaskId() {
		return TaskId;
	}
	public void setTaskId(int taskId) {
		TaskId = taskId;
	}
	public String getInShopID() {
		return InShopID;
	}
	public void setInShopID(String inShopID) {
		InShopID = inShopID;
	}
	public String getShopID() {
		return ShopID;
	}
	public void setShopID(String shopID) {
		ShopID = shopID;
	}
	public String getStockID() {
		return StockID;
	}
	public void setStockID(String stockID) {
		StockID = stockID;
	}
	public String getMatCode() {
		return MatCode;
	}
	public void setMatCode(String matCode) {
		MatCode = matCode;
	}
	public String getSizeID() {
		return SizeID;
	}
	public void setSizeID(String sizeID) {
		SizeID = sizeID;
	}
	public String getSizeName() {
		return SizeName;
	}
	public void setSizeName(String sizeName) {
		SizeName = sizeName;
	}
	public String getMatName() {
		return MatName;
	}
	public void setMatName(String matName) {
		MatName = matName;
	}
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	public String getCategoryCode() {
		return CategoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		CategoryCode = categoryCode;
	}
	public String getMidCategoryName() {
		return MidCategoryName;
	}
	public void setMidCategoryName(String midCategoryName) {
		MidCategoryName = midCategoryName;
	}
	public String getMidCategoryCode() {
		return MidCategoryCode;
	}
	public void setMidCategoryCode(String midCategoryCode) {
		MidCategoryCode = midCategoryCode;
	}
	public String getSmallCategoryName() {
		return SmallCategoryName;
	}
	public void setSmallCategoryName(String smallCategoryName) {
		SmallCategoryName = smallCategoryName;
	}
	public Long getAvgSaleAmtRank() {
		return AvgSaleAmtRank;
	}
	public void setAvgSaleAmtRank(Long avgSaleAmtRank) {
		AvgSaleAmtRank = avgSaleAmtRank;
	}
	public Double getAvgSaleQty() {
		return AvgSaleQty;
	}
	public void setAvgSaleQty(Double avgSaleQty) {
		AvgSaleQty = avgSaleQty;
	}
	public Double getUsableStockQty() {
		return UsableStockQty;
	}
	public void setUsableStockQty(Double usableStockQty) {
		UsableStockQty = usableStockQty;
	}
	public Double getNeedStockQty() {
		return NeedStockQty;
	}
	public void setNeedStockQty(Double needStockQty) {
		NeedStockQty = needStockQty;
	}
	public Double getAvgSaleAmt() {
		return AvgSaleAmt;
	}
	public void setAvgSaleAmt(Double avgSaleAmt) {
		AvgSaleAmt = avgSaleAmt;
	}
	public Long getStockQty() {
		return StockQty;
	}
	public void setStockQty(Long stockQty) {
		StockQty = stockQty;
	}
	public Long getPathStockQty() {
		return PathStockQty;
	}
	public void setPathStockQty(Long pathStockQty) {
		PathStockQty = pathStockQty;
	}
	public Long getMoveQty() {
		return MoveQty;
	}
	public void setMoveQty(Long moveQty) {
		MoveQty = moveQty;
	}
	public Long getWarehouseStockQty() {
		return WarehouseStockQty;
	}
	public void setWarehouseStockQty(Long warehouseStockQty) {
		WarehouseStockQty = warehouseStockQty;
	}
	public int getUsableStockQtyInt() {
		return UsableStockQtyInt;
	}
	public void setUsableStockQtyInt(int usableStockQtyInt) {
		UsableStockQtyInt = usableStockQtyInt;
	}
	public int getNeedStockQtyInt() {
		return NeedStockQtyInt;
	}
	public void setNeedStockQtyInt(int needStockQtyInt) {
		NeedStockQtyInt = needStockQtyInt;
	}
	public int getRawNeedStockQtyInt() {
		return RawNeedStockQtyInt;
	}
	public void setRawNeedStockQtyInt(int rawNeedStockQtyInt) {
		RawNeedStockQtyInt = rawNeedStockQtyInt;
	}
	public Double getQuotePrice() {
		return QuotePrice;
	}
	public void setQuotePrice(Double quotePrice) {
		QuotePrice = quotePrice;
	}
	public Double getAmt() {
		return Amt;
	}
	public void setAmt(Double amt) {
		Amt = amt;
	}
	public int getIsComplement() {
		return IsComplement;
	}
	public void setIsComplement(int isComplement) {
		IsComplement = isComplement;
	}
	public int getIsNew() {
		return IsNew;
	}
	public void setIsNew(int isNew) {
		IsNew = isNew;
	}
	public int getIsProhibited() {
		return IsProhibited;
	}
	public void setIsProhibited(int isProhibited) {
		IsProhibited = isProhibited;
	}
	public String getSeasonName() {
		return SeasonName;
	}
	public void setSeasonName(String seasonName) {
		SeasonName = seasonName;
	}
	public String getYearNo() {
		return YearNo;
	}
	public void setYearNo(String yearNo) {
		YearNo = yearNo;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Integer getMinQty() {
		return minQty;
	}

	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}

	public String getGoodsLevel() {
		return GoodsLevel;
	}

	public void setGoodsLevel(String goodsLevel) {
		GoodsLevel = goodsLevel;
	}

	public int getSaleQty28() {
		return SaleQty28;
	}

	public void setSaleQty28(int saleQty28) {
		SaleQty28 = saleQty28;
	}

	public int getSaleQty7() {
		return SaleQty7;
	}

	public void setSaleQty7(int saleQty7) {
		SaleQty7 = saleQty7;
	}

	public BigDecimal getDisplayPercent() {
		return DisplayPercent;
	}

	public void setDisplayPercent(BigDecimal displayPercent) {
		DisplayPercent = displayPercent;
	}

	public String getMatTypeName() {
		return MatTypeName;
	}

	public void setMatTypeName(String matTypeName) {
		MatTypeName = matTypeName;
	}

	public Integer getIsEliminate() {
		return IsEliminate;
	}

	public void setIsEliminate(Integer isEliminate) {
		IsEliminate = isEliminate;
	}
}