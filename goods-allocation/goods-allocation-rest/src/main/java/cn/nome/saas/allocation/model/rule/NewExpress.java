package cn.nome.saas.allocation.model.rule;

/**
 * NewExpress
 *
 * @author Bruce01.fan
 * @date 2019/10/15
 */
public class NewExpress {

    private String fromCity;

    private String toCity;

    private Double shipping;

    private Double addShipping;

    private Double shipping20kg;

    private Double addShipping20kg;

    private String spendTime;

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public Double getAddShipping() {
        return addShipping;
    }

    public void setAddShipping(Double addShipping) {
        this.addShipping = addShipping;
    }

    public Double getShipping20kg() {
        return shipping20kg;
    }

    public void setShipping20kg(Double shipping20kg) {
        this.shipping20kg = shipping20kg;
    }

    public Double getAddShipping20kg() {
        return addShipping20kg;
    }

    public void setAddShipping20kg(Double addShipping20kg) {
        this.addShipping20kg = addShipping20kg;
    }

    public String getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(String spendTime) {
        this.spendTime = spendTime;
    }
}
