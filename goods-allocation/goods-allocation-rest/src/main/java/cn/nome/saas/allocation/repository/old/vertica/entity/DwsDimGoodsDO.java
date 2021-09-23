package cn.nome.saas.allocation.repository.old.vertica.entity;

import cn.nome.platform.common.utils.ToString;

/**
 * DimGoods
 *
 * @author Bruce01.fan
 * @date 2019/5/25
 */
public class DwsDimGoodsDO extends ToString {

    private String matCode; // skc code

    private String matName;

    private String sizeId;

    private String categoryCode;

    private String categoryName;

    private String midCategoryCode;

    private String midCategoryName;

    private String smallCategoryCode;

    private String smallCategoryName;

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMidCategoryCode() {
        return midCategoryCode;
    }

    public void setMidCategoryCode(String midCategoryCode) {
        this.midCategoryCode = midCategoryCode;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public String getSmallCategoryCode() {
        return smallCategoryCode;
    }

    public void setSmallCategoryCode(String smallCategoryCode) {
        this.smallCategoryCode = smallCategoryCode;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }
}
