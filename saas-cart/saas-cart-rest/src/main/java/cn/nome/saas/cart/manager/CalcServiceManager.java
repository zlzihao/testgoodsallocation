package cn.nome.saas.cart.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.enums.StatusCode;
import cn.nome.saas.cart.feign.*;
import cn.nome.saas.cart.model.*;
import cn.nome.saas.cart.utils.SortCalcSkuPrice;
import cn.nome.saas.cart.utils.SortCampaigns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

/**
 * 结算服务
 *
 * @author chentaikuang
 */
@Component
public class CalcServiceManager {

	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductConvertManager productConvertManager;

	@Autowired
	private PromoConvertManager promoConvertManager;

	/**
	 * 计算商品活动规则信息及金额
	 */
	public CalcResultModel calcAmount(CalcModel calcModel) {

        int uid = calcModel.getUid();
        int appId = calcModel.getAppId();
        int corpId = calcModel.getCorpId();
        List<CalcSkuModel> calcSkuModelList = calcModel.getCalcSku();
        if (calcSkuModelList == null || calcSkuModelList.isEmpty()) {
            throw new BusinessException(StatusCode.SKU_NULL.getCode(), StatusCode.SKU_NULL.getMsg());
        }

        Set<String> skuCodeSet = new HashSet<>();
        Set<Integer> productIds = new HashSet<>();
        Iterator<CalcSkuModel> calcItr = calcSkuModelList.iterator();
        Map<Integer, List<CalcSkuModel>> productIdCalcSkuMap = new HashMap<>();
        Map<String, Integer> skuCodeCountMap = new HashMap<>();
        while (calcItr.hasNext()) {
            CalcSkuModel calcSku = calcItr.next();
            int productId = calcSku.getProductId();
            productIds.add(productId);
            skuCodeSet.add(calcSku.getSkuCode());

            if (productIdCalcSkuMap.containsKey(productId)) {
                List<CalcSkuModel> calcSkuModels = productIdCalcSkuMap.get(productId);
                calcSkuModels.add(calcSku);
                productIdCalcSkuMap.put(productId, calcSkuModels);
            } else {
                List<CalcSkuModel> calcSkuModels = new ArrayList<>();
                calcSkuModels.add(calcSku);
                productIdCalcSkuMap.put(productId, calcSkuModels);
            }

            skuCodeCountMap.put(calcSku.getSkuCode(), calcSku.getCount());
        }

        Map<Integer, Integer> productAndSetIdMap = productConvertManager.getSetIdMapByIds(uid, appId, corpId,
                productIds);
        LOGGER.info("[calcAmount] productAndSetIdMap:{}", productAndSetIdMap);

        List<Integer> productSetIdList = new ArrayList<>();
        productAndSetIdMap.entrySet().stream().forEach(entry -> productSetIdList.add(entry.getValue()));

        // 根据商品集对商品进行分组 start
        Map<Integer, List<CalcSkuModel>> productSetIdCalcSkuMap = new HashMap<>();
        Iterator<Map.Entry<Integer, List<CalcSkuModel>>> productItr = productIdCalcSkuMap.entrySet().iterator();
        while (productItr.hasNext()) {
            Map.Entry<Integer, List<CalcSkuModel>> entry = productItr.next();

            Integer productId = entry.getKey();
            List<CalcSkuModel> calcSkus = entry.getValue();
            Integer productSetId = productAndSetIdMap.get(productId);
            if (productSetId == null) {
                productSetId = ProductConvertManager.NULL_PRODUCT_SET_ID;
            }

            if (productSetIdCalcSkuMap.containsKey(productSetId)) {
                List<CalcSkuModel> productSetIdSkus = productSetIdCalcSkuMap.get(productSetId);
                productSetIdSkus.addAll(calcSkus);
                productSetIdCalcSkuMap.put(productSetId, productSetIdSkus);
            } else {
                List<CalcSkuModel> productSetIdSkus = new ArrayList<>();
                productSetIdSkus.addAll(calcSkus);
                productSetIdCalcSkuMap.put(productSetId, productSetIdSkus);
            }
        }
        // 根据商品集对商品进行分组 end

		// sku信息
		List<SkuModel> skusData = productConvertManager.getSkuByCodes(appId, corpId, new ArrayList<>(skuCodeSet));
		if (skusData == null || skusData.isEmpty()){
			throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
		}

		// 活动信息
		// productSetId->campaignModel
		Map<Integer, CampaignModel> campaignModelMap = promoConvertManager.getProductSetIdCampaignMap(corpId,appId,uid,productSetIdList);

		CalcResultModel resultModel = new CalcResultModel();
		List<ActiveSkuDetail> activeSkuDetail = new ArrayList<>();
		List<String> inactiveSku = new ArrayList<>();
		BigDecimal discAmount = new BigDecimal(0);

        Iterator<Map.Entry<Integer, List<CalcSkuModel>>> pcItr = productSetIdCalcSkuMap.entrySet().iterator();
        while (pcItr.hasNext()) {
            Map.Entry<Integer, List<CalcSkuModel>> entry = pcItr.next();
            int productSetId = entry.getKey();
            List<CalcSkuModel> calcSkuModels = entry.getValue();

            if (campaignModelMap == null || campaignModelMap.isEmpty()
                    || ProductConvertManager.NULL_PRODUCT_SET_ID == productSetId
                    || !campaignModelMap.containsKey(productSetId)) {
                // 无活动
                inactiveSku.addAll(convetNoDiscSku(calcSkuModels));
            } else {
                // 有活动sku
                CampaignModel campaign = campaignModelMap.get(productSetId);
                int condition = campaign.getSubType();// 满n
                int subNum = campaign.getCampaignType();
                int count = 0;
                for (CalcSkuModel sku : calcSkuModels) {
                    count += sku.getCount();
                }
                // 满足条件，要减一
                if (count >= condition) {
                    ActiveSkuDetail detail = new ActiveSkuDetail();
                    detail.setCondition(condition);
                    detail.setProductSetId(productSetId);
                    detail.setActiveTips(promoConvertManager.fillPromoTips(condition, subNum));

                    List<String> skuCodes = convetActiveSku(calcSkuModels);
                    detail.setSkuCodes(skuCodes);

                    // 正序排列后选最少价格的减一
                    Collections.sort(skusData, new SortCalcSkuPrice());
                    Iterator<SkuModel> skuItr = skusData.iterator();
                    while (skuItr.hasNext()) {
                        SkuModel skuModel = (SkuModel) skuItr.next();
                        if (skuCodes.contains(skuModel.getSkuCode())) {

                            discAmount = discAmount.add(new BigDecimal(skuModel.getPrice()));
                            // 活动单品优惠金额
                            detail.setDiscAmount(skuModel.getPrice());
                            break;
                        }
                    }

                    activeSkuDetail.add(detail);
                } else {
                    // 有活动但不满足
                    inactiveSku.addAll(convetNoDiscSku(calcSkuModels));
                }
            }
        }

        resultModel.setDiscAmount(discAmount.doubleValue());
        resultModel.setActiveDetail(activeSkuDetail);
        resultModel.setInactiveSku(inactiveSku);
        resultModel.setTotalAmount(getTotalAmount(skuCodeCountMap, skusData));

        return resultModel;
    }

