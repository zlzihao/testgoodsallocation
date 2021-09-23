package cn.nome.saas.allocation.model.allocation;

/**
 * AllocationTaskReq
 *
 * @author Bruce01.fan
 * @date 2019/7/24
 */
public class AllocationTaskReq {

    private int taskId;

    private String year;

    private String season;

    private String matCodes;

    private int inPeriod;

    private int outPeriod;

    private String categoryNames;

    private String excludeDemandShopIds;

    private String excludeSupplyShopIds;

    private boolean minDisplayFlag;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public int getInPeriod() {
        return inPeriod;
    }

    public void setInPeriod(int inPeriod) {
        this.inPeriod = inPeriod;
    }

    public int getOutPeriod() {
        return outPeriod;
    }

    public void setOutPeriod(int outPeriod) {
        this.outPeriod = outPeriod;
    }

    public String getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(String categoryNames) {
        this.categoryNames = categoryNames;
    }

    public String getExcludeDemandShopIds() {
        return excludeDemandShopIds;
    }

    public void setExcludeDemandShopIds(String excludeDemandShopIds) {
        this.excludeDemandShopIds = excludeDemandShopIds;
    }

    public String getExcludeSupplyShopIds() {
        return excludeSupplyShopIds;
    }

    public void setExcludeSupplyShopIds(String excludeSupplyShopIds) {
        this.excludeSupplyShopIds = excludeSupplyShopIds;
    }

    public String getMatCodes() {
        return matCodes;
    }

    public void setMatCodes(String matCodes) {
        this.matCodes = matCodes;
    }

    public boolean isMinDisplayFlag() {
        return minDisplayFlag;
    }

    public void setMinDisplayFlag(boolean minDisplayFlag) {
        this.minDisplayFlag = minDisplayFlag;
    }
}
