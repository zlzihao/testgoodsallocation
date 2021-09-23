package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

/**
 * AllocationFragementalGoodsDO
 *
 * @author Bruce01.fan
 * @date 2019/6/20
 */
public class AllocationFragementalGoodsDO extends ToString{

    public static final int ENABLE_TYPE = 0;

    public static final int FORBIDDEN_TYPE = 1;

    private Integer id;

    private Integer taskId;

    private String shopId;

    private String shopName;

    private String shopLevel;

    private String cityName;

    private Integer minDisplayQty; // 最小陈列
    private String matCode;

    private String matName;

    private String categoryCode;

    private String sizeId= "";

    private String sizeCode= "";

    private String sizeName = "";

    private String batCode= "";

    private Double quotePrice; // 零售价

    private Integer sumSaleQty; // 28天总销量

    private Double avgSaleQty; // 日均销

    private Integer applyStockQty; // 在配库存

    private Integer stockQty = 0; // 店仓库存

    private Integer pathStockQty  = 0; // 在途库存

    private Integer safeStockQty  = 0; // 安全库存

    private Integer idealStockQty  = 0; // 理想库存

    private Integer demandStockQty  = 0; // 需求库存

    private Integer supplyStockQty  = 0; // 供给库存

    private Integer forbiddenFlag = ENABLE_TYPE; // 禁配标记(0-可配 1-禁配)

    private Double supplyAvgSaleQty; // 供给店日均销

    private Integer supplySumSaleQty; // 调出店 28天总销量

    // 计算用的
    private Integer storeStockQty; // 门店库存

    private Integer salesQty70Days; // 70天销售

    private Integer alloctionStockQty = 0; //  调拨库存

    private Integer remainStockQty = 0; //剩余库存（门店库存-已调拨库存）

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

    public Integer getDemandStockQty() {
        return demandStockQty;
    }

    public void setDemandStockQty(Integer demandStockQty) {
        this.demandStockQty = demandStockQty;
    }

    public Integer getSupplyStockQty() {
        return supplyStockQty;
    }

    public void setSupplyStockQty(Integer supplyStockQty) {
        this.supplyStockQty = supplyStockQty;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Integer getIdealStockQty() {
        return idealStockQty;
    }

    public void setIdealStockQty(Integer idealStockQty) {
        this.idealStockQty = idealStockQty;
    }

    public Integer getForbiddenFlag() {
        return forbiddenFlag;
    }

    public void setForbiddenFlag(Integer forbiddenFlag) {
        this.forbiddenFlag = forbiddenFlag;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getRemainStockQty() {
        remainStockQty = this.getStockQty() - this.getAlloctionStockQty();
        return remainStockQty;
    }

    public void setRemainStockQty(Integer remainStockQty) {
        this.remainStockQty = remainStockQty;
    }

    public Integer getAlloctionStockQty() {
        return alloctionStockQty;
    }

    public void setAlloctionStockQty(Integer alloctionStockQty) {
        this.alloctionStockQty = alloctionStockQty;
    }

    /**
     * 门店库存
     * @return
     */
    public Integer getStoreQty() {
        this.stockQty = this.stockQty == null ? 0 : this.stockQty;
        this.applyStockQty = this.applyStockQty == null ? 0 : this.applyStockQty;
        this.pathStockQty = this.pathStockQty == null ? 0 : this.pathStockQty;

        storeStockQty = this.stockQty + this.applyStockQty + this.pathStockQty;

        return storeStockQty;
    }

    /**
     * 70天销量
     * @return
     */
    public Integer getDays70SaleQty() {
        return ((Double) Math.ceil(70 * this.avgSaleQty)).intValue() ;

        /*double avgQty = this.getSumSaleQty() / 28D;

        salesQty70Days = ((Double) Math.ceil( 70 * avgQty)).intValue();

        return salesQty70Days;*/

    }

    public String getKey() {
        return this.getShopId()+":"+this.matCode+":"+getSizeId();
    }
}
