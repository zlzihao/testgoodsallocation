package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;
import java.util.List;

/**
 * ForbiddenRule
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public class ForbiddenRuleDO extends GlobalConfigRuleDO{

    public static final int GLOBAL_TYPE = 1;

    public static final int SINGLE_TYPE = 2;

    public static final int STATUS_FORBIDDEN = 1;
    public static final int STATUS_UNABLE_FORBIDDEN = 2;
    public static final int STATUS_SECURITY = 3;
    public static final int STATUS_WHITE_LIST = 4;
    public static final int STATUS_SECURITY_AND_WHITE_LIST = 5;

    private Integer id;

    private String name;

    private Date startDate;

    private Date endDate;

    private int type; // 1-全局 2-单店

    private int status;

    private Integer syncId;

    private Date createdAt;

    private String createdBy;

    private Date updatedAt;

    private String updatedBy;

    private List<ForbiddenSingleItemDO> singleList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getSyncId() {
        return syncId;
    }

    public void setSyncId(Integer syncId) {
        this.syncId = syncId;
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

    public List<ForbiddenSingleItemDO> getSingleList() {
        return singleList;
    }

    public void setSingleList(List<ForbiddenSingleItemDO> singleList) {
        this.singleList = singleList;
    }
}
