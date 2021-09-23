package cn.nome.saas.sdc.model.req;

import cn.nome.platform.common.utils.ToString;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2020/3/4 10:27
 */
public class ShopWechatReq extends ToString {

    private Integer id;
    private Integer shopId;
    private String shopCode;
    private String jobNumber;
    private String configId;
    private String qrCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

}
