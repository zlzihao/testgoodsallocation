package cn.nome.saas.allocation.model.issue;

import java.math.BigDecimal;

public class DisplayDataV2 {

    private String CategoryName;
    private String MidCategoryName;
    private String SmallCategoryName;
    private BigDecimal MidDisplayDepth;
    private BigDecimal DisplayDepth;
    private BigDecimal Columns;
    private BigDecimal Rows;
    private BigDecimal Depth;
    private BigDecimal QtyWeight = new BigDecimal(0);

    private String Operator;

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

    public BigDecimal getMidDisplayDepth() {
        return MidDisplayDepth;
    }

    public void setMidDisplayDepth(BigDecimal midDisplayDepth) {
        MidDisplayDepth = midDisplayDepth;
    }

    public BigDecimal getDisplayDepth() {
        return DisplayDepth;
    }

    public void setDisplayDepth(BigDecimal displayDepth) {
        DisplayDepth = displayDepth;
    }

    public BigDecimal getColumns() {
        return Columns;
    }

    public void setColumns(BigDecimal columns) {
        Columns = columns;
    }

    public BigDecimal getRows() {
        return Rows;
    }

    public void setRows(BigDecimal rows) {
        Rows = rows;
    }

    public BigDecimal getDepth() {
        return Depth;
    }

    public void setDepth(BigDecimal depth) {
        Depth = depth;
    }

    public BigDecimal getQtyWeight() {
        return QtyWeight;
    }

    public void setQtyWeight(BigDecimal qtyWeight) {
        QtyWeight = qtyWeight;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }
}
