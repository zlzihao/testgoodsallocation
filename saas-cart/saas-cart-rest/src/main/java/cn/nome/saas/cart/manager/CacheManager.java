package cn.nome.saas.cart.manager;

import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.model.CacheSku;
import cn.nome.saas.cart.model.CartSkuModel;
import cn.nome.saas.cart.model.CartWrap;
import cn.nome.saas.cart.repository.entity.SysConfDO;
import cn.nome.saas.cart.service.SysConfService;
import cn.nome.saas.cart.utils.CacheOperationUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 缓存适配
 *
 * @author chentaikuang
 */
@Service
public class CacheManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CacheOperationUtil cacheOperationUtil;

    // 过期时间 单位：s
    private int exp = 7200;// 60 * 60 * 2

    /**
     * 购物车缓存哈希表前缀
     */
    public static final String PREFIX_CART = "CART:";
    /**
     * 购物车缓存别号：阿丽亚斯
     */
    public static final String PREFIX_CART_ALIAS = "CART_ALIAS:";
    /**
     * 购物车缓存SKU前缀
     */
    public static final String PREFIX_CART_SKU = "CART_SKU:";
    /**
     * 系统配置缓存key前缀
     */
    private final static String PREFIX_CART_SYS_CONF = "CART_SYS_CONF:";

    private static final String CART_MERGE_ERR_LOG = "CART_MERGE_ERR_LOG:";

    /**
     * 缓存操作结果标记
     */
    private static final int FLAG_ERROR = -1;

    /**
     * 缓存购物车sku有效时间
     */
    private static final String CACHE_CART_SKU_EXPIRE_TIME = "CACHE_CART_SKU_EXPIRE_TIME";
    /**
     * sku缓存默认有效期：1天=3600秒
     */
    @Value("${CACHE_CART_SKU_DEFAULT_EXPIRE_TIME:3600}")
    private int CACHE_CART_SKU_DEFAULT_EXPIRE_TIME;

    /**
     * 检查sku库存不足频率限制时间
     */
    private static final String PREFIX_CART_ADD_SKU_LESS_STORE_CHECK_TIME = "CART_ADD_SKU_LESS_STORE_CHECK_TIME:";

    @Value("${CART_ADD_SKU_LESS_STORE_CHECK_TIME:600}")
    private int CART_ADD_SKU_LESS_STORE_CHECK_TIME;

    @Autowired
    private SysConfService sysConfService;

    /**
     * 获取缓存，获取不到时返回null
     *
     * @param key
     * @return
     */
//	@Cacheable(value = "cart", key = "#key")
    public <T> T getCache(String key, Class<T> clazz) {
        String str = cacheOperationUtil.get(key);
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("getCache ERROR,keyCode:{},rtn str:{}", key, str);
            return null;
        }
        return JSONObject.parseObject(str, clazz);
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param t
     * @return
     */
//	@CachePut(value = "cart", key = "#key")
    public <T> T setCache(String key, T t) {
        String str = cacheOperationUtil.set(key, JSONObject.toJSONString(t));
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("setCache ERROR,keyCode:{},rtn str:{}", key, str);
            return null;
        }
        return t;
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param t
     * @return
     */
