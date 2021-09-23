package cn.nome.saas.cart.model;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author chentaikuang
 */
@ApiModel(value = "商品id信息")
public class CalcSku implements Serializable{

	private static final long serialVersionUID = 4752680595087773148L;

	@NotNull(message = "商品ID不可空")
	@Min(value = 1,message = "商品ID异常")
	private int productId;

	@NotEmpty(message = "skuCode不可空")
	private String skuCode;

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

}
