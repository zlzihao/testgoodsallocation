package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * AllocationFlowDO
 *
 * @author Bruce01.fan
 * @date 2019/7/26
 */
public class AllocationFlowDO extends ToString{

    private Integer id;

    private Integer taskId;

    private String demandShopId;

    private String supplyShopId;

    private String demandShopName;

    private String supplyShopName;

    private Integer allocationQty;

    private Double fee;

    private Double rate;

    private Double demandAmount;

    private Double allocationAmount;

    private int matchOrder;

    private int matchFlag;

    private int failReason;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

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

    public Integer getAllocationQty() {
        return allocationQty;
    }

    public void setAllocationQty(Integer allocationQty) {
        this.allocationQty = allocationQty;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(Double demandAmount) {
        this.demandAmount = demandAmount;
    }

    public Double getAllocationAmount() {
        return allocationAmount;
    }

    public void setAllocationAmount(Double allocationAmount) {
        this.allocationAmount = allocationAmount;
    }

    public int getMatchOrder() {
        return matchOrder;
    }

    public void setMatchOrder(int matchOrder) {
        this.matchOrder = matchOrder;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDemandShopName() {
        return demandShopName;
    }

    public void setDemandShopName(String demandShopName) {
        this.demandShopName = demandShopName;
    }

    public String getSupplyShopName() {
        return supplyShopName;
    }

    public void setSupplyShopName(String supplyShopName) {
        this.supplyShopName = supplyShopName;
    }

    public int getMatchFlag() {
        return matchFlag;
    }

    public void setMatchFlag(int matchFlag) {
        this.matchFlag = matchFlag;
    }

    public int getFailReason() {
        return failReason;
    }

    public void setFailReason(int failReason) {
        this.failReason = failReason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
