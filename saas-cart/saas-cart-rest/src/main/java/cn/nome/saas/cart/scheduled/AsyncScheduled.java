package cn.nome.saas.cart.scheduled;

import cn.nome.platform.common.kafka.NomeKafkaService;
import cn.nome.platform.common.kafka.model.send.MqSendReqVo;
import cn.nome.platform.common.kafka.model.send.MqSendRespVo;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.constant.KafkaCont;
import cn.nome.saas.cart.enums.StatusCode;
import cn.nome.saas.cart.feign.IStoreService;
import cn.nome.saas.cart.manager.CacheManager;
import cn.nome.saas.cart.manager.CartServiceManager;
import cn.nome.saas.cart.model.AddModel;
import cn.nome.saas.cart.model.AsyncAddCartSku;
import cn.nome.saas.cart.repository.entity.AddCartItemDO;
import cn.nome.saas.cart.repository.entity.CartDO;
import cn.nome.saas.cart.repository.entity.DelCartItemDO;
import cn.nome.saas.cart.repository.entity.UpdateCartItemDO;
import cn.nome.saas.cart.service.CartService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(value = 100)
public class AsyncScheduled {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CartService cartService;

    @Autowired
    private CartServiceManager cartServiceManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private NomeKafkaService nomeKafkaService;

