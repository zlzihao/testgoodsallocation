package cn.nome.saas.cart.repository.dao;

import cn.nome.saas.cart.repository.entity.AddCartItemDO;
import cn.nome.saas.cart.repository.entity.CartItemDO;
import cn.nome.saas.cart.repository.entity.DelCartItemDO;
import cn.nome.saas.cart.repository.entity.UpdateCartItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chentaikuang
 */
public interface CartItemDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CartItemDO record);

    int insertSelective(CartItemDO record);

    int updateItem(UpdateCartItemDO updateCartItemDO);

    CartItemDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CartItemDO record);

    int updateByPrimaryKey(CartItemDO record);

    List<CartItemDO> selectByAlias(@Param("alias") String alias);
    int selectCountByAlias(@Param("alias") String alias);

    List<CartItemDO> selectSkus(@Param("alias") String alias,@Param("skuCodes") List<String> skuCodes);

    int updateExistsSku(@Param("existsSkus") Map<String, Integer> existsSkus,@Param("alias") String alias);

    int batchInsertSku(@Param("addItem") List<CartItemDO> addItem);

    int delSkuCodes(DelCartItemDO delCartItemDO);

    int updateOneSku(@Param("skuCode")String skuCode, @Param("count")Integer count, @Param("alias")String alias);

    int addCartItem(AddCartItemDO addCartItemDO);

    int insertOrUpdate(AddCartItemDO addCartItemDO);

    CartItemDO selectOneSku(@Param("alias") String alias,@Param("skuCode") String skuCode);

    int batchDelSku(@Param("alias") String alias,@Param("skuCodes") List<String> skuCodes);

    int delOneSku(@Param("alias") String alias,@Param("skuCode") String skuCode);
}