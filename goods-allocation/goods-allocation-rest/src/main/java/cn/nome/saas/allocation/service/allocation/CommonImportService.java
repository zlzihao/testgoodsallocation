package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.exceptions.AllocationException;
import cn.nome.saas.allocation.model.allocation.TemplateInfoView;
import cn.nome.saas.allocation.model.issue.DisplayDataV2;
import cn.nome.saas.allocation.model.issue.DwsDimShopData;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.DownTemplateDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimShopDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ExportConfigDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ImportConfigDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.DownTemplateDO;
import cn.nome.saas.allocation.repository.entity.allocation.ExportConfigDO;
import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import cn.nome.saas.allocation.service.ProcessFactoryManager;
import cn.nome.saas.allocation.service.ProcessService;
import cn.nome.saas.allocation.utils.CheckUtil;
import cn.nome.saas.allocation.utils.ExcelUtil;
import cn.nome.saas.allocation.utils.StackUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/7 18:36
 * @description：公共导入服务处理
 * @modified By：
 * @version: 1.0.0$
 */
@Service
public class CommonImportService  extends ProcessFactoryManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DownTemplateDOMapper downTemplateDOMapper;

    @Autowired
    private ImportConfigDOMapper importConfigDOMapper;

    @Autowired
    private DwsDimShopDOMapper dwsDimShopDOMapper;

    @Autowired
    private Map<String, ProcessService> importServiceMap;

    @Autowired
    private ExportConfigDOMapper exportConfigDOMapper;

    /**
     * 业务列表
     *
     * @return
     */
    public Result businessList(){
        // 查询模板
        List<DownTemplateDO> downTemplateDOS = downTemplateDOMapper.queryAllBusList();
        if(CollectionUtils.isEmpty(downTemplateDOS)){
            return ResultUtil.handleSuccessReturn();
        }

        // 转换
        List<TemplateInfoView> templateInfoViewList = new ArrayList<>();
        downTemplateDOS.forEach(downTemplateDO -> {
            TemplateInfoView templateInfoView = new TemplateInfoView();
            templateInfoView.setCode(downTemplateDO.getBusinessCode());
            templateInfoView.setName(downTemplateDO.getBusinessName());
            templateInfoViewList.add(templateInfoView);
        });

        return ResultUtil.handleSuccessReturn(templateInfoViewList);
    }

    /**
     * 模板列表
     *
     * @return
     */
    public Result templateList(HttpServletRequest request){
        //获取业务编码
        String busCode = request.getParameter("busCode");

        // 查询模板
        List<DownTemplateDO> downTemplateDOS = downTemplateDOMapper.queryAllTplList(busCode);
        if(CollectionUtils.isEmpty(downTemplateDOS)){
            return ResultUtil.handleSuccessReturn();
        }

        // 转换
        List<TemplateInfoView> templateInfoViewList = new ArrayList<>();
        downTemplateDOS.forEach(downTemplateDO -> {
            TemplateInfoView templateInfoView = new TemplateInfoView();
            templateInfoView.setCode(downTemplateDO.getCode());
            templateInfoView.setName(downTemplateDO.getName().substring(0,downTemplateDO.getName().indexOf(".")));
            templateInfoViewList.add(templateInfoView);
        });

        return ResultUtil.handleSuccessReturn(templateInfoViewList);
    }

    /**
     * 模板下载
     *
     * @param request
     * @param response
     * @return
     */
    public  String downloadTemplate(HttpServletRequest request, HttpServletResponse response){
        long startTime = System.currentTimeMillis();

        //1、获取模板编码
        String templateCode = request.getParameter("templateCode");
        try{

            if(StringUtils.isEmpty(templateCode)){
                LoggerUtil.error(logger,"模板编码参数为空");
                throw new AllocationException("模板编码参数为空");
            }

            //2、查询模板配置表，获取配置
            DownTemplateDO downTemplateDO = downTemplateDOMapper.queryByCode(templateCode);
            if(downTemplateDO == null){
                LoggerUtil.error(logger,"根据模板编码{0}找不到配置信息",templateCode);
                throw new AllocationException("根据模板编码" + templateCode + "找不到配置信息");
            }

            LoggerUtil.info(logger,"模板配置信息={0}", JSON.toJSONString(downTemplateDO));

            //3、开始下载
            ExcelUtil.downloadTemplate(downTemplateDO.getFileDir(), downTemplateDO.getName(), request, response);
        }catch(Exception e) {
            LoggerUtil.error(logger, "模板下载错误={0}", e.getMessage());
            return e.getMessage();
        }finally {
            logger.info("{} download over,time use={}",templateCode,System.currentTimeMillis() - startTime);
        }

        return "886";
    }

    /**
     * 导入数据
     *
     * @param file
     * @return
     */
    @Transactional("allocationTransactionManager")
    public Result importData(MultipartFile file,HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        //开始处理导入新
        try {
            //1、获取文件名
            String originalFilename = file.getOriginalFilename();
            // 检查文件名
            CheckUtil.checkExcelFile(originalFilename);

            //1、获取模板编码
            String templateCode = request.getParameter("templateCode");
            if(StringUtils.isEmpty(templateCode)){
                LoggerUtil.error(logger,"模板编码参数为空");
                return ResultUtil.handleSysFailtureReturn("模板编码参数为空");
            }

            DownTemplateDO downTemplateDO = downTemplateDOMapper.queryByCode(templateCode);
            if(downTemplateDO == null){
                LoggerUtil.error(logger,"根据模板编码{0}找不到下载配置信息",templateCode);
                return ResultUtil.handleSysFailtureReturn("根据模板编码" + templateCode + "找不到下载配置信息");
            }

            // 判断是否有个性化服务处理
            int serviceReturn = NO_NEED_DO;//默认需要服务处理
            if(StringUtils.isNotEmpty(downTemplateDO.getServiceCode())){
                ProcessService processService = importServiceMap.get(downTemplateDO.getServiceCode());
                serviceReturn = processService.importData(downTemplateDO.getIsClear(),originalFilename,file,downTemplateDO.getCode());
            }

            // 不需要个性化服务处理的，走简单导入
            if(serviceReturn == NO_NEED_DO){
                //2、根据文件名查询配置信息
                List<ImportConfigDO> importConfigDOList = importConfigDOMapper.queryByTplCode(downTemplateDO.getCode());
                if(CollectionUtils.isEmpty(importConfigDOList)){
                    LoggerUtil.error(logger,"找不到导入配置信息，模板信息={0}",JSON.toJSONString(downTemplateDO));
                    return ResultUtil.handleSysFailtureReturn("找不到导入配置信息，模板编码=" + downTemplateDO.getCode() + "，模板名称=" + downTemplateDO.getName());
                }

                //3、开始导入处理
                dealData(downTemplateDO.getIsClear(),originalFilename,file,importConfigDOList);
            }
        }catch(BusinessException e){
            LoggerUtil.error(logger,"数据导入自定义错误={0}", StackUtil.getStackTrace(e));
            return ResultUtil.handleSysFailtureReturn(e.getCode());
        }catch(Exception e){
            LoggerUtil.error(logger,"数据导入系统错误={0}", StackUtil.getStackTrace(e));
            return ResultUtil.handleSysFailtureReturn("导入数据异常");
        }finally {
            logger.info("{} import over,time use={}", file.getOriginalFilename(),System.currentTimeMillis() - startTime);
        }
        return ResultUtil.handleSuccessReturn();
    }

    /**
     * 读文件信息
     * @param fileName
     * @param file
     * @return
     * @throws IOException
     */
    private void dealData(Integer isClear,String fileName, MultipartFile file,List<ImportConfigDO> importConfigDOList) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ShopInfoData> rowsData = new ArrayList<ShopInfoData>();

        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();

        // 配置转换成列配置
        Map<Integer,String> colNumAndcolNameMap = readRowDataForTitle(sheet,rowsData,totalCells,0);
        Map<Integer,ImportConfigDO>  columnNumMap = new HashMap<>();
        List<String> columnNameList = new ArrayList<>();
        List<Integer> columnNumList = new ArrayList<>();
        String tableName = null;
        for(ImportConfigDO importConfigDO : importConfigDOList) {
            // 检查表头
            checkColTitle(importConfigDO,colNumAndcolNameMap);

            // 设置表格名称
            if(StringUtils.isEmpty(tableName)){
                tableName = importConfigDO.getTableName();
            }

            // 设置单元格值类型
            columnNameList.add(importConfigDO.getColumnName().toLowerCase());//数据库字段
            columnNumList.add(importConfigDO.getTplColumnNum());//表格列名
            columnNumMap.put(importConfigDO.getTplColumnNum(),importConfigDO);//列名对应关系
        }

        // 特殊处理
        if(columnNameList.contains("shop_code")){
            columnNameList.add("shop_id");
        }

        // 组装插入字段SQL
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(" insert into ").append(tableName).append (" ( ");
        insertSql.append(" ").append(org.apache.commons.lang3.StringUtils.join(columnNameList,",")).append(",created_at").append(" ) values ");
        LoggerUtil.info(logger,"动态插入字段SQL={0}",insertSql.toString());

        //去除标题，从1开始
        Map<String,String> shopCodeAndIdMap = null;
        if(columnNameList.contains("shop_code")){
            shopCodeAndIdMap = getShopMap();
        }
        for (int i = 1; i < totalRows; i++) {
            String returnSql = readRowData(sheet, rowsData, totalCells, i, columnNumMap, columnNumList, shopCodeAndIdMap);
            if(StringUtils.isEmpty(returnSql)){
                continue;
            }
            // 组装插入信息SQL
            insertSql.append(returnSql).append(",");
        }

        // 删除数据.
        if(isClear == 1){
            StringBuilder deleteSql = new StringBuilder();
            deleteSql.append(" delete from ").append(tableName);
            importConfigDOMapper.deleteSql(deleteSql.toString());
        }

        // 插入数据
        importConfigDOMapper.insertSql(insertSql.toString().substring(0,insertSql.length() - 1));
    }

    /**
     * 获取所有门店code和id对应关系
     *
     * @return
     */
    private Map<String,String> getShopMap(){
        List<DwsDimShopData> allList = dwsDimShopDOMapper.getAllList();
        if(CollectionUtils.isEmpty(allList)){
            return new HashMap<>();
        }

        Map<String,String> resultMap = new HashMap<>();
        allList.forEach(dwsDimShopData -> {
            resultMap.put(dwsDimShopData.getShopCode(),dwsDimShopData.getShopID());
        });

        return resultMap;
    }

    /**
     * 行信息处理
     * @param sheet
     * @param rowsData
     * @param totalCells
     * @param i
     * @param columnNumMap
     */
    private  String readRowData(Sheet sheet, List<ShopInfoData> rowsData, int totalCells, int i, Map<Integer,ImportConfigDO>  columnNumMap, List<Integer> columnNumList, Map<String,String> shopCodeAndIdMap) {
        Row row;
        String shopId = null;//遍历单元格
        int realRow = i + 1;
        row = sheet.getRow(i);

        if(row == null){
            return null;
        }
        //获取每一行的单元格数
        //获取每个单元格的数据，保存到集合中
        int emptyCellNum = 0 ;
        for (int t = 0; t < totalCells; t++) {
            Cell cell = row.getCell(t);
            if (cell == null) {
                row.createCell(t).setCellType(CellType.BLANK);
            }
            if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK || StringUtils.isEmpty((cell + "").trim())) {
                emptyCellNum++;
            }
        }
        // 整行为空
        if(emptyCellNum == totalCells){
            return null;
        }

        // 初始化配置信息
        List<String> columnValueList = new ArrayList<>();
        for(Integer columnNum : columnNumList){
            ImportConfigDO importConfigDO = columnNumMap.get(columnNum);

            // 设置单元格值类型
            Cell cellVal =  row.getCell(columnNum - 1);
            String cellStrValue = null;
            if(cellVal != null && "NUMBER".equalsIgnoreCase(importConfigDO.getTplColumnType())){
                // 先设置为String类型，用于判空
                if(StringUtils.isEmpty((cellVal + "").trim()) ||  cellVal.getCellType() == Cell.CELL_TYPE_BLANK){
                    throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列是数值类型，不能为空");
                }

                // 数字类型
                try{
                    cellStrValue = cellVal.getNumericCellValue() + "";
                }catch (Exception e){
                    throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列为非数值类型");
                }
            }else if (cellVal != null && "STRING".equalsIgnoreCase(importConfigDO.getTplColumnType())){
                // 字符类型
                cellVal.setCellType(CellType.STRING);
                cellStrValue = cellVal.getStringCellValue();
            }else{
                //其他
                if(cellVal == null){
                    cellStrValue = "";
                }else{
                    cellStrValue = cellVal.getStringCellValue();
                }
            }
            cellStrValue = cellStrValue.trim();//去掉前后空格

            // 数值校验
            if(StringUtils.isNotEmpty(importConfigDO.getCheckType())){
                if("NOT_NULL".equalsIgnoreCase(importConfigDO.getCheckType())){
                    if(StringUtils.isEmpty(cellStrValue)){
                        throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列信息为空");
                    }
                }else if ("NUMBER".equalsIgnoreCase(importConfigDO.getCheckType())){
                    if(!CheckUtil.checkDouble(cellStrValue)){
                        throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列非数字");
                    }
                }
            }

            // 门店特殊处理
            if ("shop_code".equalsIgnoreCase(importConfigDO.getColumnName().toLowerCase())) {
                shopId = shopCodeAndIdMap.get(cellStrValue);
                if(StringUtils.isEmpty(shopId)){
                    throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列门店编码" + cellStrValue + "找不到对应的门店ID");
                }
            }
            // 内外搭特殊处理
            /*if ("match_type".equalsIgnoreCase(importConfigDO.getColumnName().toLowerCase())) {
                Integer matchTypeNum = Constant.matchTypeMap.get(cellStrValue);
                if(matchTypeNum == null){
                    throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列内外搭" + cellStrValue + "找不到对应的字典值");
                }
                cellStrValue = matchTypeNum + "";
            }*/


            // 配置转换规则
            if(StringUtils.isNotEmpty(importConfigDO.getTransferJson())){
                Map transferMap = JSON.parseObject(importConfigDO.getTransferJson(),Map.class);
                if(transferMap.containsKey(cellStrValue)){
                    cellStrValue = transferMap.get(cellStrValue).toString();
                }else{
                    throw new BusinessException("第" + realRow + "行，第" + columnNum  + "列值=" + cellStrValue + " 找不到转换关系");
                }
            }

            columnValueList.add(cellStrValue);
        }

        if(shopCodeAndIdMap != null){
            columnValueList.add(shopId);
        }

        // 组装插入SQL
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("  (") .append(" ");
        for(int j = 0 ;j< columnValueList.size();j++){
            String obj = columnValueList.get(j);
            insertSql.append("'").append(CheckUtil.specialStrForMysql(obj.toString())).append("',");
        }
        insertSql.append("now())");

        // 插入信息
        LoggerUtil.info(logger,"动态插入数据SQL={0}",insertSql.toString());
        return insertSql.toString();
    }


    /**
     * 二次加工
     *
     * @param request
     * @return
     */
    @Transactional("allocationTransactionManager")
    public Result secProcess(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        //1、获取模板编码
        String templateCode = request.getParameter("templateCode");
        String limitNum = request.getParameter("limitNum");
        try{
            if(StringUtils.isEmpty(templateCode)){
                LoggerUtil.error(logger,"模板编码参数为空");
                throw new AllocationException("模板编码参数为空");
            }

            DownTemplateDO downTemplateDO = downTemplateDOMapper.queryByCode(templateCode);
            if(downTemplateDO == null){
                LoggerUtil.error(logger,"根据模板编码{0}找不到下载配置信息",templateCode);
                return ResultUtil.handleSysFailtureReturn("根据模板编码" + templateCode + "找不到下载配置信息");
            }

            ProcessService processService = importServiceMap.get(downTemplateDO.getServiceCode());
            processService.secProcess(downTemplateDO.getCode(),StringUtils.isEmpty(limitNum)?500:Integer.parseInt(limitNum));//默认500条
        }catch(Exception e) {
            LoggerUtil.error(logger, "二次加工错误={0}", StackUtil.getStackTrace(e));
            return ResultUtil.handleFailtureReturn(e.getMessage());
        }finally {
            logger.info("{} secProcess over,time use={}",templateCode,System.currentTimeMillis() - startTime);
        }

        return ResultUtil.handleSuccessReturn();
    }

    /**
     * 模板下载
     *
     * @param request
     * @param response
     * @return
     */
    public  String commonExport(HttpServletRequest request, HttpServletResponse response){
        long startTime = System.currentTimeMillis();

        //1、获取模板编码
        String templateCode = request.getParameter("templateCode");
        try{
            if(StringUtils.isEmpty(templateCode)){
                LoggerUtil.error(logger,"模板编码参数为空");
                throw new AllocationException("模板编码参数为空");
            }

            // 查询模板配置
            List<DownTemplateDO> downTemplateDOList = downTemplateDOMapper.queryDownLoadTplList(templateCode);
            if(CollectionUtils.isEmpty(downTemplateDOList)){
                LoggerUtil.error(logger,"根据模板编码{0}找不到下载配置信息",templateCode);
                return  "根据模板编码" + templateCode + "找不到下载配置信息";
            }

            String fileName = downTemplateDOList.get(0).getFileDir();//导出文件名称
            HSSFWorkbook workbook = new HSSFWorkbook();
            for(DownTemplateDO downTemplateDO : downTemplateDOList){
                // 查询导出配置列表
                List<ExportConfigDO> exportConfigDOList = exportConfigDOMapper.queryByTplCode(downTemplateDO.getExportCode());
                if(CollectionUtils.isEmpty(exportConfigDOList)){
                    LoggerUtil.error(logger,"找不到导出配置信息，模板信息={0}",JSON.toJSONString(downTemplateDO));
                    return "找不到导出配置信息，模板编码=" + downTemplateDO.getCode() + "，模板名称=" + downTemplateDO.getName();
                }

                // 2、执行方式
                if(StringUtils.isNotEmpty(downTemplateDO.getExeSql())){
                    downForExeSql(request, workbook, downTemplateDO, exportConfigDOList);
                }else if(StringUtils.isNotEmpty(downTemplateDO.getServiceCode())){
                    // 待扩展
                }
            }

            // 导出表格
            ExcelUtil.downloadExcel(fileName, workbook, request, response);
        }catch(Exception e) {
            LoggerUtil.error(logger, "导出数据异常={0}", StackUtil.getStackTrace(e));
            return "导出数据异常";
        }finally {
            logger.info("{} secProcess over,time use={}",templateCode,System.currentTimeMillis() - startTime);
        }

        return "886";
    }

    /**
     * 配置SQL配置
     *
     * @param request
     * @param workbook
     * @param downTemplateDO
     * @param exportConfigDOList
     */
    private void downForExeSql(HttpServletRequest request, HSSFWorkbook workbook, DownTemplateDO downTemplateDO, List<ExportConfigDO> exportConfigDOList) {
        String exeSql = downTemplateDO.getExeSql();
        // 获取请求参数，转换key/value模式
        Map<String, String[]> reqParameterMap = request.getParameterMap();
        LoggerUtil.info(logger,"请求的参数信息={0}", JSON.toJSONString(reqParameterMap));
        if(reqParameterMap != null && !reqParameterMap.isEmpty()){
            Iterator<String> iterator = reqParameterMap.keySet().iterator();
            while(iterator.hasNext()){
                String code = iterator.next();
                String[] values = reqParameterMap.get(code);
                if(values == null || values.length == 0){
                    continue;
                }
                // 替换执行参数
                exeSql = exeSql.replace("#{" + code + "}",values[0]);
            }
        }

        // 查询导出的数据
        List<Map> dataList = exportConfigDOMapper.exeSql(exeSql);

        // 初始化表头数据
        String sheetName =downTemplateDO.getName();//sheet名称

        List<String> title = new ArrayList<>();//列名
        HSSFSheet sheet = workbook.createSheet(sheetName);//cell单元格
        Map<String,Integer> colNameAndcolumnNumMap = new HashMap<>();//行编码和行
        Map<String,String> colNameAndTypeMap = new HashMap<>();//行编码和行
        exportConfigDOList.forEach(exportConfigDO -> {
            title.add(exportConfigDO.getTplColumnName());
            sheet.setColumnWidth(exportConfigDO.getTplColumnNum() - 1, exportConfigDO.getWidth());//设置cell单元格
            colNameAndcolumnNumMap.put(exportConfigDO.getColumnName(),exportConfigDO.getTplColumnNum());
            colNameAndTypeMap.put(exportConfigDO.getColumnName(),exportConfigDO.getTplColumnType());
        });

        HSSFRow titleRow = sheet.createRow(0);
        ExcelUtil.createTitle(workbook, titleRow, title);
        HSSFCellStyle style = ExcelUtil.setCellStyleDateFormat(workbook);

        // 初始化行数据
        int rowNum = 1;
        for (Map dataMap : dataList) {
            HSSFRow row = sheet.createRow(rowNum);
            Iterator iterator = dataMap.keySet().iterator();
            while(iterator.hasNext()){
                Object columnName = iterator.next();
                if(colNameAndcolumnNumMap.get(columnName) == null){
                    continue;
                }
                String colType = colNameAndTypeMap.get(columnName);
                Object colValue = dataMap.get(columnName);
                if("STRING".equalsIgnoreCase(colType)){
                    row.createCell(colNameAndcolumnNumMap.get(columnName) - 1).setCellValue(colValue == null?"":colValue.toString());
                }else if("NUMBER".equalsIgnoreCase(colType)){
                    row.createCell(colNameAndcolumnNumMap.get(columnName) - 1).setCellValue(colValue == null && StringUtils.isNotEmpty(colValue.toString())?0:Double.parseDouble(colValue.toString()));
                }else
                    row.createCell(colNameAndcolumnNumMap.get(columnName) - 1).setCellValue(colValue == null?"":colValue.toString());
            }
            rowNum++;
        }
    }
}
