package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class BatchUpdateIssueNeedStockDo extends ToString {

    public BatchUpdateIssueNeedStockDo(Integer id, BigDecimal remainNeedQty) {
        this.id = id;
        this.remainNeedQty = remainNeedQty;
    }

    private Integer id;
    private BigDecimal remainNeedQty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getRemainNeedQty() {
        return remainNeedQty;
    }

    public void setRemainNeedQty(BigDecimal remainNeedQty) {
        this.remainNeedQty = remainNeedQty;
    }
}
