package cn.nome.saas.cart.repository.dao;

import cn.nome.saas.cart.model.test.UserIdModel;
import cn.nome.saas.cart.repository.entity.CartDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chentaikuang
 */
public interface CartDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CartDO record);

    int insertSelective(CartDO record);

    CartDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CartDO record);

    int updateByPrimaryKey(CartDO record);

    CartDO selectByUid(Integer uid);

    CartDO selectByUidModel(UserIdModel testTabIndexModel);


    CartDO selectUserCart(@Param("uid") Integer uid,@Param("appId") Integer appId,@Param("corpId") Integer corpId);

    List<CartDO> selectByPage(Map<String, Integer> data);

    int maxUid();

    String selectAlias(@Param("corpId") Integer corpId, @Param("appId") Integer appId, @Param("uid") Integer uid);

}