    /**
     * 获取总金额
     *
     * @param skuCodeCountMap
     * @param skusData
     * @return
     */
    private double getTotalAmount(Map<String, Integer> skuCodeCountMap, List<SkuModel> skusData) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (SkuModel skuModel : skusData) {
            if (skuCodeCountMap.containsKey(skuModel.getSkuCode())) {

                int skuPrice = skuCodeCountMap.get(skuModel.getSkuCode()) * skuModel.getPrice();
                LOGGER.debug("skuCode:{},skuPrice:{}", skuModel.getSkuCode(), skuPrice);

                BigDecimal countPrice = new BigDecimal(skuPrice);
                totalAmount = totalAmount.add(countPrice);
            }
        }
        return totalAmount.doubleValue();
    }

    private List<String> convetActiveSku(List<CalcSkuModel> calcSkuModels) {
        List<String> list = new ArrayList<>();
        Iterator<CalcSkuModel> itr = calcSkuModels.iterator();
        while (itr.hasNext()) {
            CalcSkuModel calcSkuModel = (CalcSkuModel) itr.next();
            list.add(calcSkuModel.getSkuCode());
        }
        return list;
    }

    private List<String> convetNoDiscSku(List<CalcSkuModel> calcSkuModels) {
        List<String> list = new ArrayList<>();
        Iterator<CalcSkuModel> itr = calcSkuModels.iterator();
        while (itr.hasNext()) {
            CalcSkuModel calcSkuModel = itr.next();
            list.add(calcSkuModel.getSkuCode());
        }
        return list;
    }

	/**
	 * 取最好规则的活动
	 *
	 * @param campaignList
	 * @return
	 */
	private CampaignModel getBestCampaign(List<CampaignModel> campaignList) {
		if (campaignList == null || campaignList.isEmpty()) {
			return null;
		} else {
			Collections.sort(campaignList, new SortCampaigns());
			return campaignList.get(0);
		}
	}

	/**
	 * 计算商品活动规则信息及金额 V2
	 *
	 * @param calcModel
	 * @return
	 */
	public CartCalcWrap calcV2(CalcModel calcModel) {

		CartCalcWrap calcWrap = null;

		int uid = calcModel.getUid();
		int appId = calcModel.getAppId();
		int corpId = calcModel.getCorpId();
		List<CalcSkuModel> calcSkuModelList = calcModel.getCalcSku();
		if (calcSkuModelList == null || calcSkuModelList.isEmpty()) {
			throw new BusinessException(StatusCode.SKU_NULL.getCode(), StatusCode.SKU_NULL.getMsg());
		}
		//结算skuCodeList
		List<String> skuCodeList = new ArrayList<>(calcSkuModelList.size());
		//skuCode->count
		Map<String, Integer> skucodeCountMap = new HashMap<>(calcSkuModelList.size());
		Iterator<CalcSkuModel> calcSkuItr = calcSkuModelList.iterator();
		while (calcSkuItr.hasNext()) {
			CalcSkuModel calcSku = calcSkuItr.next();
			skuCodeList.add(calcSku.getSkuCode());

			skucodeCountMap.put(calcSku.getSkuCode(), calcSku.getCount());
		}
		//结算sku数据
		List<SkuModel> skusData = productConvertManager.getSkuByCodes(appId, corpId, skuCodeList);
		if (skusData == null || skusData.isEmpty()) {
			throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
		}
		Map<String, Integer> skuCodePriceMap = new HashMap<>(skusData.size());
		skusData.stream().forEach(skuModel -> skuCodePriceMap.put(skuModel.getSkuCode(), skuModel.getPrice()));
		Assert.isTrue(skuCodeList.size() == skuCodePriceMap.size(), StatusCode.CALC_SKU_SIZE_NEQ.getMsg());

//		//获取折扣、满减活动sku信息
		List<FullReduceVO> fullReduceVOS = null;
		List<DisctVO> disctVOS = null;
		BigDecimal totalFavor = new BigDecimal(0);

		List<SkuCodeReq> skuCodeReqs = promoSkuCodeReqs(calcSkuModelList, skuCodePriceMap);
		List<PromoSkuCalcResult> promoSkuCalcList = promoConvertManager.calcCampaignPromoAmount(corpId, appId, uid, skuCodeReqs);
		if (!promoSkuCalcList.isEmpty()) {
			for (PromoSkuCalcResult promoSkuCalcRst : promoSkuCalcList) {

				Integer favor = promoSkuCalcRst.getPromotionPrice();
				totalFavor = totalFavor.add(new BigDecimal(favor));
				int status = promoSkuCalcRst.getStatus();
				if (status != Constant.ACTIVE_STATUS_2){
					continue;
				}

				int campaignType = promoSkuCalcRst.getCampaignType();
				if (campaignType == PromoConvertManager.PROMO_TYPE_DISC) {

					String tips = promoSkuCalcRst.getName();
					Map<String, Integer> prompSkuCodePriceMap = promoSkuCalcRst.getPromotionSkuCodes();
					convertDiscountPrice(skuCodePriceMap,prompSkuCodePriceMap);

					if (disctVOS == null) {
						disctVOS = new ArrayList<>();
					}

					DisctVO vo = new DisctVO();
					vo.setFavor(favor);
					vo.setSkuCodes(prompSkuCodePriceMap);
					vo.setTips(tips);
					disctVOS.add(vo);

				} else { //默认满减活动

					int setId = promoSkuCalcRst.getProductSetId();
					int subType = promoSkuCalcRst.getSubType();
					String tips = promoConvertManager.fillPromoTips(subType, 1);
					List<String> skuCodes = promoSkuCalcRst.getSkuCodeList();

					if (fullReduceVOS == null) {
						fullReduceVOS = new ArrayList<>();
					}
					FullReduceVO vo = new FullReduceVO();
					vo.setSubType(subType);
					vo.setFavor(favor);
					vo.setSetId(setId);
					vo.setSkuCodes(skuCodes);
					vo.setTips(tips);
					fullReduceVOS.add(vo);
				}
			}

		}

		BigDecimal totalAmount = new BigDecimal(0);
		Iterator<Map.Entry<String, Integer>> skuCodePriceItr = skuCodePriceMap.entrySet().iterator();
		while (skuCodePriceItr.hasNext()) {
			Map.Entry<String, Integer> skuCodePriceEntry = skuCodePriceItr.next();
			String skuCode = skuCodePriceEntry.getKey();
			Integer price = skuCodePriceEntry.getValue();
			int multi = skucodeCountMap.get(skuCode) * price;
			totalAmount = totalAmount.add(new BigDecimal(multi));
		}

		calcWrap = new CartCalcWrap();
		calcWrap.setTotalAmount(totalAmount.intValue());
		calcWrap.setTotalFavor(totalFavor.intValue());
		if (fullReduceVOS != null && !fullReduceVOS.isEmpty()) {
			calcWrap.setFullReduce(fullReduceVOS);
		}
		if (disctVOS != null && !disctVOS.isEmpty()) {
			calcWrap.setDiscount(disctVOS);
		}
		return calcWrap;
	}

	public CartCalcWrap calcV3(CalcModel calcModel) {

		CartCalcWrap calcWrap = null;

		int uid = calcModel.getUid();
		int appId = calcModel.getAppId();
		int corpId = calcModel.getCorpId();
		List<CalcSkuModel> calcSkuModelList = calcModel.getCalcSku();
		if (calcSkuModelList == null || calcSkuModelList.isEmpty()) {
			throw new BusinessException(StatusCode.SKU_NULL.getCode(), StatusCode.SKU_NULL.getMsg());
		}
		//结算skuCodeList
		List<String> skuCodeList = new ArrayList<>(calcSkuModelList.size());
		//skuCode->count
		Map<String, Integer> skucodeCountMap = new HashMap<>(calcSkuModelList.size());
		Iterator<CalcSkuModel> calcSkuItr = calcSkuModelList.iterator();
		while (calcSkuItr.hasNext()) {
			CalcSkuModel calcSku = calcSkuItr.next();
			skuCodeList.add(calcSku.getSkuCode());

			skucodeCountMap.put(calcSku.getSkuCode(), calcSku.getCount());
		}
		//结算sku数据
		List<SkuModel> skusData = productConvertManager.getSkuByCodes(appId, corpId, skuCodeList);
		if (skusData == null || skusData.isEmpty()) {
			throw new BusinessException(StatusCode.SKU_NO_FOUND.getCode(), StatusCode.SKU_NO_FOUND.getMsg());
		}
		Map<String, Integer> skuCodePriceMap = new HashMap<>(skusData.size());
		skusData.stream().forEach(skuModel -> skuCodePriceMap.put(skuModel.getSkuCode(), skuModel.getPrice()));
		Assert.isTrue(skuCodeList.size() == skuCodePriceMap.size(), StatusCode.CALC_SKU_SIZE_NEQ.getMsg());

//		//获取折扣、满减活动sku信息
		List<FullReduceVO> fullReduceVOS = null;
		List<DisctVO> disctVOS = null;
		BigDecimal totalFavor = new BigDecimal(0);

		List<SkuCodeReq> skuCodeReqs = promoSkuCodeReqs(calcSkuModelList, skuCodePriceMap);
		List<PromoSkuCalcResult> promoSkuCalcList = promoConvertManager.calcCampaignPromoAmount(corpId, appId, uid, skuCodeReqs);
		if (!promoSkuCalcList.isEmpty()) {
			for (PromoSkuCalcResult promoSkuCalcRst : promoSkuCalcList) {

				Integer favor = promoSkuCalcRst.getPromotionPrice();
				totalFavor = totalFavor.add(new BigDecimal(favor));

				if (PromoConvertManager.PROMO_FULL_CODE.equals(promoSkuCalcRst.getTypeCode())) {
					List<String> skuCodes = promoSkuCalcRst.getSkuCodeList();

					if (fullReduceVOS == null) {
						fullReduceVOS = new ArrayList<>();
					}
					FullReduceVO vo = new FullReduceVO();
					vo.setFavor(favor);
					vo.setSkuCodes(skuCodes);
					vo.setTips(promoSkuCalcRst.getTips());
					vo.setId(promoSkuCalcRst.getId());
					vo.setSimpleName(promoSkuCalcRst.getSimpleName());
					vo.setSetIds(promoSkuCalcRst.getProductSetIds());
					vo.setStatus(promoSkuCalcRst.getStatus());
					vo.setIsShowBillBtn(promoSkuCalcRst.getIsShowBillBtn());
					vo.setCampaignType(promoSkuCalcRst.getCampaignType());
					fullReduceVOS.add(vo);
				} else {
					String tips = promoSkuCalcRst.getName();
					Map<String, Integer> prompSkuCodePriceMap = promoSkuCalcRst.getPromotionSkuCodes();
					convertDiscountPrice(skuCodePriceMap,prompSkuCodePriceMap);

					if (disctVOS == null) {
						disctVOS = new ArrayList<>();
					}

					DisctVO vo = new DisctVO();
					vo.setFavor(favor);
					vo.setSkuCodes(prompSkuCodePriceMap);
					vo.setTips(tips);
					disctVOS.add(vo);
				}
			}

		}

		BigDecimal totalAmount = new BigDecimal(0);
		Iterator<Map.Entry<String, Integer>> skuCodePriceItr = skuCodePriceMap.entrySet().iterator();
		while (skuCodePriceItr.hasNext()) {
			Map.Entry<String, Integer> skuCodePriceEntry = skuCodePriceItr.next();
			String skuCode = skuCodePriceEntry.getKey();
			Integer price = skuCodePriceEntry.getValue();
			int multi = skucodeCountMap.get(skuCode) * price;
			totalAmount = totalAmount.add(new BigDecimal(multi));
		}

		calcWrap = new CartCalcWrap();
		calcWrap.setTotalAmount(totalAmount.intValue());
		calcWrap.setTotalFavor(totalFavor.intValue());
		if (fullReduceVOS != null && !fullReduceVOS.isEmpty()) {
			calcWrap.setFullReduce(fullReduceVOS);
		}
		if (disctVOS != null && !disctVOS.isEmpty()) {
			calcWrap.setDiscount(disctVOS);
		}
		return calcWrap;
	}

	/**
	 * sku优惠价格转换
	 * @param skuCodePriceMap
	 * @param prompSkuCodePriceMap
	 */
	private void convertDiscountPrice(Map<String, Integer> skuCodePriceMap, Map<String, Integer> prompSkuCodePriceMap) {
		Iterator<Map.Entry<String, Integer>> itr = prompSkuCodePriceMap.entrySet().iterator();
		while (itr.hasNext()){
			Map.Entry<String, Integer> map = itr.next();
			if (skuCodePriceMap.containsKey(map.getKey())){
				Integer price = skuCodePriceMap.get(map.getKey());
				if (price >= map.getValue()){
					map.setValue(price - map.getValue());
				}
			}
		}
	}


	/**
	 * 获取活动接口参数
	 *
	 * @param calcSkuModelList
	 * @param skuCodePriceMap
	 * @return
	 */
	private List<SkuCodeReq> promoSkuCodeReqs(List<CalcSkuModel> calcSkuModelList, Map<String, Integer> skuCodePriceMap) {
		List<SkuCodeReq> skuCodeReqs = new ArrayList<>(calcSkuModelList.size());
		Iterator<CalcSkuModel> calcSkuItr = calcSkuModelList.iterator();
		while (calcSkuItr.hasNext()) {
			CalcSkuModel calcSku = calcSkuItr.next();
			Assert.isTrue(skuCodePriceMap.containsKey(calcSku.getSkuCode()), StatusCode.CALC_SKU_NO_EXIST.getMsg());
			SkuCodeReq req = new SkuCodeReq(calcSku.getSkuCode(), calcSku.getProductId(), calcSku.getCount(), skuCodePriceMap.get(calcSku.getSkuCode()));
			skuCodeReqs.add(req);
		}
		return skuCodeReqs;
	}
}
