package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

/**
 * Y3AdManage
 *
 * @author Bruce01.fan
 * @date 2020/2/9
 */
public class Y3AdManage extends ToString {

    private int ad_brand_id;

    private String ad_brand_name;

    private String ad_brand_code;

    private String ad_brand_welist;

    private String merchantid;

    private String ad_wechat_nick;

    private String wechats;

    private String ad_brand_region_code;

    public int getAd_brand_id() {
        return ad_brand_id;
    }

    public void setAd_brand_id(int ad_brand_id) {
        this.ad_brand_id = ad_brand_id;
    }

    public String getAd_brand_name() {
        return ad_brand_name;
    }

    public void setAd_brand_name(String ad_brand_name) {
        this.ad_brand_name = ad_brand_name;
    }

    public String getAd_brand_code() {
        return ad_brand_code;
    }

    public void setAd_brand_code(String ad_brand_code) {
        this.ad_brand_code = ad_brand_code;
    }

    public String getAd_brand_welist() {
        return ad_brand_welist;
    }

    public void setAd_brand_welist(String ad_brand_welist) {
        this.ad_brand_welist = ad_brand_welist;
    }

    public String getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(String merchantid) {
        this.merchantid = merchantid;
    }

    public String getAd_wechat_nick() {
        return ad_wechat_nick;
    }

    public void setAd_wechat_nick(String ad_wechat_nick) {
        this.ad_wechat_nick = ad_wechat_nick;
    }

    public String getWechats() {
        return wechats;
    }

    public void setWechats(String wechats) {
        this.wechats = wechats;
    }

    public String getAd_brand_region_code() {
        return ad_brand_region_code;
    }

    public void setAd_brand_region_code(String ad_brand_region_code) {
        this.ad_brand_region_code = ad_brand_region_code;
    }
}
