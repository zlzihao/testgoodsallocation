package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author bare
 *
 */
public class NewIssueExtraStockSkuDo extends ToString {

	/**
	 * 仓库编码
	 */
	private String stockId;

	/**
	 *
	 */
	private String matCode;

	/**
	 *
	 */
	private String sizeId;

	/**
	 * 仓库库存量
	 */
	private Long stockQty;

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getMatCode() {
		return matCode;
	}

	public void setMatCode(String matCode) {
		this.matCode = matCode;
	}

	public String getSizeId() {
		return sizeId;
	}

	public void setSizeId(String sizeId) {
		this.sizeId = sizeId;
	}

	public Long getStockQty() {
		return stockQty;
	}

	public void setStockQty(Long stockQty) {
		this.stockQty = stockQty;
	}
}