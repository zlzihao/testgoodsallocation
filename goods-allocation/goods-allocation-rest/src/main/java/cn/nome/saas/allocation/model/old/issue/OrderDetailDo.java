package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class OrderDetailDo {

    private int taskId;
    private String shopId;
    private String matCode;
    private String matName;
    private String sizeId;
    private String quotePrice;

    private BigDecimal totalStockQty = new BigDecimal(0);
    private String outStockQty = "0";
    private int minPackageQty;

    private BigDecimal avgSaleQty = new BigDecimal(0);

    private BigDecimal needQty = new BigDecimal(0);
    private BigDecimal packageQty = new BigDecimal(0);
    private BigDecimal orderPackage = new BigDecimal(0);

    private String sizeName;

    private String categoryName;
    private String midCategoryName;
    private String smallCategoryName;

    private Integer midDisplaydepth = 0;
    private Integer smallDisplaydepth = 0;

    /**
     * 商品季节
     */
    private String SeasonName;
    /**
     * 商品年份
     */
    private String YearNo;

    /**
     * 在店库存
     */
    private BigDecimal inStockQty = new BigDecimal(0);
    /**
     * 在途库存
     */
    private BigDecimal PathStockQty = new BigDecimal(0);
    /**
     * 在配库存
     */
    private BigDecimal MoveQty = new BigDecimal(0);

    //是否配发商品
    private int IsIssue = 0;
    private int IsNew;
    private int IsProhibited;

    /**
     * 货盘等级
     */
    private String GoodsLevel;
    /**
     * 28天销量
     */
    private int SaleQty28;
    /**
     * 7天销量
     */
    private int SaleQty7;
    /**
     * 陈列饱满度
     */
    private BigDecimal DisplayPercent = new BigDecimal(0);

    /**
     * g规则名称
     */
    private String ruleName;
    /**
     * 保底策略数量
     */
    private Integer securityQty;


    /**
     * 补货周期天数
     */
    private Integer IssueDay;

    private Integer SafeDay;



    private Integer ShopRank = 0;
    private Integer NationalRank = 0;
    private Integer SizeCount = 0;

    /**
     * 分仓代码
     */
    private String warehouseCode;

    //sku日均销中类占比
    private BigDecimal PercentCategory = new BigDecimal(0);

    private int IsEliminate;

    /**
     * 剩余库存
     */
    private Integer remainStockQty;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓位数
     */
    private String displayQty = "";

    public String getDisplayQty() {
        return displayQty;
    }

    public void setDisplayQty(String displayQty) {
        this.displayQty = displayQty;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getRemainStockQty() {
        return remainStockQty;
    }

    public void setRemainStockQty(Integer remainStockQty) {
        this.remainStockQty = remainStockQty;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }


    public BigDecimal getPercentCategory() {
        return PercentCategory;
    }

    public void setPercentCategory(BigDecimal percentCategory) {
        PercentCategory = percentCategory;
    }

    public Integer getIssueDay() {
        return IssueDay;
    }

    public void setIssueDay(Integer issueDay) {
        IssueDay = issueDay;
    }

    public Integer getSizeCount() {
        return SizeCount;
    }

    public void setSizeCount(Integer sizeCount) {
        SizeCount = sizeCount;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getSecurityQty() {
        return securityQty;
    }

    public void setSecurityQty(Integer securityQty) {
        this.securityQty = securityQty;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
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

    public String getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(String quotePrice) {
        this.quotePrice = quotePrice;
    }

    public BigDecimal getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(BigDecimal inStockQty) {
        this.inStockQty = inStockQty;
    }

    public String getOutStockQty() {
        return outStockQty;
    }

    public void setOutStockQty(String outStockQty) {
        this.outStockQty = outStockQty;
    }

    public int getMinPackageQty() {
        return minPackageQty;
    }

    public void setMinPackageQty(int minPackageQty) {
        this.minPackageQty = minPackageQty;
    }

    public BigDecimal getAvgSaleQty() {
        return avgSaleQty;
    }

    public void setAvgSaleQty(BigDecimal avgSaleQty) {
        this.avgSaleQty = avgSaleQty;
    }

    public BigDecimal getNeedQty() {
        return needQty;
    }

    public void setNeedQty(BigDecimal needQty) {
        this.needQty = needQty;
    }

    public BigDecimal getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(BigDecimal packageQty) {
        this.packageQty = packageQty;
    }

    public BigDecimal getOrderPackage() {
        return orderPackage;
    }

    public void setOrderPackage(BigDecimal orderPackage) {
        this.orderPackage = orderPackage;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
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

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public int getIsIssue() {
        return IsIssue;
    }

    public void setIsIssue(int isIssue) {
        IsIssue = isIssue;
    }

    public int getIsNew() {
        return IsNew;
    }

    public void setIsNew(int isNew) {
        IsNew = isNew;
    }

    public int getIsProhibited() {
        return IsProhibited;
    }

    public void setIsProhibited(int isProhibited) {
        IsProhibited = isProhibited;
    }

    public String getSeasonName() {
        return SeasonName;
    }

    public void setSeasonName(String seasonName) {
        SeasonName = seasonName;
    }

    public String getYearNo() {
        return YearNo;
    }

    public void setYearNo(String yearNo) {
        YearNo = yearNo;
    }

    public BigDecimal getTotalStockQty() {
        return totalStockQty;
    }

    public void setTotalStockQty(BigDecimal totalStockQty) {
        this.totalStockQty = totalStockQty;
    }

    public BigDecimal getPathStockQty() {
        return PathStockQty;
    }

    public void setPathStockQty(BigDecimal pathStockQty) {
        PathStockQty = pathStockQty;
    }

    public BigDecimal getMoveQty() {
        return MoveQty;
    }

    public void setMoveQty(BigDecimal moveQty) {
        MoveQty = moveQty;
    }

    public String getGoodsLevel() {
        return GoodsLevel;
    }

    public void setGoodsLevel(String goodsLevel) {
        GoodsLevel = goodsLevel;
    }

    public int getSaleQty28() {
        return SaleQty28;
    }

    public void setSaleQty28(int saleQty28) {
        SaleQty28 = saleQty28;
    }

    public int getSaleQty7() {
        return SaleQty7;
    }

    public void setSaleQty7(int saleQty7) {
        SaleQty7 = saleQty7;
    }

    public BigDecimal getDisplayPercent() {
        return DisplayPercent;
    }

    public void setDisplayPercent(BigDecimal displayPercent) {
        DisplayPercent = displayPercent;
    }

    public Integer getMidDisplaydepth() {
        return midDisplaydepth;
    }

    public void setMidDisplaydepth(Integer midDisplaydepth) {
        this.midDisplaydepth = midDisplaydepth;
    }

    public Integer getSmallDisplaydepth() {
        return smallDisplaydepth;
    }

    public void setSmallDisplaydepth(Integer smallDisplaydepth) {
        this.smallDisplaydepth = smallDisplaydepth;
    }

    public Integer getShopRank() {
        return ShopRank;
    }

    public void setShopRank(Integer shopRank) {
        ShopRank = shopRank;
    }

    public Integer getNationalRank() {
        return NationalRank;
    }

    public void setNationalRank(Integer nationalRank) {
        NationalRank = nationalRank;
    }

    public Integer getSafeDay() {
        return SafeDay;
    }

    public void setSafeDay(Integer safeDay) {
        SafeDay = safeDay;
    }

    public int getIsEliminate() {
        return IsEliminate;
    }

    public void setIsEliminate(int isEliminate) {
        IsEliminate = isEliminate;
    }
}
