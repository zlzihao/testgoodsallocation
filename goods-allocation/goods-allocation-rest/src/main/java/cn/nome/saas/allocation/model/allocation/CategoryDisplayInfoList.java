package cn.nome.saas.allocation.model.allocation;

import cn.nome.platform.common.utils.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Shop
 *
 * @author Bruce01.fan
 * @date 2019/7/2
 */
public class CategoryDisplayInfoList extends ToString {

    private String categoryName;
    private BigDecimal oldStockNum;
    private BigDecimal newStockNum;
    private List<MidCategoryDisplayInfoList> midCategoryDisplayInfoList;

    public List<MidCategoryDisplayInfoList> getMidCategoryDisplayInfoList() {
        return midCategoryDisplayInfoList;
    }

    public void setMidCategoryDisplayInfoList(List<MidCategoryDisplayInfoList> midCategoryDisplayInfoList) {
        this.midCategoryDisplayInfoList = midCategoryDisplayInfoList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getOldStockNum() {
        return oldStockNum;
    }

    public void setOldStockNum(BigDecimal oldStockNum) {
        this.oldStockNum = oldStockNum;
    }

    public BigDecimal getNewStockNum() {
        return newStockNum;
    }

    public void setNewStockNum(BigDecimal newStockNum) {
        this.newStockNum = newStockNum;
    }

}
