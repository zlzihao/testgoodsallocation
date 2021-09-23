package cn.nome.saas.sdc.model.vo;

import java.math.BigDecimal;

/**
 * @author lizihao@nome.com
 */
public class SeasonChangeVO {
    private Long id;
    private String shopCode;
    private String shopName;
    private String year;
    private String seasonsAlternate;
    private String seasonsAlternateDay;
    private Double targetPre;
    private Double targetNext;
    private BigDecimal seasonsAlternateCoefficient;
    private Integer isDeleted;

    public SeasonChangeVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSeasonsAlternateDay() {
        return seasonsAlternateDay;
    }

    public void setSeasonsAlternateDay(String seasonsAlternateDay) {
        this.seasonsAlternateDay = seasonsAlternateDay;
    }

    public Double getTargetPre() {
        return targetPre;
    }

    public void setTargetPre(Double targetPre) {
        this.targetPre = targetPre;
    }

    public Double getTargetNext() {
        return targetNext;
    }

    public void setTargetNext(Double targetNext) {
        this.targetNext = targetNext;
    }

    public BigDecimal getSeasonsAlternateCoefficient() {
        return seasonsAlternateCoefficient;
    }

    public void setSeasonsAlternateCoefficient(BigDecimal seasonsAlternateCoefficient) {
        this.seasonsAlternateCoefficient = seasonsAlternateCoefficient;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