    @Async("taskExecutor")
    public <T> void asyncTask(T t, String type) {
        LOGGER.debug("AsyncScheduled start... asyncUserSkuModel=" + t + ",type:" + type);
        if (type.equals(Constant.ASYNC_TASK_TYPE_ADD)) {
            if (t instanceof AddCartItemDO) {
                AddCartItemDO addCartItemDO = (AddCartItemDO) t;
                if (StringUtils.isBlank(addCartItemDO.getAlias())) {
                    String alias = cartServiceManager.getCartAlias(addCartItemDO.getUserId(), addCartItemDO.getAppId(),
                            addCartItemDO.getCorpId());
                    Assert.notNull(alias, StatusCode.CART_NO_FOUND.getMsg());
                    addCartItemDO.setAlias(alias);
                }
                cartService.addCartItem(addCartItemDO);
                LOGGER.info("[ASYNC_TASK_TYPE_ADD] alias:{}", addCartItemDO.getAlias());
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_GLOBAL_CART_ADD)) {
            if (t instanceof CartDO) {
                CartDO cartDO = (CartDO) t;
                Assert.notNull(cartDO.getAlias(), StatusCode.CART_NO_FOUND.getMsg());
                cartService.addGlobalCart(cartDO);
                LOGGER.info("[ASYNC_TASK_TYPE_GLOBAL_CART_ADD] alias:{}", cartDO.getAlias());
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_MODIFY)) {
            if (t instanceof UpdateCartItemDO) {
                UpdateCartItemDO updateCartItemDO = (UpdateCartItemDO) t;
                if (StringUtils.isBlank(updateCartItemDO.getAlias())) {
                    String alias = cartServiceManager.getCartAlias(updateCartItemDO.getUserId(),
                            updateCartItemDO.getAppId(), updateCartItemDO.getCorpId());
                    Assert.notNull(alias, StatusCode.CART_NO_FOUND.getMsg());
                    updateCartItemDO.setAlias(alias);
                }
                cartService.updateItem(updateCartItemDO);
                LOGGER.info("[ASYNC_TASK_TYPE_MODIFY] alias:{}", updateCartItemDO.getAlias());
            } else {
                LOGGER.error("[ASYNC_TASK_TYPE_MODIFY]AsyncScheduled type error! type:" + type + ", can not cast to UpdateCartItemDO");
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_DEL)) {
            if (t instanceof DelCartItemDO) {
                DelCartItemDO delCartItemDO = (DelCartItemDO) t;
                if (StringUtils.isBlank(delCartItemDO.getAlias())) {
                    String alias = cartServiceManager.getCartAlias(delCartItemDO.getUserId(), delCartItemDO.getAppId(),
                            delCartItemDO.getCorpId());
                    Assert.notNull(alias, StatusCode.CART_NO_FOUND.getMsg());
                    delCartItemDO.setAlias(alias);
                }
                cartService.delSkuCodes(delCartItemDO);
                LOGGER.info("[ASYNC_TASK_TYPE_DEL] alias:{}", delCartItemDO.getAlias());
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_ADD_OR_MODIFY)) {
            if (t instanceof AddCartItemDO) {
                AddCartItemDO addCartItemDO = (AddCartItemDO) t;
                cartService.insertOrUpdate(addCartItemDO);
                LOGGER.info("[ASYNC_TASK_TYPE_ADD_OR_MODIFY] alias:{}", addCartItemDO.getAlias());
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_CART_SKU_ADD)) {
            if (t instanceof AsyncAddCartSku) {
                AsyncAddCartSku sku = (AsyncAddCartSku) t;
                cacheManager.setCartSku(cacheManager.getCorpIdAppIdUId(sku.getUid(), sku.getAppId(), sku.getCorpId()),
                        sku.getCartSkuModel());
                LOGGER.info("[ASYNC_TASK_TYPE_CART_SKU_ADD] uid:{},skuCode:{}", sku.getUid(), sku.getCartSkuModel().getSkuCode());
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_SEND_SHOPPING_LIST)) {
            if (t instanceof String) {
                String sendMsg = (String) t;
                sendMqShoppinglist(sendMsg);
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_DEL_SHOPPING_LIST)) {
            if (t instanceof DelCartItemDO) {
                DelCartItemDO delCartItemDO = (DelCartItemDO) t;
                List<String> skuCodes = delCartItemDO.getSkuCodes();
                /*
                for (String skuCode : skuCodes) {
                    try {
                        String rtnModel = storeService.delShoppingList(delCartItemDO.getCorpId(), delCartItemDO.getAppId(), delCartItemDO.getUserId(), skuCode);
                        LOGGER.info("[ASYNC_TASK_TYPE_DEL_SHOPPING_LIST]del skuCode:{},rtnModel:{}", skuCode, rtnModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.info("[ASYNC_TASK_TYPE_DEL_SHOPPING_LIST]del fail,skuCode:{},msg:{}", skuCode, e.getMessage());
                    }
                }
                */
                String rtnModel = storeService.delShoppingList(delCartItemDO.getCorpId(), delCartItemDO.getAppId(), delCartItemDO.getUserId(), skuCodes);
                LOGGER.info("[ASYNC_TASK_TYPE_DEL_SHOPPING_LIST]del skuCodes:{},rtnModel:{}", skuCodes, rtnModel);
            }
        } else if (type.equals(Constant.ASYNC_TASK_TYPE_ADD_SHOPPING_LIST)) {
            if (t instanceof AddModel) {
                AddModel addDO = (AddModel) t;
                try {
                    Object rtnModel = storeService.addShoppingList(addDO.getCorpId(), addDO.getAppId(), addDO.getUid(),
                            addDO.getSku().getSkuCode());
                    LOGGER.info("[ASYNC_TASK_TYPE_ADD_SHOPPING_LIST]add skuCode:{},rtn:{}", addDO.getSku().getSkuCode(), JSONObject.toJSONString(rtnModel));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("[ASYNC_TASK_TYPE_ADD_SHOPPING_LIST] add fail,skuCode:{},msg:{}", addDO.getSku().getSkuCode(), e.getMessage());
                }
            }
        } else {
            LOGGER.error("AsyncScheduled type error! type:" + type);
        }
        LOGGER.debug("AsyncScheduled end...");
    }

    private void sendMqShoppinglist(String sendMsg) {
        MqSendReqVo sendReqVo = new MqSendReqVo();
        sendReqVo.setPlatform(Constant.PLATFORM);
        sendReqVo.setTopicName(KafkaCont.TOPIC_WX_SHOPPING_LIST);
        sendReqVo.setBizType(KafkaCont.BIZ_TYPE_SHOPPING_LIST);
        List<String> datas = new ArrayList<>(1);
        datas.add(sendMsg);
        sendReqVo.setDatas(datas);
        try {
            Result<MqSendRespVo> respVo = nomeKafkaService.send(sendReqVo);
            LOGGER.debug("[ASYNC_SEND_MQ_SHOPPING_LIST] respVo:{}", JSONObject.toJSONString(respVo));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug("[ASYNC_SEND_MQ_SHOPPING_LIST] sendMsg:{},errMsg:{}", sendMsg, e.getMessage());
        }
    }

}
