package cn.nome.saas.allocation.model.old.issue;

/**
 * NewIssueCategorySkcData
 *
 * @author Bruce01.fan
 * @date 2019/9/28
 */
public class NewIssueCategorySkcData {

    private String shopID;
    private String categoryName;
    private String midCategoryName;

    private Long canSkcCount;//可下SKC数量
    private Long newSkcCount;//新品SKC数量
    private Long keepSkcCount;//保底SKC数量
    private Long prohibitedSkcCount;//禁配SKC数
    private Long validSkcCount;//有效SKC数量

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public Long getCanSkcCount() {
        return canSkcCount;
    }

    public void setCanSkcCount(Long canSkcCount) {
        this.canSkcCount = canSkcCount;
    }

    public Long getNewSkcCount() {
        return newSkcCount;
    }

    public void setNewSkcCount(Long newSkcCount) {
        this.newSkcCount = newSkcCount;
    }

    public Long getKeepSkcCount() {
        return keepSkcCount;
    }

    public void setKeepSkcCount(Long keepSkcCount) {
        this.keepSkcCount = keepSkcCount;
    }

    public Long getProhibitedSkcCount() {
        return prohibitedSkcCount;
    }

    public void setProhibitedSkcCount(Long prohibitedSkcCount) {
        this.prohibitedSkcCount = prohibitedSkcCount;
    }

    public Long getValidSkcCount() {
        return validSkcCount;
    }

    public void setValidSkcCount(Long validSkcCount) {
        this.validSkcCount = validSkcCount;
    }
}
