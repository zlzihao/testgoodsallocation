package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.repository.dao.allocation.ImportConfigDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.QdIssueDepthSuggestDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.QdIssueDepthSuggestDO;
import cn.nome.saas.allocation.service.ProcessFactoryManager;
import cn.nome.saas.allocation.service.ProcessService;
import cn.nome.saas.allocation.utils.StackUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：godsfer
 * @date ：Created in 2019/8/13 11:42
 * @description：深度指引导入
 * @modified By：
 * @version: 1.0.0$
 */
@Service("depthSuggestProcessService")
public class DepthSuggestProcessServiceImpl extends ProcessFactoryManager implements ProcessService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ImportConfigDOMapper importConfigDOMapper;

    @Autowired
    private QdIssueDepthSuggestDOMapper qdIssueDepthSuggestDOMapper;

    @Override
    public int importData(Integer isClear, String fileName, MultipartFile file,String downTemplateCode)   throws Exception {
        InputStream is = file.getInputStream();
        Workbook wb = getWkb(fileName, is);
        Sheet sheet = wb.getSheetAt(0);
        List<ShopInfoData> rowsData = new ArrayList<ShopInfoData>();

        Row row = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        //先取得总列数
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();

        // 组装插入字段SQL
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(" insert into qd_issue_depth_suggest (type_name,match_type,level,depth,created_at) values ");
        LoggerUtil.info(logger,"动态插入字段SQL={0}",insertSql.toString());

        //从0开始
        List<Object> firstRowList = new ArrayList<>();//第一行数据
        List<List<Object>> otherRowDataList = new ArrayList<>();//其他行数据
        for (int i = 0; i < totalRows; i++) {
            List<Object> rowDataList = readRowData(sheet, rowsData, totalCells, i);
            if(rowDataList == null){
                continue;
            }

            if(i == 0){
                firstRowList.addAll(rowDataList);
            }else{
                otherRowDataList.add(rowDataList);
            }
        }

        // 开始组装SQL
        otherRowDataList.forEach(otherRowDatas->{
            String typeName = otherRowDatas.get(0).toString();//类型名称
            String matchTypeName = otherRowDatas.get(1).toString();//类别名称
            for(int j = 2;j < otherRowDatas.size();j++){
                insertSql.append("('").append(typeName).append("','").append(matchTypeName).append("',").append(firstRowList.get(j)).append(",").append(otherRowDatas.get(j)).append(",now()),");
            }
        });

        // 删除数据.
        if(isClear == 1){
            StringBuilder deleteSql = new StringBuilder();
            deleteSql.append(" delete from qd_issue_depth_suggest");
            importConfigDOMapper.deleteSql(deleteSql.toString());
        }

        // 插入数据
        importConfigDOMapper.insertSql(insertSql.toString().substring(0,insertSql.length() - 1));

        return NEED_DO;
    }

    /**
     * 行信息处理
     *
     * @param sheet
     * @param rowsData
     * @param totalCells
     * @param i
     * @return
     * @throws Exception
     */
    private  List<Object> readRowData(Sheet sheet, List<ShopInfoData> rowsData, int totalCells, int i) throws Exception{
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

        List<Object> rowDataList = new ArrayList<>();
        //获取每一行的单元格数
        //获取每个单元格的数据，保存到集合中
        for (int t = 0; t < totalCells; t++) {
            Cell cell = row.getCell(t);
            if (cell == null) {
                row.createCell(t).setCellType(CellType.BLANK);
            }

            Cell cellVal =  row.getCell(t);
            if(t == 0){
                cellVal.setCellType(CellType.STRING);
                String typeName = cellVal.getStringCellValue().trim();
                if(StringUtils.isEmpty(typeName)){
                    throw new BusinessException("第" + realRow + "行，第" + t  + "列为空或空字符");
                }
                rowDataList.add(typeName);
            }else if(t == 1){
                cellVal.setCellType(CellType.STRING);
                String matchType = cellVal.getStringCellValue().trim();
                if(StringUtils.isEmpty(matchType)){
                    throw new BusinessException("第" + realRow + "行，第" + t  + "列为空或空字符");
                }
                rowDataList.add(matchType);
            }else{
                try{
                    cellVal.setCellType(CellType.NUMERIC);
                    rowDataList.add(cellVal.getNumericCellValue());
                }catch (Exception e){
                    LoggerUtil.error(logger,"深度指引转换数值报错，错误信息={0}", StackUtil.getStackTrace(e));
                    throw new BusinessException("第" + realRow + "行，第" + t  + "列非数值");
                }

            }
        }

        return rowDataList;
    }

    /**
     * 二次加工
     *
     * @param downTemplateCode
     * @param limitNum
     */
    @Override
    public void secProcess(String downTemplateCode,int limitNum) {
        // 1、查询是否存在未处理的数据
        int countNum = qdIssueDepthSuggestDOMapper.countForDealStatus(0);
        if(countNum == 0){
            LoggerUtil.info(logger,"没有需要处理的数据");
            return;
        }
        // 2、存在则重新计算
        List<QdIssueDepthSuggestDO> qdIssueDepthSuggestDOList = qdIssueDepthSuggestDOMapper.queryAll();

        if(CollectionUtils.isEmpty(qdIssueDepthSuggestDOList)){
            return;
        }

        // 按matchType分组
        Map<String,List<QdIssueDepthSuggestDO>> matchTypeMap = qdIssueDepthSuggestDOList.stream().collect(Collectors.groupingBy(QdIssueDepthSuggestDO::getMatchType));

        // 循环处理
        Iterator<String> iterator = matchTypeMap.keySet().iterator();
        while(iterator.hasNext()){
            String matchtype = iterator.next();
            List<QdIssueDepthSuggestDO> qdIssueDepthSuggestDOs  = matchTypeMap.get(matchtype);
            try{
                BigDecimal totalDepth = BigDecimal.ZERO;// 总深度
                int levelNum = 0;//登记个数
                for(QdIssueDepthSuggestDO qdIssueDepthSuggestDO: qdIssueDepthSuggestDOs){
                    totalDepth = totalDepth.add(new BigDecimal(qdIssueDepthSuggestDO.getDepth()));
                    levelNum ++ ;
                }

                // 计算平均深度
                BigDecimal avgDepth = totalDepth.divide(new BigDecimal(levelNum), 2, RoundingMode.HALF_UP);
                for(QdIssueDepthSuggestDO qdIssueDepthSuggestDO: qdIssueDepthSuggestDOs){
                    qdIssueDepthSuggestDO.setAvgDepth(avgDepth.doubleValue());
                    qdIssueDepthSuggestDO.setRemark("成功");
                    qdIssueDepthSuggestDO.setIsDeal(1);//标记已处理
                }
            }catch (Exception e){
                LoggerUtil.error(logger,"处理深度指引报错，错误信息=",StackUtil.getStackTrace(e));
                for(QdIssueDepthSuggestDO qdIssueDepthSuggestDO: qdIssueDepthSuggestDOs){
                    qdIssueDepthSuggestDO.setRemark(e.getMessage().substring(0,999));
                }
            }
        }

        // 3、更新平均深度、处理状态
        qdIssueDepthSuggestDOMapper.batchUpdateInfo(qdIssueDepthSuggestDOList);
    }
}
