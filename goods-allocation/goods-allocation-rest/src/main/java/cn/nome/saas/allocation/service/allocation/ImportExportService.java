package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ImportNewGoodsIssueRangeDo;
import cn.nome.saas.allocation.model.issue.DisplayDataV2;
import cn.nome.saas.allocation.model.issue.DwsDimShopData;
import cn.nome.saas.allocation.model.issue.ShopDisplayDesignData;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.model.old.allocation.NewGoodsIssueRangeReq;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.ExcelUtil;
import cn.nome.saas.allocation.utils.IssueDayUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.nome.saas.allocation.constant.Constant.ATTR_VALUE_SPLIT;

@Service
public class ImportExportService {

    //日期时间格式长度20190706
    private int BAT_TAB_SUFFIX_LEN_8 = 8;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GlobalConfigRuleService globalConfigRuleService;

    @Autowired
    private DisplayService displayService;
    @Autowired
    private ShopService shopService;

    @Autowired
    private DisplayDOMapper displayDOMapper;

    @Autowired
    private GoodsInfoDOMapper goodsInfoDOMapper;

    @Autowired
    private ShopInfoDOMapper shopInfoDOMapper;

    @Autowired
    private ShopDisplayDesignDOMapper shopDisplayDesignDOMapper;

    @Autowired
    private DwsDimShopDOMapper dwsDimShopDOMapper;

    @Autowired
    private CommonDOMapper commonDOMapper;

    @Autowired
    ShopInfoCache shopInfoCache;

    @Autowired
    ShopToStockDOMapper shopToStockDoDOMapper;
    @Autowired
    NewGoodsIssueRangeMapper newGoodsRangeDOMapper;
    @Autowired
    NewGoodsIssueRangeDetailMapper newGoodsIssueRangeDetailMapper;
    @Autowired
    NewGoodsRangeService newGoodsRangeService;


