package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenSingleItemDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueNewSkcStockDO;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueSkcStockDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ForbiddenSingleItemDOMapper2
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public interface ForbiddenSingleItemDOMapper {

    void deleteByRuleName(String ruleName);

    /**
     * 根据ruleId删除
     * @param ruleId
     */
    void deleteByRuleId(int ruleId);

    /**
     * 单条插入
     * @param singleItem
     * @return
     */
    Integer insertSelective(ForbiddenSingleItemDO singleItem);

    /**
     * 批量插入
     * @param map
     * @return
     */
    int batchInsert(Map map);

    List<Integer> getFinishedSettingRuleIds(@Param("ruleIds") List<Integer> ruleIds);

    /**
     * 获取总行数
     * @param param
     * @return
     */
    int countByParam(Map<String, Object> param);

    /**
     * 根据条件查询
     * @param param
     * @return
     */
    List<ForbiddenSingleItemDO> selectBySelective(Map<String, Object> param);

    List<ForbiddenSingleItemDO> selectByType(Map<String, Object> param);


    List<String> selectModifiedByList();

    void updateShopCode(ForbiddenSingleItemDO singleItem);

    List<Map<String,String>> getForbiddenDetailList(Map<String, Object> param);

    /**
     * 根据id更新
     * @param singleItem
     */
    void updateById(ForbiddenSingleItemDO singleItem);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    int deleteById(int id);

    /**
     * 根据参数获取总条数
     * @param param
     * @return
     */
    int getCount(Map<String, Object> param);

    /**
     * 根据参数获取ForbiddenSingleItemDO列表
     * @param param
     * @return
     */
    List<ForbiddenSingleItemDO> selectByPage(Map<String, Object> param);

    List<QdIssueSkcStockDO> getMidCategoryForbiddenList(@Param("seasonList") List<String> seasonList);

    List<QdIssueNewSkcStockDO> getSkcForbiddenList(@Param("seasonList") List<String> seasonList);

    /**
     * 根据店铺代码与对象值查询禁配
     * @param shopCodes shopCodes
     * @param typeValues typeValues
     * @return 禁配信息
     */
    List<ForbiddenSingleItemDO> getForbiddenByTypeValueAndShopCode(@Param("shopCodes") List<String> shopCodes, @Param("typeValues") List<String> typeValues);

    /**
     * 根据店铺代码与对象值删除禁配
     * @param shopCodes shopCodes
     * @param typeValues typeValues
     * @return 禁配信息
     */
    int delForbiddenByTypeValueAndShopCode(@Param("shopCodes") List<String> shopCodes, @Param("typeValues") List<String> typeValues);

    /**
     * 备份删除的单店禁配
     * @param ids ids
     * @param user user
     * @return int
     */
    int bakDelSingleForbidden(@Param("ids") Set<Integer> ids, @Param("user") String user);

}
