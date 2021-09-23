package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.SecuritySingleRuleDO;

import java.util.List;
import java.util.Map;

/**
 * SecuritySingleItemDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public interface SecuritySingleRuleDOMapper {

    List<SecuritySingleRuleDO> selectSecurityList(Map map);

    int batchInsert(Map map);

    int deleteByRuleName(String ruleName);

    int deleteByRuleId(int ruleId);
}
