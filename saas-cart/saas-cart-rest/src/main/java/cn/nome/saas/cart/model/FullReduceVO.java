package cn.nome.saas.cart.model;

import java.util.List;

/**
 * @author chentaikuang
 */
public class FullReduceVO {

    @Deprecated
    private int subType;// 减n
    @Deprecated
    private int setId;
    private List<String> skuCodes;
    private String tips;
    private int favor;//活动优惠金额

    private Integer campaignType;

    /**
     * 活动id
     */
    private Integer id;

    /**
     * 活动类型简称
     */
    private String simpleName;

    /**
     * 多个选品集id
     * @return
     */
    private List<Integer> setIds;

    /**
     * 1代表未满足活动，2代表满足活动
     */
    private Integer status;

    /**
     * 1代表线上去凑单按钮，2代表显示在逛逛
     */
    private Integer isShowBillBtn;

    public Integer getIsShowBillBtn() {
        return isShowBillBtn;
    }

    public void setIsShowBillBtn(Integer isShowBillBtn) {
        this.isShowBillBtn = isShowBillBtn;
    }

    public Integer getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(Integer campaignType) {
        this.campaignType = campaignType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {

        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public List<Integer> getSetIds() {
        return setIds;
    }
    public void setSetIds(List<Integer> setIds) {
        this.setIds = setIds;
    }
    @Deprecated
    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }
    @Deprecated
    public int getSetId() {
        return setId;
    }
    @Deprecated
    public void setSetId(int setId) {
        this.setId = setId;
    }

    public List<String> getSkuCodes() {
        return skuCodes;
    }

    public void setSkuCodes(List<String> skuCodes) {
        this.skuCodes = skuCodes;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getFavor() {
        return favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }
}
