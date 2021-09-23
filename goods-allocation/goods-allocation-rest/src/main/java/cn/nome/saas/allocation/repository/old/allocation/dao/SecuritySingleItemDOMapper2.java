package cn.nome.saas.allocation.repository.old.allocation.dao;

import java.util.Map;

/**
 * SecuritySingleItemDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public interface SecuritySingleItemDOMapper2 {

    int batchInsert(Map map);

    int deleteByRuleName(String ruleName);

    int deleteByRuleId(int ruleId);

}
