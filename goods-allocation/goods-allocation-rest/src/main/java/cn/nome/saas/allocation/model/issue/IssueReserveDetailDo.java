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
public class IssueReserveDetailDo extends ToString {
    private Integer id;
    private String shopId;
    private String matCode;
    private String sizeName;
    private BigDecimal issueReserveNum;


    public IssueReserveDetailDo(String shopId, String matCode, String sizeName, BigDecimal issueReserveNum) {
        this.shopId = shopId;
        this.matCode = matCode;
        this.sizeName = sizeName;
        this.issueReserveNum = issueReserveNum;
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

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public BigDecimal getIssueReserveNum() {
        return issueReserveNum;
    }

    public void setIssueReserveNum(BigDecimal issueReserveNum) {
        this.issueReserveNum = issueReserveNum;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getShopIdMatCodeSizeNameKey() {
        return this.getShopId() + "_" + this.getMatCode() + "_" + this.getSizeName();
    }


}
