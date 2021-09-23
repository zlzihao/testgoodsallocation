package cn.nome.saas.sdc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.saas.sdc.bigData.model.DwsViewWarehouseInfoVO;
import cn.nome.saas.sdc.bigData.repository.dao.DwsViewWarehouseInfoMapper;
import cn.nome.saas.sdc.bigData.repository.entity.DwsViewWarehouseInfoDO;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.enums.ReturnType;
import cn.nome.saas.sdc.manager.WarehouseConfigServiceManager;
import cn.nome.saas.sdc.model.excel.WareHouseEO;
import cn.nome.saas.sdc.model.form.WarehouseForm;
import cn.nome.saas.sdc.model.req.RegionsReq;
import cn.nome.saas.sdc.model.req.WarehouseReq;
import cn.nome.saas.sdc.model.vo.WarehouseVO;
import cn.nome.saas.sdc.repository.dao.RegionsMapper;
import cn.nome.saas.sdc.repository.dao.WareHouseConfigMapper;
import cn.nome.saas.sdc.repository.entity.RegionsDO;
import cn.nome.saas.sdc.repository.entity.WarehouseDO;
import cn.nome.saas.sdc.service.WarehouseConfigService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Service
public class WarehouseConfigServiceImpl implements WarehouseConfigService {
    private final Logger logger = LoggerFactory.getLogger(WarehouseConfigServiceImpl.class);

    private final WareHouseConfigMapper houseConfigMapper;
    private final RegionsMapper regionsMapper;
    private final WarehouseConfigServiceManager wareHouseConfigServiceManager;
    private final DwsViewWarehouseInfoMapper viewWarehouseInfoMapper;

    @Autowired
    public WarehouseConfigServiceImpl(WareHouseConfigMapper houseConfigMapper,
                                      RegionsMapper regionsMapper,
                                      WarehouseConfigServiceManager wareHouseConfigServiceManager,
                                      DwsViewWarehouseInfoMapper viewWarehouseInfoMapper) {
        this.houseConfigMapper = houseConfigMapper;
        this.regionsMapper = regionsMapper;
        this.wareHouseConfigServiceManager = wareHouseConfigServiceManager;
        this.viewWarehouseInfoMapper = viewWarehouseInfoMapper;
    }


    @Override
    public List<WarehouseVO> getPageList(WarehouseReq req, Page page) {
        if (page != null) {
            int count = houseConfigMapper.pageCount(BaseConvertor.convert(req, WarehouseDO.class));
            page.setTotalRecord(count);
        }
        return BaseConvertor.convertList(houseConfigMapper.getPageList(BaseConvertor.convert(req, WarehouseDO.class),
                page), WarehouseVO.class);
    }

    @Override
    public void exportExcel(HttpServletResponse response, Integer userCode) {
        XSSFWorkbook workbook = ExcelUtil.createTemplate("模板", 0, WareHouseEO.class);
        ResponseUtil.export(response, workbook, "example");
    }

    @Override
    public int importExcel(HttpServletResponse response, MultipartFile file) {
        InputStream is = null;
        int success = 0;
        try {
            is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            success = importSheetData(response, workbook);
        } catch (IOException e) {
            logger.error("importExcel error {}", e.getMessage());
            throw new BusinessException(ReturnType.IMPORT_EXCEL_FAIL.getType(), "模板导入失败");
        }

        return success;
    }

    @Override
    public int update(WarehouseForm form, Integer userCode) {
        WarehouseDO wDo = new WarehouseDO();
        wDo.setId(form.getId());
        List<WarehouseDO> checkList = houseConfigMapper.getByProvinceAndWareHouse(wDo);
        if (CollectionUtil.isEmpty(checkList)) {
            throw new BusinessException(ReturnType.VALIDATION_FAIL.getType(), "id不存在");
        }
        return houseConfigMapper.update(BaseConvertor.convert(form, WarehouseDO.class));
    }

    @Override
    public void delete(Long id, Integer userCode) {
        WarehouseDO houseDO = new WarehouseDO();
        houseDO.setId(id);
        houseDO.setIsDeleted(Constant.IS_DELETE_TRUE);
        houseDO.setUpdatedUserCode(userCode);
        houseConfigMapper.delete(houseDO);
    }

    private int importSheetData(HttpServletResponse response, XSSFWorkbook workbook) {
        XSSFWorkbook errBook = ExcelUtil.createTemplate("导出错误数据", 0, WareHouseEO.class);
        XSSFSheet errSheet = errBook.getSheet("导出错误数据");
        AtomicInteger success = new AtomicInteger();
        RegionsReq regionsReq = new RegionsReq();
        List<DwsViewWarehouseInfoDO> warehouseInfoDOList = viewWarehouseInfoMapper.list();
        Map<String, DwsViewWarehouseInfoDO> wareHouseMap =
                warehouseInfoDOList.stream().collect(Collectors.toMap(DwsViewWarehouseInfoDO::getStockName, Function.identity(),
                        (v1, v2) -> v1));
        //获取regions
        Map<String, RegionsDO> regionMap =
                regionsMapper.getList(regionsReq).stream().collect(Collectors.toMap(RegionsDO::getProvince, Function.identity(), (v1, v2) -> v1));

        List<WareHouseEO> wareHouseEOList = null;
        try {
            wareHouseEOList = ExcelUtil.readExcel(workbook, WareHouseEO.class);
        } catch (Exception e) {
            throw new BusinessException(ReturnType.IMPORT_EXCEL_FAIL.getType(), "导入数据格式与模板格式不匹配,请仔细核对格式！");
        }
        int countRow = 1;
        int checkRow = 1;
        for (WareHouseEO eo : wareHouseEOList) {
            boolean flag = false;
            XSSFRow errRow = errSheet.createRow(countRow);
            for (int i = 0; i < errSheet.getRow(0).getLastCellNum(); ++i) {
                if (!regionMap.containsKey(eo.getProvince()) && i == 0) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + eo.getProvince() + "省份不存在");
                    continue;
                }
                if (CollectionUtil.isEmpty(warehouseInfoDOList) && i == 1) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + eo.getWarehouse() + "不存在该仓库");
                    continue;
                }
                if (!CollectionUtil.isEmpty(warehouseInfoDOList) && !wareHouseMap.containsKey(eo.getWarehouse()) && i == 1) {
                    flag = true;
                    errRow.createCell(i).setCellValue("第" + checkRow + "行" + eo.getWarehouse() + "不存在该仓库");
                    continue;
                }
            }
            if (flag) {
                countRow++;
                continue;
            }
            errSheet.removeRow(errRow);
            WarehouseVO houseVO = new WarehouseVO();
            houseVO.setProvince(eo.getProvince());
            houseVO.setProvinceCode(regionMap.get(eo.getProvince()).getProvinceCode());
            houseVO.setWarehouse(eo.getWarehouse());
            houseVO.setWarehouseCode(wareHouseMap.get(eo.getWarehouse()).getStockCode());
            wareHouseConfigServiceManager.save(houseVO);
            success.getAndIncrement();
            checkRow++;
        }
        if (errSheet.getLastRowNum() >= 1) {
            ResponseUtil.export(response, errBook, "ErrorData");
        }
        return success.get();
    }

    public List<DwsViewWarehouseInfoVO> getAllWareHouse() {
        return BaseConvertor.convertList(viewWarehouseInfoMapper.list(), DwsViewWarehouseInfoVO.class);
    }
}
