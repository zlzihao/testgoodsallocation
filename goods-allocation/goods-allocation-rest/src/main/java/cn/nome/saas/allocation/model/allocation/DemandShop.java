package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.repository.entity.allocation.OutOfStockGoodsDO;

import java.util.List;

/**
 * DemandShop
 *
 * @author Bruce01.fan
 * @date 2019/6/22
 */
public class DemandShop extends ToString {

    private String shopId;

    private double demandAmount; //需求总金额

    private List<OutOfStockGoodsDO> demandList;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public double getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(double demandAmount) {
        this.demandAmount = demandAmount;
    }

    public List<OutOfStockGoodsDO> getDemandList() {
        return demandList;
    }

    public void setDemandList(List<OutOfStockGoodsDO> demandList) {
        this.demandList = demandList;
    }
}
