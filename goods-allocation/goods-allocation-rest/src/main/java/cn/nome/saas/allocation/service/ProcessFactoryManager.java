package cn.nome.saas.allocation.service;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.entity.allocation.ImportConfigDO;
import cn.nome.saas.allocation.utils.CheckUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/12 18:19
 * @description：导入导出公共管理
 * @modified By：
 * @version: 1.0.0$
 */
public class ProcessFactoryManager {
    public static int NO_NEED_DO = -1;//无需处理
    public static int NEED_DO = 1;//需处理
    public Workbook getWkb(String fileName, InputStream is) throws IOException {
        Workbook wb = null;
        boolean isExcel2003 = isExcel2003(fileName);
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        return wb;
    }

    public boolean isExcel2003(String fileName) {
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        return isExcel2003;
    }

    /**
     * 获取指定头列的列号和名称
     *
     * @param sheet
     * @param rowsData
     * @param totalCells
     * @param i
     * @return
     */
    public  Map<Integer,String> readRowDataForTitle(Sheet sheet, List<ShopInfoData> rowsData, int totalCells, int i) {
        Row row = sheet.getRow(i);
        Map<Integer,String> colNumAndcolNameMap = new HashMap<>();

        if(row == null){
            return new HashMap<>();
        }

        //获取每个单元格的数据，保存到集合中
        for (int t = 0; t < totalCells; t++) {
            Cell cell = row.getCell(t);
            if (cell == null) {
                row.createCell(t).setCellType(CellType.BLANK);
            }
            colNumAndcolNameMap.put(t + 1, row.getCell(t).toString());
        }

        return colNumAndcolNameMap;
    }

    /**
     * 检查是否有满足的表头
     *
     * @param importConfigDO
     * @param colNumAndcolNameMap
     */
    public void checkColTitle(ImportConfigDO importConfigDO,Map<Integer,String> colNumAndcolNameMap){
        // 校验导入列和配置模板是否一致
        if(colNumAndcolNameMap.get(importConfigDO.getTplColumnNum()) == null){
            throw new BusinessException("第1行，第" + importConfigDO.getTplColumnNum()  + "列找不到指定列头，请按照导入模板规范，重新导入。" );
        }

        // 校验导入列和配置模板是否一致
        if( !importConfigDO.getTplColumnName().equals(colNumAndcolNameMap.get(importConfigDO.getTplColumnNum()))){
            throw new BusinessException("第1行，第" + importConfigDO.getTplColumnNum()  + "列列名不符合导入模板规范，请确认后，重新导入。导入的列名=" + colNumAndcolNameMap.get(importConfigDO.getTplColumnNum()) + ",配置的列名=" +  importConfigDO.getTplColumnName());
        }
    }
}
