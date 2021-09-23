package cn.nome.saas.cart.model;

import java.util.List;

/**
 * @author chentaikuang
 * 满减n
 */
public class PromoVo {
    private int id;
    @Deprecated
    private int condition;
    private String name;
    private String tips;

    private String simpleName;

    private List<Integer> productSetIds;

    private Integer campaignType;

    public Integer getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(Integer campaignType) {
        this.campaignType = campaignType;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public List<Integer> getProductSetIds() {
        return productSetIds;
    }

    public void setProductSetIds(List<Integer> productSetIds) {
        this.productSetIds = productSetIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
