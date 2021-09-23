package cn.nome.saas.allocation.utils.old;


import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.constant.old.Constant;
import cn.nome.saas.allocation.model.old.issue.*;
import cn.nome.saas.allocation.repository.entity.allocation.GoodsInfoDO;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.CSVUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import cn.nome.saas.allocation.utils.old.excel.XlsxProcessUtils;

public class ExcelUtil2 {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil2.class);

    //浏览器下载excel
    public static void downloadExcel(String fileName, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        setResponseParam(fileName, request, response);

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 浏览器下载csv
     * @param csvFile
     * @param request
     * @param response
     * @throws IOException
     */
    public static void downloadCsv(File csvFile, HttpServletRequest request, HttpServletResponse response) {
        // 以流的形式下载文件。
        try (OutputStream toClient = new BufferedOutputStream(response.getOutputStream()); FileInputStream fis = new FileInputStream(csvFile)) {
            // 取得文件名。
            String filename = csvFile.getName();

            // 设置response的Header
            String userAgent = request.getHeader("User-Agent");
//            // 针对IE或者以IE为内核的浏览器：
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                filename = URLEncoder.encode(filename, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", filename));
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("UTF-8");

            response.setContentType("application/octet-stream");

            int content;
            while ((content = fis.read()) != -1) {
                toClient.write(content);
            }

            toClient.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
    private static void createTitle(HSSFWorkbook workbook, HSSFRow row, List<String> cellValList) {
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
    private static HSSFCellStyle setCellStyleDateFormat(HSSFWorkbook workbook) {
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
        title.add("门店代码");//0
        title.add("门店名称");
        title.add("商品代码");
        title.add("商品名称");
        title.add("陈列大类");
        title.add("陈列中类");//5
        title.add("陈列小类");
        title.add("年份");
        title.add("季节");
        title.add("货盘");

        title.add("零售价");//10
        title.add("配货规格");
        title.add("尺码数");
        title.add("尺码名称");
        title.add("28天销量");
        title.add("7天销量");//15
        title.add("有效日均销");
        title.add("日均销占比");
        title.add("仓位数");
        title.add("7日均销");

        title.add("分仓库存");//20
        title.add("剩余库存");
        title.add("订货后库存");
        title.add("在店库存");
        title.add("在途库存");
        title.add("在配库存");//25
        title.add("补货周期间隔天数");
        title.add("安全天数");
        title.add("补货前周转天数");
        title.add("补货标识");

        title.add("淘汰状态");//30
        title.add("建议数量");
        title.add("调出仓代码");
        title.add("调出仓名称");
        title.add("补货数量");

        title.add("订货包数");//35
        title.add("补货后周转天数");
        title.add("中类陈列量");
        title.add("小类陈列量");
        title.add("饱满度目标");

        title.add("本店排名");//40
        title.add("全国排名");
        title.add("禁配/保底策略");
        title.add("策略数量");
        title.add("需求量");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        //cell单元格\Column列
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 40 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 30 * 256);//陈列小类

        sheet.setColumnWidth(5, 10 * 256);
        sheet.setColumnWidth(6, 10 * 256);
        sheet.setColumnWidth(7, 10 * 256);
        sheet.setColumnWidth(8, 10 * 256);
        sheet.setColumnWidth(9, 10 * 256);//配货规格

        sheet.setColumnWidth(10, 10 * 256);
        sheet.setColumnWidth(11, 10 * 256);
        sheet.setColumnWidth(12, 10 * 256);
        sheet.setColumnWidth(13, 10 * 256);
        sheet.setColumnWidth(14, 20 * 256);//有效日均销

        sheet.setColumnWidth(15, 15 * 256);
        sheet.setColumnWidth(16, 10 * 256);
        sheet.setColumnWidth(17, 10 * 256);
        sheet.setColumnWidth(18, 10 * 256);
        sheet.setColumnWidth(19, 10 * 256);//剩余库存

        sheet.setColumnWidth(20, 10 * 256);
        sheet.setColumnWidth(21, 10 * 256);
        sheet.setColumnWidth(22, 10 * 256);
        sheet.setColumnWidth(23, 10 * 256);
        sheet.setColumnWidth(24, 25 * 256);//补货周期间隔天数

        sheet.setColumnWidth(25, 25 * 256);
        sheet.setColumnWidth(26, 10 * 256);
        sheet.setColumnWidth(27, 10 * 256);
        sheet.setColumnWidth(28, 10 * 256);
        sheet.setColumnWidth(29, 20 * 256);//订货包数

        sheet.setColumnWidth(30, 25 * 256);
        sheet.setColumnWidth(31, 15 * 256);
        sheet.setColumnWidth(32, 15 * 256);
        sheet.setColumnWidth(33, 10 * 256);
        sheet.setColumnWidth(34, 10 * 256);//本店排名

        sheet.setColumnWidth(35, 20 * 256);
        sheet.setColumnWidth(36, 10 * 256);
        sheet.setColumnWidth(37, 10 * 256);
        sheet.setColumnWidth(38, 10 * 256);
        sheet.setColumnWidth(39, 10 * 256);

        sheet.setColumnWidth(40, 10 * 256);
        sheet.setColumnWidth(41, 10 * 256);
        sheet.setColumnWidth(42, 10 * 256);
        sheet.setColumnWidth(43, 10 * 256);
        sheet.setColumnWidth(44, 10 * 256);

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //新增数据行，并且设置单元格数据
        int rowNum = 1;
        for (OrderDetailVo vo : rows) {
            HSSFRow row = sheet.createRow(rowNum);
            //门店代码, 门店名称, 商品代码, 商品名称, 陈列大类
            row.createCell(0).setCellValue(vo.getShopCode());
            row.createCell(1).setCellValue(vo.getShopName());
            row.createCell(2).setCellValue(vo.getMatCode());
            row.createCell(3).setCellValue(vo.getMatName());
            row.createCell(4).setCellValue(vo.getCategoryName());
            //陈列中类, 陈列小类, 年份, 季节, 货盘
            row.createCell(5).setCellValue(vo.getMidCategoryName());
            row.createCell(6).setCellValue(vo.getSmallCategoryName());
            row.createCell(7).setCellValue(vo.getYearNo());
            row.createCell(8).setCellValue(vo.getSeasonName());
            row.createCell(9).setCellValue(vo.getGoodsLevel());
            //零售价, 配货规格 , 尺码数 , 尺码名称 , 28天销量 ,
            row.createCell(10).setCellValue(vo.getQuotePrice() == null ? 0 : new BigDecimal(vo.getQuotePrice()).doubleValue());
            row.createCell(11).setCellValue(vo.getMinPackageQty());
            row.createCell(12).setCellValue(vo.getSizeCount() == null ? 0 : vo.getSizeCount());
            row.createCell(13).setCellValue(vo.getSizeName());
            row.createCell(14).setCellValue(vo.getSaleQty28());
            //7天销量, 有效日均销 , 日均销占比 , 仓位数 , 7日均销
            row.createCell(15).setCellValue(vo.getSaleQty7());
            row.createCell(16).setCellValue(vo.getValidSaleQty() == null ? 0 : vo.getValidSaleQty().doubleValue());
            row.createCell(17).setCellValue(vo.getPercentAvgSaleQty());
            row.createCell(18).setCellValue(vo.getDisplayQty());
            row.createCell(19).setCellValue(vo.getAvgSaleQty() == null ? 0 : vo.getAvgSaleQty().doubleValue());
            //分仓库存, 剩余库存 , 订货后库存 , 在店库存 , 在途库存
            row.createCell(20).setCellValue(StringUtils.isBlank(vo.getOutStockQty()) ? 0 : new BigDecimal(vo.getOutStockQty()).doubleValue());
            row.createCell(21).setCellValue(vo.getRemainStockQty() == null ? 0 : vo.getRemainStockQty().intValue());
            row.createCell(22).setCellValue(vo.getTotalStockQty() == null ? 0 : vo.getTotalStockQty().doubleValue());
            row.createCell(23).setCellValue(vo.getInStockQty() == null ? 0 : vo.getInStockQty().doubleValue());
            row.createCell(24).setCellValue(vo.getPathStockQty() == null ? 0 : vo.getPathStockQty().doubleValue());
            //在配库存, 补货周期间隔天数 , 安全天数 , 补货前周转天数 , 补货标识
            row.createCell(25).setCellValue(vo.getMoveQty() == null ? 0 : vo.getMoveQty().doubleValue());
            row.createCell(26).setCellValue(vo.getIssueDay() == null ? 0 : vo.getIssueDay());
            row.createCell(27).setCellValue(vo.getSafeDay() == null ? 0 : vo.getSafeDay());
            row.createCell(28).setCellValue(StringUtils.isBlank(vo.getExIssueTurnoverDay()) ? Constant.DEFAULT_VAL_SLASH : vo.getExIssueTurnoverDay());
            row.createCell(29).setCellValue(vo.getIssueFlag());
            //淘汰状态 , 建议数量 , 调出仓代码 调出仓名称   补货数量 ,
            row.createCell(30).setCellValue(vo.getIsEliminate() == 1 ? "是" : "否");
            row.createCell(31).setCellValue(vo.getPackageQty().doubleValue());
            row.createCell(32).setCellValue(vo.getWarehouseCode() == null ? "" : vo.getWarehouseCode());
            row.createCell(33).setCellValue(vo.getWarehouseName() == null ? "" : vo.getWarehouseName());
            row.createCell(34).setCellValue((vo.getOrderPackage().multiply(new BigDecimal(vo.getMinPackageQty()))).doubleValue());
            //订货包数 , 补货后周转天数   中类陈列量 //, 小类陈列量 , 饱满度目标 ,
            row.createCell(35).setCellValue(vo.getOrderPackage().doubleValue());
            row.createCell(36).setCellValue(StringUtils.isBlank(vo.getSaleDays()) ? Constant.DEFAULT_VAL_SLASH : vo.getSaleDays());
            row.createCell(37).setCellValue(vo.getMidDisplaydepth() == null ? 0 : vo.getMidDisplaydepth());
            row.createCell(38).setCellValue(vo.getSmallDisplaydepth() == null ? 0 : vo.getSmallDisplaydepth());
            row.createCell(39).setCellValue(vo.getDisplayPercent() == null ? 0 : vo.getDisplayPercent().doubleValue());
            //本店排名 , 全国排名    禁配/保底策略 , 策略数量
            row.createCell(40).setCellValue(vo.getShopRank() == null ? 0 : vo.getShopRank());
            row.createCell(41).setCellValue(vo.getNationalRank() == null ? 0 : vo.getNationalRank());
            row.createCell(42).setCellValue(vo.getRuleName());
            row.createCell(43).setCellValue(vo.getSecurityQty() == null ? "" : vo.getSecurityQty().toString());
            row.createCell(44).setCellValue(vo.getNeedQty() == null ? 0 : vo.getNeedQty().doubleValue());

            rowNum++;
        }

        //生成excel文件
        //ExcelUtil.buildExcelFile(fileName, workbook);

        //浏览器下载excel
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportOrderDetailCSV(String fileName, List<OrderDetailVo> rows, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sheetName = "配补明细";
        List<Object> title = new ArrayList<>();
        title.add("门店代码");//0
        title.add("门店名称");
        title.add("商品代码");
        title.add("商品名称");
        title.add("陈列大类");
        title.add("陈列中类");//5
        title.add("陈列小类");
        title.add("年份");
        title.add("季节");
        title.add("货盘");

        title.add("零售价");//10
        title.add("配货规格");
        title.add("尺码数");
        title.add("尺码名称");
        title.add("28天销量");
        title.add("7天销量");//15
        title.add("有效日均销");
        title.add("日均销占比");
        title.add("仓位数");
        title.add("7日均销");

        title.add("分仓库存");//20
        title.add("剩余库存");
        title.add("订货后库存");
        title.add("在店库存");
        title.add("在途库存");
        title.add("在配库存");//25
        title.add("补货周期间隔天数");
        title.add("安全天数");
        title.add("补货前周转天数");
        title.add("补货标识");

        title.add("淘汰状态");//30
        title.add("建议数量");
        title.add("调出仓代码");
        title.add("调出仓名称");
        title.add("补货数量");

        title.add("订货包数");//35
        title.add("补货后周转天数");
        title.add("中类陈列量");
        title.add("小类陈列量");
        title.add("饱满度目标");

        title.add("本店排名");//40
        title.add("全国排名");
        title.add("禁配/保底策略");
        title.add("策略数量");
        title.add("需求量");

//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet sheet = workbook.createSheet(sheetName);
//        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
//        //cell单元格\Column列
//        sheet.setColumnWidth(0, 20 * 256);
//        sheet.setColumnWidth(1, 40 * 256);
//        sheet.setColumnWidth(2, 15 * 256);
//        sheet.setColumnWidth(3, 20 * 256);
//        sheet.setColumnWidth(4, 30 * 256);//陈列小类
//
//        sheet.setColumnWidth(5, 10 * 256);
//        sheet.setColumnWidth(6, 10 * 256);
//        sheet.setColumnWidth(7, 10 * 256);
//        sheet.setColumnWidth(8, 10 * 256);
//        sheet.setColumnWidth(9, 10 * 256);//配货规格
//
//        sheet.setColumnWidth(10, 10 * 256);
//        sheet.setColumnWidth(11, 10 * 256);
//        sheet.setColumnWidth(12, 10 * 256);
//        sheet.setColumnWidth(13, 10 * 256);
//        sheet.setColumnWidth(14, 20 * 256);//有效日均销
//
//        sheet.setColumnWidth(15, 15 * 256);
//        sheet.setColumnWidth(16, 10 * 256);
//        sheet.setColumnWidth(17, 10 * 256);
//        sheet.setColumnWidth(18, 10 * 256);
//        sheet.setColumnWidth(19, 10 * 256);//剩余库存
//
//        sheet.setColumnWidth(20, 10 * 256);
//        sheet.setColumnWidth(21, 10 * 256);
//        sheet.setColumnWidth(22, 10 * 256);
//        sheet.setColumnWidth(23, 10 * 256);
//        sheet.setColumnWidth(24, 25 * 256);//补货周期间隔天数
//
//        sheet.setColumnWidth(25, 25 * 256);
//        sheet.setColumnWidth(26, 10 * 256);
//        sheet.setColumnWidth(27, 10 * 256);
//        sheet.setColumnWidth(28, 10 * 256);
//        sheet.setColumnWidth(29, 20 * 256);//订货包数
//
//        sheet.setColumnWidth(30, 25 * 256);
//        sheet.setColumnWidth(31, 15 * 256);
//        sheet.setColumnWidth(32, 15 * 256);
//        sheet.setColumnWidth(33, 10 * 256);
//        sheet.setColumnWidth(34, 10 * 256);//本店排名
//
//        sheet.setColumnWidth(35, 20 * 256);
//        sheet.setColumnWidth(36, 10 * 256);
//        sheet.setColumnWidth(37, 10 * 256);
//        sheet.setColumnWidth(38, 10 * 256);
//        sheet.setColumnWidth(39, 10 * 256);
//        sheet.setColumnWidth(40, 10 * 256);
//        sheet.setColumnWidth(41, 10 * 256);
//        sheet.setColumnWidth(42, 10 * 256);
//        sheet.setColumnWidth(43, 10 * 256);

        //新增数据行，并且设置单元格数据
        int rowNum = 1;
        List<List<Object>> dataList = new ArrayList<>();
        for (OrderDetailVo vo : rows) {
            List<Object> data = new ArrayList<>();
            //门店代码, 门店名称, 商品代码, 商品名称, 陈列大类
            data.add(vo.getShopCode());
            data.add(vo.getShopName());
            data.add(vo.getMatCode() == null ? "" : vo.getMatCode());
            data.add(vo.getMatName() == null ? "" : vo.getMatName());
            data.add(vo.getCategoryName() == null ? "" : vo.getCategoryName());
            //陈列中类, 陈列小类, 年份, 季节, 货盘
            data.add(vo.getMidCategoryName() == null ? "" : vo.getMidCategoryName());
            data.add(vo.getSmallCategoryName() == null ? "" : vo.getSmallCategoryName());
            data.add(vo.getYearNo() == null ? "" : vo.getYearNo());
            data.add(vo.getSeasonName() == null ? "" : vo.getSeasonName());
            data.add(vo.getGoodsLevel() == null ? "" : vo.getGoodsLevel());
            //零售价, 配货规格 , 尺码数 , 尺码名称 , 28天销量 ,
            data.add(vo.getQuotePrice() == null ? 0 : new BigDecimal(vo.getQuotePrice()).doubleValue());
            data.add(vo.getMinPackageQty());
            data.add(vo.getSizeCount() == null ? 0 : vo.getSizeCount());
            data.add(vo.getSizeName());
            data.add(vo.getSaleQty28());
            //7天销量, 有效日均销 , 日均销占比 , 仓位数 , 7日均销
            data.add(vo.getSaleQty7());
            data.add(vo.getValidSaleQty() == null ? 0 : vo.getValidSaleQty().doubleValue());
            data.add(vo.getPercentAvgSaleQty());
            data.add(vo.getDisplayQty());
            data.add(vo.getAvgSaleQty() == null ? 0 : vo.getAvgSaleQty().doubleValue());
            //分仓库存, 剩余库存 , 订货后库存 , 在店库存 , 在途库存
            data.add(StringUtils.isBlank(vo.getOutStockQty()) ? 0 : new BigDecimal(vo.getOutStockQty()).doubleValue());
            data.add(vo.getRemainStockQty() == null ? 0 : vo.getRemainStockQty().intValue());
            data.add(vo.getTotalStockQty() == null ? 0 : vo.getTotalStockQty().doubleValue());
            data.add(vo.getInStockQty() == null ? 0 : vo.getInStockQty().doubleValue());
            data.add(vo.getPathStockQty() == null ? 0 : vo.getPathStockQty().doubleValue());
            //在配库存, 补货周期间隔天数 , 安全天数 , 补货前周转天数 , 补货标识
            data.add(vo.getMoveQty() == null ? 0 : vo.getMoveQty().doubleValue());
            data.add(vo.getIssueDay() == null ? 0 : vo.getIssueDay());
            data.add(vo.getSafeDay() == null ? 0 : vo.getSafeDay());
            data.add(StringUtils.isBlank(vo.getExIssueTurnoverDay()) ? Constant.DEFAULT_VAL_SLASH : vo.getExIssueTurnoverDay());
            data.add(vo.getIssueFlag());
            //淘汰状态 , 建议数量 , 调出仓代码 调出仓名称   补货数量 ,
            data.add(vo.getIsEliminate() == 1 ? "是" : "否");
            data.add(vo.getPackageQty().doubleValue());
            data.add(vo.getWarehouseCode() == null ? "" : vo.getWarehouseCode());
            data.add(vo.getWarehouseName() == null ? "" : vo.getWarehouseName());
            data.add((vo.getOrderPackage().multiply(new BigDecimal(vo.getMinPackageQty()))).doubleValue());
            //订货包数 , 补货后周转天数   中类陈列量 //, 小类陈列量 , 饱满度目标 ,
            data.add(vo.getOrderPackage().doubleValue());
            data.add(StringUtils.isBlank(vo.getSaleDays()) ? Constant.DEFAULT_VAL_SLASH : vo.getSaleDays());
            data.add(vo.getMidDisplaydepth() == null ? 0 : vo.getMidDisplaydepth());
            data.add(vo.getSmallDisplaydepth() == null ? 0 : vo.getSmallDisplaydepth());
            data.add(vo.getDisplayPercent() == null ? 0 : vo.getDisplayPercent().doubleValue());
            //本店排名 , 全国排名    禁配/保底策略 , 策略数量
            data.add(vo.getShopRank() == null ? 0 : vo.getShopRank());
            data.add(vo.getNationalRank() == null ? 0 : vo.getNationalRank());
            data.add(vo.getRuleName());
            data.add(vo.getSecurityQty() == null ? "" : vo.getSecurityQty().toString());
            data.add(vo.getNeedQty().doubleValue());
            dataList.add(data);
        }

        File csvFile = CSVUtils.createCSVFile(title, dataList, request.getSession().getServletContext().getRealPath(""), fileName);

        downloadCsv(csvFile, request, response);
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

    public static String downloadFile(String tempalteDir, String fileName, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

        String tips = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File file;FileInputStream fis;
        try {
            file = new File(tempalteDir + fileName);
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        try {

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

    public static void exportDisplayCategory(List<DisplayData> displayData, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        for (DisplayData vo : displayData) {
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
            row.createCell(0).setCellValue(vo.getMatId());
            row.createCell(1).setCellValue(vo.getMatCode());
            row.createCell(2).setCellValue(vo.getMatName());
            row.createCell(3).setCellValue(vo.getCategoryName());
            row.createCell(4).setCellValue(vo.getMidCategoryName());
            row.createCell(5).setCellValue(vo.getSmallCategoryName());
            row.createCell(6).setCellValue(vo.getMinPackageQty());
            row.createCell(7).setCellValue(vo.getArea());
            row.createCell(8).setCellValue(vo.getLevel());
            row.createCell(9).setCellValue(vo.getIsAllocationProhibited());
            row.createCell(10).setCellValue(vo.getOrderNo());
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportShopInfoData(List<ShopInfoData> shopInfoData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sheetName = "门店信息配置";
        String fileName = "门店信息配置.xls";

        //门店代码	商品管理员ID	货盘区域	商品管理员	配发周期	门店等级	儿童是否单独陈列	男装货盘	女装货盘	百货货盘	物流天数
        List<String> title = new ArrayList<>();
        title.add("门店代码");
        title.add("商品管理员ID");
        title.add("货盘区域");
        title.add("商品管理员");
        title.add("到货时间");
        title.add("门店等级");
        title.add("儿童是否单独陈列");
        title.add("男装货盘");
        title.add("女装货盘");
        title.add("百货货盘");
        title.add("物流天数");
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

        HSSFRow titleRow = sheet.createRow(0);
        createTitle(workbook, titleRow, title);
        HSSFCellStyle style = setCellStyleDateFormat(workbook);

        //门店代码	商品管理员ID	货盘区域	商品管理员	配发周期	门店等级	儿童是否单独陈列	男装货盘	女装货盘	百货货盘	物流天数
        int rowNum = 1;
        for (ShopInfoData vo : shopInfoData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopCode());
            row.createCell(1).setCellValue(vo.getUserId());
            row.createCell(2).setCellValue(vo.getGoodsArea());
            row.createCell(3).setCellValue(vo.getUserName());
            row.createCell(4).setCellValue(vo.getIssueTime());
            row.createCell(5).setCellValue(vo.getShopLevel());
            row.createCell(6).setCellValue(vo.getHaveChild());
            row.createCell(7).setCellValue(vo.getMenLevel());
            row.createCell(8).setCellValue(vo.getWomenLevel());
            row.createCell(9).setCellValue(vo.getCommodityLevel());
            row.createCell(10).setCellValue(vo.getRoadDay());
            rowNum++;
        }
        downloadExcel(fileName, workbook, request, response);
    }

    public static void exportShopDisplayData(List<ShopDisplayData> shopDisplayData, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        for (ShopDisplayData vo : shopDisplayData) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(vo.getShopCode());
            row.createCell(1).setCellValue(vo.getCategoryName());
            row.createCell(2).setCellValue(vo.getMidCategoryName());
            row.createCell(3).setCellValue(vo.getDisplayPercent() == null ? 0 : vo.getDisplayPercent().doubleValue());
            row.createCell(4).setCellValue(vo.getDisplay_Qty() == null ? 0 : vo.getDisplay_Qty().doubleValue());
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

//    /**
//     * 大数量excel获取方法
//     * @param file
//     * @return
//     * @throws Exception
//     */
//    public static List<UploadDetailData> readForbiddenDetailData(MultipartFile file)throws Exception  {
//        return XlsxProcessUtils.processAllSheet(file.getInputStream(),1, UploadDetailData.class, data->data.getShopCode()!=null);
//    }

//    public static void exportForbiddenDetailData(List<ForbiddenSingleItem> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String sheetName = "禁配明细";
//        String fileName = "禁配明细"+ DateUtil.format(DateUtil.getCurrentDate(), DateUtil.DATE_ONLY)+".xlsx";
//
//        //门店代码	陈列大类	陈列中类	陈列饱满度	仓位数
//        List<String> title = new ArrayList<>();
//        title.add("门店代码");
//        title.add("类型");
//        title.add("类型值");
//        title.add("开始日期");
//        title.add("结束日期");
//        title.add("备注");
//        title.add("最后修改人");
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet(sheetName);
//
//        //cell单元格\Column列
//        sheet.setColumnWidth(0, 20 * 256);
//        sheet.setColumnWidth(1, 20 * 256);
//        sheet.setColumnWidth(2, 10 * 256);
//        sheet.setColumnWidth(3, 15 * 256);
//        sheet.setColumnWidth(4, 15 * 256);
//        sheet.setColumnWidth(5, 15 * 256);
//        sheet.setColumnWidth(6, 15 * 256);
//
//        XSSFRow titleRow = sheet.createRow(0);
//        createTitle2007(workbook, titleRow, title);
//        XSSFCellStyle style = setCellStyleDateFormat2007(workbook);
//
//        int rowNum = 1;
//        for (ForbiddenSingleItem vo : list) {
//            XSSFRow row = sheet.createRow(rowNum);
//            row.createCell(0).setCellValue(vo.getShopCode());
//            row.createCell(1).setCellValue(vo.getTypeName());
//            row.createCell(2).setCellValue(vo.getTypeValue());
//
//            XSSFCell startCell =row.createCell(3);
//            startCell.setCellStyle(style);
//            startCell.setCellValue(vo.getStartDate());
//            XSSFCell endCell = row.createCell(4);
//            endCell.setCellStyle(style);
//            endCell.setCellValue(vo.getEndDate());
//            row.createCell(5).setCellValue(vo.getRemark());
//            row.createCell(6).setCellValue(vo.getModifiedBy());
//            rowNum++;
//        }
//
//        downloadExcel2007(fileName, workbook, request, response);
//    }

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

            //门店代码	商品管理员ID	货盘区域	商品管理员	配发周期	门店等级	儿童是否单独陈列	男装货盘	女装货盘	百货货盘	物流天数
            row.getCell(1).setCellType(CellType.STRING);
            row.getCell(4).setCellType(CellType.STRING);
            row.getCell(6).setCellType(CellType.NUMERIC);
            row.getCell(10).setCellType(CellType.NUMERIC);

            //检查是否重复门店编码
            shopCode = row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue();
            if (shops.contains(shopCode)){
                throw new BizException(Constant.REPEAT_SHOP_CODE.replace("{0}", shopCode));
            }
            shops.add(shopCode);

            ShopInfoData data = new ShopInfoData();
            data.setShopCode(shopCode);

            try {
                data.setUserId(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
                data.setGoodsArea(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
                data.setUserName(row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue());
                data.setIssueTime(row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue());
                data.setShopLevel(row.getCell(5) == null ? "" : row.getCell(5).getStringCellValue());
                data.setHaveChild(getInt(row.getCell(6) == null ? 0 : row.getCell(6).getNumericCellValue()));
                data.setMenLevel(row.getCell(7) == null ? "" : row.getCell(7).getStringCellValue());
                data.setWomenLevel(row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue());
                data.setCommodityLevel(row.getCell(9) == null ? "" : row.getCell(9).getStringCellValue());
                data.setRoadDay(getInt(row.getCell(10) == null ? 0 : row.getCell(10).getNumericCellValue()));
                data.setOperator(AuthUtil.getSessionUserId());
                rowsData.add(data);
            }catch (Exception e){
                logger.error("readShopInfoData err:{}", e.getMessage());
                throw new BizException("读数异常，检查第" + (i+1) + "行");
            }
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
    public static List<ShopDisplayData> readShopDisplayData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ShopDisplayData> rowsData = new ArrayList<ShopDisplayData>();
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
            ShopDisplayData data = new ShopDisplayData();
            try {

                data.setShopCode(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
                data.setCategoryName(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
                data.setMidCategoryName(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
                data.setDisplayPercent(row.getCell(3) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(3).getNumericCellValue()));
                data.setDisplay_Qty(row.getCell(4) == null ? new BigDecimal(0) : new BigDecimal(row.getCell(4).getNumericCellValue()));
                data.setOperator(AuthUtil.getSessionUserId());
                rowsData.add(data);
            } catch (Exception e) {
                logger.error("readShopDisplayData err:{}", e.getMessage());
                throw new BizException("读数异常，检查第" + (i+1) + "行");
            }
        }
        return rowsData;
    }

    public static List<DisplayData> readDisplayData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<DisplayData> rowsData = new ArrayList<DisplayData>();
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

            DisplayData data = new DisplayData();
            try {

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
            } catch (Exception e) {
                logger.error("readDisplayData err:{}", e.getMessage());
                throw new BizException("读数异常，检查第" + (i+1) + "行");
            }
        }
        return rowsData;
    }

    public static List<GoodsInfoData> readDisplayGoodsData(String fileName, MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<GoodsInfoData> rowsData = new ArrayList<GoodsInfoData>();
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

            try {
                //货品ID	货号	货品简称	陈列大类	陈列中类	陈列小类	中包装数	货区	货盘	是否易碎品
                row.getCell(0).setCellType(CellType.STRING);
                row.getCell(1).setCellType(CellType.STRING);
                row.getCell(6).setCellType(CellType.NUMERIC);
                row.getCell(7).setCellType(CellType.STRING);
                row.getCell(8).setCellType(CellType.STRING);
                row.getCell(9).setCellType(CellType.NUMERIC);

                GoodsInfoData data = new GoodsInfoData();
                data.setMatId(row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue());
                data.setMatCode(row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue());
                data.setMatName(row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue());
                data.setCategoryName(row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue());
                data.setMidCategoryName(row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue());
                data.setSmallCategoryName(row.getCell(5) == null ? "" : row.getCell(5).getStringCellValue());
                data.setMinPackageQty(row.getCell(6) == null ? 0 : getInt(row.getCell(6).getNumericCellValue()));
                data.setArea(row.getCell(7) == null ? "" : row.getCell(7).getStringCellValue());
                data.setLevel(row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue());
                data.setProhibited(row.getCell(9) == null ? 0 : getInt(row.getCell(9).getNumericCellValue()));
                data.setOperator(AuthUtil.getSessionUserId());
                rowsData.add(data);
            }catch (Exception e){
                logger.error("readDisplayGoodsData err:{}", e.getMessage());
                throw new BizException("读数异常，检查第" + (i+1) + "行");
            }
        }
        return rowsData;
    }
}
