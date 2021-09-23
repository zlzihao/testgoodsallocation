package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class IssueOutStock extends ToString {

    private Integer taskId;

    private String stockID;

    private String matCode;

    private String sizeID;

    private Double avgSaleQty;

    private Double avgSaleAmt;

    private Double quotePrice;

    private Long stockQty = 0L;

    private Long remainQty = 0L;

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeID() {
        return sizeID;
    }

    public void setSizeID(String sizeID) {
        this.sizeID = sizeID;
    }

    public Long getStockQty() {
        return stockQty;
    }

    public void setStockQty(Long stockQty) {
        this.stockQty = stockQty;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public Double getAvgSaleAmt() {
        return avgSaleAmt;
    }

    public void setAvgSaleAmt(Double avgSaleAmt) {
        this.avgSaleAmt = avgSaleAmt;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public Long getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(Long remainQty) {
        this.remainQty = remainQty;
    }
}
