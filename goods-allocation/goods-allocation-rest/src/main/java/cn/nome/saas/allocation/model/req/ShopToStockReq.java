package cn.nome.saas.allocation.model.req;

import cn.nome.platform.common.utils.ToString;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
public class ShopToStockReq extends ToString {
    private String shopCode;
    private String shopName;
    private List<String> categoryName;
    private List<String> midCategoryName;
    private Integer status;
    private Integer isNewStatus;
    private String orderNo;
    private List<String> shopNameReq;
    private List<Integer> categoryIds;
    private List<Integer> midCategoryIds;
    //2021-7-13 operate_change_stock_count id
    private Integer operateId;

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Integer> getMidCategoryIds() {
        return midCategoryIds;
    }

    public void setMidCategoryIds(List<Integer> midCategoryIds) {
        this.midCategoryIds = midCategoryIds;
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

    public List<String> getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(List<String> categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getMidCategoryName() {
        return midCategoryName;
    }

    public void setMidCategoryName(List<String> midCategoryName) {
        this.midCategoryName = midCategoryName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsNewStatus() {
        return isNewStatus;
    }

    public void setIsNewStatus(Integer isNewStatus) {
        this.isNewStatus = isNewStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<String> getShopNameReq() {
        return shopNameReq;
    }

    public void setShopNameReq(List<String> shopNameReq) {
        this.shopNameReq = shopNameReq;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }
}
