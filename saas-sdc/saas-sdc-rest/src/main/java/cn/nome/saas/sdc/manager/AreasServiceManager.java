package cn.nome.saas.sdc.manager;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.model.excel.AreasEO;
import cn.nome.saas.sdc.model.form.AreasForm;
import cn.nome.saas.sdc.model.form.ImportForm;
import cn.nome.saas.sdc.model.req.AreasReq;
import cn.nome.saas.sdc.model.vo.AreaOptionVO;
import cn.nome.saas.sdc.model.vo.AreasVO;
import cn.nome.saas.sdc.model.vo.SearchBusinessAttributesVO;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import cn.nome.saas.sdc.repository.entity.AreasDO;
import cn.nome.saas.sdc.repository.entity.ShopsDO;
import cn.nome.saas.sdc.service.AreasService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@Component
public class AreasServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AreasServiceManager.class);

    private AreasService areasService;

    private BusinessAttributeValuesServiceManager businessAttributeValuesServiceManager;

    @Autowired
    public AreasServiceManager(AreasService areasService, BusinessAttributeValuesServiceManager businessAttributeValuesServiceManager) {
        this.areasService = areasService;
        this.businessAttributeValuesServiceManager = businessAttributeValuesServiceManager;
    }

    void fillMarkingAreaName(List<ShopsVO> shops) {
        List<AreaOptionVO> areas = getAllAreas();
        Map<Integer, String> areasMap = areas.stream().collect(Collectors.toMap(AreaOptionVO::getId, AreaOptionVO::getAreaName));
        for (ShopsVO shop : shops) {
            shop.setMarketingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));
            List<SearchBusinessAttributesVO> attributes = businessAttributeValuesServiceManager.getBusinessAttributes(Constant.DEFAULT_CORP_ID, shop.getId(), Constant.BUSINESS_TYPE_SHOP, Constant.SOURCE_TYPE_ID_SHOP);
            SearchBusinessAttributesVO provinceAttribute = attributes.stream().filter(attribute -> attribute.getAttributeName().equals(Constant.FIXED_ATTRIBUTE_PROVINCE)).findAny().orElse(null);
            shop.setChannelAreaName("");
            if (provinceAttribute != null && !provinceAttribute.getAttributeValue().isEmpty()) {
                shop.setChannelAreaName(getAreaNameByLocation(Constant.AREA_TYPE_CHANNEL, provinceAttribute.getAttributeValue()));
            }
        }
    }

    void fillMarkingAreaFullName(List<ShopsVO> shops) {
        Map<Integer, String> areasMap = new HashMap<>();
        Map<Integer, Integer> areasParent = new HashMap<>();
        fillAreasMap(areasMap, areasParent);
        for (ShopsVO shop : shops) {
            shop.setMarketingAreaName("");
            if (shop.getMarketingAreaId() <= 0) {
                continue;
            }
            if (areasParent.containsKey(shop.getMarketingAreaId())) {
                shop.setSecondMarkingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));
                shop.setFirstMarkingAreaName(areasMap.getOrDefault(areasParent.get(shop.getMarketingAreaId()), ""));
                shop.setMarketingAreaName(String.format("%s/%s", shop.getFirstMarkingAreaName(), shop.getSecondMarkingAreaName()));
            } else {
                shop.setMarketingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));
            }
        }
    }

    private void fillAreasMap(Map<Integer, String> areasMap, Map<Integer, Integer> areasParent) {
        List<AreaOptionVO> areas = getAllAreas();
        areasMap.putAll(areas.stream().collect(Collectors.toMap(AreaOptionVO::getId, AreaOptionVO::getAreaName)));
        areasParent.putAll(areas.stream().collect(Collectors.toMap(AreaOptionVO::getId, AreaOptionVO::getParentId)));
    }

    void fillAreaName(List<ShopsVO> shops) {
        Map<Integer, String> areasMap = new HashMap<>();
        Map<Integer, Integer> areasParent = new HashMap<>();
        fillAreasMap(areasMap, areasParent);
        for (ShopsVO shop : shops) {
            shop.setMarketingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));
            shop.setChannelAreaName(areasMap.getOrDefault(shop.getChannelAreaId(), ""));
            shop.setFirstMarkingAreaName("");
            shop.setSecondMarkingAreaName("");
            if (shop.getMarketingAreaId() > 0) {
                if (areasParent.containsKey(shop.getMarketingAreaId())) {
                    shop.setSecondMarkingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));
                    shop.setFirstMarkingAreaName(areasMap.getOrDefault(areasParent.get(shop.getMarketingAreaId()), ""));
                } else {
                    shop.setFirstMarkingAreaName(areasMap.getOrDefault(shop.getMarketingAreaId(), ""));
                }
            }
        }
    }

    private List<AreaOptionVO> getAllAreas() {
        AreasReq req = new AreasReq();
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        return areasService.queryAll(req);
    }

    private HashMap<String, String> getLocationsNameMap(Integer areaTypeId) {
        AreasReq req = new AreasReq();
        req.setAreaTypeId(areaTypeId);
        List<AreasVO> list = areasService.search(req, null);
        HashMap<String, String> locationsNameMap = new HashMap<>();
        for (AreasVO vo : list) {
            if (vo.getLocations().isEmpty()) {
                continue;
            }
            String[] locations = vo.getLocations().split(Constant.AREA_LOCATIONS_SEPARATOR);
            if (locations.length <= 0) {
                continue;
            }
            for (String location : locations) {
                locationsNameMap.put(location, vo.getAreaName());
            }
        }
        return locationsNameMap;
    }

    private HashMap<String, Integer> getLocationsIdMap(Integer areaTypeId) {
        AreasReq req = new AreasReq();
        req.setAreaTypeId(areaTypeId);
        List<AreasVO> list = areasService.search(req, null);
        HashMap<String, Integer> locationsIdMap = new HashMap<>();
        for (AreasVO vo : list) {
            if (vo.getLocations().isEmpty()) {
                continue;
            }
            String[] locations = vo.getLocations().split(Constant.AREA_LOCATIONS_SEPARATOR);
            if (locations.length <= 0) {
                continue;
            }
            for (String location : locations) {
                locationsIdMap.put(location, vo.getId());
            }
        }
        return locationsIdMap;
    }

    Integer getAreaIdByLocation(Integer areaTypeId, String location) {
        HashMap<String, Integer> locationsMap = getLocationsIdMap(areaTypeId);
        return locationsMap.getOrDefault(location, 0);
    }

    String getAreaNameByLocation(Integer areaTypeId, String location) {
        HashMap<String, String> locationsMap = getLocationsNameMap(areaTypeId);

        return locationsMap.getOrDefault(location, "");
    }

    public List<String> getAllLocations(Integer excludeAreaId, Integer areaTypeId) {
        AreasReq req = new AreasReq();
        req.setAreaTypeId(areaTypeId);
        List<AreasVO> list = areasService.search(req, null);
        List<String> locations = new ArrayList<>();
        for (AreasVO vo : list) {
            if (vo.getId().equals(excludeAreaId)) {
                continue;
            }
            if (vo.getLocations().isEmpty()) {
                continue;
            }
            locations.addAll(Arrays.asList(vo.getLocations().split(Constant.AREA_LOCATIONS_SEPARATOR)));
        }
        return locations.stream().distinct().collect(Collectors.toList());
    }

    List<Integer> getChildrenIds(AreasReq req) {
        List<AreasVO> list = areasService.search(req, null);
        List<Integer> ids = new ArrayList<>();
        int size = list.size();
        if (size > 0) {
            for (AreasVO vo : list) {
                ids.add(vo.getId());
            }
        }
        return ids;
    }

    public List<AreasVO> search(AreasReq req, Page page) {
        return areasService.search(req, page);
    }

    public List<AreaOptionVO> options(AreasReq req) {
        return areasService.queryAll(req);
    }

    public AreasForm getDetail(Integer id) {
        AreasVO areasVO = areasService.selectByPrimaryKey(id);
        return BaseConvertor.convert(areasVO, AreasForm.class);
    }

    public void add(AreasForm form) {
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "区域名称已存在");
        }
        if (!StringUtils.isBlank(form.getAreaCode()) && this.areaCodeExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "区域编码已存在");
        }
        if (form.getLocations() != null) {
            verifyDuplicateLocation(null, form.getLocations(), form.getAreaTypeId());
        }
        AreasDO record = BaseConvertor.convert(form, AreasDO.class);
        areasService.add(record);
    }

    private void verifyDuplicateLocation(Integer excludeAreaID, String location, Integer areaTypeId) {
        String[] locations = location.split(Constant.AREA_LOCATIONS_SEPARATOR);
        List<String> allLocations = getAllLocations(excludeAreaID, areaTypeId);
        for (String l : locations) {
            if (allLocations.contains(l)) {
                throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), String.format("%s 已存在其它区域中，不能重复选择", l));
            }
        }
    }

    public void softDelete(AreasForm form) {
        AreasReq req = new AreasReq();
        req.setParentId(form.getId());
        List<AreasVO> list = areasService.search(req, null);
        if (list.size() > 0) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "该大区下有小区，请先删除小区");
        }
        AreasDO areasDO = BaseConvertor.convert(form, AreasDO.class);
        ShopsDO shopsDO = new ShopsDO();
        shopsDO.setMarketingAreaId(form.getId());
        shopsDO.setChannelAreaId(form.getId());
        shopsDO.setCorpId(form.getCorpId());
        areasService.softDelete(areasDO, shopsDO);
    }

    public void update(AreasForm form) {
        if (form.getId() == null) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "ID不能为空");
        }
        if (this.nameExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "区域名称已存在");
        }
        if (!StringUtils.isBlank(form.getAreaCode()) && this.areaCodeExist(form)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "区域编码已存在");
        }
        verifyDuplicateLocation(form.getId(), form.getLocations(), form.getAreaTypeId());
        AreasDO record = BaseConvertor.convert(form, AreasDO.class);
        areasService.update(record);
    }

    List<AreasVO> getMarkingAreas() {
        AreasReq req = new AreasReq();
        req.setAreaTypeId(Constant.AREA_TYPE_MARKING);
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        return areasService.search(req, null);
    }

    public XSSFWorkbook exportMarkingArea(Integer corpId) {
        AreasReq req = new AreasReq();
        req.setCorpId(corpId);
        req.setAreaTypeId(Constant.AREA_TYPE_MARKING);
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        List<AreasVO> areasVOList = areasService.search(req, null);
        HashMap<Integer, AreasVO> bigAreasMap = (HashMap<Integer, AreasVO>) areasVOList.stream().filter(item -> item.getParentId() <= 0)
                .collect(Collectors.toMap(AreasVO::getId, Function.identity()));
        List<AreasEO> areasEOList = new ArrayList<>();
        for (AreasVO vo : areasVOList) {
            if (vo.getParentId() <= 0) {
                continue;
            }
            if (!bigAreasMap.containsKey(vo.getParentId())) {
                throw new BusinessException(ReturnType.EXPORT_EXCEL_FAIL.getType(), ReturnType.EXPORT_EXCEL_FAIL.getMsg());
            }
            AreasVO bigArea = bigAreasMap.get(vo.getParentId());
            AreasEO eo = new AreasEO();
            eo.setBigAreaName(bigArea.getAreaName());
            eo.setBigAreaManager(bigArea.getAreaManager());
            eo.setBigAreaManagerJobNumber(bigArea.getAreaManagerJobNumber());
            eo.setSmallAreaName(vo.getAreaName());
            eo.setSmallAreaManager(vo.getAreaManager());
            eo.setSmallAreaManagerJobNumber(vo.getAreaManagerJobNumber());
            areasEOList.add(eo);
        }
        return ExcelUtil.exportExcel("区域表", areasEOList, AreasEO.class);
    }

    public void importMarkingArea(ImportForm form) {
        XSSFWorkbook wb;
        try {
            wb = new XSSFWorkbook(form.getFile().getInputStream());
        } catch (Exception e) {
            LOGGER.error("导入区域EXCEL表格处理异常, 错误信息：" + e.getMessage());
            throw new BusinessException(ReturnType.IMPORT_EXCEL_FAIL.getType(), ReturnType.IMPORT_EXCEL_FAIL.getMsg());
        }
        List<AreasEO> list = ExcelUtil.readExcel(wb, AreasEO.class);
        HashMap<String, AreasDO> bigAreaMap = new HashMap<>();
        HashMap<String, List<AreasDO>> smallAreaMap = new HashMap<>();
        for (AreasEO eo : list) {
            if (eo.getBigAreaName() == null) {
                continue;
            }
            if (!bigAreaMap.containsKey(eo.getBigAreaName())) {
                AreasDO bigAreaDO = new AreasDO();
                bigAreaDO.setCorpId(form.getCorpId());
                bigAreaDO.setCreateUserId(form.getUserId());
                bigAreaDO.setLastUpdateUserId(form.getUserId());
                bigAreaDO.setAreaTypeId(Constant.AREA_TYPE_MARKING);
                bigAreaDO.setAreaName(eo.getBigAreaName());
                bigAreaDO.setAreaManager(eo.getBigAreaManager());
                bigAreaDO.setAreaManagerJobNumber(eo.getBigAreaManagerJobNumber());
                bigAreaMap.put(eo.getBigAreaName(), bigAreaDO);
            }
            if (!smallAreaMap.containsKey(eo.getBigAreaName())) {
                smallAreaMap.put(eo.getBigAreaName(), new ArrayList<>());
            }
            AreasDO smallAreaDO = new AreasDO();
            smallAreaDO.setCorpId(form.getCorpId());
            smallAreaDO.setCreateUserId(form.getUserId());
            smallAreaDO.setLastUpdateUserId(form.getUserId());
            smallAreaDO.setAreaTypeId(Constant.AREA_TYPE_MARKING);
            smallAreaDO.setAreaName(eo.getSmallAreaName());
            smallAreaDO.setAreaManager(eo.getSmallAreaManager());
            smallAreaDO.setAreaManagerJobNumber(eo.getSmallAreaManagerJobNumber());
            smallAreaMap.get(eo.getBigAreaName()).add(smallAreaDO);
        }
        areasService.importMarkingArea(form.getCorpId(), Constant.AREA_TYPE_MARKING, bigAreaMap, smallAreaMap);
    }

    private boolean nameExist(AreasForm form) {
        AreasReq req = new AreasReq();
        req.setCorpId(form.getCorpId());
        req.setAreaTypeId(form.getAreaTypeId());
        req.setAreaName(form.getAreaName());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        AreasVO record = areasService.nameExist(req);
        if (record == null) {
            return false;
        }
        return form.getId() == null || !form.getId().equals(record.getId());
    }

    private boolean areaCodeExist(AreasForm form) {
        AreasReq req = new AreasReq();
        req.setCorpId(form.getCorpId());
        req.setAreaTypeId(form.getAreaTypeId());
        req.setAreaCode(form.getAreaCode());
        req.setIsDeleted(Constant.IS_DELETE_FALSE);
        AreasVO record = areasService.areaCodeExist(req);
        if (record == null) {
            return false;
        }
        return form.getId() == null || !form.getId().equals(record.getId());
    }
}
