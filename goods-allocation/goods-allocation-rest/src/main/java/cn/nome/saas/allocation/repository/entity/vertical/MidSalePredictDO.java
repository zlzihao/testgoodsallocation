package cn.nome.saas.allocation.repository.entity.vertical;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * IssueInStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/19
 */
public class MidSalePredictDO extends ToString{

    private String shopId;

    private BigDecimal avgSalePredict;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public BigDecimal getAvgSalePredict() {
        return avgSalePredict;
    }

    public void setAvgSalePredict(BigDecimal avgSalePredict) {
        this.avgSalePredict = avgSalePredict;
    }
}
