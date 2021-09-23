package cn.nome.saas.cart.repository.dao;

import cn.nome.saas.cart.repository.entity.SysConfDO;

import java.util.List;

public interface SysConfDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysConfDO record);

    int insertSelective(SysConfDO record);

    SysConfDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysConfDO record);

    int updateByPrimaryKey(SysConfDO record);

    List<SysConfDO> selectConfByStatus(Integer status);

    SysConfDO selectOneByKeyCode(String keyCode);
    
    List<SysConfDO> selectByKeyCode(String keyCode);
}