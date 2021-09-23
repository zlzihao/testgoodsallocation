package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.api.result.RpcResult;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.cache.AreaLatitudeCache;
import cn.nome.saas.allocation.cache.GoodsLatitudeCache;
import cn.nome.saas.allocation.feign.api.MopClient;
import cn.nome.saas.allocation.feign.model.*;
import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.model.old.allocation.NewGoodsIssueRangeReq;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.portal.UserMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsGoodsMaterialSizeDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsSaleStockMoveDDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsStockSkuDDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDO;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDetailDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsDimGoodsMaterialSizeDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsSaleStockMoveDDO;
import cn.nome.saas.allocation.repository.entity.vertical.DwsStockSkuDDO;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ForbiddenRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class NewGoodsRangeService {

    private static Logger logger = LoggerFactory.getLogger(NewGoodsRangeService.class);

    private Integer PAGE_SIZE = 500;

    private final static String ATTR_VALUE_SPLIT = ",";

    @Autowired
    NewGoodsIssueRangeMapper newGoodsRangeDOMapper;
    @Autowired
    NewGoodsIssueRangeDetailMapper newGoodsIssueRangeDetailMapper;

    @Autowired
    DwsStockSkuDDOMapper dwsStockSkuDDOMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ForbiddenRuleDOMapper forbiddenRuleDOMapper;

    @Autowired
    ForbiddenSingleItemDOMapper forbiddenSingleItemDOMapper;

    @Autowired
    DwsShopDOMapper dwsShopDOMapper;

    @Autowired
    AreaLatitudeCache areaLatitudeCache;

    @Autowired
    DwsGoodsMaterialSizeDOMapper dwsGoodsMaterialSizeDOMapper;

    @Autowired
    GoodsLatitudeCache goodsLatitudeCache;

    @Autowired
    DwsSaleStockMoveDDOMapper dwsSaleStockMoveDDOMapper;

    @Autowired
    ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper;
    @Autowired
    SecuritySingleRuleDOMapper securitySingleRuleDOMapper;
    @Autowired
    WhiteListSingleItemDOMapper whiteListSingleItemDOMapper;
    @Autowired
    GlobalConfigRuleService globalConfigRuleService;

    public SelectByPageResult selectByParamRangeDo(String matCode, String sizeId, String matName, String createdStart,
                                                    String createdEnd, String updatedBy, Integer validFlag,
                                                    int page, int pageSize) {
        NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
        if (StringUtils.isNotBlank(matCode)) {
            req.setMatCode(matCode);
        }
        if (StringUtils.isNotBlank(sizeId)) {
            req.setSizeId(sizeId);
        }
        if (StringUtils.isNotBlank(matName)) {
            req.setMatName(matName);
        }
        if (StringUtils.isNotBlank(createdStart)) {
            req.setCreatedStart(LocalDate.parse(createdStart, DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (StringUtils.isNotBlank(createdEnd)) {
            req.setCreatedEnd(LocalDate.parse(createdEnd, DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (StringUtils.isNotBlank(updatedBy)) {
            req.setUpdatedBy(updatedBy);
        }
        if (validFlag != null) {
            //0-失效, 1- 生效
            req.setValidFlag(validFlag);
        }

        req.setOffset((page - 1) * pageSize);
        req.setPageSize(pageSize);


        int total = newGoodsRangeDOMapper.pageCount(req);
        List<NewGoodsIssueRangeDO> list = newGoodsRangeDOMapper.pageList(req);
        SelectByPageResult result = new SelectByPageResult();
        result.setTotal(total);
        result.setTotalPage(total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1);
        result.setList(list);

        return result;
    }

    public NewGoodsIssueRangeDO selectByParamRangeDo(Integer id) {
        NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
        req.setId(id);

        List<NewGoodsIssueRangeDO> list = newGoodsRangeDOMapper.pageList(req);

        if (list.size() <= 0) {
            throw new BusinessException("12000", "查无数据");
        }
        NewGoodsIssueRangeDO newGoodsIssueRangeDO = list.get(0);

        Map<String, String> shopIdNameMap = globalConfigRuleService.getShopIdMap();
        String shopIdStr = newGoodsIssueRangeDO.getShopIdInclude();
        if (StringUtils.isNotBlank(shopIdStr)) {
            String[] shopIds = shopIdStr.split(",");
            String shopName;
            Map<String, String> shopIdMap = new HashMap<>(shopIds.length);
            for (String shopId : shopIds) {
                shopName = shopIdNameMap.get(shopId);
                if (shopName != null) {
                    shopIdMap.put(shopId, shopName);
                }
            }
            newGoodsIssueRangeDO.setShopIdMapInclude(shopIdMap);
        }

        shopIdStr = newGoodsIssueRangeDO.getShopIdExclude();
        if (StringUtils.isNotBlank(shopIdStr)) {
            String[] shopIds = shopIdStr.split(",");
            String shopName;
            Map<String, String> shopIdMap = new HashMap<>(shopIds.length);
            for (String shopId : shopIds) {
                shopName = shopIdNameMap.get(shopId);
                if (shopName != null) {
                    shopIdMap.put(shopId, shopName);
                }
            }
            newGoodsIssueRangeDO.setShopIdMapExclude(shopIdMap);
        }

        return newGoodsIssueRangeDO;
    }

    @Transactional(value = "allocationTransactionManager", rollbackFor = Exception.class)
    public void updNewGoodsIssueRangeDO(Integer id, String invalidAt,
                                        String regionInclude, String provinceInclude, String cityInclude, String saleLvInclude, String displayLvInclude, String shopIdInclude,
                                        String attrVal1In, String attrVal2In, String attrVal3In, String attrVal4In, String attrVal5In,
                                        String regionExclude, String provinceExclude, String cityExclude, String saleLvExclude, String displayLvExclude, String shopIdExclude,
                                        String attrVal1Ex, String attrVal2Ex, String attrVal3Ex, String attrVal4Ex, String attrVal5Ex) {
        NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
        req.setId(id);
        req.setInvalidAt(LocalDate.parse(invalidAt, DateTimeFormatter.ISO_LOCAL_DATE));
        req.setRegionInclude(regionInclude);
        req.setProvinceInclude(provinceInclude);
        req.setCityInclude(cityInclude);
        req.setSaleLvInclude(saleLvInclude);
        req.setDisplayLvInclude(displayLvInclude);
        req.setShopIdInclude(shopIdInclude);
        req.setRegionExclude(regionExclude);
        req.setProvinceExclude(provinceExclude);
        req.setCityExclude(cityExclude);
        req.setSaleLvExclude(saleLvExclude);
        req.setDisplayLvExclude(displayLvExclude);
        req.setShopIdExclude(shopIdExclude);
        req.setAttrVal1In(attrVal1In);
        req.setAttrVal2In(attrVal2In);
        req.setAttrVal3In(attrVal3In);
        req.setAttrVal4In(attrVal4In);
        req.setAttrVal5In(attrVal5In);
        req.setAttrVal1Ex(attrVal1Ex);
        req.setAttrVal2Ex(attrVal2Ex);
        req.setAttrVal3Ex(attrVal3Ex);
        req.setAttrVal4Ex(attrVal4Ex);
        req.setAttrVal5Ex(attrVal5Ex);

        req.setUpdatedBy(AuthUtil.getSessionUserId());

        Integer ret = newGoodsRangeDOMapper.updateByPrimaryKeySelective(req);
        if (ret <= 0) {
            throw new BusinessException("12000", "更新失败");
        }

        Map<String, String> shopApplyMap = globalConfigRuleService.getShopApplyMap(
                req.getRegionInclude(), req.getProvinceInclude(), req.getCityInclude(), req.getSaleLvInclude(), req.getDisplayLvInclude(), req.getShopIdInclude(),
                req.getAttrVal1In(), req.getAttrVal2In(), req.getAttrVal3In(), req.getAttrVal4In(), req.getAttrVal5In(),
                req.getRegionExclude(), req.getProvinceExclude(), req.getCityExclude(), req.getSaleLvExclude(), req.getDisplayLvExclude(), req.getShopIdExclude(),
                req.getAttrVal1Ex(), req.getAttrVal2Ex(), req.getAttrVal3Ex(), req.getAttrVal4Ex(), req.getAttrVal5Ex());

        //删除旧的
        int delRet = newGoodsIssueRangeDetailMapper.delByRangeId(id, NewGoodsIssueRangeDetailDO.PLAN_FLAG_WHITELIST);
        List<NewGoodsIssueRangeDetailDO> list = new ArrayList<>();
        NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailDO;
        for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
            newGoodsIssueRangeDetailDO = new NewGoodsIssueRangeDetailDO();
            newGoodsIssueRangeDetailDO.setRangeId(id);
            newGoodsIssueRangeDetailDO.setShopId(entry.getKey());
            newGoodsIssueRangeDetailDO.setShopCode(entry.getValue());
            newGoodsIssueRangeDetailDO.setPlanFlag(NewGoodsIssueRangeDetailDO.PLAN_FLAG_WHITELIST);
            list.add(newGoodsIssueRangeDetailDO);
        }
        newGoodsIssueRangeDetailMapper.batchInsert(list);
    }

    //获取完成首配计划
    public List<CompletePlan> getCompletePlan(MatCodes matCodes){
        List<CompletePlan> completePlanList = new ArrayList<>();
        NewGoodsIssueRangeReq req = new NewGoodsIssueRangeReq();
        //获取sku已经配货的门店数量
        for (String each : matCodes.getMatCodes()){
            req.setMatCode(each);
            List<NewGoodsIssueRangeDO> newGoodsIssueRangeDOList = newGoodsRangeDOMapper.pageList(req);
            if(newGoodsIssueRangeDOList.size() == 0){
                continue;
            }
            Integer shopCount = newGoodsIssueRangeDetailMapper.shopCount(newGoodsIssueRangeDOList);
            if(shopCount > 0){
                CompletePlan completePlan = new CompletePlan();
                completePlan.setMatCode(each);
                completePlan.setShopCount(shopCount);
                completePlanList.add(completePlan);
            }
        }
        return  completePlanList;
    }

    //计划完成首配更新状态
    public void updateCompletePlan(){
        //select * from dws.dws_stok_sku_d  where OperationDate = CURRENT_DATE - 1 ;
        //StockQty 在店 PathStockQty在途
        //dws.dws_saleStockMove_d    MoveQty  在配   相加大于0
        List<NewGoodsIssueRangeDO> newGoodsIssueRangeDOList= newGoodsRangeDOMapper.selectByIssueFin(0);
        NewGoodsIssueRangeReq newGoodsIssueRangeReq = new NewGoodsIssueRangeReq();
        newGoodsIssueRangeReq.setIssueFin(1);
        newGoodsIssueRangeReq.setUpdatedAt(LocalDate.now());
        for (NewGoodsIssueRangeDO ng : newGoodsIssueRangeDOList) {
            List<NewGoodsIssueRangeDetailDO> newGoodsIssueRangeDetailDOList = newGoodsIssueRangeDetailMapper.selectByIssueFin(ng.getId(),0);
            if (newGoodsIssueRangeDetailDOList == null){//所有尺码完成首配更改计划首配状态
                newGoodsRangeDOMapper.updateByPrimaryKeySelective(newGoodsIssueRangeReq);
                continue;
            }
            //未完成首配尺码查找数仓是否有完成首配
            int i = 0;
            for (NewGoodsIssueRangeDetailDO ngDetail : newGoodsIssueRangeDetailDOList) {
                DwsSaleStockMoveDDO dwsSaleStockMoveDDO = dwsSaleStockMoveDDOMapper.selectByParam(ngDetail.getShopId(),ng.getMatCode(),ng.getSizeName());
                DwsStockSkuDDO  dwsStockSkuDDO = dwsStockSkuDDOMapper.selectByParam(ngDetail.getShopId(),ng.getMatCode(),ng.getSizeName());
                if(dwsSaleStockMoveDDO != null && dwsStockSkuDDO != null){
                    int qty = (int)dwsSaleStockMoveDDO.getMoveQty() + (int)dwsStockSkuDDO.getStockQty() + (int)dwsStockSkuDDO.getPathStockQty();
                    if(qty > 0){
                        NewGoodsIssueRangeDetailDO newGoodsIssueRangeDetailDO = new NewGoodsIssueRangeDetailDO();
                        newGoodsIssueRangeDetailDO.setId(ngDetail.getId());
                        newGoodsIssueRangeDetailDO.setIssueFin(1);
                        newGoodsIssueRangeDetailMapper.updateByPrimaryKeySelective(newGoodsIssueRangeDetailDO);//满足条件，完成首配
                        i++;
                    }
                }
            }
            //都已完成首配，更改主信息首配状态
            if(i == newGoodsIssueRangeDetailDOList.size()){
                newGoodsRangeDOMapper.updateByPrimaryKeySelective(newGoodsIssueRangeReq);
            }
        }
    }


}