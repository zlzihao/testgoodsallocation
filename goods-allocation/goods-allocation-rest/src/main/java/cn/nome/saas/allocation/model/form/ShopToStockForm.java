package cn.nome.saas.allocation.model.form;

import cn.nome.platform.common.utils.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Shop
 *
 * @author Bruce01.fan
 * @date 2019/7/2
 */
public class ShopToStockForm extends ToString {
    private Integer id;
    @NotBlank(message = "门店编码不能为空")
    private String shopCode;
    private String shopName;
    @NotBlank(message = "大类名称不能为空")
    private String categoryName;
    @NotBlank(message = "中类名称不能为空")
    private String midCategoryName;
    @NotNull(message = "原仓位数不能为空")
    private BigDecimal oldStockNum;
    @NotNull(message = "最新仓位数不能为空")
    private BigDecimal newStockNum;
    private Date date;
    private String userName;
    private String orderNo;
    private int status;
    private String reason;

    // 2021-4-26 新增类目id
    private Integer categoryId;
    private Integer midCategoryId;

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
}
