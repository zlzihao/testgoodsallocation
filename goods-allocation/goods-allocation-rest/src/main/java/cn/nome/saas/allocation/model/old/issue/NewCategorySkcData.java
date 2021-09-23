package cn.nome.saas.allocation.model.old.issue;

/**
 * NewCategorySkcData
 *
 * @author Bruce01.fan
 * @date 2019/9/28
 */
public class NewCategorySkcData {

    private String shopId;

    private String categoryName;

    private String midCategoryName;

    private long count;

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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getKey() {
        return this.shopId+":"+this.categoryName;
    }

    public String getMidKey() {
        return this.shopId+":"+this.categoryName+":"+this.midCategoryName;
    }
}
