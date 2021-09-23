package cn.nome.saas.allocation.model.old.allocation;

import java.util.Date;

@Deprecated
public class StockYesterday {

    private Date OperationDate;
    private String ShopID;
    private String StockID;
    private String MatCode;
    private String SizeID;
    private String SizeName;

    private String MatName;
    private String CategoryName;
    private String MidCategoryName;
    private Double ShopStockQtyYd;
    private Double ShopStockAmtYd;

    public Date getOperationDate() {
        return OperationDate;
    }

    public void setOperationDate(Date operationDate) {
        OperationDate = operationDate;
    }

    public String getShopID() {
        return ShopID;
    }

    public void setShopID(String shopID) {
        ShopID = shopID;
    }

    public String getStockID() {
        return StockID;
    }

    public void setStockID(String stockID) {
        StockID = stockID;
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

    public String getSizeName() {
        return SizeName;
    }

    public void setSizeName(String sizeName) {
        SizeName = sizeName;
    }

    public String getMatName() {
        return MatName;
    }

    public void setMatName(String matName) {
        MatName = matName;
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

    public Double getShopStockQtyYd() {
        return ShopStockQtyYd;
    }

    public void setShopStockQtyYd(Double shopStockQtyYd) {
        ShopStockQtyYd = shopStockQtyYd;
    }

    public Double getShopStockAmtYd() {
        return ShopStockAmtYd;
    }

    public void setShopStockAmtYd(Double shopStockAmtYd) {
        ShopStockAmtYd = shopStockAmtYd;
    }
}
