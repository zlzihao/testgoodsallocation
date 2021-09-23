package cn.nome.saas.sdc.bigData.repository.dao;

import cn.nome.saas.sdc.bigData.repository.entity.DwsDimShopStatusDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@Component
public interface DwsDimShopStatusMapper {

    public List<DwsDimShopStatusDO> queryByShopCode(@Param("list") List<String> list);
}
