package cn.nome.saas.sdc.repository.entity;


import cn.nome.platform.common.utils.ToString;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class ShopsDO extends ToString {
    /**
     *
     **/
    private Integer id;

    /**
     * 企业ID
     **/
    private Integer corpId;

    /**
     * 店铺名称
     **/
    private String shopName;

    /**
     * 旧门店编号
     */
    private String oldShopCode;

    /**
     * 门店编号
     **/
    private String shopCode;

    /**
     * 营销区域ID
     **/
    private Integer marketingAreaId;

    /**
     * 渠道区域ID
     **/
    private Integer channelAreaId;

    /**
     * 计划开业日期
     **/
    private String plannedOpeningDate;

    /**
     * 开业日期
     **/
    private String openingDate;

    /**
     * 计算开业日期
     */
    private String calcOpeningDate;

    /**
     * 计划结业日期
     **/
    private String plannedClosingDate;

    /**
     * 结业日期
     **/
    private String closingDate;

    /**
     * 计算结业日期
     */
    private String calcClosingDate;

    /**
     * 暂停时间
     **/
    private String suspendDate;

    /**
     * 店铺状态ID
     **/
    private Integer shopStateId;

    /**
     * 店铺状态
     **/
    private String shopStateName;

    /**
     * 店铺图像
     **/
    private String shopImage;

    /**
     * 是否开启服装配补计算，1-开启，0-不开启
     */
    private Integer enableClothingAllocation;

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

    private String channelAreaName;

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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOldShopCode() {
        return oldShopCode;
    }

    public void setOldShopCode(String oldShopCode) {
        this.oldShopCode = oldShopCode;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public Integer getMarketingAreaId() {
        return marketingAreaId;
    }

    public void setMarketingAreaId(Integer marketingAreaId) {
        this.marketingAreaId = marketingAreaId;
    }

    public Integer getChannelAreaId() {
        return channelAreaId;
    }

    public void setChannelAreaId(Integer channelAreaId) {
        this.channelAreaId = channelAreaId;
    }

    public String getPlannedOpeningDate() {
        return plannedOpeningDate;
    }

    public void setPlannedOpeningDate(String plannedOpeningDate) {
        this.plannedOpeningDate = plannedOpeningDate;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getCalcOpeningDate() {
        return calcOpeningDate;
    }

    public void setCalcOpeningDate(String calcOpeningDate) {
        this.calcOpeningDate = calcOpeningDate;
    }

    public String getPlannedClosingDate() {
        return plannedClosingDate;
    }

    public void setPlannedClosingDate(String plannedClosingDate) {
        this.plannedClosingDate = plannedClosingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public String getCalcClosingDate() {
        return calcClosingDate;
    }

    public void setCalcClosingDate(String calcClosingDate) {
        this.calcClosingDate = calcClosingDate;
    }

    public String getSuspendDate() {
        return suspendDate;
    }

    public void setSuspendDate(String suspendDate) {
        this.suspendDate = suspendDate;
    }

    public Integer getShopStateId() {
        return shopStateId;
    }

    public void setShopStateId(Integer shopStateId) {
        this.shopStateId = shopStateId;
    }

    public String getShopStateName() {
        return shopStateName;
    }

    public void setShopStateName(String shopStateName) {
        this.shopStateName = shopStateName;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public Integer getEnableClothingAllocation() {
        return enableClothingAllocation;
    }

    public void setEnableClothingAllocation(Integer enableClothingAllocation) {
        this.enableClothingAllocation = enableClothingAllocation;
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

    public String getChannelAreaName() {
        return channelAreaName;
    }

    public void setChannelAreaName(String channelAreaName) {
        this.channelAreaName = channelAreaName;
    }
}
