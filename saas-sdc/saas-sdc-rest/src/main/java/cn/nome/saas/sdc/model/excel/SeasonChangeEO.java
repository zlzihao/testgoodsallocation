package cn.nome.saas.sdc.model.excel;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class SeasonChangeEO extends ToString {
    @Column(num = 0, value = "门店编号", width = 20)
    private String shopCode;
    @Column(num = 1, value = "门店名称", width = 20)
    private String shopName;
    @Column(num = 2, value = "年份", width = 20)
    private String year;
    @Column(num = 3, value = "转季季度", width = 20)
    private String seasonsAlternate;
    @Column(num = 4, value = "转季日期(必须是日期格式)", width = 20)
    private Date seasonsAlternateDay;
    @Column(num = 5, value = "转季系数(必须是数字)", width = 20)
    private String seasonsAlternateCoefficient;

    public SeasonChangeEO() {
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeasonsAlternate() {
        return seasonsAlternate;
    }

    public void setSeasonsAlternate(String seasonsAlternate) {
        this.seasonsAlternate = seasonsAlternate;
    }

    public Date getSeasonsAlternateDay() {
        return seasonsAlternateDay;
    }

    public void setSeasonsAlternateDay(Date seasonsAlternateDay) {
        this.seasonsAlternateDay = seasonsAlternateDay;
    }

    public String getSeasonsAlternateCoefficient() {
        return seasonsAlternateCoefficient;
    }

    public void setSeasonsAlternateCoefficient(String seasonsAlternateCoefficient) {
        this.seasonsAlternateCoefficient = seasonsAlternateCoefficient;
    }
}
