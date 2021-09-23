package cn.nome.saas.allocation.model.old.issue;

import java.util.List;

public class OrderDetailWrap {

    private List<OrderDetailVo> orderDetailVo;
    private String shopName;
    private String shopId;
    private Integer total;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public List<OrderDetailVo> getOrderDetailVo() {
        return orderDetailVo;
    }

    public void setOrderDetailVo(List<OrderDetailVo> orderDetailVo) {
        this.orderDetailVo = orderDetailVo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
