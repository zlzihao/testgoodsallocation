package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;

public class ShopDisplayDesignData {

    private String ShopId;
    private String ShopCode;
    private String CategoryName;
    private String MidCategoryName;
    private BigDecimal DisplayPercent;
    private BigDecimal Display_Qty;

    private String Operator;

    /**
     * 店铺名称. 非表字段
     */
    private String shopName;

    /**
     * 导入的表格的行数, 非表数据
     */
    private Integer rowNum;

    /**
     * 是否有文具台 1-是 0-否 非表数据
     */
    private Integer stationeryTable;

    public Integer getStationeryTable() {
        return stationeryTable;
    }

    public void setStationeryTable(Integer stationeryTable) {
        this.stationeryTable = stationeryTable;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    public String getShopCode() {
        return ShopCode;
    }

    public void setShopCode(String shopCode) {
        ShopCode = shopCode;
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

    public BigDecimal getDisplayPercent() {
        return DisplayPercent;
    }

    public void setDisplayPercent(BigDecimal displayPercent) {
        DisplayPercent = displayPercent;
    }

    public BigDecimal getDisplay_Qty() {
        return Display_Qty;
    }

    public void setDisplay_Qty(BigDecimal display_Qty) {
        Display_Qty = display_Qty;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}
