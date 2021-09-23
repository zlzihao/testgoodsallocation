package cn.nome.saas.sdc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.saas.sdc.bigData.repository.dao.DwsGoalDayShopCategoryMapper;
import cn.nome.saas.sdc.bigData.repository.entity.DwsGoalDayShopCategoryDO;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.enums.SeasonType;
import cn.nome.saas.sdc.manager.SeasonChangeServiceManager;
import cn.nome.saas.sdc.model.excel.SeasonChangeEO;
import cn.nome.saas.sdc.model.form.SeasonChangeForm;
import cn.nome.saas.sdc.model.req.SeasonChangeReq;
import cn.nome.saas.sdc.model.req.ShopsReq;
import cn.nome.saas.sdc.model.vo.SeasonChangeVO;
import cn.nome.saas.sdc.repository.dao.SeasonChangeMapper;
import cn.nome.saas.sdc.repository.dao.ShopsMapper;
import cn.nome.saas.sdc.repository.entity.SeasonChangeDO;
import cn.nome.saas.sdc.repository.entity.ShopsDO;
import cn.nome.saas.sdc.service.SeasonChangeService;
import cn.nome.saas.sdc.util.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Service
public class SeasonChangeServiceImpl implements SeasonChangeService {
    private final Logger logger = LoggerFactory.getLogger(SeasonChangeServiceImpl.class);

    private final SeasonChangeMapper seasonChangeMapper;
    private final SeasonChangeServiceManager changeServiceManager;
    private final ShopsMapper shopsMapper;
    private final DwsGoalDayShopCategoryMapper categoryMapper;

    @Autowired
    public SeasonChangeServiceImpl(SeasonChangeMapper seasonChangeMapper,
                                   SeasonChangeServiceManager changeServiceManager,
                                   ShopsMapper shopsMapper,
                                   DwsGoalDayShopCategoryMapper categoryMapper) {
        this.seasonChangeMapper = seasonChangeMapper;
        this.changeServiceManager = changeServiceManager;
        this.shopsMapper = shopsMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<SeasonChangeVO> getPageList(SeasonChangeReq req, Page page) {
        List<SeasonChangeVO> seasonChangeVOList= new ArrayList<SeasonChangeVO>();
        if (page != null) {
            int count = seasonChangeMapper.pageCount(BaseConvertor.convert(req, SeasonChangeDO.class));
            page.setTotalRecord(count);
        }

        List<SeasonChangeDO> changeDOList = seasonChangeMapper.getPageList(BaseConvertor.convert(req,
                SeasonChangeDO.class), page);
        if (CollectionUtils.isEmpty(changeDOList)) {
            return seasonChangeVOList;
        }
        List<DwsGoalDayShopCategoryDO> categoryDOList = categoryMapper.getGoalDay(changeDOList);
        //取前七天 和后七天的销售销售总额
        changeDOList.forEach(dos -> {
            List<DwsGoalDayShopCategoryDO> list =
                    categoryDOList.stream().filter(s -> s.getShopCode().equals(dos.getShopCode())).collect(Collectors.toList());
            if (!CollectionUtil.isEmpty(list)) {
                double nextOptional =
                        list.stream().filter(over -> (over.getOperationDate().getTime() < (dos.getSeasonsAlternateDay().getTime() + (1000 * 3600 * 24) * 7) &&
                                over.getOperationDate().getTime() >= dos.getSeasonsAlternateDay().getTime())).mapToDouble(DwsGoalDayShopCategoryDO::getGoalDay).sum();
                double preOptional =
                        list.stream().filter(over -> (over.getOperationDate().getTime() >= (dos.getSeasonsAlternateDay().getTime() - (1000 * 3600 * 24) * 7) &&
                                over.getOperationDate().getTime() < dos.getSeasonsAlternateDay().getTime())).mapToDouble(DwsGoalDayShopCategoryDO::getGoalDay).sum();
                dos.setTargetNext(new BigDecimal(nextOptional).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                dos.setTargetPre(new BigDecimal(preOptional).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                //转季系数
                dos.setSeasonsAlternateCoefficient(dos.getSeasonsAlternateCoefficient().compareTo(BigDecimal.ZERO) == 0 ?
                        (dos.getTargetNext() == 0 || dos.getTargetPre() == 0 ? new BigDecimal("0.0") :
                                new BigDecimal((double) dos.getTargetNext() / dos.getTargetPre()).setScale(1, BigDecimal.ROUND_HALF_UP)) :
                        dos.getSeasonsAlternateCoefficient().setScale(1, BigDecimal.ROUND_HALF_UP));
            }
            SeasonChangeVO convertVO=BaseConvertor.convert(dos,SeasonChangeVO.class);
            convertVO.setSeasonsAlternateDay(DateUtil.format(dos.getSeasonsAlternateDay(),"yyyy-MM-dd"));
            seasonChangeVOList.add(convertVO);
        });
        return seasonChangeVOList;
    }

    @Override
    public List<SeasonChangeVO> selectByCondition(SeasonChangeReq req) {
        List<SeasonChangeVO> list = getPageList(req, null);
        List<ShopsDO> allShops = shopsMapper.queryAll(new ShopsReq());
        Map<String, SeasonChangeVO> shopMap = list.stream().collect(Collectors.toMap(SeasonChangeVO::getShopCode,
                Function.identity(), (v1, v2) -> v1));
        if(CollectionUtils.isEmpty(list)){
            allShops.forEach(vos -> {
                    SeasonChangeVO vo = new SeasonChangeVO();
                    vo.setShopCode(vos.getShopCode());
                    vo.setShopName(vos.getShopName());
                    vo.setSeasonsAlternateDay(DateUtil.format(req.getSeasonsAlternateDay(),"yyyy-MM-dd"));
                    vo.setSeasonsAlternateCoefficient(new BigDecimal("1.0"));
                    list.add(vo);
            });
        }else{
            allShops.forEach(vos -> {
                if (!shopMap.containsKey(vos.getShopCode())) {
                    SeasonChangeVO vo = new SeasonChangeVO();
                    vo.setShopCode(vos.getShopCode());
                    vo.setShopName(vos.getShopName());
                    vo.setSeasonsAlternateDay(DateUtil.format(req.getSeasonsAlternateDay(),"yyyy-MM-dd"));
                    vo.setSeasonsAlternateCoefficient(new BigDecimal("1.0"));
                    list.add(vo);
                }
            });
        }
        return list;
    }

    @Override
    public void exportExcel(HttpServletResponse response, Integer userCode) {
        XSSFWorkbook workbook;
        try {
            workbook = ExcelUtil.createTemplate("模板", 0, SeasonChangeEO.class);
        } catch (Exception e) {
            throw new BusinessException("1", "导入数据跟模板格式不对");
        }
        ResponseUtil.export(response, workbook, "example");
    }

    @Override
    public int importExcel(HttpServletResponse response, MultipartFile file) {
        int success = 0;
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            success = importSheetData(response, workbook);
        } catch (IOException e) {
            logger.error("importExcel error {}", e.getMessage());
            throw new BusinessException(ReturnType.IMPORT_EXCEL_FAIL.getType(), "模板导入失败");
        }

        return success;
    }

    @Override
    public int update(SeasonChangeForm form, Integer userCode) {
        SeasonChangeDO seasonChangeDO = BaseConvertor.convert(form, SeasonChangeDO.class);
        seasonChangeDO.setCreateUserCode(userCode);
        return seasonChangeMapper.update(seasonChangeDO);
    }

    @Override
    public int deleted(long id, Integer userCode) {
        SeasonChangeDO changeDO = new SeasonChangeDO();
        changeDO.setId(id);
        List<SeasonChangeDO> checkList = seasonChangeMapper.getByCondition(changeDO);
        if (CollectionUtil.isEmpty(checkList)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), ReturnType.VALIDATION_FAIL.getMsg());
        }
        changeDO.setIsDeleted(Constant.IS_DELETE_TRUE);
        changeDO.setUpdatedUserCode(userCode);
        changeDO.setGmtUpdated(new Date());
        return seasonChangeMapper.deleted(changeDO);
    }

