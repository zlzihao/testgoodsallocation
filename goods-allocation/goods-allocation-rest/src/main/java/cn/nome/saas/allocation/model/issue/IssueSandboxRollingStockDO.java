package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店商品滚动库存信息表
 * @author zengdewu@nome.com
 */
public class IssueSandboxRollingStockDO {
    private Integer id;
    private Integer taskId;
    private String shopCode;
    private String matCode;
    private String sizeName;
    private Date issueDate;
    private BigDecimal avgSaleQty;//日均销
    private BigDecimal totalStockQty;//门店总库存
    private BigDecimal arriveStockQty;//本轮到货库存 或 上轮发货库存

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

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public BigDecimal getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        this.totalStockQty = totalStockQty;
    }

    public BigDecimal getArriveStockQty() {
        return arriveStockQty;
    }

    public void setArriveStockQty(BigDecimal arriveStockQty) {
        this.arriveStockQty = arriveStockQty;
    }
}
