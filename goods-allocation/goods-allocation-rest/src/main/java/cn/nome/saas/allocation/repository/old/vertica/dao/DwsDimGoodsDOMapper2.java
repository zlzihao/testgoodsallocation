package cn.nome.saas.allocation.repository.old.vertica.dao;

import cn.nome.saas.allocation.repository.old.allocation.entity.DwsDimGoodsDO;

import java.util.List;

/**
 * DwsDimGoodsMapper
 *
 * @author Bruce01.fan
 * @date 2019/5/25
 */
public interface DwsDimGoodsDOMapper2 {

    public List<DwsDimGoodsDO> selectGoodsList();
}
