package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;
import java.util.List;

/**
 * Shop
 *
 * @author Bruce01.fan
 * @date 2019/7/2
 */
public class ShopToStock extends ToString {

    private String shopCode;
    private String shopName;
    private Date date;
    private String userName;
    private String orderNo;
    private int status;
    private String reason;
    private List<CategoryDisplayInfoList> categoryDisplayInfoList;

    public List<CategoryDisplayInfoList> getCategoryDisplayInfoList() {
        return categoryDisplayInfoList;
    }

    public void setCategoryDisplayInfoList(List<CategoryDisplayInfoList> categoryDisplayInfoList) {
        this.categoryDisplayInfoList = categoryDisplayInfoList;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
