package cn.nome.saas.allocation.model.old.issue;

import java.util.List;

/**
 * MatDetailTree
 *
 * @author Bruce01.fan
 * @date 2019/9/26
 */
public class MatDetailTree {

    String shopId;

    String shopCode;

    String shopName;

    MatCategoryDetailVo parent;

    List<MatCategoryDetailVo> childList;

    //大类使用字段
    private double totalOrderAmt;
    private double totalOrderQty;


    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public MatCategoryDetailVo getParent() {
        return parent;
    }

    public void setParent(MatCategoryDetailVo parent) {
        this.parent = parent;
    }

    public List<MatCategoryDetailVo> getChildList() {
        return childList;
    }

    public void setChildList(List<MatCategoryDetailVo> childList) {
        this.childList = childList;
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
