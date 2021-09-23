package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class MatCategoryDetailVo {

    private String categoryName;
    /**
     * 订货数量
     */
    private BigDecimal orderQty = new BigDecimal(0);
    /**
     * 订货金额
     */
    private BigDecimal orderAmt = new BigDecimal(0);
    /**
     * 版面陈列量
     */
    private BigDecimal displayDepth = new BigDecimal(0);
    /**
     * 版面数
     */
    private BigDecimal displayQty = new BigDecimal(0);
    /**
     * 动态需求
     */
    private BigDecimal needQty = new BigDecimal(0);
    /**
     * 总库存量
     */
    private BigDecimal shopStock = new BigDecimal(0);
    private BigDecimal avgSaleQty = new BigDecimal(0);
    private BigDecimal turnOverDays = new BigDecimal(0);
    private String beforeStockRate;
    private String afterStockRate;
    private String soldRate;
    /**
     * 预计周转
     */
    private BigDecimal turnOverEstimate;
    /**
     * 饱满度目标
     */
    private BigDecimal displayPercent;
    /**
     * 在配库存数量
     */
    private BigDecimal moveQty;
    /**
     * 在途库存数量
     */
    private BigDecimal pathStockQty;
    /**
     * 在店库存数量
     */
    private BigDecimal stockQty;

    /**
     * 总库存金额
     */
    private BigDecimal totalStockAmt;

    /**
     * 在店库存金额
     */
    private BigDecimal OnlyShopStockAmtYd;


    /**
     * 总在店库存金额
     */
    private BigDecimal shopStockTotalAmt;

    /**
     * 总库存金额占比
     */
    private String totalStockAmtRate;
    /**
     * 在店库存金额占比
     */
    private String shopStockAmtRate;
    /**
     * 在店库存占比
     */
    private String onlyShopStockQtyRate;

    /**
     * 销售数量占比
     */
    private String saleQtyRate;
    /**
     * 偏差数量
     */
    private BigDecimal deviationNum = new BigDecimal(0.0);

    private BigDecimal displayNeedQty;

    /**
     * 28销售数量
     */
    private BigDecimal saleQty28 = new BigDecimal(0);
    /**
     * 7销售数量
     */
    private BigDecimal saleQty7 = new BigDecimal(0);

    //分类SKC数量
    private BigDecimal NewSkcCount = new BigDecimal(0);
    private BigDecimal ProhibitedSkcCount = new BigDecimal(0);
    private BigDecimal ValidSkcCount = new BigDecimal(0);
    private BigDecimal KeepSkcCount = new BigDecimal(0);
    private BigDecimal CanSkcCount = new BigDecimal(0);

    public BigDecimal getNewSkcCount() {
        return NewSkcCount;
    }

    public void setNewSkcCount(BigDecimal newSkcCount) {
        NewSkcCount = newSkcCount;
    }

    public BigDecimal getProhibitedSkcCount() {
        return ProhibitedSkcCount;
    }

    public void setProhibitedSkcCount(BigDecimal prohibitedSkcCount) {
        ProhibitedSkcCount = prohibitedSkcCount;
    }

    public BigDecimal getValidSkcCount() {
        return ValidSkcCount;
    }

    public void setValidSkcCount(BigDecimal validSkcCount) {
        ValidSkcCount = validSkcCount;
    }

    public BigDecimal getKeepSkcCount() {
        return KeepSkcCount;
    }

    public void setKeepSkcCount(BigDecimal keepSkcCount) {
        KeepSkcCount = keepSkcCount;
    }

    public BigDecimal getCanSkcCount() {
        return CanSkcCount;
    }

    public void setCanSkcCount(BigDecimal canSkcCount) {
        CanSkcCount = canSkcCount;
    }

    public BigDecimal getOnlyShopStockAmtYd() {
        return OnlyShopStockAmtYd;
    }

    public void setOnlyShopStockAmtYd(BigDecimal onlyShopStockAmtYd) {
        OnlyShopStockAmtYd = onlyShopStockAmtYd;
    }

    public String getSaleQtyRate() {
        return saleQtyRate;
    }

    public void setSaleQtyRate(String saleQtyRate) {
        this.saleQtyRate = saleQtyRate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(BigDecimal orderQty) {
        this.orderQty = orderQty;
    }

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    public BigDecimal getDisplayDepth() {
        return displayDepth;
    }

    public void setDisplayDepth(BigDecimal displayDepth) {
        this.displayDepth = displayDepth;
    }

    public BigDecimal getDisplayQty() {
        return displayQty;
    }

    public void setDisplayQty(BigDecimal displayQty) {
        this.displayQty = displayQty;
    }

    public BigDecimal getNeedQty() {
        return needQty;
    }

    public void setNeedQty(BigDecimal needQty) {
        this.needQty = needQty;
    }

    public BigDecimal getShopStock() {
        return shopStock;
    }

    public void setShopStock(BigDecimal shopStock) {
        this.shopStock = shopStock;
    }

    public BigDecimal getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public BigDecimal getTurnOverDays() {
        return turnOverDays;
    }

    public void setTurnOverDays(BigDecimal turnOverDays) {
        this.turnOverDays = turnOverDays;
    }

    public String getBeforeStockRate() {
        return beforeStockRate;
    }

    public void setBeforeStockRate(String beforeStockRate) {
        this.beforeStockRate = beforeStockRate;
    }

    public String getAfterStockRate() {
        return afterStockRate;
    }

    public void setAfterStockRate(String afterStockRate) {
        this.afterStockRate = afterStockRate;
    }

    public String getSoldRate() {
        return soldRate;
    }

    public void setSoldRate(String soldRate) {
        this.soldRate = soldRate;
    }

    public BigDecimal getTurnOverEstimate() {
        return turnOverEstimate;
    }

    public void setTurnOverEstimate(BigDecimal turnOverEstimate) {
        this.turnOverEstimate = turnOverEstimate;
    }

    public BigDecimal getDisplayPercent() {
        return displayPercent;
    }

    public void setDisplayPercent(BigDecimal displayPercent) {
        this.displayPercent = displayPercent;
    }

    public BigDecimal getMoveQty() {
        return moveQty;
    }

    public void setMoveQty(BigDecimal moveQty) {
        this.moveQty = moveQty;
    }

    public BigDecimal getPathStockQty() {
        return pathStockQty;
    }

    public void setPathStockQty(BigDecimal pathStockQty) {
        this.pathStockQty = pathStockQty;
    }

    public BigDecimal getStockQty() {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        this.stockQty = stockQty;
    }

    public BigDecimal getTotalStockAmt() {
        return totalStockAmt;
    }

    public void setTotalStockAmt(BigDecimal totalStockAmt) {
        this.totalStockAmt = totalStockAmt;
    }

    public BigDecimal getShopStockTotalAmt() {
        return shopStockTotalAmt;
    }

    public void setShopStockTotalAmt(BigDecimal shopStockTotalAmt) {
        this.shopStockTotalAmt = shopStockTotalAmt;
    }

    public void setTotalStockAmtRate(String totalStockAmtRate) {
        this.totalStockAmtRate = totalStockAmtRate;
    }

    public void setShopStockAmtRate(String shopStockAmtRate) {
        this.shopStockAmtRate = shopStockAmtRate;
    }

    public String getTotalStockAmtRate() {
        return totalStockAmtRate;
    }

    public String getShopStockAmtRate() {
        return shopStockAmtRate;
    }

    public String getOnlyShopStockQtyRate() {
        return onlyShopStockQtyRate;
    }

    public void setOnlyShopStockQtyRate(String onlyShopStockQtyRate) {
        this.onlyShopStockQtyRate = onlyShopStockQtyRate;
    }

    public BigDecimal getDeviationNum() {
        return deviationNum;
    }

    public void setDeviationNum(BigDecimal deviationNum) {
        this.deviationNum = deviationNum;
    }

    public BigDecimal getDisplayNeedQty() {
        return displayNeedQty;
    }

    public void setDisplayNeedQty(BigDecimal displayNeedQty) {
        this.displayNeedQty = displayNeedQty;
    }

    public BigDecimal getSaleQty28() {
        return saleQty28;
    }

    public void setSaleQty28(BigDecimal saleQty28) {
        this.saleQty28 = saleQty28;
    }

    public BigDecimal getSaleQty7() {
        return saleQty7;
    }

    public void setSaleQty7(BigDecimal saleQty7) {
        this.saleQty7 = saleQty7;
    }
}
