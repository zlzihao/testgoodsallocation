package cn.nome.saas.cart.model;

import java.util.List;

/**
 * 封装购物车结算返回
 *
 * @author chentaikuang
 */
public class CartCalcWrap {

    // 总金额
    private Integer totalAmount = 0;
    // 总优惠
    private Integer totalFavor = 0;
    //满减
    private List<FullReduceVO> fullReduce;
    //折扣
    private List<DisctVO> discount;

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalFavor() {
        return totalFavor;
    }

    public void setTotalFavor(Integer totalFavor) {
        this.totalFavor = totalFavor;
    }

    public List<FullReduceVO> getFullReduce() {
        return fullReduce;
    }

    public void setFullReduce(List<FullReduceVO> fullReduce) {
        this.fullReduce = fullReduce;
    }

    public List<DisctVO> getDiscount() {
        return discount;
    }

    public void setDiscount(List<DisctVO> discount) {
        this.discount = discount;
    }
}
