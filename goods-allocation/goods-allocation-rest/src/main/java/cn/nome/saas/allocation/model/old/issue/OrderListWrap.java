package cn.nome.saas.allocation.model.old.issue;

import java.util.List;

public class OrderListWrap {
    private List<OrderListVo> orderList;
    private int curPage;
    private int totalCount;
    private int totalPage;

    public List<OrderListVo> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderListVo> orderList) {
        this.orderList = orderList;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String toString() {
        return "{" +
                "orderList=" + orderList +
                ", curPage=" + curPage +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                '}';
    }
}
