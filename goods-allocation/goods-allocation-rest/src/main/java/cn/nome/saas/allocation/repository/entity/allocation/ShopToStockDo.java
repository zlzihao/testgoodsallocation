package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.excel.annotation.Column;
import cn.nome.platform.common.utils.excel.converter.impl.BigDecimalConverter;

import java.math.BigDecimal;
import java.util.Date;

/*
 * @describe  调整陈列类目为后台类目
 * @author  lizihao@nomo.com
 * */
public class ShopToStockDo {
    private Integer id;
    private String orderNo;
    @Column(value = "门店代码", num = 0)
    private String shopCode;
    @Column(value = "门店名称", num = 1)
    private String shopName;
    @Column(value = "后台类目大类", num = 2)
    private String categoryName;
    @Column(value = "后台类目中类", num = 3)
    private String midCategoryName;
    @Column(value = "原仓位数", num = 4, classConvertor = BigDecimalConverter.class)
    private BigDecimal oldStockNum;
    @Column(value = "新仓位数", num = 5, classConvertor = BigDecimalConverter.class)
    private BigDecimal newStockNum;
    //@Column(value="申请日期", num=7, format = "yyyy-mm-dd hh:mm:ss")
    private Date date;
    //@Column(value="申请人", num=8)
    private String userName;
    //@Column(value="是否审核", num=9, classConvertor = ShopToStockStatusConverter.class)
    private int status;
    //@Column(value="调整理由", num=10)
    private String reason;

    private Integer channelAreaId;

    private String channelAreaName;

    private String provinceAndArea;

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

    public Integer getChannelAreaId() {
        return channelAreaId;
    }

    public void setChannelAreaId(Integer channelAreaId) {
        this.channelAreaId = channelAreaId;
    }

    public String getChannelAreaName() {
        return channelAreaName;
    }

    public void setChannelAreaName(String channelAreaName) {
        this.channelAreaName = channelAreaName;
    }

    public String getProvinceAndArea() {
        return provinceAndArea;
    }

    public void setProvinceAndArea(String provinceAndArea) {
        this.provinceAndArea = provinceAndArea;
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