    /**
     * 导出陈列分类
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportDisplayCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<DisplayDo> displayData = displayDOMapper.displayData();
        ExcelUtil.exportDisplayCategory(displayData, request, response);
        logger.info("exportDisplayCategory over");
    }

    /**
     * 导出陈列分类
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportDisplayByParam(String largeCategory, String midCategory, String smallCategory, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategory)) {
            param.put("smallCategory", smallCategory.split(ATTR_VALUE_SPLIT));
        }

        List<DisplayDo> list = displayDOMapper.selectByPage(param);

        ExcelUtil.exportDisplayCategory(list, request, response);
        logger.info("exportDisplayCategory over");
    }

    /**
     * 导出陈列商品
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportDisplayGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<GoodsInfoDO> goodsInfoData = goodsInfoDOMapper.goodsInfoData();
        ExcelUtil.exportGoodsInfoData(goodsInfoData, request, response);
        logger.info("exportDisplayGoods over");
    }

    /**
     * 导出陈列商品
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportDisplayGoodsByParam(String largeCategory, String midCategory, String smallCategory, String matCode, String matName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategory)) {
            param.put("smallCategory", smallCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(matCode)) {
            param.put("matCode", matCode);
        }
        if (!StringUtils.isEmpty(matName)) {
            param.put("matName", matName);
        }

        List<GoodsInfoDO> list = goodsInfoDOMapper.selectByPage(param);

        ExcelUtil.exportGoodsInfoData(list, request, response);
        logger.info("exportDisplayGoods over");
    }

    /**
     * 导出门店信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportShopInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ShopInfoData> shopInfoData = shopInfoDOMapper.shopInfoData();
        ExcelUtil.exportShopInfoData(shopInfoData, shopService.getShopInfoAttrKeys(), request, response);
        logger.info("exportShopInfo over");
    }

    /**
     * 导出门店信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportShopInfoByParam(String goodsAreas, String shopLvs, String shopNames, String shopCode, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>(16);
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

        List<ShopInfoData> list = shopInfoDOMapper.selectByPage(param);

        ExcelUtil.exportShopInfoData(list, shopService.getShopInfoAttrKeys(), request, response);
        logger.info("exportShopInfo over");
    }

    /**
     * 导出门店陈列
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportShopDisplay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ShopDisplayDesignData> shopDisplayDesignData = shopDisplayDesignDOMapper.shopDisplayData();
        ExcelUtil.exportShopDisplayData(shopDisplayDesignData, request, response);
        logger.info("exportShopDisplay over");
    }

    /**
     * 导出门店陈列
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportShopDisplayByParam(String largeCategory, String midCategory, String shopNames, HttpServletRequest request, HttpServletResponse response) throws Exception {
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

        List<ShopDisplayDesignData> list = shopDisplayDesignDOMapper.selectByPage(param);

        //获取店铺名称
        for (ShopDisplayDesignData s : list) {
            s.setShopName(globalConfigRuleService.getShopIdMap().get(s.getShopId()));
        }

        ExcelUtil.exportShopDisplayData(list, request, response);
        logger.info("exportShopDisplay over");
    }

    /**
     * 导出调仓申请
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportShopToStockByParam(String orderNo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>(16);
        if (!StringUtils.isEmpty(orderNo)) {
            param.put("orderNo", orderNo);
        }
        List<ShopToStockDo> list = shopToStockDoDOMapper.selectByPage(param);
        XSSFWorkbook wb = cn.nome.platform.common.utils.excel.ExcelUtil.exportExcel("调仓申请", list, ShopToStockDo.class);
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
        String nowString = dateFormat.format(now);
        String fileName = ObjectUtils.toString(list.get(0).getShopName()) + "调仓申请" + nowString;
        ResponseUtil.export(response, wb, fileName);
//        ExcelUtil.exportShopToStockData(list, request, response);
        logger.info("exportShopDisplay over");
    }

    /**
     * 导入门店信息
     *
     * @param file
     * @return
     */
    public Result importShopInfo(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        //if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
        checkFileType(fileName);

        List<ShopInfoData> importData = null;
        try {
            importData = ExcelUtil.readShopInfoData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        checkImportDataNull(importData);

        List<String> shopCodes = new ArrayList<>(importData.size());
        importData.stream().forEach(data -> shopCodes.add(data.getShopCode()));
        List<DwsDimShopData> dwsDimShopData = getShopId(shopCodes);

        checkDimShopDataNull(dwsDimShopData);
        Map<String, String> map = new HashMap<>(dwsDimShopData.size());
        dwsDimShopData.stream().forEach(data -> map.put(data.getShopCode(), data.getShopID()));
        importData.stream().forEach(data -> data.setShopID(map.get(data.getShopCode()) == null ? "" : map.get(data.getShopCode())));

        List<ShopInfoData> list = shopInfoCache.getShopList();
        if (list.size() > 0) {
            importData.stream().forEach(data -> data.setAttrKeys(list.get(0).getAttrKeys()));
        }

        importData.stream().forEach(data -> data.setIssueDay(IssueDayUtil.getIssueDayV2(DateUtil.getCurrentDate(), data.getRoadDay(), data.getIssueTime())));

        Map<String, Object> param = new HashMap<>();
        param.put("shopIds", importData.stream().map(ShopInfoData::getShopID).collect(Collectors.toList()));
        List<ShopInfoData> shopInfoDataList = shopInfoDOMapper.selectByPage(param);

        int succ = batchInsertShopInfoTab(importData);

        if (succ > 0) {
            importData.forEach(shopInfoData -> {
                boolean isNewShop = true;
                for (ShopInfoData shopInfo : shopInfoDataList) {
                    if (shopInfoData.getShopID().equals(shopInfo.getShopID())) {

                        if (!shopInfoData.getStatus().equals(shopInfo.getStatus())
                                || !shopInfoData.getShopLevel().equals(shopInfo.getShopLevel())
                                || !shopInfoData.getGoodsArea().equals(shopInfo.getGoodsArea())
                                || !shopInfoData.getAttrFirVal().equals(shopInfo.getAttrFirVal())
                                || !shopInfoData.getAttrSecVal().equals(shopInfo.getAttrSecVal())
                                || !shopInfoData.getAttrThiVal().equals(shopInfo.getAttrThiVal())
                                || !shopInfoData.getAttrFourVal().equals(shopInfo.getAttrFourVal())
                                || !shopInfoData.getAttrFifVal().equals(shopInfo.getAttrFifVal())) {

                            if (shopInfoDOMapper.checkShopTask(shopInfoData.getShopID()) == 0) {
                                shopInfoDOMapper.insertShopTask(shopInfoData.getShopID());
                            }
                        }

                        isNewShop = false;
                    }
                }

                if (isNewShop && shopInfoDOMapper.checkShopTask(shopInfoData.getShopID()) == 0) {
                    shopInfoDOMapper.insertShopTask(shopInfoData.getShopID());
                }

            });
        }

        return ResultUtil.handleSuccessReturn(importData.size());
    }

    private void checkDimShopDataNull(List<DwsDimShopData> dwsDimShopData) {
        if (dwsDimShopData == null || dwsDimShopData.isEmpty()) {
            throw new BusinessException("12000", Constant.GET_SHOPID_NULL);
        }
    }

    private void checkFileType(String fileName) {
        if (!isExcel(fileName)) {
            throw new BusinessException("12000", Constant.FILE_TYPE_ERROR);
        }
    }

    /**
     * 截掉尾部文件格式，得到原表名
     *
     * @param batTableName
     * @return
     */
    private String getSubTabName(String batTableName) {
        return batTableName.substring(0, batTableName.length() - BAT_TAB_SUFFIX_LEN_8);
    }

    private int batchInsertShopInfoTab(List<ShopInfoData> importData) {
        int rst = shopInfoDOMapper.batchInsertTab(importData);
        logger.warn("batchInsertShopInfoTab:{}", rst);
        checkBatchInsert(rst);
        return rst;
    }

    private void checkBatchInsert(int rst) {
        if (rst <= 0) {
            throw new BusinessException("12000", Constant.BATCH_INSERT_ERR);
        }
    }

    private List<DwsDimShopData> getShopId(List<String> shopCodes) {
        List<DwsDimShopData> dwsDimShops = dwsDimShopDOMapper.getShopIdByCode(shopCodes);
        return dwsDimShops;
    }

    private boolean selectAndInsert(String batTableName) {
        String tableName = getSubTabName(batTableName);
        try {
            commonDOMapper.selectAndInsert(batTableName, tableName);
        } catch (Exception e) {
            logger.error("selectAndInsert err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean createAndCopyTab(String batTableName) {
        String tableName = getSubTabName(batTableName);
        try {
            commonDOMapper.createAndCopyTab(batTableName, tableName);
        } catch (Exception e) {
            logger.error("createAndCopyTab err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean truncateTable(String tableName) {
        try {
            commonDOMapper.truncateTable(tableName);
        } catch (Exception e) {
            logger.error("truncateTable err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean existsTable(String tableName) {
        Map tabList = commonDOMapper.showTableExists(tableName);
        if (tabList == null || tabList.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 导入陈列分类
     *
     * @param file
     * @return
     */
    public Result importDisplayCategory(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Assert.isTrue(isExcel(fileName), Constant.FILE_TYPE_ERROR);

        //校验excel文件
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING, CellType.NUMERIC, CellType.NUMERIC, CellType.NUMERIC, CellType.NUMERIC, CellType.NUMERIC, CellType.NUMERIC),
                Arrays.asList("陈列大类", "陈列中类", "陈列小类", "中类单版陈列量", "小类单版陈列量", "坑位列数", "坑位行数", "坑位深度", "数量权重"),
                null,
                new HashSet<>(Arrays.asList(0, 1, 2)));
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<DisplayDataV2> importData = null;
        try {
            importData = ExcelUtil.readDisplayData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        Assert.isTrue(importData != null && !importData.isEmpty(), Constant.NO_DATA_IMPORT);

//        String batTableName = getBatTableName("display");
//        boolean doflag = existsTable(batTableName);
//        logger.debug("{} existsTable:{}", batTableName, doflag);
//
//        doflag = batTabData(batTableName, doflag);
//        // batch import data 2 table
//        if (doflag) {
//            String oldTab = getSubTabName(batTableName);
//            doflag = truncateTable(oldTab);
//            logger.warn("{} truncateTable:{}", oldTab, doflag);
//            if (doflag) {
//                ret = batchInsertDisplayTab(importData);
//            }
//        }
        batchInsertDisplayTab(importData);
        return ResultUtil.handleSuccessReturn(importData.size());
    }

    private boolean isExcel(String fileName) {
        return fileName.endsWith("xls") || fileName.endsWith("xlsx");
    }

    /**
     * 备份表数据
     *
     * @param batTableName
     * @param existsTable
     * @return
     */
    private boolean batTabData(String batTableName, boolean existsTable) {
        if (existsTable) {
//            // truncate bat table
//            existsTable = truncateTable(batTableName);
//            logger.warn("{} truncateTable:{}", batTableName, existsTable);
//
//            // select and insert
//            existsTable = selectAndInsert(batTableName);
//            logger.warn("{} selectAndInsert:{}", batTableName, existsTable);
            logger.info("{} is backup", batTableName);
        } else {
            // create and copy
            existsTable = createAndCopyTab(batTableName);
            logger.warn("{} createAndCopyTab:{}", batTableName, existsTable);
        }
        if (!existsTable) {
            throw new BusinessException("12000", Constant.BAK_TAB_ERR);
        }
        return existsTable;
    }

    private int batchInsertDisplayTab(List<DisplayDataV2> importData) {
        int rst = displayDOMapper.batchInsertTab(importData);
        logger.warn("batchInsertDisplayTab:{}", rst);
        checkBatchInsert(rst);
        return rst;
    }

    /**
     * 导入陈列商品
     *
     * @param file
     * @return
     */
    public Result importDisplayGoods(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        checkFileType(fileName);

        Map<Integer, Set<String>> limitValMap = new HashMap<>(1);
        limitValMap.put(9, new HashSet<>(Arrays.asList("是", "否")));
        //校验excel文件
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.NUMERIC, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING),
                Arrays.asList("货品ID", "货号", "货品简称", "陈列大类", "陈列中类", "陈列小类", "中包装数", "货区", "货盘", "是否易碎品", "单号"),
                limitValMap,
                new HashSet<>(Collections.singletonList(1)));
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<GoodsInfoDO> importData = null;
        try {
            importData = ExcelUtil.readDisplayGoodsData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        checkImportDataNull(importData);

        List<GoodsInfoDO> goodsInfoDOList = goodsInfoDOMapper.selectGoodsListByMatCode(importData.stream().map(GoodsInfoDO::getMatCode).collect(Collectors.toList()));

        int succ = batchInsertGoodsInfoTab(importData);

        if (succ > 0) {

            for (GoodsInfoDO data : importData) {
                boolean isNewGoods = true;
                for (GoodsInfoDO goodsInfoDO : goodsInfoDOList) {
                    if (data.getMatCode().equals(goodsInfoDO.getMatCode())) {
                        if (!data.getCategoryName().equals(goodsInfoDO.getCategoryName()) ||
                                !data.getMidCategoryName().equals(goodsInfoDO.getMidCategoryName()) ||
                                !data.getSmallCategoryName().equals(goodsInfoDO.getSmallCategoryName())) {

                            if (goodsInfoDOMapper.checkGoodsTask(data.getMatCode()) == 0) {
                                goodsInfoDOMapper.insertGoodsTask(data.getMatCode());
                            }
                        }
                        isNewGoods = false;
                    }
                }

                if (isNewGoods && goodsInfoDOMapper.checkGoodsTask(data.getMatCode()) == 0) {
                    goodsInfoDOMapper.insertGoodsTask(data.getMatCode());
                }
            }
        }

        return ResultUtil.handleSuccessReturn(importData.size());
    }

    private String getBatTableName(String tabName) {
        return tabName + DateUtil.format(new Date(), Constant.DATE_PATTERN_2);
    }

    private int batchInsertGoodsInfoTab(List<GoodsInfoDO> importData) {
        int rst = goodsInfoDOMapper.batchInsertTab(importData);
        logger.warn("batchInsertGoodsInfoTab:{}", rst);
        checkBatchInsert(rst);
        return rst;
    }

    /**
     * 导入门店陈列
     *
     * @param file
     * @return
     */
    public Result importShopDisplay(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        checkFileType(fileName);


//        Map<Integer, Set<String>> limitValMap = new HashMap<>(1);
//        limitValMap.put(9, new HashSet<>(Arrays.asList("是", "否")));
        //校验excel文件
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2, 3, 4),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING, CellType.NUMERIC, CellType.NUMERIC),
                Arrays.asList("门店代码", "陈列大类", "陈列中类", "陈列饱满度", "仓位数"),
                null,
                new HashSet<>(Arrays.asList(0, 1, 2)));
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<ShopDisplayDesignData> importData = null;

        try {
            importData = ExcelUtil.readShopDisplayData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        checkImportDataNull(importData);

        msg = displayService.checkShopSheetSpace(importData);
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<String> shopCodes = new ArrayList<>(importData.size());
        importData.stream().forEach(data -> shopCodes.add(data.getShopCode()));
        List<DwsDimShopData> dwsDimShopData = getShopId(shopCodes);
        checkDimShopDataNull(dwsDimShopData);

        Map<String, String> map = new HashMap<>(dwsDimShopData.size());
        dwsDimShopData.stream().forEach(data -> map.put(data.getShopCode(), data.getShopID()));
        importData.stream().forEach(data -> data.setShopId(map.get(data.getShopCode()) == null ? "" : map.get(data.getShopCode())));

        String batTableName = getBatTableName("shop_display_design");
        boolean doflag = existsTable(batTableName);
        logger.debug("{} existsTable:{}", batTableName, doflag);

        doflag = batTabData(batTableName, doflag);

        //删除导入店铺的旧数据
        Set<String> shopIdSet = importData.stream().map(ShopDisplayDesignData::getShopId).collect(Collectors.toSet());
        shopDisplayDesignDOMapper.delByShopIds(shopIdSet);
        //插入导入店铺的新数据
        batchInsertShopDisplayDesignTab(importData);

//        // batch import data 2 table
//        if (doflag) {
//            String oldTab = getSubTabName(batTableName);
//            doflag = truncateTable(oldTab);
//            logger.warn("{} truncateTable:{}", oldTab, doflag);
//            if (doflag) {
//                batchInsertShopDisplayDesignTab(importData);
//            }
//        }
        return ResultUtil.handleSuccessReturn(importData.size());
    }

    private void checkImportDataNull(List<?> importData) {
        if (importData == null || importData.isEmpty()) {
            throw new BusinessException("12000", Constant.NO_DATA_IMPORT);
        }
    }

    private int batchInsertShopDisplayDesignTab(List<ShopDisplayDesignData> importData) {
        int rst = shopDisplayDesignDOMapper.batchInsertTab(importData);
        logger.warn("batchInsertTab:{}", rst);
        checkBatchInsert(rst);
        return rst;
    }

    /**
     * 铺货计划列表导出
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportRangeDo(String matCode, String sizeId, String matName, String createStart,
                              String createEnd, String updatedBy, Integer validFlag, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
        if (StringUtils.isNotBlank(matCode)) {
            req.setMatCode(matCode);
        }
        if (StringUtils.isNotBlank(sizeId)) {
            req.setSizeId(sizeId);
        }
        if (StringUtils.isNotBlank(matName)) {
            req.setMatName(matName);
        }
        if (StringUtils.isNotBlank(createStart)) {
            req.setCreatedStart(LocalDate.parse(createStart, DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (StringUtils.isNotBlank(createEnd)) {
            req.setCreatedStart(LocalDate.parse(createEnd, DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (StringUtils.isNotBlank(updatedBy)) {
            req.setUpdatedBy(updatedBy);
        }
        if (validFlag != null) {
            //0-失效, 1- 生效
            req.setValidFlag(validFlag);
        }

        List<NewGoodsIssueRangeDO> list = newGoodsRangeDOMapper.pageList(req);
        ExcelUtil.exportRangeDo(list, request, response);
        logger.info("exportRangeDo over");
    }

    /**
     * 导出铺货计划
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportRangeDetail(Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<NewGoodsIssueRangeDetailDO> list = newGoodsIssueRangeDetailMapper.selectNewGoodsList(NewGoodsIssueRangeDetailDO.PLAN_FLAG_PLAN, id);
        ExcelUtil.exportRangeDetailDo(list, request, response);
        logger.info("exportRangeDo over");
    }

    /**
     * 导出门店信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportRangeShop(Integer id, Integer includeFlag, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NewGoodsIssueRangeDO newGoodsIssueRangeDO = newGoodsRangeService.selectByParamRangeDo(id);
        Map<String, String> shopIdMap;
        if (includeFlag == 1) {
            if ((shopIdMap = newGoodsIssueRangeDO.getShopIdMapInclude()) == null) {
                shopIdMap = new HashMap<>();
            }
        } else {
            if ((shopIdMap = newGoodsIssueRangeDO.getShopIdMapExclude()) == null) {
                shopIdMap = new HashMap<>();
            }
        }
        ExcelUtil.exportRangeShop(shopIdMap.keySet(), includeFlag, request, response);
        logger.info("exportRangeDo over");
    }

    /**
     * 导入铺货计划失效时间
     *
     * @param file
     * @return
     */
    public Result importRangeDoInvalid(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Assert.isTrue(isExcel(fileName), Constant.FILE_TYPE_ERROR);

        //校验excel文件
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING),
                Arrays.asList("货号", "尺码名称", "失效时间"),
                null,
                new HashSet<>(Arrays.asList(0, 1)));
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<NewGoodsIssueRangeReq> importData;
        try {
            importData = ExcelUtil.readRangeDoInvalidData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        Assert.isTrue(importData != null && !importData.isEmpty(), Constant.NO_DATA_IMPORT);

        //批量更新
        for (NewGoodsIssueRangeReq req : importData) {
            newGoodsRangeDOMapper.updateByMatCodeSizeName(req);
        }
        return ResultUtil.handleSuccessReturn(importData.size());
    }

    /**
     * 导入铺货计划范围
     *
     * @param file
     * @return
     */
    @Transactional(value = "allocationTransactionManager", rollbackFor = Exception.class)
    public Result importNewGoodsRangeRange(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Assert.isTrue(isExcel(fileName), Constant.FILE_TYPE_ERROR);

        Map<Integer, Set<String>> limitValMap = new HashMap(2);
        limitValMap.put(2, new HashSet<>(Arrays.asList("大区", "省份", "城市", "销售等级", "陈列等级", "门店编码")));
        limitValMap.put(4, new HashSet<>(Arrays.asList("应用", "排除")));
        //校验excel文件
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2, 3, 4),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING),
                Arrays.asList("货号", "尺码名称", "类型", "对象", "应用/排除"),
                limitValMap,
                new HashSet<>(Arrays.asList(0, 1, 2, 4)));
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<ImportNewGoodsIssueRangeDo> importData;
        try {
            importData = ExcelUtil.readNewGoodsRangeRangeDo(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        Assert.isTrue(importData != null && !importData.isEmpty(), Constant.NO_DATA_IMPORT);

        //批量更新
        Map<String, NewGoodsIssueRangeReq> newGoodsIssueRangeReqMap = new HashMap<>();
        for (ImportNewGoodsIssueRangeDo importDo : importData) {
            String key = importDo.getMatCode() + "_" + importDo.getSizeName();
            NewGoodsIssueRangeReq req = newGoodsIssueRangeReqMap.get(key);
            if (req == null) {
                req = new NewGoodsIssueRangeReq();
                req.setMatCode(importDo.getMatCode());
                req.setSizeName(importDo.getSizeName());
            }
            if ("应用".equals(importDo.getInExclude())) {
                if ("大区".equals(importDo.getType())) {
                    req.setRegionInclude(importDo.getObj());
                } else if ("省份".equals(importDo.getType())) {
                    req.setProvinceInclude(importDo.getObj());
                } else if ("城市".equals(importDo.getType())) {
                    req.setCityInclude(importDo.getObj());
                } else if ("销售等级".equals(importDo.getType())) {
                    req.setSaleLvInclude(importDo.getObj());
                } else if ("陈列等级".equals(importDo.getType())) {
                    req.setDisplayLvInclude(importDo.getObj());
                } else if ("门店编码".equals(importDo.getType())) {
                    req.setShopIdInclude(importDo.getObj());
                }
            } else {
                if ("大区".equals(importDo.getType())) {
                    req.setRegionExclude(importDo.getObj());
                } else if ("省份".equals(importDo.getType())) {
                    req.setProvinceExclude(importDo.getObj());
                } else if ("城市".equals(importDo.getType())) {
                    req.setCityExclude(importDo.getObj());
                } else if ("销售等级".equals(importDo.getType())) {
                    req.setSaleLvExclude(importDo.getObj());
                } else if ("陈列等级".equals(importDo.getType())) {
                    req.setDisplayLvExclude(importDo.getObj());
                } else if ("门店编码".equals(importDo.getType())) {
                    req.setShopIdExclude(importDo.getObj());
                }
            }
            newGoodsIssueRangeReqMap.put(key, req);
        }

        for (NewGoodsIssueRangeReq req : newGoodsIssueRangeReqMap.values()) {
            //更新修改人
            req.setUpdatedBy(AuthUtil.getSessionUserId());
            NewGoodsIssueRangeDO newGoodsIssueRangeDO = newGoodsRangeDOMapper.selectByMatCodeSizeName(req.getMatCode(), req.getSizeName());
            if (newGoodsIssueRangeDO == null) {
                throw new BusinessException("12000", "导入的文件中有系统中不存在的铺货计划的MatCode与尺码名称");
            }
            newGoodsRangeDOMapper.updateByMatCodeSizeName(req);
            Map<String, String> shopApplyMap = globalConfigRuleService.getShopApplyMap(
                    req.getRegionInclude(), req.getProvinceInclude(), req.getCityInclude(), req.getSaleLvInclude(), req.getDisplayLvInclude(), req.getShopIdInclude(),
                    req.getAttrVal1In(), req.getAttrVal2In(), req.getAttrVal3In(), req.getAttrVal4In(), req.getAttrVal5In(),
                    req.getRegionExclude(), req.getProvinceExclude(), req.getCityExclude(), req.getSaleLvExclude(), req.getDisplayLvExclude(), req.getShopIdExclude(),
                    req.getAttrVal1Ex(), req.getAttrVal2Ex(), req.getAttrVal3Ex(), req.getAttrVal4Ex(), req.getAttrVal5Ex());

            if (shopApplyMap.size() <= 0) {
                throw new BusinessException("12000", "没有需要导入的门店");
            }
            //删除旧的
            int delRet = newGoodsIssueRangeDetailMapper.delByRangeId(newGoodsIssueRangeDO.getId(), NewGoodsIssueRangeDetailDO.PLAN_FLAG_WHITELIST);
            List<NewGoodsIssueRangeDetailDO> list = new ArrayList<>();
            NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailDO;
            for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
                newGoodsIssueRangeDetailDO = new NewGoodsIssueRangeDetailDO();
                newGoodsIssueRangeDetailDO.setRangeId(newGoodsIssueRangeDO.getId());
                newGoodsIssueRangeDetailDO.setShopId(entry.getKey());
                newGoodsIssueRangeDetailDO.setShopCode(entry.getValue());
                newGoodsIssueRangeDetailDO.setPlanFlag(NewGoodsIssueRangeDetailDO.PLAN_FLAG_WHITELIST);
                list.add(newGoodsIssueRangeDetailDO);
            }
            newGoodsIssueRangeDetailMapper.batchInsert(list);
        }

        return ResultUtil.handleSuccessReturn(importData.size());
    }

    /**
     * 导入铺货计划范围
     *
     * @param file
     * @return
     */
    public Result importRangeDetailShop(Integer id, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Assert.isTrue(isExcel(fileName), Constant.FILE_TYPE_ERROR);

        Map<Integer, Set<String>> limitValMap = new HashMap(2);
        limitValMap.put(2, new HashSet<>(Arrays.asList("大区", "省份", "城市", "销售等级", "陈列等级", "门店编码")));
        limitValMap.put(4, new HashSet<>(Arrays.asList("应用", "排除")));
        //校验excel文件
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2, 3, 4),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING),
                Arrays.asList("货号", "尺码ID", "类型", "对象", "应用/排除"),
                limitValMap,
                new HashSet<>(Arrays.asList(0, 1, 2, 4)));
        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        List<ImportNewGoodsIssueRangeDo> importData;
        try {
            importData = ExcelUtil.readNewGoodsRangeRangeDo(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        Assert.isTrue(importData != null && !importData.isEmpty(), Constant.NO_DATA_IMPORT);

        //批量更新
        Map<String, NewGoodsIssueRangeReq> newGoodsIssueRangeReqMap = new HashMap<>();
        for (ImportNewGoodsIssueRangeDo importDo : importData) {
            String key = importDo.getMatCode() + "_" + importDo.getSizeId();
            NewGoodsIssueRangeReq req = newGoodsIssueRangeReqMap.get(key);
            if (req == null) {
                req = new NewGoodsIssueRangeReq();
                req.setMatCode(importDo.getMatCode());
                req.setSizeName(importDo.getSizeName());
            }
            if ("应用".equals(importDo.getInExclude())) {
                if ("大区".equals(importDo.getType())) {
                    req.setRegionInclude(importDo.getObj());
                } else if ("省份".equals(importDo.getType())) {
                    req.setProvinceInclude(importDo.getObj());
                } else if ("城市".equals(importDo.getType())) {
                    req.setCityInclude(importDo.getObj());
                } else if ("销售等级".equals(importDo.getType())) {
                    req.setSaleLvInclude(importDo.getObj());
                } else if ("陈列等级".equals(importDo.getType())) {
                    req.setDisplayLvInclude(importDo.getObj());
                } else if ("门店编码".equals(importDo.getType())) {
                    req.setShopIdInclude(importDo.getObj());
                }
            } else {
                if ("大区".equals(importDo.getType())) {
                    req.setRegionExclude(importDo.getObj());
                } else if ("省份".equals(importDo.getType())) {
                    req.setProvinceExclude(importDo.getObj());
                } else if ("城市".equals(importDo.getType())) {
                    req.setCityExclude(importDo.getObj());
                } else if ("销售等级".equals(importDo.getType())) {
                    req.setSaleLvExclude(importDo.getObj());
                } else if ("陈列等级".equals(importDo.getType())) {
                    req.setDisplayLvExclude(importDo.getObj());
                } else if ("门店编码".equals(importDo.getType())) {
                    req.setShopIdExclude(importDo.getObj());
                }
            }
            newGoodsIssueRangeReqMap.put(key, req);
        }

        if (newGoodsIssueRangeReqMap.values().size() > 1) {
            throw new BusinessException("12000", "只能导入一个商品的门店编码范围");
        }
        NewGoodsIssueRangeReq req = newGoodsIssueRangeReqMap.values().stream().collect(Collectors.toList()).get(0);
        NewGoodsIssueRangeReq selectReq = new NewGoodsIssueRangeReq();
        selectReq.setId(id);
        List<NewGoodsIssueRangeDO> list = newGoodsRangeDOMapper.pageList(selectReq);
        if (list.size() <= 0) {
            throw new BusinessException("12000", "查无数据");
        }
        NewGoodsIssueRangeDO newGoodsIssueRangeDO = list.get(0);
        if (!(newGoodsIssueRangeDO.getMatCode().equals(req.getMatCode()) && newGoodsIssueRangeDO.getSizeName().equals(req.getSizeName()))) {
            throw new BusinessException("12000", "导入的商品编码与当前商品不一致");
        }

        Map<String, String> shopApplyMap = globalConfigRuleService.getShopApplyMap(
                req.getRegionInclude(), req.getProvinceInclude(), req.getCityInclude(), req.getSaleLvInclude(), req.getDisplayLvInclude(), req.getShopIdInclude(),
                null, null, null, null, null,
                req.getRegionExclude(), req.getProvinceExclude(), req.getCityExclude(), req.getSaleLvExclude(), req.getDisplayLvExclude(), req.getShopIdExclude(),
                null, null, null, null, null);
//        req = new NewGoodsIssueRangeReq();
//        req.setMatCode(newGoodsIssueRangeDO.getMatCode());
//        req.setSizeId(newGoodsIssueRangeDO.getSizeId());
//        if (includeFlag == 1) {
//            req.setShopIdInclude(newGoodsIssueRangeDO.getSizeId());
//        } else {
//
//        }
//
//        newGoodsRangeDOMapper.updateByMatCodeSizeId(req);
        Map<String, String> shopIdNameMap = globalConfigRuleService.getShopIdMap();
        Map<String, String> shopIdMap = new HashMap<>(shopApplyMap.keySet().size());
        String shopName;
        for (String shopId : shopApplyMap.keySet()) {
            shopName = shopIdNameMap.get(shopId);
            if (shopName != null) {
                shopIdMap.put(shopId, shopName);
            }
        }
        newGoodsIssueRangeDO.setShopIdMapExclude(shopIdMap);
        return ResultUtil.handleSuccessReturn(shopIdMap);
    }

    public static void main(String[] args) {
        ShopDisplayDesignData shopDisplayDesignData = new ShopDisplayDesignData();
        Optional<String> fullName = Optional.ofNullable("");
        shopDisplayDesignData.setShopId(Optional.ofNullable(shopDisplayDesignData).map(ShopDisplayDesignData::getCategoryName).orElse(""));
    }
}
