package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;

/**
 * AllocationDetailRecord
 *
 * @author Bruce01.fan
 * @date 2019/7/26
 */
public class AllocationDetailRecord extends ToString{

    private String categoryName;

    private String midCategoryName;

    private String smallCategoryName;

    private String matName;

    private String matCode;

    private Double quotePrice;

    private String yearNo;

    private String seasonName;

    private String colorName;

    private String sizeId;

    private String sizeName;

    private int isAllocationProhibited;

    private int minDisplayQty;

    private String supplyShopId;

    private String supplyShopCode;

    private String supplyShopName;

    private String supplyShopLevel;

    private int supplyForbiddenFlag;

    private Double supply28SalesQty;

    private Double supplyAvgSalesQty;

    private int supplyStockQty;

    private int supplyRemainStockQty;

    private double supplyRemainSalesDays;

    private String demandShopId;

    private String demandShopCode;

    private String demandShopName;

    private String demandShopLevel;

    private int demandForbiddenFlag;

    private Double demand28SalesQty;

    private Double demandAvgSalesQty;

    private int demandQty;

    private int demandSotckQty;

    private int demandPathQty;

    private int demandApplyQty;

    private int allocationQty;

    private Double allocationAmount;

    private int beforeStockQty;

    private int afterStockQty;

    private double demandRemainSalesDay;

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

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getYearNo() {
        return yearNo;
    }

    public void setYearNo(String yearNo) {
        this.yearNo = yearNo;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
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

    public int getIsAllocationProhibited() {
        return isAllocationProhibited;
    }

    public void setIsAllocationProhibited(int isAllocationProhibited) {
        this.isAllocationProhibited = isAllocationProhibited;
    }

    public int getMinDisplayQty() {
        return minDisplayQty;
    }

    public void setMinDisplayQty(int minDisplayQty) {
        this.minDisplayQty = minDisplayQty;
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

    public int getSupplyForbiddenFlag() {
        return supplyForbiddenFlag;
    }

    public void setSupplyForbiddenFlag(int supplyForbiddenFlag) {
        this.supplyForbiddenFlag = supplyForbiddenFlag;
    }

    public Double getSupply28SalesQty() {
        return supply28SalesQty;
    }

    public void setSupply28SalesQty(Double supply28SalesQty) {
        this.supply28SalesQty = supply28SalesQty;
    }

    public Double getSupplyAvgSalesQty() {
        return supplyAvgSalesQty;
    }

    public void setSupplyAvgSalesQty(Double supplyAvgSalesQty) {
        BigDecimal bg = new BigDecimal(supplyAvgSalesQty);
        this.supplyAvgSalesQty = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public int getSupplyStockQty() {
        return supplyStockQty;
    }

    public void setSupplyStockQty(int supplyStockQty) {
        this.supplyStockQty = supplyStockQty;
    }

    public int getSupplyRemainStockQty() {
        return supplyRemainStockQty;
    }


    public void setSupplyRemainSalesDays(int supplyRemainSalesDays) {
        this.supplyRemainSalesDays = supplyRemainSalesDays;
    }

    public String getDemandShopId() {
        return demandShopId;
    }

    public void setDemandShopId(String demandShopId) {
        this.demandShopId = demandShopId;
    }

    public String getDemandShopName() {
        return demandShopName;
    }

    public void setDemandShopName(String demandShopName) {
        this.demandShopName = demandShopName;
    }

    public int getDemandForbiddenFlag() {
        return demandForbiddenFlag;
    }

    public void setDemandForbiddenFlag(int demandForbiddenFlag) {
        this.demandForbiddenFlag = demandForbiddenFlag;
    }

    public Double getDemand28SalesQty() {
        return demand28SalesQty;
    }

    public void setDemand28SalesQty(Double demand28SalesQty) {
        this.demand28SalesQty = demand28SalesQty;
    }

    public Double getDemandAvgSalesQty() {
        return demandAvgSalesQty;
    }

    public void setDemandAvgSalesQty(Double demandAvgSalesQty) {
        BigDecimal bg = new BigDecimal(demandAvgSalesQty);
        this.demandAvgSalesQty = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public int getDemandQty() {
        return demandQty;
    }

    public void setDemandQty(int demandQty) {
        this.demandQty = demandQty;
    }

    public int getDemandSotckQty() {
        return demandSotckQty;
    }

    public void setDemandSotckQty(int demandSotckQty) {
        this.demandSotckQty = demandSotckQty;
    }

    public int getDemandPathQty() {
        return demandPathQty;
    }

    public void setDemandPathQty(int demandPathQty) {
        this.demandPathQty = demandPathQty;
    }

    public int getDemandApplyQty() {
        return demandApplyQty;
    }

    public void setDemandApplyQty(int demandApplyQty) {
        this.demandApplyQty = demandApplyQty;
    }

    public int getAllocationQty() {
        return allocationQty;
    }

    public void setAllocationQty(int allocationQty) {
        this.allocationQty = allocationQty;
    }

    public Double getAllocationAmount() {
        return allocationAmount;
    }

    public void setAllocationAmount(Double allocationAmount) {
        this.allocationAmount = allocationAmount;
    }

    public int getBeforeStockQty() {
        return beforeStockQty;
    }

    public void setBeforeStockQty(int beforeStockQty) {
        this.beforeStockQty = beforeStockQty;
    }

    public int getAfterStockQty() {
        return afterStockQty;
    }

    public void setAfterStockQty(int afterStockQty) {
        this.afterStockQty = afterStockQty;
    }

    public String getSupplyShopCode() {
        return supplyShopCode;
    }

    public void setSupplyShopCode(String supplyShopCode) {
        this.supplyShopCode = supplyShopCode;
    }

    public String getDemandShopCode() {
        return demandShopCode;
    }

    public void setDemandShopCode(String demandShopCode) {
        this.demandShopCode = demandShopCode;
    }

    public void setSupplyRemainStockQty(int supplyRemainStockQty) {
        this.supplyRemainStockQty = supplyRemainStockQty;
    }

    public double getSupplyRemainSalesDays() {
        return supplyRemainSalesDays;
    }

    public void setSupplyRemainSalesDays(double supplyRemainSalesDays) {
        this.supplyRemainSalesDays = supplyRemainSalesDays;
    }

    public double getDemandRemainSalesDay() {
        return demandRemainSalesDay;
    }

    public void setDemandRemainSalesDay(double demandRemainSalesDay) {
        this.demandRemainSalesDay = demandRemainSalesDay;
    }

    public String getSupplyShopLevel() {
        return supplyShopLevel;
    }

    public void setSupplyShopLevel(String supplyShopLevel) {
        this.supplyShopLevel = supplyShopLevel;
    }

    public String getDemandShopLevel() {
        return demandShopLevel;
    }

    public void setDemandShopLevel(String demandShopLevel) {
        this.demandShopLevel = demandShopLevel;
    }
}
