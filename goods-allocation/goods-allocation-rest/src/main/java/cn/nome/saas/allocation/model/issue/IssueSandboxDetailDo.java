package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class IssueSandboxDetailDo extends ToString {
    private Integer id;
    private Integer taskId;
    private String shopId;
    private String shopCode;
    private String shopName;
    private Date issueDate;
    private BigDecimal issueNum;
    private BigDecimal issueValue;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(BigDecimal issueNum) {
        this.issueNum = issueNum;
    }

    public BigDecimal getIssueValue() {
        return issueValue;
    }

    public void setIssueValue(BigDecimal issueValue) {
        this.issueValue = issueValue;
    }
}
