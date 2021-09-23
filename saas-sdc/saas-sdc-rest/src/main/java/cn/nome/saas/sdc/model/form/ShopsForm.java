package cn.nome.saas.sdc.model.form;

import cn.nome.platform.common.utils.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class ShopsForm extends ToString {


    @NotNull(message = "店铺ID不能为空")
    private Integer id;

    private Integer corpId;

    @Length(max = 256, min = 0, message = "店铺名称字符长度不能超过256")
    private String shopName;

    @Length(max = 64, min = 0, message = "门店编号字符长度不能超过64")
    private String shopCode;

    private Integer marketingAreaId;

    private Integer channelAreaId;

    @Length(max = 10, min = 0, message = "计划开业日期字符长度不能超过10")
    private String plannedOpeningDate;

    @Length(max = 10, min = 0, message = "开业日期字符长度不能超过10")
    private String openingDate;

    @Length(max = 10, min = 0, message = "计划关门日期字符长度不能超过10")
    private String plannedClosingDate;

    @Length(max = 10, min = 0, message = "关门日期字符长度不能超过10")
    private String closingDate;

    @Length(max = 10, min = 0, message = "停业日期字符长度不能超过10")
    private String outOfDate;

    private Integer shopStateId;

    @Length(max = 32, min = 0, message = "店铺状态字符长度不能超过32")
    private String shopStateName;

    @Length(max = 256, min = 0, message = "店铺图像字符长度不能超过256")
    private String shopImage;

    private Long lastUpdateUserId;

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

    public String getOutOfDate() {
        return outOfDate;
    }

    public void setOutOfDate(String outOfDate) {
        this.outOfDate = outOfDate;
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

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

}