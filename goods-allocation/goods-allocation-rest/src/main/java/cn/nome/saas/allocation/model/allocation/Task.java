package cn.nome.saas.allocation.model.allocation;


import cn.nome.platform.common.utils.ToString;
import cn.nome.saas.allocation.constant.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Task
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task extends ToString {

    //调拨类型-平衡调拨
    public final static int ALLOCATION_TYPE_BALANCE = 3;

    //调拨类型-撤店
    public final static int ALLOCATION_TYPE_REJECT = 4;

    private int taskId;

    private String taskName;

    private int taskType;

    private Integer taskStatus; // 1-未执行 2-运行中 3-已完成

    private int allocationType; // 1.平衡&归并 2.撤店

    private int closeTaskId;

    private String categoryNames; //  陈列品类

    private String year; // 年份

    private String season; // 季节

    private int breakable; //  易碎标示

    private int inDays; // 调入天数

    private int outDays; // 调出天数

    private int minAllocationPrice; // 最小起调金额

    private Double maxFeeRatio; // 最高费率

    private String userId;

    private String userName;

    private Date createTime;

    private String runTimeStr;

    private Date runTime;

    private int rightNow;

    private int areaType;

    // 需求门店列表
    private String demandShopIds;

    // 供给门店列表
    private String supplyShopIds;

    private String demandShopCodes;

    private String supplyShopCodes;


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getDemandShopIds() {
        return demandShopIds;
    }

    public void setDemandShopIds(String demandShopIds) {
        this.demandShopIds = demandShopIds;
    }

    public String getSupplyShopIds() {
        return supplyShopIds;
    }

    public void setSupplyShopIds(String supplyShopIds) {
        this.supplyShopIds = supplyShopIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public String getDemandShopCodes() {
        return demandShopCodes;
    }

    public void setDemandShopCodes(String demandShopCodes) {
        this.demandShopCodes = demandShopCodes;
    }

    public String getSupplyShopCodes() {
        return supplyShopCodes;
    }

    public void setSupplyShopCodes(String supplyShopCodes) {
        this.supplyShopCodes = supplyShopCodes;
    }

    public String getRunTimeStr() {
        return runTimeStr;
    }

    public void setRunTimeStr(String runTimeStr) {
        this.runTimeStr = runTimeStr;
    }

    public int getRightNow() {
        return rightNow;
    }

    public void setRightNow(int rightNow) {
        this.rightNow = rightNow;
    }

    public int getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(int allocationType) {
        this.allocationType = allocationType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getBreakable() {
        return breakable;
    }

    public void setBreakable(int breakable) {
        this.breakable = breakable;
    }

    public int getInDays() {
        return inDays;
    }

    public void setInDays(int inDays) {
        this.inDays = inDays;
    }

    public int getOutDays() {
        return outDays;
    }

    public void setOutDays(int outDays) {
        this.outDays = outDays;
    }

    public int getMinAllocationPrice() {
        return minAllocationPrice;
    }

    public void setMinAllocationPrice(int minAllocationPrice) {
        this.minAllocationPrice = minAllocationPrice;
    }

    public Double getMaxFeeRatio() {
        return maxFeeRatio;
    }

    public void setMaxFeeRatio(Double maxFeeRatio) {
        this.maxFeeRatio = maxFeeRatio;
    }


    public String getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(String categoryNames) {
        this.categoryNames = categoryNames;
    }

    public int getCloseTaskId() {
        return closeTaskId;
    }

    public void setCloseTaskId(int closeTaskId) {
        this.closeTaskId = closeTaskId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Task{");
        sb.append("taskId=").append(taskId);
        sb.append(", taskName='").append(taskName).append('\'');
        sb.append(", taskType=").append(taskType);
        sb.append(", taskStatus=").append(taskStatus);
        sb.append(", allocationType=").append(allocationType);
        sb.append(", closeTaskId=").append(closeTaskId);
        sb.append(", categoryNames='").append(categoryNames).append('\'');
        sb.append(", year='").append(year).append('\'');
        sb.append(", season='").append(season).append('\'');
        sb.append(", breakable=").append(breakable);
        sb.append(", inDays=").append(inDays);
        sb.append(", outDays=").append(outDays);
        sb.append(", minAllocationPrice=").append(minAllocationPrice);
        sb.append(", maxFeeRatio=").append(maxFeeRatio);
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", runTimeStr='").append(runTimeStr).append('\'');
        sb.append(", runTime=").append(runTime);
        sb.append(", rightNow=").append(rightNow);
        sb.append(", areaType=").append(areaType);
        sb.append(", demandShopIds='").append(demandShopIds).append('\'');
        sb.append(", supplyShopIds='").append(supplyShopIds).append('\'');
        sb.append(", demandShopCodes='").append(demandShopCodes).append('\'');
        sb.append(", supplyShopCodes='").append(supplyShopCodes).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
