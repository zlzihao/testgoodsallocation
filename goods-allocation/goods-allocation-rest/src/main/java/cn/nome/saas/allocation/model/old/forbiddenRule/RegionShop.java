package cn.nome.saas.allocation.model.old.forbiddenRule;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

/**
 * RegionShop
 *
 * @author Bruce01.fan
 * @date 2019/5/30
 */
public class RegionShop extends ToString {

    @Column(value = "门店代码",num = 0,isFilter = true,autoLine=true)
    private String shopCode;
    @Column(value = "区域",num = 4,isFilter = true,autoLine=true)
    private String region;
    @Column(value = "省份",num = 5,isFilter = true,autoLine=true)
    private String province;
    @Column(value = "城市",num = 6,isFilter = true,autoLine=true)
    private String city;
    @Column(value = "状态",num = 7,isFilter = true,autoLine=true)
    private String status;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
