package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * QdIssueShopListDO
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueShopListDO {

    private Integer id;

    private String areaName;

    private String regionName;

    private String provinceName;

    private String shopCode;

    private String shopId;

    private String shopName;

    private int businessLevel; // 1 2,3,4

    private String areaLevel; // A,B,C,D

    private int skcWidthSuggest;

    private Double oldSkcPercentageSuggest;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getBusinessLevel() {
        return businessLevel;
    }

    public void setBusinessLevel(int businessLevel) {
        this.businessLevel = businessLevel;
    }

    public int getSkcWidthSuggest() {
        return skcWidthSuggest;
    }

    public void setSkcWidthSuggest(int skcWidthSuggest) {
        this.skcWidthSuggest = skcWidthSuggest;
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public Double getOldSkcPercentageSuggest() {
        return oldSkcPercentageSuggest;
    }

    public void setOldSkcPercentageSuggest(Double oldSkcPercentageSuggest) {
        this.oldSkcPercentageSuggest = oldSkcPercentageSuggest;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
