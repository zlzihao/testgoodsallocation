package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * TaskDO
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class TaskDO extends ToString{

    private int taskId;

    private String taskName;

    private int allocationType;

    private int taskType;

    private int rightNow; // 马上运行

    private String userId;

    private String userName;

    private Integer taskStatus; // 1-未执行 2-运行中 3-已完成

    private Integer taskProcess; // 执行进度

    private int closeTaskId;

    private String categoryNames; //  陈列品类

    private String year; // 年份

    private String season; // 季节

    private int breakable; //  易碎标示

    private int inDays; // 调入天数

    private int outDays; // 调出天数

    private int minAllocationPrice; // 最小起调金额

    private Double maxFeeRatio; // 最高费率

    private Date runTime;

    private int areaType;

    private String demandShopIds;

    private String supplyShopIds;

    private Date createTime;

    private String remark;

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

    public int getRightNow() {
        return rightNow;
    }

    public void setRightNow(int rightNow) {
        this.rightNow = rightNow;
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

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(int allocationType) {
        this.allocationType = allocationType;
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

    public int getCloseTaskId() {
        return closeTaskId;
    }

    public void setCloseTaskId(int closeTaskId) {
        this.closeTaskId = closeTaskId;
    }

    public String getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(String categoryNames) {
        this.categoryNames = categoryNames;
    }

    public Integer getTaskProcess() {
        return taskProcess;
    }

    public void setTaskProcess(Integer taskProcess) {
        this.taskProcess = taskProcess;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
