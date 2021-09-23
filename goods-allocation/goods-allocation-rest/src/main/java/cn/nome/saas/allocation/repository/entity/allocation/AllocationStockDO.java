package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * AllocationStockDO
 *
 * @author Bruce01.fan
 * @date 2019/6/22
 */
public class AllocationStockDO extends ToString {

    private Integer id;

    private Integer taskId;

    private String allocationDate;

    private String shopId;

    private String shopName;

    private String shopLevel;

    private String cityName;

    private String supplyShopId;

    private String supplyShopName;

    private String supplyShopLevel;

    private String supplyCityName;

    private String matCode;

    private String matName;

    private String categoryCode;

    private String sizeId;

    private String sizeCode;

    private String sizeName;

    private String batCode;

    private Double quotePrice; // 零售价

    private Integer sumSaleQty; // 28天总销量

    private Double avgSaleQty; // 日均销

    private Double supplyAvgSaleQty; // 供给店日均销

    private Integer supplySumSaleQty; // 调出店 28天总销量

    private Integer applyStockQty; // 在配库存

    private Integer stockQty; // 门店库存

    private Integer pathStockQty; // 在途库存

    private Integer safeStockQty; // 安全库存

    private Integer idealStockQty; // 理想库存

    private Integer demandStockQty; // 需求库存

    private Integer supplyStockQty; // 供给库存

    private Integer allocationStockQty; // 调拨库存

    private Integer minDisplayQty; // 最小陈列数

    private Integer demandForbiddenFlag; // 调入店禁配标示

    private Integer supplyForbiddenFlag; // 调出店禁配标示

    private Integer demandDays = 0; // 调入安全天数

    private Integer supplyDays = 0; // 调入安全天数

    public String getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(String allocationDate) {
        this.allocationDate = allocationDate;
    }

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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLevel() {
        return shopLevel;
    }

    public void setShopLevel(String shopLevel) {
        this.shopLevel = shopLevel;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSupplyShopId() {
        return supplyShopId;
    }

    public void setSupplyShopId(String supplyShopId) {
        this.supplyShopId = supplyShopId;
    }

    public String getSupplyShopName() {
        return supplyShopName;
    }

    public void setSupplyShopName(String supplyShopName) {
        this.supplyShopName = supplyShopName;
    }

    public String getSupplyShopLevel() {
        return supplyShopLevel;
    }

    public void setSupplyShopLevel(String supplyShopLevel) {
        this.supplyShopLevel = supplyShopLevel;
    }

    public String getSupplyCityName() {
        return supplyCityName;
    }

    public void setSupplyCityName(String supplyCityName) {
        this.supplyCityName = supplyCityName;
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

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getBatCode() {
        return batCode;
    }

    public void setBatCode(String batCode) {
        this.batCode = batCode;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public Integer getSumSaleQty() {
        return sumSaleQty;
    }

    public void setSumSaleQty(Integer sumSaleQty) {
        this.sumSaleQty = sumSaleQty;
    }

    public Double getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Double avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public Integer getApplyStockQty() {
        return applyStockQty;
    }

    public void setApplyStockQty(Integer applyStockQty) {
        this.applyStockQty = applyStockQty;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Integer getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(Integer pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public Integer getSafeStockQty() {
        return safeStockQty;
    }

    public void setSafeStockQty(Integer safeStockQty) {
        this.safeStockQty = safeStockQty;
    }

    public Integer getIdealStockQty() {
        return idealStockQty;
    }

    public void setIdealStockQty(Integer idealStockQty) {
        this.idealStockQty = idealStockQty;
    }

    public Integer getDemandStockQty() {
        return demandStockQty;
    }

    public void setDemandStockQty(Integer demandStockQty) {
        this.demandStockQty = demandStockQty;
    }

    public Integer getAllocationStockQty() {
        return allocationStockQty;
    }

    public void setAllocationStockQty(Integer allocationStockQty) {
        this.allocationStockQty = allocationStockQty;
    }

    public Integer getSupplyStockQty() {
        return supplyStockQty;
    }

    public void setSupplyStockQty(Integer supplyStockQty) {
        this.supplyStockQty = supplyStockQty;
    }

    public Double getSupplyAvgSaleQty() {
        return supplyAvgSaleQty;
    }

    public void setSupplyAvgSaleQty(Double supplyAvgSaleQty) {
        this.supplyAvgSaleQty = supplyAvgSaleQty;
    }

    public Integer getMinDisplayQty() {
        return minDisplayQty;
    }

    public void setMinDisplayQty(Integer minDisplayQty) {
        this.minDisplayQty = minDisplayQty;
    }

    public Integer getDemandForbiddenFlag() {
        return demandForbiddenFlag;
    }

    public void setDemandForbiddenFlag(Integer demandForbiddenFlag) {
        this.demandForbiddenFlag = demandForbiddenFlag;
    }

    public Integer getSupplyForbiddenFlag() {
        return supplyForbiddenFlag;
    }

    public void setSupplyForbiddenFlag(Integer supplyForbiddenFlag) {
        this.supplyForbiddenFlag = supplyForbiddenFlag;
    }

    public Integer getSupplySumSaleQty() {
        return supplySumSaleQty;
    }

    public void setSupplySumSaleQty(Integer supplySumSaleQty) {
        this.supplySumSaleQty = supplySumSaleQty;
    }

    public Integer getDemandDays() {
        return demandDays;
    }

    public void setDemandDays(Integer demandDays) {
        this.demandDays = demandDays;
    }

    public Integer getSupplyDays() {
        return supplyDays;
    }

    public void setSupplyDays(Integer supplyDays) {
        this.supplyDays = supplyDays;
    }
}
