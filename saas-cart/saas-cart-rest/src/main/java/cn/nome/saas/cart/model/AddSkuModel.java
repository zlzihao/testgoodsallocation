package cn.nome.saas.cart.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author chentaikuang
 */
@ApiModel("添加购物车SKU实体")
public class AddSkuModel {

	@ApiModelProperty(value = "数量", required = true)
	@NotNull(message = "数量不可空")
	@Min(value = 1,message = "数量最小为1")
	private int count;

	@ApiModelProperty(value = "skuCode", required = true)
	@NotEmpty(message = "skuCode不可空")
	private String skuCode;

	@ApiModelProperty(value = "导购员")
	private Integer seller;

	public Integer getSeller() {
		return seller;
	}

	public void setSeller(Integer seller) {
		this.seller = seller;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
