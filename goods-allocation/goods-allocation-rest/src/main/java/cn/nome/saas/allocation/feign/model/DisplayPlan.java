package cn.nome.saas.allocation.feign.model;


import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="")
public class DisplayPlan extends ToString {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="")
	private Integer id;
	
	@ApiModelProperty(value="货号")
	private String goodsNo;

	@ApiModelProperty(value="货品名称")
	private String goodsName;

	@ApiModelProperty(value="企划大类")
	private String qhBigCategoryName;

	@ApiModelProperty(value="企划中类")
	private String qhMidCategoryName;

	@ApiModelProperty(value="企划小类")
	private String qhSmallCategoryName;

	@ApiModelProperty(value="企划子类")
	private String qhSubCategoryName;

	@ApiModelProperty(value="'陈列大类'")
	private String clBigCategoryName;

	@ApiModelProperty(value="'陈列中类'")
	private String clMidCategoryName;

	@ApiModelProperty(value="'陈列小类'")
	private String clSmallCategoryName;

	@ApiModelProperty(value="'陈列子类'")
	private String clSubCategoryName;

	@ApiModelProperty(value="首次铺货门店数量")
	private Integer firstDisplayShop;

	@ApiModelProperty(value="首铺最小陈列量")
	private Integer firstDisplayCount;

	@ApiModelProperty(value="0全国上市时间一致 1全国各区上市时间不一致")
	private Integer saleTimeType;

	@ApiModelProperty(value="配货状态 0待选择门店 1 未开始配货 2 首次配货中 3已完成首次配货")
	private Integer displayType;

	@ApiModelProperty(value="'尺码比例'")
	private String sizeScale;

	@ApiModelProperty(value="")
	private String createTime;

	@ApiModelProperty(value="")
	private String updateTime;

	private Integer turnDay;

	private Integer fitsFirstDisplayCount = 0; //首配量

	private List<SaleTimeList> saleTimeList;

	private List<DisplayPlanShop> displayPlanStoreList;

	public String getSizeScale() {
		return sizeScale;
	}

	public void setSizeScale(String sizeScale) {
		this.sizeScale = sizeScale;
	}

	public List<SaleTimeList> getSaleTimeList() {
		return saleTimeList;
	}

	public List<DisplayPlanShop> getDisplayPlanStoreList() {
		return displayPlanStoreList;
	}

	public void setDisplayPlanStoreList(List<DisplayPlanShop> displayPlanStoreList) {
		this.displayPlanStoreList = displayPlanStoreList;
	}

	public Integer getTurnDay() {
		return turnDay;
	}

	public void setTurnDay(Integer turnDay) {
		this.turnDay = turnDay;
	}

	public void setSaleTimeList(List<SaleTimeList> saleTimeList) {
		this.saleTimeList = saleTimeList;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	public void setGoodsNo(String goodsNo) {
		this.goodsNo = goodsNo;
	}

	public String getGoodsNo() {
		return goodsNo;
	}
	
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setFirstDisplayShop(Integer firstDisplayShop) {
		this.firstDisplayShop = firstDisplayShop;
	}

	public Integer getFirstDisplayShop() {
		return firstDisplayShop;
	}
	
	public void setFirstDisplayCount(Integer firstDisplayCount) {
		this.firstDisplayCount = firstDisplayCount;
	}

	public Integer getFirstDisplayCount() {
		return firstDisplayCount;
	}
	
	public void setSaleTimeType(Integer saleTimeType) {
		this.saleTimeType = saleTimeType;
	}

	public Integer getSaleTimeType() {
		return saleTimeType;
	}
	
	public void setDisplayType(Integer displayType) {
		this.displayType = displayType;
	}

	public Integer getDisplayType() {
		return displayType;
	}
	
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateTime() {
		return createTime;
	}
	
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public String getQhBigCategoryName() {
		return qhBigCategoryName;
	}

	public void setQhBigCategoryName(String qhBigCategoryName) {
		this.qhBigCategoryName = qhBigCategoryName;
	}

	public String getQhMidCategoryName() {
		return qhMidCategoryName;
	}

	public void setQhMidCategoryName(String qhMidCategoryName) {
		this.qhMidCategoryName = qhMidCategoryName;
	}

	public String getQhSmallCategoryName() {
		return qhSmallCategoryName;
	}

	public void setQhSmallCategoryName(String qhSmallCategoryName) {
		this.qhSmallCategoryName = qhSmallCategoryName;
	}

	public String getQhSubCategoryName() {
		return qhSubCategoryName;
	}

	public void setQhSubCategoryName(String qhSubCategoryName) {
		this.qhSubCategoryName = qhSubCategoryName;
	}

	public String getClBigCategoryName() {
		return clBigCategoryName;
	}

	public void setClBigCategoryName(String clBigCategoryName) {
		this.clBigCategoryName = clBigCategoryName;
	}

	public String getClMidCategoryName() {
		return clMidCategoryName;
	}

	public void setClMidCategoryName(String clMidCategoryName) {
		this.clMidCategoryName = clMidCategoryName;
	}

	public String getClSmallCategoryName() {
		return clSmallCategoryName;
	}

	public void setClSmallCategoryName(String clSmallCategoryName) {
		this.clSmallCategoryName = clSmallCategoryName;
	}

	public String getClSubCategoryName() {
		return clSubCategoryName;
	}

	public void setClSubCategoryName(String clSubCategoryName) {
		this.clSubCategoryName = clSubCategoryName;
	}

	public Integer getFitsFirstDisplayCount() {
		return fitsFirstDisplayCount;
	}

	public void setFitsFirstDisplayCount(Integer fitsFirstDisplayCount) {
		this.fitsFirstDisplayCount = fitsFirstDisplayCount;
	}
}
