package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class AllocationDetail extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private String InShopID;
	private String ShopID;
	private String MatCode;
	private String SizeID;
	private int TaskId;
	private int Qty;
	// 商品
	private Double QuotePrice;
	private String CategoryName;
	private String MidCategoryName;
	private String SmallCategoryName;
	private String SeasonName;
	private String YearNo;
	private int InIsComplement;
	
	//调出
	private double OutAvgSaleAmt;
	private double OutAvgSaleQty;
	private double OutStockQty;
	
	//调入
	private double InAvgSaleAmt;
	private double InAvgSaleQty;
	private double InStockQty;
	private double InMoveQty;
	private double InPathStockQty;
	private double InNeedStockQty;
	
	
	public double getInNeedStockQty() {
		return InNeedStockQty;
	}

	public void setInNeedStockQty(double inNeedStockQty) {
		InNeedStockQty = inNeedStockQty;
	}

	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}

	public String getMidCategoryName() {
		return MidCategoryName;
	}

	public void setMidCategoryName(String midCategoryName) {
		MidCategoryName = midCategoryName;
	}

	public String getSmallCategoryName() {
		return SmallCategoryName;
	}

	public void setSmallCategoryName(String smallCategoryName) {
		SmallCategoryName = smallCategoryName;
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

	public int getInIsComplement() {
		return InIsComplement;
	}

	public void setInIsComplement(int inIsComplement) {
		InIsComplement = inIsComplement;
	}

	public double getOutAvgSaleAmt() {
		return OutAvgSaleAmt;
	}

	public void setOutAvgSaleAmt(double outAvgSaleAmt) {
		OutAvgSaleAmt = outAvgSaleAmt;
	}

	public double getOutAvgSaleQty() {
		return OutAvgSaleQty;
	}

	public void setOutAvgSaleQty(double outAvgSaleQty) {
		OutAvgSaleQty = outAvgSaleQty;
	}

	public double getOutStockQty() {
		return OutStockQty;
	}

	public void setOutStockQty(double outStockQty) {
		OutStockQty = outStockQty;
	}

	public double getInAvgSaleAmt() {
		return InAvgSaleAmt;
	}

	public void setInAvgSaleAmt(double inAvgSaleAmt) {
		InAvgSaleAmt = inAvgSaleAmt;
	}

	public double getInAvgSaleQty() {
		return InAvgSaleQty;
	}

	public void setInAvgSaleQty(double inAvgSaleQty) {
		InAvgSaleQty = inAvgSaleQty;
	}

	public double getInStockQty() {
		return InStockQty;
	}

	public void setInStockQty(double inStockQty) {
		InStockQty = inStockQty;
	}

	public double getInMoveQty() {
		return InMoveQty;
	}

	public void setInMoveQty(double inMoveQty) {
		InMoveQty = inMoveQty;
	}

	public double getInPathStockQty() {
		return InPathStockQty;
	}

	public void setInPathStockQty(double inPathStockQty) {
		InPathStockQty = inPathStockQty;
	}

	public Double getQuotePrice() {
		return QuotePrice;
	}

	public void setQuotePrice(Double quotePrice) {
		QuotePrice = quotePrice;
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

	public int getQty() {
		return Qty;
	}

	public void setQty(int qty) {
		Qty = qty;
	}

}