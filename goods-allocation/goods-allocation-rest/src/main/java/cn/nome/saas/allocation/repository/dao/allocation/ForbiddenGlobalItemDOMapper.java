package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.model.rule.ForbiddenGlobalItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 禁配相关
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public interface ForbiddenGlobalItemDOMapper {

    ForbiddenGlobalItem selectByFRuleId(int fRuleId);

    Integer insertSelective(ForbiddenGlobalItem forbiddenGlobalItem);

    void deleteByFRuleId(int ruleId);

    void updateByFRuleId(ForbiddenGlobalItem forbiddenGlobalItem);

    List<ForbiddenGlobalItem> selectBySelective(Map<String,Object> param);

    List<Integer> selectForbiddenRule(@Param("shopId")String shopId);

    List<Integer> selectWhiteListRule(@Param("shopId")String shopId);

    List<Integer> selectSecurityRule(@Param("shopId")String shopId);

    List<Integer> selectForbiddenRuleByMatCode(@Param("matCode")String matCode);

    List<Integer> selectWhiteListRuleByMatCode(@Param("matCode")String matCode);

    List<Integer> selectSecurityRuleByMatCode(@Param("matCode")String matCode);

}
