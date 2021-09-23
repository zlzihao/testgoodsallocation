package cn.nome.saas.cart.service;

import cn.nome.platform.common.shard.annotation.Param;
import cn.nome.platform.common.shard.annotation.TableSharding;
import cn.nome.platform.common.shard.constants.DbConstants;
import cn.nome.saas.cart.repository.dao.CartDOMapper;
import cn.nome.saas.cart.repository.dao.CartItemDOMapper;
import cn.nome.saas.cart.repository.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 购物车原子服务层：提供原子接口方法，不做业务逻辑、转换封装之类的动作
 *
 * @author chentaikuang
 */
@Service
public class CartService {

	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private static final int FLAG_SUCCESS = 1;

	@Autowired
	private CartDOMapper cartDOMapper;
	@Autowired
	private CartItemDOMapper cartItemDOMapper;

	/**
	 * 根据别号获取购物车明细sku
	 * 
	 * @param alias
	 * @return
	 */
	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public List<CartItemDO> selectByAlias(@Param("alias") String alias) {
		List<CartItemDO> cartItemDOs = cartItemDOMapper.selectByAlias(alias);
		return cartItemDOs;
	}

	/**
	 * 根据别号获取购物车明细sku条数
	 * 
	 * @param alias
	 * @return
	 */
	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public int selectCountByAlias(@Param("alias") String alias) {
		return cartItemDOMapper.selectCountByAlias(alias);
	}

	public static void main(String[] args) {
		Set<Integer> set = new HashSet<>();
		set.add(2);
		set.add(2);
		set.add(1);
		System.err.println(new ArrayList<>(set));
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#corpId,#appId,#uid")
	public CartDO selectUserCart(@Param("uid") Integer uid, @Param("appId") Integer appId,
			@Param("corpId") Integer corpId) {
		CartDO cartDO = cartDOMapper.selectUserCart(uid, appId, corpId);
		return cartDO;
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public List<CartItemDO> selectSku(@Param("alias") String alias, List<String> skuCodes) {
		List<CartItemDO> cartItemDOs = cartItemDOMapper.selectSkus(alias, skuCodes);
		return cartItemDOs;
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public int batchUpdateSku(@Param("alias") String alias, Map<String, Integer> existsSkus) {
		int nm = cartItemDOMapper.updateExistsSku(existsSkus, alias);
		return nm;
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public int updateSku(@Param("alias") String alias, String skuCode, Integer count) {
		int nm = cartItemDOMapper.updateOneSku(skuCode, count, alias);
		return nm;
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#itemDO.alias")
	public int addItem(@Param("itemDO") CartItemDO itemDO) {
		return cartItemDOMapper.insertSelective(itemDO);
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#updateCartItemDO.alias")
	public int updateItem(@Param("updateCartItemDO") UpdateCartItemDO updateCartItemDO) {
		return cartItemDOMapper.updateItem(updateCartItemDO);
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public int batchInsertSku(@Param("alias") String alias, List<CartItemDO> addItem) {
		int nm = cartItemDOMapper.batchInsertSku(addItem);
		return nm;
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#cart.corpId,#cart.appId,#cart.userId")
	public int addNewCart(@Param("cart") CartDO cart) {
		return cartDOMapper.insert(cart);
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#delCartItemDO.alias")
	public int delSkuCodes(@Param("delCartItemDO") DelCartItemDO delCartItemDO) {
		return cartItemDOMapper.delSkuCodes(delCartItemDO);
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#addCartItemDO.alias")
	public int addCartItem(@Param("addCartItemDO") AddCartItemDO addCartItemDO) {
		return cartItemDOMapper.addCartItem(addCartItemDO);
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#addCartItemDO.alias")
	public int insertOrUpdate(@Param("addCartItemDO") AddCartItemDO addCartItemDO) {
//		CartItemDO cartItemDO = cartItemDOMapper.selectOneSku(addCartItemDO.getAlias(), addCartItemDO.getSkuCode());
//		if (cartItemDO != null) {
//			Map<String, Integer> map = new HashMap<>();
//			map.put(addCartItemDO.getSkuCode(), addCartItemDO.getCount());
//			return batchUpdateSku(map, addCartItemDO.getAlias());
//		} else {
//			return addItem(convertCartItem(addCartItemDO));
//		}
		return cartItemDOMapper.insertOrUpdate(addCartItemDO);
	}

	private CartItemDO convertCartItem(AddCartItemDO addCartItemDO) {
		CartItemDO itemDO = new CartItemDO();
		itemDO.setSkuCode(addCartItemDO.getSkuCode());
		itemDO.setUserId(addCartItemDO.getUserId());
		itemDO.setCorpId(addCartItemDO.getCorpId());
		itemDO.setAppId(addCartItemDO.getAppId());
		itemDO.setSkuId(addCartItemDO.getSkuId());
		itemDO.setCount(addCartItemDO.getCount());
		itemDO.setProductId(addCartItemDO.getProductId());
		itemDO.setAlias(addCartItemDO.getAlias());
		return itemDO;
	}

	/**
	 * 不加分表注解，插入指定表
	 * 
	 * @param cart
	 * @return
	 */
	public int addGlobalCart(CartDO cart) {
		return cartDOMapper.insert(cart);
	}

	public List<CartDO> syncByPage(Map<String, Integer> data) {
		List<CartDO> cartDos = cartDOMapper.selectByPage(data);
		return cartDos;
	}

	/**
	 * 获取全部表最大userId
	 * @return
	 */
    public int getMaxUid4GlobalTab() {
		return cartDOMapper.maxUid();
    }

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public int batchDelSku(@Param("alias") String alias, List<String> skuCodes) {
		if (StringUtils.isBlank(alias)) {
			LOGGER.error("[batchDelSku] alias:{}", alias);
			return 0;
		}
		return cartItemDOMapper.batchDelSku(alias,skuCodes);
	}

    /**
     * 获取全局购物车别号alias
     * @param corpId
     * @param appId
     * @param uid
     * @return
     */
	public String getGlobalCartAlias(int corpId, int appId, int uid) {
		return cartDOMapper.selectAlias(corpId, appId, uid);
	}

    /**
     * 获取分组购物车别号alias
     * @param corpId
     * @param appId
     * @param uid
     * @return
     */
	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#corpId,#appId,#uid")
	public String getGroupTabAlias(@Param("corpId") Integer corpId, @Param("appId") Integer appId,
								   @Param("uid") Integer uid) {
		return cartDOMapper.selectAlias(corpId, appId, uid);
	}

	@TableSharding(strategy = DbConstants.DEFAULT_TABLE_SHARDING_STRATEGY, key = "#alias")
	public int delOneSku(@Param("alias") String alias, String skuCode) {
		int rst = 0;
		try {
			rst = cartItemDOMapper.delOneSku(alias, skuCode);
		} catch (Exception e) {
			LOGGER.error("[delOneSku] alias:{},skuCode:{},err:{}", alias, skuCode, e.getMessage());
		}
		return rst;
	}
}
