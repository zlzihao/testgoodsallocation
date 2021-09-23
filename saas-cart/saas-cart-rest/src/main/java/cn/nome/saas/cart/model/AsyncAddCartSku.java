package cn.nome.saas.cart.model;

public class AsyncAddCartSku extends BaseModel {

	private static final long serialVersionUID = -91744668553172442L;
	private Integer uid;
	private CartSkuModel cartSkuModel;

	public AsyncAddCartSku(Integer uid, Integer appId, Integer corpId, CartSkuModel cartSkuModel) {
		this.uid = uid;
		this.setAppId(appId);
		this.setCorpId(corpId);
		this.cartSkuModel = cartSkuModel;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public CartSkuModel getCartSkuModel() {
		return cartSkuModel;
	}

	public void setCartSkuModel(CartSkuModel cartSkuModel) {
		this.cartSkuModel = cartSkuModel;
	}

}
