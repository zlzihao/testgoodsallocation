package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

public class TaskStoreCommodity extends ToString {

	private static final long serialVersionUID = -3171660219195919830L;

	private String commodity_id;
	private String commodity_name;
	private String size_id;
	private String size_name;
	private int commodity_num;
	private String inshop_id;
	private String inshop_code;
	private String inshop_name;
	private String outshop_id;
	private String outshop_code;
	private String outshop_name;

	private String inCityName;
	private String inProvinceName;
	private String outCityName;
	private String outProvinceName;
	private String categoryName;
	private String midCategoryName;
	private String smallCategoryName;
	private String seasonName;
	private String yearNo;

	private double inAvgSaleAmt;
	private double inAvgSaleQty;
	private double inStockQty;
	private double inMoveQty;
	private double inPathStockQty;
	private double inNeedStockQty;
	private int inIsComplement;

	private double outAvgSaleAmt;
	private double outAvgSaleQty;
	private double outStockQty;

	
	
	public double getInNeedStockQty() {
		return inNeedStockQty;
	}

	public void setInNeedStockQty(double inNeedStockQty) {
		this.inNeedStockQty = inNeedStockQty;
	}

	public String getInCityName() {
		return inCityName;
	}

	public void setInCityName(String inCityName) {
		this.inCityName = inCityName;
	}

	public String getInProvinceName() {
		return inProvinceName;
	}

	public void setInProvinceName(String inProvinceName) {
		this.inProvinceName = inProvinceName;
	}

	public String getOutCityName() {
		return outCityName;
	}

	public void setOutCityName(String outCityName) {
		this.outCityName = outCityName;
	}

	public String getOutProvinceName() {
		return outProvinceName;
	}

	public void setOutProvinceName(String outProvinceName) {
		this.outProvinceName = outProvinceName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getMidCategoryName() {
		return midCategoryName;
	}

	public void setMidCategoryName(String midCategoryName) {
		this.midCategoryName = midCategoryName;
	}

	public String getSmallCategoryName() {
		return smallCategoryName;
	}

	public void setSmallCategoryName(String smallCategoryName) {
		this.smallCategoryName = smallCategoryName;
	}

	public String getSeasonName() {
		return seasonName;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

	public String getYearNo() {
		return yearNo;
	}

	public void setYearNo(String yearNo) {
		this.yearNo = yearNo;
	}

	public double getInAvgSaleAmt() {
		return inAvgSaleAmt;
	}

	public void setInAvgSaleAmt(double inAvgSaleAmt) {
		this.inAvgSaleAmt = inAvgSaleAmt;
	}

	public double getInAvgSaleQty() {
		return inAvgSaleQty;
	}

	public void setInAvgSaleQty(double inAvgSaleQty) {
		this.inAvgSaleQty = inAvgSaleQty;
	}

	public double getInStockQty() {
		return inStockQty;
	}

	public void setInStockQty(double inStockQty) {
		this.inStockQty = inStockQty;
	}

	public double getInMoveQty() {
		return inMoveQty;
	}

	public void setInMoveQty(double inMoveQty) {
		this.inMoveQty = inMoveQty;
	}

	public double getInPathStockQty() {
		return inPathStockQty;
	}

	public void setInPathStockQty(double inPathStockQty) {
		this.inPathStockQty = inPathStockQty;
	}

	public int getInIsComplement() {
		return inIsComplement;
	}

	public void setInIsComplement(int inIsComplement) {
		this.inIsComplement = inIsComplement;
	}

	public double getOutAvgSaleAmt() {
		return outAvgSaleAmt;
	}

	public void setOutAvgSaleAmt(double outAvgSaleAmt) {
		this.outAvgSaleAmt = outAvgSaleAmt;
	}

	public double getOutAvgSaleQty() {
		return outAvgSaleQty;
	}

	public void setOutAvgSaleQty(double outAvgSaleQty) {
		this.outAvgSaleQty = outAvgSaleQty;
	}

	public double getOutStockQty() {
		return outStockQty;
	}

	public void setOutStockQty(double outStockQty) {
		this.outStockQty = outStockQty;
	}

	public String getInshop_code() {
		return inshop_code;
	}

	public void setInshop_code(String inshop_code) {
		this.inshop_code = inshop_code;
	}

	public String getOutshop_code() {
		return outshop_code;
	}

	public void setOutshop_code(String outshop_code) {
		this.outshop_code = outshop_code;
	}

	public String getSize_name() {
		return size_name;
	}

	public void setSize_name(String size_name) {
		this.size_name = size_name;
	}

	public String getSize_id() {
		return size_id;
	}

	public void setSize_id(String size_id) {
		this.size_id = size_id;
	}

	public String getCommodity_id() {
		return commodity_id;
	}

	public void setCommodity_id(String commodity_id) {
		this.commodity_id = commodity_id;
	}

	public String getCommodity_name() {
		return commodity_name;
	}

	public void setCommodity_name(String commodity_name) {
		this.commodity_name = commodity_name;
	}

	public int getCommodity_num() {
		return commodity_num;
	}

	public void setCommodity_num(int commodity_num) {
		this.commodity_num = commodity_num;
	}

	public String getInshop_id() {
		return inshop_id;
	}

	public void setInshop_id(String inshop_id) {
		this.inshop_id = inshop_id;
	}

	public String getInshop_name() {
		return inshop_name;
	}

	public void setInshop_name(String inshop_name) {
		this.inshop_name = inshop_name;
	}

	public String getOutshop_id() {
		return outshop_id;
	}

	public void setOutshop_id(String outshop_id) {
		this.outshop_id = outshop_id;
	}

	public String getOutshop_name() {
		return outshop_name;
	}

	public void setOutshop_name(String outshop_name) {
		this.outshop_name = outshop_name;
	}
}