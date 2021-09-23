package cn.nome.saas.cart.manager;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.cart.feign.*;
import cn.nome.saas.cart.model.CampaignSkuCodeReq;
import cn.nome.saas.cart.model.PromoVo;
import cn.nome.saas.cart.utils.SortCampaigns;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 活动促销服务方法
 *
 * @author chentaikuang
 */
@Component
public class PromoConvertManager {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 折扣活动类型
     */
    @Deprecated
    public static final int PROMO_TYPE_DISC = 2;

    /**
     *
     */
    public static final String PROMO_FULL_CODE = "package";

    @Autowired
    private IPromoService promoService;

    /**
     * 获取选品集对应活动实体的map
     *
     * @param corpId
     * @param appId
     * @param uid
     * @param productSetIds
     * @return Map<productSetId, CampaignModel>
     */
    public Map<Integer, CampaignModel> getProductSetIdCampaignMap(Integer corpId, Integer appId, Integer uid,
                                                                  List<Integer> productSetIds) {
        Map<Integer, CampaignModel> productSetIdRuleMap = null;
        Result<Map<Integer, List<CampaignModel>>> rulesRst = getCampaignByProductSetIds(corpId, appId, uid, productSetIds);
        if (rulesRst == null || rulesRst.getData() == null || rulesRst.getData().isEmpty()) {
            return productSetIdRuleMap;
        }
        Map<Integer, List<CampaignModel>> campaignModels = rulesRst.getData();
        productSetIdRuleMap = new HashMap<>();
        Iterator<Map.Entry<Integer, List<CampaignModel>>> itr = campaignModels.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Integer, List<CampaignModel>> entry = itr.next();
            List<CampaignModel> val = entry.getValue();
            if (val != null && !val.isEmpty()) {
                if (val.size() > 1) {
                    Collections.sort(val, new SortCampaigns());
                }
                CampaignModel campaign = getBestCampaign(val);
                productSetIdRuleMap.put(entry.getKey(), campaign);
            }
        }
        return productSetIdRuleMap;
    }

    /**
     * 根据商品集获取活动
     *
     * @param corpId
     * @param appId
     * @param uid
     * @param productSetIds
     * @return
     */
    public Result<Map<Integer, List<CampaignModel>>> getCampaignByProductSetIds(Integer corpId, Integer appId, Integer uid, List<Integer> productSetIds) {
        Result<Map<Integer, List<CampaignModel>>> rulesRst = null;
        try {
            rulesRst = promoService.getCampaignByProductSetIds(corpId, appId, uid,
                    productSetIds);
            LOGGER.debug("[getCampaignByProductSetIds] rulesRst:{}", JSONObject.toJSONString(rulesRst));
        } catch (Exception e) {
            LOGGER.error("[getCampaignByProductSetIds] errMsg:{}", e.getMessage());
        }
        return rulesRst;
    }

    /**
     * 经过自定义排序器排序后，第一个即是最优活动
     *
     * @param val
     * @return
     */
    private CampaignModel getBestCampaign(List<CampaignModel> val) {
        CampaignModel campaign = new CampaignModel();
        BeanUtils.copyProperties(val.get(0), campaign);
        return campaign;
    }

    /**
     * 满subType免subNum
     *
     * @param subType n
     * @param subNum  1
     * @return
     */
    public String fillPromoTips(Integer subType, int subNum) {
        StringBuffer sbf = new StringBuffer("满");
        return sbf.append(subType).append("免").append(subNum).toString();
    }

    public List<CampaignInfoResult> getCampaignListBySkuCodes(Integer corpId, Integer appId, Integer uid, List<SkuCodeReq> skuCodeReqs) {
        Result<List<CampaignInfoResult>> rst = null;
        List<CampaignInfoResult> data = null;
        try {
//            rst = promoService.campaignListBySkuCodes(corpId, appId, uid, skuCodeReqs);
            CampaignSkuCodeReq req = new CampaignSkuCodeReq();
            req.setAppId(appId);
            req.setCorpId(corpId);
            req.setUid(uid);
            req.setSkuCodes(skuCodeReqs);
            rst = promoService.campaignListBySkuCodes(req);
        } catch (Exception e) {
            LOGGER.error("campaignListBySkuCodes errMsg:{},uid:{}", e.getMessage(), uid);
        }
        if (rst == null || rst.getData() == null || rst.getData().isEmpty()) {
            data = Collections.emptyList();
        } else {
            data = rst.getData();
        }
//        data = testDataCampaignListBySkuCodes(skuCodeReqs);
        return data;
    }

    /**
     * 测试数据
     *
     * @param skuCodeReqs
     * @return
     */
    private List<CampaignInfoResult> testDataCampaignListBySkuCodes(List<SkuCodeReq> skuCodeReqs) {
        List<CampaignInfoResult> rst = new ArrayList<>();
        for (SkuCodeReq req : skuCodeReqs) {
            CampaignInfoResult vo = new CampaignInfoResult();
            int type = new Random().nextInt(2) + 1;
            LOGGER.warn("type:{}", type);
            vo.setCampaignType(type);
            vo.setId(new Random().nextInt(2000));
            vo.setName(RandomStringUtils.randomNumeric(10));
            vo.setProductSetId(new Random().nextInt(10000));
            Map<String, Integer> map = new HashMap<>();
            Integer disctPrice = new Random().nextInt(500);
            map.put(req.getSkuCode(), disctPrice);
            vo.setPromotionSkuCodes(map);
            vo.setSkuCodeList(Arrays.asList(req.getSkuCode()));
            vo.setSubType(3);
            rst.add(vo);
        }
        return rst;
    }

    @Deprecated
    public int convertPromo(Map<Integer, PromoVo> promoVoMap, CampaignInfoResult psr) {
        int setId = psr.getProductSetId();
        String promoName = psr.getName();
        int promoId = psr.getId();
        int subType = psr.getSubType();
        PromoVo promoVo = newPromoVo(promoName, promoId, subType);
        promoVoMap.put(setId, promoVo);
        return setId;
    }

    public int convertPromoV3(Map<Integer, PromoVo> promoVoMap, CampaignInfoResult psr) {
        String promoName = psr.getName();
        int promoId = psr.getId();
        PromoVo promoVo = newPromoVo(promoName, promoId, psr.getTips(), psr.getSimpleName(), psr.getProductSetIds(), psr.getCampaignType());
        promoVoMap.put(promoId, promoVo);
        return promoId;
    }

    @Deprecated
    private PromoVo newPromoVo(String promoName, int promoId, int subType) {
        PromoVo promoVo = new PromoVo();
        promoVo.setId(promoId);
        promoVo.setName(promoName);
        promoVo.setCondition(subType);
        promoVo.setTips(fillPromoTips(subType, 1));
        return promoVo;
    }

    private PromoVo newPromoVo(String promoName, int promoId, String tips, String simpleName, List<Integer> productSetIds, Integer campaignType) {
        PromoVo promoVo = new PromoVo();
        promoVo.setId(promoId);
        promoVo.setName(promoName);
        promoVo.setTips(tips);
        promoVo.setProductSetIds(productSetIds);
        promoVo.setSimpleName(simpleName);
        promoVo.setCampaignType(campaignType);
        return promoVo;
    }

    /**
     * 转换满减活动vo
     *
     * @param rule
     * @return
     */
    @Deprecated
    public PromoVo convertPromoVo(CampaignModel rule) {
        PromoVo promoVo = new PromoVo();
        promoVo.setId(rule.getId());
        promoVo.setCondition(rule.getSubType());
        promoVo.setName(rule.getName());
        return promoVo;
    }

    /**
     * 计算结算商品促销、优惠价格
     *
     * @param corpId
     * @param appId
     * @param uid
     * @param skuCodeReqs
     * @return
     */
    public List<PromoSkuCalcResult> calcCampaignPromoAmount(int corpId, int appId, int uid, List<SkuCodeReq> skuCodeReqs) {
        Result<List<PromoSkuCalcResult>> rst = null;
        try {
            //rst = promoService.calcCampaignPromoAmount(corpId, appId, uid, skuCodeReqs);
            CampaignSkuCodeReq req = new CampaignSkuCodeReq();
            req.setAppId(appId);
            req.setCorpId(corpId);
            req.setUid(uid);
            req.setSkuCodes(skuCodeReqs);
            rst = promoService.calcCampaignPromoAmount(req);
        } catch (Exception e) {
            LOGGER.error("calcCampaignPromoAmount errMsg:{},uid:{}", e.getMessage(), uid);
        }
        List<PromoSkuCalcResult> data = null;
        if (rst == null || rst.getData() == null || rst.getData().isEmpty()) {
            data = Collections.emptyList();
        } else {
            data = rst.getData();
        }
//        data = testDataCalcCampaignPromoAmount(skuCodeReqs);
        return data;

    }

    /**
     * 购物车预结算接口测试数据
     *
     * @param skuCodeReqs
     * @return
     */
    private List<PromoSkuCalcResult> testDataCalcCampaignPromoAmount(List<SkuCodeReq> skuCodeReqs) {
        List<PromoSkuCalcResult> rst = new ArrayList<>();
        for (SkuCodeReq req : skuCodeReqs) {
            PromoSkuCalcResult vo = new PromoSkuCalcResult();
            int type = new Random().nextInt(2) + 1;
            LOGGER.warn("type:{}", type);
            vo.setCampaignType(type);
            vo.setId(new Random().nextInt(2000));
            vo.setName(RandomStringUtils.randomNumeric(10));
            vo.setProductSetId(new Random().nextInt(10000));
            Map<String, Integer> map = new HashMap<>();
            Integer disctPrice = new Random().nextInt(500);
            map.put(req.getSkuCode(), disctPrice);
            vo.setPromotionSkuCodes(map);
            vo.setSkuCodeList(Arrays.asList(req.getSkuCode()));
            vo.setSubType(3);
            vo.setPromotionPrice(disctPrice);
            vo.setStatus(type);
            rst.add(vo);
        }
        return rst;
    }
}
