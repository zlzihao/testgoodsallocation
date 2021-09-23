package cn.nome.saas.allocation.repository.entity.allocation;

import java.math.BigDecimal;
import java.util.Date;

public class DwsShopDO {
    private String province;

    private String city;

    private String town;

    private String shopname;

    private Date shopopendate;

    private BigDecimal shoparea;

    private String shopcode;

    private BigDecimal stokarea;

    private String shopsinglemanger;

    private String shopmanger;

    private String shopopenstatus;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town == null ? null : town.trim();
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname == null ? null : shopname.trim();
    }

    public Date getShopopendate() {
        return shopopendate;
    }

    public void setShopopendate(Date shopopendate) {
        this.shopopendate = shopopendate;
    }

    public BigDecimal getShoparea() {
        return shoparea;
    }

    public void setShoparea(BigDecimal shoparea) {
        this.shoparea = shoparea;
    }

    public String getShopcode() {
        return shopcode;
    }

    public void setShopcode(String shopcode) {
        this.shopcode = shopcode == null ? null : shopcode.trim();
    }

    public BigDecimal getStokarea() {
        return stokarea;
    }

    public void setStokarea(BigDecimal stokarea) {
        this.stokarea = stokarea;
    }

    public String getShopsinglemanger() {
        return shopsinglemanger;
    }

    public void setShopsinglemanger(String shopsinglemanger) {
        this.shopsinglemanger = shopsinglemanger == null ? null : shopsinglemanger.trim();
    }

    public String getShopmanger() {
        return shopmanger;
    }

    public void setShopmanger(String shopmanger) {
        this.shopmanger = shopmanger == null ? null : shopmanger.trim();
    }

    public String getShopopenstatus() {
        return shopopenstatus;
    }

    public void setShopopenstatus(String shopopenstatus) {
        this.shopopenstatus = shopopenstatus == null ? null : shopopenstatus.trim();
    }
}