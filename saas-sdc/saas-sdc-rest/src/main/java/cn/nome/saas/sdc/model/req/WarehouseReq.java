package cn.nome.saas.sdc.model.req;

import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class WarehouseReq {
    private Long id;
    private String province;
    private String warehouse;
    private Integer createUserCode;
    private Integer updatedUserCode;
    private Date gmtCreate;
    private Date gmtUpdated;
    private Integer isDeleted;

    public WarehouseReq() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getUpdatedUserCode() {
        return updatedUserCode;
    }

    public void setUpdatedUserCode(Integer updatedUserCode) {
        this.updatedUserCode = updatedUserCode;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtUpdated() {
        return gmtUpdated;
    }

    public void setGmtUpdated(Date gmtUpdated) {
        this.gmtUpdated = gmtUpdated;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
