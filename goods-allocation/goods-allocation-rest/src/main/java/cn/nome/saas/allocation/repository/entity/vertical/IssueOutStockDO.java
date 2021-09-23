package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class IssueOutStockDO extends ToString {

    private String matCode;

    private String sizeID;

    private Long stockQty = 0L;

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
}
