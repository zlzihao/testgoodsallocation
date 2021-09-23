package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class MatCategoryDetailDo {
    private String categoryName;
    private BigDecimal orderQty = new BigDecimal(0);
    private BigDecimal orderAmt = new BigDecimal(0);
    private BigDecimal displayDepth = new BigDecimal(0);
    private BigDecimal displayQty = new BigDecimal(0);
    private BigDecimal needQty = new BigDecimal(0);
    private BigDecimal avgSaleQty = new BigDecimal(0);
    private BigDecimal turnOverDays = new BigDecimal(0);
    private BigDecimal beforeStockRate = new BigDecimal(0);
    private BigDecimal afterStockRate = new BigDecimal(0);
    private BigDecimal soldRate = new BigDecimal(0);
    private BigDecimal turnOverEstimate = new BigDecimal(0);
    /**
     * 饱满度目标
     */
    private BigDecimal displayPercent = new BigDecimal(0);
    /**
     * 在配库存数量
     */
    private BigDecimal moveQty = new BigDecimal(0);
    /**
     * 在途库存数量
     */
    private BigDecimal pathStockQty = new BigDecimal(0);
    /**
     * 在店库存数量
     */
    private BigDecimal stockQty = new BigDecimal(0);

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

    public BigDecimal getBeforeStockRate() {
        return beforeStockRate;
    }

    public void setBeforeStockRate(BigDecimal beforeStockRate) {
        this.beforeStockRate = beforeStockRate;
    }

    public BigDecimal getAfterStockRate() {
        return afterStockRate;
    }

    public void setAfterStockRate(BigDecimal afterStockRate) {
        this.afterStockRate = afterStockRate;
    }

    public BigDecimal getSoldRate() {
        return soldRate;
    }

    public void setSoldRate(BigDecimal soldRate) {
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
}
