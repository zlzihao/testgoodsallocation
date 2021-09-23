package cn.nome.saas.allocation.model.allocation;

import java.util.Date;

/**
 * AllocationDetail
 *
 * @author Bruce01.fan
 * @date 2019/8/28
 */
public class AllocationDetail {

    private int id;

    private int flag;

    private String inShopId;

    private String outShopId;

    private String inShop;

    private String outShop;

    private int skuCnt;

    private double allocationAmount;

    private double demandAmount;

    private int expressFee;

    private double feeRatio;

    private Date date;

    private int failFlag;

    private String failMsg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getInShop() {
        return inShop;
    }

    public void setInShop(String inShop) {
        this.inShop = inShop;
    }

    public String getOutShop() {
        return outShop;
    }

    public void setOutShop(String outShop) {
        this.outShop = outShop;
    }

    public int getSkuCnt() {
        return skuCnt;
    }

    public void setSkuCnt(int skuCnt) {
        this.skuCnt = skuCnt;
    }

    public double getAllocationAmount() {
        return allocationAmount;
    }

    public void setAllocationAmount(double allocationAmount) {
        this.allocationAmount = allocationAmount;
    }

    public double getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(double demandAmount) {
        this.demandAmount = demandAmount;
    }

    public int getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(int expressFee) {
        this.expressFee = expressFee;
    }

    public double getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(double feeRatio) {
        this.feeRatio = feeRatio;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFailFlag() {
        return failFlag;
    }

    public void setFailFlag(int failFlag) {
        this.failFlag = failFlag;
    }

    public String getInShopId() {
        return inShopId;
    }

    public void setInShopId(String inShopId) {
        this.inShopId = inShopId;
    }

    public String getOutShopId() {
        return outShopId;
    }

    public void setOutShopId(String outShopId) {
        this.outShopId = outShopId;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }
}
