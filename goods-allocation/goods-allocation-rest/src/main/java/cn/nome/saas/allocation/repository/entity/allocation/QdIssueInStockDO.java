package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * 秋冬老品需求池
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueInStockDO extends ToString{

    private Integer id;

    private Integer taskId;

    private String shopId;

    private String matName;

    private String matCode;

    private String categoryName;

    private String midCategoryName;

    private String sizeId;

    private String sizeName;

    private long idealQty = 0;

    private long stockQty = 0; // 门店库存

    private long pathQty = 0; // 在途库存

    private long applyQty = 0; // 在配库存

    private long demandQty = 0; // 需求库存

    private Date createdAt;

    private int isNews = 0; // 新/老品·

    /**
     * 辅助字段
     *
     */
    private String areaName;

    private String regionName;

    private String provinceName;

    private Double depth;

    private long regionDemandQty; // 区域需求量

    private long provinceDemandQty; // 省需求量

    private long totalDemandQty; // 门店需求量

    private long issueQty = 0; // 分配库存

    private long afterIssueStockQty; // 分配后总库存（门店+在途+在配+配发）

    private int businessLevel; // 1 2,3,4

    private String matchType; // 内外搭(1-内搭 2-外搭 3-下装)

    private String modelType; // 款型

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

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public long getIdealQty() {
        return idealQty;
    }

    public void setIdealQty(long idealQty) {
        this.idealQty = idealQty;
    }

    public long getStockQty() {
        return stockQty;
    }

    public void setStockQty(long stockQty) {
        this.stockQty = stockQty;
    }

    public long getPathQty() {
        return pathQty;
    }

    public void setPathQty(long pathQty) {
        this.pathQty = pathQty;
    }

    public long getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(long applyQty) {
        this.applyQty = applyQty;
    }

    public long getDemandQty() {
        return demandQty;
    }

    public void setDemandQty(long demandQty) {
        this.demandQty = demandQty;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public long getRegionDemandQty() {
        return regionDemandQty;
    }

    public void setRegionDemandQty(long regionDemandQty) {
        this.regionDemandQty = regionDemandQty;
    }

    public long getProvinceDemandQty() {
        return provinceDemandQty;
    }

    public void setProvinceDemandQty(long provinceDemandQty) {
        this.provinceDemandQty = provinceDemandQty;
    }

    //
    public long getTotalDemandQty() {
        this.totalDemandQty = this.demandQty + this.regionDemandQty + this.provinceDemandQty;

        return this.totalDemandQty;
    }

    public void setTotalDemandQty(long totalDemandQty) {
        this.totalDemandQty = totalDemandQty;
    }

    public long getIssueQty() {
        return issueQty;
    }

    public void setIssueQty(long issueQty) {
        this.issueQty = issueQty;
    }

    public long getAfterIssueStockQty() {
        return this.stockQty + this.pathQty + this.applyQty + this.issueQty;
    }

    public void setAfterIssueStockQty(long afterIssueStockQty) {
        this.afterIssueStockQty = afterIssueStockQty;
    }

    public int getBusinessLevel() {
        return businessLevel;
    }

    public void setBusinessLevel(int businessLevel) {
        this.businessLevel = businessLevel;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getKey() {
        return this.shopId+":" + this.matCode+":" + this.sizeId;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public int getIsNews() {
        return isNews;
    }

    public void setIsNews(int isNews) {
        this.isNews = isNews;
    }
}
