package cn.nome.saas.allocation.model.old.issue;

/**
 * @author chentaikuang
 */
public class OrderModifyWrap {

    private String saleDays;
    private int orderQty = 0;

    public String getSaleDays() {
        return saleDays;
    }

    public void setSaleDays(String saleDays) {
        this.saleDays = saleDays;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }
}
