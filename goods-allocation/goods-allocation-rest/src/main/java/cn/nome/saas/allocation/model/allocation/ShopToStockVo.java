package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Shop
 *
 * @author Bruce01.fan
 * @date 2019/7/2
 */
public class ShopToStockVo extends ToString {
    private Integer id;
    private String shopCode;
    private String shopName;
    private String categoryName;
    private String midCategoryName;
    private BigDecimal oldStockNum;
    private BigDecimal newStockNum;
    private Date date;
    private String userName;
    private String orderNo;
    private int status;
    private String reason;
    private Integer isNewStatus;

    // 2021-4-26 新增类目id
    private Integer categoryId;
    private Integer midCategoryId;
    //2021-7-713
    private Integer type;
    private Integer operateId;
    private Integer isDeleted;
    private Integer saveStatus;
    private Integer isRead;
    private String remark;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getMidCategoryId() {
        return midCategoryId;
    }

    public void setMidCategoryId(Integer midCategoryId) {
        this.midCategoryId = midCategoryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getOldStockNum() {
        return oldStockNum;
    }

    public void setOldStockNum(BigDecimal oldStockNum) {
        this.oldStockNum = oldStockNum;
    }

    public BigDecimal getNewStockNum() {
        return newStockNum;
    }

    public void setNewStockNum(BigDecimal newStockNum) {
        this.newStockNum = newStockNum;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getIsNewStatus() {
        return isNewStatus;
    }

    public void setIsNewStatus(Integer isNewStatus) {
        this.isNewStatus = isNewStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(Integer saveStatus) {
        this.saveStatus = saveStatus;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
