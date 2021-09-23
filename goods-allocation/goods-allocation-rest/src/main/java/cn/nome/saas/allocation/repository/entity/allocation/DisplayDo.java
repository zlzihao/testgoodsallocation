package cn.nome.saas.allocation.repository.entity.allocation;

import java.math.BigDecimal;
import java.util.Date;

public class DisplayDo {

    private String categoryCode;
    private String categoryName;
    private String midCategoryCode;
    private String midCategoryName;
    private String smallCategoryCode;
    private String smallCategoryName;
    private BigDecimal midDisplayDepth;
    private BigDecimal displayDepth;
    private BigDecimal columns;
    private BigDecimal rows;
    private BigDecimal depth;
    private BigDecimal qtyWeight;
    private String operator;
    private Date createdAt;
    private Date updatedAt;


    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryCode() {
        return midCategoryCode;
    }

    public void setMidCategoryCode(String midCategoryCode) {
        this.midCategoryCode = midCategoryCode;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getSmallCategoryCode() {
        return smallCategoryCode;
    }

    public void setSmallCategoryCode(String smallCategoryCode) {
        this.smallCategoryCode = smallCategoryCode;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public BigDecimal getMidDisplayDepth() {
        return midDisplayDepth;
    }

    public void setMidDisplayDepth(BigDecimal midDisplayDepth) {
        this.midDisplayDepth = midDisplayDepth;
    }

    public BigDecimal getDisplayDepth() {
        return displayDepth;
    }

    public void setDisplayDepth(BigDecimal displayDepth) {
        this.displayDepth = displayDepth;
    }

    public BigDecimal getColumns() {
        return columns;
    }

    public void setColumns(BigDecimal columns) {
        this.columns = columns;
    }

    public BigDecimal getRows() {
        return rows;
    }

    public void setRows(BigDecimal rows) {
        this.rows = rows;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    public BigDecimal getQtyWeight() {
        return qtyWeight;
    }

    public void setQtyWeight(BigDecimal qtyWeight) {
        this.qtyWeight = qtyWeight;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
