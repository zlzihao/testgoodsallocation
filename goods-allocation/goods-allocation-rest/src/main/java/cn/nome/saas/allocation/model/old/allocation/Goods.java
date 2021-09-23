package cn.nome.saas.allocation.model.old.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class Goods extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;

	private String MatCode;
	private Double QuotePrice;
	private String SeasonName;
	private String YearNo;
	private String CategoryName;
	private String MidCategoryName;
	private String SmallCategoryName;
	
	
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
	public String getMatCode() {
		return MatCode;
	}
	public void setMatCode(String matCode) {
		MatCode = matCode;
	}
	public Double getQuotePrice() {
		return QuotePrice;
	}
	public void setQuotePrice(Double quotePrice) {
		QuotePrice = quotePrice;
	}
	
	

}