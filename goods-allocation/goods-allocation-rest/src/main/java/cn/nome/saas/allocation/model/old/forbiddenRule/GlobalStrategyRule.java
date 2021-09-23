package cn.nome.saas.allocation.model.old.forbiddenRule;

import cn.nome.platform.common.utils.ToString;
import cn.nome.platform.common.utils.excel.annotation.Column;

import java.util.List;

/**
 * GlobalStrategyRule
 *
 * @author Bruce01.fan
 * @date 2019/5/29
 */
public class GlobalStrategyRule extends ToString {

    @Column(value = "策略名",num = 0,isFilter = true,autoLine=true)
    private String name;

    @Column(value = "应用门店",num = 1,isFilter = true,autoLine=true)
    private String shop;

    @Column(value = "应用区域",num = 2,isFilter = true,autoLine=true)
    private String region;

    @Column(value = "应用类型",num = 3,isFilter = true,autoLine=true)
    private String type;

    @Column(value = "应用省市",num = 4,isFilter = true,autoLine=true)
    private String province;

    @Column(value = "应用级别",num = 5,isFilter = true,autoLine=true)
    private String level;

    @Column(value = "排除门店",num = 6,isFilter = true,autoLine=true)
    private String excludeShop;

    @Column(value = "排除区域",num = 7,isFilter = true,autoLine=true)
    private String excludeRegion;

    @Column(value = "排除类型",num = 8,isFilter = true,autoLine=true)
    private String excludeType;

    @Column(value = "排除省市",num = 9,isFilter = true,autoLine=true)
    private String excludeProvince;


    @Column(value = "排除级别",num = 10,isFilter = true,autoLine=true)
    private String excludeLevel;


    @Column(value = "备注",num = 12,isFilter = true,autoLine=true)
    private String remark;

    @Column(value = "创建人",num = 13,isFilter = true,autoLine=true)
    private String creator;

    private List<String> shopList;
    // 商品列表
    private List<String> goodsCodeList;

    // 保底
    private List<GlobalStrategyList> securityStrategyLists;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExcludeShop() {
        return excludeShop;
    }

    public void setExcludeShop(String excludeShop) {
        this.excludeShop = excludeShop;
    }

    public String getExcludeRegion() {
        return excludeRegion;
    }

    public void setExcludeRegion(String excludeRegion) {
        this.excludeRegion = excludeRegion;
    }

    public String getExcludeProvince() {
        return excludeProvince;
    }

    public void setExcludeProvince(String excludeProvince) {
        this.excludeProvince = excludeProvince;
    }

    public String getExcludeLevel() {
        return excludeLevel;
    }

    public void setExcludeLevel(String excludeLevel) {
        this.excludeLevel = excludeLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getGoodsCodeList() {
        return goodsCodeList;
    }

    public void setGoodsCodeList(List<String> goodsCodeList) {
        this.goodsCodeList = goodsCodeList;
    }

    public List<String> getShopList() {
        return shopList;
    }

    public void setShopList(List<String> shopList) {
        this.shopList = shopList;
    }

    public String getExcludeType() {
        return excludeType;
    }

    public void setExcludeType(String excludeType) {
        this.excludeType = excludeType;
    }

    public List<GlobalStrategyList> getSecurityStrategyLists() {
        return securityStrategyLists;
    }

    public void setSecurityStrategyLists(List<GlobalStrategyList> securityStrategyLists) {
        this.securityStrategyLists = securityStrategyLists;
    }
}
