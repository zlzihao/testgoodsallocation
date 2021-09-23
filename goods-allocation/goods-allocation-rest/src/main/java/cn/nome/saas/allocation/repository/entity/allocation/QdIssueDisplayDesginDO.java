package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;

/**
 * QdIssueSkcListDO
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public class QdIssueDisplayDesginDO {

    private Integer id;

    private String regionName;

    private String provinceName;

    private String shopCode;

    private String shopId;

    private String shopName;

    private int maleStandardSkc;

    private int femaleStandardSkc;

    private int clothingSkc;

    private double displayRatio; // 陈列系数

    private double newDisplayRatio; // 新品陈列系数

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getMaleStandardSkc() {
        return maleStandardSkc;
    }

    public void setMaleStandardSkc(int maleStandardSkc) {
        this.maleStandardSkc = maleStandardSkc;
    }

    public int getFemaleStandardSkc() {
        return femaleStandardSkc;
    }

    public void setFemaleStandardSkc(int femaleStandardSkc) {
        this.femaleStandardSkc = femaleStandardSkc;
    }

    public int getClothingSkc() {
        return clothingSkc;
    }

    public void setClothingSkc(int clothingSkc) {
        this.clothingSkc = clothingSkc;
    }

    public double getDisplayRatio() {
        return displayRatio;
    }

    public void setDisplayRatio(double displayRatio) {
        this.displayRatio = displayRatio;
    }

    public double getNewDisplayRatio() {
        return newDisplayRatio;
    }

    public void setNewDisplayRatio(double newDisplayRatio) {
        this.newDisplayRatio = newDisplayRatio;
    }
}
