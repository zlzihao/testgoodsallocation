package cn.nome.saas.allocation.model.rule;

/**
 * NewShopExpress
 *
 * @author Bruce01.fan
 * @date 2019/10/16
 */
public class NewShopExpress {

    private String province1;

    private String city1;

    private String province2;

    private String city2;

    private Double shippingFee;

    private Double addShippingFee;

    private Double shippingFee20kg;

    private Double bigAddShippingFee;

    private Integer spendDays;

    public String getProvince1() {
        return province1;
    }

    public void setProvince1(String province1) {
        this.province1 = province1;
    }

    public String getCity1() {
        return city1;
    }

    public void setCity1(String city1) {
        this.city1 = city1;
    }

    public String getProvince2() {
        return province2;
    }

    public void setProvince2(String province2) {
        this.province2 = province2;
    }

    public String getCity2() {
        return city2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public Double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Double getAddShippingFee() {
        return addShippingFee;
    }

    public void setAddShippingFee(Double addShippingFee) {
        this.addShippingFee = addShippingFee;
    }

    public Double getShippingFee20kg() {
        return shippingFee20kg;
    }

    public void setShippingFee20kg(Double shippingFee20kg) {
        this.shippingFee20kg = shippingFee20kg;
    }

    public Double getBigAddShippingFee() {
        return bigAddShippingFee;
    }

    public void setBigAddShippingFee(Double bigAddShippingFee) {
        this.bigAddShippingFee = bigAddShippingFee;
    }

    public Integer getSpendDays() {
        return spendDays;
    }

    public void setSpendDays(Integer spendDays) {
        this.spendDays = spendDays;
    }

    public static NewShopExpress getDefault() {
        NewShopExpress newShopExpress = new NewShopExpress();

        newShopExpress.setShippingFee(23D);
        newShopExpress.setAddShippingFee(14D);
        newShopExpress.setShippingFee20kg(106D);
        newShopExpress.setBigAddShippingFee(4D);
        newShopExpress.setSpendDays(3);

        return newShopExpress;
    }
}
