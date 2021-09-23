package cn.nome.saas.sdc.manager;

import cn.hutool.core.date.DateUtil;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.saas.sdc.bigData.repository.dao.DwsDimShopStatusMapper;
import cn.nome.saas.sdc.bigData.repository.entity.DwsDimShopStatusDO;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.event.AttributeUpdateEvent;
import cn.nome.saas.sdc.model.excel.ShopsEO;
import cn.nome.saas.sdc.model.form.ImportForm;
import cn.nome.saas.sdc.model.form.ShopsForm;
import cn.nome.saas.sdc.model.req.*;
import cn.nome.saas.sdc.model.vo.*;
import cn.nome.saas.sdc.repository.entity.ShopsDO;
import cn.nome.saas.sdc.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Component
public class ShopsServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopsServiceManager.class);

    private final ShopsService shopsService;
    private final SearchBusinessAttributesService searchBusinessAttributesService;
    private final SearchBusinessAttributesServiceManager searchBusinessAttributesServiceManager;
    private final BusinessAttributeValuesServiceManager businessAttributeValuesServiceManager;
    private final AreasService areasService;
    private final AreasServiceManager areasServiceManager;
    private final AttributesService attributesService;
    private final AttributesServiceManager attributesServiceManager;
    private final ShopWechatService shopWechatService;
    private final WarehouseConfigServiceManager warehouseConfigServiceManager;
    private final SeasonChangeService seasonChangeService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ExecutorService shopsSearchThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final DwsDimShopStatusMapper dwsDimShopStatusMapper;

    @Autowired
    public ShopsServiceManager(
            ShopsService shopsService,
            SearchBusinessAttributesService searchBusinessAttributesService,
            SearchBusinessAttributesServiceManager searchBusinessAttributesServiceManager,
            BusinessAttributeValuesServiceManager businessAttributeValuesServiceManager,
            AreasService areasService,
            AreasServiceManager areasServiceManager,
            AttributesService attributesService,
            AttributesServiceManager attributesServiceManager,
            ShopWechatService shopWechatService,
            WarehouseConfigServiceManager warehouseConfigServiceManager,
            SeasonChangeService seasonChangeService,
            DwsDimShopStatusMapper dwsDimShopStatusMapper,
            ApplicationEventPublisher applicationEventPublisher) {
        this.shopsService = shopsService;
        this.searchBusinessAttributesService = searchBusinessAttributesService;
        this.searchBusinessAttributesServiceManager = searchBusinessAttributesServiceManager;
        this.businessAttributeValuesServiceManager = businessAttributeValuesServiceManager;
        this.areasService = areasService;
        this.areasServiceManager = areasServiceManager;
        this.attributesService = attributesService;
        this.attributesServiceManager = attributesServiceManager;
        this.shopWechatService = shopWechatService;
        this.warehouseConfigServiceManager = warehouseConfigServiceManager;
        this.seasonChangeService = seasonChangeService;
        this.dwsDimShopStatusMapper = dwsDimShopStatusMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private Map<String, Integer> getAttributeIds(int corpId, List<String> attributeNames) {
        AttributesReq attributesReq = new AttributesReq();
        attributesReq.setCorpId(corpId);
        attributesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributesVO> attributes = attributesService.search(attributesReq, null);
        return attributes.stream().filter(attribute -> attributeNames.contains(
                attribute.getName())).collect(Collectors.toMap(AttributesVO::getName, AttributesVO::getId, (l, r) -> r));
    }

    private Map<Integer, List<BusinessAttributeValuesVO>> getShopAttributeValues(int corpId, List<Integer> attributeIds) {
        BusinessAttributeValuesReq businessAttributeValuesReq = new BusinessAttributeValuesReq();
        businessAttributeValuesReq.setCorpId(corpId);
        businessAttributeValuesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        businessAttributeValuesReq.setBusinessType(Constant.BUSINESS_TYPE_SHOP);
        businessAttributeValuesReq.setAttributeIds(attributeIds);
        List<BusinessAttributeValuesVO> businessAttributeValuesVOList = businessAttributeValuesServiceManager.query(businessAttributeValuesReq);
        return businessAttributeValuesVOList.stream().collect(Collectors.groupingBy(BusinessAttributeValuesVO::getBusinessId));
    }

    private List<ShopsVO> getValidShops(int corpId) {
        ShopsReq shopsReq = new ShopsReq();
        shopsReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        shopsReq.setCorpId(corpId);
        List<ShopsVO> shops = shopsService.search(shopsReq, null);
        shops.forEach(this::calcShopState);
        return shops;
    }

    public List<IwsShopsVO> toIws(int corpId) {
        AttributesReq attributesReq = new AttributesReq();
        attributesReq.setCorpId(corpId);
        attributesReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AttributesVO> attributes = attributesService.search(attributesReq, null);
        Map<Integer, String> attributesMap = attributes.stream().collect(Collectors.toMap(AttributesVO::getId, AttributesVO::getName));
        List<String> targetAttributeNames = IwsShopsVO.getAttributeNames();
        List<Integer> targetAttributeIds = new ArrayList<>();
        for (AttributesVO vo : attributes) {
            if (targetAttributeNames.contains(vo.getName())) {
                targetAttributeIds.add(vo.getId());
            }
        }

        Map<Integer, List<BusinessAttributeValuesVO>> valuesMap = getShopAttributeValues(corpId, targetAttributeIds);

        List<ShopsVO> allShops = getValidShops(corpId);
        Map<Integer, ShopsVO> allShopsMap = allShops.stream().collect(Collectors.toMap(ShopsVO::getId, Function.identity()));

        List<IwsShopsVO> iwsShopsVOList = new ArrayList<>();
        IwsShopsVO iwsShopsVO;
        String attributeName;
        for (Map.Entry<Integer, List<BusinessAttributeValuesVO>> entry : valuesMap.entrySet()) {
            iwsShopsVO = new IwsShopsVO();
            iwsShopsVO.setShopId(entry.getKey());
            iwsShopsVO.setShopName(allShopsMap.get(entry.getKey()).getShopName());
            iwsShopsVO.setShopCode(allShopsMap.get(entry.getKey()).getShopCode());
            iwsShopsVO.setCashRegisterNumber(0);
            iwsShopsVO.setHasIceCreamMachine(false);
            iwsShopsVO.setOpeningTime("");
            iwsShopsVO.setClosingTime("");
            iwsShopsVO.setOpeningDate(allShopsMap.get(entry.getKey()).getOpeningDate());
            iwsShopsVO.setClosingDate(allShopsMap.get(entry.getKey()).getClosingDate());
            iwsShopsVO.setEndBusinessTime("");
            iwsShopsVO.setStartBusinessTime("");
            iwsShopsVO.setFittingRoomFloorNumber(0);
            for (BusinessAttributeValuesVO vo : entry.getValue()) {
                if (!attributesMap.containsKey(vo.getAttributeId())) {
                    continue;
                }
                if (vo.getAttributeValue().isEmpty()) {
                    continue;
                }
                attributeName = attributesMap.get(vo.getAttributeId());
                switch (attributeName) {
                    case "收银机数量":
                        iwsShopsVO.setCashRegisterNumber(Integer.parseInt(vo.getAttributeValue()));
                        break;
                    case "雪糕机":
                        if (vo.getAttributeValue().contains("有")) {
                            iwsShopsVO.setHasIceCreamMachine(true);
                        }
                        break;
                    case "开门时间":
                        iwsShopsVO.setOpeningTime(vo.getAttributeValue());
                        break;
                    case "关门时间":
                        iwsShopsVO.setClosingTime(vo.getAttributeValue());
                        break;
                    case "营业开始时间":
                        iwsShopsVO.setStartBusinessTime(vo.getAttributeValue());
                        break;
                    case "营业结束时间":
                        iwsShopsVO.setEndBusinessTime(vo.getAttributeValue());
                        break;
                    case "试衣间数量（一层）":
                    case "试衣间数量（二层）":
                        if (Integer.parseInt(vo.getAttributeValue()) > 0) {
                            iwsShopsVO.setFittingRoomFloorNumber(iwsShopsVO.getFittingRoomFloorNumber() + 1);
                        }
                        break;
                }
            }
            iwsShopsVOList.add(iwsShopsVO);
        }
        return iwsShopsVOList;
    }

    public List<IssueShopVO> getIssueShops(int corpId) {

        Map<String, String> provinceToStockCodesMap = warehouseConfigServiceManager.getProvinceStockCodeMap();

        SeasonChangeReq req = new SeasonChangeReq();
        req.setSeasonsAlternateDay(new Date());
        List<SeasonChangeVO> seasonChanges = seasonChangeService.selectByCondition(req);
        Map<String, BigDecimal> seasonChangesMap = seasonChanges.stream().collect(Collectors.toMap(SeasonChangeVO::getShopCode, SeasonChangeVO::getSeasonsAlternateCoefficient));

        List<ShopsVO> shops = getValidShops(corpId);

        Map<String, Integer> attributesMap = getAttributeIds(corpId, IssueShopVO.getAttributeNames());

        Map<Integer, List<BusinessAttributeValuesVO>> businessAttributeValuesMap = getShopAttributeValues(corpId, new ArrayList<>(attributesMap.values()));

        Integer shopLevelAttributeId = attributesMap.getOrDefault("门店等级", 0);
        Integer issueDayAttributeId = attributesMap.getOrDefault("发货日", 0);
        Integer logisticsDaysAttributeId = attributesMap.getOrDefault("物流天数", 0);
        Integer provinceAttributeId = attributesMap.getOrDefault("省", 0);

        List<IssueShopVO> issueShops = new ArrayList<>();
        for (ShopsVO shop : shops) {
            if (!shop.getShopStateName().equals("已开业")) {
                continue;
            }
            if (!shop.getEnableClothingAllocation().equals(Constant.EnableClothingAllocation)) {
                continue;
            }
            List<BusinessAttributeValuesVO> values = businessAttributeValuesMap.get(shop.getId());
            if (CollectionUtils.isEmpty(values)) {
                continue;
            }
            IssueShopVO issueShop = IssueShopVO.initIssueShopVO();
            issueShop.setShopCode(shop.getShopCode());
            issueShop.setCoefficient(seasonChangesMap.getOrDefault(issueShop.getShopCode(), new BigDecimal(1)));
//            issueShop.setCoefficient(new BigDecimal(1));
            issueShop.setShopName(shop.getShopName());
            issueShop.setShopState(shop.getShopStateName());


            values.forEach(value -> {
                if (value.getAttributeId().equals(shopLevelAttributeId)) {
                    issueShop.setShopLevel(value.getAttributeValue());
                } else if (value.getAttributeId().equals(logisticsDaysAttributeId)) {
                    issueShop.setLogisticsDays(Integer.parseInt(value.getAttributeValue()));
                } else if (value.getAttributeId().equals(issueDayAttributeId) && !value.getAttributeValue().isEmpty()) {
                    JSONArray issueDays = JSON.parseArray(value.getAttributeValue());
                    if (issueDays != null) {
                        issueShop.setIssueDays(issueDays.toJavaList(String.class));
                    }
                } else if (value.getAttributeId().equals(provinceAttributeId) && !value.getAttributeValue().isEmpty()) {
                    issueShop.setProvince(value.getAttributeValue());
                    issueShop.setStockCode(provinceToStockCodesMap.getOrDefault(issueShop.getProvince(), ""));
                }
            });

            if (issueShop.getShopLevel().isEmpty() || issueShop.getLogisticsDays().compareTo(0) <= 0 || CollectionUtils.isEmpty(issueShop.getIssueDays())) {
                continue;
            }

            issueShops.add(issueShop);

        }
        return issueShops;
    }

    public List<ShopsVO> getAllShopsBase(int corpId) {
        ShopsReq req = new ShopsReq();
        req.setCorpId(corpId);
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<ShopsVO> shops = shopsService.search(req, null);
        areasServiceManager.fillMarkingAreaName(shops);
        searchBusinessAttributesServiceManager.fillAttributeProvinceCity(shops);
        fillWechatConfig(shops);
        return shops;
    }

    public void fillWechatConfig(List<ShopsVO> shops) {
        List<ShopWechatVO> shopWechatVOList = shopWechatService.search(new ShopWechatReq(), null);
        Map<Integer, ShopWechatVO> shopWechatMap = shopWechatVOList.stream().collect(Collectors.toMap(ShopWechatVO::getShopId, Function.identity()));
        for (ShopsVO shopsVO : shops) {
            shopsVO.setWechatConfigId("");
            shopsVO.setWechatQrCode("");
            ShopWechatVO vo = shopWechatMap.getOrDefault(shopsVO.getId(), null);
            if (vo != null) {
                shopsVO.setWechatConfigId(vo.getConfigId());
                shopsVO.setWechatQrCode(vo.getQrCode());
            }
        }
    }


    public void initShopsChannelArea(int corpId) {
        ShopsReq req = new ShopsReq();
        req.setCorpId(corpId);
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<ShopsVO> shops = shopsService.search(req, null);
        for (ShopsVO vo : shops) {
            if (vo.getChannelAreaId() <= 0) {
                applicationEventPublisher.publishEvent(new AttributeUpdateEvent(this, corpId, vo.getId()));
            }
        }
    }

    public List<ShopsVO> search(ShopsReq req, Page page) throws ExecutionException, InterruptedException {
        Future<List<Integer>> marketingAreaFuture = null;
        if (req.getMarketingAreaId() != null) {
            AreasReq areasReq = new AreasReq();
            areasReq.setParentId(req.getMarketingAreaId());
            marketingAreaFuture = shopsSearchThreadPool.submit(() -> areasServiceManager.getChildrenIds(areasReq));
        }
        Future<List<Integer>> channelAreaFuture = null;
        if (req.getChannelAreaId() != null) {
            AreasReq areasReq = new AreasReq();
            areasReq.setParentId(req.getChannelAreaId());
            channelAreaFuture = shopsSearchThreadPool.submit(() -> areasServiceManager.getChildrenIds(areasReq));
        }

        List<Future<List<Integer>>> businessFutures = new ArrayList<>();
        if (req.getShopBusinessAttribute() != null) {
            Future<List<Integer>> shopAttributeFuture = shopsSearchThreadPool.submit(() -> searchBusinessAttributesServiceManager.searchShopAttribute(Constant.FIXED_ATTRIBUTE_BUSINESS_ATTRIBUTE, req.getShopBusinessAttribute()));
            businessFutures.add(shopAttributeFuture);
        }
        if (req.getProvince() != null) {
            Future<List<Integer>> provinceFuture = shopsSearchThreadPool.submit(() -> searchBusinessAttributesServiceManager.searchShopAttribute(Constant.FIXED_ATTRIBUTE_PROVINCE, req.getProvince()));
            businessFutures.add(provinceFuture);
        }
        if (req.getCity() != null) {
            Future<List<Integer>> cityFuture = shopsSearchThreadPool.submit(() -> searchBusinessAttributesServiceManager.searchShopAttribute(Constant.FIXED_ATTRIBUTE_CITY, req.getCity()));
            businessFutures.add(cityFuture);
        }

        if (marketingAreaFuture != null) {
            List<Integer> marketingAreaIds = marketingAreaFuture.get();
            marketingAreaIds.add(req.getMarketingAreaId());
            req.setMarketingAreaIds(marketingAreaIds);
            req.setMarketingAreaId(null);
        }

        if (channelAreaFuture != null) {
            List<Integer> channelAreaIds = channelAreaFuture.get();
            channelAreaIds.add(req.getChannelAreaId());
            req.setChannelAreaIds(channelAreaIds);
            req.setChannelAreaId(null);
        }

        for (Future<List<Integer>> future : businessFutures) {
            List<Integer> businessIds = future.get();
            if (businessIds.size() <= 0) {
                return new ArrayList<>();
            }
            if (req.getIds() != null) {
                req.getIds().retainAll(businessIds);
            } else {
                req.setIds(businessIds);
            }
        }
        if (req.getIds() != null && req.getIds().size() <= 0) {
            return new ArrayList<>();
        }
        List<ShopsVO> shops = shopsService.search(req, page);
        List<Future<String>> fillFutures = new ArrayList<>();
        Future<String> fillAreaFuture = shopsSearchThreadPool.submit(() -> {
            areasServiceManager.fillMarkingAreaFullName(shops);
            return "SUCCESS";
        });
        fillFutures.add(fillAreaFuture);
        Future<String> fillAttributeFuture = shopsSearchThreadPool.submit(() -> {
            searchBusinessAttributesServiceManager.fillShopAttribute(shops);
            return "SUCCESS";
        });
        fillFutures.add(fillAttributeFuture);
        for (Future<String> future : fillFutures) {
            future.get();
        }
        return shops;
    }

    public List<ShopOptionVO> queryAll(ShopsReq req) {
        return shopsService.queryAll(req);
    }

    public ShopDetailVO getDetail(Integer id, Integer corpId) {
        ShopsReq shopsReq = new ShopsReq();
        shopsReq.setCorpId(corpId);
        shopsReq.setId(id);
        shopsReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        ShopsVO shop = shopsService.queryRow(shopsReq);
        if (shop == null) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "店铺不存在");
        }

        AreasReq areasReq = new AreasReq();
        areasReq.setCorpId(corpId);
        areasReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        HashMap<Integer, String> areasMap = areasService.queryLevelMap(areasReq);
        shop.setMarketingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));

        List<SearchBusinessAttributesVO> attributes = businessAttributeValuesServiceManager.getBusinessAttributes(corpId, id, Constant.BUSINESS_TYPE_SHOP, Constant.SOURCE_TYPE_ID_SHOP);
        SearchBusinessAttributesVO provinceAttribute = attributes.stream().filter(attribute -> attribute.getAttributeName().equals(Constant.FIXED_ATTRIBUTE_PROVINCE)).findAny().orElse(null);
        shop.setChannelAreaName("");
        if (provinceAttribute != null && !provinceAttribute.getAttributeValue().isEmpty()) {
            shop.setChannelAreaName(areasServiceManager.getAreaNameByLocation(Constant.AREA_TYPE_CHANNEL, provinceAttribute.getAttributeValue()));
        }
