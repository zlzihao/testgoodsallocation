package cn.nome.saas.sdc.repository.entity;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class AreasDO extends ToString {


    /**
     *
     **/
    private Integer id;

    private Integer corpId;

    /**
     * 区域类型
     **/
    private Integer areaTypeId;

    /**
     * 父级ID
     **/
    private Integer parentId;

    /**
     * 区域名称
     **/
    private String areaName;

    /**
     * 区域编码
     **/
    private String areaCode;

    /**
     * 区域编码说明
     **/
    private String areaCodeDescription;

    /**
     * 地区位置(对应省份)
     **/
    private String locations;

    /**
     * 区域经理
     **/
    private String areaManager;

    /**
     * 区域经理工号
     **/
    private String areaManagerJobNumber;

    /**
     * 创建时间
     **/
    private Date createdAt;

    /**
     * 更新时间
     **/
    private Date updatedAt;

    /**
     * 创建用户ID
     **/
    private Long createUserId;

    /**
     * 最后更新用户ID
     **/
    private Long lastUpdateUserId;

    /**
     * 是否删除，1-已删除，0-未删除
     **/
    private Integer isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getAreaTypeId() {
        return areaTypeId;
    }

    public void setAreaTypeId(Integer areaTypeId) {
        this.areaTypeId = areaTypeId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaCodeDescription() {
        return areaCodeDescription;
    }

    public void setAreaCodeDescription(String areaCodeDescription) {
        this.areaCodeDescription = areaCodeDescription;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getAreaManager() {
        return areaManager;
    }

    public void setAreaManager(String areaManager) {
        this.areaManager = areaManager;
    }

    public String getAreaManagerJobNumber() {
        return areaManagerJobNumber;
    }

    public void setAreaManagerJobNumber(String areaManagerJobNumber) {
        this.areaManagerJobNumber = areaManagerJobNumber;
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

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}
