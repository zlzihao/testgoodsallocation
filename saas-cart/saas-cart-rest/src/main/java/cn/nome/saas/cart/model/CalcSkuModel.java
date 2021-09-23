package cn.nome.saas.cart.model;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author chentaikuang
 */
@ApiModel(value ="结算sku实体")
public class CalcSkuModel extends CalcSku implements Serializable {
	private static final long serialVersionUID = -3748677421646786940L;

	@NotNull(message = "数量不可空")
	@Min(value = 1,message = "数量最小为1")
	private int count;

	public CalcSkuModel() {
		super();
	}

	public CalcSkuModel(int count,int productId) {
		this.count = count;
		this.setProductId(productId);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