//	@CachePut(value = "cart", key = "#key")
    public <T> T setCache(String key, T t, int exp) {
        String str = cacheOperationUtil.setex(key, JSONObject.toJSONString(t), exp);
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("setCache exp ERROR,keyCode:{},rtn str:{}", key, str);
            return null;
        }
        return t;
    }

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
//	@CacheEvict(value = "usercache", key = "#key")
    public void delCache(String key) {
        cacheOperationUtil.del(key);
    }

    public <T> Long hsetCache(String key, String field, T t) {
        Long setL = cacheOperationUtil.hset(key, field, JSONObject.toJSONString(t));
        if (Constant.REDIS_ERROR_LONG.equals(setL)) {
            LOGGER.error("hsetCache ERROR,keyCode:{},field:{},rtn str:{}", key, field, setL);
            return null;
        }
        return setL;
    }

    public <T> T hgetCache(String key, String field, Class<T> clazz) {
        String str = cacheOperationUtil.hget(key, field);
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("hgetCache ERROR,keyCode:{},field:{},rtn str:{}", key, field, str);
            return null;
        }
        return JSONObject.parseObject(str, clazz);
    }

    /**
     * 从缓存获取配置，不查表
     *
     * @param keyCode
     * @return
     */
    public SysConfDO getConf(String keyCode) {
        String str = cacheOperationUtil.get(PREFIX_CART_SYS_CONF + keyCode);
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("getConf ERROR,keyCode:{},rtn str:{}", keyCode, str);
            return sysConfService.selectTabByCode(keyCode);
        }
        SysConfDO ob = JSON.parseObject(str, SysConfDO.class);
        return ob;
    }

    /**
     * 保存配置
     *
     * @param keyCode
     * @param sc
     * @return
     */
    public int setConf(String keyCode, SysConfDO sc) {
        String str = cacheOperationUtil.set(PREFIX_CART_SYS_CONF + keyCode, JSONObject.toJSONString(sc));
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("setConf ERROR,keyCode:{},rtn str:{}", keyCode, str);
            return FLAG_ERROR;
        }
        return Constant.FLAG_SUCCESS;
    }

    public Long hdelCache(String key, String... field) {
        Long longRet = cacheOperationUtil.hdel(key, field);
        if (Constant.REDIS_ERROR_LONG.equals(longRet)) {
            LOGGER.error("hdelCache ERROR,keyCode:{},rtn str:{}", key, field);
            return Constant.FLAG_ERROR_LONG;
        }
        return longRet;
    }

    public Map<String, String> hgetAllCache(String key) {
        return cacheOperationUtil.hgetAll(key);
    }

    /**
     * 获取用户购物车缓存sku
     *
     * @param corpIdAppIdUid
     * @param skuCode
     * @return
     */
    public CartSkuModel getCartSku(String corpIdAppIdUid, String skuCode) {
        CartSkuModel cartSkuModel = hgetCache(CacheManager.PREFIX_CART + corpIdAppIdUid, skuCode,
                CartSkuModel.class);
        return cartSkuModel;
    }

    /**
     * 缓存sku到用户购物车
     *
     * @param corpIdAppIdUid
     * @param skuCodes
     * @return
     */
    public int delCartSkus(String corpIdAppIdUid, String... skuCodes) {
        Long delRst = hdelCache(CacheManager.PREFIX_CART + corpIdAppIdUid, skuCodes);
        if (delRst.equals(Constant.REDIS_ERROR_LONG)) {
            LOGGER.error("delCartSkus:{}", skuCodes.toString());
            return FLAG_ERROR;
        }
        return delRst.intValue();
    }

    /**
     * 获取缓存的sku
     *
     * @param skuCode
     * @return
     */
    public CacheSku getCacheSku(String skuCode) {
        CacheSku cacheSku = getCache(PREFIX_CART_SKU + skuCode, CacheSku.class);
        return cacheSku;
    }

    /**
     * 拼接用户标记串
     *
     * @param uid
     * @param appId
     * @param corpId
     * @return
     */
    public String getCorpIdAppIdUId(Integer uid, Integer appId, Integer corpId) {
        StringBuffer sbf = new StringBuffer();
        sbf.append(corpId).append("_").append(appId).append("_").append(uid);
        return sbf.toString();
    }

    /**
     * 设置缓存sku的有效期
     * @param cacheSku
     * @return
     */
    public int setExpireCacheSku(CacheSku cacheSku) {
        int expireTime = sysConfService.getVal(CACHE_CART_SKU_EXPIRE_TIME);
        if (expireTime <= 0) {
            LOGGER.warn("SET_EXPIRE_CACHE_SKU expireTime<=0,set default val:{}", expireTime);
            expireTime = CACHE_CART_SKU_DEFAULT_EXPIRE_TIME;
        }
        String str = cacheOperationUtil.setex(PREFIX_CART_SKU + cacheSku.getSkuCode(), JSONObject.toJSONString(cacheSku),
                expireTime);
        if (Constant.REDIS_ERROR_STRING.equals(str)) {
            LOGGER.error("SET_EXPIRE_CACHE_SKU ERROR,keyCode:{},rtn str:{}", PREFIX_CART_SKU + cacheSku.getSkuCode(), str);
            return FLAG_ERROR;
        }
        return Constant.FLAG_SUCCESS;
    }

    public static void main(String[] args) {
        CartWrap cartWrap = new CartWrap();
        List<CartSkuModel> invalidSkus = new ArrayList<>();
        CartSkuModel cartSkuModel = new CartSkuModel();
        cartSkuModel.setAddTime(123);
        cartSkuModel.setCount(123);
        cartSkuModel.setPrice(11);
        cartSkuModel.setSkuId(131313);
        invalidSkus.add(cartSkuModel);
        cartWrap.setInvalidSkus(invalidSkus);

        System.out.println(JSONObject.toJSONString(cartWrap));

        System.out.println(JSONObject.parseObject(JSONObject.toJSONString(cartWrap), new TypeReference<CartWrap>() {
        }).getInvalidSkus().get(0).getSkuCode());
    }

    /**
     * 获取用户购物车sku总数
     *
     * @param corpId
     * @param appId
     * @param uid
     * @return
     */
    public int getCartSkuCount(Integer uid, Integer appId, Integer corpId) {
        StringBuffer sbf = new StringBuffer(CacheManager.PREFIX_CART);
        String userCacheSkusKey = sbf.append(corpId).append("_").append(appId).append("_").append(uid).toString();
        Long len = cacheOperationUtil.hlen(userCacheSkusKey);
        LOGGER.info("getCartSkuCount len:{}", len);
        return len == null ? 0 : len.intValue();
    }

    /**
     * 获取用户缓存购物车全部sku
     *
     * @param uid
     * @param appId
     * @param corpId
     * @return
     */
    public Map<String, String> getCartSkus(Integer uid, Integer appId, Integer corpId) {
        StringBuffer sbf = new StringBuffer(CacheManager.PREFIX_CART);
        // CART:CORPID_APPID_UID
        Map<String, String> cacheCartSkuMap = cacheOperationUtil
                .hgetAll(sbf.append(corpId).append("_").append(appId).append("_").append(uid).toString());
        LOGGER.info("getCartSkus, uid:{},cacheCartSkuMap:{}", uid, cacheCartSkuMap);
        if (cacheCartSkuMap.containsKey(Constant.REDIS_ERROR_STRING)) {
            LOGGER.error("getCartSkus,uid:{},REDIS_ERROR_STRING", uid);
        }
        return cacheCartSkuMap;
    }

    public String getCartAlias(String key) {
        String alias = cacheOperationUtil.get(PREFIX_CART_ALIAS + key);
        if (Constant.REDIS_ERROR_STRING.equals(alias)) {
            LOGGER.error("getCartAlias ERROR,key:{},alias:{}", PREFIX_CART_ALIAS + key, alias);
        }
        return alias;
    }

    public int setCartAlias(String key, String alias) {
        cacheOperationUtil.set(PREFIX_CART_ALIAS + key, alias);
        return Constant.FLAG_SUCCESS;
    }

    public int setMergeCartErr(String key, String errNum) {
        cacheOperationUtil.hset(CART_MERGE_ERR_LOG, key, errNum);
        return Constant.FLAG_SUCCESS;
    }

    public String getMergeCartLog(String key) {
        String val = cacheOperationUtil.hget(CART_MERGE_ERR_LOG, key);
        if (Constant.REDIS_ERROR_STRING.equals(val)) {
            LOGGER.error("getMergeCartLog ERROR,key:{},val:{}", CART_MERGE_ERR_LOG + key, val);
        }
        return val;
    }

    /**
     * 设置购物车sku库存不足检查时间
     *
     * @param skuCode
     * @return
     */
    public int setLessStoreCheckTime(String skuCode) {
        cacheOperationUtil.setex(PREFIX_CART_ADD_SKU_LESS_STORE_CHECK_TIME + skuCode, DateUtil.getCurTimeMillis() + "", CART_ADD_SKU_LESS_STORE_CHECK_TIME);
        LOGGER.info("setLessStoreCheckTime ,key:{},val:{}", PREFIX_CART_ADD_SKU_LESS_STORE_CHECK_TIME + skuCode);
        return Constant.FLAG_SUCCESS;
    }

    public int setCartSku(String corpIdAppIdUid, CartSkuModel userSkuModel) {
        Long setL = cacheOperationUtil.hset(CacheManager.PREFIX_CART + corpIdAppIdUid, userSkuModel.getSkuCode(),
                JSONObject.toJSONString(userSkuModel));
        if (Constant.REDIS_ERROR_LONG.equals(setL)) {
            LOGGER.error("hsetCache ERROR,corpId_appId_uid:{},field:{},rtn str:{}", corpIdAppIdUid,
                    userSkuModel.getSkuCode(), setL);
            return FLAG_ERROR;
        }
        return Constant.FLAG_SUCCESS;
    }

    /**
     * 删除缓存sku
     * @param skuCode
     * @return
     */
    public Long delCacheSku(String skuCode) {
        Long rst = cacheOperationUtil.del(PREFIX_CART_SKU + skuCode);
        return rst;
    }
}
