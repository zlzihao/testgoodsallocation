package cn.nome.saas.cart.model;

import java.util.List;
import java.util.Map;

/**
 * @author chentaikuang
 */
public class DisctVO {

    private Map<String,Integer> skuCodes;
    private String tips;
    private Integer favor;

    public Map<String, Integer> getSkuCodes() {
        return skuCodes;
    }

    public void setSkuCodes(Map<String, Integer> skuCodes) {
        this.skuCodes = skuCodes;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Integer getFavor() {
        return favor;
    }

    public void setFavor(Integer favor) {
        this.favor = favor;
    }
}
