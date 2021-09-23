package cn.nome.saas.allocation.repository.old.allocation.dao;


import cn.nome.saas.allocation.model.old.forbiddenRule.ForbiddenSingleItem;

import java.util.List;
import java.util.Map;

/**
 * ForbiddenSingleItemDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public interface ForbiddenSingleItemDOMapper2 {

    public void deleteByRuleName(String ruleName);

    public void deleteByRuleId(int ruleId);

    public Integer insertSelective(ForbiddenSingleItem singleItem);

    int batchInsert(Map map);

    int countByParam(Map<String, Object> param);

    List<ForbiddenSingleItem> selectBySelective(Map<String, Object> param);

    List<String> selectModifiedByList();

    void updateShopCode(ForbiddenSingleItem singleItem);


}
