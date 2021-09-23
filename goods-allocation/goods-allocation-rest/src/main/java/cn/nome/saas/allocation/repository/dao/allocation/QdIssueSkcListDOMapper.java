package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueSkcListDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QdIssueShopListDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public interface QdIssueSkcListDOMapper {

    List<String> getMatCodelist(@Param("seasonList")List<String> seasonList);

    List<QdIssueSkcListDO> getSkclistByOrder();

    List<String> getMatCodeByMidCategory(@Param("list") List<String> list);

    List<String> getNewMatCodeList();

}
