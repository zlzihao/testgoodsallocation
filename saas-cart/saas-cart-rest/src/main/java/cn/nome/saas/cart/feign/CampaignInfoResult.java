package cn.nome.saas.cart.feign;

import java.util.List;
import java.util.Map;

/**
 * @author chentaikuang
 */
public class CampaignInfoResult {
    private int campaignType; //活动类型；1，满N减1,2，折扣活动
    private int id;//活动id
    @Deprecated
    private int productSetId;
    private String name;
    @Deprecated
    private int subType;
    private List<String> skuCodeList;
    private Map<String, Integer> promotionSkuCodes;

    /**
     * 活动提示语
     */
    private String tips;

    /**
     * 活动类型简称
     */
    private String simpleName;

    /**
     * 活动code【package为套餐活动，如满N减1,，满件折扣；single为非套餐活动，针对单件商品的，如折扣活动】
     */
    private String typeCode;

    /**
     * 多个选品集id
     * @return
     */
    private List<Integer> productSetIds;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public List<Integer> getProductSetIds() {
        return productSetIds;
    }

    public void setProductSetIds(List<Integer> productSetIds) {
        this.productSetIds = productSetIds;
    }
    @Deprecated
    public int getProductSetId() {
        return productSetId;
    }
    @Deprecated
    public void setProductSetId(int productSetId) {
        this.productSetId = productSetId;
    }
    public int getCampaignType() {
        return campaignType;
    }
    public void setCampaignType(int campaignType) {
        this.campaignType = campaignType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Deprecated
    public int getSubType() {
        return subType;
    }
    @Deprecated
    public void setSubType(int subType) {
        this.subType = subType;
    }

    public List<String> getSkuCodeList() {
        return skuCodeList;
    }

    public void setSkuCodeList(List<String> skuCodeList) {
        this.skuCodeList = skuCodeList;
    }

    public Map<String, Integer> getPromotionSkuCodes() {
        return promotionSkuCodes;
    }

    public void setPromotionSkuCodes(Map<String, Integer> promotionSkuCodes) {
        this.promotionSkuCodes = promotionSkuCodes;
    }
}
