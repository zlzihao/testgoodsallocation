package cn.nome.saas.allocation.repository.entity.allocation;

import java.math.BigDecimal;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/14 10:03
 * @description：商品扩展实例，用于秋冬老品统计
 * @modified By：
 * @version: 1.0.0$
 */
public class DwsDimGoodsExDO {
    /**
     * 大类
     */
    private String CategoryName;

    /**
     * 中类
     */
    private String midCategoryName;

    /**
     * 均价
     */
    private BigDecimal avgPrice;

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }
}
