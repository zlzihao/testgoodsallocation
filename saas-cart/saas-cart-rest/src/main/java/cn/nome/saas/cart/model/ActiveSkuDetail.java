package cn.nome.saas.cart.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author chentaikuang
 */
public class ActiveSkuDetail implements Serializable{
	private static final long serialVersionUID = -8893688163459621547L;
	private int condition;// 减n
	private int productSetId;
	private List<String> skuCodes;
	private String activeTips;
	private double discAmount;//活动优惠金额

	public double getDiscAmount() {
		return discAmount;
	}

	public void setDiscAmount(double discAmount) {
		this.discAmount = discAmount;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public int getProductSetId() {
		return productSetId;
	}

	public void setProductSetId(int productSetId) {
		this.productSetId = productSetId;
	}

	public String getActiveTips() {
		return activeTips;
	}

	public void setActiveTips(String activeTips) {
		this.activeTips = activeTips;
	}

	public List<String> getSkuCodes() {
		return skuCodes;
	}

	public void setSkuCodes(List<String> skuCodes) {
		this.skuCodes = skuCodes;
	}
}
