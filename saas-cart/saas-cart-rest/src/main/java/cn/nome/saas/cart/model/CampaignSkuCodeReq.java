package cn.nome.saas.cart.model;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.cart.feign.SkuCodeReq;

import java.util.List;

public class CampaignSkuCodeReq extends ToString {
    private List<SkuCodeReq> skuCodes;

    private Integer appId;

    private Integer corpId;

    private Integer uid;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public List<SkuCodeReq> getSkuCodes() {
        return skuCodes;
    }

    public void setSkuCodes(List<SkuCodeReq> skuCodes) {
        this.skuCodes = skuCodes;
    }
}
