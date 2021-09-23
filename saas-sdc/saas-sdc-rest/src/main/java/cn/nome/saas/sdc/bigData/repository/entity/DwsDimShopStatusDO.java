package cn.nome.saas.sdc.bigData.repository.entity;

import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class DwsDimShopStatusDO {
    private String shopCode;
    private String shopName;
    private String shopState;
    private Date openShopDate;

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

    public String getShopState() {
        return shopState;
    }

    public void setShopState(String shopState) {
        this.shopState = shopState;
    }

    public Date getOpenShopDate() {
        return openShopDate;
    }

    public void setOpenShopDate(Date openShopDate) {
        this.openShopDate = openShopDate;
    }
}
