package cn.nome.saas.allocation.model.old.issue;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * 
 * @author bare
 *
 */
public class IssueDetail extends ToString {
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
	private String MidCategoryName;
	private String SmallCategoryName;
	
	private Long AvgSaleAmtRank;

	private Double AvgSaleQty;
	// 可用库存
	private Double UsableStockQty;
	// 需求库存
	private Double NeedStockQty;
	private Double AvgSaleAmt;

	private Long StockQty;
	private Long PathStockQty;

	private Long WarehouseStockQty;

	private int UsableStockQtyInt;
	private int NeedStockQtyInt;
	// 价格
	private Double QuotePrice;
	private Double Amt;
	
	
	public String getStockID() {
		return StockID;
	}

	public void setStockID(String stockID) {
		StockID = stockID;
	}

	public String getSmallCategoryName() {
		return SmallCategoryName;
	}

	public void setSmallCategoryName(String smallCategoryName) {
		SmallCategoryName = smallCategoryName;
	}

	public Double getAmt() {
		return Amt;
	}

	public void setAmt(Double amt) {
		Amt = amt;
	}

	public int getTaskId() {
		return TaskId;
	}

	public void setTaskId(int taskId) {
		TaskId = taskId;
	}

	public Double getQuotePrice() {
		return QuotePrice;
	}

	public void setQuotePrice(Double quotePrice) {
		QuotePrice = quotePrice;
	}

	public Double getNeedStockQty() {
		return NeedStockQty;
	}

	public void setNeedStockQty(Double needStockQty) {
		NeedStockQty = needStockQty;
	}

	public Date getOperationDate() {
		return OperationDate;
	}

	public void setOperationDate(Date operationDate) {
		OperationDate = operationDate;
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

	public String getSizeName() {
		return SizeName;
	}

	public void setSizeName(String sizeName) {
		SizeName = sizeName;
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

	public String getMidCategoryName() {
		return MidCategoryName;
	}

	public void setMidCategoryName(String midCategoryName) {
		MidCategoryName = midCategoryName;
	}
	
	

}