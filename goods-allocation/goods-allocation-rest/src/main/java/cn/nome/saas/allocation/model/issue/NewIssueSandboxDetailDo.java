package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zengdewu@nome.com
 */
public class NewIssueSandboxDetailDo {

    private Integer id;
    private Integer taskId;
    private String shopId;
    private String shopCode;
    private String shopName;
    private Date issueDate;
    private BigDecimal issueNum;
    private String matCode;
    private String sizeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
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

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}
