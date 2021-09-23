package cn.nome.saas.cart.feign;

/**
 * @author chentaikuang
 * 通过接口查得的商品sku表数据
 */
public class SkuModel {

    private int skuId;
    private String skuCode;
    private int productId;
    private String name;
    private String specVal;
    private int price;
    private int store;

    private int productStatus;
    private int skuStatus;

    private String imgUrl;

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

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public int getSkuStatus() {
        return skuStatus;
    }

    public void setSkuStatus(int skuStatus) {
        this.skuStatus = skuStatus;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "SkuModel{" +
                "skuId=" + skuId +
                ", skuCode='" + skuCode + '\'' +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", specVal='" + specVal + '\'' +
                ", price=" + price +
                ", store=" + store +
                ", productStatus=" + productStatus +
                ", skuStatus=" + skuStatus +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
