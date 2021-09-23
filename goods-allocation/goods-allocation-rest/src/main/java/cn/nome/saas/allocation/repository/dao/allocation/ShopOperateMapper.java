package cn.nome.saas.allocation.repository.dao.allocation;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.repository.entity.allocation.ShopOperateDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
public interface ShopOperateMapper {

    Integer getCount();

    List<ShopOperateDO> getList(@Param("page") Page page);

    ShopOperateDO getDetail(@Param("record") ShopOperateDO record);

    Integer insert(@Param("record") ShopOperateDO record);

    Integer update(@Param("record") ShopOperateDO record);

    Integer delete(@Param("list") List<Integer> list);

    Integer commit(@Param("id") Integer id);
}
