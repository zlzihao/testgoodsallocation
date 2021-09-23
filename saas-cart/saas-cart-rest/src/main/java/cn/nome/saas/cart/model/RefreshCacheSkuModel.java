package cn.nome.saas.cart.model;

import java.util.List;

/**
 * @author chentaikuang
 */
public class RefreshCacheSkuModel extends BaseModel {

    private static final long serialVersionUID = 5004085040280732488L;
    private List<String> skuCodes;

    private int uid;

    public RefreshCacheSkuModel(Integer uid,Integer appId, Integer corpId, List<String> skuCodes) {
        this.setAppId(appId);
        this.setCorpId(corpId);
        this.skuCodes = skuCodes;
        this.uid = uid;
    }

    public List<String> getSkuCodes() {
        return skuCodes;
    }

    public void setSkuCodes(List<String> skuCodes) {
        this.skuCodes = skuCodes;
    }

    public RefreshCacheSkuModel(List<String> skuCodes, int uid) {
        this.skuCodes = skuCodes;
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
