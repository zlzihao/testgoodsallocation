package cn.nome.saas.allocation.repository.dao.allocation;


import cn.nome.saas.allocation.repository.entity.allocation.WhiteListSingleItemDO;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WhiteListSingleItemDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/5/26
 */
public interface WhiteListSingleItemDOMapper {

    public void deleteByRuleName(String ruleName);

    public void deleteByRuleId(int ruleId);

    public Integer insertSelective(WhiteListSingleItemDO singleItem);

    int batchInsert(Map map);

    int countByParam(Map<String, Object> param);

    List<WhiteListSingleItemDO> selectBySelective(Map<String, Object> param);

    List<String> selectModifiedByList();

    void updateShopCode(WhiteListSingleItemDO singleItem);

    public List<Map<String,String>> getForbiddenDetailList(Map<String, Object> param);

    public List<WhiteListSingleItemDO> getQdIssueWhitelist(Map<String, Object> param);

    /**
     * getIssueWhiteList
     * @param shopIds shopIds
     * @return return
     */
    Set<String> getIssueWhiteList(@Param("shopIds") Set<String> shopIds);


}
