package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.feign.model.CategoriesVO;
import cn.nome.saas.allocation.feign.model.ShopMappingPositionForm;
import cn.nome.saas.allocation.feign.model.ShopMappingPositionVO;
import cn.nome.saas.allocation.manager.PdcManager;
import cn.nome.saas.allocation.manager.SdcAllShopsManager;
import cn.nome.saas.allocation.model.allocation.ShopToStockExportVO;
import cn.nome.saas.allocation.model.allocation.ShopToStockVo;
import cn.nome.saas.allocation.model.excel.OperateEO;
import cn.nome.saas.allocation.model.form.ShopToStockForm;
import cn.nome.saas.allocation.model.form.ShopToStockOperateInsertForm;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.model.req.ShopToStockReq;
import cn.nome.saas.allocation.model.vo.ShopOperateVO;
import cn.nome.saas.allocation.repository.dao.allocation.ShopInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ShopOperateMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ShopToStockDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ShopOperateDO;
import cn.nome.saas.allocation.repository.entity.allocation.ShopToStockDo;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizihao@nome.com
 */
@Service
public class ShopOperateService {
    private static Logger logger = LoggerFactory.getLogger(ShopOperateService.class);
    @Autowired
    ShopOperateMapper operateMapper;
    @Autowired
    ShopToStockDOMapper stockDOMapper;
    @Autowired
    private PdcManager pdcManager;
    @Autowired
    private ShopInfoDOMapper shopInfoDOMapper;
    @Autowired
    private ShopToStockService stockService;
    @Autowired
    private SdcAllShopsManager sdcAllShopsManager;


    public List<ShopOperateVO> getList(Page page) {
        int count = operateMapper.getCount();
        if (count >= 0) {
            page.setTotalRecord(count);
        }
        return BaseConvertor.convertList(operateMapper.getList(page), ShopOperateVO.class);
    }


