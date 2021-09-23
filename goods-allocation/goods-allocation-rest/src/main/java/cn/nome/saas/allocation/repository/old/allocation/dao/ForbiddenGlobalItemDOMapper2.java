package cn.nome.saas.allocation.repository.old.allocation.dao;


import cn.nome.saas.allocation.model.old.forbiddenRule.ForbiddenGlobalItem;

/**
 * 禁配相关
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public interface ForbiddenGlobalItemDOMapper2 {
//
    public ForbiddenGlobalItem selectByFRuleId(int fRuleId);
//
//    public int getForbiddenRuleCount(Map<String, Object> param);
//
//    public List<ForbiddenRule> selectFrobiddenRuleByPage(Map<String, Object> param);

    public Integer insertSelective(ForbiddenGlobalItem forbiddenGlobalItem);

    public void deleteById(int ruleId);

    public void updateByFRuleId(ForbiddenGlobalItem forbiddenGlobalItem);
}
