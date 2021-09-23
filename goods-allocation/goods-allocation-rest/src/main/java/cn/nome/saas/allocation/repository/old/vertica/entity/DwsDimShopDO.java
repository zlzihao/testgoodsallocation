package cn.nome.saas.allocation.repository.old.vertica.entity;

import cn.nome.platform.common.utils.ToString;

/**
 * DimShop
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public class DwsDimShopDO extends ToString {

    private String shopCode;

    private String shopId;

    private String shopName;

    private String regioneNo;

    private String regioneName;

    private String regioneBusName;

    private String subRegioneBusName;

    private String provinceId;

    private String provinceCode;

    private String provinceName;

    private String cityId;

    private String cityCode;

    private String cityName;

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

    public String getRegioneNo() {
        return regioneNo;
    }

    public void setRegioneNo(String regioneNo) {
        this.regioneNo = regioneNo;
    }

    public String getRegioneName() {
        return regioneName;
    }

    public void setRegioneName(String regioneName) {
        this.regioneName = regioneName;
    }

    public String getRegioneBusName() {
        return regioneBusName;
    }

    public void setRegioneBusName(String regioneBusName) {
        this.regioneBusName = regioneBusName;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSubRegioneBusName() {
        return subRegioneBusName;
    }

    public void setSubRegioneBusName(String subRegioneBusName) {
        this.subRegioneBusName = subRegioneBusName;
    }
}
