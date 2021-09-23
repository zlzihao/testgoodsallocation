package cn.nome.saas.cart.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.cart.enums.StatusCode;
import cn.nome.saas.cart.feign.CampaignModel;
import cn.nome.saas.cart.feign.IGoodsService;
import cn.nome.saas.cart.feign.SkuModel;
import cn.nome.saas.cart.model.CartSkuModel;
import cn.nome.saas.cart.utils.SortCampaigns;
import cn.nome.saas.cart.utils.SortSkuModel;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 商品服务方法
 *
 * @author chentaikuang
 */
@Component
public class ProductConvertManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Deprecated
    static final Integer NULL_PRODUCT_SET_ID = 0;
    static final Integer NULL_CAMPAIGN_ID = 0;
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private PromoConvertManager promoConvertManager;

    /**
     * 通过商品id获取对应最佳商品集ID
     *
     * @param corpId
     * @param appId
     * @param productIds
     * @return
     */
    public Map<Integer, Integer> getSetIdMapByIds(Integer uid, Integer appId, Integer corpId, Set<Integer> productIds) {
        Result<Map<Integer, Set<Integer>>> productSetIdsRst = null;
        try {
            productSetIdsRst = goodsService.getProductSetIds(corpId, appId, uid, new ArrayList<>(productIds));
        } catch (Exception e) {
            LOGGER.error("getSetIdMapByIds err msg:{}", e.getMessage());
        }
        Map<Integer, Integer> productIdSetIdsMap = new HashMap<>();
        if (productSetIdsRst == null || productSetIdsRst.getData() == null || productSetIdsRst.getData().isEmpty()) {
            LOGGER.warn("GET_SETID_MAP_BYIDS qry null,productIds:{}", productIds.toString());
            return productIdSetIdsMap;
        }
        Map<Integer, Set<Integer>> rstData = productSetIdsRst.getData();
        // 存在一对多的情况:1个productId对应多个productSetId
        Iterator<Map.Entry<Integer, Set<Integer>>> itr = rstData.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Integer, Set<Integer>> entry = itr.next();
            Integer productSetId = getBestProductSetId(uid, appId, corpId, entry.getValue());
            productIdSetIdsMap.put(entry.getKey(), productSetId);
        }
        return productIdSetIdsMap;
    }

    /**
     * 过滤符合最有条件的活动商品集id
     *
     * @param corpId
     * @param appId
     * @param setIds
     * @return
     */
    private Integer getBestProductSetId(Integer uid, Integer appId, Integer corpId, Set<Integer> setIds) {
        if (setIds == null) {
            return NULL_PRODUCT_SET_ID;
        }
        List<Integer> productSetIds = new ArrayList<>(setIds);
        if (productSetIds.size() == 1) {
            return productSetIds.get(0);
        } else {

            Result<Map<Integer, List<CampaignModel>>> campaignsRst = promoConvertManager.getCampaignByProductSetIds(corpId,
                    appId, uid, productSetIds);

            // 查询promo获取condition条件最低门槛的
            if (campaignsRst != null && campaignsRst.getData() != null && !campaignsRst.getData().isEmpty()) {
                List<CampaignModel> list = new ArrayList<>();
                Map<Integer, List<CampaignModel>> data = campaignsRst.getData();
                data.entrySet().forEach(entry -> list.addAll(entry.getValue()));
                Collections.sort(list, new SortCampaigns());
                CampaignModel tarCampaign = list.get(0);
                LOGGER.info("GET_BEST_PRODUCT_SET_ID get target campaignModel:{}",
                        JSONObject.toJSONString(tarCampaign));
                return tarCampaign.getProductSetId();
            }

            // 默认返回第一个
            LOGGER.info("GET_BEST_PRODUCT_SET_ID return default productSetId:{}", productSetIds.get(0));
            return productSetIds.get(0);
        }
    }

    /**
     * 多个skuId批量查询sku
     *
     * @param appId
     * @param corpId
     * @param skuCodes
     * @return
     */
    public List<SkuModel> getSkuByCodes(Integer appId, Integer corpId, List<String> skuCodes) {
        Result<List<SkuModel>> skus = null;
        try {
            skus = goodsService.getSkuByIdsOrCodes(corpId, appId, skuCodes);
        }catch (Exception e){
            LOGGER.error("getSkuByCodes err msg:{}",e.getMessage());
        }
        if (skus == null || skus.getData() == null) {
            LOGGER.debug("GET_SKU_BY_CODES return null:{}", skuCodes.toString());
            return null;
        }
        LOGGER.debug("GET_SKU_BY_CODES get skus:{}", JSONObject.toJSONString(skus));
        List<SkuModel> skuData = skus.getData();

        List<SkuModel> choiceSkus = new ArrayList<>();
        // 过滤筛选重复的sku
        Map<String, List<SkuModel>> skuDataGroup = skuData.stream()
                .collect(Collectors.groupingBy(SkuModel::getSkuCode));
        Iterator<Entry<String, List<SkuModel>>> itr = skuDataGroup.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, List<SkuModel>> entry = itr.next();
            List<SkuModel> vals = entry.getValue();
            if (vals.size() > 1) {
                LOGGER.warn("GET_SKU_BY_CODES, have more sku with same skuCode:{}", vals.get(0).getSkuCode());
                Collections.sort(vals, new SortSkuModel());
            }
            choiceSkus.add(vals.get(0));
        }
        return choiceSkus;
    }

    /**
     * 查询实时sku
     * @param skuCode
     * @param corpIdAppIdUid
     * @return
     */
    public SkuModel getRealTimeSku(String skuCode, String corpIdAppIdUid) {
        String[] idArr = corpIdAppIdUid.split("_");
        List<String> skuCodes = new ArrayList<>(1);
        skuCodes.add(skuCode);
        //corpId_appId_uid
        List<SkuModel> skus = getSkuByCodes(Integer.valueOf(idArr[1]), Integer.valueOf(idArr[0]), skuCodes);
        LOGGER.info("getRealTimeSku ,skuCode:{},corpIdAppIdUid:{}", skuCode,corpIdAppIdUid);
        if (skus == null || skus.isEmpty()) {
            throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
        }
        SkuModel sku = skus.get(0);
        return sku;
    }

    public void resetCartSkuPrice(Map<String, CartSkuModel> validSkusMap, Map<String, Integer> promoPriceMap) {
        //重新设置价格
        if (promoPriceMap != null && !promoPriceMap.isEmpty()) {
            Iterator<Entry<String, CartSkuModel>> itr = validSkusMap.entrySet().iterator();
            while (itr.hasNext()) {
                Entry<String, CartSkuModel> cartSkuEntriy = itr.next();
                CartSkuModel cartSku = cartSkuEntriy.getValue();
                if (promoPriceMap.containsKey(cartSku.getSkuCode())) {
                    cartSku.setDiscPrice(promoPriceMap.get(cartSku.getSkuCode()));
                }
            }
        }
    }

    public void resetSkuDiscPrice(Map<String, Integer> skuCodePriceMap, Map<String, Integer> promoSkuCodePriceMap) {
        if (!promoSkuCodePriceMap.isEmpty()){
            Iterator<Map.Entry<String, Integer>> itr = promoSkuCodePriceMap.entrySet().iterator();
            while (itr.hasNext()){
                Map.Entry<String, Integer> entry = itr.next();
                String skuCode = entry.getKey();
                if (skuCodePriceMap.containsKey(skuCode)){
                    LOGGER.info("sku price change:{}->{}",skuCodePriceMap.get(skuCode),entry.getValue());
                    skuCodePriceMap.put(skuCode,entry.getValue());
                }
            }
        }
    }
}
