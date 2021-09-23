package cn.nome.saas.cart.model;

/**
 * @author chentaikuang
 * 购物车sku数据
 */
public class CartSkuModel implements Comparable<CartSkuModel> {

	private int skuId;
	private String skuCode;
	private int productId;
	private String name;
	private String specVal;
	private int price;
	private int store;
	private int count;
	/**
	 * 导购员
	 */
	private Integer seller;

	// 删除、上下架、库存不足
	private int status;
	private String imgUrl;

	private long addTime;
	private long refreshTime;
	private int discPrice;

    @Override
    public int compareTo(CartSkuModel o) {
        long time = o.getAddTime() - this.getAddTime();
        return time > 0 ? 1 : -1;
    }

	public Integer getSeller() {
		return seller;
	}

	public void setSeller(Integer seller) {
		this.seller = seller;
	}

	public int getSkuId() {
		return skuId;
	}

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecVal() {
        return specVal;
    }

    public void setSpecVal(String specVal) {
        this.specVal = specVal;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

	public int getStore() {
		return store;
	}

    public void setStore(int store) {
        this.store = store;
    }

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getImgUrl() {
		return imgUrl;
	}

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public int getDiscPrice() {
		return discPrice;
	}

	public void setDiscPrice(int discPrice) {
		this.discPrice = discPrice;
	}
}
