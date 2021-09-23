package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;

/**
 * Y3FansRequest
 *
 * @author Bruce01.fan
 * @date 2020/2/9
 */
public class Y3FansRequest extends ToString {

    private int ad_brand_id;

    private int requeststarttime;

    private int requestendtime;

    public int getAd_brand_id() {
        return ad_brand_id;
    }

    public void setAd_brand_id(int ad_brand_id) {
        this.ad_brand_id = ad_brand_id;
    }

    public int getRequeststarttime() {
        return requeststarttime;
    }

    public void setRequeststarttime(int requeststarttime) {
        this.requeststarttime = requeststarttime;
    }

    public int getRequestendtime() {
        return requestendtime;
    }

    public void setRequestendtime(int requestendtime) {
        this.requestendtime = requestendtime;
    }
}
