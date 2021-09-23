package cn.nome.saas.cart.feign;

import java.util.List;
import java.util.Map;

/**
 * @author chentaikuang
 */
public class PromoSkuCalcResult {

    private int campaignType; //活动类型；1，满N减1,2，折扣活动，3满件折扣
    private int id;
    private int productSetId;
    private String name;
    private int promotionPrice;
    private int subType;
    private List<String> skuCodeList;
    private Map<String, Integer> promotionSkuCodes;
    private int status; //1代表未满足活动，2代表满足活动
    /**
     * 1代表要显示“去凑单”按钮，2代表显示“再逛逛”
     */
    private Integer isShowBillBtn;

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

    public Integer getIsShowBillBtn() {
        return isShowBillBtn;
    }

    public void setIsShowBillBtn(Integer isShowBillBtn) {
        this.isShowBillBtn = isShowBillBtn;
    }

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

    public int getProductSetId() {
        return productSetId;
    }

    public void setProductSetId(int productSetId) {
        this.productSetId = productSetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(int promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public int getSubType() {
        return subType;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