//        calcShopState(shop);
        ShopDetailVO detail = new ShopDetailVO();
        detail.setShop(shop);
        detail.setAttributes(attributes);
        return detail;
    }

    private void calcShopState(ShopsVO shop) {
        if (shop.getOpeningDate().isEmpty() && !shop.getCalcOpeningDate().isEmpty()) {
            shop.setOpeningDate(shop.getCalcOpeningDate());
        }
        if (shop.getClosingDate().isEmpty() && !shop.getCalcClosingDate().isEmpty()) {
            shop.setClosingDate(shop.getCalcClosingDate());
        }
        if (shop.getShopStateName().equals(Constant.SHOP_STATE_PAUSE)) {
            return;
        }
    }

    public Integer insert(ShopsForm form) {
        ShopsDO record = BaseConvertor.convert(form, ShopsDO.class);
        return shopsService.insertSelective(record);
    }

    public void update(ShopsForm form) {
        ShopsDO record = BaseConvertor.convert(form, ShopsDO.class);
        shopsService.update(record);
    }

    public void syncChannelArea(Integer corpId, Integer shopId) {
        SearchBusinessAttributesReq req = new SearchBusinessAttributesReq();
        req.setBusinessType(Constant.BUSINESS_TYPE_SHOP);
        req.setAttributeName(Constant.FIXED_ATTRIBUTE_PROVINCE);
        req.setBusinessId(shopId);
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        SearchBusinessAttributesVO searchBusinessAttributesDO = searchBusinessAttributesService.getAttributeValue(req);
        Integer areaId = areasServiceManager.getAreaIdByLocation(Constant.AREA_TYPE_CHANNEL, searchBusinessAttributesDO.getAttributeValue());
        LOGGER.debug("syncChannelArea area_id = {}", areaId);
        ShopsDO record = new ShopsDO();
        record.setId(shopId);
        record.setCorpId(corpId);
        record.setChannelAreaId(areaId);
        shopsService.update(record);
    }

    private XSSFWorkbook toExcel(List<ShopsVO> shops, List<SearchBusinessAttributesVO> shopsAttributes, List<AttributeTypesVO> attributeTypes) {
        //店铺属性数据
        Map<Integer, Map<String, List<SearchBusinessAttributesVO>>> attributes = shopsAttributes.stream()
                .collect(Collectors.groupingBy(SearchBusinessAttributesVO::getBusinessId, Collectors.groupingBy(SearchBusinessAttributesVO::getAttributeName)));

        List<String> fields = Arrays.asList("店铺名称", "旧店铺编码", "店铺编码", "营销大区", "营销小区", "渠道大区", "预计开业时间", "开业时间", "预计结业时间", "结业时间", "店铺状态");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("店铺属性数据");

        XSSFRow titleRow = sheet.createRow(0);
        XSSFRow attributeRow = sheet.createRow(1);
        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCell titleCell;
        XSSFCell attributeCell;

        int idx = 0;
        for (String field : fields) {
            titleCell = titleRow.createCell(idx);
            titleCell.setCellValue(field);
            titleCell.setCellStyle(titleStyle);

            attributeCell = attributeRow.createCell(idx);
            attributeCell.setCellValue(field);
            attributeCell.setCellStyle(titleStyle);

            idx++;
        }

        List<Integer> attributesCount = new ArrayList<>();
        for (AttributeTypesVO type : attributeTypes) {
            List<AttributesVO> attributesList = type.getAttributes();
            attributesCount.add(attributesList.size());
            for (AttributesVO attribute : attributesList) {

                titleCell = titleRow.createCell(idx);
                titleCell.setCellValue(type.getName());
                titleCell.setCellStyle(titleStyle);

                attributeCell = attributeRow.createCell(idx);
                attributeCell.setCellValue(attribute.getName());
                attributeCell.setCellStyle(titleStyle);

                idx++;
            }
        }

        CellRangeAddress region;

        for (int col = 0; col < fields.size(); col++) {
            region = new CellRangeAddress(0, 1, col, col);
            sheet.addMergedRegion(region);
        }

        int startIdx = fields.size();
        int endIdx;
        for (Integer count : attributesCount) {
            endIdx = startIdx + count - 1;
            region = new CellRangeAddress(0, 0, startIdx, endIdx);
            sheet.addMergedRegion(region);
            startIdx = endIdx + 1;

        }

        Map<String, List<SearchBusinessAttributesVO>> shopAttributes;
        XSSFRow dataRow;
        XSSFCell cell;
        String val;
        int rowsIdx = 2;
        for (ShopsVO vo : shops) {
            if (!attributes.containsKey(vo.getId())) {
                continue;
            }
            shopAttributes = attributes.get(vo.getId());

            dataRow = sheet.createRow(rowsIdx);
            cell = dataRow.createCell(0);
            cell.setCellValue(vo.getShopName());
            cell = dataRow.createCell(1);
            cell.setCellValue(vo.getOldShopCode());
            cell = dataRow.createCell(2);
            cell.setCellValue(vo.getShopCode());
            cell = dataRow.createCell(3);
            cell.setCellValue(vo.getFirstMarkingAreaName());
            cell = dataRow.createCell(4);
            cell.setCellValue(vo.getSecondMarkingAreaName());
            cell = dataRow.createCell(5);
            cell.setCellValue(vo.getChannelAreaName());
            cell = dataRow.createCell(6);
            cell.setCellValue(vo.getPlannedOpeningDate());
            cell = dataRow.createCell(7);
            cell.setCellValue(vo.getOpeningDate());
            cell = dataRow.createCell(8);
            cell.setCellValue(vo.getPlannedClosingDate());
            cell = dataRow.createCell(9);
            cell.setCellValue(vo.getClosingDate());
            cell = dataRow.createCell(10);
            cell.setCellValue(vo.getShopStateName());

            idx = fields.size();
            for (AttributeTypesVO type : attributeTypes) {
                List<AttributesVO> attributesList = type.getAttributes();
                for (AttributesVO attribute : attributesList) {
                    cell = dataRow.createCell(idx);
                    val = "";
                    List<SearchBusinessAttributesVO> vos = shopAttributes.get(attribute.getName());
                    if (vos != null && !vos.isEmpty()) {
                        val = vos.get(0).getAttributeValue();
                    }
                    cell.setCellValue(val);
                    idx++;
                }
            }

            rowsIdx++;
        }
        return wb;
    }

    public XSSFWorkbook exportShops(ShopsExportReq req) {
        ShopsReq shopsReq = new ShopsReq();
        shopsReq.setCorpId(req.getCorpId());
        shopsReq.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<ShopsVO> shops = shopsService.search(shopsReq, null);
        if (shops.isEmpty()) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "未找到任何店铺数据");
        }
        areasServiceManager.fillAreaName(shops);

        SearchBusinessAttributesReq searchBusinessAttributesReq = new SearchBusinessAttributesReq();
        searchBusinessAttributesReq.setBusinessType(Constant.BUSINESS_TYPE_SHOP);
        searchBusinessAttributesReq.setAttributeTypes(req.getAttributeTypesList());
        List<SearchBusinessAttributesVO> shopsAttributes = searchBusinessAttributesService.filterAttributes(searchBusinessAttributesReq);
        if (shopsAttributes.isEmpty()) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "未找到任何店铺属性数据");
        }

        List<AttributeTypesVO> attributeTypes = attributesServiceManager.filterAttributes(req.getAttributeTypesList());
        if (attributeTypes.isEmpty()) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "未找到任何店铺属性类型数据");
        }

        return toExcel(shops, shopsAttributes, attributeTypes);
    }

    public void importShops(ImportForm form) {
        XSSFWorkbook wb;
        try {
            wb = new XSSFWorkbook(form.getFile().getInputStream());
        } catch (Exception e) {
            LOGGER.error("导入店铺EXCEL表格处理异常, 错误信息：" + e.getMessage());
            throw new BusinessException(ReturnType.IMPORT_EXCEL_FAIL.getType(), ReturnType.IMPORT_EXCEL_FAIL.getMsg());
        }
        List<ShopsEO> shopsEOList = ExcelUtil.readExcel(wb, ShopsEO.class);
        HashMap<String, Integer> existShopCodes = getShopCodes();
        List<AreasVO> areas = areasServiceManager.getMarkingAreas();
        Map<String, Integer> bigAreasMap = areas.stream().filter(item -> item.getParentId() <= 0).collect(Collectors.toMap(AreasVO::getAreaName, AreasVO::getId, (prev, next) -> next));
        LOGGER.warn("big areas map {}", bigAreasMap.toString());
        Map<Integer, Map<String, Integer>> smallAreasMap = areas.stream().collect(Collectors.groupingBy(AreasVO::getParentId, Collectors.toMap(AreasVO::getAreaName, AreasVO::getId, (prev, next) -> next)));
        LOGGER.warn("small areas map {}", smallAreasMap.toString());
        List<ShopsDO> shopsDOList = new ArrayList<>();
        for (ShopsEO eo : shopsEOList) {
            if (eo.getShopCode() == null || (eo.getBigAreaName() == null && eo.getSmallAreaName() == null)) {
                continue;
            }
            if (!existShopCodes.containsKey(eo.getShopCode())) {
                continue;
            }
            ShopsDO shopsDO = new ShopsDO();
            shopsDO.setId(existShopCodes.getOrDefault(eo.getShopCode(), 0));
            shopsDO.setCorpId(form.getCorpId());
            shopsDO.setLastUpdateUserId(form.getUserId());
            shopsDO.setShopCode(eo.getShopCode());
            shopsDO.setMarketingAreaId(0);
            if (eo.getBigAreaName() != null) {
                Integer id = bigAreasMap.getOrDefault(eo.getBigAreaName(), 0);
                if (id > 0 && eo.getSmallAreaName() != null) {
                    id = smallAreasMap.getOrDefault(id, new HashMap<>()).getOrDefault(eo.getSmallAreaName(), 0);
                    shopsDO.setMarketingAreaId(id);
                }
            }
            shopsDOList.add(shopsDO);
        }
        shopsService.updateShopsMarkingArea(shopsDOList, form.getCorpId());
    }

    private HashMap<String, Integer> getShopCodes() {
        ShopsReq req = new ShopsReq();
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<ShopsVO> shopsVOList = shopsService.search(req, null);
        return (HashMap<String, Integer>) shopsVOList.stream().collect(Collectors.toMap(ShopsVO::getShopCode, ShopsVO::getId));
    }

    /*
     * @describe  自动更新门店状态
     * */
    public void autoChangeStatus() {
        List<ShopsVO> shopsVOList = shopsService.getAll(new ShopsReq());
        if (CollectionUtils.isEmpty(shopsVOList)) {
            return;
        }
        List<DwsDimShopStatusDO> dwsDimShopStatusDOList = dwsDimShopStatusMapper.queryByShopCode(new ArrayList<>());
        Map<String, DwsDimShopStatusDO> map = dwsDimShopStatusDOList.stream().collect(Collectors.toMap(DwsDimShopStatusDO::getShopCode, Function.identity(), (v1, v2) -> v1));
        shopsVOList.forEach(vos -> {
            if (map.get(vos.getShopCode()) != null && map.get(vos.getShopCode()).getShopState() != null && map.get(vos.getShopCode()).getShopState().equals(Constant.SHOP_STATE_OPENED) &&
                    vos.getShopStateName().equals(Constant.SHOP_STATE_TO_BE_OPENED)) {
                vos.setShopStateName(map.get(vos.getShopCode()).getShopState());
                vos.setOpeningDate(map.get(vos.getShopCode()).getOpenShopDate() == null ? "" : DateUtil.format(map.get(vos.getShopCode()).getOpenShopDate(), "yyyy-MM-dd"));
            }
            if (vos.getOpeningDate().isEmpty() && !vos.getCalcOpeningDate().isEmpty()) {
                vos.setOpeningDate(vos.getCalcOpeningDate());
            }
            if (vos.getClosingDate().isEmpty() && !vos.getCalcClosingDate().isEmpty()) {
                vos.setClosingDate(vos.getCalcClosingDate());
            }
            if (vos.getShopStateName().equals(Constant.SHOP_STATE_PAUSE)) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            if (!vos.getClosingDate().isEmpty() && (vos.getClosingDate().compareTo(now) < 0)) {
                vos.setShopStateName(Constant.SHOP_STATE_CLOSED);
                return;
            }
            if (!vos.getOpeningDate().isEmpty() && (vos.getOpeningDate().compareTo(now) <= 0)) {
                vos.setShopStateName(Constant.SHOP_STATE_OPENED);
                return;
            }
            if (!vos.getPlannedOpeningDate().isEmpty()) {
                if (vos.getOpeningDate().isEmpty() || now.compareTo(vos.getOpeningDate()) < 0) {
                    vos.setShopStateName(Constant.SHOP_STATE_TO_BE_OPENED);
                }
            }
        });
        shopsService.batchUpdate(BaseConvertor.convertList(shopsVOList, ShopsDO.class));
        return;
    }
}
