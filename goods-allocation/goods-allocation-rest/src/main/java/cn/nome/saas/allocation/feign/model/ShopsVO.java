package cn.nome.saas.allocation.feign.model;

import java.util.Date;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class ShopsVO {

    private Integer id;

    private Integer corpId;

    private String shopName;

    private String oldShopCode;

    private String shopCode;

    private Integer marketingAreaId;

    private String firstMarkingAreaName;

    private String secondMarkingAreaName;

    private String marketingAreaName;

    private Integer channelAreaId;

    private String channelAreaName;

    private String plannedOpeningDate;

    private String openingDate;

    private String calcOpeningDate;

    private String plannedClosingDate;

    private String closingDate;

    private String calcClosingDate;

    private String suspendDate;

    private Integer shopStateId;

    private String shopStateName;

    private String shopImage;

    private Date createdAt;

    private Date updatedAt;

    private Long createUserId;

    private Long lastUpdateUserId;

    private String shopBusinessAttribute;

    private String province;
    private String city;
    private String distinct;
    private String address;

    private Boolean enableWechatGroupMarking;

    private String wechatConfigId;

    private String wechatQrCode;

    private String lng;

    private String lat;

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

    public String getFirstMarkingAreaName() {
        return firstMarkingAreaName;
    }

    public void setFirstMarkingAreaName(String firstMarkingAreaName) {
        this.firstMarkingAreaName = firstMarkingAreaName;
    }

    public String getSecondMarkingAreaName() {
        return secondMarkingAreaName;
    }

    public void setSecondMarkingAreaName(String secondMarkingAreaName) {
        this.secondMarkingAreaName = secondMarkingAreaName;
    }

    public String getMarketingAreaName() {
        return marketingAreaName;
    }

    public void setMarketingAreaName(String marketingAreaName) {
        this.marketingAreaName = marketingAreaName;
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

    public String getShopBusinessAttribute() {
        return shopBusinessAttribute;
    }

    public void setShopBusinessAttribute(String shopBusinessAttribute) {
        this.shopBusinessAttribute = shopBusinessAttribute;
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

    public String getDistinct() {
        return distinct;
    }

    public void setDistinct(String distinct) {
        this.distinct = distinct;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getEnableWechatGroupMarking() {
        return enableWechatGroupMarking;
    }

    public void setEnableWechatGroupMarking(Boolean enableWechatGroupMarking) {
        this.enableWechatGroupMarking = enableWechatGroupMarking;
    }

    public String getWechatConfigId() {
        return wechatConfigId;
    }

    public void setWechatConfigId(String wechatConfigId) {
        this.wechatConfigId = wechatConfigId;
    }

    public String getWechatQrCode() {
        return wechatQrCode;
    }

    public void setWechatQrCode(String wechatQrCode) {
        this.wechatQrCode = wechatQrCode;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