    public void delete(List<Integer> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("1001", "删除记录id不能为空");
        }
        if (operateMapper.delete(list) < 0) {
            throw new BusinessException("1001", "operateMapper删除记录失败");
        }
        if (stockDOMapper.delete(list) < 0) {
            throw new BusinessException("1001", "stockDOMapper删除失败");
        }
    }

    public void detailDelete(List<Integer> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("1001", "删除记录id不能为空");
        }
        if (stockDOMapper.delete(list) < 0) {
            throw new BusinessException("1001", "stockDOMapper删除失败");
        }
    }


    /*
     * 根据operateId 查询仓位列表
     * */
    public List<ShopToStockVo> selectByOperate(Integer operateId, Page page) {
        if (operateId == null) {
            throw new BusinessException("1001", "operateId为空");
        }
        ShopToStockReq req = new ShopToStockReq();
        req.setOperateId(operateId);
        Integer count = null;
        if (page != null) {
            count = stockDOMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        return BaseConvertor.convertList(stockDOMapper.selectByOperate(operateId, page), ShopToStockVo.class);
    }

    /*
     * @describe  保存未提交
     * */
    public void save(ShopToStockOperateInsertForm forms) {
        List<ShopToStockDo> shopToStockDos = commonSave(forms, Constant.DEFAULT_NOT_SAVE_STATUS);
        List<ShopToStockDo> insertList = new ArrayList<ShopToStockDo>();
        List<ShopToStockDo> updateList = new ArrayList<ShopToStockDo>();
        for (ShopToStockDo check : shopToStockDos) {
            if (check.getId() == null) {
                insertList.add(check);
                continue;
            }
            updateList.add(check);
        }
        if (!CollectionUtils.isEmpty(insertList) && stockDOMapper.batchInsertByOperate(insertList) < 0) {
            throw new BusinessException("1001", "保存失败");
        }
        if (!CollectionUtils.isEmpty(updateList) && stockDOMapper.batchUpdate(updateList) < 0) {
            throw new BusinessException("1001", "更新失败");
        }
    }


    public String commit(ShopToStockOperateInsertForm forms) {
        //先保存operate_change_stock_count
        List<ShopToStockDo> shopToStockDos = commonSave(forms, Constant.DEFAULT_COMMIT_SAVE_STATUS);
        List<ShopToStockForm> formList = forms.getShopForms();
        if (CollectionUtils.isEmpty(formList)) {
            throw new BusinessException("1001", "[ShopForms]记录不能为空");
        }
        Set<String> shopCodes = new HashSet<String>();
        shopToStockDos.forEach(vos -> {
            vos.setIsNewStatus(Constant.DEFAULT_EN_NEW_STATUS);
            vos.setIsRead(Constant.DEFAULT_NOT_READ);
            shopCodes.add(vos.getShopCode());
        });
        Map<String, List<ShopToStockDo>> shopMap = shopToStockDos.stream().collect(Collectors.groupingBy(ShopToStockDo::getShopCode));
        String err = "";
        List<ShopToStockDo> filterList = new ArrayList<ShopToStockDo>();
        for (String shopCode : shopCodes) {
            List<ShopToStockDo> list = shopMap.get(shopCode);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            err = batchUpdate(list);
            if (err != null && err != "") {
                err.concat(err);
                continue;
            }
            filterList.addAll(list);
        }

        ShopMappingPositionForm form = new ShopMappingPositionForm();
        List<ShopMappingPositionVO> mappingPositionVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filterList)) {
            //如果 shopToStock记录存在则更新，不存在则插入
            List<ShopToStockDo> insertList = new ArrayList<ShopToStockDo>();
            List<ShopToStockDo> updateList = new ArrayList<ShopToStockDo>();
            for (ShopToStockDo check : filterList) {
                if (check.getId() == null) {
                    insertList.add(check);
                    continue;
                }
                updateList.add(check);
                ShopMappingPositionVO shopMappingPositionVO = new ShopMappingPositionVO();
                shopMappingPositionVO.setCategoryId(check.getMidCategoryId());
                shopMappingPositionVO.setShopCode(check.getShopCode());
                shopMappingPositionVO.setPositionCoefficient(check.getNewStockNum());
                mappingPositionVOList.add(shopMappingPositionVO);
            }
            //更新仓位最新有效状态
            if (!CollectionUtils.isEmpty(filterList)) {
                stockDOMapper.batchUpdateNewStatusByOperate(filterList);
            }
            if (!CollectionUtils.isEmpty(insertList)) {
                stockDOMapper.batchInsertByOperate(insertList);
            }

            if (!CollectionUtils.isEmpty(updateList)) {
                stockDOMapper.batchUpdate(updateList);
            }
            form.setList(mappingPositionVOList);
            String responseFeign = sdcAllShopsManager.changePositionByShopCode(form);
            logger.info("[feign response shopManager]------", responseFeign);
        }
        return err;
    }


    public List<ShopToStockDo> getOldNum(String shopCode, List<Integer> midCategoryIds) {
        ShopToStockReq req = new ShopToStockReq();
        req.setShopCode(shopCode);
        req.setMidCategoryIds(midCategoryIds);
        req.setIsNewStatus(Constant.DEFAULT_EN_NEW_STATUS);
        List<ShopToStockDo> list = stockDOMapper.selectByCondition(req, null);
        return list;
    }


    public List<ShopToStockVo> getMessageByShop(String shopCode) {
        ShopToStockDo stockDo = new ShopToStockDo();
        stockDo.setShopCode(shopCode);
        stockDo.setIsRead(Constant.DEFAULT_NOT_READ);
        stockDo.setSaveStatus(Constant.DEFAULT_COMMIT_SAVE_STATUS);
        return BaseConvertor.convertList(stockDOMapper.selectByNotRead(stockDo), ShopToStockVo.class);
    }

    public void readMessageByShop(String shopCode) {
        if (shopCode == null || shopCode == "") {
            throw new BusinessException("1001", "门店编码不能为空");
        }
        if (stockDOMapper.updateByRead(shopCode) < 0) {
            throw new BusinessException("1001", "更新失败");
        }
        return;
    }


    public void exportMode(HttpServletResponse response) {
        Workbook workbook = ExcelUtil.createTemplate("运营仓位模板", 1, OperateEO.class);
        ResponseUtil.export(response, workbook, "运营仓位模板");
        return;
    }


    public List<ShopToStockExportVO> importExcel(MultipartFile file) {
        //查询所有大类和中类
        List<CategoriesVO> bigList = pdcManager.getBigCategory();
        Map<String, CategoriesVO> bigMap = bigList.stream().collect(Collectors.toMap(CategoriesVO::getCnName,
                Function.identity(), (v1, v2) -> v1));
        List<CategoriesVO> midList = pdcManager.getMidCategory();
        Map<String, CategoriesVO> midMap = midList.stream().collect(Collectors.toMap(CategoriesVO::getCnName,
                Function.identity(), (v1, v2) -> v1));
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException("1001", "解析导入文件失败");
        }
        List<OperateEO> list = ExcelUtil.readExcel(workbook, OperateEO.class);
        List<ShopToStockExportVO> convertList = BaseConvertor.convertList(list, ShopToStockExportVO.class);
        for (ShopToStockExportVO check : convertList) {
            if (check.getShopCode() == "" || check.getShopCode() == null) {
                throw new BusinessException("门店编码不能为空");
            }
            if (check.getShopName() == "" || check.getShopName() == null) {
                throw new BusinessException("门店名称不能为空");
            }
            if (check.getCategoryName() == "" || check.getCategoryName() == null) {
                throw new BusinessException("陈列大类不能为空");
            }
            if (check.getMidCategoryName() == "" || check.getMidCategoryName() == null) {
                throw new BusinessException("陈列中类不能为空");
            }
            if (check.getNewStockNum() == null) {
                throw new BusinessException("调整后仓位不能为空");
            }
            if (bigMap.containsKey(check.getCategoryName())) {
                check.setCategoryId(bigMap.get(check.getCategoryName()).getId());
                check.setMidCategoryId(midMap.get(check.getMidCategoryName()).getId());
            }
        }
        return convertList;
    }


    public void init() {
        ShopToStockReq record = new ShopToStockReq();
        record.setIsNewStatus(Constant.DEFAULT_EN_NEW_STATUS);
        record.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        List<ShopToStockDo> list = stockDOMapper.selectByCondition(record, null);
        ShopMappingPositionForm form = new ShopMappingPositionForm();
        List<ShopMappingPositionVO> positionVOList = new ArrayList<>();
        list.forEach(vos -> {
            ShopMappingPositionVO vo = new ShopMappingPositionVO();
            vo.setShopCode(vos.getShopCode());
            vo.setCategoryId(vos.getMidCategoryId());
            vo.setPositionCoefficient(vos.getNewStockNum());
            positionVOList.add(vo);
        });
        form.setList(positionVOList);
        sdcAllShopsManager.changePositionByShopCode(form);
        return;
    }

    private List<ShopToStockDo> commonSave(ShopToStockOperateInsertForm forms, Integer saveStatus) {
        // operate_change_stock_count 不存在则插入， 存在则更新
        List<ShopToStockDo> shopToStockDos = new ArrayList<ShopToStockDo>();
        Integer operateId = null;
        ShopOperateDO operateDO = new ShopOperateDO();
        operateDO.setId(forms.getId());
        operateDO.setReason(forms.getReason());
        operateDO.setRemark(forms.getRemark());
        operateDO.setStatus(saveStatus);
        operateDO.setType(Constant.DEFAULT_OPERATE_TYPE);
        operateDO.setIsRead(Constant.DEFAULT_NOT_READ);
        operateDO.setUserName(forms.getUserName());
        operateDO.setCreateAt(forms.getCreateAt() == null ? 0 : forms.getCreateAt());
        operateDO.setSumStockCount(forms.getSumStockCount());
        if (forms.getId() == null) {
            operateMapper.insert(operateDO);
            operateId = operateDO.getId();
        } else {
            operateId = forms.getId();
            //更新操作
            if (operateMapper.update(operateDO) < 0) {
                throw new BusinessException("1001", "更新失败");
            }
        }
        //shopToStock 记录
        ShopToStockDo stockDo = null;
        for (ShopToStockForm form : forms.getShopForms()) {
            stockDo = new ShopToStockDo();
            stockDo.setId(form.getId());
            stockDo.setCategoryId(form.getCategoryId());
            stockDo.setCategoryName(form.getCategoryName());
            stockDo.setMidCategoryId(form.getMidCategoryId());
            stockDo.setMidCategoryName(form.getMidCategoryName());
            stockDo.setIsNewStatus(saveStatus == Constant.DEFAULT_COMMIT_SAVE_STATUS ? Constant.DEFAULT_EN_NEW_STATUS : Constant.DEFAULT_IS_NEW_STATUS);
            stockDo.setType(Constant.DEFAULT_OPERATE_TYPE);
            stockDo.setReason(forms.getReason());
            stockDo.setStatus(1);
            stockDo.setShopCode(form.getShopCode());
            stockDo.setShopName(form.getShopName());
            stockDo.setOldStockNum(form.getOldStockNum());
            stockDo.setNewStockNum(form.getNewStockNum());
            stockDo.setSaveStatus(saveStatus);
            stockDo.setOperateId(operateId);
            stockDo.setUserName(form.getUserName());
        }
        shopToStockDos.add(stockDo);
        return shopToStockDos;

    }

    private String batchUpdate(List<ShopToStockDo> list) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(list)) {
            throw new BusinessException("1001", "传参为空");
        }
        // 获取前台类目对应后台类目顶类 取shopCode查已有仓位设置 提取中类id
        String shopCode = list.stream().findAny().orElse(new ShopToStockDo()).getShopCode();
        ShopToStockReq req = new ShopToStockReq();
        req.setShopCode(shopCode);
        req.setIsNewStatus(1);
        List<ShopToStockDo> stockInfoList = stockDOMapper.selectByCondition(req, null);
        Set<Integer> midCategoryIds = stockInfoList.stream().map(ShopToStockDo::getMidCategoryId).collect(Collectors.toSet());
        // 再取传参里的中类id 和上面已存在的做并集
        Set<Integer> toUpdateMidCategoryIds = list.stream().map(ShopToStockDo::getMidCategoryId).collect(Collectors.toSet());
        midCategoryIds.addAll(toUpdateMidCategoryIds);
        // 获得该门店所需前台类目id对应后台类目顶类的map
        Map<Integer, Integer> receptionMapTop = pdcManager.getTopCategoryTypeByDisplayReceptionCategoryIds(new ArrayList<>(midCategoryIds));
        // 获取门店设置的百货仓位和服装仓位
        Map<String, Object> param = new HashMap<>();
        param.put("shopCode", shopCode);
        ShopInfoData shopInfo = shopInfoDOMapper.selectByPage(param).stream().filter(s -> s.getShopCode().equals(shopCode)).findFirst().orElse(null);
        if (shopInfo == null) {
            throw new BusinessException("1001", "总仓位超出仓位上限【仓位上限0】，请调整后再提交");
        }
        if (shopInfo.getClothSpace() == null || shopInfo.getClothSpace().equals(BigDecimal.ZERO) || shopInfo.getClothSpace().equals(new BigDecimal("0.00"))) {
            throw new BusinessException("1001", "服装总仓位超出仓位上限【服装仓位上限0】，请调整后再提交");
        }
        if (shopInfo.getCommoditySpace() == null || shopInfo.getCommoditySpace().equals(BigDecimal.ZERO) || shopInfo.getCommoditySpace().equals(new BigDecimal("0.00"))) {
            throw new BusinessException("1001", "百货总仓位超出仓位上限【百货仓位上限0】，请调整后再提交");
        }
        // 统计传入数量
        BigDecimal clotNum = BigDecimal.ZERO;
        BigDecimal gmNum = BigDecimal.ZERO;
        for (ShopToStockDo stockDo : list) {
            Integer topCategoryId = receptionMapTop.get(stockDo.getMidCategoryId());
            if (topCategoryId == null) {
                continue;
            }
            if (topCategoryId.equals(Constant.TOP_CATEGORY_ID_CLOTHING)) {
                clotNum = clotNum.add(stockDo.getNewStockNum());
            } else {
                gmNum = gmNum.add(stockDo.getNewStockNum());
            }
        }
        // 统计已在库数量
        stockInfoList = stockInfoList.stream().filter(s -> !toUpdateMidCategoryIds.contains(s.getMidCategoryId())).collect(Collectors.toList());
        for (ShopToStockDo stockDo : stockInfoList) {
            Integer topCategoryId = receptionMapTop.get(stockDo.getMidCategoryId());
            if (topCategoryId == null) {
                continue;
            }
            if (topCategoryId.equals(Constant.TOP_CATEGORY_ID_CLOTHING)) {
                clotNum = clotNum.add(stockDo.getNewStockNum());
            } else {
                gmNum = gmNum.add(stockDo.getNewStockNum());
            }
        }
        // 最后校验
        logger.info("[batchUpdate] clotSpace: {}, gmSpace: {},clotNum: {},gmNum: {}", shopInfo.getClothSpace(), shopInfo.getCommoditySpace(), clotNum, gmNum);
        boolean clotFlag = false;
        boolean gmFlag = false;
        if (shopInfo.getClothSpace().compareTo(clotNum) < 0) {
            clotFlag = true;
        }
        if (shopInfo.getCommoditySpace().compareTo(gmNum) < 0) {
            gmFlag = true;
        }
        if (clotFlag && gmFlag) {
            return "百货、服装仓位超出仓位上限【百货仓位上限" + shopInfo.getCommoditySpace() + "、服装仓位上限" + shopInfo.getClothSpace() + "】，请调整后提交";
        } else if (clotFlag) {
            return "服装总仓位超出仓位上限【服装仓位上限" + shopInfo.getClothSpace() + "】，请调整后再提交";
        } else if (gmFlag) {
            return "百货总仓位超出仓位上限【百货仓位上限" + shopInfo.getCommoditySpace() + "】，请调整后再提交";
        }
        // do insert
        logger.info("[batchUpdate] list: {}", JSON.toJSONString(list));


        return null;
    }
}
