package cn.nome.saas.allocation.utils;


import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.AllocationDetailRecord;
import cn.nome.saas.allocation.model.allocation.ImportNewGoodsIssueRangeDo;
import cn.nome.saas.allocation.model.allocation.Task;
import cn.nome.saas.allocation.model.issue.*;
import cn.nome.saas.allocation.model.issue.ShopDisplayDesignData;
import cn.nome.saas.allocation.model.old.allocation.NewGoodsIssueRangeReq;
import cn.nome.saas.allocation.model.old.issue.MatCategoryDetailVo;
import cn.nome.saas.allocation.model.old.issue.MatDetailTree;
import cn.nome.saas.allocation.model.rule.UploadDetailData;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import io.undertow.security.idm.Account;
import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import io.undertow.security.idm.Account;
import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import io.undertow.security.idm.Account;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    //浏览器下载excel
    public static void downloadExcel(String fileName, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        setResponseParam(fileName, request, response);

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    //浏览器下载excel
    public static void downloadExcel2007(String fileName, XSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        setResponseParam(fileName, request, response);

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    //生成excel文件
    public static void buildExcelFile(String filename, HSSFWorkbook workbook) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();
    }

    //创建表头
    public static void createTitle(HSSFWorkbook workbook, HSSFRow row, List<String> cellValList) {
        //设置为居中加粗
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        HSSFCell cell;

        for (int nm = 0; nm < cellValList.size(); nm++) {
            cell = row.createCell(nm);
            cell.setCellValue(cellValList.get(nm));
            cell.setCellStyle(style);
        }
    }

    //创建表头
    private static void createTitle2007(XSSFWorkbook workbook, XSSFRow row, List<String> cellValList) {
        //设置为居中加粗
        XSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        XSSFCell cell;

        for (int nm = 0; nm < cellValList.size(); nm++) {
            cell = row.createCell(nm);
            cell.setCellValue(cellValList.get(nm));
            cell.setCellStyle(style);
        }
    }

    /**
     * 设置日期格式
     *
     * @param workbook
     * @return
     */
    public static HSSFCellStyle setCellStyleDateFormat(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat(Constant.DATE_PATTERN_1));
        return style;
    }

    private static XSSFCellStyle setCellStyleDateFormat2007(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFDataFormat fmt = workbook.createDataFormat();
        style.setDataFormat(fmt.getFormat(Constant.DATE_PATTERN_1));
        return style;
    }

    public static void exportOrderDetail(String fileName, List<OrderDetailVo> rows, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "配补明细";
        List<String> title = new ArrayList<>();
        title.add("商品代码");
        title.add("商品名称");
        title.add("商品小类");
        title.add("零售价");
        title.add("尺码");

        title.add("门店库存");
        title.add("分仓库存");
        title.add("配货规格");
        title.add("7日均销");
        title.add("可售天数");
        title.add("建议数量");
        title.add("订货数量(个/件)");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 40 * 256);
        sheet.setColumnWidth(2, 35 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(9, 15 * 256);
        sheet.setColumnWidth(10, 15 * 256);
        sheet.setColumnWidth(11, 20 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //新增数据行，并且设置单元格数据
        int rowNum = 1;
        for (OrderDetailVo vo : rows) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getMatCode());
            row.createCell(1).setCellValue(vo.getMatName());
            row.createCell(2).setCellValue(vo.getSmallCategoryName());
            row.createCell(3).setCellValue(vo.getQuotePrice() == null ? 0 : new BigDecimal(vo.getQuotePrice()).doubleValue());
            row.createCell(4).setCellValue(vo.getSizeName());

            row.createCell(5).setCellValue(StringUtils.isBlank(vo.getInStockQty()) ? 0 : new BigDecimal(vo.getInStockQty()).doubleValue());
            row.createCell(6).setCellValue(StringUtils.isBlank(vo.getOutStockQty()) ? 0 : new BigDecimal(vo.getOutStockQty()).doubleValue());
            row.createCell(7).setCellValue(vo.getMinPackageQty());
            row.createCell(8).setCellValue(vo.getAvgSaleQty() == null ? 0 : vo.getAvgSaleQty().doubleValue());
            row.createCell(9).setCellValue(vo.getSoldDay() == null ? 0 : vo.getSoldDay().doubleValue());
            row.createCell(10).setCellValue(vo.getPackageQty() == null ? 0 : vo.getPackageQty().doubleValue());
            row.createCell(11).setCellValue(vo.getOrderPackage().multiply(new BigDecimal(vo.getMinPackageQty())).doubleValue());
            rowNum++;
        }

        //生成excel文件
        //ExcelUtil.buildExcelFile(fileName, workbook);

        //浏览器下载excel
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportOrderList(List<OrderListVo> rows, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "配补单";
        String fileName = "配补单-" + DateUtil.format(new Date(), Constant.DATE_PATTERN_3) + ".xls";

        List<String> title = new ArrayList<>();
        title.add("门店编号");
        title.add("门店名称");
        title.add("大区");
        title.add("小区");
        title.add("城市");

        title.add("本次配补货量");
        title.add("本次配补货值");

        title.add("配货时间");
        title.add("生成时间");

        title.add("在途天数");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 35 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 20 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //新增数据行，并且设置单元格数据
        int rowNum = 1;
        for (OrderListVo vo : rows) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopId());
            row.createCell(1).setCellValue(vo.getShopName());
            row.createCell(2).setCellValue(vo.getRegioneName());
            row.createCell(3).setCellValue(vo.getSubRegoneName());
            row.createCell(4).setCellValue(vo.getCityName());

            row.createCell(5).setCellValue(vo.getPackageQty() == null ? 0 : vo.getPackageQty().doubleValue());
            row.createCell(6).setCellValue(vo.getPackageVal() == null ? 0 : vo.getPackageVal().doubleValue());

            row.createCell(7).setCellValue(vo.getIssueTime());
            row.createCell(8).setCellValue(vo.getCreatedAt());

            row.createCell(9).setCellValue(vo.getOnRoadDay());
            rowNum++;
        }

        //生成excel文件
        //ExcelUtil.buildExcelFile(fileName, workbook);

        //浏览器下载excel
        downloadExcel(fileName, workbook, request, response);
    }

    public static String downloadTemplate(String tempalteDir, String fileName, HttpServletRequest request, HttpServletResponse response) {

        String tips = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {

            setResponseParam(fileName, request, response);

            OutputStream out = response.getOutputStream();

            File file = new File(tempalteDir + fileName);
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tips = "下载失败:" + e.getMessage();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                tips = "下载失败:" + e.getMessage();
            }
        }
        return tips;
    }

    public static String downloadFile(String tempalteDir, String ruleName, String startDate, String endDate, String typeName, Boolean syncFileFlag, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

        String tips = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File file;FileInputStream fis;
        String fileName;
        try {
            fileName = ruleName.trim() + "-" + startDate.trim() + "-" + endDate.trim() + "-" + typeName + ".xlsx";
            file = new File(tempalteDir + fileName);
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (BusinessException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("12000", e.getMessage());
        }

        try {
            if (syncFileFlag) {
                fileName = "由'" + ruleName + "'生成的白名单"  + "-" + startDate.trim() + "-" + endDate.trim() + "-" + typeName + ".xlsx";;
            }
            setResponseParam(fileName, request, response);

            OutputStream out = response.getOutputStream();


            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tips = "下载失败:" + e.getMessage();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                tips = "下载失败:" + e.getMessage();
            }
        }
        return tips;
    }

    private static void setResponseParam(String fileName, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
            // firefox浏览器
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
            // IE浏览器
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else if (request.getHeader("User-Agent").toUpperCase().indexOf("CHROME") > 0) {
            // 谷歌
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    }

    public static void exportDisplayCategory(List<DisplayDo> displayData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "陈列类别配置";
        String fileName = "陈列类别配置.xls";

        //陈列大类	陈列中类	陈列小类	中类单版陈列量	小类单版陈列量	坑位列数	坑位行数	坑位深度	数量权重
        List<String> title = new ArrayList<>();
        title.add("陈列大类");
        title.add("陈列中类");
        title.add("陈列小类");
        title.add("中类单版陈列量");
        title.add("小类单版陈列量");
        title.add("坑位列数");
        title.add("坑位行数");
        title.add("坑位深度");
        title.add("数量权重");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //陈列大类	陈列中类	陈列小类	中类单版陈列量	小类单版陈列量	坑位列数	坑位行数	坑位深度	数量权重
        int rowNum = 1;
        for (DisplayDo vo : displayData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getCategoryName());
            row.createCell(1).setCellValue(vo.getMidCategoryName());
            row.createCell(2).setCellValue(vo.getSmallCategoryName());
            row.createCell(3).setCellValue(vo.getMidDisplayDepth() == null ? 0 : vo.getMidDisplayDepth().doubleValue());
            row.createCell(4).setCellValue(vo.getDisplayDepth() == null ? 0 : vo.getDisplayDepth().doubleValue());
            row.createCell(5).setCellValue(vo.getColumns() == null ? 0 : vo.getColumns().doubleValue());
            row.createCell(6).setCellValue(vo.getRows() == null ? 0 : vo.getRows().doubleValue());
            row.createCell(7).setCellValue(vo.getDepth() == null ? 0 : vo.getDepth().doubleValue());
            row.createCell(8).setCellValue(vo.getQtyWeight() == null ? 0 : vo.getQtyWeight().doubleValue());
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportGoodsInfoData(List<GoodsInfoDO> goodsInfoData, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "商品陈列配置";
        String fileName = "商品陈列配置.xls";

        //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘
        List<String> title = new ArrayList<>();
        title.add("货品ID");
        title.add("货号");
        title.add("货品简称");
        title.add("陈列大类");
        title.add("陈列中类");
        title.add("陈列小类");
        title.add("中包装数");
        title.add("货区");
        title.add("货盘");
        title.add("是否易碎品");
        title.add("单号");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 25 * 256);
        sheet.setColumnWidth(2, 35 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 30 * 256);
        sheet.setColumnWidth(5, 30 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(9, 15 * 256);
        sheet.setColumnWidth(10, 15 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘	是否易碎品
        int rowNum = 1;
        for (GoodsInfoDO vo : goodsInfoData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getMatId() == null ? "" : vo.getMatId());
            row.createCell(1).setCellValue(vo.getMatCode() == null ? "" : vo.getMatCode());
            row.createCell(2).setCellValue(vo.getMatName() == null ? "" : vo.getMatName());
            row.createCell(3).setCellValue(vo.getCategoryName() == null ? "" : vo.getCategoryName());
            row.createCell(4).setCellValue(vo.getMidCategoryName() == null ? "" : vo.getMidCategoryName());
            row.createCell(5).setCellValue(vo.getSmallCategoryName() == null ? "" : vo.getSmallCategoryName());
            row.createCell(6).setCellValue(vo.getMinPackageQty() == null ? 0 : vo.getMinPackageQty());
            row.createCell(7).setCellValue(vo.getArea() == null ? "" : vo.getArea());
            row.createCell(8).setCellValue(vo.getLevel() == null ? "" : vo.getLevel());
            row.createCell(9).setCellValue(vo.getIsAllocationProhibited() == null ? "" : vo.getIsAllocationProhibited() == 1 ? "是" : "否");
            row.createCell(10).setCellValue(vo.getOrderNo() == null ? "" : vo.getOrderNo());
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportShopInfoData(List<ShopInfoData> shopInfoData, String [] attrKeys, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "门店信息配置";
        String fileName = "门店信息配置.xls";

        //门店代码	商品管理员ID	货盘区域	商品管理员	配发周期 安全天数	门店等级	儿童是否单独陈列	男装货盘	女装货盘	百货货盘	物流天数
        List<String> title = new ArrayList<>();
        title.add("门店代码");
        title.add("商品管理员ID");
        title.add("货盘区域");
        title.add("商品管理员");
        title.add("到货时间");
        title.add("安全天数");
        title.add("门店等级");
        title.add("上限天数");
        title.add("儿童是否单独陈列");
        title.add("男装货盘");
        title.add("女装货盘");
        title.add("百货货盘");
        title.add("物流天数");
//        title.add("补货周期间隔天数");

        title.add("百货仓位");
        title.add("服装仓位");
        title.add("店铺状态");
        title.add("端头仓位数");
        title.add("是否有彩妆台");
        title.add("是否有文具台");
        //门店自定义属性
        title.addAll(Arrays.asList(attrKeys));
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 25 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(9, 15 * 256);
        sheet.setColumnWidth(10, 15 * 256);
        sheet.setColumnWidth(11, 15 * 256);

        sheet.setColumnWidth(12, 15 * 256);
        sheet.setColumnWidth(13, 15 * 256);
        sheet.setColumnWidth(14, 15 * 256);
        sheet.setColumnWidth(15, 15 * 256);
        sheet.setColumnWidth(16, 15 * 256);
        sheet.setColumnWidth(17, 15 * 256);
        sheet.setColumnWidth(18, 15 * 256);
        sheet.setColumnWidth(19, 15 * 256);
        sheet.setColumnWidth(20, 15 * 256);
        sheet.setColumnWidth(21, 15 * 256);
        sheet.setColumnWidth(22, 15 * 256);
        sheet.setColumnWidth(23, 15 * 256);
//        sheet.setColumnWidth(22, 15 * 256);


        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //门店代码	商品管理员ID	货盘区域	商品管理员	配发周期	安全天数 门店等级	儿童是否单独陈列	男装货盘	女装货盘	百货货盘	物流天数
        int rowNum = 1;
        for (ShopInfoData vo : shopInfoData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopCode());
            row.createCell(1).setCellValue(vo.getUserId());
            row.createCell(2).setCellValue(vo.getGoodsArea());
            row.createCell(3).setCellValue(vo.getUserName());
            row.createCell(4).setCellValue(vo.getIssueTime());
            // 安全天数
            row.createCell(5).setCellValue(vo.getSafeDay().intValue());

            row.createCell(6).setCellValue(vo.getShopLevel());
            row.createCell(7).setCellValue(vo.getMaxDays());
            row.createCell(8).setCellValue(vo.getHaveChild() == 1 ? "是" : "否");
            row.createCell(9).setCellValue(vo.getMenLevel());
            row.createCell(10).setCellValue(vo.getWomenLevel());
            row.createCell(11).setCellValue(vo.getCommodityLevel());
            row.createCell(12).setCellValue(vo.getRoadDay());
//            row.createCell(11).setCellValue(vo.getIssueDay());

            row.createCell(13).setCellValue(vo.getCommoditySpace() == null ? 0 : vo.getCommoditySpace().doubleValue());
            row.createCell(14).setCellValue(vo.getClothSpace() == null ? 0 : vo.getClothSpace().doubleValue());
            row.createCell(15).setCellValue(vo.getStatus() == null ? "" : ShopInfoData.ShopStatus.getStatusName(vo.getStatus()));
            row.createCell(16).setCellValue(vo.getSheetHeadSpaceNum() == null ? 0 : vo.getSheetHeadSpaceNum().doubleValue());
            row.createCell(17).setCellValue(vo.getCosmeticsTable() == null ? "" : vo.getCosmeticsTable() == 1 ? "是" : "否");
            row.createCell(18).setCellValue(vo.getStationeryTable() == null ? "" : vo.getStationeryTable() == 1 ? "是" : "否");
            row.createCell(19).setCellValue(vo.getAttrFirVal() == null ? "" : vo.getAttrFirVal());
            row.createCell(20).setCellValue(vo.getAttrSecVal() == null ? "" : vo.getAttrSecVal());
            row.createCell(21).setCellValue(vo.getAttrThiVal() == null ? "" : vo.getAttrThiVal());
            row.createCell(22).setCellValue(vo.getAttrFourVal() == null ? "" : vo.getAttrFourVal());
            row.createCell(23).setCellValue(vo.getAttrFifVal() == null ? "" : vo.getAttrFifVal());

            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    //导出门店调仓申请
    public static void exportShopToStockData(List<ShopToStockDo>  shopToStockData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "调仓申请";
        String fileName = "调仓申请.xls";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("订单编号");
        title.add("门店代码");
        title.add("门店名称");
        title.add("申请人");
        title.add("申请日期");
        title.add("调整理由");
        title.add("大类名称");
        title.add("中类名称");
        title.add("原仓位数");
        title.add("新仓位数");
        title.add("是否审核");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(9, 15 * 256);
        sheet.setColumnWidth(10, 15 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //门店代码	门店名称	申请人	申请日期	调整理由 大类名称 中类名称 原仓位数 新仓位数 是否审核
        int rowNum = 1;
        for (ShopToStockDo vo : shopToStockData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getOrderNo() == null ? "" : vo.getOrderNo());
            row.createCell(1).setCellValue(vo.getShopCode() == null ? "" : vo.getShopCode());
            row.createCell(2).setCellValue(vo.getShopName() == null ? "" : vo.getShopName());
            row.createCell(3).setCellValue(vo.getUserName() == null ? "" : vo.getUserName());
            row.createCell(4).setCellValue(vo.getDate() == null ? "" : vo.getDate().toString());
            row.createCell(5).setCellValue(vo.getReason() == null ? "" : vo.getReason());
            row.createCell(6).setCellValue(vo.getCategoryName() == null ? "" : vo.getCategoryName());
            row.createCell(7).setCellValue(vo.getMidCategoryName() == null ? "" : vo.getMidCategoryName());
            row.createCell(8).setCellValue(vo.getOldStockNum() == null ? 0 : vo.getOldStockNum().intValue());
            row.createCell(9).setCellValue(vo.getNewStockNum() == null ? 0 : vo.getNewStockNum().intValue());
            row.createCell(10).setCellValue(vo.getStatus() == 0 ? "未审核" : "已审核");
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }
    public static void exportShopDisplayData(List<ShopDisplayDesignData> shopDisplayData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "门店陈列配置";
        String fileName = "门店陈列配置.xls";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("门店代码");
        title.add("陈列大类");
        title.add("陈列中类");
        title.add("陈列饱满度");
        title.add("仓位数");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        int rowNum = 1;
        for (ShopDisplayDesignData vo : shopDisplayData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopCode() == null ? "" : vo.getShopCode());
            row.createCell(1).setCellValue(vo.getCategoryName() == null ? "" : vo.getCategoryName());
            row.createCell(2).setCellValue(vo.getMidCategoryName() == null ? "" : vo.getMidCategoryName());
            row.createCell(3).setCellValue(vo.getDisplayPercent() == null ? 0 : vo.getDisplayPercent().doubleValue());
            row.createCell(4).setCellValue(vo.getDisplay_Qty() == null ? 0 : vo.getDisplay_Qty().doubleValue());
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportRangeDo(List<NewGoodsIssueRangeDO> newGoodsRangeDOS, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "新品铺货范围";
        String fileName = "新品铺货范围.xls";

        //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘
        List<String> title = new ArrayList<>();
        title.add("货号");
        title.add("商品名称");
        title.add("是否完成首配计划");
        title.add("生成时间");
        title.add("失效时间");
        //5
        title.add("修改人");
        title.add("修改时间");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 25 * 256);
        sheet.setColumnWidth(2, 35 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 30 * 256);
        sheet.setColumnWidth(5, 30 * 256);
        sheet.setColumnWidth(6, 15 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //货号	商品名称	是否完成首配计划	生成时间	失效时间	修改人	修改时间
        int rowNum = 1;
        for (NewGoodsIssueRangeDO vo : newGoodsRangeDOS) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getMatCode() == null ? "" : vo.getMatCode());
            row.createCell(1).setCellValue(vo.getMatName() == null ? "" : vo.getMatName());
            row.createCell(2).setCellValue(vo.getIssueFin() == null ? "否" : vo.getIssueFin() == 1 ? "是" : "否");
//            row.createCell(3).setCellValue(vo.getCreatedAt() == null ? "" : vo.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
            row.createCell(3).setCellValue(vo.getCreatedAt() == null ? "" : DateUtil.format(vo.getCreatedAt(), DateUtil.DATE_ONLY));
//            row.createCell(4).setCellValue(vo.getInvalidAt() == null ? "" : vo.getInvalidAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
            row.createCell(4).setCellValue(vo.getInvalidAt() == null ? "" : DateUtil.format(vo.getInvalidAt(), DateUtil.DATE_ONLY));
            row.createCell(5).setCellValue(vo.getUpdatedBy() == null ? "" : vo.getUpdatedBy());
//            row.createCell(6).setCellValue(vo.getUpdatedAt() == null ? "" : vo.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
            row.createCell(6).setCellValue(vo.getUpdatedAt() == null ? "" : DateUtil.format(vo.getUpdatedAt(), DateUtil.DATE_ONLY));
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportRangeDetailDo(List<NewGoodsIssueRangeDetailDO> newGoodsIssueRangeDetailDOS, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "首配计划";
        String fileName = "首配计划.xls";

        //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘
        List<String> title = new ArrayList<>();
        title.add("货号");
        title.add("尺码名称");
        title.add("门店名称");
        title.add("配发数量");
        title.add("上市时间");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 25 * 256);
        sheet.setColumnWidth(2, 25 * 256);
        sheet.setColumnWidth(3, 25 * 256);
        sheet.setColumnWidth(4, 25 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //门店ID	配发数量
        int rowNum = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (NewGoodsIssueRangeDetailDO vo : newGoodsIssueRangeDetailDOS) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getMatCode() == null ? "" : vo.getMatCode());
            row.createCell(1).setCellValue(vo.getSizeName() == null ? "" : vo.getSizeName());
            row.createCell(2).setCellValue(vo.getShopName() == null ? "" : vo.getShopName());
            row.createCell(3).setCellValue(vo.getNum() == null ? "0" : vo.getNum().toString());
            row.createCell(4).setCellValue(vo.getSaleTime() == null ? "" : sdf.format(vo.getSaleTime()));
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportRangeShop(Set<String> shopIds, Integer includeFlag, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "新品铺货门店" + (includeFlag == 1 ? "应用" : "排除");
        String fileName = "新品铺货门店" + (includeFlag == 1 ? "应用" : "排除") + ".xls";

        //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘
        List<String> title = new ArrayList<>();
        title.add("门店编码");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 25 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //货号	商品名称	是否完成首配计划	生成时间	失效时间	修改人	修改时间
        int rowNum = 1;
        for (String str : shopIds) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(str);
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }




    private static int getInt(double number) {
        BigDecimal bd = new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return bd.intValue();
    }

    private static Workbook getWkb(String fileName, InputStream is) throws IOException {
        Workbook wb = null;
        boolean isExcel2003 = isExcel2003(fileName);
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        return wb;
    }

    private static boolean isExcel2003(String fileName) {
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        return isExcel2003;
    }

    /**
     * 大数量excel获取方法
     * @param file
     * @return
     * @throws Exception
     */
    public static List<UploadDetailData> readForbiddenDetailData(MultipartFile file)throws Exception  {
        // 考虑到excelindex不一定都是设置为1，所以给一个范围值(1~5)
        for (int index = 1;index <= 5; index++) {
            List<UploadDetailData> list = XlsxProcessUtils.processAllSheet(file.getInputStream(), index, UploadDetailData.class, data -> data.getShopCode() != null);
            if (CollectionUtils.isNotEmpty(list)) {
                return list;
            }
        }

        return null;
    }

    public static void exportAllocationDetailData(TaskDO taskDO, List<AllocationDetailRecord> list, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "调拨明细";
        String fileName = taskDO.getTaskName() + DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY)+".xlsx";

        String title = "大类,中类,小类,商品名称,零售价,年份,季节,货号,颜色,是否缺码,尺码名称,易碎标示," +
                "最小陈列量,调出门店代码,调出门店名称,调出门店等级,调出店-是否禁配,调出店日均销,调出店当前库存";
        title += ",调出店安全天数,调入门店代码,调入门店名称,调入门店等级,调入店-是否禁配,调入店日均销量,调入店需求数量,调入店当前库存,调入店在途库存,调入店在配库存," +
                    "申请调拨数量,调入店安全天数,申请调拨金额";


        List<String> titleList = Stream.of(title.split(",")).collect(Collectors.toList());

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        for (int index = 0;index<titleList.size();index++) {
            sheet.setColumnWidth(index, 20 * 256);
        }

        XSSFRow titleRow = sheet.createRow(0);
        createTitle2007(workbook, titleRow, titleList);

        int rowNum = 1;
        for (AllocationDetailRecord vo : list) {
            XSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getCategoryName());
            row.createCell(1).setCellValue(vo.getMidCategoryName());
            row.createCell(2).setCellValue(vo.getSmallCategoryName());
            row.createCell(3).setCellValue(vo.getMatName());
            row.createCell(4).setCellValue(vo.getQuotePrice());
            row.createCell(5).setCellValue(vo.getYearNo());
            row.createCell(6).setCellValue(vo.getSeasonName());
            row.createCell(7).setCellValue(vo.getMatCode());
            row.createCell(8).setCellValue(vo.getColorName());
            row.createCell(9).setCellValue(0);
            row.createCell(10).setCellValue(vo.getSizeName());
            row.createCell(11).setCellValue(vo.getIsAllocationProhibited());
            row.createCell(12).setCellValue(vo.getMinDisplayQty());
            row.createCell(13).setCellValue(vo.getSupplyShopCode());
            row.createCell(14).setCellValue(vo.getSupplyShopName());
            row.createCell(15).setCellValue(vo.getSupplyShopLevel());
            row.createCell(16).setCellValue(vo.getSupplyForbiddenFlag());
            //row.createCell(17).setCellValue(vo.getSupply28SalesQty());
            row.createCell(17).setCellValue(vo.getSupplyAvgSalesQty());
            row.createCell(18).setCellValue(vo.getSupplyStockQty());


            if (taskDO.getAllocationType() != Task.ALLOCATION_TYPE_REJECT) {
                row.createCell(19).setCellValue(taskDO.getOutDays());
                row.createCell(20).setCellValue(vo.getDemandShopCode());
                row.createCell(21).setCellValue(vo.getDemandShopName());
                row.createCell(22).setCellValue(vo.getDemandShopLevel());
                row.createCell(23).setCellValue(vo.getDemandForbiddenFlag());
                //row.createCell(25).setCellValue(vo.getDemand28SalesQty());
                row.createCell(24).setCellValue(vo.getDemandAvgSalesQty());
                row.createCell(25).setCellValue(vo.getDemandQty());
                row.createCell(26).setCellValue(vo.getDemandSotckQty());
                row.createCell(27).setCellValue(vo.getDemandPathQty());
                row.createCell(28).setCellValue(vo.getDemandApplyQty());
                row.createCell(29).setCellValue(vo.getAllocationQty());
                row.createCell(30).setCellValue(taskDO.getInDays());
                row.createCell(31).setCellValue(vo.getAllocationAmount());
            } else {
                if (vo.getDemandShopName().equals("总仓")) {
                    row.createCell(19).setCellValue("");
                    row.createCell(20).setCellValue(vo.getDemandShopCode());
                    row.createCell(21).setCellValue(vo.getDemandShopName());
                    row.createCell(22).setCellValue("");
                    row.createCell(23).setCellValue("");
                    //row.createCell(25).setCellValue("");
                    row.createCell(24).setCellValue("");
                    row.createCell(25).setCellValue("");
                    row.createCell(26).setCellValue("");
                    row.createCell(27).setCellValue("");
                    row.createCell(28).setCellValue("");
                    row.createCell(29).setCellValue(vo.getAllocationQty());
                    row.createCell(30).setCellValue("");
                    row.createCell(31).setCellValue(vo.getAllocationAmount());
                } else {
                    row.createCell(19).setCellValue(taskDO.getOutDays());
                    row.createCell(20).setCellValue(vo.getDemandShopCode());
                    row.createCell(21).setCellValue(vo.getDemandShopName());
                    row.createCell(22).setCellValue(vo.getDemandShopLevel());
                    row.createCell(23).setCellValue(vo.getDemandForbiddenFlag());
                    //row.createCell(25).setCellValue(vo.getDemand28SalesQty());
                    row.createCell(24).setCellValue(vo.getDemandAvgSalesQty());
                    row.createCell(25).setCellValue(vo.getDemandQty());
                    row.createCell(26).setCellValue(vo.getDemandSotckQty());
                    row.createCell(27).setCellValue(vo.getDemandPathQty());
                    row.createCell(28).setCellValue(vo.getDemandApplyQty());
                    row.createCell(29).setCellValue(vo.getAllocationQty());
                    row.createCell(30).setCellValue(taskDO.getInDays());
                    row.createCell(31).setCellValue(vo.getAllocationAmount());
                }
            }

            rowNum++;
        }

        downloadExcel2007(fileName, workbook, request, response);

    }

    public static void exportForbiddenDetailData(String ruleName,List<ForbiddenSingleItemDO> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "禁配明细";
        String fileName = ruleName + DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY)+".xlsx";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("门店代码");
        title.add("类型");
        title.add("类型值");
        title.add("开始日期");
        title.add("结束日期");
        title.add("备注");
        title.add("最后修改人");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 10 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);

        XSSFRow titleRow = sheet.createRow(0);
        createTitle2007(workbook, titleRow, title);
        XSSFCellStyle style = setCellStyleDateFormat2007(workbook);

        int rowNum = 1;
        for (ForbiddenSingleItemDO vo : list) {
            XSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopCode());
            row.createCell(1).setCellValue(vo.getTypeName());
            row.createCell(2).setCellValue(vo.getTypeValue());

            XSSFCell startCell =row.createCell(3);
            startCell.setCellStyle(style);
            startCell.setCellValue(vo.getStartDate());
            XSSFCell endCell = row.createCell(4);
            endCell.setCellStyle(style);
            endCell.setCellValue(vo.getEndDate());
            row.createCell(5).setCellValue(vo.getRemark());
            row.createCell(6).setCellValue(vo.getModifiedBy());
            rowNum++;
        }

        downloadExcel2007(fileName, workbook, request, response);
    }

    public static void exportForbiddenSingleRuleData(String shopName,List<ForbiddenSingleItemDO> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "单店禁配明细";
        String fileName = "单店禁配明细" + DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY)+".xlsx";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("门店代码");
        title.add("门店名称");
        title.add("类型");
        title.add("对象");
        title.add("开始日期");
        title.add("结束日期");
        title.add("备注");
        title.add("创建人");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);

        XSSFRow titleRow = sheet.createRow(0);
        createTitle2007(workbook, titleRow, title);
        XSSFCellStyle style = setCellStyleDateFormat2007(workbook);

        int rowNum = 1;int type;
        for (ForbiddenSingleItemDO vo : list) {
            XSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopCode());
            row.createCell(1).setCellValue(vo.getShopName());
            type = vo.getType();
            if (type == 1) {
                row.createCell(2).setCellValue("大类");
            } else  if (type == 2) {
                row.createCell(2).setCellValue("中类");
            } else  if (type == 3) {
                row.createCell(2).setCellValue("小类");
            } else  if (type == 4) {
                row.createCell(2).setCellValue("skc");
            } else  if (type == 5) {
                row.createCell(2).setCellValue("sku");
            }
            row.createCell(3).setCellValue(vo.getTypeValue());

            XSSFCell startCell =row.createCell(4);
            startCell.setCellStyle(style);
            startCell.setCellValue(vo.getStartDate());
            XSSFCell endCell = row.createCell(5);
            endCell.setCellStyle(style);
            endCell.setCellValue(vo.getEndDate());
            row.createCell(6).setCellValue(vo.getRemark());
            row.createCell(7).setCellValue(vo.getCreatedBy());
            rowNum++;
        }

        downloadExcel2007(fileName, workbook, request, response);
    }

    public static void exportMatDetailData(List<MatDetailTree> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "中类汇总明细";
        String fileName = "中类汇总.xlsx";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("门店代码");
        title.add("门店名称");
        title.add("品类");
        title.add("动态需求量");
        title.add("订货后库存");
        title.add("订货数量");
        title.add("偏差数据");
        title.add("28天销量");
        title.add("7天销量");
        title.add("日均销量");

        title.add("预计周转");
        title.add("有效SKC");
        title.add("可下SKC");
        title.add("新品SKC");
        title.add("禁配SKC");
        title.add("保底SKC");
        title.add("版面数");
        title.add("单仓陈列量");
        title.add("陈列需求量");
        title.add("饱满度目标");

        title.add("订货金额");
        title.add("总库存金额");
        title.add("在店库存金额");
        title.add("总库存金额占比");
        title.add("在店库存金额占比");
        title.add("销售金额占比");
        title.add("在店库存占比");
        title.add("销售数量占比");
        title.add("订货前库存占比");
        title.add("订货后库存占比");

        title.add("在店库存数量");
        title.add("在途库存数量");
        title.add("在配库存数量");
        title.add("周转天数");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 10 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 20 * 256);
        sheet.setColumnWidth(8, 10 * 256);
        sheet.setColumnWidth(9, 20 * 256);
        sheet.setColumnWidth(10, 15 * 256);
        sheet.setColumnWidth(11, 15 * 256);
        sheet.setColumnWidth(12, 15 * 256);
        sheet.setColumnWidth(13, 15 * 256);
        sheet.setColumnWidth(14, 15 * 256);
        sheet.setColumnWidth(15, 15 * 256);
        sheet.setColumnWidth(16, 15 * 256);
        sheet.setColumnWidth(17, 15 * 256);
        sheet.setColumnWidth(18, 15 * 256);
        sheet.setColumnWidth(19, 15 * 256);
        sheet.setColumnWidth(20, 20 * 256);
        sheet.setColumnWidth(21, 20 * 256);
        sheet.setColumnWidth(22, 20 * 256);
        sheet.setColumnWidth(23, 20 * 256);
        sheet.setColumnWidth(24, 20 * 256);
        sheet.setColumnWidth(25, 20 * 256);
        sheet.setColumnWidth(26, 20 * 256);
        sheet.setColumnWidth(27, 20 * 256);
        sheet.setColumnWidth(28, 20 * 256);
        sheet.setColumnWidth(29, 20 * 256);
        sheet.setColumnWidth(30, 20 * 256);
        sheet.setColumnWidth(31, 20 * 256);
        sheet.setColumnWidth(32, 20 * 256);
        sheet.setColumnWidth(33, 20 * 256);

        XSSFRow titleRow = sheet.createRow(0);
        createTitle2007(workbook, titleRow, title);

        int rowNum = 1;
        for (MatDetailTree vo : list) {
            XSSFRow row = sheet.createRow(rowNum);

            MatCategoryDetailVo parent = vo.getParent();
            List<MatCategoryDetailVo> childList = vo.getChildList();
            addMatDetail(row,vo.getShopCode(),vo.getShopName(),parent,null);

            rowNum++;

            for (MatCategoryDetailVo child : childList) {
                XSSFRow childRow = sheet.createRow(rowNum);
                addMatDetail(childRow,vo.getShopCode(),vo.getShopName(),child,parent.getCategoryName());
                rowNum++;
            }

        }

        downloadExcel2007(fileName, workbook, request, response);

    }

    public static void exportClothingInvalidGoods(List<AllocationClothingInvalidGoods> allocationClothingInvalidGoodsList, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "非有效商品明细列表";
        String fileName = "服装调拨非有效商品明细.xls";

        List<String> title = new ArrayList<>();
        title.add("门店名称");
        title.add("商品名称");
        title.add("最小陈列量");
        title.add("matcode");
        title.add("sizeid");
        title.add("日均销");
        title.add("在店库存");
        title.add("在配库存");
        title.add("在途库存");
        title.add("可售天数");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(9, 15 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //陈列大类	陈列中类	陈列小类	中类单版陈列量	小类单版陈列量	坑位列数	坑位行数	坑位深度	数量权重
        int rowNum = 1;
        for (AllocationClothingInvalidGoods vo : allocationClothingInvalidGoodsList) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopName());
            row.createCell(1).setCellValue(vo.getMatName());
            row.createCell(2).setCellValue(vo.getMinDisplayQty());
            row.createCell(3).setCellValue(vo.getMatCode());
            row.createCell(4).setCellValue(vo.getSizeId());
            row.createCell(5).setCellValue(vo.getAvgSaleQty());
            row.createCell(6).setCellValue(vo.getStockQty());
            row.createCell(7).setCellValue(vo.getApplyStockQty());
            row.createCell(8).setCellValue(vo.getPathStockQty());
            row.createCell(9).setCellValue(vo.getSaleDays());
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    private static void addMatDetail(XSSFRow row,String shopCode,String shopName,MatCategoryDetailVo vo,String parentCategoryName) {

        row.createCell(0).setCellValue(shopCode);
        row.createCell(1).setCellValue(shopName);
        if (parentCategoryName != null) {
            row.createCell(2).setCellValue(parentCategoryName+"-"+vo.getCategoryName());
        } else {
            row.createCell(2).setCellValue(vo.getCategoryName());
        }
        row.createCell(3).setCellValue(vo.getNeedQty() != null ? vo.getNeedQty().toString() : "-");
        row.createCell(4).setCellValue(vo.getShopStock().toString());
        row.createCell(5).setCellValue(vo.getOrderQty().toString());
        row.createCell(6).setCellValue(vo.getDeviationNum().toString());
        row.createCell(7).setCellValue(vo.getSaleQty28().toString());
        row.createCell(8).setCellValue(vo.getSaleQty7().toString());
        row.createCell(9).setCellValue(vo.getAvgSaleQty().toString());

        row.createCell(10).setCellValue(vo.getTurnOverEstimate().toString());
        row.createCell(11).setCellValue(vo.getValidSkcCount().toString());
        row.createCell(12).setCellValue(vo.getCanSkcCount().toString());
        row.createCell(13).setCellValue(vo.getNewSkcCount().toString());
        row.createCell(14).setCellValue(vo.getProhibitedSkcCount().toString());
        row.createCell(15).setCellValue(vo.getKeepSkcCount().toString());
        row.createCell(16).setCellValue(vo.getDisplayQty() != null ? vo.getDisplayQty().toString() : "-");
        row.createCell(17).setCellValue(vo.getDisplayDepth() != null ? vo.getDisplayDepth().toString() : "-");
        row.createCell(18).setCellValue(vo.getDisplayNeedQty() != null ? vo.getDisplayNeedQty().toString() : "-");
        row.createCell(19).setCellValue(vo.getDisplayPercent() != null ? vo.getDisplayPercent().toString() : "-");

        row.createCell(20).setCellValue(vo.getOrderAmt() != null ? vo.getOrderAmt().toString() : "-");
        row.createCell(21).setCellValue(vo.getTotalStockAmt() != null ? vo.getTotalStockAmt().toString() : "-");
        row.createCell(22).setCellValue(vo.getOnlyShopStockAmtYd() != null ? vo.getOnlyShopStockAmtYd().toString() : "-");
        row.createCell(23).setCellValue(vo.getTotalStockAmtRate());
        row.createCell(24).setCellValue(vo.getOnlyShopStockQtyRate());
        row.createCell(25).setCellValue(vo.getSoldRate());
        row.createCell(26).setCellValue(vo.getShopStockAmtRate());
        row.createCell(27).setCellValue(vo.getSaleQtyRate());
        row.createCell(28).setCellValue(vo.getBeforeStockRate());
        row.createCell(29).setCellValue(vo.getAfterStockRate());

        row.createCell(30).setCellValue(vo.getStockQty().toString());
        row.createCell(31).setCellValue(vo.getPathStockQty().toString());
        row.createCell(32).setCellValue(vo.getMoveQty().toString());
        row.createCell(33).setCellValue(vo.getTurnOverDays().toString());

    }



    /**
     * 门店信息
     * @param fileName
     * @param file
     * @return
     * @throws IOException
     */
    public static List<ShopInfoData> readShopInfoData(String fileName, MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ShopInfoData> rowsData = new ArrayList<ShopInfoData>();

        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();

        String shopCode = null;
        List<String> shops = new ArrayList<>(totalRows);

        //去除标题，从1开始
        for (int i = 1; i < totalRows; i++) {

            //遍历单元格
            row = sheet.getRow(i);

            //获取每一行的单元格数
            //int totalCells = row.getPhysicalNumberOfCells();
            //logger.debug("totalCells:{}", totalCells);
            //获取每个单元格的数据，保存到集合中
            for (int t = 0; t < totalCells; t++) {
                Cell cell = row.getCell(t);
                if (cell == null) {
                    row.createCell(t).setCellType(CellType.BLANK);
                }
                //logger.debug("cellVal:{}", cell);
            }

            //门店代码	商品管理员ID	货盘区域	商品管理员	配发周期  安全天数	门店等级 上限天数	儿童是否单独陈列	男装货盘	女装货盘	百货货盘	物流天数
            row.getCell(1).setCellType(CellType.STRING);
            row.getCell(4).setCellType(CellType.STRING);
            row.getCell(7).setCellType(CellType.NUMERIC);
            row.getCell(12).setCellType(CellType.NUMERIC);
            row.getCell(13).setCellType(CellType.NUMERIC);
            row.getCell(14).setCellType(CellType.NUMERIC);
            row.getCell(16).setCellType(CellType.NUMERIC);


            //检查是否重复门店编码
            shopCode = row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue();
            if (shops.contains(shopCode)){
                throw new BusinessException(Constant.REPEAT_SHOP_CODE.replace("{0}", shopCode));
            }
            shops.add(shopCode);

            ShopInfoData data = new ShopInfoData();
            data.setShopCode(shopCode);
            data.setUserId(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
            data.setGoodsArea(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
            data.setUserName(row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue());
            data.setIssueTime(row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue());
            // 安全天数
            if (row.getCell(5) == null || !NumberUtils.isNumber(row.getCell(5).toString()) || getInt(row.getCell(5).getNumericCellValue()) <0) {
                throw new BusinessException("12000","安全天数为空或格式错误");
            }
            data.setSafeDay(getInt(row.getCell(5).getNumericCellValue()));

            data.setShopLevel(row.getCell(6) == null ? "" : row.getCell(6).getStringCellValue());
            data.setMaxDays(row.getCell(7) == null ? 0 : (int)row.getCell(7).getNumericCellValue());
            data.setHaveChild(row.getCell(8) == null ? 0 : "是".equals(row.getCell(8).getStringCellValue()) ? 1 : 0);
            data.setMenLevel(row.getCell(9) == null ? "" : row.getCell(9).getStringCellValue());
            data.setWomenLevel(row.getCell(10) == null ? "" : row.getCell(10).getStringCellValue());
            data.setCommodityLevel(row.getCell(11) == null ? "" : row.getCell(11).getStringCellValue());
            data.setDisplayLevel(row.getCell(11) == null ? "" : row.getCell(11).getStringCellValue());//陈列等级 == 百货等级
            data.setRoadDay(getInt(row.getCell(12) == null ? 0 : row.getCell(12).getNumericCellValue()));
            data.setCommoditySpace(row.getCell(13) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(13).getNumericCellValue()));
            data.setClothSpace(row.getCell(14) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(14).getNumericCellValue()));
            data.setStatus(row.getCell(15) == null ? 0 : ShopInfoData.ShopStatus.getStatus(row.getCell(15).getStringCellValue()));
            data.setSheetHeadSpaceNum(row.getCell(16) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(16).getNumericCellValue()));
            data.setCosmeticsTable(row.getCell(17) == null ? 0 : "是".equals(row.getCell(17).getStringCellValue()) ? 1 : 0);
            data.setStationeryTable(row.getCell(18) == null ? 0 : "是".equals(row.getCell(18).getStringCellValue()) ? 1 : 0);
            data.setAttrFirVal(row.getCell(19) == null ? "" : row.getCell(19).getStringCellValue());
            data.setAttrSecVal(row.getCell(20) == null ? "" : row.getCell(20).getStringCellValue());
            data.setAttrThiVal(row.getCell(21) == null ? "" : row.getCell(21).getStringCellValue());
            data.setAttrFourVal(row.getCell(22) == null ? "" : row.getCell(22).getStringCellValue());
            data.setAttrFifVal(row.getCell(23) == null ? "" : row.getCell(23).getStringCellValue());
            data.setOperator(AuthUtil.getSessionUserId());
            rowsData.add(data);

        }
        return rowsData;
    }

    /**
     * 门店陈列
     * @param fileName
     * @param file
     * @return
     * @throws IOException
     */
    public static List<ShopDisplayDesignData> readShopDisplayData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ShopDisplayDesignData> rowsData = new ArrayList<ShopDisplayDesignData>();
        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        //去除标题，从1开始
        for (int i = 1; i < totalRows; i++) {

            //遍历单元格
            row = sheet.getRow(i);

            //获取每一行的单元格数
            //int totalCells = row.getPhysicalNumberOfCells();
            //logger.debug("getPhysicalNumberOfCells:{},getLastCellNum:{}", totalCells, row.getLastCellNum());

            //获取每个单元格的数据，保存到集合中
            for (int t = 0; t < totalCells; t++) {
                Cell cell = row.getCell(t);
                if (cell == null) {
                    row.createCell(t).setCellType(CellType.BLANK);
                }
                //logger.debug("cellVal:{}", cell);
            }

            row.getCell(3).setCellType(CellType.NUMERIC);
            row.getCell(4).setCellType(CellType.NUMERIC);

            //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
            ShopDisplayDesignData data = new ShopDisplayDesignData();
            data.setShopCode(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
            data.setCategoryName(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
            data.setMidCategoryName(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
            data.setDisplayPercent(row.getCell(3) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(3).getNumericCellValue()));
            data.setDisplay_Qty(row.getCell(4) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(4).getNumericCellValue()));
            data.setOperator(AuthUtil.getSessionUserId());
            data.setRowNum(i+1);
            rowsData.add(data);
        }
        return rowsData;
    }

    public static List<DisplayDataV2> readDisplayData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<DisplayDataV2> rowsData = new ArrayList<>();
        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        //去除标题，从1开始
        for (int i = 1; i < totalRows; i++) {

            //遍历单元格
            row = sheet.getRow(i);

            //获取每一行的单元格数
            //int totalCells = row.getPhysicalNumberOfCells();

            //获取每个单元格的数据，保存到集合中
            for (int t = 0; t < totalCells; t++) {
                Cell cell = row.getCell(t);
                if (cell == null) {
                    row.createCell(t).setCellType(CellType.BLANK);
                }
                //logger.debug("cellVal:{}", cell);
            }

            //陈列大类	陈列中类	陈列小类	中类单版陈列量	小类单版陈列量	坑位列数	坑位行数	坑位深度	数量权重
            row.getCell(3).setCellType(CellType.NUMERIC);
            row.getCell(4).setCellType(CellType.NUMERIC);
            row.getCell(5).setCellType(CellType.NUMERIC);
            row.getCell(6).setCellType(CellType.NUMERIC);
            row.getCell(7).setCellType(CellType.NUMERIC);
            row.getCell(8).setCellType(CellType.NUMERIC);

            DisplayDataV2 data = new DisplayDataV2();
            data.setCategoryName(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
            data.setMidCategoryName(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
            data.setSmallCategoryName(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
            data.setMidDisplayDepth(row.getCell(3) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(3).getNumericCellValue()));
            data.setDisplayDepth(row.getCell(4) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(4).getNumericCellValue()));
            data.setColumns(row.getCell(5) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(5).getNumericCellValue()));
            data.setRows(row.getCell(6) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(6).getNumericCellValue()));
            data.setDepth(row.getCell(7) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(7).getNumericCellValue()));
            data.setQtyWeight(row.getCell(8) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(8).getNumericCellValue()));
            data.setOperator(AuthUtil.getSessionUserId());
            rowsData.add(data);
        }
        return rowsData;
    }

    public static List<GoodsInfoDO> readDisplayGoodsData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<GoodsInfoDO> rowsData = new ArrayList<>();
        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        //去除标题，从1开始
        for (int i = 1; i < totalRows; i++) {

            //遍历单元格
            row = sheet.getRow(i);
//            logger.info("row:{}", i);

            //获取每一行的单元格数
            //int totalCells = row.getPhysicalNumberOfCells();

            //获取每个单元格的数据，保存到集合中
            for (int t = 0; t < totalCells; t++) {
                Cell cell = row.getCell(t);
                if (cell == null) {
                    row.createCell(t).setCellType(CellType.BLANK);
                }
                //logger.debug("cellVal:{}", cell);
            }

            //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘	是否易碎品
            row.getCell(0).setCellType(CellType.STRING);
            row.getCell(1).setCellType(CellType.STRING);
            row.getCell(6).setCellType(CellType.NUMERIC);
            row.getCell(7).setCellType(CellType.STRING);
            row.getCell(8).setCellType(CellType.STRING);
            row.getCell(9).setCellType(CellType.STRING);
            row.getCell(10).setCellType(CellType.STRING);

            GoodsInfoDO data = new GoodsInfoDO();
            data.setMatId(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
            data.setMatCode(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
            data.setMatName(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
            data.setCategoryName(row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue());
            data.setMidCategoryName(row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue());
            data.setSmallCategoryName(row.getCell(5) == null ? "" : row.getCell(5).getStringCellValue());
            data.setMinPackageQty(row.getCell(6) == null ? 0 : getInt(row.getCell(6).getNumericCellValue()));
            data.setArea(row.getCell(7) == null ? "" : row.getCell(7).getStringCellValue());
            data.setLevel(row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue());
            data.setIsAllocationProhibited(row.getCell(9) == null ? 0 : "是".equals(row.getCell(9).getStringCellValue()) ? 1 : 0);
            data.setOperator(AuthUtil.getSessionUserId());
            data.setOrderNo(row.getCell(10) == null ? "" : row.getCell(10).getStringCellValue());
            rowsData.add(data);
        }
        return rowsData;
    }

    public static List<NewGoodsIssueRangeReq> readRangeDoInvalidData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<NewGoodsIssueRangeReq> rowsData = new ArrayList<>();
        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        //去除标题，从1开始
        for (int i = 1; i < totalRows; i++) {
            //遍历单元格
            row = sheet.getRow(i);
            //获取每个单元格的数据，保存到集合中
            for (int t = 0; t < totalCells; t++) {
                Cell cell = row.getCell(t);
                if (cell == null) {
                    row.createCell(t).setCellType(CellType.BLANK);
                }
            }

            //货号, 失效时间
            row.getCell(0).setCellType(CellType.STRING);
            row.getCell(1).setCellType(CellType.STRING);
            row.getCell(2).setCellType(CellType.STRING);

            NewGoodsIssueRangeReq data = new NewGoodsIssueRangeReq();
            data.setMatCode(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
            data.setSizeName(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
            data.setInvalidAt(row.getCell(2) == null ? null : LocalDate.parse(row.getCell(2).getStringCellValue(), DateTimeFormatter.ISO_LOCAL_DATE));
            rowsData.add(data);
        }
        return rowsData;
    }

    public static List<ImportNewGoodsIssueRangeDo> readNewGoodsRangeRangeDo(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ImportNewGoodsIssueRangeDo> rowsData = new ArrayList<>();
        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        //去除标题，从1开始
        for (int i = 1; i < totalRows; i++) {
            //遍历单元格
            row = sheet.getRow(i);
            //获取每个单元格的数据，保存到集合中
            for (int t = 0; t < totalCells; t++) {
                Cell cell = row.getCell(t);
                if (cell == null) {
                    row.createCell(t).setCellType(CellType.BLANK);
                }
            }

            //货号, 失效时间
            row.getCell(0).setCellType(CellType.STRING);
            row.getCell(1).setCellType(CellType.STRING);
            row.getCell(2).setCellType(CellType.STRING);
            row.getCell(3).setCellType(CellType.STRING);
            row.getCell(4).setCellType(CellType.STRING);

            ImportNewGoodsIssueRangeDo data = new ImportNewGoodsIssueRangeDo();
            data.setMatCode(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
            data.setSizeName(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
            data.setType(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
            data.setObj(row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue());
            data.setInExclude(row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue());
            rowsData.add(data);
        }
        return rowsData;
    }

    /**
     * excel文件检查
     * @param file 文件
     * @param startRow 开始行数
     * @param endRow 结束行数
     * @param columns 检查列集合
     * @param cellTypes 检查列的类型
     * @param tableHeadNames 检查列的表头名称
     * @param limitValMap 检查列的限定值
     * @param repeatCol 检查重复的列
     */
    public static String checkExcel(MultipartFile file, Integer startRow, Integer endRow, List<Integer> columns, List<CellType> cellTypes, List<String> tableHeadNames,
                                    Map<Integer, Set<String>> limitValMap, Set<Integer> repeatCol) {
        StringBuilder msg = new StringBuilder();

        Map<String, Integer> repeatMap = new HashMap<>(16);
        if (file != null) {
            try {
                Workbook wb;
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                String  extString = fileName.substring(fileName.lastIndexOf("."));
                if (".xlsx".equalsIgnoreCase(extString)) {
                    InputStream is = file.getInputStream();
                    wb = new XSSFWorkbook(is);
                } else if (".xls".equalsIgnoreCase(extString)) {
                    InputStream is = file.getInputStream();
                    wb = new HSSFWorkbook(is);
                } else {
                    throw new BusinessException("10001", "文件类型错误!");
                }
                Sheet sheet = wb.getSheetAt(0);
                int totalRows = endRow == null ? sheet.getPhysicalNumberOfRows() : endRow;
                for (int rowIdx = startRow; rowIdx < totalRows; rowIdx++) {
                    try {
                        Row row = sheet.getRow(rowIdx);
                        StringBuilder repeatStr = new StringBuilder();
                        StringBuilder rowMsg = new StringBuilder();
                        for (int i = 0; i < columns.size(); i++) {
                            int columnIdx = columns.get(i);
                            String cellStr = "";
                            Cell cell = row.getCell(columnIdx);
                            if (cell == null) {
                                rowMsg.append("第").append(rowIdx+1).append("行：").append(tableHeadNames.get(i)).append("列缺少必填项；\r\n");
                                continue;
                            }
                            CellType cellType = cell.getCellTypeEnum();
                            if (cellType.equals(CellType.BLANK)) {
                                rowMsg.append("第").append(rowIdx+1).append("行：").append(tableHeadNames.get(i)).append("列缺少必填项；\r\n");
                            } else if (!cellType.equals(cellTypes.get(i))) {
                                rowMsg.append("第").append(rowIdx+1).append("行：").append(tableHeadNames.get(i)).append("列格式错误；\r\n");
                            }
                            if (cellTypes.get(i).equals(CellType.STRING) && cellType.equals(CellType.STRING)) {
                                cellStr = row.getCell(columnIdx).getStringCellValue();
                            } else if (cellTypes.get(i).equals(CellType.NUMERIC) && cellType.equals(CellType.NUMERIC)) {
                                cellStr = String.valueOf(row.getCell(columnIdx).getNumericCellValue());
                            }
                            if (limitValMap != null && limitValMap.get(columnIdx) != null && !limitValMap.get(columnIdx).contains(cellStr)) {
                                rowMsg.append("第").append(rowIdx+1).append("行：").append(tableHeadNames.get(i)).append("列不在限定值范围中；\r\n");
                            }
                            if (repeatCol != null && repeatCol.contains(columnIdx)) {
                                repeatStr.append(cellStr).append("-");
                            }
                        }

                        if (repeatCol != null && StringUtils.isEmpty(rowMsg.toString())) {
                            if (repeatMap.get(repeatStr.toString()) != null) {
                                rowMsg.append("第").append(rowIdx+1).append("行：与第").append(repeatMap.get(repeatStr.toString())).append("行数据重复；\r\n");
                            } else {
                                repeatMap.put(repeatStr.toString(), rowIdx+1);
                            }
                        }
                        msg.append(rowMsg);
                    } catch (Exception e) {
                        throw new BusinessException("10001", "解析文件错误");
                    }
                }
            } catch (BusinessException be) {
                throw new BusinessException("10001", "解析文件错误");
            }  catch (Exception e) {
                throw new BusinessException("10002", "解析文件错误");
            }
        }
        return msg.toString();
    }

    public static void checkTableHead(MultipartFile file) {
        Map<String, Integer> repeatMap = new HashMap<>(16);
        if (file != null) {
            try {
                Workbook wb;
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                String  extString = fileName.substring(fileName.lastIndexOf("."));
                if (".xlsx".equalsIgnoreCase(extString)) {
                    InputStream is = file.getInputStream();
                    wb = new XSSFWorkbook(is);
                } else if (".xls".equalsIgnoreCase(extString)) {
                    InputStream is = file.getInputStream();
                    wb = new HSSFWorkbook(is);
                } else {
                    throw new BusinessException("10001", "文件类型错误!");
                }
                Sheet sheet = wb.getSheetAt(0);
                Row row = sheet.getRow(0);
                Cell cell = row.getCell(2);
                String str = cell.getStringCellValue();
                if (!"对象".equals(str)) {
                    throw new BusinessException("12000", "导入列与模板不匹配");
                }
            } catch (IOException ioe) {

            }

        }
    }


    public static void exportSandBoxData(String startDate, String endDate, List<IssueSandboxDetailDo> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "沙盘计算结果";
        String fileName = "沙盘计算结果" + startDate + "-" + endDate +".xlsx";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("门店ID");
        title.add("门店代码");
        title.add("门店名称");
        title.add("配货日期");
        title.add("配货数量");
        title.add("配货货值");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 50 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);

        XSSFRow titleRow = sheet.createRow(0);
        createTitle2007(workbook, titleRow, title);
        XSSFCellStyle style = setCellStyleDateFormat2007(workbook);

        int rowNum = 1;int type;
        for (IssueSandboxDetailDo data : list) {
            XSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(data.getShopId());
            row.createCell(1).setCellValue(data.getShopCode());
            row.createCell(2).setCellValue(data.getShopName());
            XSSFCell cell = row.createCell(3);
            cell.setCellValue(data.getIssueDate());
            cell.setCellStyle(style);
            row.createCell(4).setCellValue(data.getIssueNum().toString());
            row.createCell(5).setCellValue(data.getIssueValue().toString());
            rowNum++;
        }

        downloadExcel2007(fileName, workbook, request, response);
    }

    public static void exportSandBoxShopStockData(String startDate, String endDate, List<IssueSandboxShopStockDo> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "沙盘计算库存明细结果";
        String fileName = "沙盘计算库存明细结果" + startDate + "-" + endDate +".xlsx";

        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
        List<String> title = new ArrayList<>();
        title.add("门店ID");
        title.add("门店代码");
        title.add("门店名称");
        title.add("日期");
        title.add("期末库存");
        title.add("期末货值");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 50 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);

        XSSFRow titleRow = sheet.createRow(0);
        createTitle2007(workbook, titleRow, title);
        XSSFCellStyle style = setCellStyleDateFormat2007(workbook);

        int rowNum = 1;int type;
        for (IssueSandboxShopStockDo data : list) {
            XSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(data.getShopId());
            row.createCell(1).setCellValue(data.getShopCode());
            row.createCell(2).setCellValue(data.getShopName());
            XSSFCell cell = row.createCell(3);
            cell.setCellValue(data.getSandboxDate());
            cell.setCellStyle(style);
            row.createCell(4).setCellValue(data.getStock().toString());
            row.createCell(5).setCellValue(data.getStockValue().toString());
            rowNum++;
        }

        downloadExcel2007(fileName, workbook, request, response);
    }

}
