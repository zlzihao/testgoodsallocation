package cn.nome.saas.sdc.model.excel;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/11/9 14:40
 */
public class ShopsEO extends ToString {

    @Column(num = 0, value = "店铺代码", width = 64)
    private String shopCode;

    @Column(num = 1, value = "店铺名称", width = 128)
    private String shopName;

    @Column(num = 2, value = "大区", width = 64)
    private String bigAreaName;

    @Column(num = 3, value = "小区", width = 64)
    private String smallAreaName;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBigAreaName() {
        return bigAreaName;
    }

    public void setBigAreaName(String bigAreaName) {
        this.bigAreaName = bigAreaName;
    }

    public String getSmallAreaName() {
        return smallAreaName;
    }

    public void setSmallAreaName(String smallAreaName) {
        this.smallAreaName = smallAreaName;
    }
}
