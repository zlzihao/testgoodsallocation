package cn.nome.saas.allocation.service.basic;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.model.allocation.Area;
import cn.nome.saas.allocation.model.allocation.Shop;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.model.issue.DwsDimShopData;
import cn.nome.saas.allocation.model.issue.ShopDisplayDesignData;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimShopDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenRuleDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ShopInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import cn.nome.saas.allocation.utils.IssueDayUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.nome.saas.allocation.constant.Constant.ATTR_VALUE_SPLIT;

/**
 * ShopService
 *
 * @author Bruce01.fan
 * @date 2019/7/1
 */
@Service
public class ShopService {

    private static Logger logger = LoggerFactory.getLogger(ShopService.class);

    private static final String SHOP_INFO_ATTR_SPLIT_SYMBOL = ",,";

    @Autowired
    GlobalConfigRuleService globalConfigRuleService;

    @Autowired
    ShopListCache shopListCache;
    @Autowired
    DwsDimShopDOMapper dwsDimShopDOMapper;
    @Autowired
    DwsShopDOMapper dwsShopDOMapper;

    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;

    @Autowired
    ShopInfoCache shopInfoCache;

    @Autowired
    ForbiddenRuleDOMapper forbiddenRuleDOMapper;

    public String getShopIds(String codes) {
        List<String> shopCodeList = Stream.of(codes.split(",")).collect(Collectors.toList());
        List<DwsDimShopDO> shopList = shopListCache.getShopList();

        return shopList.stream().filter(shop->shopCodeList.contains(shop.getShopCode())).map(DwsDimShopDO::getShopId).collect(Collectors.joining(","));
    }

    public List<ShopInfoData>  getShopInfoData(){
        List<ShopInfoData> l = shopInfoDOMapper.shopInfoData();
        return  l;
    }


    public List<DwsDimShopDO> getAllShop() {

        return  shopListCache.getShopList();
    }

    public List<Shop> getShopList() {

        List<DwsDimShopDO> dwsDimShopDOList = shopListCache.getShopList();
        List<ShopInfoData> shopInfoDataList = shopInfoCache.getShopList();

        List<Shop> shopList = dwsDimShopDOList.stream().map(dwsDimShopDO -> {
            Shop shop = new Shop();
            BeanUtils.copyProperties(dwsDimShopDO,shop);
            return shop;
        }).collect(Collectors.toList());

        List<Shop> newShopList = new ArrayList<>();
        for (Shop shop : shopList) {
            for (ShopInfoData shopInfoData : shopInfoDataList) {
                // 正常营业+计划撤店
                if (shop.getShopId().equals(shopInfoData.getShopID())
                        && (shopInfoData.getStatus().equals(1) || shopInfoData.getStatus().equals(4) ) ) {
                    newShopList.add(shop);
                }
            }
        }

        return newShopList;
    }

