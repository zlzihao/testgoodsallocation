package cn.nome.saas.allocation.service.old.allocation.impl;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.repository.dao.allocation.GoodsInfoDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import cn.nome.saas.allocation.repository.old.allocation.dao.IssueRestDOMapper2;
import cn.nome.saas.allocation.utils.old.BizException;
import cn.nome.saas.allocation.utils.old.ExcelUtil2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
public class ImportExportService2 {

    //日期时间格式长度20190706
    private int BAT_TAB_SUFFIX_LEN_8 = 8;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IssueRestDOMapper2 issueRestDOMapper2;
    @Autowired
    private GoodsInfoDOMapper goodsInfoDOMapper;

    /**
     * 导出陈列分类
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportDisplayCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<DisplayData> displayData = issueRestDOMapper2.displayData();
        ExcelUtil2.exportDisplayCategory(displayData, request, response);
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
        ExcelUtil2.exportGoodsInfoData(goodsInfoData, request, response);
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
        List<ShopInfoData> shopInfoData = issueRestDOMapper2.shopInfoData();
        ExcelUtil2.exportShopInfoData(shopInfoData, request, response);
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
        List<ShopDisplayData> shopDisplayData = issueRestDOMapper2.shopDisplayData();
        ExcelUtil2.exportShopDisplayData(shopDisplayData, request, response);
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
            importData = ExcelUtil2.readShopInfoData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        checkImportDataNull(importData);

        List<String> shopCodes = new ArrayList<>(importData.size());
        importData.stream().forEach(data -> shopCodes.add(data.getShopCode()));
        List<DwsDimShopDo> dwsDimShopData = getShopId(shopCodes);

        checkDimShopDataNull(dwsDimShopData);
        Map<String, String> map = new HashMap<>(dwsDimShopData.size());
        dwsDimShopData.stream().forEach(data -> map.put(data.getShopCode(), data.getShopID()));
        importData.stream().forEach(data -> data.setShopID(map.get(data.getShopCode()) == null ? "" : map.get(data.getShopCode())));

        String batTableName = getBatTableName(Constant.TAB_SHOP_INFO);
        boolean doflag = existsTable(batTableName);
        logger.debug("{} existsTable:{}", batTableName, doflag);

        doflag = batTabData(batTableName, doflag);
        // batch import data 2 table
        if (doflag) {
            String oldTab = getSubTabName(batTableName);
            doflag = truncateTable(oldTab);
            logger.warn("{} truncateTable:{}", oldTab, doflag);
            if (doflag) {
                batchInsertShopInfoTab(importData);
            }
        }
        return ResultUtil.handleSuccessReturn();
    }

    private void checkDimShopDataNull(List<DwsDimShopDo> dwsDimShopData) {
        if (dwsDimShopData == null || dwsDimShopData.isEmpty()) {
            throw new BizException(Constant.GET_SHOPID_NULL);
        }
    }

    private void checkFileType(String fileName) {
        if (!isExcel(fileName)) {
            throw new BizException(Constant.FILE_TYPE_ERROR);
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
        int rst = issueRestDOMapper2.batchInsertShopInfoTab(importData);
        logger.warn("batchInsertShopInfoTab:{}", rst);
        checkBatchInsert(rst);
        return rst;
    }

    private void checkBatchInsert(int rst) {
        if (rst <= 0) {
            throw new BizException(Constant.BATCH_INSERT_ERR);
        }
    }

    private List<DwsDimShopDo> getShopId(List<String> shopCodes) {
        List<DwsDimShopDo> dwsDimShops = issueRestDOMapper2.getShopIdByCode(shopCodes);
        return dwsDimShops;
    }

    private boolean selectAndInsert(String batTableName) {
        String tableName = getSubTabName(batTableName);
        try {
            issueRestDOMapper2.selectAndInsert(batTableName, tableName);
        } catch (Exception e) {
            logger.error("selectAndInsert err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean createAndCopyTab(String batTableName) {
        String tableName = getSubTabName(batTableName);
        try {
            issueRestDOMapper2.createAndCopyTab(batTableName, tableName);
        } catch (Exception e) {
            logger.error("createAndCopyTab err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean truncateTable(String tableName) {
        try {
            issueRestDOMapper2.truncateTable(tableName);
        } catch (Exception e) {
            logger.error("truncateTable err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean existsTable(String tableName) {
        Map tabList = issueRestDOMapper2.showTableExists(tableName);
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

        List<DisplayData> importData = null;
        try {
            importData = ExcelUtil2.readDisplayData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        Assert.isTrue(importData != null && !importData.isEmpty(), Constant.NO_DATA_IMPORT);

        String batTableName = getBatTableName(Constant.TAB_DISPLAY);
        boolean doflag = existsTable(batTableName);
        logger.debug("{} existsTable:{}", batTableName, doflag);

        doflag = batTabData(batTableName, doflag);
        // batch import data 2 table
        if (doflag) {
            String oldTab = getSubTabName(batTableName);
            doflag = truncateTable(oldTab);
            logger.warn("{} truncateTable:{}", oldTab, doflag);
            if (doflag) {
                batchInsertDisplayTab(importData);
            }
        }
        return ResultUtil.handleSuccessReturn();
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
            throw new BizException(Constant.BAK_TAB_ERR);
        }
        return existsTable;
    }

    private int batchInsertDisplayTab(List<DisplayData> importData) {
        int rst = issueRestDOMapper2.batchInsertDisplayTab(importData);
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

        List<GoodsInfoData> importData = null;
        try {
            importData = ExcelUtil2.readDisplayGoodsData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

        checkImportDataNull(importData);

        String batTableName = getBatTableName(Constant.TAB_GOODS_INFO);
        boolean doflag = existsTable(batTableName);
        logger.debug("{} existsTable:{}", batTableName, doflag);

        doflag = batTabData(batTableName, doflag);
        // batch import data 2 table
        if (doflag) {
            String oldTab = getSubTabName(batTableName);
            doflag = truncateTable(oldTab);
            logger.warn("{} truncateTable:{}", oldTab, doflag);
            if (doflag) {
                batchInsertGoodsInfoTab(importData);
            }
        }
        return ResultUtil.handleSuccessReturn();
    }

    private String getBatTableName(String tabName) {
        return tabName + DateUtil.format(new Date(), Constant.DATE_PATTERN_2);
    }

    private int batchInsertGoodsInfoTab(List<GoodsInfoData> importData) {
        int rst = issueRestDOMapper2.batchInsertGoodsInfoTab(importData);
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

        List<ShopDisplayData> importData = null;
        try {
            importData = ExcelUtil2.readShopDisplayData(fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
        checkImportDataNull(importData);

        List<String> shopCodes = new ArrayList<>(importData.size());
        importData.stream().forEach(data -> shopCodes.add(data.getShopCode()));
        List<DwsDimShopDo> dwsDimShopData = getShopId(shopCodes);
        checkDimShopDataNull(dwsDimShopData);

        Map<String, String> map = new HashMap<>(dwsDimShopData.size());
        dwsDimShopData.stream().forEach(data -> map.put(data.getShopCode(), data.getShopID()));
        importData.stream().forEach(data -> data.setShopId(map.get(data.getShopCode()) == null ? "" : map.get(data.getShopCode())));

        String batTableName = getBatTableName(Constant.TAB_SHOP_DISPLAY_DESIGN);
        boolean doflag = existsTable(batTableName);
        logger.debug("{} existsTable:{}", batTableName, doflag);

        doflag = batTabData(batTableName, doflag);
        // batch import data 2 table
        if (doflag) {
            String oldTab = getSubTabName(batTableName);
            doflag = truncateTable(oldTab);
            logger.warn("{} truncateTable:{}", oldTab, doflag);
            if (doflag) {
                batchInsertShopDisplayDesignTab(importData);
            }
        }
        return ResultUtil.handleSuccessReturn();
    }

    private void checkImportDataNull(List<?> importData) {
        if (importData == null || importData.isEmpty()) {
            throw new BizException(Constant.NO_DATA_IMPORT);
        }
    }

    private int batchInsertShopDisplayDesignTab(List<ShopDisplayData> importData) {
        int rst = issueRestDOMapper2.batchInsertShopDisplayDesignTab(importData);
        logger.warn("batchInsertShopDisplayDesignTab:{}", rst);
        checkBatchInsert(rst);
        return rst;
    }
}
