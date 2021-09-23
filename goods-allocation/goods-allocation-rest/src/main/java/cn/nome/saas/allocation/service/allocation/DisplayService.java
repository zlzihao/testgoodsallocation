package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.model.issue.MinDisplaySkcData;
import cn.nome.saas.allocation.model.issue.ShopDisplayDesignData;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.entity.allocation.DisplayDo;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.ATTR_VALUE_SPLIT;

/**
 * ForbiddenRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class DisplayService {

    private static Logger logger = LoggerFactory.getLogger(DisplayService.class);

    private static final BigDecimal ZERO = new BigDecimal(0.00);
    private static final BigDecimal HALF = new BigDecimal(0.50);
    private static final BigDecimal HALF_HALF = new BigDecimal(0.25);
    private static final BigDecimal ONE = new BigDecimal(1.00);

    @Autowired
    DisplayDOMapper displayDOMapper;
    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;
    @Autowired
    ShopDisplayDesignDOMapper shopDisplayDesignDOMapper;
    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;
    @Autowired
    MinDisplaySkcDOMapper minDisplaySkcDOMapper;

    @Autowired
    GlobalConfigRuleService globalConfigRuleService;

    public Set<String> getLargeCategoryList() {
        return displayDOMapper.getLargeCategoryList();
    }

    public Set<String> getMidCategoryList(String largeCategory) {
        Set<String> set = displayDOMapper.getMidCategoryList(StringUtils.isEmpty(largeCategory) ? null : largeCategory.split(Constant.ATTR_VALUE_SPLIT));
        set.remove("");
        return set;
    }

    public Set<String> getSmallCategoryList(String midCategory) {
        Set<String> set = displayDOMapper.getSmallCategoryList(StringUtils.isEmpty(midCategory) ? null : midCategory.split(Constant.ATTR_VALUE_SPLIT));
        set.remove("");
        return set;
    }

    public SelectByPageResult selectByParam(String largeCategory, String midCategory, String smallCategory, int page, int pageSize) {
        Map<String, Object> param = new HashMap<>(16);
        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategory)) {
            param.put("smallCategory", smallCategory.split(ATTR_VALUE_SPLIT));
        }
        param.put("offset", (page - 1) * pageSize);
        param.put("pageSize", pageSize);

        int total = displayDOMapper.getCount(param);
        List<DisplayDo> list = displayDOMapper.selectByPage(param);


        SelectByPageResult<DisplayDo> result = new SelectByPageResult<>();
        result.setTotal(total);
        result.setList(list);
        int totalPage;
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = (total / pageSize) + 1;
        }
        result.setTotalPage(totalPage);
        return result;
    }

    public List<String> getGoodsLargeCategoryList() {
        return goodsInfoDOMapper.getGoodsLargeCategoryList();
    }

    public List<String> getGoodsMidCategoryList(String largeCategory) {
        return goodsInfoDOMapper.getGoodsMidCategoryList(StringUtils.isEmpty(largeCategory) ? null : largeCategory.split(Constant.ATTR_VALUE_SPLIT));
    }

    public List<String> getGoodsSmallCategoryList(String midCategory) {
        return goodsInfoDOMapper.getGoodsSmallCategoryList(StringUtils.isEmpty(midCategory) ? null : midCategory.split(Constant.ATTR_VALUE_SPLIT));
    }

//    public List<String> getGoodsSmallCategoryList() {
//        return goodsInfoDOMapper.getGoodsSmallCategoryList();
//    }


    public List<String> getShopDisplayLargeCategoryList() {
        return shopDisplayDesignDOMapper.getLargeCategoryList();
    }

    public List<String> getShopDisplayMidCategoryList(String largeCategory) {
        return shopDisplayDesignDOMapper.getMidCategoryList(StringUtils.isEmpty(largeCategory) ? null : largeCategory.split(Constant.ATTR_VALUE_SPLIT));
    }

    public SelectByPageResult shopSelectByParam(String largeCategory, String midCategory, String shopNames, int page, int pageSize) {
        Map<String, Object> param = new HashMap<>(16);
        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(shopNames)) {
            param.put("shopIds", Arrays.stream(shopNames.split(ATTR_VALUE_SPLIT)).map(globalConfigRuleService.getShopNameMap()::get).collect(Collectors.toList()));
        }
        param.put("offset", (page - 1) * pageSize);
        param.put("pageSize", pageSize);

        int total = shopDisplayDesignDOMapper.getCount(param);
        List<ShopDisplayDesignData> list = shopDisplayDesignDOMapper.selectByPage(param);

        //获取店铺名称
        for (ShopDisplayDesignData s : list) {
            s.setShopName(globalConfigRuleService.getShopIdMap().get(s.getShopId()));
        }

        SelectByPageResult<ShopDisplayDesignData> result = new SelectByPageResult<>();
        result.setTotal(total);
        result.setList(list);
        int totalPage;
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = (total / pageSize) + 1;
        }
        result.setTotalPage(totalPage);
        return result;
    }

    public void checkShopSheetSpace() {
        String userId = AuthUtil.getSessionUserId();
        List<ShopDisplayDesignData> list = shopDisplayDesignDOMapper.getShopDisplayDesignListByUserId(userId);

        String msg = checkShopSheetSpace(list);

        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

    }

    public String checkShopSheetSpace(List<ShopDisplayDesignData> list) {
        StringBuilder msg = new StringBuilder();
/*
        Integer rowNum;
        for (ShopDisplayDesignData s : list) {
            rowNum = s.getRowNum();
            if (s.getMidCategoryName().startsWith("BTI")) {
                if (ZERO.compareTo(s.getDisplay_Qty()) != 0 && HALF.compareTo(s.getDisplay_Qty()) != 0 && ONE.compareTo(s.getDisplay_Qty()) != 0) {
                    if (rowNum == null) {
                        msg.append(s.getShopCode()).append("：BTI中类仓位数不是0或0.5或1\r\n");
                    } else {
                        msg.append("第").append(rowNum).append("行：BTI中类仓位数不是0或0.5或1\r\n");
                    }

                }
            } else if ("彩妆".equals(s.getMidCategoryName())) {
                if (ZERO.compareTo(s.getDisplay_Qty()) != 0 && ONE.compareTo(s.getDisplay_Qty()) != 0) {
                    if (rowNum == null) {
                        msg.append(s.getShopCode()).append("：“彩妆”中类仓位数不是0或者1\r\n");
                    } else {
                        msg.append("第").append(rowNum).append("行：“彩妆”中类仓位数不是0或者1\r\n");
                    }

                }
            } else if ("笔-文具".equals(s.getMidCategoryName())) {
                //只针对有文具台的门店
                Map<String,Object> param = new HashMap<>(1);
                param.put("shopCode", s.getShopCode());
                List<ShopInfoData> list1 = shopInfoDOMapper.selectByPage(param);
                if (list1.size() > 0 && list1.get(0).getStationeryTable() != null && 1 == list1.get(0).getStationeryTable() && ZERO.compareTo(s.getDisplay_Qty()) != 0 && ONE.compareTo(s.getDisplay_Qty()) != 0) {
                    if (rowNum == null) {
                        msg.append(s.getShopCode()).append("：“笔-文具”中类仓位数不是0或者1\r\n");
                    } else {
                        msg.append("第").append(rowNum).append("行：“笔-文具”中类仓位数不是0或者1\r\n");
                    }
                }
            }
            if (!"男装".equals(s.getCategoryName()) && !"女装".equals(s.getCategoryName())) {
                BigDecimal remainder = s.getDisplay_Qty().divideAndRemainder(HALF_HALF)[1];
                if (ZERO.compareTo(remainder) != 0) {
                    msg.append("第").append(rowNum).append("行：“百货类”中类仓位数不是0.25的倍数\r\n");
                }
            }
        }*/
        Map<String, BigDecimal> map = new HashMap<>();
        Map<String, BigDecimal> clothingMap = new HashMap<>();
        for (ShopDisplayDesignData s : list) {
            if (!"男装".equals(s.getCategoryName()) && !"女装".equals(s.getCategoryName())) {
                if (map.get(s.getShopCode()) != null) {
                    map.put(s.getShopCode(), map.get(s.getShopCode()).add(s.getDisplay_Qty()));
                } else {
                    map.put(s.getShopCode(), s.getDisplay_Qty());
                }
            } else {
                if (clothingMap.get(s.getShopCode()) != null) {
                    clothingMap.put(s.getShopCode(), clothingMap.get(s.getShopCode()).add(s.getDisplay_Qty()));
                } else {
                    clothingMap.put(s.getShopCode(), s.getDisplay_Qty());
                }
            }
        }

        for (String key : map.keySet()) {
            if (StringUtils.isEmpty(key)) {
                continue;
            }
            Map<String, Object> param = new HashMap<>(1);
            param.put("shopCode", key);
            List<ShopInfoData> list1 = shopInfoDOMapper.selectByPage(param);
            if (list1.size() > 0) {
                if (map.get(key).doubleValue() != list1.get(0).getCommoditySpace().doubleValue()) {
                    msg.append(list1.get(0).getShopCode()).append("：百货总仓位不正确。\r\n");
                }
                if (clothingMap.get(key).doubleValue() != list1.get(0).getClothSpace().doubleValue()) {
                    msg.append(list1.get(0).getShopCode()).append("：服装总仓位不正确。\r\n");
                }
            }
        }
        return msg.toString();
    }

    public SelectByPageResult minDisplayList(int page, int pageSize) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("offset", (page - 1) * pageSize);
        param.put("pageSize", pageSize);

        int total = minDisplaySkcDOMapper.getCount(param);
        List<MinDisplaySkcData> list = minDisplaySkcDOMapper.selectByPage(param);

        for (MinDisplaySkcData s : list) {
            LoggerUtil.debug(logger, "value", s.getTypeValue());
            LoggerUtil.debug(logger, "type", s.getType());

            s.setTypeName(MinDisplaySkcData.SkcType.getTypeName(s.getType()));
        }

        SelectByPageResult<MinDisplaySkcData> result = new SelectByPageResult<>();
        result.setTotal(total);
        result.setList(list);
        int totalPage;
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = (total / pageSize) + 1;
        }
        result.setTotalPage(totalPage);
        return result;
    }


}
