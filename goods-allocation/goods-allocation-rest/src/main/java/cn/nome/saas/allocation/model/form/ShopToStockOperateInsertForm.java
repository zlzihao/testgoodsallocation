package cn.nome.saas.allocation.model.form;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Shop
 *
 * @author Bruce01.fan
 * @date 2019/7/2
 */
public class ShopToStockOperateInsertForm extends ToString {
    private Integer id;
    private String reason;
    private String remark;
    private Integer status;
    private Integer type;
    private Integer isRead;
    private BigDecimal sumStockCount;
    private String userName;
    private Date createTime;
    private Date updateTime;
    private Integer createAt;
    private Integer updateAt;
    private List<ShopToStockForm> shopForms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public BigDecimal getSumStockCount() {
        return sumStockCount;
    }

    public void setSumStockCount(BigDecimal sumStockCount) {
        this.sumStockCount = sumStockCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Integer createAt) {
        this.createAt = createAt;
    }

    public Integer getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Integer updateAt) {
        this.updateAt = updateAt;
    }

    public List<ShopToStockForm> getShopForms() {
        return shopForms;
    }

    public void setShopForms(List<ShopToStockForm> shopForms) {
        this.shopForms = shopForms;
    }
}
