package cn.nome.saas.sdc.model.req;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class ShopsReq extends ToString {

    private List<Integer> ids;

    private Integer id;

    private Integer corpId;

    private String shopName;

    private String oldShopCode;

    private String shopCode;

    private Integer marketingAreaId;

    private List<Integer> marketingAreaIds;

    private Integer channelAreaId;

    private List<Integer> channelAreaIds;

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

    private Integer isDeleted;

    private String shopBusinessAttribute;

    private String province;

    private String city;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

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

    public List<Integer> getMarketingAreaIds() {
        return marketingAreaIds;
    }

    public void setMarketingAreaIds(List<Integer> marketingAreaIds) {
        this.marketingAreaIds = marketingAreaIds;
    }

    public List<Integer> getChannelAreaIds() {
        return channelAreaIds;
    }

    public void setChannelAreaIds(List<Integer> channelAreaIds) {
        this.channelAreaIds = channelAreaIds;
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
}
