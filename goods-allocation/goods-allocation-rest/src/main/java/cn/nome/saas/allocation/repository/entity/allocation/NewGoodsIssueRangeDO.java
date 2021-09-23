package cn.nome.saas.allocation.repository.entity.allocation;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ForbiddenRule
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public class NewGoodsIssueRangeDO {

    private Integer id;

    /**
     * matCode
     */
    private String matCode;

    /**
     * 商品名称
     */
    private String matName;

    /**
     * 尺码id
     */
    private String sizeId;

    /**
     * 尺码名称
     */
    private String sizeName;

    /**
     * 是否完成首配, 0-否, 1-是
     */
    private Integer issueFin;

    /**
     * 失效时间
     */
    private Date invalidAt;

    private Date createdAt;

    private Date updatedAt;

    private String updatedBy;

    private String regionInclude;
    private String provinceInclude;
    private String cityInclude;
    private String saleLvInclude;
    private String displayLvInclude;
    private String shopIdInclude;
    private String regionExclude;
    private String provinceExclude;
    private String cityExclude;
    private String saleLvExclude;
    private String displayLvExclude;
    private String shopIdExclude;

    private String attrVal1In;
    private String attrVal2In;
    private String attrVal3In;
    private String attrVal4In;
    private String attrVal5In;
    private String attrVal1Ex;
    private String attrVal2Ex;
    private String attrVal3Ex;
    private String attrVal4Ex;
    private String attrVal5Ex;

    private Map<String, String> shopIdMapInclude;
    private Map<String, String> shopIdMapExclude;

    public String getAttrVal1In() {
        return attrVal1In;
    }

    public void setAttrVal1In(String attrVal1In) {
        this.attrVal1In = attrVal1In;
    }

    public String getAttrVal2In() {
        return attrVal2In;
    }

    public void setAttrVal2In(String attrVal2In) {
        this.attrVal2In = attrVal2In;
    }

    public String getAttrVal3In() {
        return attrVal3In;
    }

    public void setAttrVal3In(String attrVal3In) {
        this.attrVal3In = attrVal3In;
    }

    public String getAttrVal4In() {
        return attrVal4In;
    }

    public void setAttrVal4In(String attrVal4In) {
        this.attrVal4In = attrVal4In;
    }

    public String getAttrVal5In() {
        return attrVal5In;
    }

    public void setAttrVal5In(String attrVal5In) {
        this.attrVal5In = attrVal5In;
    }

    public String getAttrVal1Ex() {
        return attrVal1Ex;
    }

    public void setAttrVal1Ex(String attrVal1Ex) {
        this.attrVal1Ex = attrVal1Ex;
    }

    public String getAttrVal2Ex() {
        return attrVal2Ex;
    }

    public void setAttrVal2Ex(String attrVal2Ex) {
        this.attrVal2Ex = attrVal2Ex;
    }

    public String getAttrVal3Ex() {
        return attrVal3Ex;
    }

    public void setAttrVal3Ex(String attrVal3Ex) {
        this.attrVal3Ex = attrVal3Ex;
    }

    public String getAttrVal4Ex() {
        return attrVal4Ex;
    }

    public void setAttrVal4Ex(String attrVal4Ex) {
        this.attrVal4Ex = attrVal4Ex;
    }

    public String getAttrVal5Ex() {
        return attrVal5Ex;
    }

    public void setAttrVal5Ex(String attrVal5Ex) {
        this.attrVal5Ex = attrVal5Ex;
    }

    public Map<String, String> getShopIdMapInclude() {
        return shopIdMapInclude;
    }

    public void setShopIdMapInclude(Map<String, String> shopIdMapInclude) {
        this.shopIdMapInclude = shopIdMapInclude;
    }

    public Map<String, String> getShopIdMapExclude() {
        return shopIdMapExclude;
    }

    public void setShopIdMapExclude(Map<String, String> shopIdMapExclude) {
        this.shopIdMapExclude = shopIdMapExclude;
    }

    public String getRegionInclude() {
        return regionInclude;
    }

    public void setRegionInclude(String regionInclude) {
        this.regionInclude = regionInclude;
    }

    public String getProvinceInclude() {
        return provinceInclude;
    }

    public void setProvinceInclude(String provinceInclude) {
        this.provinceInclude = provinceInclude;
    }

    public String getCityInclude() {
        return cityInclude;
    }

    public void setCityInclude(String cityInclude) {
        this.cityInclude = cityInclude;
    }

    public String getSaleLvInclude() {
        return saleLvInclude;
    }

    public void setSaleLvInclude(String saleLvInclude) {
        this.saleLvInclude = saleLvInclude;
    }

    public String getDisplayLvInclude() {
        return displayLvInclude;
    }

    public void setDisplayLvInclude(String displayLvInclude) {
        this.displayLvInclude = displayLvInclude;
    }

    public String getShopIdInclude() {
        return shopIdInclude;
    }

    public void setShopIdInclude(String shopIdInclude) {
        this.shopIdInclude = shopIdInclude;
    }

    public String getRegionExclude() {
        return regionExclude;
    }

    public void setRegionExclude(String regionExclude) {
        this.regionExclude = regionExclude;
    }

    public String getProvinceExclude() {
        return provinceExclude;
    }

    public void setProvinceExclude(String provinceExclude) {
        this.provinceExclude = provinceExclude;
    }

    public String getCityExclude() {
        return cityExclude;
    }

    public void setCityExclude(String cityExclude) {
        this.cityExclude = cityExclude;
    }

    public String getSaleLvExclude() {
        return saleLvExclude;
    }

    public void setSaleLvExclude(String saleLvExclude) {
        this.saleLvExclude = saleLvExclude;
    }

    public String getDisplayLvExclude() {
        return displayLvExclude;
    }

    public void setDisplayLvExclude(String displayLvExclude) {
        this.displayLvExclude = displayLvExclude;
    }

    public String getShopIdExclude() {
        return shopIdExclude;
    }

    public void setShopIdExclude(String shopIdExclude) {
        this.shopIdExclude = shopIdExclude;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public Integer getIssueFin() {
        return issueFin;
    }

    public void setIssueFin(Integer issueFin) {
        this.issueFin = issueFin;
    }

    public Date getInvalidAt() {
        return invalidAt;
    }

    public void setInvalidAt(Date invalidAt) {
        this.invalidAt = invalidAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
