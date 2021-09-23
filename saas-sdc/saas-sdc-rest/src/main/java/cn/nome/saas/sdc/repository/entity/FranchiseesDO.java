package cn.nome.saas.sdc.repository.entity;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 13:54
 */
public class FranchiseesDO extends ToString {


    /**
     *
     **/
    private Integer id;


    /**
     * 企业ID
     **/
    private Integer corpId;

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
