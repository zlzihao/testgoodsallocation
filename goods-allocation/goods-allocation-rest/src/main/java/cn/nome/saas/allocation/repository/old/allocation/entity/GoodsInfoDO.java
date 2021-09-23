package cn.nome.saas.allocation.repository.old.allocation.entity;

import cn.nome.platform.common.utils.ToString;

/**
 * GoodsInfoDO
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
public class GoodsInfoDO extends ToString {

    private String matCode; // skc code

    private String matName;

    private String categoryName;

    private String midCategoryName;

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

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }
}
