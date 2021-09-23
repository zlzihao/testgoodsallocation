package cn.nome.saas.allocation.model.issue;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * IssueOutStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/20
 */
public class GoodsAreaLevelDetailDo extends ToString {
    private String shopId;
    private String matCode;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }
}
