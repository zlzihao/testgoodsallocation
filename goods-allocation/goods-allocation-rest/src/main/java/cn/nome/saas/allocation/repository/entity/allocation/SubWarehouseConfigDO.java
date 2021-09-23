package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * TaskDO
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class SubWarehouseConfigDO extends ToString{

    private Integer id;
    /**
     * 仓库id
     */
    private String warehouseCode;
    /**
     * 仓库代码
     */
    private String warehouseCode1;
    private Integer priority;
    private String region;
    private String province;
    private String city;
    private String saleLv;
    private String displayLv;
    private String shopId;
    private String shopStatus;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

    public String getWarehouseCode1() {
        return warehouseCode1;
    }

    public void setWarehouseCode1(String warehouseCode1) {
        this.warehouseCode1 = warehouseCode1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSaleLv() {
        return saleLv;
    }

    public void setSaleLv(String saleLv) {
        this.saleLv = saleLv;
    }

    public String getDisplayLv() {
        return displayLv;
    }

    public void setDisplayLv(String displayLv) {
        this.displayLv = displayLv;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