    private int importSheetData(HttpServletResponse response, XSSFWorkbook workbook) {
        XSSFWorkbook errBook = ExcelUtil.createTemplate("导出错误数据", 0, SeasonChangeEO.class);
        XSSFSheet errSheet = errBook.getSheet("导出错误数据");
        AtomicInteger success = new AtomicInteger();
        List<SeasonChangeEO> seasonChangeEOS = null;
        List<SeasonChangeDO> updateList = new ArrayList<SeasonChangeDO>();
        try {
            seasonChangeEOS = ExcelUtil.readExcel(workbook, SeasonChangeEO.class);
        } catch (Exception e) {
            throw new BusinessException(ReturnType.IMPORT_EXCEL_FAIL.getType(), "导入的数据与模板格式不匹配,请仔细核对");
        }
        Map<String, String> shopMap = shopsMapper.queryAll(new ShopsReq()).stream().collect(Collectors.toMap(ShopsDO::getShopCode,
                ShopsDO::getShopName, (v1, v2) -> v1));
        int countRow = 1;
        int checkRow = 1;
        for (SeasonChangeEO eo : seasonChangeEOS) {
            boolean flag = false;
            XSSFRow errRow = errSheet.createRow(countRow);

            for (int i = 0; i < errSheet.getRow(0).getLastCellNum(); ++i) {
                if (!shopMap.containsKey(eo.getShopCode()) && i == 0) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + "门店编号不存在");
                    continue;
                }
                if (!shopMap.containsValue(eo.getShopName()) && i == 1) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + "门店名称不存在");
                    continue;
                }
                if (!(StringUtils.stringConvertDateFormat("yyyy", eo.getYear())) && i == 2) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + "年份格式不正确");
                    continue;
                }
                if (!SeasonType.getSeason().contains(eo.getSeasonsAlternate()) && i == 3) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + "不存在该季节");
                    continue;
                }
                if (!StringUtils.stringConvertBigDecimal(eo.getSeasonsAlternateCoefficient()) && i == 5) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + "转季系数必须为数字");
                    continue;
                }

            }
            if (flag) {
                errRow.createCell(0).setCellValue(eo.getShopCode());
                countRow++;
                checkRow++;
                continue;
            }
            errSheet.removeRow(errRow);
            SeasonChangeDO seasonChangeDO = BaseConvertor.convert(eo, SeasonChangeDO.class);
            seasonChangeDO.setSeasonsAlternateCoefficient(eo.getSeasonsAlternateCoefficient() == null ?
                    new BigDecimal("0.0") : new BigDecimal(eo.getSeasonsAlternateCoefficient()).setScale(1, BigDecimal.ROUND_HALF_UP));
            updateList.add(seasonChangeDO);
            success.getAndIncrement();
            checkRow++;
        }
        if (!CollectionUtils.isEmpty(updateList)) {
            changeServiceManager.save(updateList);
        }
        if (errSheet.getLastRowNum() >= 1) {
            ResponseUtil.export(response, errBook, "ErrorData");
        }
        return success.get();
    }
}
