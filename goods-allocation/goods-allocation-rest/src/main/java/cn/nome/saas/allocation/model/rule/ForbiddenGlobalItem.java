package cn.nome.saas.allocation.model.rule;

import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;

import java.util.Date;
import java.util.List;

/**
 * ForbiddenRule
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public class ForbiddenGlobalItem {

    private String id;
    private String fRuleId;
//
    private String regionInclude;
    private String provinceInclude;
    private String cityInclude;
    private String shopInclude;
    private String largeInclude;
    private String middleInclude;
    private String smallInclude;
    private String saleLvInclude;
    private String displayLvInclude;
    private String skcInclude;
    private String skuInclude;

    private String attrFirValInclude;
    private String attrSecValInclude;
    private String attrThiValInclude;
    private String attrFourValInclude;
    private String attrFifValInclude;

    private String regionExclude;
    private String provinceExclude;
    private String cityExclude;
    private String shopExclude;
    private String largeExclude;
    private String middleExclude;
    private String smallExclude;
    private String saleLvExclude;
    private String displayLvExclude;
    private String skcExclude;
    private String skuExclude;

    private String attrFirValExclude;
    private String attrSecValExclude;
    private String attrThiValExclude;
    private String attrFourValExclude;
    private String attrFifValExclude;

    private String ruleName;
    private Date startDate;
    private Date endDate;

    private List<ImportRuleFileVo> includeList;
    private List<ImportRuleFileVo> excludeList;

    private Integer ruleType;

    private Integer syncWhiteFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfRuleId() {
        return fRuleId;
    }

    public void setfRuleId(String fRuleId) {
        this.fRuleId = fRuleId;
    }

//    public Integer getRuleType() {
//        return ruleType;
//    }
//
//    public void setRuleType(Integer ruleType) {
//        this.ruleType = ruleType;
//    }

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

    public String getShopInclude() {
        return shopInclude;
    }

    public void setShopInclude(String shopInclude) {
        this.shopInclude = shopInclude;
    }

    public String getLargeInclude() {
        return largeInclude;
    }

    public void setLargeInclude(String largeInclude) {
        this.largeInclude = largeInclude;
    }

    public String getMiddleInclude() {
        return middleInclude;
    }

    public void setMiddleInclude(String middleInclude) {
        this.middleInclude = middleInclude;
    }

    public String getSmallInclude() {
        return smallInclude;
    }

    public void setSmallInclude(String smallInclude) {
        this.smallInclude = smallInclude;
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

    public String getShopExclude() {
        return shopExclude;
    }

    public void setShopExclude(String shopExclude) {
        this.shopExclude = shopExclude;
    }

    public String getLargeExclude() {
        return largeExclude;
    }

    public void setLargeExclude(String largeExclude) {
        this.largeExclude = largeExclude;
    }

    public String getMiddleExclude() {
        return middleExclude;
    }

    public void setMiddleExclude(String middleExclude) {
        this.middleExclude = middleExclude;
    }

    public String getSmallExclude() {
        return smallExclude;
    }

    public void setSmallExclude(String smallExclude) {
        this.smallExclude = smallExclude;
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

    public String getCityExclude() {
        return cityExclude;
    }

    public void setCityExclude(String cityExclude) {
        this.cityExclude = cityExclude;
    }

    public String getSkcExclude() {
        return skcExclude;
    }

    public void setSkcExclude(String skcExclude) {
        this.skcExclude = skcExclude;
    }

    public String getSkuExclude() {
        return skuExclude;
    }

    public void setSkuExclude(String skuExclude) {
        this.skuExclude = skuExclude;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSkcInclude() {
        return skcInclude;
    }

    public void setSkcInclude(String skcInclude) {
        this.skcInclude = skcInclude;
    }

    public String getSkuInclude() {
        return skuInclude;
    }

    public void setSkuInclude(String skuInclude) {
        this.skuInclude = skuInclude;
    }

    public List<ImportRuleFileVo> getIncludeList() {
        return includeList;
    }

    public void setIncludeList(List<ImportRuleFileVo> includeList) {
        this.includeList = includeList;
    }

    public List<ImportRuleFileVo> getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(List<ImportRuleFileVo> excludeList) {
        this.excludeList = excludeList;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getSyncWhiteFlag() {
        return syncWhiteFlag;
    }

    public void setSyncWhiteFlag(Integer syncWhiteFlag) {
        this.syncWhiteFlag = syncWhiteFlag;
    }

    public String getAttrFirValInclude() {
        return attrFirValInclude;
    }

    public void setAttrFirValInclude(String attrFirValInclude) {
        this.attrFirValInclude = attrFirValInclude;
    }

    public String getAttrSecValInclude() {
        return attrSecValInclude;
    }

    public void setAttrSecValInclude(String attrSecValInclude) {
        this.attrSecValInclude = attrSecValInclude;
    }

    public String getAttrThiValInclude() {
        return attrThiValInclude;
    }

    public void setAttrThiValInclude(String attrThiValInclude) {
        this.attrThiValInclude = attrThiValInclude;
    }

    public String getAttrFourValInclude() {
        return attrFourValInclude;
    }

    public void setAttrFourValInclude(String attrFourValInclude) {
        this.attrFourValInclude = attrFourValInclude;
    }

    public String getAttrFifValInclude() {
        return attrFifValInclude;
    }

    public void setAttrFifValInclude(String attrFifValInclude) {
        this.attrFifValInclude = attrFifValInclude;
    }

    public String getAttrFirValExclude() {
        return attrFirValExclude;
    }

    public void setAttrFirValExclude(String attrFirValExclude) {
        this.attrFirValExclude = attrFirValExclude;
    }

    public String getAttrSecValExclude() {
        return attrSecValExclude;
    }

    public void setAttrSecValExclude(String attrSecValExclude) {
        this.attrSecValExclude = attrSecValExclude;
    }

    public String getAttrThiValExclude() {
        return attrThiValExclude;
    }

    public void setAttrThiValExclude(String attrThiValExclude) {
        this.attrThiValExclude = attrThiValExclude;
    }

    public String getAttrFourValExclude() {
        return attrFourValExclude;
    }

    public void setAttrFourValExclude(String attrFourValExclude) {
        this.attrFourValExclude = attrFourValExclude;
    }

    public String getAttrFifValExclude() {
        return attrFifValExclude;
    }

    public void setAttrFifValExclude(String attrFifValExclude) {
        this.attrFifValExclude = attrFifValExclude;
    }
}
