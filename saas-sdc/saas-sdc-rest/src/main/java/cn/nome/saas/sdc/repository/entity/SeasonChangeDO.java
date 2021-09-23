package cn.nome.saas.sdc.repository.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizihao@nome.com
 */
public class SeasonChangeDO {
    private Long id;
    private String shopCode;
    private String shopName;
    private String year;
    private String seasonsAlternate;
    private Date seasonsAlternateDay;
    private Double targetPre;
    private Double targetNext;
    private BigDecimal seasonsAlternateCoefficient;
    private Integer createUserCode;
    private Integer updatedUserCode;
    private Date gmtCreate;
    private Date gmtUpdated;
    private Integer isDeleted;

    public SeasonChangeDO() {
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

    public Date getSeasonsAlternateDay() {
        return seasonsAlternateDay;
    }

    public void setSeasonsAlternateDay(Date seasonsAlternateDay) {
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

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getUpdatedUserCode() {
        return updatedUserCode;
    }

    public void setUpdatedUserCode(Integer updatedUserCode) {
        this.updatedUserCode = updatedUserCode;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtUpdated() {
        return gmtUpdated;
    }

    public void setGmtUpdated(Date gmtUpdated) {
        this.gmtUpdated = gmtUpdated;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
