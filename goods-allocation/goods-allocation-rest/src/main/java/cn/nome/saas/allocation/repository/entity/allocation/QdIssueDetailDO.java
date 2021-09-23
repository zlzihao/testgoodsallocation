package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * 秋冬老品明细
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueDetailDO {

    private Integer id;

    private Integer taskId;

    private String shopId;

    private String categoryName;

    private String midCategoryName;

    private String matCode;

    private String matName;

    private String sizeId;

    private String sizeName;

    private Double quotePrice;

    private long stockQty = 0; // 门店库存

    private long pathQty = 0; // 在途库存

    private long applyQty = 0; // 在配库存

    private long demandQty = 0; // 需求库存

    private long qty;

    private String order;

    private long areaTotal;

    private long provinceTotal;

    private long shopTotal;

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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
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

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(long areaTotal) {
        this.areaTotal = areaTotal;
    }

    public long getProvinceTotal() {
        return provinceTotal;
    }

    public void setProvinceTotal(long provinceTotal) {
        this.provinceTotal = provinceTotal;
    }

    public long getShopTotal() {
        return shopTotal;
    }

    public void setShopTotal(long shopTotal) {
        this.shopTotal = shopTotal;
    }
}
