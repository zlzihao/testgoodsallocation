package cn.nome.saas.allocation.repository.old.allocation.dao;
import cn.nome.saas.allocation.model.old.forbiddenRule.ForbiddenRule;
import cn.nome.saas.allocation.repository.old.allocation.entity.GoodsInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 禁配相关
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public interface ForbiddenRuleDOMapper2 {

    //public List<DwsDimShopDO> selectAllShopList();

    public List<GoodsInfoDO> selectGoodsList();

    //List<Map<String,String>> selectShopIdByCode(Map<String,Object> param);

    Map<String,String> selectGoodsNameByMatCode(List<String> matCodes);

    public ForbiddenRule selectByParam(ForbiddenRule forbiddenRule);


    public int getForbiddenRuleCount(Map<String, Object> param);

    public List<ForbiddenRule> selectFrobiddenRuleByPage(Map<String, Object> param);

    Integer insertSelective(@Param("rule") ForbiddenRule rule);

    List<String> selectMatCodeByParam(Map<String, Object> param);

    public void deleteById(int ruleId);

    public void updateById(ForbiddenRule forbiddenRule);

    ForbiddenRule selectById(int ruleId);
}
