package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDisplayDesginDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * QdIssueShopListDOMapper
 *
 * @author Bruce01.fan
 * @date 2019/8/5
 */
public interface QdIssueDisplayDesginDOMapper {

    List<QdIssueDisplayDesginDO> getShopDisplayDesignList();

    List<QdIssueDisplayDesginDO> getShopDisplayDesignListByShop(@Param("list")Set<String> shopIdList);
}
