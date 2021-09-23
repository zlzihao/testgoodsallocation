package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.utils.excel.ExcelUtil;
import cn.nome.platform.common.utils.excel.ResponseUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.feign.model.ShopMappingPositionForm;
import cn.nome.saas.allocation.feign.model.ShopMappingPositionVO;
import cn.nome.saas.allocation.feign.model.ShopsVO;
import cn.nome.saas.allocation.manager.PdcManager;
import cn.nome.saas.allocation.manager.SdcAllShopsManager;
import cn.nome.saas.allocation.model.allocation.CategoryDisplayInfoList;
import cn.nome.saas.allocation.model.allocation.MidCategoryDisplayInfoList;
import cn.nome.saas.allocation.model.allocation.ShopToStock;
import cn.nome.saas.allocation.model.allocation.ShopToStockVo;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.model.excel.CategoryPositionReportEO;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.model.req.ShopToStockReq;
import cn.nome.saas.allocation.repository.dao.allocation.ShopInfoDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ShopToStockDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ShopToStockDo;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ForbiddenRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class ShopToStockService {

    private static Logger logger = LoggerFactory.getLogger(ShopToStockService.class);

    @Autowired
    ShopToStockDOMapper shopToStockDoDOMapper;

    @Autowired
    private SdcAllShopsManager shopsManager;

    @Autowired
    private PdcManager pdcManager;

    @Autowired
    private ShopInfoDOMapper shopInfoDOMapper;


    public SelectByPageResult selectByParam(Integer status, Set shopCode, Integer page, Integer pageSize) {
        Map<String, Object> param = new HashMap<>(16);
        if (status != null) {
            param.put("status", status);
        }
        if (CollectionUtils.isNotEmpty(shopCode)) {
            param.put("shopCode", shopCode);
        }
        param.put("offset", (page - 1) * pageSize);
        param.put("pageSize", pageSize);
        int total = shopToStockDoDOMapper.getCount(param);
        List<ShopToStockDo> list = shopToStockDoDOMapper.selectByPage(param);
        if (CollectionUtils.isEmpty(list)) {
            SelectByPageResult<ShopToStockDo> paramList = new SelectByPageResult<>();
            paramList.setTotal(total);
            paramList.setList(new ArrayList<ShopToStockDo>());
            int totalPage;
            if (total % pageSize == 0) {
                totalPage = total / pageSize;
            } else {
                totalPage = (total / pageSize) + 1;
            }
            paramList.setTotalPage(totalPage);
            return paramList;
        }
        List<ShopsVO> allShopsAttributes = shopsManager.allShopsAttribute();
        if (!CollectionUtils.isEmpty(allShopsAttributes)) {
            Map<String, ShopsVO> shopsVOList =
                    allShopsAttributes.stream().collect(Collectors.toMap(ShopsVO::getShopCode,
                            Function.identity(), (v1, v2) -> v1));
            if (!CollectionUtils.isEmpty(list)) {
                list.stream().forEach(vos -> {
                    if (shopsVOList.containsKey(vos.getShopCode())) {
                        vos.setProvinceAndArea(shopsVOList.get(vos.getShopCode()).getProvince()
                                .concat(shopsVOList.get(vos.getShopCode()).getCity())
                                .concat(shopsVOList.get(vos.getShopCode()).getDistinct()));
                        vos.setChannelAreaId(shopsVOList.get(vos.getShopCode()).getChannelAreaId());
                        vos.setChannelAreaName(shopsVOList.get(vos.getShopCode()).getChannelAreaName() == null ? "" : shopsVOList.get(vos.getShopCode()).getChannelAreaName());
                    }
                });
            }
        }
        SelectByPageResult<ShopToStockDo> result = new SelectByPageResult<>();
        result.setTotal(total);
        result.setList(list);
        int totalPage;
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = (total / pageSize) + 1;
        }
        result.setTotalPage(totalPage);
        return result;
    }

    public void shopToStockAdd(ShopToStock shopToStock) {

        List<ShopToStockVo> doList = new ArrayList<>();
        List<CategoryDisplayInfoList> categoryDisplayInfoList = shopToStock.getCategoryDisplayInfoList();//取一级分类
        categoryDisplayInfoList.forEach(subCategoryDisplayInfo -> {
            List<MidCategoryDisplayInfoList> midCategoryDisplayInfoList = subCategoryDisplayInfo.getMidCategoryDisplayInfoList();
            if (midCategoryDisplayInfoList == null) {
                throw new BusinessException("12000", "二级分类为空");
            }
            midCategoryDisplayInfoList.forEach(subMidCategoryDisplayInfo -> {//取二级分类
                //ShopToStockVo shopToStockVo = BaseConvertor.convert(shopToStock, ShopToStockVo.class);//自动反射相同字段进 model
                if (subMidCategoryDisplayInfo.getNewStockNum() != subMidCategoryDisplayInfo.getOldStockNum()) { //新旧仓位不相等的记录才入库
                    ShopToStockVo shopToStockVo = new ShopToStockVo();
                    shopToStockVo.setShopCode(shopToStock.getShopCode());
                    shopToStockVo.setShopName(shopToStock.getShopName());
                    shopToStockVo.setDate(shopToStock.getDate());
                    shopToStockVo.setUserName(shopToStock.getUserName());
                    shopToStockVo.setOrderNo(shopToStock.getOrderNo());
                    shopToStockVo.setStatus(0);
                    shopToStockVo.setReason(shopToStock.getReason());
                    shopToStockVo.setCategoryName(subCategoryDisplayInfo.getCategoryName());
                    shopToStockVo.setOldStockNum(subCategoryDisplayInfo.getOldStockNum());
                    shopToStockVo.setNewStockNum(subCategoryDisplayInfo.getNewStockNum());
                    shopToStockVo.setMidCategoryName(subMidCategoryDisplayInfo.getCategoryName());
                    shopToStockVo.setNewStockNum(subMidCategoryDisplayInfo.getNewStockNum());
                    shopToStockVo.setOldStockNum(subMidCategoryDisplayInfo.getOldStockNum());
                    shopToStockVo.setType(Constant.DEFAULT_STORE_TYPE);
                    shopToStockVo.setOperateId(0);
                    shopToStockVo.setSaveStatus(Constant.DEFAULT_COMMIT_SAVE_STATUS);
                    shopToStockVo.setIsRead(Constant.DEFAULT_IS_READ);
                    doList.add(shopToStockVo);
                }
            });
        });
        if (shopToStockDoDOMapper.batchInsertTab(BaseConvertor.convertList(doList, ShopToStockDo.class)) <= 0) {
            throw new BusinessException("12000", "新增失败");
        }
    }

    @Transactional
    public void update(String orderNo, String shopCode) {
        List<ShopToStockDo> updateList;
        ShopToStockReq req = new ShopToStockReq();
        req.setOrderNo(orderNo);
        // 根据调整单号查询仓位
        List<ShopToStockDo> list = shopToStockDoDOMapper.selectByStatus(req);
        // 根据门店编号和是否最新查询目前仓位设置数据
        ShopToStockReq checkReq = new ShopToStockReq();
        checkReq.setShopCode(shopCode);
        checkReq.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        List<ShopToStockDo> checkList = shopToStockDoDOMapper.selectByStatus(checkReq);
        // 2021-4-28 改用id判断
        Map<Integer, ShopToStockDo> map = checkList.stream().collect(Collectors.toMap(ShopToStockDo::getMidCategoryId,
                Function.identity(), (v1, v2) -> v1));

        ShopMappingPositionForm form = new ShopMappingPositionForm();
        List<ShopMappingPositionVO> mappingPositionVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(checkList)) {
            //新增
            updateList = list;
            updateList.forEach(dos -> {
                dos.setOperateId(0);
                dos.setType(Constant.DEFAULT_STORE_TYPE);
                dos.setSaveStatus(Constant.DEFAULT_COMMIT_SAVE_STATUS);
                dos.setIsRead(Constant.DEFAULT_IS_READ);
                dos.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
                dos.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
                ShopMappingPositionVO shopMappingPositionVO = new ShopMappingPositionVO();
                shopMappingPositionVO.setCategoryId(dos.getMidCategoryId());
                shopMappingPositionVO.setShopCode(dos.getShopCode());
                shopMappingPositionVO.setPositionCoefficient(dos.getNewStockNum());
                mappingPositionVOList.add(shopMappingPositionVO);
            });
            if (shopToStockDoDOMapper.batchUpdate(updateList) < 0) {
                throw new BusinessException("12000", "审核失败");
            }
            form.setList(mappingPositionVOList);
            String responseFeign = shopsManager.changePositionByShopCode(form);
            logger.info("[feign response shopManager]------", responseFeign);
            return;
        }
        updateList = new ArrayList<>();
        for (ShopToStockDo vos : list) {
            // 2021-4-28 改用id判断
            if (!map.containsKey(vos.getMidCategoryId())) {
                //新增
                vos.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
                vos.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
                vos.setOperateId(0);
                vos.setType(Constant.DEFAULT_STORE_TYPE);
                vos.setSaveStatus(Constant.DEFAULT_COMMIT_SAVE_STATUS);
                vos.setIsRead(Constant.DEFAULT_IS_READ);
                updateList.add(vos);
                continue;
            }
            // 2021-4-28 改用id判断
            //更新
            ShopToStockDo checkDO = map.get(vos.getMidCategoryId());
            checkDO.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_CLOSE);
            vos.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
            vos.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
            vos.setOldStockNum(checkDO.getNewStockNum());
            vos.setOperateId(0);
            vos.setType(Constant.DEFAULT_STORE_TYPE);
            vos.setSaveStatus(Constant.DEFAULT_COMMIT_SAVE_STATUS);
            vos.setIsRead(Constant.DEFAULT_IS_READ);
            ShopMappingPositionVO shopMappingPositionVO = new ShopMappingPositionVO();
            shopMappingPositionVO.setCategoryId(vos.getMidCategoryId());
            shopMappingPositionVO.setShopCode(vos.getShopCode());
            shopMappingPositionVO.setPositionCoefficient(vos.getNewStockNum());
            mappingPositionVOList.add(shopMappingPositionVO);
            updateList.add(vos);
            updateList.add(checkDO);
        }
        //更新最新仓位数
        if (shopToStockDoDOMapper.batchUpdate(updateList) < 0) {
            throw new BusinessException("12000", "审核失败");
        }
        form.setList(mappingPositionVOList);
        String responseFeign = shopsManager.changePositionByShopCode(form);
        logger.info("[feign response shopManager]------", responseFeign);
    }

    /*
     * 仓位调整筛选条件查询
     * */
    public List<ShopToStockVo> selectByCondition(ShopToStockReq req, Page page) {
        if (page != null) {
            int count = shopToStockDoDOMapper.pageCount(req);
            page.setTotalRecord(count);
        }
        List<ShopToStockDo> list = shopToStockDoDOMapper.selectByCondition(req, page);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        // 2021-4-28 重新从pdc获取前台类目名称
        Set<Integer> bigCategoryIds = list.stream().map(ShopToStockDo::getCategoryId).collect(Collectors.toSet());
        Set<Integer> midCategoryIds = list.stream().map(ShopToStockDo::getMidCategoryId).collect(Collectors.toSet());
        bigCategoryIds.addAll(midCategoryIds);
        Map<Integer, String> idNameMap = pdcManager.getIdNameMap(new ArrayList<>(bigCategoryIds));
        for (ShopToStockDo stockDo : list) {
            if (idNameMap.containsKey(stockDo.getCategoryId())) {
                stockDo.setCategoryName(idNameMap.get(stockDo.getCategoryId()));
            }
            if (idNameMap.containsKey(stockDo.getMidCategoryId())) {
                stockDo.setMidCategoryName(idNameMap.get(stockDo.getMidCategoryId()));
            }
        }
        return BaseConvertor.convertList(list, ShopToStockVo.class);
    }

    /*
     * sys仓位调整批量更新
     * */

    public String batchUpdate(List<ShopToStockVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "传参为空";
        }
        // 获取前台类目对应后台类目顶类 取shopCode查已有仓位设置 提取中类id
        String shopCode = list.stream().findAny().orElse(new ShopToStockVo()).getShopCode();
        ShopToStockReq req = new ShopToStockReq();
        req.setShopCode(shopCode);
        req.setIsNewStatus(1);
        List<ShopToStockDo> stockInfoList = shopToStockDoDOMapper.selectByCondition(req, null);
        Set<Integer> midCategoryIds = stockInfoList.stream().map(ShopToStockDo::getMidCategoryId).collect(Collectors.toSet());
        // 再取传参里的中类id 和上面已存在的做并集
        Set<Integer> toUpdateMidCategoryIds = list.stream().map(ShopToStockVo::getMidCategoryId).collect(Collectors.toSet());
        midCategoryIds.addAll(toUpdateMidCategoryIds);
        // 获得该门店所需前台类目id对应后台类目顶类的map
        Map<Integer, Integer> receptionMapTop = pdcManager.getTopCategoryTypeByDisplayReceptionCategoryIds(new ArrayList<>(midCategoryIds));
        // 获取门店设置的百货仓位和服装仓位
        Map<String, Object> param = new HashMap<>();
        param.put("shopCode", shopCode);
        ShopInfoData shopInfo = shopInfoDOMapper.selectByPage(param).stream().filter(s -> s.getShopCode().equals(shopCode)).findFirst().orElse(null);
        if (shopInfo == null) {
            return "总仓位超出仓位上限【仓位上限0】，请调整后再提交";
        }
        if (shopInfo.getClothSpace() == null || shopInfo.getClothSpace().equals(BigDecimal.ZERO) || shopInfo.getClothSpace().equals(new BigDecimal("0.00"))) {
            return "服装总仓位超出仓位上限【服装仓位上限0】，请调整后再提交";
        }
        if (shopInfo.getCommoditySpace() == null || shopInfo.getCommoditySpace().equals(BigDecimal.ZERO) || shopInfo.getCommoditySpace().equals(new BigDecimal("0.00"))) {
            return "百货总仓位超出仓位上限【百货仓位上限0】，请调整后再提交";
        }
        // 统计传入数量
        BigDecimal clotNum = BigDecimal.ZERO;
        BigDecimal gmNum = BigDecimal.ZERO;
        for (ShopToStockVo stockVo : list) {
            stockVo.setOperateId(0);
            stockVo.setType(Constant.DEFAULT_STORE_TYPE);
            stockVo.setSaveStatus(Constant.DEFAULT_COMMIT_SAVE_STATUS);
            stockVo.setIsRead(Constant.DEFAULT_IS_READ);
            Integer topCategoryId = receptionMapTop.get(stockVo.getMidCategoryId());
            if (topCategoryId == null) {
                continue;
            }
            if (topCategoryId.equals(Constant.TOP_CATEGORY_ID_CLOTHING)) {
                clotNum = clotNum.add(stockVo.getNewStockNum());
            } else {
                gmNum = gmNum.add(stockVo.getNewStockNum());
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
        shopToStockDoDOMapper.batchInsertTab(BaseConvertor.convertList(list, ShopToStockDo.class));
        return null;
    }

    /*
     *@describe仓位调整报表导出
     * */
    public void reportExport(HttpServletResponse response, ShopToStockReq vo) {
        List<CategoryPositionReportEO> list = BaseConvertor.convertList(selectByCondition(vo, null),
                CategoryPositionReportEO.class);
        Workbook workbook = ExcelUtil.exportExcel("门店仓位报表", list, CategoryPositionReportEO.class);
        ResponseUtil.export(response, workbook, "shopReport");
    }

}
