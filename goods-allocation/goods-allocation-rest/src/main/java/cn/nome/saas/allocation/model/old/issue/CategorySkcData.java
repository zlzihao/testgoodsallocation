package cn.nome.saas.allocation.model.old.issue;

/**
 * @author chentaikuang
 */
public class CategorySkcData {

    private String CategoryName;
    private String MidCategoryName;
    private int SkcCount;

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getMidCategoryName() {
        return MidCategoryName;
    }

    public void setMidCategoryName(String midCategoryName) {
        MidCategoryName = midCategoryName;
    }

    public int getSkcCount() {
        return SkcCount;
    }

    public void setSkcCount(int skcCount) {
        SkcCount = skcCount;
    }
}
