package cn.nome.saas.cart.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.JsonUtils;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.enums.ProductEnum;
import cn.nome.saas.cart.enums.SkuEnum;
import cn.nome.saas.cart.enums.StatusCode;
import cn.nome.saas.cart.feign.CampaignInfoResult;
import cn.nome.saas.cart.feign.CampaignModel;
import cn.nome.saas.cart.feign.SkuCodeReq;
import cn.nome.saas.cart.feign.SkuModel;
import cn.nome.saas.cart.model.*;
import cn.nome.saas.cart.repository.entity.*;
import cn.nome.saas.cart.scheduled.AsyncScheduled;
import cn.nome.saas.cart.service.CartService;
import cn.nome.saas.cart.service.SysConfService;
import cn.nome.saas.cart.utils.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.DomainEvents;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 购物车聚合层服务：处理具体业务逻辑，封装、转换数据等
 * @author chentaikuang
 */
@Component
public class CartServiceManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final String QRY_CART_SWITCH = "QRY_CART_SWITCH";

    @Autowired
    private CartService cartService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheGenerateUtil cacheGenerateUtil;

    @Autowired
    private SysConfService sysConfService;

    @Autowired
    private ProductConvertManager productConvertManager;

    @Autowired
    private PromoConvertManager promoConvertManager;

    /**
     * 线程池异步操作工具类
     */
    @Autowired
    @Lazy(value = true)
    private AsyncScheduled asyncScheduled;

    // 操作过频时间
    @Value("frequently_time")
    private static long FREQUENTLY_TIME;
    /**
     * 至少size >= 2才有排序的必要
     */
    private final int COLLECTION_SIZE_IS_2 = 2;
    /**
     * kafka开关
     */
    private final String SHOPPING_LIST_KAFKA_SWITCH = "SHOPPING_LIST_KAFKA_SWITCH";
    private final String SHOPPING_LIST_SWITCH = "SHOPPING_LIST_SWITCH";

    @Autowired
    private JoinForkUtil joinForkUtil;

    /**
     * 删除SKU
     *
     * @param corpId
     * @param appId
     * @param uid
     * @param skuCodeList
     */
    public int delSkuCodes(Integer corpId, Integer appId, Integer uid, List<String> skuCodeList) {
        int rtn = 0;
        if (skuCodeList.size() == 0) {
            LOGGER.info("DEL_SKU_CODES ,skuCodes null,uid:{}", uid);
            return rtn;
        }
        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(uid, appId, corpId);
        LOGGER.info("DEL_SKU_CODES ,corpIdAppIdUid:{}", corpIdAppIdUid);

        String alias = getCartAlias(uid, appId, corpId);
        Assert.notNull(alias, StatusCode.CART_NO_FOUND.getMsg());

        String[] skuCodes = new String[skuCodeList.size()];
        for (int i = 0; i < skuCodeList.size(); i++) {
            skuCodes[i] = skuCodeList.get(i).toString();
        }
        // 删除sku不需要判断缓存时间，直接删除
        try {
            rtn = cacheManager.delCartSkus(corpIdAppIdUid, skuCodes);
            LOGGER.info("DEL_SKU_CODES ,rtn:{}", rtn);
            if (rtn <= Constant.FLAG_ERROR) {
                LOGGER.error("DEL_SKU_CODES, do error and return:{}", rtn);
                return rtn;
            } else if (rtn >= Constant.FLAG_SUCCESS) {
                // 删除sku不需要判断缓存时间，直接删表
                DelCartItemDO delCartItemDO = new DelCartItemDO();
                delCartItemDO.setCorpId(corpId);
                delCartItemDO.setAppId(appId);
                delCartItemDO.setUserId(uid);
                delCartItemDO.setSkuCodes(skuCodeList);
                asyncScheduled.asyncTask(delCartItemDO, Constant.ASYNC_TASK_TYPE_DEL);

                delWxShoppingList(delCartItemDO);
            }
            rtn = getUserSkuCount(uid, appId, corpId);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("DEL_SKU_CODES,del skuCodes redis err,skuCodes:{}", skuCodes.toString());
        }

        return rtn;
    }

    /**
     * 获取购物车别号
     *
     * @param corpId
     * @param appId
     * @param uid
     * @return
     */
    public String getCartAlias(Integer uid, Integer appId, Integer corpId) {
        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(uid, appId, corpId);
        String alias = cacheManager.getCartAlias(corpIdAppIdUid);
        if (StringUtils.isBlank(alias) || Constant.REDIS_ERROR_STRING.equals(alias)) {
            CartDO cart = cartService.selectUserCart(uid, appId, corpId);
            if (cart == null) {
                LOGGER.info("GET_CART_ALIAS,get alias from table null,corpIdAppIdUid:{}", corpIdAppIdUid);
                return null;
            }
            alias = cart.getAlias();
            cacheManager.setCartAlias(corpIdAppIdUid, alias);
            LOGGER.info("GET_CART_ALIAS,get alias from table and set cache.corpIdAppIdUid:{}", corpIdAppIdUid);
        }
        LOGGER.info("GET_CART_ALIAS,get alias:{}.corpIdAppIdUid:{}", alias, corpIdAppIdUid);
        return alias;
    }

    /**
     * 修改购物车sku或数量
     *
     * @param corpId
     * @param appId
     * @param uid
     * @param oldModifyModel
     * @param newModifyModel
     * @return
     */
    public CartSkuModel modifyCart(Integer corpId, Integer appId, Integer uid, SyncSkuModel oldModifyModel,
                                   SyncSkuModel newModifyModel) {
        StringBuffer sbf = new StringBuffer();
        String corpIdAppIdUid = sbf.append(corpId).append("_").append(appId).append("_").append(uid).toString();
        LOGGER.info("MODIFY_CART corpIdAppIdUid:{}", corpIdAppIdUid);
        String alias = getCartAlias(uid, appId, corpId);
        Assert.notNull(alias, StatusCode.CART_NO_FOUND.getMsg());

        // 获取缓存购物车对应SKU
        CacheSku cacheSku = cacheManager.getCacheSku(newModifyModel.getSkuCode());
        // 获取缓存购物车对应SKU为空时，则远程调用服务获取最新SKU信息
        if (cacheSku == null) {
            List<String> skuCodes = new ArrayList<>();
            skuCodes.add(newModifyModel.getSkuCode());
            List<SkuModel> skuData = productConvertManager.getSkuByCodes(appId, corpId, skuCodes);
            if (skuData == null || skuData.isEmpty()) {
                throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
            }
            // 这里只处理单个sku添加购物车，只有一个sku
            SkuModel skuModel = skuData.get(0);
            cacheSku = convertCacheSku(skuModel);

            // 维护cartCacheSku进缓存sku
            cacheManager.setExpireCacheSku(cacheSku);
            LOGGER.debug("MODIFY_CART,reset cacheSku,skuCode:{}", cacheSku.getSkuCode());
        }

        CartSkuModel cartSkuModel = cacheManager.getCartSku(corpIdAppIdUid, oldModifyModel.getSkuCode());
        LOGGER.info("MODIFY_CART ,find cart sku by skuCode:{}", oldModifyModel.getSkuCode());
        Assert.notNull(cartSkuModel, StatusCode.CART_SKU_NO_FOUND.getMsg());

        // 判断skuCode是否相等，相等则为修改数量
        if (newModifyModel.getSkuCode().equals(oldModifyModel.getSkuCode())) {

            LOGGER.info("MODIFY_CART ,change cart sku count,skuCode:{}", oldModifyModel.getSkuCode());

//            if (cartSkuModel != null) {
            // 修改的数量与旧数量一致，或者与缓存中的数量一致直接返回
            if (oldModifyModel.getCount() == newModifyModel.getCount()
                    || newModifyModel.getCount() == cartSkuModel.getCount()) {
                return cartSkuModel;
            }

            // 判断库存是否足于下单数量
            checkSkuStore(newModifyModel.getCount(), cacheSku.getStore(), newModifyModel.getSkuCode(), corpIdAppIdUid);
            // 不相等则修改缓存中数量与更新时间
            cartSkuModel.setCount(newModifyModel.getCount());
            // 修改数量不改变添加时间
            // cartSkuModel.setAddTime(getCurTime());
            cacheManager.setCartSku(corpIdAppIdUid, cartSkuModel);
            // 异步存入数据库
            UpdateCartItemDO updateCartItemDO = new UpdateCartItemDO();
            updateCartItemDO.setAppId(appId);
            updateCartItemDO.setCorpId(corpId);
            updateCartItemDO.setUserId(uid);
            updateCartItemDO.setAlias(alias);
            updateCartItemDO.setCount(newModifyModel.getCount());
            updateCartItemDO.setOldSkuCode(oldModifyModel.getSkuCode());
            asyncScheduled.asyncTask(updateCartItemDO, Constant.ASYNC_TASK_TYPE_MODIFY);

            return cartSkuModel;

        } else {// 判断sku不相等，则为修改商品

            // 新修改sku已存在，要合并数量
            CartSkuModel alreadySku = cacheManager.getCartSku(corpIdAppIdUid, newModifyModel.getSkuCode());
            if (alreadySku != null) {

                LOGGER.info("MODIFY_CART ,merge cart sku already exist,skuCode:{}", alreadySku.getSkuCode());

                // 合并sku保留操作sku的最新加购时间
                if (cartSkuModel.getAddTime() > alreadySku.getAddTime()) {
                    alreadySku.setAddTime(cartSkuModel.getAddTime());
                }

                int totalCount = alreadySku.getCount() + newModifyModel.getCount();
                checkSkuStore(totalCount, alreadySku.getStore(), alreadySku.getSkuCode(), corpIdAppIdUid);

                alreadySku.setCount(totalCount);
                cacheManager.setCartSku(corpIdAppIdUid, alreadySku);

                // 删除旧sku，修改已存在sku的数据
                DelCartItemDO delCartItemDO = new DelCartItemDO();
                delCartItemDO.setAlias(alias);
                List<String> skuCodes = new ArrayList<>(1);
                skuCodes.add(oldModifyModel.getSkuCode());
                delCartItemDO.setSkuCodes(skuCodes);
                asyncScheduled.asyncTask(delCartItemDO, Constant.ASYNC_TASK_TYPE_DEL);

                int rtn = cartService.updateSku(alias, alreadySku.getSkuCode(), alreadySku.getCount());
                LOGGER.info("MODIFY_CART ,merge cart sku already exist,updateSku skuCode:{},rtn:{}", alreadySku.getSkuCode(), rtn);
            } else {// 新修改sku不存在

                // old sku加购时间
                long userSkuAddTime = cartSkuModel.getAddTime();

                // 添加新的商品数据,查询的sku转换用户购物车sku
                cartSkuModel = convertUserSkuModel(newModifyModel.getCount(), cacheSku);
                // 购物车页面加购sku不改变加购时间
                cartSkuModel.setAddTime(userSkuAddTime);
                cacheManager.setCartSku(corpIdAppIdUid, cartSkuModel);

                UpdateCartItemDO updateCartItemDO = new UpdateCartItemDO();
                updateCartItemDO.setAppId(appId);
                updateCartItemDO.setCorpId(corpId);
                updateCartItemDO.setUserId(uid);
                updateCartItemDO.setSkuId(cartSkuModel.getSkuId());
                updateCartItemDO.setCount(newModifyModel.getCount());
                updateCartItemDO.setNewSkuCode(newModifyModel.getSkuCode());
                updateCartItemDO.setOldSkuCode(oldModifyModel.getSkuCode());
                asyncScheduled.asyncTask(updateCartItemDO, Constant.ASYNC_TASK_TYPE_MODIFY);
                LOGGER.info("MODIFY_CART ,change cart sku,old:{},new:{}", oldModifyModel.getSkuCode(), newModifyModel.getSkuCode());
            }

            // 删除旧的商品数据
            cacheManager.delCartSkus(corpIdAppIdUid, String.valueOf(oldModifyModel.getSkuCode()));
            return cartSkuModel;
        }
    }

    public void judgeFrequently(long addTime) {
        if (getCurTime() - addTime < FREQUENTLY_TIME) {
            throw new BusinessException("操作过于频繁，请稍后重试。");
        }
    }

    /**
     * sku无效状态
     *
     * @param status
     * @return
     */
    private boolean isInvalidStatus(int status) {
        return status == SkuEnum.Status.INVALID.getStatus();
    }

    /**
     * sku有效状态
     *
     * @param status
     * @return
     */
    private boolean isValidStats(int status) {
        return status == SkuEnum.Status.VALID.getStatus();
    }

    /**
     * 是否打开缓存开关
     *
     * @return
     */
    private boolean isLoadCartCache() {
        SysConfDO sysConfDO = sysConfService.selectByCode(QRY_CART_SWITCH);
        if (sysConfDO != null) {
            return (StringUtils.isBlank(sysConfDO.getKeyCode()) || sysConfDO.getKeyVal().equals("0")) ? false : true;
        }
        LOGGER.info("LOAD_CART_CACHE_SWITCH:close");
        return false;
    }

    /**
     * 当前时间戳
     *
     * @return
     */
    private long getCurTime() {
        return System.currentTimeMillis();
    }

    /**
     * 添加购物车
     *
     * @param addModel
     * @return
     * @throws Exception
     */
    public Result addCart(AddModel addModel) throws Exception {

        int totalCount = getUserSkuCount(addModel.getUid(), addModel.getAppId(), addModel.getCorpId());
        boolean isLimitAdd = isLimitAdd(totalCount);
        if (isLimitAdd){
            throw new BusinessException(StatusCode.ADD_LIMIT_MAX_COUNT.getCode(), StatusCode.ADD_LIMIT_MAX_COUNT.getMsg());
            //Assert.isTrue(!isLimitAdd,StatusCode.ADD_LIMIT_MAX_COUNT.getMsg());
        }

        String skuCode = addModel.getSku().getSkuCode();
        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(addModel.getUid(), addModel.getAppId(),
                addModel.getCorpId());
        LOGGER.debug("ADD_CART corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, skuCode);

        CacheSku cacheSku = cacheManager.getCacheSku(skuCode);
        if (cacheSku == null || SkuEnum.Status.INVALID.getStatus() == cacheSku.getStatus()) {
            SkuModel skuModel = productConvertManager.getRealTimeSku(skuCode, corpIdAppIdUid);
            cacheSku = convertCacheSku(skuModel);
            cacheManager.setExpireCacheSku(cacheSku);
        }

        // 查询用户缓存购物车是否存在该sku
        CartSkuModel cartSkuModel = cacheManager.getCartSku(corpIdAppIdUid, skuCode);
        if (cartSkuModel != null) {
            incrCartSkuCount(addModel, corpIdAppIdUid, cacheSku, cartSkuModel);
        } else {
            addNewCartSku(addModel, skuCode, corpIdAppIdUid, cacheSku);
        }
        totalCount = getUserSkuCount(addModel.getUid(), addModel.getAppId(), addModel.getCorpId());
        return ResultUtil.handleSuccessReturn(totalCount);
    }

    /**
     * 不存在则新加sku入购物车
     *
     * @param addModel
     * @param skuCode
     * @param corpIdAppIdUid
     * @param cacheSku
     */
    private void addNewCartSku(AddModel addModel, String skuCode, String corpIdAppIdUid, CacheSku cacheSku) {

    	 AddSkuModel addSku = addModel.getSku();
         // 检查总库存
         if (addSku.getCount() > cacheSku.getStore()) {
             SkuModel sku = productConvertManager.getRealTimeSku(skuCode, corpIdAppIdUid);
             if (addSku.getCount() > sku.getStore()) {
                 throw new BusinessException(StatusCode.STORE_NOT_ENOUGH.getCode(), StatusCode.STORE_NOT_ENOUGH.getMsg());
             }
             //实际还有库存，更新缓存sku
             cacheSku = convertCacheSku(sku);
             cacheManager.setExpireCacheSku(cacheSku);
         }

         CartSkuModel cartSku = convertCartSkuModel(addSku.getCount(), addSku.getSeller(), cacheSku);
         int rst = cacheManager.setCartSku(corpIdAppIdUid, cartSku);
         if (rst == 0) {
             throw new BusinessException(StatusCode.ADD_FAIL.getCode(), StatusCode.ADD_FAIL.getMsg());
         }

         addOrModifyCartItem(addModel.getUid(), addModel.getAppId(), addModel.getCorpId(), skuCode, cacheSku.getSkuId(), cartSku.getProductId(), addSku.getCount(), addSku.getSeller());
         addWxShoppingList(addModel);
    }

	/**
     * CacheSku -> CartSkuModel 缓存sku转换成购物车sku
     *
     * @param count
     * @param cacheSku
     * @return
     */
    private CartSkuModel convertCartSkuModel(Integer count, CacheSku cacheSku) {
        CartSkuModel cartSku = new CartSkuModel();
        long curTime = getCurTime();
        cartSku.setCount(count);
        cartSku.setSkuId(cacheSku.getSkuId());
        cartSku.setProductId(cacheSku.getProductId());
        cartSku.setAddTime(curTime);
        cartSku.setName(cacheSku.getName());
        cartSku.setPrice(cacheSku.getPrice());
        cartSku.setSpecVal(cacheSku.getSpecVal());
        cartSku.setStore(cacheSku.getStore());
        cartSku.setImgUrl(cacheSku.getImgUrl());
        cartSku.setStatus(cacheSku.getStatus());
        cartSku.setSkuCode(cacheSku.getSkuCode());
        cartSku.setRefreshTime(curTime);
        return cartSku;
    }

    /**
     * CacheSku -> CartSkuModel 缓存sku转换成购物车sku
     *
     * @param count
     * @param cacheSku
     * @return
     */
    private CartSkuModel convertCartSkuModel(Integer count, Integer seller, CacheSku cacheSku) {
        CartSkuModel cartSku = new CartSkuModel();
        long curTime = getCurTime();
        cartSku.setCount(count);
        cartSku.setSeller(seller);
        cartSku.setSkuId(cacheSku.getSkuId());
        cartSku.setProductId(cacheSku.getProductId());
        cartSku.setAddTime(curTime);
        cartSku.setName(cacheSku.getName());
        cartSku.setPrice(cacheSku.getPrice());
        cartSku.setSpecVal(cacheSku.getSpecVal());
        cartSku.setStore(cacheSku.getStore());
        cartSku.setImgUrl(cacheSku.getImgUrl());
        cartSku.setStatus(cacheSku.getStatus());
        cartSku.setSkuCode(cacheSku.getSkuCode());
        cartSku.setRefreshTime(curTime);
        return cartSku;
    }

	/**
     * 添加存在的购物车sku，数量增加方法-v2
     *
     * @param addModel
     * @param corpIdAppIdUid
     * @param cacheSku
     * @param cartSku
     */
    private void incrCartSkuCount(AddModel addModel, String corpIdAppIdUid, CacheSku cacheSku, CartSkuModel cartSku) {

        int existCount = cartSku.getCount();
        int cacheStore = cacheSku.getStore();
        AddSkuModel addSku = addModel.getSku();
        String skuCode = addSku.getSkuCode();

        //检查存在的sku数量是否超限
        if (existCount >= cacheStore) {
            SkuModel realTimeSku = productConvertManager.getRealTimeSku(skuCode, corpIdAppIdUid);
            if (existCount >= realTimeSku.getStore()) {
                throw new BusinessException(StatusCode.STORE_LIMIT.getCode(), convertStoreLimitTips(existCount));
            }

            //实际还有库存，更新缓存sku
            cacheSku = convertCacheSku(realTimeSku);
            cacheManager.setExpireCacheSku(cacheSku);

            //导购员信息以第一次记录为准
            cartSku = convertCartSku(realTimeSku, existCount, cartSku.getSeller() != null ? cartSku.getSeller() : addModel.getSku().getSeller(), cartSku.getAddTime());
            cacheManager.setCartSku(corpIdAppIdUid, cartSku);
            LOGGER.info("incrCartSkuCount ,refresh cacheSku and reset cartSku:{}", skuCode);
        }

        // 检查总库存
        int totalCount = existCount + addSku.getCount();
        if (totalCount > cacheStore) {
            SkuModel sku = productConvertManager.getRealTimeSku(skuCode, corpIdAppIdUid);
            if (totalCount > sku.getStore()) {
                throw new BusinessException(StatusCode.STORE_NOT_ENOUGH.getCode(), StatusCode.STORE_NOT_ENOUGH.getMsg());
            }
            //实际还有库存，更新缓存sku
            cacheSku = convertCacheSku(sku);
            cacheManager.setExpireCacheSku(cacheSku);
        }

        cartSku.setCount(totalCount);

        //若导购员为空时增加导购员信息, 否则以第一次导购员为准
        if (cartSku.getSeller() == null) {
            cartSku.setSeller(addModel.getSku().getSeller());
        }

        cartSku.setAddTime(getCurTime());
        cartSku.setRefreshTime(getCurTime());
        //之前购物车sku是无效状态，则重置状态
        if (!isValidStats(cartSku.getStatus())) {
            cartSku.setStatus(cacheSku.getStatus());
        }
        // 更新数量后写回缓存
        int rst = cacheManager.setCartSku(corpIdAppIdUid, cartSku);
        if (rst > 0) {
            addOrModifyCartItem(addModel.getUid(), addModel.getAppId(), addModel.getCorpId(),
                    skuCode, cartSku.getSkuId(), cartSku.getProductId(), totalCount, cartSku.getSeller());
        }
    }

//    /**
//     * 添加或修改cartItem
//     *
//     * @param uid
//     * @param appId
//     * @param corpId
//     * @param skuCode
//     * @param skuId
//     * @param productId
//     * @param totalCount
//     */
//    private void addOrModifyCartItem(int uid, int appId, int corpId,
//                                     String skuCode, int skuId, int productId, int totalCount) {
//        // 是否存在购物车，不存在则新增
//        String alias = createNewCartRtnAlias(uid, appId, corpId);
//        AddCartItemDO addCartItemDO = new AddCartItemDO();
//        addCartItemDO.setAppId(appId);
//        addCartItemDO.setCorpId(corpId);
//        addCartItemDO.setUserId(uid);
//        addCartItemDO.setCount(totalCount);
//        addCartItemDO.setAlias(alias);
//        addCartItemDO.setSkuCode(skuCode);
//        addCartItemDO.setSkuId(skuId);
//        addCartItemDO.setProductId(productId);
//        asyncScheduled.asyncTask(addCartItemDO, Constant.ASYNC_TASK_TYPE_ADD_OR_MODIFY);
//    }

    /**
     * 添加或修改cartItem
     *
     * @param uid
     * @param appId
     * @param corpId
     * @param skuCode
     * @param skuId
     * @param productId
     * @param totalCount
     */
    private void addOrModifyCartItem(int uid, int appId, int corpId,
                                     String skuCode, int skuId, int productId, int totalCount, Integer seller) {
        // 是否存在购物车，不存在则新增
        String alias = createNewCartRtnAlias(uid, appId, corpId);
        AddCartItemDO addCartItemDO = new AddCartItemDO();
        addCartItemDO.setAppId(appId);
        addCartItemDO.setCorpId(corpId);
        addCartItemDO.setUserId(uid);
        addCartItemDO.setCount(totalCount);
        addCartItemDO.setSeller(seller);
        addCartItemDO.setAlias(alias);
        addCartItemDO.setSkuCode(skuCode);
        addCartItemDO.setSkuId(skuId);
        addCartItemDO.setProductId(productId);
        asyncScheduled.asyncTask(addCartItemDO, Constant.ASYNC_TASK_TYPE_ADD_OR_MODIFY);
    }

	/**
     * 添加商品限制
     *
     * @param totalCount
     * @return
     */
    private boolean isLimitAdd(int totalCount) {
        SysConfDO sc = sysConfService.selectByCode(Constant.ADD_LIMIT_MAX_COUNT_SWITCH);
        if (sc != null && totalCount >= Integer.valueOf(sc.getKeyVal())) {
            return true;
        }
        return false;
    }

	/**
     * 添加商品成功，推送给微信购物单接口
     *
     * @param addModel
     */
    private void addWxShoppingList(AddModel addModel) {
        if (isOpenWxShoppingListSwitch(SHOPPING_LIST_SWITCH)) {
            SysConfDO conf = sysConfService.selectByCode(SHOPPING_LIST_KAFKA_SWITCH);
            if (conf != null && StringUtils.isNotBlank(conf.getKeyVal()) && Boolean.valueOf(conf.getKeyVal())) {
                WxShoppingListModel wxModel = new WxShoppingListModel();
                wxModel.setSkuCode(addModel.getSku().getSkuCode());
                wxModel.setType(Constant.WX_SHOPPING_LIST_ADD);
                wxModel.setUid(addModel.getUid());
                wxModel.setAppId(addModel.getAppId());
                wxModel.setCorpId(addModel.getCorpId());
                asyncScheduled.asyncTask(JSONObject.toJSONString(wxModel), Constant.ASYNC_TASK_TYPE_SEND_SHOPPING_LIST);
            } else {
                asyncScheduled.asyncTask(addModel, Constant.ASYNC_TASK_TYPE_ADD_SHOPPING_LIST);
            }
        }
    }

    /**
     * 删除商品成功，推送给微信购物单接口
     *
     * @param delCartItemDO
     */
    private void delWxShoppingList(DelCartItemDO delCartItemDO) {
        if (isOpenWxShoppingListSwitch(SHOPPING_LIST_SWITCH)) {
            SysConfDO conf = sysConfService.selectByCode(SHOPPING_LIST_KAFKA_SWITCH);
            if (conf != null && StringUtils.isNotBlank(conf.getKeyVal()) && Boolean.valueOf(conf.getKeyVal())) {
                List<String> skuCodes = delCartItemDO.getSkuCodes();
                for (String skuCode : skuCodes) {
                    WxShoppingListModel wxModel = new WxShoppingListModel();
                    wxModel.setSkuCode(skuCode);
                    wxModel.setType(Constant.WX_SHOPPING_LIST_DEL);
                    wxModel.setUid(delCartItemDO.getUserId());
                    wxModel.setAppId(delCartItemDO.getAppId());
                    wxModel.setCorpId(delCartItemDO.getCorpId());
                    asyncScheduled.asyncTask(JSONObject.toJSONString(wxModel), Constant.ASYNC_TASK_TYPE_SEND_SHOPPING_LIST);
                }
            } else {
                asyncScheduled.asyncTask(delCartItemDO, Constant.ASYNC_TASK_TYPE_DEL_SHOPPING_LIST);
            }
        }
    }

    private boolean isOpenWxShoppingListSwitch(String shoppingListSwitch) {
        SysConfDO sc = sysConfService.selectByCode(shoppingListSwitch);
        if (sc != null && Boolean.valueOf(sc.getKeyVal())) {
            return true;
        }
        return false;
    }

    /**
     * 转换库存限购提示语
     *
     * @param existCount
     * @return
     */
    private String convertStoreLimitTips(int existCount) {
        return StatusCode.STORE_LIMIT.getMsg().replace("{0}", existCount + "");
    }

    /**
     * 库存检查
     *
     * @param addCount
     * @param storeCount
     * @param skuCode
     * @param corpIdAppIdUid
     */
    private void checkSkuStore(int addCount, int storeCount, String skuCode, String corpIdAppIdUid) {
        if (addCount > storeCount) {

            List<SkuModel> skus = getRealSku(skuCode, corpIdAppIdUid);
            if (skus == null || skus.isEmpty()) {
                throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
            }
            SkuModel sku = skus.get(0);
            if (addCount <= sku.getStore()) {
                //实际还有库存，更新缓存sku
                CacheSku cacheSku = convertCacheSku(sku);
                cacheManager.setExpireCacheSku(cacheSku);
                LOGGER.info("CHECK_SKU_STORE,store enough，and update cache sku after qryBySkuCode:{}", skuCode);
                return;
            }
            LOGGER.info(StatusCode.STORE_NOT_ENOUGH.getMsg() + ",skuCode:{}", skuCode);
            throw new BusinessException(StatusCode.STORE_NOT_ENOUGH.getCode(), StatusCode.STORE_NOT_ENOUGH.getMsg());
        }
    }

    private List<SkuModel> getRealSku(String skuCode, String corpIdAppIdUid) {
        //目前库存释放未同步购物车，在库存不足情况下，实时查询校验一次
        String[] idArr = corpIdAppIdUid.split("_");
        List<String> skuCodes = new ArrayList<>(1);
        skuCodes.add(skuCode);
        //corpId_appId_uid
        List<SkuModel> skus = productConvertManager.getSkuByCodes(Integer.valueOf(idArr[1]), Integer.valueOf(idArr[0]), skuCodes);
        LOGGER.info("CHECK_SKU_STORE,store no enough，to check by query table,skuCode:{}", skuCode);
        return skus;
    }

    /**
     * 创建新的购物车并返回别号
     *
     * @param uid
     * @param appId
     * @param corpId
     * @return
     */
    private String createNewCartRtnAlias(Integer uid, Integer appId, Integer corpId) {
        String alias = getCartAlias(uid, appId, corpId);
        if (StringUtils.isBlank(alias)) {
            //alias = GenerateRandomStrUtil.uuid();
            //替换购物车别号生成规则
            alias = GenerateRandomStrUtil.newCartAlias(uid);
            CartDO cart = new CartDO();
            cart.setUserId(uid);
            cart.setCorpId(corpId);
            cart.setAppId(appId);
            cart.setAlias(alias);
            int rtn = cartService.addNewCart(cart);
            if (rtn == 0) {
                LOGGER.error("[CREATE_NEW_CART_RTN_ALIAS] uid:{},rtn:{}", uid, rtn);
                return null;
            }
            cartService.addGlobalCart(cart);
            LOGGER.info("[CREATE_NEW_CART_RTN_ALIAS] addGlobalCart uid:{},alias:{}", uid, alias);
//            asyncScheduled.asyncTask(cart, Constant.ASYNC_TASK_TYPE_GLOBAL_CART_ADD);
        }
        return alias;
    }

    /**
     * CacheSku -> CartSkuModel 缓存sku转换成购物车sku
     *
     * @param count
     * @param cacheSku
     * @return
     */
    private CartSkuModel convertUserSkuModel(Integer count, CacheSku cacheSku) {
        CartSkuModel userSkuModel = new CartSkuModel();
        userSkuModel.setCount(count);
        userSkuModel.setSkuId(cacheSku.getSkuId());
        userSkuModel.setProductId(cacheSku.getProductId());
        userSkuModel.setAddTime(getCurTime());
        userSkuModel.setName(cacheSku.getName());
        userSkuModel.setPrice(cacheSku.getPrice());
        userSkuModel.setSpecVal(cacheSku.getSpecVal());
        userSkuModel.setStore(cacheSku.getStore());
        userSkuModel.setImgUrl(cacheSku.getImgUrl());
        userSkuModel.setStatus(cacheSku.getStatus());
        userSkuModel.setSkuCode(cacheSku.getSkuCode());
        return userSkuModel;
    }

    /**
     * 表中查出来的SKU转换成缓存购物车sku
     *
     * @param skuModel
     * @return
     */
    private CacheSku convertCacheSku(SkuModel skuModel) {
        CacheSku cartCacheSku = new CacheSku();
        cartCacheSku.setAddTime(getCurTime());
        cartCacheSku.setPrice(skuModel.getPrice());
        cartCacheSku.setName(skuModel.getName());
        cartCacheSku.setProductId(skuModel.getProductId());
        cartCacheSku.setSkuId(skuModel.getSkuId());
        cartCacheSku.setSpecVal(skuModel.getSpecVal());
        cartCacheSku.setStatus(convertSkuStatus(skuModel, 0));
        cartCacheSku.setStore(skuModel.getStore());
        cartCacheSku.setImgUrl(skuModel.getImgUrl());
        cartCacheSku.setSkuCode(skuModel.getSkuCode());
        return cartCacheSku;
    }

    /**
     * 查询购物车数据
     *
     * @param loadModel
     * @return
     */
    @Deprecated
    public CartWrap loadCart(LoadModel loadModel) {

        CartWrap cartWrap = null;

        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(loadModel.getUid(), loadModel.getAppId(),
                loadModel.getCorpId());

        if (isLoadCartCache()) {

            LOGGER.debug("QUERY_CART ,load cache cart switch:open,corpIdAppIdUid:{}", corpIdAppIdUid);

            // 获取用户缓存购物车sku
            List<CartSkuModel> userCartSkus = getCartSkus(loadModel.getUid(), loadModel.getAppId(),
                    loadModel.getCorpId());
            if (userCartSkus == null || userCartSkus.isEmpty()) {
                LOGGER.debug("QUERY_CART ,user cart isEmpty,corpIdAppIdUid:{}", corpIdAppIdUid);
                return cartWrap;
            }

            List<String> syncQrySkuCodes = null;// 同步
            List<String> asyncQrySkuCodes = null;// 异步

            Iterator<CartSkuModel> userSkuItr = userCartSkus.iterator();
            Map<String, CartSkuModel> userCartSkusMap = new HashMap<>();
            while (userSkuItr.hasNext()) {

                CartSkuModel userSku = userSkuItr.next();
                String userSkuCode = userSku.getSkuCode();
                userCartSkusMap.put(userSkuCode, userSku);

                // 判断缓存分代有效期
                long addTime = userSku.getAddTime();
                boolean flag = cacheGenerateUtil.isNewGeneration(addTime);
                if (flag) {
                    // 新生代，不处理直接返回
                    LOGGER.debug("QUERY_CART ,CACHE_AT_NEW_GENERATION,corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, userSkuCode);
                } else if (cacheGenerateUtil.isYoungGeneration(addTime)) {
                    LOGGER.debug("QUERY_CART ,CACHE_AT_YOUNG_GENERATION,corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, userSkuCode);
                    // 年轻代，直接返回，但需要异步去更新缓存数据
                    if (asyncQrySkuCodes == null) {
                        asyncQrySkuCodes = new ArrayList<>();
                    }
                    // 提取出需要异步更新的sku
                    asyncQrySkuCodes.add(userSkuCode);
                } else {
                    LOGGER.debug("QUERY_CART ,CACHE_AT_OLD_GENERATION,corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, userSkuCode);
                    // 年老代，抛弃缓存数据，取实时sku
                    if (syncQrySkuCodes == null) {
                        syncQrySkuCodes = new ArrayList<>();
                    }
                    // 提取出需要同步更新的sku
                    syncQrySkuCodes.add(userSkuCode);
                    // 删掉要同步的sku。剩下的sku是可直接使用的
                    userSkuItr.remove();
                }
            }

            // 同步更新购物车商品
            if (syncQrySkuCodes != null && !syncQrySkuCodes.isEmpty()) {

                LOGGER.info("QUERY_CART sync get sku data from tab,syncQrySkuCodes:{}", syncQrySkuCodes);

                List<SkuModel> qrySkus = productConvertManager.getSkuByCodes(loadModel.getAppId(),
                        loadModel.getCorpId(), syncQrySkuCodes);

                if (qrySkus != null && !qrySkus.isEmpty()) {

                    for (SkuModel qrySku : qrySkus) {
                        CartSkuModel userSku = userCartSkusMap.get(qrySku.getSkuCode());
                        CartSkuModel cartSkuModel = convertCartSku(qrySku, userSku.getCount(),userSku.getAddTime());

                        userCartSkus.add(cartSkuModel);
                        cacheManager.setCartSku(corpIdAppIdUid, cartSkuModel);
                        LOGGER.debug("QUERY_CART ,sync get sku and reset back UserCart:{}", qrySku.getSkuCode());

                        CacheSku cacheSku = convertCacheSku(qrySku);
                        cacheManager.setExpireCacheSku(cacheSku);
                        LOGGER.debug("QUERY_CART ,sync get sku and reset back CacheSku:{}", qrySku.getSkuCode());
                    }
                }
            }

            // 异步更新用户购物车sku
            if (asyncQrySkuCodes != null && !asyncQrySkuCodes.isEmpty()) {
                LOGGER.info("QUERY_CART async update sku cache,asyncQrySkuCodes:{}", asyncQrySkuCodes);

                List<SkuModel> qrySkus = productConvertManager.getSkuByCodes(loadModel.getAppId(),
                        loadModel.getCorpId(), asyncQrySkuCodes);
                if (qrySkus != null && !qrySkus.isEmpty()) {
                    for (SkuModel qrySku : qrySkus) {
                        CartSkuModel sku = userCartSkusMap.get(qrySku.getSkuCode());
                        CartSkuModel cartSkuModel = convertCartSku(qrySku, sku.getCount(),sku.getAddTime());
                        AsyncAddCartSku addUserCartSku = new AsyncAddCartSku(loadModel.getUid(),
                                loadModel.getAppId(), loadModel.getCorpId(), cartSkuModel);
                        asyncScheduled.asyncTask(addUserCartSku, Constant.ASYNC_TASK_TYPE_CART_SKU_ADD);

                        CacheSku cacheSku = convertCacheSku(qrySku);
                        cacheManager.setExpireCacheSku(cacheSku);
                        LOGGER.debug("QUERY_CART ,async get sku and reset back CacheSku:{}", qrySku.getSkuCode());
                    }
                }
            }
            // 将购物车数据封装返回前端
            cartWrap = convertCartWrap(loadModel, userCartSkus);
        } else {

            LOGGER.info("QUERY_CART ,corpIdAppIdUid:{},query data from tab", corpIdAppIdUid);

            // 实时获取用户购物车数据
            List<CartItemDO> cartItemDOs = getDetail(loadModel.getUid(), loadModel.getAppId(), loadModel.getCorpId());
            if (cartItemDOs != null && !cartItemDOs.isEmpty()) {

                // 获取sku实时数据 start
                Set<String> skuCodes = new HashSet<String>();
                cartItemDOs.stream().forEach(item -> skuCodes.add(item.getSkuCode()));

                List<SkuModel> skuData = productConvertManager.getSkuByCodes(loadModel.getAppId(),
                        loadModel.getCorpId(), new ArrayList<>(skuCodes));

                if (skuData == null || skuData.isEmpty()) {
                    LOGGER.info(StatusCode.SKU_NO_FOUND.getMsg());
                    return cartWrap;
                }
                Map<String, SkuModel> skuModelMap = getSkuModelMap(skuData);
                // 获取sku实时数据 end
                List<CartSkuModel> cartSkuModels = getCartSkuModels(cartItemDOs, skuModelMap);

                // 将购物车数据封装返回前端
                cartWrap = convertCartWrap(loadModel, cartSkuModels);
            }
        }
        LOGGER.debug("QUERY_CART ,corpIdAppIdUid:{},return cartWrap:{}", corpIdAppIdUid, cartWrap);
        return cartWrap;
    }

    private Map<String, SkuModel> getSkuModelMap(List<SkuModel> skusData) {
        Map<String, SkuModel> skuModelMap = new HashMap<>();
        for (SkuModel sku : skusData) {
            skuModelMap.put(sku.getSkuCode(), sku);
        }
        return skuModelMap;
    }

    private List<CartSkuModel> getCartSkuModels(List<CartItemDO> cartItemDOs, Map<String, SkuModel> skuModelMap) {
        // sku实时数赋值给购物车SKU start
        List<CartSkuModel> cartSkuModels = new ArrayList<>();
        for (CartItemDO cartItemDO : cartItemDOs) {
            if (skuModelMap.containsKey(cartItemDO.getSkuCode())) {
                SkuModel skuModel = skuModelMap.get(cartItemDO.getSkuCode());
                CartSkuModel cartSkuModel = convertCartSku(skuModel, cartItemDO.getCount(), cartItemDO.getSeller(), cartItemDO.getCreatedAt().getTime());
                cartSkuModels.add(cartSkuModel);
            }
        }
        // sku实时数赋值给购物车SKU end
        return cartSkuModels;
    }

    /**
     * 从缓存获取用户购物车sku,有异常则读表数据
     *
     * @param uid
     * @param appId
     * @param corpId
     * @return
     */
    private List<CartSkuModel> getCartSkus(Integer uid, Integer appId, Integer corpId) {
        Map<String, String> cacheCartSkuMap = cacheManager.getCartSkus(uid, appId, corpId);

        if (cacheCartSkuMap == null || cacheCartSkuMap.isEmpty()) {
            return null;
        } else if (cacheCartSkuMap.containsKey(Constant.REDIS_ERROR_STRING)) {
            LOGGER.error("GET_CACHE_CART_SKUS cache unavalid ，query tab,uid:{}", uid);
            List<CartItemDO> items = getDetail(uid, appId, corpId);
            if (items == null || items.isEmpty()) {
                return null;
            }
            Set<String> skuCodes = new HashSet<String>();
            items.stream().forEach(item -> skuCodes.add(item.getSkuCode()));

            List<SkuModel> skuData = productConvertManager.getSkuByCodes(appId, corpId, new ArrayList<>(skuCodes));

            Map<String, SkuModel> skuModelMap = getSkuModelMap(skuData);
            List<CartSkuModel> cartSkuModels = getCartSkuModels(items, skuModelMap);
            return cartSkuModels;
        }
        List<String> cartStrList = new ArrayList<>();
        cacheCartSkuMap.entrySet().stream().forEach(entry -> cartStrList.add(entry.getValue()));
        return JsonUtil.jsonToList(cartStrList.toString(), CartSkuModel.class);
    }

    /**
     * SKU状态转换，默认有效
     *
     * @param qrySku
     * @param oldCount
     * @return
     */
    private int convertSkuStatus(SkuModel qrySku, int oldCount) {
        int status = SkuEnum.Status.VALID.getStatus();
        if (qrySku.getProductStatus() == ProductEnum.Status.DOWN.getStatus()
                || qrySku.getSkuStatus() == SkuEnum.Status.DEL.getStatus()) {
            status = SkuEnum.Status.INVALID.getStatus();
        } else if (oldCount > qrySku.getStore()) {
            // 购物车中SKU数量大于表中库存数，则库存不足
            status = SkuEnum.Status.LESS_STORE.getStatus();
        }
        return status;
    }

    /**
     * 封装返回购物车数据
     *
     * @param loadModel
     * @param userCartSkuModels
     * @return
     */
    @Deprecated
    private CartWrap convertCartWrap(LoadModel loadModel, List<CartSkuModel> userCartSkuModels) {
        CartWrap cartWrap = null;
        if (userCartSkuModels != null && !userCartSkuModels.isEmpty()) {

            // 提取购物车全部sku的productId，查询到对应的商品集ID start
            Set<Integer> productIds = new HashSet<>();
            userCartSkuModels.stream().forEach(cs -> productIds.add(cs.getProductId()));
            Map<Integer, Integer> productIdProductSetIdsMap = productConvertManager.getSetIdMapByIds(loadModel.getUid(),
                    loadModel.getAppId(), loadModel.getCorpId(), productIds);
            // 提取购物车全部sku的productId，查询到对应的商品集ID end

            Set<Integer> productSetIdsSet = new HashSet<>();
            if (productIdProductSetIdsMap != null && !productIdProductSetIdsMap.isEmpty()) {
                productIdProductSetIdsMap.entrySet().stream().forEach(entry -> productSetIdsSet.add(entry.getValue()));
            }
            List<Integer> productSetIds = new ArrayList<>(productSetIdsSet);
            Map<Integer, CampaignModel> productSetIdRuleMap = null;
            if (!productSetIds.isEmpty()) {
                productSetIdRuleMap = promoConvertManager.getProductSetIdCampaignMap(loadModel.getCorpId(),
                        loadModel.getAppId(), loadModel.getUid(), productSetIds);
            }
            LOGGER.info("CONVERT_CART_WRAP productSetIdRuleMap:{}", productSetIdRuleMap);
            // 提取所有的商品集进行活动查询 end

            // 无效商品
            List<CartSkuModel> invalidSkus = new ArrayList<>();
            // 库存不足商品
            List<CartSkuModel> lessStockSkus = new ArrayList<>();

            // 根据商品集对购物车商品进行分组
            Map<Integer, List<CartSkuModel>> productSetIdSkuMap = new HashMap<>();
            for (CartSkuModel userSku : userCartSkuModels) {

                int productId = userSku.getProductId();
                int status = userSku.getStatus();
                LOGGER.info("skuCode:{},status:{}", userSku.getSkuCode(), status);

                if (isValidStats(status)) {

                    Integer productSetId = productIdProductSetIdsMap.get(productId);

                    // 没有商品集ID的sku，则统一归在为NULL_PRODUCT_SET_ID的虚拟商品集上
                    if (productSetId == null) {
                        productSetId = ProductConvertManager.NULL_PRODUCT_SET_ID;
                    }
                    // 根据商品集分组 start
                    if (productSetIdSkuMap.containsKey(productSetId)) {
                        List<CartSkuModel> groupSkus = productSetIdSkuMap.get(productSetId);
                        groupSkus.add(userSku);
                    } else {
                        List<CartSkuModel> groupSkus = new ArrayList<>();
                        groupSkus.add(userSku);
                        productSetIdSkuMap.put(productSetId, groupSkus);
                    }
                    // 根据商品集分组 end
                } else if (isInvalidStatus(status)) {
                    invalidSkus.add(userSku);
                } else {
                    lessStockSkus.add(userSku);
                }

            }

            // 根据有效商品集分组提取活动信息，封装集合 start
            List<ValidSkuModel> validSkuItems = new ArrayList<>();
            LOGGER.info("CONVERT_CART_WRAP uid:{},productSetIdSkuMap:{}", loadModel.getUid(),
                    JSONObject.toJSONString(productSetIdSkuMap));
            Iterator<Entry<Integer, List<CartSkuModel>>> itr = productSetIdSkuMap.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<Integer, List<CartSkuModel>> entry = itr.next();
                List<CartSkuModel> cartSkus = entry.getValue();
                Integer productSetId = entry.getKey();
                LOGGER.info("CONVERT_CART_WRAP productSetId:{}", productSetId);

                // 商品集已划分对应多个sku
                if (productSetIdRuleMap != null && !productSetIdRuleMap.isEmpty()
                        && productSetIdRuleMap.containsKey(productSetId)) {

                    ValidSkuModel validSkuItem = new ValidSkuModel();

                    CampaignModel rule = productSetIdRuleMap.get(productSetId);
                    PromoVo promoVo = promoConvertManager.convertPromoVo(rule);
                    validSkuItem.setPromoVo(promoVo);
                    validSkuItem.setPromoTips(promoConvertManager.fillPromoTips(rule.getSubType(), rule.getCampaignType()));
                    Collections.sort(cartSkus);

                    validSkuItem.setProductSetId(productSetId);
                    validSkuItem.setSkuModels(cartSkus);
                    validSkuItems.add(validSkuItem);
                } else {

                    for (CartSkuModel cartSku : cartSkus) {
                        List<CartSkuModel> skuModels = new ArrayList<>(1);
                        skuModels.add(cartSku);
                        ValidSkuModel singleSku = new ValidSkuModel();
                        singleSku.setProductSetId(productSetId);
                        singleSku.setSkuModels(skuModels);
                        validSkuItems.add(singleSku);
                    }
                }
            }
            cartWrap = new CartWrap();
            Collections.sort(validSkuItems, new SortValidSku());
            mergeNoPromoSku(cartWrap, validSkuItems);
            cartWrap.setInvalidSkus(invalidSkus);
            cartWrap.setLessStockSkus(lessStockSkus);
        }
        return cartWrap;
    }

    /**
     * 非活动的sku合并在为0的选品集
     *
     * @param cartWrap
     * @param validSkuItems
     */
    private void mergeNoPromoSku(CartWrap cartWrap, List<ValidSkuModel> validSkuItems) {

        if (validSkuItems.size() >= COLLECTION_SIZE_IS_2) {
            List<CartSkuModel> tempSkuModels = new ArrayList<>();
            List<ValidSkuModel> convertValidSkus = new ArrayList<>();

            Iterator<ValidSkuModel> validSkusItr = validSkuItems.iterator();
            while (validSkusItr.hasNext()) {

                ValidSkuModel skuModel = validSkusItr.next();

                if (skuModel.getPromoVo() == null && StringUtils.isBlank(skuModel.getPromoTips())) {
                    tempSkuModels.addAll(skuModel.getSkuModels());
                } else {
                    ValidSkuModel promoValidSkuModel = new ValidSkuModel();
                    BeanUtils.copyProperties(skuModel, promoValidSkuModel);

                    if (!tempSkuModels.isEmpty()) {
                        createNewNoPromoSku(tempSkuModels, convertValidSkus);
                        tempSkuModels.clear();
                    }
                    convertValidSkus.add(promoValidSkuModel);
                }
            }

            if (!tempSkuModels.isEmpty()) {
                createNewNoPromoSku(tempSkuModels, convertValidSkus);
            }
            cartWrap.setValidSkus(convertValidSkus);
        } else {
            cartWrap.setValidSkus(validSkuItems);
        }
    }



    /**
     * 创建新的无活动sku
     *
     * @param tempSkuModels
     * @param convertValidSkus
     */
    private void createNewNoPromoSku(List<CartSkuModel> tempSkuModels, List<ValidSkuModel> convertValidSkus) {
        ValidSkuModel noPromoSku = new ValidSkuModel();
        noPromoSku.setProductSetId(ProductConvertManager.NULL_PRODUCT_SET_ID);
        List<CartSkuModel> copyList = new ArrayList<>(tempSkuModels);
        noPromoSku.setSkuModels(copyList);
        convertValidSkus.add(noPromoSku);
    }

    /**
     * 同步cookie商品至购物车
     *
     * @param syncModel
     * @param syncSkuModel
     * @return
     * @throws Exception
     */
    public int syncCookieSkus(SyncCookieSkuModel syncModel, List<SyncSkuModel> syncSkuModel) throws Exception {

        if (syncSkuModel == null || syncSkuModel.isEmpty()) {
            return Constant.FLAG_FAIL;
        }

        // cookie中要同步的skuCodes
        List<String> syncSkuCodes = new ArrayList<>();
        Map<String, Integer> cookieSkuCodeCountMap = new HashMap<>();
        Iterator<SyncSkuModel> syncSkuItr = syncSkuModel.iterator();
        while (syncSkuItr.hasNext()) {
            SyncSkuModel addSku = syncSkuItr.next();
            syncSkuCodes.add(addSku.getSkuCode());
            cookieSkuCodeCountMap.put(addSku.getSkuCode(), addSku.getCount());
        }
        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(syncModel.getUid(), syncModel.getAppId(),
                syncModel.getCorpId());
        String alias = createNewCartRtnAlias(syncModel.getUid(), syncModel.getAppId(), syncModel.getCorpId());
        // 全部加入缓存，同步到表
        List<SkuModel> qrySkusData = productConvertManager.getSkuByCodes(syncModel.getAppId(), syncModel.getCorpId(),
                syncSkuCodes);
        if (qrySkusData == null || qrySkusData.isEmpty()) {
            LOGGER.warn("SYNC_COOKIE_SKUS, syncSku query null,default success,skuCode:{}", syncSkuCodes);
            return Constant.FLAG_SUCCESS;
            //throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
        }

        for (SkuModel skuModel : qrySkusData) {

            int count = cookieSkuCodeCountMap.get(skuModel.getSkuCode());

            // 要存入缓存sku
            CacheSku cacheSku = convertCacheSku(skuModel);
            cacheManager.setExpireCacheSku(cacheSku);

            // 缓存购物车sku
            CartSkuModel cartSkuModel = convertUserSkuModel(count, cacheSku);
            cacheManager.setCartSku(corpIdAppIdUid, cartSkuModel);

            AddCartItemDO addCartItem = new AddCartItemDO();
            addCartItem.setAlias(alias);
            addCartItem.setAppId(syncModel.getAppId());
            addCartItem.setCorpId(syncModel.getCorpId());
            addCartItem.setCount(count);
            addCartItem.setProductId(skuModel.getProductId());
            addCartItem.setSkuId(skuModel.getSkuId());
            addCartItem.setUserId(syncModel.getUid());
            addCartItem.setSkuCode(skuModel.getSkuCode());
            try {
                cartService.insertOrUpdate(addCartItem);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("SYNC_COOKIE_SKUS,err,addCartItem:{}", JSONObject.toJSONString(addCartItem));
                return Constant.FLAG_FAIL;
            }
        }

        return getUserSkuCount(syncModel.getUid(), syncModel.getAppId(), syncModel.getCorpId());
    }

    /**
     * 从缓存获取用户购物车sku总数，有异常读表数据
     *
     * @param uid
     * @param appId
     * @param corpId
     * @return
     */
    private int getUserSkuCount(Integer uid, Integer appId, Integer corpId) {
        int count = cacheManager.getCartSkuCount(uid, appId, corpId);
        if (Constant.REDIS_ERROR_INT == count) {
            LOGGER.error("GET_USER_SKU_COUNT err and query table,uid:{}", uid);
            CartDO cartDO = cartService.selectUserCart(uid, appId, corpId);
            if (cartDO != null) {
                count = cartService.selectCountByAlias(cartDO.getAlias());
            }
        }
        return count;
    }

    /**
     * 合并购物车数据：使缓存数据和表数据两边的数据一致（并集）
     *
     * @param mergeModel
     * @return
     */
    public int mergeCart(MergeModel mergeModel) {

        boolean ok = true;

        Map<String, MergeCountAddTimeVO> cacheSkuMergeVoMap = getCacheSkuMergeVoMap(mergeModel);
        String alias = createNewCartRtnAlias(mergeModel.getUid(), mergeModel.getAppId(), mergeModel.getCorpId());
        Map<String, MergeCountAddTimeVO> cartItemDOMap = getItemMergeVoMap(mergeModel);


        // 合并缓存、购物车实表数据 map<skuCode,count>
        Map<String, MergeCountAddTimeVO> mergeMap = new HashMap<>();
        mergeMap.putAll(cartItemDOMap);
        mergeMap.putAll(cacheSkuMergeVoMap);
        LOGGER.info("MERGE_CART merge cart size:{}", mergeMap.size());

        List<Entry<String, MergeCountAddTimeVO>> addCacheCartList = new ArrayList<>();
        List<Entry<String, MergeCountAddTimeVO>> addCartItemList = new ArrayList<>();
        Iterator<Entry<String, MergeCountAddTimeVO>> mergeItr = mergeMap.entrySet().iterator();
        while (mergeItr.hasNext()) {
            Entry<String, MergeCountAddTimeVO> entry = mergeItr.next();
            // 缓存没有的sku
            if (!cacheSkuMergeVoMap.containsKey(entry.getKey())) {
                addCacheCartList.add(entry);
            }
            // 表没有的sku
            if (!cartItemDOMap.containsKey(entry.getKey())) {
                addCartItemList.add(entry);
            }
        }

        int oprNm = 0;
        // 处理表中没有的sku
        if (!addCartItemList.isEmpty()) {
            List<String> skuCodes = new ArrayList<>();
            addCartItemList.stream().forEach(entry -> skuCodes.add(entry.getKey()));
            List<SkuModel> skuData = productConvertManager.getSkuByCodes(mergeModel.getAppId(), mergeModel.getCorpId(),
                    skuCodes);
            if (skuData != null && !skuData.isEmpty()) {
                List<CartItemDO> cartItemDOs = new ArrayList<>(skuData.size());
                for (SkuModel sku : skuData) {
                    String skuCode = sku.getSkuCode();
                    cacheManager.setExpireCacheSku(convertCacheSku(sku));
                    LOGGER.info("MERGE_CART ,reset cache sku before batchInsertSku:", skuCode);

                    if (mergeMap.containsKey(skuCode)) {
                        MergeCountAddTimeVO mergeCountAddTimeVO = mergeMap.get(skuCode);
                        CartItemDO cartItemDO = new CartItemDO();
                        cartItemDO.setAppId(mergeModel.getAppId());
                        cartItemDO.setCorpId(mergeModel.getCorpId());
                        cartItemDO.setUserId(mergeModel.getUid());
                        cartItemDO.setProductId(sku.getProductId());
                        cartItemDO.setCount(mergeCountAddTimeVO.getCount());
                        cartItemDO.setSkuId(sku.getSkuId());
                        cartItemDO.setSkuCode(skuCode);
                        cartItemDO.setAlias(alias);
                        cartItemDO.setCreatedAt(new Date(mergeCountAddTimeVO.getAddTime()));
                        cartItemDOs.add(cartItemDO);
                    }
                }
                try {
                    oprNm = cartService.batchInsertSku(alias, cartItemDOs);
                    if (oprNm < Constant.FLAG_SUCCESS) {
                        ok = false;
                    }
                } catch (Exception e) {
                    ok = false;
                    //e.printStackTrace();
                    LOGGER.error("MERGE_CART batchInsertSku Err oprNm:{} ", oprNm);
                }
                LOGGER.info("MERGE_CART merge cart item oprNm:{}", oprNm);
            }
        }

        // 处理缓存购物车没有的sku
        if (!addCacheCartList.isEmpty()) {
            List<String> skuCodes = new ArrayList<>();
            addCacheCartList.stream().forEach(entry -> skuCodes.add(entry.getKey()));
            List<SkuModel> skuData = productConvertManager.getSkuByCodes(mergeModel.getAppId(), mergeModel.getCorpId(),
                    skuCodes);
            if (skuData != null && !skuData.isEmpty()) {
                String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(mergeModel.getUid(), mergeModel.getAppId(),
                        mergeModel.getCorpId());
                for (SkuModel sku : skuData) {
                    MergeCountAddTimeVO mergeCountAddTimeVO = mergeMap.get(sku.getSkuCode());
                    CartSkuModel cartSku = convertCartSku(sku, mergeCountAddTimeVO.getCount(), mergeCountAddTimeVO.getAddTime());
                    oprNm = cacheManager.setCartSku(corpIdAppIdUid, cartSku);
                    LOGGER.info("MERGE_CART ,set user cart sku oprNm:{}", oprNm);
                    if (oprNm <= 0 && ok) {
                        ok = false;
                    }
                    cacheManager.setExpireCacheSku(convertCacheSku(sku));
                    LOGGER.info("MERGE_CART ,reset cache sku after setCartSku:", sku.getSkuCode());
                }
            }
        }
        return ok ? Constant.FLAG_SUCCESS : Constant.FLAG_FAIL;
    }

    private Map<String, MergeCountAddTimeVO> getItemMergeVoMap(MergeModel mergeModel) {
        // 获取表购物车明细
        Map<String, MergeCountAddTimeVO> cartItemDOMap = new HashMap<>();
        List<CartItemDO> cartItemDOList = getDetail(mergeModel.getUid(), mergeModel.getAppId(), mergeModel.getCorpId());
        if (cartItemDOList != null && !cartItemDOList.isEmpty()) {
            cartItemDOList.stream().forEach(itemDO -> cartItemDOMap.put(itemDO.getSkuCode(), new MergeCountAddTimeVO(itemDO.getCount(), itemDO.getCreatedAt().getTime())));
        }
        LOGGER.info("getItemMergeVoMap cart item size:{}", cartItemDOMap.size());
        return cartItemDOMap;
    }

    private Map<String, MergeCountAddTimeVO> getCacheSkuMergeVoMap(MergeModel mergeModel) {
        Map<String, MergeCountAddTimeVO> cacheSkuMergeVoMap = new HashMap<>();
        List<CartSkuModel> cartSkus = getCartSkus(mergeModel.getUid(), mergeModel.getAppId(),
                mergeModel.getCorpId());
        if (cartSkus != null && !cartSkus.isEmpty()) {
            cartSkus.stream().forEach(cartSku -> cacheSkuMergeVoMap.put(cartSku.getSkuCode(), new MergeCountAddTimeVO(cartSku.getCount(), cartSku.getAddTime())));
        }
        LOGGER.info("MERGE_CART cache cart size:{}", cacheSkuMergeVoMap.size());
        return cacheSkuMergeVoMap;
    }

    /**
     * 获取购物车明细sku
     *
     * @param uid
     * @param appId
     * @param corpId
     * @return
     */
    private List<CartItemDO> getDetail(Integer uid, Integer appId, Integer corpId) {
        CartDO cartDO = cartService.selectUserCart(uid, appId, corpId);
        if (cartDO != null) {
            return cartService.selectByAlias(cartDO.getAlias());
        }
        return null;
    }

    /**
     * SkuModel->CartSkuModel
     *
     * @param sku
     * @param count
     * @return
     */
//    @Deprecated
//    private CartSkuModel convertCartSku(SkuModel sku, int count) {
//        CartSkuModel cartSkuModel = new CartSkuModel();
//        cartSkuModel.setStore(sku.getStore());
//        cartSkuModel.setSkuId(sku.getSkuId());
//        cartSkuModel.setPrice(sku.getPrice());
//        cartSkuModel.setProductId(sku.getProductId());
//        cartSkuModel.setSpecVal(sku.getSpecVal());
//        cartSkuModel.setStatus(convertSkuStatus(sku, count));
//        cartSkuModel.setName(sku.getName());
//        cartSkuModel.setImgUrl(sku.getImgUrl());
//        cartSkuModel.setAddTime(getCurTime());
//        cartSkuModel.setCount(count);
//        cartSkuModel.setSkuCode(sku.getSkuCode());
//        return cartSkuModel;
//    }


    /**
     * SkuModel->CartSkuModel
     *
     * @param sku
     * @param count
     * @param addTime
     * @return
     */
    private CartSkuModel convertCartSku(SkuModel sku, int count, long addTime) {
        CartSkuModel cartSkuModel = new CartSkuModel();
        cartSkuModel.setStore(sku.getStore());
        cartSkuModel.setSkuId(sku.getSkuId());
        cartSkuModel.setPrice(sku.getPrice());
        cartSkuModel.setProductId(sku.getProductId());
        cartSkuModel.setSpecVal(sku.getSpecVal());
        cartSkuModel.setStatus(convertSkuStatus(sku, count));
        cartSkuModel.setName(sku.getName());
        cartSkuModel.setImgUrl(sku.getImgUrl());
        if (addTime == 0) {
            addTime = getCurTime();
        }
        cartSkuModel.setAddTime(addTime);
        cartSkuModel.setCount(count);
        cartSkuModel.setSkuCode(sku.getSkuCode());
        cartSkuModel.setRefreshTime(getCurTime());
        return cartSkuModel;
    }

    /**
     * SkuModel->CartSkuModel
     *
     * @param sku
     * @param count
     * @param addTime
     * @return
     */
    private CartSkuModel convertCartSku(SkuModel sku, int count, Integer seller, long addTime) {
        CartSkuModel cartSkuModel = new CartSkuModel();
        cartSkuModel.setStore(sku.getStore());
        cartSkuModel.setSkuId(sku.getSkuId());
        cartSkuModel.setPrice(sku.getPrice());
        cartSkuModel.setProductId(sku.getProductId());
        cartSkuModel.setSpecVal(sku.getSpecVal());
        cartSkuModel.setStatus(convertSkuStatus(sku, count));
        cartSkuModel.setName(sku.getName());
        cartSkuModel.setImgUrl(sku.getImgUrl());
        if (addTime == 0) {
            addTime = getCurTime();
        }
        cartSkuModel.setAddTime(addTime);
        cartSkuModel.setCount(count);
        cartSkuModel.setSeller(seller);
        cartSkuModel.setSkuCode(sku.getSkuCode());
        cartSkuModel.setRefreshTime(getCurTime());
        return cartSkuModel;
    }

    public static void main(String[] args) {
        SyncSkuModel s1 = new SyncSkuModel();
        s1.setCount(123);
        System.out.println(JSONObject.toJSONString(s1));

        CartSkuModel CartSkuModel = new CartSkuModel();
        CartSkuModel.setAddTime(123123123);
        CartSkuModel.setCount(123);
        CartSkuModel.setName("jQNrqfcwrF");
        CartSkuModel.setPrice(123);
        CartSkuModel.setSkuId(123123);
        CartSkuModel.setProductId(456456);
        CartSkuModel.setSpecVal("PKWJX6");
        CartSkuModel.setStatus(1);
        CartSkuModel.setStore(299);
        CartSkuModel.setSkuCode("XXX");
        List<Integer> list = new ArrayList<Integer>();
        list.add(123);
        list.add(123);
        list.add(123);
        list.add(123);
        list.add(123);
        System.out.println(ResultUtil.handleSuccessReturn(JSONObject.toJSONString(CartSkuModel)));
        String str = "{\"addTime\":1542782405881,\"count\":1,\"name\":\"男装外套\",\"price\":23900,\"productId\":18,\"skuId\":997,\"specVal\":\"深蓝\",\"status\":0,\"store\":0}";
        System.out.println(JSONObject.parseObject(str, CartSkuModel.getClass()));

        List<Integer> skuIdList = new ArrayList<>();
        skuIdList.add(1460);
        List<String> skuIdStrList = new ArrayList<>();
        for (Integer skuId : skuIdList) {
            skuIdStrList.add(String.valueOf(skuId));
        }
        String[] skuIds = new String[skuIdStrList.size()];
        skuIds = skuIdStrList.toArray(skuIds);
        System.out.println(skuIds);
        SyncSkuModel oldModel = new SyncSkuModel();
        oldModel.setCount(1);
        SyncSkuModel newModel = new SyncSkuModel();
        newModel.setCount(1);
        HashMap map = new HashMap();
        map.put("oldModel", oldModel);
        map.put("newModel", newModel);
        System.out.println(JSONObject.parseObject(JSONObject.toJSON(map).toString(), Map.class));

        String jsonStr = "{\n" + "\t\"newModel\": {\n" + "\t\t\"count\": 1,\n" + "\t\t\"skuId\": 996\n" + "\t},\n"
                + "\t\"oldModel\": {\n" + "\t\t\"count\": 1,\n" + "\t\t\"skuId\": 996\n" + "\t}\n" + "}";
        Map<String, SyncSkuModel> map1 = JSONObject.parseObject(jsonStr, Map.class);
        System.out.println(map.get("oldModel"));
        SyncSkuModel oldModel1 = JSONObject.parseObject(map.get("oldModel").toString(), SyncSkuModel.class);
        System.out.println(oldModel1);
//        SyncSkuModel oldModel = (SyncSkuModel) map.get("oldModel");
//        SyncSkuModel newModel = (SyncSkuModel) map.get("newModel");
    }

    /**
     * 用户购物车数量
     *
     * @param countModel
     * @return
     */
    public int count(CountModel countModel) {

        return getUserSkuCount(countModel.getUid(), countModel.getAppId(), countModel.getCorpId());
    }

    private void testBatchUpdate() {
        cartService.batchUpdateSku("3f96b234500f4f8b829ca92ac9f3fb73", new HashMap<>());
    }

    /**
     * 刷新缓存购物车sku
     *
     * @param refreshModel
     * @return
     */
    public int refreshCart(RefreshModel refreshModel) {

        Map<String, String> cacheCartSkuMap = cacheManager.getCartSkus(refreshModel.getUid(),
                refreshModel.getAppId(), refreshModel.getCorpId());
        if (cacheCartSkuMap == null || cacheCartSkuMap.isEmpty()) {
            LOGGER.info("REFRESH_CART no exists cache cart sku,over");
            return 0;
        } else if (cacheCartSkuMap.containsKey(Constant.REDIS_ERROR_STRING)) {
            LOGGER.error("REFRESH_CART cache unavalid status,over");
            return -1;
        } else {

            List<String> cartStrList = new ArrayList<>();
            cacheCartSkuMap.entrySet().stream().forEach(entry -> cartStrList.add(entry.getValue()));

            List<CartSkuModel> cacheCartSkuList = JsonUtil.jsonToList(cartStrList.toString(), CartSkuModel.class);
            List<String> cartSkuCodeList = cacheCartSkuList.stream().map(CartSkuModel::getSkuCode)
                    .collect(Collectors.toList());
            // 查询最新的sku数据刷新
            Map<String, SkuModel> skuModelMap = new HashMap<>();
            if (cartSkuCodeList != null && !cartSkuCodeList.isEmpty()) {
                List<SkuModel> skus = productConvertManager.getSkuByCodes(refreshModel.getAppId(),
                        refreshModel.getCorpId(), cartSkuCodeList);
                if (skus == null || skus.isEmpty()) {
                    LOGGER.warn("refreshCart err msg:{}", StatusCode.SKU_NO_FOUND.getMsg());
                    //throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
                    return 0;
                }
                Iterator<SkuModel> itr = skus.iterator();
                while (itr.hasNext()) {
                    SkuModel skuModel = itr.next();
                    skuModelMap.put(skuModel.getSkuCode(), skuModel);
                    CacheSku cacheSku = convertCacheSku(skuModel);
                    cacheManager.setExpireCacheSku(cacheSku);

                    LOGGER.debug("REFRESH_CART,update cache skuCode:{}", skuModel.getSkuCode());
                }
            }

            String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(refreshModel.getUid(), refreshModel.getAppId(),
                    refreshModel.getCorpId());
            Iterator<CartSkuModel> itr = cacheCartSkuList.iterator();
            while (itr.hasNext()) {
                CartSkuModel cartSkuModel = itr.next();
                String skuCode = cartSkuModel.getSkuCode();
                if (skuModelMap.containsKey(skuCode)) {
                    CartSkuModel cartSku = convertCartSku(skuModelMap.get(skuCode), cartSkuModel.getCount(), cartSkuModel.getAddTime());
                    cacheManager.setCartSku(corpIdAppIdUid, cartSku);
                    LOGGER.debug("REFRESH_CART,setCartSku skuCode:{}", cartSku.getSkuCode());
                }
            }
        }
        return 1;
    }

    /**
     * 刷新、重置缓存sku
     *
     * @param refreshCacheSkuModel
     * @return
     */
    public int refreshCacheSku(RefreshCacheSkuModel refreshCacheSkuModel) {
        List<String> skuCodes = refreshCacheSkuModel.getSkuCodes();
        LOGGER.info("REFRESH_CACHE_SKU,skus size:{},skuCodes:{}", skuCodes.size(), skuCodes.toString());
        List<SkuModel> skuModels = productConvertManager.getSkuByCodes(refreshCacheSkuModel.getAppId(), refreshCacheSkuModel.getCorpId(), skuCodes);
        if (skuModels == null || skuModels.isEmpty()) {
            LOGGER.info("REFRESH_CACHE_SKU ,get skus null.skuCodes:{}", skuCodes.toString());
            return Constant.FLAG_FAIL;
        }

        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(refreshCacheSkuModel.getUid(), refreshCacheSkuModel.getAppId(), refreshCacheSkuModel.getCorpId());
        for (SkuModel sku : skuModels) {
            cacheManager.setExpireCacheSku(convertCacheSku(sku));
            LOGGER.info("REFRESH_CACHE_SKU,setExpireCacheSku:{}", sku.getSkuCode());

            CartSkuModel userSku = cacheManager.getCartSku(corpIdAppIdUid, sku.getSkuCode());
            if (userSku != null) {
                CartSkuModel addSku = convertCartSku(sku, userSku.getCount(),userSku.getAddTime());
                cacheManager.setCartSku(corpIdAppIdUid, addSku);
                LOGGER.info("REFRESH_CACHE_SKU,setCartSku:{}", sku.getSkuCode());
            }
        }

        return Constant.FLAG_SUCCESS;
    }

    public void testInsertOrUpdate() {
        AddCartItemDO addCartItemDO = new AddCartItemDO();
        addCartItemDO.setAlias("XXX");
        addCartItemDO.setAppId(2);
        addCartItemDO.setCorpId(2);
        addCartItemDO.setCount(new Random().nextInt(10));
        addCartItemDO.setSkuCode(RandomStringUtils.randomAlphanumeric(5));
        addCartItemDO.setProductId(1213);
        addCartItemDO.setSkuId(223);
        addCartItemDO.setUserId(223);
        int nm = cartService.insertOrUpdate(addCartItemDO);
        LOGGER.info("nm:{}", nm);
    }

    /**
     * 分页同步购物车
     *
     * @param curPage
     * @param pageSize
     * @return
     */
    public int syncByPage(int curPage, int pageSize) {
        int count = 0;
        Map<String, Integer> data = new HashMap<>();
        data.put("curIndex", (curPage - 1) * pageSize);
        data.put("pageSize", pageSize);
        List<CartDO> cartDos = cartService.syncByPage(data);
        if (cartDos == null || cartDos.isEmpty()) {
            LOGGER.info("SYNC_BY_PAGE, syncByPage null");
            return count;
        }
        String corpIdAppIdUid = null;
        int rtn = 0;
        for (CartDO cartDO : cartDos) {
            corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(cartDO.getUserId(), cartDO.getAppId(), cartDO.getCorpId());
            LOGGER.info("SYNC_BY_PAGE,start merge user cart ,corpIdAppIdUid:{}", corpIdAppIdUid);
            MergeModel merge = new MergeModel(cartDO.getUserId(), cartDO.getAppId(), cartDO.getCorpId());
            try {
                rtn = mergeCart(merge);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("SYNC_BY_PAGE, corpIdAppIdUid:{}, err:{}", corpIdAppIdUid, e.getMessage());
            }
            if (rtn == Constant.FLAG_SUCCESS) {
                ++count;
            } else {
                cacheManager.setMergeCartErr(corpIdAppIdUid, DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                LOGGER.error("SYNC_BY_PAGE,set cache with err user cart,corpIdAppIdUid:{}", corpIdAppIdUid);
            }
        }
        LOGGER.info("SYNC_BY_PAGE,end merge user cart ,total:{},doCount:{}", cartDos.size(), count);
        return count;
    }

    public CartWrap loadCartV2(LoadModel loadModel) {
        return loadCartV2(loadModel, 2);
    }
    //------------------------- 2019-04-22 --------------------------------//

    public CartWrap loadCartV2(LoadModel loadModel, int version) {
        CartWrap cartWrap = null;

        String corpIdAppIdUid = cacheManager.getCorpIdAppIdUId(loadModel.getUid(), loadModel.getAppId(),
                loadModel.getCorpId());

        // 获取用户缓存购物车sku
        List<CartSkuModel> cartSkus = getCartSkus(loadModel.getUid(), loadModel.getAppId(),
                loadModel.getCorpId());
        if (cartSkus == null || cartSkus.isEmpty()) {
            return cartWrap;
        }

        // 同步sku
        List<String> syncQrySkuCodes = null;
        // 异步sku
        List<String> asyncQrySkuCodes = null;

        Map<String, CartSkuModel> cartSkusMap = new HashMap<>();

        Iterator<CartSkuModel> cartSkusItr = cartSkus.iterator();
        while (cartSkusItr.hasNext()) {
            CartSkuModel cartSku = cartSkusItr.next();
            cartSkusMap.put(cartSku.getSkuCode(), cartSku);
            // 判断缓存分代有效期
            long refreshTime = cartSku.getRefreshTime();
            boolean flag = cacheGenerateUtil.isNewGeneration(refreshTime);
            if (flag) {
                // 新生代，不处理直接返回
                LOGGER.debug("QUERY_CART ,cache_at_new corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, cartSku.getSkuCode());
            } else if (cacheGenerateUtil.isYoungGeneration(refreshTime)) {
                // 年轻代，直接返回，但需要异步去更新缓存数据
                if (asyncQrySkuCodes == null) {
                    asyncQrySkuCodes = new ArrayList<>();
                }
                asyncQrySkuCodes.add(cartSku.getSkuCode());
                LOGGER.debug("QUERY_CART ,cache_at_young corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, cartSku.getSkuCode());
            } else {
                // 年老代，抛弃缓存数据，取实时sku
                if (syncQrySkuCodes == null) {
                    syncQrySkuCodes = new ArrayList<>();
                }
                syncQrySkuCodes.add(cartSku.getSkuCode());
                // 删掉要同步的sku，剩下的sku是可直接使用的
                cartSkusItr.remove();
                LOGGER.debug("QUERY_CART ,cache_at_old corpIdAppIdUid:{},skuCode:{}", corpIdAppIdUid, cartSku.getSkuCode());
            }
        }
        syncRefreshCartAndSku(loadModel, corpIdAppIdUid, cartSkus, syncQrySkuCodes, cartSkusMap);
        asyncRefreshCartAndSku(loadModel, asyncQrySkuCodes, cartSkusMap);
        // 将购物车数据封装返回前端
        if(version == 2) {
            cartWrap = convertCartWrapV2(loadModel, cartSkus);
        } else {
            cartWrap = convertCartWrapV3(loadModel, cartSkus);
        }

        return cartWrap;
    }

    /**
     * 加载购物车v2
     *
     * @param loadModel
     * @param cartSkuModels
     * @return
     */
    @Deprecated
    private CartWrap convertCartWrapV2(LoadModel loadModel, List<CartSkuModel> cartSkuModels) {
        CartWrap cartWrap = null;
        if (cartSkuModels != null && !cartSkuModels.isEmpty()) {

            // 无效商品
            List<CartSkuModel> invalidSkus = new ArrayList<>();
            // 库存不足商品
            List<CartSkuModel> lessStockSkus = new ArrayList<>();
            //有效商品
            List<CartSkuModel> validSkus = new ArrayList<>();

            Map<String,CartSkuModel> validSkusMap = new HashMap<>();
            // 根据状态分组
            for (CartSkuModel userSku : cartSkuModels) {
                int status = userSku.getStatus();
                LOGGER.info("skuCode:{},status:{}", userSku.getSkuCode(), status);
                if (isValidStats(status)) {
                    validSkus.add(userSku);
                    validSkusMap.put(userSku.getSkuCode(),userSku);
                } else if (isInvalidStatus(status)) {
                    invalidSkus.add(userSku);
                } else {
                    lessStockSkus.add(userSku);
                }
            }

            //有效商品进行分组后集合
            List<ValidSkuModel> validSkuItems = new ArrayList<>();
            if (!validSkus.isEmpty()){
                // 处理有效状态的商品分组及优惠价格
                Map<String,Integer> promoPriceMap = new HashMap<>();
                Map<Integer,List<String>> promoSetIdSkuCodesMap = new HashMap<>();
                Map<Integer,PromoVo> promoVoMap = new HashMap<>();

                if (!validSkus.isEmpty()) {
                    List<SkuCodeReq> skuCodeReqs = new ArrayList<>(validSkus.size());
                    validSkus.stream().forEach(validSku -> skuCodeReqs.add(new SkuCodeReq(validSku.getSkuCode(), validSku.getProductId(), validSku.getCount(), validSku.getPrice())));
                    List<CampaignInfoResult> campaignInfoRst = promoConvertManager.getCampaignListBySkuCodes(loadModel.getCorpId(), loadModel.getAppId(), loadModel.getUid(), skuCodeReqs);
                    if (!campaignInfoRst.isEmpty()) {
                        for (CampaignInfoResult campaignInfo : campaignInfoRst) {
                            int campaignType = campaignInfo.getCampaignType();
                            if (campaignType == PromoConvertManager.PROMO_TYPE_DISC) {
                                Map<String, Integer> prompSkuCodePriceMap = campaignInfo.getPromotionSkuCodes();
                                convertDiscountPrice(validSkusMap,prompSkuCodePriceMap);
                                promoPriceMap.putAll(prompSkuCodePriceMap);
                            } else { //默认满减活动
                                int setId = promoConvertManager.convertPromo(promoVoMap, campaignInfo);
                                if (setId == ProductConvertManager.NULL_PRODUCT_SET_ID){
                                    LOGGER.error("getCampaignListBySkuCodes setId=0,campaignInfo:{}",JSONObject.toJSONString(campaignInfo));
                                    continue;
                                }
                                List<String> skuCodeList = campaignInfo.getSkuCodeList();
                                if (skuCodeList != null && !skuCodeList.isEmpty()) {
                                    promoSetIdSkuCodesMap.put(setId, skuCodeList);
                                }
                            }
                        }
                    }
                }

                productConvertManager.resetCartSkuPrice(validSkusMap, promoPriceMap);

                Map<Integer, List<CartSkuModel>> setIdCartSkusMap = new HashMap<>();
                if (promoSetIdSkuCodesMap.isEmpty()){
                    setIdCartSkusMap.put(ProductConvertManager.NULL_PRODUCT_SET_ID,validSkus);
                }else {
                    Iterator<Entry<Integer, List<String>>> itr = promoSetIdSkuCodesMap.entrySet().iterator();
                    while (itr.hasNext()) {
                        List<CartSkuModel> skuModels = new ArrayList<>();
                        Entry<Integer, List<String>> setIdSkuCodesEntity = itr.next();
                        int setId = setIdSkuCodesEntity.getKey();
                        List<String> skuCodes = setIdSkuCodesEntity.getValue();
                        for (String skuCode : skuCodes) {
                            CartSkuModel cartSku = validSkusMap.get(skuCode);
                            if (cartSku == null) {
                                LOGGER.warn("cartSku null,skuCode:{}", skuCode);
                                continue;
                            }
                            skuModels.add(cartSku);
                            validSkusMap.remove(skuCode);
                        }
                        setIdCartSkusMap.put(setId, skuModels);
                    }
                    if (!validSkusMap.isEmpty()) {
                        Iterator<Entry<String, CartSkuModel>> validSkusItr = validSkusMap.entrySet().iterator();
                        List<CartSkuModel> skuModels = new ArrayList<>();
                        while (validSkusItr.hasNext()) {
                            Entry<String, CartSkuModel> vailSkuEntry = validSkusItr.next();
                            CartSkuModel cartSku = vailSkuEntry.getValue();
                            skuModels.add(cartSku);
                        }
                        setIdCartSkusMap.put(ProductConvertManager.NULL_PRODUCT_SET_ID, skuModels);
                    }
                }

                //商品集遍历分组
                Iterator<Entry<Integer, List<CartSkuModel>>> itr = setIdCartSkusMap.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<Integer, List<CartSkuModel>> entry = itr.next();
                    List<CartSkuModel> vals = entry.getValue();
                    Integer productSetId = entry.getKey();
                    LOGGER.info("convertCartWrapV2 productSetId:{}", productSetId);

                    if (!promoVoMap.isEmpty() && promoVoMap.containsKey(productSetId)){

                        ValidSkuModel validSkuItem = new ValidSkuModel();
                        PromoVo promoVo = promoVoMap.get(productSetId);
                        validSkuItem.setPromoVo(promoVo);
                        //已经挪至promoVo
                        //validSkuItem.setPromoTips(promoConvertManager.fillPromoTips(promoVo.getSubType(), 1));
                        Collections.sort(vals);

                        validSkuItem.setProductSetId(productSetId);
                        validSkuItem.setSkuModels(vals);
                        validSkuItems.add(validSkuItem);
                    } else {

                        for (CartSkuModel cartSku : vals) {
                            List<CartSkuModel> singleCartSku = new ArrayList<>(1);
                            singleCartSku.add(cartSku);
                            ValidSkuModel validSku = new ValidSkuModel();
                            validSku.setProductSetId(productSetId);
                            validSku.setSkuModels(singleCartSku);
                            validSkuItems.add(validSku);
                        }

                    }
                }
            }
            cartWrap = new CartWrap();
            Collections.sort(validSkuItems, new SortValidSku());
            mergeNoPromoSku(cartWrap, validSkuItems);
            cartWrap.setInvalidSkus(invalidSkus);
            cartWrap.setLessStockSkus(lessStockSkus);
        }
        return cartWrap;
    }

    /**
     * 加载购物车v2
     *
     * @param loadModel
     * @param cartSkuModels
     * @return
     */
    private CartWrap convertCartWrapV3(LoadModel loadModel, List<CartSkuModel> cartSkuModels) {
        CartWrap cartWrap = null;
        if (cartSkuModels != null && !cartSkuModels.isEmpty()) {

            // 无效商品
            List<CartSkuModel> invalidSkus = new ArrayList<>();
            // 库存不足商品
            List<CartSkuModel> lessStockSkus = new ArrayList<>();
            //有效商品
            List<CartSkuModel> validSkus = new ArrayList<>();

            Map<String,CartSkuModel> validSkusMap = new HashMap<>();
            // 根据状态分组
            for (CartSkuModel userSku : cartSkuModels) {
                int status = userSku.getStatus();
                LOGGER.info("skuCode:{},status:{}", userSku.getSkuCode(), status);
                if (isValidStats(status)) {
                    validSkus.add(userSku);
                    validSkusMap.put(userSku.getSkuCode(),userSku);
                } else if (isInvalidStatus(status)) {
                    invalidSkus.add(userSku);
                } else {
                    lessStockSkus.add(userSku);
                }
            }

            //有效商品进行分组后集合
            List<ValidSkuModel> validSkuItems = new ArrayList<>();
            if (!validSkus.isEmpty()){
                // 处理有效状态的商品分组及优惠价格
                Map<String,Integer> promoPriceMap = new HashMap<>();
                Map<Integer,List<String>> promoSetIdSkuCodesMap = new HashMap<>();
                Map<Integer,PromoVo> promoVoMap = new HashMap<>();

                if (!validSkus.isEmpty()) {
                    List<SkuCodeReq> skuCodeReqs = new ArrayList<>(validSkus.size());
                    validSkus.stream().forEach(validSku -> skuCodeReqs.add(new SkuCodeReq(validSku.getSkuCode(), validSku.getProductId(), validSku.getCount(), validSku.getPrice())));
                    List<CampaignInfoResult> campaignInfoRst = promoConvertManager.getCampaignListBySkuCodes(loadModel.getCorpId(), loadModel.getAppId(), loadModel.getUid(), skuCodeReqs);
                    if (!campaignInfoRst.isEmpty()) {
                        for (CampaignInfoResult campaignInfo : campaignInfoRst) {
                            if(PromoConvertManager.PROMO_FULL_CODE.equals(campaignInfo.getTypeCode())) {
                                int id = promoConvertManager.convertPromoV3(promoVoMap, campaignInfo);
                                if (id == ProductConvertManager.NULL_CAMPAIGN_ID){
                                    LOGGER.error("getCampaignListBySkuCodes setId=0,campaignInfo:{}",JSONObject.toJSONString(campaignInfo));
                                    continue;
                                }
                                List<String> skuCodeList = campaignInfo.getSkuCodeList();
                                if (skuCodeList != null && !skuCodeList.isEmpty()) {
                                    promoSetIdSkuCodesMap.put(id, skuCodeList);
                                }
                            } else {
                                Map<String, Integer> prompSkuCodePriceMap = campaignInfo.getPromotionSkuCodes();
                                convertDiscountPrice(validSkusMap,prompSkuCodePriceMap);
                                promoPriceMap.putAll(prompSkuCodePriceMap);
                            }
                        }
                    }
                }

                productConvertManager.resetCartSkuPrice(validSkusMap, promoPriceMap);

                Map<Integer, List<CartSkuModel>> setIdCartSkusMap = new HashMap<>();
                if (promoSetIdSkuCodesMap.isEmpty()){
                    setIdCartSkusMap.put(ProductConvertManager.NULL_CAMPAIGN_ID,validSkus);
                }else {
                    Iterator<Entry<Integer, List<String>>> itr = promoSetIdSkuCodesMap.entrySet().iterator();
                    while (itr.hasNext()) {
                        List<CartSkuModel> skuModels = new ArrayList<>();
                        Entry<Integer, List<String>> setIdSkuCodesEntity = itr.next();
                        int id = setIdSkuCodesEntity.getKey();
                        List<String> skuCodes = setIdSkuCodesEntity.getValue();
                        for (String skuCode : skuCodes) {
                            CartSkuModel cartSku = validSkusMap.get(skuCode);
                            if (cartSku == null) {
                                LOGGER.warn("cartSku null,skuCode:{}", skuCode);
                                continue;
                            }
                            skuModels.add(cartSku);
                            validSkusMap.remove(skuCode);
                        }
                        setIdCartSkusMap.put(id, skuModels);
                    }
                    if (!validSkusMap.isEmpty()) {
                        Iterator<Entry<String, CartSkuModel>> validSkusItr = validSkusMap.entrySet().iterator();
                        List<CartSkuModel> skuModels = new ArrayList<>();
                        while (validSkusItr.hasNext()) {
                            Entry<String, CartSkuModel> vailSkuEntry = validSkusItr.next();
                            CartSkuModel cartSku = vailSkuEntry.getValue();
                            skuModels.add(cartSku);
                        }
                        setIdCartSkusMap.put(ProductConvertManager.NULL_CAMPAIGN_ID, skuModels);
                    }
                }

                //商品集遍历分组
                Iterator<Entry<Integer, List<CartSkuModel>>> itr = setIdCartSkusMap.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<Integer, List<CartSkuModel>> entry = itr.next();
                    List<CartSkuModel> vals = entry.getValue();
                    Integer id = entry.getKey();
                    LOGGER.info("convertCartWrapV2 id:{}", id);

                    if (!promoVoMap.isEmpty() && promoVoMap.containsKey(id)){

                        ValidSkuModel validSkuItem = new ValidSkuModel();
                        PromoVo promoVo = promoVoMap.get(id);
                        validSkuItem.setPromoVo(promoVo);
                        //已经挪至promoVo
                        //validSkuItem.setPromoTips(promoConvertManager.fillPromoTips(promoVo.getSubType(), 1));
                        Collections.sort(vals);
                        validSkuItem.setId(id);
//                        validSkuItem.setProductSetId(productSetId);
                        validSkuItem.setSkuModels(vals);
                        validSkuItems.add(validSkuItem);
                    } else {

                        for (CartSkuModel cartSku : vals) {
                            List<CartSkuModel> singleCartSku = new ArrayList<>(1);
                            singleCartSku.add(cartSku);
                            ValidSkuModel validSku = new ValidSkuModel();
                            validSku.setId(id);
//                            validSku.setProductSetId(productSetId);
                            validSku.setSkuModels(singleCartSku);
                            validSkuItems.add(validSku);
                        }

                    }
                }
            }
            cartWrap = new CartWrap();
            Collections.sort(validSkuItems, new SortValidSku());
            mergeNoPromoSku(cartWrap, validSkuItems);
            cartWrap.setInvalidSkus(invalidSkus);
            cartWrap.setLessStockSkus(lessStockSkus);
        }
        return cartWrap;
    }

    private void convertDiscountPrice(Map<String, CartSkuModel> validSkusMap, Map<String, Integer> prompSkuCodePriceMap) {
        Iterator<Map.Entry<String, Integer>> itr = prompSkuCodePriceMap.entrySet().iterator();
        while (itr.hasNext()){
            Map.Entry<String, Integer> map = itr.next();
            if (validSkusMap.containsKey(map.getKey())){
                Integer price = validSkusMap.get(map.getKey()).getPrice();
                if (price >= map.getValue()){
                    map.setValue(price - map.getValue());
                }
            }
        }
    }

    /**
     * 异步去刷新用户购物车和缓存sku数据
     *
     * @param loadModel
     * @param asyncQrySkuCodes
     * @param cartSkusMap
     */
    private void asyncRefreshCartAndSku(LoadModel loadModel, List<String> asyncQrySkuCodes, Map<String, CartSkuModel> cartSkusMap) {
        // 异步更新用户购物车sku
        if (asyncQrySkuCodes != null && !asyncQrySkuCodes.isEmpty()) {
            LOGGER.debug("QUERY_CART async update sku cache,asyncQrySkuCodes:{}", asyncQrySkuCodes);

            List<SkuModel> realTimeSkus = productConvertManager.getSkuByCodes(loadModel.getAppId(),
                    loadModel.getCorpId(), asyncQrySkuCodes);
            if (realTimeSkus != null && !realTimeSkus.isEmpty()) {

                CartSkuModel temCartSku = null;
                for (SkuModel realTimeSku : realTimeSkus) {
                    temCartSku = cartSkusMap.get(realTimeSku.getSkuCode());
                    CartSkuModel cartSkuModel = convertCartSku(realTimeSku, temCartSku.getCount(), temCartSku.getSeller(), temCartSku.getAddTime());
                    AsyncAddCartSku addCartSku = new AsyncAddCartSku(loadModel.getUid(), loadModel.getAppId(), loadModel.getCorpId(), cartSkuModel);
                    asyncScheduled.asyncTask(addCartSku, Constant.ASYNC_TASK_TYPE_CART_SKU_ADD);

                    CacheSku cacheSku = convertCacheSku(realTimeSku);
                    cacheManager.setExpireCacheSku(cacheSku);
                    LOGGER.debug("QUERY_CART ,async get sku and reset back CacheSku:{}", realTimeSku.getSkuCode());
                }
            }
        }
    }

    /**
     * 同步去刷新用户购物车和缓存sku数据
     *
     * @param loadModel
     * @param corpIdAppIdUid
     * @param cartSkus
     * @param syncQrySkuCodes
     * @param cartSkusMap
     */
    private void syncRefreshCartAndSku(LoadModel loadModel, String corpIdAppIdUid, List<CartSkuModel> cartSkus, List<String> syncQrySkuCodes, Map<String, CartSkuModel> cartSkusMap) {
        // 同步更新购物车商品
        if (syncQrySkuCodes != null && !syncQrySkuCodes.isEmpty()) {

            LOGGER.info("QUERY_CART sync get sku data from tab,syncQrySkuCodes:{}", syncQrySkuCodes);
            List<SkuModel> realTimeSkus = productConvertManager.getSkuByCodes(loadModel.getAppId(),
                    loadModel.getCorpId(), syncQrySkuCodes);
            if (realTimeSkus != null && !realTimeSkus.isEmpty()) {

                CartSkuModel oldCartSku = null;
                for (SkuModel realTimeSku : realTimeSkus) {
                    oldCartSku = cartSkusMap.get(realTimeSku.getSkuCode());
                    CartSkuModel cartSkuModel = convertCartSku(realTimeSku, oldCartSku.getCount(), oldCartSku.getSeller(), oldCartSku.getAddTime());
                    cacheManager.setCartSku(corpIdAppIdUid, cartSkuModel);
                    cartSkus.add(cartSkuModel);
                    LOGGER.debug("QUERY_CART ,sync get sku and reset back UserCart:{}", realTimeSku.getSkuCode());

                    CacheSku cacheSku = convertCacheSku(realTimeSku);
                    cacheManager.setExpireCacheSku(cacheSku);
                    LOGGER.debug("QUERY_CART ,sync get sku and reset back CacheSku:{}", realTimeSku.getSkuCode());
                }
            }
        }
    }

	/**
     * 删除冗余skus
     *
     * @param doSize
     * @param uids
     * @return
     */
    public Result<String> delCartRedunSkus(String doSize, String uids) {
        LOGGER.warn("delCartRedunSkus start,uids:{}", uids);
        String rst = "";
        boolean isAll = StringUtils.isBlank(uids) ? true : false;
        boolean delDbSizeSku = Constant.DEL_REDU_SKU_4_DB.equals(doSize);
        if (isAll) {
            int minUid = 1;
            int maxUid = cartService.getMaxUid4GlobalTab();
            Assert.isTrue(maxUid > 0, StatusCode.GET_MAX_UID_ERR.getMsg().replace("{0}", "" + maxUid));
            rst = joinForkUtil.delSkusByRange(minUid, maxUid, delDbSizeSku, this);
        } else if (isRangeUids(uids)) {
            String[] uidsArr = uids.split("~");
            int minUid = Integer.valueOf(uidsArr[0]);
            int maxUid = Integer.valueOf(uidsArr[1]);
            Assert.isTrue(uidsArr.length == 2 && minUid <= maxUid && maxUid > 0, StatusCode.PARAMS_ERR.getMsg() + "|" + uids);
            rst = joinForkUtil.delSkusByRange(minUid, maxUid, delDbSizeSku, this);
        } else {
            String[] uidsArr = uids.split(",");
            rst = joinForkUtil.delSkusByUids(uidsArr, delDbSizeSku, this);
        }
        LOGGER.warn("delCartRedunSkus end,uids:{}", uids);
        return ResultUtil.handleSuccessReturn(rst);
    }

    /**
     * 是否范围uids
     *
     * @param uids
     * @return
     */
    private boolean isRangeUids(String uids) {
        return uids.indexOf("~") > 0;
    }

    /**
     * 同步购物车别号alias
     *
     * @param uids
     * @return
     */
    public Result<String> syncCartAlias(String uids) {
        String rst = "";
        if (StringUtils.isBlank(uids)) {
            //不传则表中存在的最大用户id为最大临界为范围操作
            int minUid = 1;
            int maxUid = cartService.getMaxUid4GlobalTab();
            rst = joinForkUtil.syncAliasByRange(minUid, maxUid, this);
        } else if (uids.contains("~")) {
            //指定用户id范围操作
            String[] uidsArr = uids.split("~");
            int minUid = Integer.valueOf(uidsArr[0]);
            int maxUid = Integer.valueOf(uidsArr[1]);
            rst = joinForkUtil.syncAliasByRange(minUid, maxUid, this);
        } else {
            //指定具体用户id操作
            String[] uidsArr = uids.split(",");
            rst = joinForkUtil.syncAliasByUids(uidsArr, this);
        }
        return ResultUtil.handleSuccessReturn(rst);
    }

    /**
     * 根据doSide删除该侧的冗余sku数据
     *
     * @param delSkuDbSize
     * @param uid
     * @return
     */
    public int execDelRedunSkus(boolean delSkuDbSize, int uid) {

        LOGGER.warn("execDelRedunSkus start uid:{}", uid);
        int rst = 1;
        MergeModel merge = new MergeModel(uid, 12, 2);
        String alias = null;
        if (delSkuDbSize) {
            alias = cacheManager.getCartAlias(Constant.DEFAULT_ID_2_12 + uid);
        } else {
            alias = cartService.getGroupTabAlias(2, 12, uid);
        }
        LOGGER.warn("execDelRedunSkus alias:{}", alias);
        if (StringUtils.isBlank(alias)) {
            LOGGER.warn(StatusCode.CART_ALIAS_NULL.getMsg() + "," + uid);
            return rst;
        }
        Map<String, CartSkuModel> cacheCartSkusMap = justCacheCartSkus(merge);
        //排除缓存为空的情况，不执行删除db数据操作
        if (cacheCartSkusMap.isEmpty() && delSkuDbSize) {
            LOGGER.warn("[execDelRedunSkus] uid:{},cacheCartSkusMap:{}", uid, cacheCartSkusMap);
            return 0;
        }

        Set<String> mergeAllSkuCodeSet = new HashSet<>();
        Set<String> cacheSkuKeys = cacheCartSkusMap.keySet();
        mergeAllSkuCodeSet.addAll(cacheSkuKeys);

        Map<String, CartItemDO> itemMergeVoMap = getItemDoMap(merge);
        Set<String> itemKeys = itemMergeVoMap.keySet();
        mergeAllSkuCodeSet.addAll(itemKeys);

        Iterator<String> mergeAllSkuCodeItr = mergeAllSkuCodeSet.iterator();
        if (delSkuDbSize) {

            List<CartItemDO> addItems = new ArrayList<>();
            List<String> delItems = new ArrayList<>();

            while (mergeAllSkuCodeItr.hasNext()) {
                String skuCode = mergeAllSkuCodeItr.next();
                if (cacheSkuKeys.contains(skuCode) && !itemKeys.contains(skuCode)) {
                    CartSkuModel cartSku = cacheCartSkusMap.get(skuCode);
                    CartItemDO itemDO = convertCartItemDO(merge.getUid(), merge.getAppId(), merge.getCorpId(), alias, cartSku);
                    addItems.add(itemDO);
                }
                if (!cacheSkuKeys.contains(skuCode) && itemKeys.contains(skuCode)) {
                    delItems.add(skuCode);
                }
            }
            if (addItems.size() > 0) {
                rst = cartService.batchInsertSku(alias, addItems);
                LOGGER.warn("execDelRedunSkus batchInsertSku rst:{},uid:{}", rst, uid);
            }
            if (delItems.size() > 0) {
                rst = cartService.batchDelSku(alias,delItems);
                LOGGER.warn("execDelRedunSkus batchDelSku rst:{},uid:{}", rst, uid);
            }
        } else {
            // 以表为主
            List<String> addCartSkus = new ArrayList<>();
            List<String> delCartSkus = new ArrayList<>();

            while (mergeAllSkuCodeItr.hasNext()) {
                String skuCode = mergeAllSkuCodeItr.next();
                if (cacheSkuKeys.contains(skuCode) && !itemKeys.contains(skuCode)) {
                    delCartSkus.add(skuCode);
                }
                if (!cacheSkuKeys.contains(skuCode) && itemKeys.contains(skuCode)) {
                    addCartSkus.add(skuCode);
                }
            }
            if (addCartSkus.size() > 0) {
                Result result = null;
                for (String skuCode : addCartSkus) {
                    try {
                        AddSkuModel addSkuModel = new AddSkuModel();
                        addSkuModel.setCount(1);
                        addSkuModel.setSkuCode(skuCode);
                        AddModel addModel = new AddModel(uid, merge.getAppId(), merge.getCorpId(), addSkuModel);
                        result = addCart(addModel);
                        if (!StatusCode.SUCCESS.getCode().equals(result.getCode())) {
                            LOGGER.error("execDelRedunSkus addCart uid:{},result:{}", uid, result);
                            rst = 0;
                        }
                    } catch (Exception e) {
                        rst = 0;
                        LOGGER.error("execDelRedunSkus addCart uid:{},errMsg:{}", uid, e.getMessage());
                    }
                }
            }
            if (delCartSkus.size() > 0) {
                rst = delSkuCodes(merge.getCorpId(), merge.getAppId(), uid, delCartSkus);
                LOGGER.info("execDelRedunSkus batchDelSku rst:{},uid:{}", rst, uid);
            }
        }
        LOGGER.warn("execDelRedunSkus end uid:{},rst:{}", uid, rst);
        return rst;
    }

	private Map<String, CartItemDO> getItemDoMap(MergeModel mergeModel) {
        // 获取表购物车明细
        Map<String, CartItemDO> cartItemDOMap = new HashMap<>();
        List<CartItemDO> cartItemDOList = getDetail(mergeModel.getUid(), mergeModel.getAppId(), mergeModel.getCorpId());
        if (cartItemDOList != null && !cartItemDOList.isEmpty()) {
            cartItemDOList.stream().forEach(itemDO -> cartItemDOMap.put(itemDO.getSkuCode(), itemDO));
        }
        LOGGER.debug("getItemDoMap cart item size:{}", cartItemDOMap.size());
        return cartItemDOMap;
    }

	/**
     * CartSkuModel->CartItemDO
     *
     * @param uid
     * @param appId
     * @param corpId
     * @param alias
     * @param cartSku
     * @return
     */
    private CartItemDO convertCartItemDO(int uid, int appId, int corpId, String alias, CartSkuModel cartSku) {
        CartItemDO itemDO = new CartItemDO();
        itemDO.setAlias(alias);
        itemDO.setProductId(cartSku.getProductId());
        itemDO.setCount(cartSku.getCount());
        itemDO.setSkuId(cartSku.getSkuId());
        itemDO.setSkuCode(cartSku.getSkuCode());

        itemDO.setUserId(uid);
        itemDO.setAppId(appId);
        itemDO.setCorpId(corpId);
        return itemDO;
    }

	/**
     * 仅从缓存获取cartSkus
     *
     * @param merge
     * @return
     */
    private Map<String, CartSkuModel> justCacheCartSkus(MergeModel merge) {
        Map<String, String> map = cacheManager.getCartSkus(merge.getUid(), merge.getAppId(), merge.getCorpId());
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        } else if (map.containsKey(Constant.REDIS_ERROR_STRING)) {
            LOGGER.error("justCacheCartSkus cache unavalid ,uid:{}", merge.getUid());
            throw new BusinessException(StatusCode.CACHE_UNAVALID.getMsg());
        }
        Map<String, CartSkuModel> cartSkuModelMap = new HashMap<>();
        map.entrySet().stream().forEach(entry -> cartSkuModelMap.put(entry.getKey(), JsonUtils.parse(entry.getValue(), CartSkuModel.class)));
        return cartSkuModelMap;
    }

    /**
     * 根据uid执行同步购物车别号
     *
     * @param uid
     * @return
     */
    public int execSyncAliasByUid(int uid) {

        int rst = 1;
        String uidKey = Constant.DEFAULT_ID_2_12 + uid;

        try {
            String sourceAlias = null, cacheAlias = null, groupTabAlias = null, globalAlias = null;
            cacheAlias = cacheManager.getCartAlias(uidKey);
            groupTabAlias = cartService.getGroupTabAlias(2, 12, uid);
            globalAlias = cartService.getGlobalCartAlias(2, 12, uid);

            if (isCompleteCart(cacheAlias, groupTabAlias, globalAlias)) {
                LOGGER.debug("[sync cart alias completed:{}]", uidKey);
                return rst;
            }

            sourceAlias = choiceAlias(cacheAlias, groupTabAlias, globalAlias);
            if (StringUtils.isBlank(sourceAlias)) {
                LOGGER.debug(StatusCode.CART_ALIAS_NULL.getMsg() + ",{}", uid);
                return rst;
            }

            if (StringUtils.isBlank(cacheAlias)) {
                //同步到缓存
                rst = cacheManager.setCartAlias(uidKey, sourceAlias);
            }
            if (StringUtils.isBlank(groupTabAlias)) {
                // 同步到分组表
                CartDO cartDO = createCartDO(uid, 12, 2, sourceAlias);
                if (cartService.addNewCart(cartDO) == 0) {
                    rst = 0;
                }
            }
            if (StringUtils.isBlank(globalAlias)) {
                // 同步到全局表
                CartDO cartDO = createCartDO(uid, 12, 2, sourceAlias);
                if (cartService.addGlobalCart(cartDO) == 0) {
                    rst = 0;
                }
            }
        } catch (Exception e) {
            LOGGER.error("execSyncAliasByUid errMsg:{},fullIdsKey:{}", e.getMessage(), uidKey);
            rst = 0;
        }
        LOGGER.info("[end sync uid:{},rst:{}]", uid,rst);
        return rst;
    }

    /**
     * 选择购物车别号
     *
     * @param cacheAlias
     * @param groupTabAlias
     * @param globalAlias
     * @return
     */
    private String choiceAlias(String cacheAlias, String groupTabAlias, String globalAlias) {
        if (StringUtils.isNotBlank(cacheAlias)) {
            return cacheAlias;
        } else if (StringUtils.isNotBlank(groupTabAlias)) {
            return groupTabAlias;
        } else if (StringUtils.isNotBlank(globalAlias)) {
            return globalAlias;
        }
        return null;
    }

	private CartDO createCartDO(Integer uid, Integer appId, Integer corpId, String alias) {
        CartDO cart = new CartDO();
        cart.setUserId(uid);
        cart.setCorpId(corpId);
        cart.setAppId(appId);
        cart.setAlias(alias);
        return cart;
    }

	/**
     * 是否完整的购物车
     *
     * @param cacheAlias
     * @param groupTabAlias
     * @param globalAlias
     * @return
     */
    private boolean isCompleteCart(String cacheAlias, String groupTabAlias, String globalAlias) {
        return StringUtils.isNotBlank(cacheAlias) && StringUtils.isNotBlank(groupTabAlias) && StringUtils.isNotBlank(globalAlias);
    }

    public Result delCacheSku(String skuCode) {
        long rst = 0;
        CacheSku sku = cacheManager.getCacheSku(skuCode);
        if (sku == null) {
            return ResultUtil.handleSysFailtureReturn(StatusCode.SKU_NULL.getMsg());
        } else {
            rst = cacheManager.delCacheSku(skuCode);
            LOGGER.info("delCacheSku rst:{}", rst);
        }
        if (rst == 0) {
            return ResultUtil.handleSysFailtureReturn(rst + "");
        }
        return ResultUtil.handleSuccessReturn(rst);
    }
}
