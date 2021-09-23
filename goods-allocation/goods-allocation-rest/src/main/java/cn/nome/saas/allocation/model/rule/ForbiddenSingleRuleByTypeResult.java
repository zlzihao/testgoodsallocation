package cn.nome.saas.allocation.model.rule;

import cn.nome.platform.common.utils.ToString;

import java.util.Set;

/**
 * ForbiddenRuleResult
 *
 * @author Bruce01.fan
 * @date 2019/6/11
 */
public class ForbiddenSingleRuleByTypeResult extends ToString {

    private Set<String> shopCodes;

    private Integer type;

    private String typeValue;

    public Set<String> getShopCodes() {
        return shopCodes;
    }

    public void setShopCodes(Set<String> shopCodes) {
        this.shopCodes = shopCodes;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }
}
