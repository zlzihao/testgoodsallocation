package cn.nome.saas.cart.model;

import java.util.List;

/**
 * 封装购物车返回数据
 * @author chentaikuang
 */
public class CartWrap {
	// 有效
	private List<ValidSkuModel> validSkus;
	// 无效
	private List<CartSkuModel> invalidSkus;
	// 缺少库存
	private List<CartSkuModel> lessStockSkus;

	public List<ValidSkuModel> getValidSkus() {
		return validSkus;
	}

	public void setValidSkus(List<ValidSkuModel> validSkus) {
		this.validSkus = validSkus;
	}

	public List<CartSkuModel> getInvalidSkus() {
		return invalidSkus;
	}

	public void setInvalidSkus(List<CartSkuModel> invalidSkus) {
		this.invalidSkus = invalidSkus;
	}

	public List<CartSkuModel> getLessStockSkus() {
		return lessStockSkus;
	}

	public void setLessStockSkus(List<CartSkuModel> lessStockSkus) {
		this.lessStockSkus = lessStockSkus;
	}

}
