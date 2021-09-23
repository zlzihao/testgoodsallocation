package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class StockStat extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private String inshopid;
	private String ShopID;
	private String ShopName;
	//需求库存总数
	private int NeedStockQtyInt;
	private int UsableStockQty;
	
	//贡献金额
	private double ContributeAmt;
	//总金额
	private double TotalAmt;
	
	private int Sku;
	private int OutShopidQty;
	
	private int AvgSaleQty;
	private double AvgSaleAmt;

	// 物流信息
	private ExpressInfo expressInfo;

	
	public int getAvgSaleQty() {
		return AvgSaleQty;
	}
	public void setAvgSaleQty(int avgSaleQty) {
		AvgSaleQty = avgSaleQty;
	}
	public double getAvgSaleAmt() {
		return AvgSaleAmt;
	}
	public void setAvgSaleAmt(double avgSaleAmt) {
		AvgSaleAmt = avgSaleAmt;
	}
	public String getShopName() {
		return ShopName;
	}
	public void setShopName(String shopName) {
		ShopName = shopName;
	}
	public int getSku() {
		return Sku;
	}
	public void setSku(int sku) {
		Sku = sku;
	}
	public int getOutShopidQty() {
		return OutShopidQty;
	}
	public void setOutShopidQty(int outShopidQty) {
		OutShopidQty = outShopidQty;
	}
	public double getContributeAmt() {
		return ContributeAmt;
	}
	public void setContributeAmt(double contributeAmt) {
		ContributeAmt = contributeAmt;
	}
	public String getShopID() {
		return ShopID;
	}
	public void setShopID(String shopID) {
		ShopID = shopID;
	}
	public int getNeedStockQtyInt() {
		return NeedStockQtyInt;
	}
	public void setNeedStockQtyInt(int needStockQtyInt) {
		NeedStockQtyInt = needStockQtyInt;
	}
	public int getUsableStockQty() {
		return UsableStockQty;
	}
	public void setUsableStockQty(int usableStockQty) {
		UsableStockQty = usableStockQty;
	}
	public double getTotalAmt() {
		return TotalAmt;
	}
	public void setTotalAmt(double totalAmt) {
		TotalAmt = totalAmt;
	}


	public ExpressInfo getExpressInfo() {
		return expressInfo;
	}

	public void setExpressInfo(ExpressInfo expressInfo) {
		this.expressInfo = expressInfo;
	}

	public String getInshopid() {
		return inshopid;
	}

	public void setInshopid(String inshopid) {
		this.inshopid = inshopid;
	}

	public int calcExpressSort(int period) {
		if (expressInfo == null) {
			return (int)ContributeAmt;
		}
		// 贡献金额 - 快递费用 - （运输天数 * 贡献金额/ 调拨周期）
		return (int)((Double)ContributeAmt - expressInfo.getShippingFree() - (expressInfo.getShippingDays() * ContributeAmt / period));

	}
}