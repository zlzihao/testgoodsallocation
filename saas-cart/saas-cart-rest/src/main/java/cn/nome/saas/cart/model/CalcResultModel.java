package cn.nome.saas.cart.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author chentaikuang
 */
public class CalcResultModel implements Serializable {
    private static final long serialVersionUID = 8182966499880096903L;
    // 总金额
    private double totalAmount;
    // 优惠金额
    private double discAmount;
    private List<ActiveSkuDetail> activeDetail;
    private List<String> inactiveSku;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscAmount() {
        return discAmount;
    }

    public void setDiscAmount(double discAmount) {
        this.discAmount = discAmount;
    }

    public List<ActiveSkuDetail> getActiveDetail() {
        return activeDetail;
    }

    public void setActiveDetail(List<ActiveSkuDetail> activeDetail) {
        this.activeDetail = activeDetail;
    }

    public List<String> getInactiveSku() {
        return inactiveSku;
    }

    public void setInactiveSku(List<String> inactiveSku) {
        this.inactiveSku = inactiveSku;
    }
}
