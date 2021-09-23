package cn.nome.saas.cart.feign;

public class SkuCodeReq {

    private String skuCode;
    private int productId;
    private int count;
    private int price;

    public SkuCodeReq(String skuCode, int productId, int count, int price) {
        this.skuCode = skuCode;
        this.productId = productId;
        this.count = count;
        this.price = price;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
