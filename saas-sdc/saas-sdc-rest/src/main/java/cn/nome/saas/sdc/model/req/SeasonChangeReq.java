package cn.nome.saas.sdc.model.req;

import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class SeasonChangeReq {
    private Long id;
    private String shopName;
    private String year;
    private Date seasonsAlternateDay;


    public SeasonChangeReq() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Date getSeasonsAlternateDay() {
        return seasonsAlternateDay;
    }

    public void setSeasonsAlternateDay(Date seasonsAlternateDay) {
        this.seasonsAlternateDay = seasonsAlternateDay;
    }
}
