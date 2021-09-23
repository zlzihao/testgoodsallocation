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
public class IssueSandboxShopStockDo extends ToString {
    private Integer id;
    private Integer taskId;
    private String shopId;
    private String shopCode;
    private String shopName;
    private Date sandboxDate;
    private BigDecimal stock;
    private BigDecimal stockValue;

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

    public Date getSandboxDate() {
        return sandboxDate;
    }

    public void setSandboxDate(Date sandboxDate) {
        this.sandboxDate = sandboxDate;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = stockValue;
    }
}
