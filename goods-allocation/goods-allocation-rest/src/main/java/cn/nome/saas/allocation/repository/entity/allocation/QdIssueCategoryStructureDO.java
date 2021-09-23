package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * 秋冬老品品类结构
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueCategoryStructureDO {

    private Integer id;

    private String regionName;

    private int month;

    private String categoryName;

    private String midCategoryName;

    private String matchType; // 内外搭(1-内搭 2-外搭 3-下装)

    private Double businessPercent;

    private Double avgPrice;

    private Double depth;

    private Double skcPercent;

    private Date createdAt;

    /**
     * 是否处理，1-是，0-否
     */
    private Integer isDeal;

    /**
     * 处理结果
     */
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Double getBusinessPercent() {
        return businessPercent;
    }

    public void setBusinessPercent(Double businessPercent) {
        this.businessPercent = businessPercent;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getSkcPercent() {
        return skcPercent;
    }

    public void setSkcPercent(Double skcPercent) {
        this.skcPercent = skcPercent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(Integer isDeal) {
        this.isDeal = isDeal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
