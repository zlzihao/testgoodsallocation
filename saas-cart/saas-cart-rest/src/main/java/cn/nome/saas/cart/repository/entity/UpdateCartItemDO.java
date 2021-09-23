package cn.nome.saas.cart.repository.entity;

public class UpdateCartItemDO {

    private Integer corpId;

    private Integer appId;

    private Integer userId;

    private String alias;

    private Integer productId;

    private String oldSkuCode;

    private String newSkuCode;

    private Integer skuId;

    private Integer count;

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getOldSkuCode() {
        return oldSkuCode;
    }

    public void setOldSkuCode(String oldSkuCode) {
        this.oldSkuCode = oldSkuCode;
    }

    public String getNewSkuCode() {
        return newSkuCode;
    }

    public void setNewSkuCode(String newSkuCode) {
        this.newSkuCode = newSkuCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }
}