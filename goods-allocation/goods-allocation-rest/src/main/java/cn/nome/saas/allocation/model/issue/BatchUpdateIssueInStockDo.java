package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class BatchUpdateIssueInStockDo extends ToString {

    public BatchUpdateIssueInStockDo(Integer id, BigDecimal totalStockQty) {
        this.id = id;
        this.totalStockQty = totalStockQty;
    }

    private Integer id;
    private BigDecimal totalStockQty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        this.totalStockQty = totalStockQty;
    }
}