    public List<Area>  getAreaList() {

        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        List<Area> provinceList = shopDOList.stream().map(shop->{
            Area province = new Area();
            province.setAreaCode(shop.getProvinceCode().trim());
            province.setAreaName(shop.getProvinceName());

            return province;
        }).distinct().collect(Collectors.toList());

        provinceList = provinceList.stream().filter(distinctByKey(Area::getAreaCode)).collect(Collectors.toList());

        Map<String,List<Area>> cityMap = shopDOList.stream().collect(Collectors.groupingBy(DwsDimShopDO::getProvinceCode,Collectors.mapping(shop->{
            Area city = new Area();
            city.setAreaCode(shop.getCityCode().trim());
            city.setAreaName(shop.getCityName());
            return city;
        },Collectors.toList())));

        provinceList.forEach(province->{

            if (cityMap.containsKey(province.getAreaCode())) {
                province.setSubAreaList(cityMap.get(province.getAreaCode()).stream().filter(distinctByKey(Area::getAreaCode)).collect(Collectors.toList()));
            }

        });

        return provinceList;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public List<String> getShopIdByAreaCode(String areaCode) {
        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        return shopDOList.stream()
                .filter(shop->shop.getProvinceCode().trim().equals(areaCode) || shop.getCityCode().trim().equals(areaCode))
                .map(DwsDimShopDO::getShopId)
                .collect(Collectors.toList());
    }

    public List<String> getShopIdByName(String shopName) {
        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        return shopDOList.stream().filter(shop->shop.getShopName().contains(shopName)).map(DwsDimShopDO::getShopId).collect(Collectors.toList());
    }

    public String getShopCodeById(String shopId) {
        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        return shopDOList.stream().filter(shop->shop.getShopId().equals(shopId)).map(DwsDimShopDO::getShopCode).findFirst().orElse(null);
    }

    public int syncDataCenterData() {
        List<cn.nome.saas.allocation.repository.entity.vertical.DwsDimShopDO> dwsDimShopDOS = dwsShopDOMapper.selectAllShopList();

        logger.info("[SYNC_DATA_CENTER_DATA]|get data center data size:{0}", dwsDimShopDOS.size());
        if (dwsDimShopDOS.size() > 0) {
            dwsDimShopDOMapper.clearAll();
            dwsDimShopDOMapper.insertBatchDataCenterData(dwsDimShopDOS);
        } else {
            logger.error("[SYNC_DATA_CENTER_DATA]|get data center data is empty");
        }
        return 1;
    }

    public List<String> getRegioneBusNameList() {
        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        return shopDOList.stream().map(DwsDimShopDO::getRegionBusName).collect(Collectors.toList());

    }

    public List<String> getSubRegioneBusNameList(String regioneBusName) {
        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        return shopDOList.stream().filter(shop->shop.getRegionBusName().equals(regioneBusName)).map(DwsDimShopDO::getSubRegionBusName).collect(Collectors.toList());
    }


    public List<Area> getCityList(String subRegioneBusName) {
        List<DwsDimShopDO>  shopDOList = shopListCache.getShopList();

        if (StringUtils.isBlank(subRegioneBusName)) {
            return shopDOList.stream()
                    .map(shop->{
                        Area city = new Area();
                        city.setAreaCode(shop.getCityCode().trim());
                        city.setAreaName(shop.getCityName());
                        return city;
                    }).collect(Collectors.toList());
        } else {
            return shopDOList.stream().filter(shop->shop.getSubRegionBusName().equals(subRegioneBusName))
                    .map(shop->{
                        Area city = new Area();
                        city.setAreaCode(shop.getCityCode().trim());
                        city.setAreaName(shop.getCityName());
                        return city;
                    }).collect(Collectors.toList());
        }
    }


    public ShopInfoData getShopInfoById(String shopId) {
        List<ShopInfoData> list = shopInfoCache.getShopList();
        return list.stream().filter(shopInfoData -> shopInfoData.getShopID().equals(shopId)).findFirst().orElse(null);
    }

    public Set getShopInfoUserName(String userName) {

        List<ShopInfoData> list = shopInfoDOMapper.getShopListOfUser(userName);

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.stream().map(ShopInfoData::getShopCode).collect(Collectors.toSet());
    }

    public String[] getShopInfoAttrKeys() {
        List<ShopInfoData> list = shopInfoCache.getShopList();
        if (StringUtils.isEmpty(list.get(0).getAttrKeys())) {
            return new String[]{"自定义属性一", "自定义属性二", "自定义属性三", "自定义属性四", "自定义属性五"};
        }
        return list.get(0).getAttrKeys().split(SHOP_INFO_ATTR_SPLIT_SYMBOL);
    }

    public void updateShopInfoAttrKeys(String attrKey1, String attrKey2, String attrKey3, String attrKey4, String attrKey5) {
        String attrKeys = attrKey1 + SHOP_INFO_ATTR_SPLIT_SYMBOL + attrKey2 + SHOP_INFO_ATTR_SPLIT_SYMBOL + attrKey3 + SHOP_INFO_ATTR_SPLIT_SYMBOL +
                attrKey4 + SHOP_INFO_ATTR_SPLIT_SYMBOL + attrKey5;

        if (shopInfoDOMapper.updateAttrKeys(attrKeys) <= 0) {
            throw new BusinessException("12000", "更新失败");
        }
    }

    public void shopInfoAdd(String shopCode, String goodsArea, String userName, String issueTime,
//                            String issuePeriod, String displayLevel,
                            String shopLevel, Integer maxDays, Integer haveChild, String commodityLevel, String womenLevel, String menLevel,
                            Integer roadDay,
                            Integer status,
                            String commoditySpace, String clothSpace, String sheetHeadSpaceNum, Integer cosmeticsTable, Integer stationeryTable,
//                            Integer singleDisplayChild,
                            String attrFirVal, String attrSecVal, String attrThiVal, String attrFourVal, String attrFifVal,
                            Integer safeDay) {

        ShopInfoData shopInfoData = new ShopInfoData();
        shopInfoData.setShopCode(shopCode);
        List<DwsDimShopData> dwsDimShops = dwsDimShopDOMapper.getShopIdByCode(Collections.singletonList(shopCode));
        if (dwsDimShops.size() < 1) {
            throw new BusinessException("12000", "门店代码输入有误");
        }
        Map<String,Object> param = new HashMap<>(1);
        param.put("shopCode", shopCode);
        if (shopInfoDOMapper.selectByPage(param).size() > 0) {
            throw new BusinessException("12000", "已存在此门店代码");
        }
        shopInfoData.setShopID(dwsDimShops.get(0).getShopID());
        shopInfoData.setGoodsArea(goodsArea);
        shopInfoData.setUserName(userName);
        shopInfoData.setIssueTime(issueTime);
        shopInfoData.setShopLevel(shopLevel);
        shopInfoData.setMaxDays(maxDays);
        shopInfoData.setHaveChild(haveChild);
        shopInfoData.setCommodityLevel(commodityLevel);
        shopInfoData.setDisplayLevel(commodityLevel); // 这里displayLevel取commodityLevel的值
        shopInfoData.setWomenLevel(womenLevel);
        shopInfoData.setMenLevel(menLevel);
        shopInfoData.setRoadDay(roadDay);
        //shopInfoData.setIssueDay(IssueDayUtil.getIssueDay(shopInfoData.getIssueDay(), shopInfoData.getIssueTime(), 0));
        shopInfoData.setIssueDay(IssueDayUtil.getIssueDayV2(DateUtil.getCurrentDate(),shopInfoData.getRoadDay(), shopInfoData.getIssueTime()));
        shopInfoData.setStatus(status);
        shopInfoData.setCommoditySpace(new BigDecimal(commoditySpace));
        shopInfoData.setClothSpace(new BigDecimal(clothSpace));
        shopInfoData.setSheetHeadSpaceNum(new BigDecimal(sheetHeadSpaceNum));
        shopInfoData.setCosmeticsTable(cosmeticsTable);
        shopInfoData.setStationeryTable(stationeryTable);
        shopInfoData.setAttrFirVal(attrFirVal);
        shopInfoData.setAttrSecVal(attrSecVal);
        shopInfoData.setAttrThiVal(attrThiVal);
        shopInfoData.setAttrFourVal(attrFourVal);
        shopInfoData.setAttrFifVal(attrFifVal);
        // 安全天数
        shopInfoData.setSafeDay(safeDay);

        if (shopInfoDOMapper.batchInsertTab(Collections.singletonList(shopInfoData)) <= 0) {
            throw new BusinessException("12000", "新增失败");
        }

        // add shop task
        shopInfoDOMapper.insertShopTask(dwsDimShops.get(0).getShopID());
    }

    public void shopInfoUpdate(Integer id, String shopCode, String goodsArea, String userName, String issueTime,
//                            String issuePeriod, String displayLevel,
                            String shopLevel, Integer maxDays, Integer haveChild, String commodityLevel, String womenLevel, String menLevel,
                            Integer roadDay,
                            Integer status,
                            String commoditySpace, String clothSpace, String sheetHeadSpaceNum, Integer cosmeticsTable, Integer stationeryTable,
//                               Integer singleDisplayChild,
                            String attrFirVal, String attrSecVal, String attrThiVal, String attrFourVal, String attrFifVal,
                               Integer safeDay) {

        attrFirVal = attrFirVal == null ? "" : attrFirVal;
        attrSecVal = attrSecVal == null ? "" : attrSecVal;
        attrThiVal = attrThiVal == null ? "" : attrThiVal;
        attrFourVal = attrFourVal == null ? "" : attrFourVal;
        attrFifVal = attrFifVal == null ? "" : attrFifVal;


        ShopInfoData shopInDataDB = shopInfoDOMapper.selectById(id);

        ShopInfoData shopInfoData = new ShopInfoData();
        shopInfoData.setID(id);
        shopInfoData.setShopCode(shopCode);
        List<DwsDimShopData> dwsDimShops = dwsDimShopDOMapper.getShopIdByCode(Collections.singletonList(shopCode));
        if (dwsDimShops.size() < 1) {
            throw new BusinessException("12000", "门店代码输入有误");
        }
        shopInfoData.setShopID(dwsDimShops.get(0).getShopID());
        shopInfoData.setGoodsArea(goodsArea);
        String userNameFirst, userIdSec;
        if (userName.contains("：")) {
            String[] userInfo = userName.split("：");
            if (userInfo.length < 2) {
                throw new BusinessException("12000", "请输入名称跟工号");
            }
            userNameFirst = userInfo[0];
            userIdSec = userInfo[1];
        } else if (userName.contains(":")) {
            String[] userInfo = userName.split(":");
            if (userInfo.length < 2) {
                throw new BusinessException("12000", "请输入名称跟工号");
            }
            userNameFirst = userInfo[0];
            userIdSec = userInfo[1];
        } else {
            throw new BusinessException("12000", "修改名称需要包含冒号[：]");
        }
        shopInfoData.setUserName(userNameFirst);
        shopInfoData.setUserId(userIdSec);
        shopInfoData.setIssueTime(issueTime);
        shopInfoData.setShopLevel(shopLevel);
        shopInfoData.setMaxDays(maxDays);
        shopInfoData.setHaveChild(haveChild);
        shopInfoData.setCommodityLevel(commodityLevel);
        shopInfoData.setDisplayLevel(commodityLevel); // 这里displayLevel取commodityLevel的值
        shopInfoData.setWomenLevel(womenLevel);
        shopInfoData.setMenLevel(menLevel);
        shopInfoData.setRoadDay(roadDay);
        //shopInfoData.setIssueDay(IssueDayUtil.getIssueDay(shopInfoData.getRoadDay(), shopInfoData.getIssueTime(), 0));
        shopInfoData.setIssueDay(IssueDayUtil.getIssueDayV2(DateUtil.getCurrentDate(),shopInfoData.getRoadDay(), shopInfoData.getIssueTime()));
        shopInfoData.setStatus(status);
        shopInfoData.setCommoditySpace(new BigDecimal(commoditySpace));
        shopInfoData.setClothSpace(new BigDecimal(clothSpace));
        shopInfoData.setSheetHeadSpaceNum(new BigDecimal(sheetHeadSpaceNum));
        shopInfoData.setCosmeticsTable(cosmeticsTable);
        shopInfoData.setStationeryTable(stationeryTable);
//        shopInfoData.setSingleDisplayChild(singleDisplayChild);
        shopInfoData.setAttrFirVal(attrFirVal);
        shopInfoData.setAttrSecVal(attrSecVal);
        shopInfoData.setAttrThiVal(attrThiVal);
        shopInfoData.setAttrFourVal(attrFourVal);
        shopInfoData.setAttrFifVal(attrFifVal);
        shopInfoData.setSafeDay(safeDay);

        boolean addShopTaskFlag = false;
        if (!goodsArea.equals(shopInDataDB.getGoodsArea()) ||
                !shopLevel.equals(shopInDataDB.getShopLevel()) ||
                !status.equals(shopInDataDB.getStatus())
                || !attrFirVal.equals(shopInDataDB.getAttrFirVal())
                || !attrSecVal.equals(shopInDataDB.getAttrSecVal())
                || !attrThiVal.equals(shopInDataDB.getAttrThiVal())
                || !attrFourVal.equals(shopInDataDB.getAttrFourVal())
                || !attrFifVal.equals(shopInDataDB.getAttrFifVal())) {
            addShopTaskFlag = true;
        }

        if (shopInfoDOMapper.updateById(shopInfoData) <= 0) {
            throw new BusinessException("12000", "更新失败");
        }

        if (addShopTaskFlag && shopInfoDOMapper.checkShopTask(shopInfoData.getShopID()) == 0) {
            shopInfoDOMapper.insertShopTask(dwsDimShops.get(0).getShopID());
        }

    }

    public List<String> getGoodsAreaList() {
        return shopInfoDOMapper.getGoodsAreaList();
    }

    public List<String> getShopLvList() {
        return shopInfoDOMapper.getShopLvList();
    }

    public SelectByPageResult selectByParam(String goodsAreas, String shopLvs, String shopNames, String shopCode, int page, int pageSize) {
        Map<String,Object> param = new HashMap<>(16);
        if (!StringUtils.isEmpty(goodsAreas)) {
            param.put("goodsAreas", goodsAreas.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(shopLvs)) {
            param.put("shopLvs", shopLvs.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(shopNames)) {
            param.put("shopIds", Arrays.stream(shopNames.split(ATTR_VALUE_SPLIT)).map(globalConfigRuleService.getShopNameMap()::get).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(shopCode)) {
            param.put("shopCode", shopCode);
        }
        param.put("offset",(page - 1) * pageSize);
        param.put("pageSize",pageSize);

        int total = shopInfoDOMapper.getCount(param);
        List<ShopInfoData> list = shopInfoDOMapper.selectByPage(param);

        // 2021-4-26 缓存移出循环
        List<DwsDimShopDO> shopDOList = shopListCache.getShopList();
        Map<String, String> shopMap = shopDOList.stream().collect(Collectors.toMap(cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopId, cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopName));
        //获取店铺名称
        for (ShopInfoData s : list) {
//            List<cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO> dwsDimShopDOS = forbiddenRuleDOMapper.getShopCodeList();
            s.setShopName(shopMap.get(s.getShopID()));
            s.setUserName(s.getUserName() + "：" + s.getUserId());
        }

        SelectByPageResult<ShopInfoData> result = new SelectByPageResult<>();
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
