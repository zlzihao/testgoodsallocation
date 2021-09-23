package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * 
 * @author bare
 *
 */
public class InStockReq extends ToString {
	private static final long serialVersionUID = -3171660219195919830L;


	private String CityCode;
	private String ShopName;
	private String CategoryCode;
	private String MidCategoryCode;
	private String SmallCategoryCode;
	private Integer TaskId;
	private String InShopId;
	private String ShopId;
	
	
	public String getShopId() {
		return ShopId;
	}
	public void setShopId(String shopId) {
		ShopId = shopId;
	}
	public String getInShopId() {
		return InShopId;
	}
	public void setInShopId(String inShopId) {
		InShopId = inShopId;
	}
	public String getCityCode() {
		return CityCode;
	}
	public void setCityCode(String cityCode) {
		CityCode = cityCode;
	}
	public String getShopName() {
		return ShopName;
	}
	public void setShopName(String shopName) {
		ShopName = shopName;
	}
	public String getCategoryCode() {
		return CategoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		CategoryCode = categoryCode;
	}
	public String getMidCategoryCode() {
		return MidCategoryCode;
	}
	public void setMidCategoryCode(String midCategoryCode) {
		MidCategoryCode = midCategoryCode;
	}
	public String getSmallCategoryCode() {
		return SmallCategoryCode;
	}
	public void setSmallCategoryCode(String smallCategoryCode) {
		SmallCategoryCode = smallCategoryCode;
	}
	public Integer getTaskId() {
		return TaskId;
	}
	public void setTaskId(Integer taskId) {
		TaskId = taskId;
	}
	
	
}