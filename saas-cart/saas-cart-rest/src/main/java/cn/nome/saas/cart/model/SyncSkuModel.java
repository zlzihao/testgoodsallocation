package cn.nome.saas.cart.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author chentaikuang
 */
@ApiModel("同步Cookie SKU实体")
public class SyncSkuModel {

	@NotNull(message = "数量不可空")
	@Min(value = 1,message = "数量最小为1")
	@ApiModelProperty(value = "数量", required = true)
	private int count;

	@NotEmpty(message = "skuCode不可空")
	@ApiModelProperty(value = "skuCode", required = true)
	private String skuCode;

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
