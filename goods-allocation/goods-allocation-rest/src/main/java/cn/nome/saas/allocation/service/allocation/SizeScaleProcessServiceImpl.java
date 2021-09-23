package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.ImportConfigDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import cn.nome.saas.allocation.service.ProcessFactoryManager;
import cn.nome.saas.allocation.service.ProcessService;
import cn.nome.saas.allocation.utils.CheckUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/12 17:58
 * @description：区域尺码比例导入
 * @modified By：
 * @version: 1.0.0$
 */
@Service("sizeScaleProcessService")
public class SizeScaleProcessServiceImpl extends ProcessFactoryManager implements ProcessService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ImportConfigDOMapper importConfigDOMapper;

    @Override
    public int importData(Integer isClear, String fileName, MultipartFile file, String downTemplateCode)   throws Exception {
        // 根据文件名查询配置信息
        List<ImportConfigDO> importConfigDOList = importConfigDOMapper.queryByTplCode(downTemplateCode);
        if(CollectionUtils.isEmpty(importConfigDOList)){
            LoggerUtil.error(logger,"找不到导入配置信息，模板信息={0}",downTemplateCode);
            throw new BusinessException("找不到导入配置信息，模板编码=" + downTemplateCode);
        }

        // 开始处理表格
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ShopInfoData> rowsData = new ArrayList<ShopInfoData>();

        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();

        // 配置转换成列配置
        Map<Integer,String> colNumAndcolNameMap = readRowDataForTitle(sheet,rowsData,totalCells,2);
        Map<Integer,ImportConfigDO> columnNumMap = new HashMap<>();
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

        // 组装插入字段SQL
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(" insert into ").append(tableName).append (" ( ");
        insertSql.append(" ").append(org.apache.commons.lang3.StringUtils.join(columnNameList,",")).append(",size_name,percentage,created_at").append(" ) values ");
        LoggerUtil.info(logger,"动态插入字段SQL={0}",insertSql.toString());

        //去除标题，从3开始
        for (int i = 3; i < totalRows; i++) {
            String returnSql = readRowData(sheet, rowsData, totalCells, i,columnNumMap,columnNumList);
            if(returnSql == null){
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

        return NEED_DO;
    }

    /**
     * 行信息处理
     * @param sheet
     * @param rowsData
     * @param totalCells
     * @param i
     * @param columnNumMap
     */
    private  String readRowData(Sheet sheet, List<ShopInfoData> rowsData, int totalCells, int i, Map<Integer,ImportConfigDO>  columnNumMap, List<Integer> columnNumList) throws Exception{
        Row row = sheet.getRow(i);
        int realRow = i + 1;
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

        // 获取码段
        Cell cellValSize =  row.getCell(4);
        cellValSize.setCellType(CellType.STRING);
        String sizeSizeValue = cellValSize.getStringCellValue().trim();
        if(StringUtils.isEmpty(sizeSizeValue)){
            throw new BusinessException("第" + realRow + "行，第5列码段不能为空");
        }
        List<String> sizeList = Constant.sizeScalseMap.get(sizeSizeValue);
        if(CollectionUtils.isEmpty(sizeList)){
            throw new BusinessException("第" + realRow + "行，第5列码段找不到对应尺码列表");
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
                if(cellVal != null){
                    cellStrValue = "";
                }else{
                    cellStrValue = cellVal.getStringCellValue();
                }
            }
            cellStrValue = cellStrValue.trim();//去掉前后空格

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
            columnValueList.add(cellStrValue);
        }

        // 组装插入SQL
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("  (") .append(" ");
        for(int j = 0 ;j< columnValueList.size();j++){
            String obj = columnValueList.get(j);
            insertSql.append("'").append(CheckUtil.specialStrForMysql(obj.toString())).append("',");
        }

        // 组装扩展SQL
        StringBuilder insertSqlEx = new StringBuilder();
        for(int j = 0;j<sizeList.size();j++){
            String size = sizeList.get(j);
            Cell cellSizeValue =  row.getCell(j + 6);
            cellSizeValue.setCellType(CellType.NUMERIC);
            //判空
            if(cellSizeValue == null){
                continue;
            }

            //判空字符
            double sizeValue = cellSizeValue.getNumericCellValue();
            if(sizeValue == 0){
                continue;
            }

            insertSqlEx.append(insertSql.toString()).append("'").append(size).append("',").append(sizeValue).append(",now()),");
        }

        // 插入信息
        LoggerUtil.info(logger,"动态插入数据SQL={0}",insertSqlEx.toString());
        return insertSqlEx.toString().substring(0,insertSqlEx.length() - 1);
    }

    @Override
    public void secProcess(String downTemplateCode,int limitNum) {
        // 无需二次加工
    }
}
