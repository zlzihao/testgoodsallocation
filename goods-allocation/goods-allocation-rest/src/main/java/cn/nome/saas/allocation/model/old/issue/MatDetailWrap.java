package cn.nome.saas.allocation.model.old.issue;

import java.util.List;

public class MatDetailWrap {
    private String shopName;
    private List<MatCategoryDetailVo> categoryVo;

    //大类使用字段
    private double totalOrderAmt;
    private double totalOrderQty;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<MatCategoryDetailVo> getCategoryVo() {
        return categoryVo;
    }

    public void setCategoryVo(List<MatCategoryDetailVo> categoryVo) {
        this.categoryVo = categoryVo;
    }

    public double getTotalOrderAmt() {
        return totalOrderAmt;
    }

    public void setTotalOrderAmt(double totalOrderAmt) {
        this.totalOrderAmt = totalOrderAmt;
    }

    public double getTotalOrderQty() {
        return totalOrderQty;
    }

    public void setTotalOrderQty(double totalOrderQty) {
        this.totalOrderQty = totalOrderQty;
    }
}
