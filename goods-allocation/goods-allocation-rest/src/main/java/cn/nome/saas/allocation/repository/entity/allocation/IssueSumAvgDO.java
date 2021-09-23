package cn.nome.saas.allocation.repository.entity.allocation;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * IssueNeedStockDO
 *
 * @author Bruce01.fan
 * @date 2019/7/25
 */
public class IssueSumAvgDO {

    private String shopId;
    private String matCode;
    private String sizeId;
    private String sizeName;

    private String categoryName;

    private String midCategoryName;

    private String smallCategoryName;

    private double sumAvg;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public double getSumAvg() {
        return sumAvg;
    }

    public void setSumAvg(double sumAvg) {
        this.sumAvg = sumAvg;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}
