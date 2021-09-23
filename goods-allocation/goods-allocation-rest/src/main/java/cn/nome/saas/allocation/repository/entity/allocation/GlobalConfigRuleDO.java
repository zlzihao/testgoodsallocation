package cn.nome.saas.allocation.repository.entity.allocation;

import cn.nome.platform.common.utils.ToString;

public class GlobalConfigRuleDO extends ToString {

//    public static final int TYPE_FORBIDDEN = 1;
//    public static final int TYPE_SECURITY = 2;
//    public static final int TYPE_WHITE_LIST = 3;

    private Integer ruleType;

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }
}
