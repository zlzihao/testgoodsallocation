package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * SubWarehouseShopMappingDO
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
public class SubWarehouseShopMappingDO extends ToString{

    private Integer id;
    /**
     * 仓库id
     */
    private String warehouseCode;
    private Integer priority;
    private String shopId;
    private Date createdAt;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
