package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class BatchUpdateIssueOutStockDo extends ToString {

    public BatchUpdateIssueOutStockDo(Integer id, BigDecimal remainOutQty) {
        this.id = id;
        this.remainOutQty = remainOutQty;
    }

    private Integer id;
    private BigDecimal remainOutQty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getRemainOutQty() {
        return remainOutQty;
    }

    public void setRemainOutQty(BigDecimal remainOutQty) {
        this.remainOutQty = remainOutQty;
    }
}
