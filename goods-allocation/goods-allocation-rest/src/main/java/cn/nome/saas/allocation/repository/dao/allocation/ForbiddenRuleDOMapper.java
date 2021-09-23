package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.UserAdminDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 禁配相关
 *
 * @author Bruce01.fan
 * @date 2019/5/24
 */
public interface ForbiddenRuleDOMapper {

    public ForbiddenRuleDO selectByParam(ForbiddenRuleDO forbiddenRule);

    List<Integer> getValidRuleIds(Integer type, Integer status, String endDate);

    List<Integer> getRecentlyChangedRuleIds(Integer type, Integer status, String endDate, String updatedAt);

    public int getForbiddenRuleCount(Map<String, Object> param);

    public List<ForbiddenRuleDO> selectFrobiddenRuleByPage(Map<String, Object> param);

    public Integer insertSelective(ForbiddenRuleDO rule);

    public void deleteById(int ruleId);

    public void updateById(ForbiddenRuleDO forbiddenRule);

    ForbiddenRuleDO selectById(int  ruleId);
    ForbiddenRuleDO selectBySyncId(int  syncId);

    List<String> getRegioneNameList();
    List<String> getProvinceNameList();
    List<String> getCityNameList();
    List<DwsDimShopDO> getShopCodeList();
    List<String> getCategoryNameList();
    List<String> getMidCategoryNameList();
    List<String> getSmallCategoryNameList();
    List<String> getShopSaleLvList();
    List<String> getShopDisplayLvList();

    List<ShopInfoData> getShopBySaleDisplayLv(Map<String,Object> param);

    List<ShopInfoData> getShopBySaleDisplayLvIn(Map<String,Object> param);

    List<String> getSkcByCategory(Map<String,Object> param);

    UserAdminDO getAdmin(@Param("userId") String userId);

    Integer checkRuleIsNew(@Param("id")Integer id);
}
