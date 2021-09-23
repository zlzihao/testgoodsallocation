package cn.nome.saas.allocation.feign.model;


import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="后台类目")
public class CategoriesVO extends ToString {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="")
	private Integer id;
	
	@ApiModelProperty(value="企业ID")
	private Integer corpId;

	@ApiModelProperty(value="父级分类id")
	private Integer parentId;

	@ApiModelProperty(value="类目简写")
	private String abbreviation;

	@ApiModelProperty(value="排序，默认为0，数字越大越靠前")
	private Integer sortOrder;

	@ApiModelProperty(value="是否启用，1-启用，0-停用")
	private Integer isEnable;

	@ApiModelProperty(value="创建时间")
	private Date createdAt;

	@ApiModelProperty(value="更新时间")
	private Date updatedAt;

	@ApiModelProperty(value="是否删除，1-已删除，0-未删除")
	private Integer isDeleted;

	private String cnName;

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}

	public Integer getCorpId() {
		return corpId;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getParentId() {
		return parentId;
	}
	
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
	
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}
	
	public void setIsEnable(Integer isEnable) {
		this.isEnable = isEnable;
	}

	public Integer getIsEnable() {
		return isEnable;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}
	
}
