package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class IssueGoodsData {

    private String ShopID;
    private int TaskId;
    private String MatCode;
    private String SizeID;
    private String SizeName;

    private BigDecimal SaleQty7 = new BigDecimal(0);
    private BigDecimal SaleQty28 = new BigDecimal(0);

    private String CategoryName;
    private String MidCategoryName;
    private String SmallCategoryName;

    private String CategoryCode;

    private String GoodsLevel;

    private int SizeCount = 0;

    private int MidDisplaydepth = 0;
    private int SmallDisplaydepth = 0;

    private Integer ShopRank = 0;
    private Integer NationalRank = 0;

    private BigDecimal DisplayPercent = new BigDecimal(0);

    private int Status = 0;

    //仓位数
    private BigDecimal DisplayQty = new BigDecimal(0);

    public String getSizeName() {
        return SizeName;
    }

    public void setSizeName(String sizeName) {
        SizeName = sizeName;
    }

    public BigDecimal getDisplayQty() {
        return DisplayQty;
    }

    public void setDisplayQty(BigDecimal displayQty) {
        DisplayQty = displayQty;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public String getSizeID() {
        return SizeID;
    }

    public void setSizeID(String sizeID) {
        SizeID = sizeID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getMidCategoryName() {
        return MidCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        MidCategoryName = midCategoryName;
    }

    public String getSmallCategoryName() {
        return SmallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        SmallCategoryName = smallCategoryName;
    }

    public BigDecimal getSaleQty7() {
        return SaleQty7;
    }

    public void setSaleQty7(BigDecimal saleQty7) {
        SaleQty7 = saleQty7;
    }

    public BigDecimal getSaleQty28() {
        return SaleQty28;
    }

    public void setSaleQty28(BigDecimal saleQty28) {
        SaleQty28 = saleQty28;
    }

    public String getGoodsLevel() {
        return GoodsLevel;
    }

    public void setGoodsLevel(String goodsLevel) {
        GoodsLevel = goodsLevel;
    }

    public BigDecimal getDisplayPercent() {
        return DisplayPercent;
    }

    public void setDisplayPercent(BigDecimal displayPercent) {
        DisplayPercent = displayPercent;
    }

    public int getSizeCount() {
        return SizeCount;
    }

    public void setSizeCount(int sizeCount) {
        SizeCount = sizeCount;
    }

    public int getMidDisplaydepth() {
        return MidDisplaydepth;
    }

    public void setMidDisplaydepth(int midDisplaydepth) {
        MidDisplaydepth = midDisplaydepth;
    }

    public int getSmallDisplaydepth() {
        return SmallDisplaydepth;
    }

    public void setSmallDisplaydepth(int smallDisplaydepth) {
        SmallDisplaydepth = smallDisplaydepth;
    }

    public String getCategoryCode() {
        return CategoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        CategoryCode = categoryCode;
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
}
