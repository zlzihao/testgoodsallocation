package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * QdIssueSkcListDO
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueSkcListDO {

    private Integer id;

    private String categoryName;

    private String midCategoryName;

    private String matCode;

    private String matName;

    private String modelType;

    private int year;

    private String season;

    private Double quotePrice;

    private String matchType;

    private int stockQty;

    private int purchaseMonth;

    private String planIssueArea;

    private Date createdAt;

    private int priorityFlag; // 优先级 0-非 1-是

    private int isNews = 0; // 新/老品

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public int getPurchaseMonth() {
        return purchaseMonth;
    }

    public void setPurchaseMonth(int purchaseMonth) {
        this.purchaseMonth = purchaseMonth;
    }

    public String getPlanIssueArea() {
        return planIssueArea;
    }

    public void setPlanIssueArea(String planIssueArea) {
        this.planIssueArea = planIssueArea;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(int priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public int getIsNews() {
        return isNews;
    }

    public void setIsNews(int isNews) {
        this.isNews = isNews;
    }
}
