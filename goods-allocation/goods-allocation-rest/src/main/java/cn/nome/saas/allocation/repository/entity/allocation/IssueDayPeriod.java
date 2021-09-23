package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * IssueDayPeriod
 *
 * @author Bruce01.fan
 * @date 2019/11/27
 */
public class IssueDayPeriod extends ToString {

    private String shopCode;

    private String shopId;

    private Date issueDate;

    private Date arriveDate;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }
}
