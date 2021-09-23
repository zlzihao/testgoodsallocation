package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

public class OrderDetailVo {

    private String shopCode;
    private String shopName;
    private String img;
    private int taskId;
    private String matCode;
    private String matName;

    private String sizeName;
    private String sizeId;

    private String quotePrice;
    private BigDecimal totalStockQty;
    private String outStockQty;
    private int minPackageQty;

    private BigDecimal avgSaleQty;
    private String saleDays;
    private BigDecimal needQty;
    private BigDecimal packageQty;
    private BigDecimal orderPackage;

    private String categoryName;
    private String midCategoryName;
    private String smallCategoryName;

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

    /**
     * 补货数量
     */
    private BigDecimal orderQty = new BigDecimal(0);

    //是否配发商品
    private int IsIssue = 0;
    private int IsNew;
    private int IsProhibited;

    //补货标识
    private String issueFlag = "";

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
     * 有效日均销(目前实际是28日均销)
     */
    private BigDecimal validSaleQty;
    /**
     * 陈列饱满度
     */
    private BigDecimal DisplayPercent;
    /**
     * 补货前周转天数
     */
    private String exIssueTurnoverDay;

    /**
     * g规则名称
     */
    private String ruleName;
    /**
     * 保底策略数量
     */
    private Integer securityQty;

    private Integer midDisplaydepth;
    private Integer smallDisplaydepth;

    private Integer ShopRank;
    private Integer NationalRank;

    private Integer SizeCount;

    //剩余库存
    private Integer remainStockQty;


    /**
     * 补货周期天数
     */
    private Integer IssueDay;

    /**
     * 分仓代码
     */
    private String warehouseCode;

    /**
     * 分仓名称
     */
    private String warehouseName;

    /**
     * 仓位数
     */
    private String displayQty = "";

    private String percentAvgSaleQty = "";

    private int IsEliminate;

    /**
     * 安全天数
     */
    private Integer safeDay;


    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getDisplayQty() {
        return displayQty;
    }

    public void setDisplayQty(String displayQty) {
        this.displayQty = displayQty;
    }

    public String getPercentAvgSaleQty() {
        return percentAvgSaleQty;
    }

    public void setPercentAvgSaleQty(String percentAvgSaleQty) {
        this.percentAvgSaleQty = percentAvgSaleQty;
    }

    public Integer getIssueDay() {
        return IssueDay;
    }

    public void setIssueDay(Integer issueDay) {
        IssueDay = issueDay;
    }

    public Integer getRemainStockQty() {
        return remainStockQty;
    }

    public void setRemainStockQty(Integer remainStockQty) {
        this.remainStockQty = remainStockQty;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
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

    public String getSaleDays() {
        return saleDays;
    }

    public void setSaleDays(String saleDays) {
        this.saleDays = saleDays;
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

    public String getIssueFlag() {
        return issueFlag;
    }

    public void setIssueFlag(String issueFlag) {
        this.issueFlag = issueFlag;
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

    public BigDecimal getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(BigDecimal orderQty) {
        this.orderQty = orderQty;
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

    public BigDecimal getValidSaleQty() {
        return validSaleQty;
    }

    public void setValidSaleQty(BigDecimal validSaleQty) {
        this.validSaleQty = validSaleQty;
    }

    public String getExIssueTurnoverDay() {
        return exIssueTurnoverDay;
    }

    public void setExIssueTurnoverDay(String exIssueTurnoverDay) {
        this.exIssueTurnoverDay = exIssueTurnoverDay;
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
        return safeDay;
    }

    public void setSafeDay(Integer safeDay) {
        this.safeDay = safeDay;
    }

    public int getIsEliminate() {
        return IsEliminate;
    }

    public void setIsEliminate(int isEliminate) {
        IsEliminate = isEliminate;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
