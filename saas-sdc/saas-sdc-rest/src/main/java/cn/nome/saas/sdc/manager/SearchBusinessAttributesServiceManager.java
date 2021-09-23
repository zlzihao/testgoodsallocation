package cn.nome.saas.sdc.manager;

import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.model.req.SearchBusinessAttributesReq;
import cn.nome.saas.sdc.model.vo.SearchBusinessAttributesVO;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import cn.nome.saas.sdc.service.SearchBusinessAttributesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/10/31 17:29
 */
@Component
public class SearchBusinessAttributesServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchBusinessAttributesServiceManager.class);

    private SearchBusinessAttributesService searchBusinessAttributesService;

    @Autowired
    public SearchBusinessAttributesServiceManager(SearchBusinessAttributesService searchBusinessAttributesService) {
        this.searchBusinessAttributesService = searchBusinessAttributesService;
    }

    void fillShopAttribute(List<ShopsVO> shops) {
        List<String> names = new ArrayList<>();
        names.add(Constant.FIXED_ATTRIBUTE_BUSINESS_ATTRIBUTE);
        SearchBusinessAttributesReq req = new SearchBusinessAttributesReq();
        req.setAttributeNames(names);
        List<SearchBusinessAttributesVO> attributes = searchBusinessAttributesService.queryAttributes(req);
        Map<String, List<SearchBusinessAttributesVO>> attributesMap = attributes.stream().collect(Collectors.groupingBy(SearchBusinessAttributesVO::getAttributeName));
        List<SearchBusinessAttributesVO> list = attributesMap.getOrDefault(Constant.FIXED_ATTRIBUTE_BUSINESS_ATTRIBUTE, new ArrayList<>());
        if (list.size() <= 0) {
            return;
        }
        Map<Integer, String> shopAttributeValue = list.stream().collect(Collectors.toMap(SearchBusinessAttributesVO::getBusinessId, SearchBusinessAttributesVO::getAttributeValue));
        shops.forEach(shop -> {
            shop.setShopBusinessAttribute(shopAttributeValue.getOrDefault(shop.getId(), ""));
        });
    }

    void fillAttributeProvinceCity(List<ShopsVO> shops) {

        SearchBusinessAttributesReq req = new SearchBusinessAttributesReq();
        req.setAttributeNames(Arrays.asList(
                Constant.FIXED_ATTRIBUTE_PROVINCE,
                Constant.FIXED_ATTRIBUTE_CITY,
                Constant.FIXED_ATTRIBUTE_DISTINCT,
                Constant.FIXED_ATTRIBUTE_ADDRESS,
                Constant.FIXED_ATTRIBUTE_ENABLE_WECHAT_GROUP_MARKING,
                Constant.FIXED_ATTRIBUTE_LNG,
                Constant.FIXED_ATTRIBUTE_LAT
        ));
        List<SearchBusinessAttributesVO> attributeValues = searchBusinessAttributesService.queryAttributes(req);
        Map<Integer, List<SearchBusinessAttributesVO>> attributeValuesMap = attributeValues.stream().collect(Collectors.groupingBy(SearchBusinessAttributesVO::getBusinessId));
        List<SearchBusinessAttributesVO> shopAttributeValues;
        for (ShopsVO shop : shops) {
            shop.setProvince("");
            shop.setCity("");
            shop.setEnableWechatGroupMarking(false);
            shopAttributeValues = attributeValuesMap.getOrDefault(shop.getId(), new ArrayList<>());

            for (SearchBusinessAttributesVO vo : shopAttributeValues) {
                switch (vo.getAttributeName()) {
                    case Constant.FIXED_ATTRIBUTE_PROVINCE:
                        shop.setProvince(vo.getAttributeValue());
                        break;
                    case Constant.FIXED_ATTRIBUTE_CITY:
                        shop.setCity(vo.getAttributeValue());
                        break;
                    case Constant.FIXED_ATTRIBUTE_DISTINCT:
                        shop.setDistinct(vo.getAttributeValue());
                        break;
                    case Constant.FIXED_ATTRIBUTE_ADDRESS:
                        shop.setAddress(vo.getAttributeValue());
                        break;
                    case Constant.FIXED_ATTRIBUTE_LNG:
                        shop.setLng(vo.getAttributeValue());
                        break;
                    case Constant.FIXED_ATTRIBUTE_LAT:
                        shop.setLat(vo.getAttributeValue());
                        break;
                    case Constant.FIXED_ATTRIBUTE_ENABLE_WECHAT_GROUP_MARKING:
                        LOGGER.info("attribute value = {}", vo.getAttributeValue());
                        if (vo.getAttributeValue().contains("是")) {
                            shop.setEnableWechatGroupMarking(true);
                        }
                        break;
                }
            }
        }
    }

    List<Integer> searchShopAttribute(String attributeName, String attributeValue) {
        List<Integer> ids = new ArrayList<>();
        SearchBusinessAttributesReq req = new SearchBusinessAttributesReq();
        req.setBusinessType(Constant.BUSINESS_TYPE_SHOP);
        req.setAttributeName(attributeName);
        req.setAttributeValue(attributeValue);
        List<SearchBusinessAttributesVO> listVO = searchBusinessAttributesService.searchAttribute(req);
        if (listVO.size() <= 0) {
            return ids;
        }
        for (SearchBusinessAttributesVO vo : listVO) {
            ids.add(vo.getBusinessId());
        }
        return ids;
    }
}
