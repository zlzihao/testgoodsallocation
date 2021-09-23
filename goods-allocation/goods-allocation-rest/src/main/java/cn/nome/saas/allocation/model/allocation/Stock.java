package cn.nome.saas.allocation.model.allocation;

/**
 * Stock
 *
 * @author Bruce01.fan
 * @date 2019/6/21
 */
public class Stock {

    protected String shopId;

    protected String shopName;

    protected Integer shopLevel;

    protected Integer minDisplayQty; // 最小陈列

    protected String matCode;

    protected String matName;

    protected String categoryCode;

    protected String sizeId;

    protected String sizeCode;

    protected String sizeName;

    protected String batCode;

    protected Double quotePrice; // 零售价

    protected Integer sumSaleQty; // 28天总销量

    protected Integer avgSaleQty; // 日均销

    protected Integer applyStockQty; // 在配库存

    protected Integer stockQty; //  店仓库存

    protected Integer pathStockQty; // 在途库存

    protected Integer idealStockQty; // 安全库存

    protected Integer bestStockQty; // 理想库存

    protected boolean forbiddenFlag; // 禁配标志性

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

    public Integer getShopLevel() {
        return shopLevel;
    }

    public void setShopLevel(Integer shopLevel) {
        this.shopLevel = shopLevel;
    }

    public Integer getMinDisplayQty() {
        return minDisplayQty;
    }

    public void setMinDisplayQty(Integer minDisplayQty) {
        this.minDisplayQty = minDisplayQty;
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

    public Integer getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(Integer avgSaleQty) {
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

    public Integer getIdealStockQty() {
        return idealStockQty;
    }

    public void setIdealStockQty(Integer idealStockQty) {
        this.idealStockQty = idealStockQty;
    }

    public Integer getBestStockQty() {
        return bestStockQty;
    }

    public void setBestStockQty(Integer bestStockQty) {
        this.bestStockQty = bestStockQty;
    }

    public Integer getStoreQty() {
        return this.stockQty + this.applyStockQty + this.pathStockQty;
    }

    public Integer getDays70SaleQty() {
        return 70 * this.avgSaleQty ;
    }

    public boolean isForbiddenFlag() {
        return forbiddenFlag;
    }

    public void setForbiddenFlag(boolean forbiddenFlag) {
        this.forbiddenFlag = forbiddenFlag;
    }
}
