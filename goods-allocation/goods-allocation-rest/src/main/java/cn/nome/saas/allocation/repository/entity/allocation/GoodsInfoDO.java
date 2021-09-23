package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * GoodsInfoDO
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
public class GoodsInfoDO extends ToString{

    private String matId;
    // skc code
    private String matCode;

    private String matName;

    private String categoryName;
    private String categoryCode;

    private String midCategoryName;
    private String midCategoryCode;


    private String smallCategoryName;
    private String smallCategoryCode;

    /**
     * 是否禁止调拨
     */
    private Integer isAllocationProhibited;

    /**m
     * 配货规格
     */
    private Integer minPackageQty;

    /**
     * 坑位深度
     */
    private Integer displayDepth;
    /**
     * 货区
     */
    private String area;
    /**
     * 货盘等级
     */
    private String level;


    private String operator;
    /**
     * 创建时间
     */
    private Date createdAt;
    private Date updatedAt;

    /**
     * 单号
     */
    private String orderNo;

    public String getMatId() {
        return matId;
    }

    public void setMatId(String matId) {
        this.matId = matId;
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

    public Integer getIsAllocationProhibited() {
        return isAllocationProhibited;
    }

    public void setIsAllocationProhibited(Integer isAllocationProhibited) {
        this.isAllocationProhibited = isAllocationProhibited;
    }

    public Integer getMinPackageQty() {
        return minPackageQty;
    }

    public void setMinPackageQty(Integer minPackageQty) {
        this.minPackageQty = minPackageQty;
    }

    public Integer getDisplayDepth() {
        return displayDepth;
    }

    public void setDisplayDepth(Integer displayDepth) {
        this.displayDepth = displayDepth;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getMidCategoryCode() {
        return midCategoryCode;
    }

    public void setMidCategoryCode(String midCategoryCode) {
        this.midCategoryCode = midCategoryCode;
    }

    public String getSmallCategoryCode() {
        return smallCategoryCode;
    }

    public void setSmallCategoryCode(String smallCategoryCode) {
        this.smallCategoryCode = smallCategoryCode;
    }
}
