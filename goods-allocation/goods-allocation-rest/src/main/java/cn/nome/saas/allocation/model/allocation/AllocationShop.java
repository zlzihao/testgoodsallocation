package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.model.rule.NewShopExpress;
import cn.nome.saas.allocation.repository.entity.allocation.AllocationStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.OutOfStockGoodsDO;
import cn.nome.saas.allocation.repository.entity.allocation.ShopExpressDO;

import java.util.List;

/**
 * AllocationDetail
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class AllocationShop extends ToString {

    private String demandShopId;

    private String supplyShopId;

    private double demandAmount; // 需求金额

    private double amount; // 可满足需求金额

    private double fee; // 运费

    private double rate; // 费率

    private double score; // 匹配分值

    private NewShopExpress newShopExpress;

    private List<AllocationStockDO> allocationStockDOList; //调拨明细

    public String getDemandShopId() {
        return demandShopId;
    }

    public void setDemandShopId(String demandShopId) {
        this.demandShopId = demandShopId;
    }

    public String getSupplyShopId() {
        return supplyShopId;
    }

    public void setSupplyShopId(String supplyShopId) {
        this.supplyShopId = supplyShopId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


    public NewShopExpress getNewShopExpress() {
        return newShopExpress;
    }

    public void setNewShopExpress(NewShopExpress newShopExpress) {
        this.newShopExpress = newShopExpress;
    }

    public List<AllocationStockDO> getAllocationStockDOList() {
        return allocationStockDOList;
    }

    public void setAllocationStockDOList(List<AllocationStockDO> allocationStockDOList) {
        this.allocationStockDOList = allocationStockDOList;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(double demandAmount) {
        this.demandAmount = demandAmount;
    }
}
