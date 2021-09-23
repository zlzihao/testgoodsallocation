package cn.nome.saas.sdc.bigData.repository.dao;

import cn.nome.saas.sdc.bigData.repository.entity.DwsGoalDayShopCategoryDO;
import cn.nome.saas.sdc.repository.entity.SeasonChangeDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@Component
public interface DwsGoalDayShopCategoryMapper {

    List<DwsGoalDayShopCategoryDO> getGoalDay(@Param("list") List<SeasonChangeDO> list);



}
