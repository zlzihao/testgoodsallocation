package cn.nome.saas.allocation.repository.dao.vertical;


import cn.nome.saas.allocation.repository.entity.vertical.DwsDimGoodsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DwsGoodsDOMapper {

    List<DwsDimGoodsDO> selectGoodsListByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    int getCount();

}