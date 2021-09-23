package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.cache.AreaLatitudeCache;
import cn.nome.saas.allocation.cache.GoodsLatitudeCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.feign.model.User;
import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.model.rule.*;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.dao.portal.UserMapper;
import cn.nome.saas.allocation.repository.dao.vertical.DwsShopDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.repository.entity.vertical.DwsDimShopDO;
import cn.nome.saas.allocation.utils.AuthUtil;
import cn.nome.saas.allocation.utils.ExcelUtil;
import cn.nome.saas.allocation.utils.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.apache.catalina.util.ParameterMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ForbiddenRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class ForbiddenRuleService {

    private static Logger logger = LoggerFactory.getLogger(ForbiddenRuleService.class);

    private Integer PAGE_SIZE = 500;

    private final static String ATTR_VALUE_SPLIT = ",";

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
    GoodsLatitudeCache goodsLatitudeCache;

    @Autowired
    ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper;
    @Autowired
    SecuritySingleRuleDOMapper securitySingleRuleDOMapper;
    @Autowired
    WhiteListSingleItemDOMapper whiteListSingleItemDOMapper;
    @Autowired
    GlobalConfigRuleService globalConfigRuleService;
    @Value("${upload.xls.forbidden.dir}")
    private String UPLOAD_XLS_FORBIDDEN_DIR;


    private static ConcurrentHashMap forbiddenProceedMap = new ConcurrentHashMap();

    public List<Map<String,String>> getForbiddenDetailList(Set<String> matCodeList) {

        Map<String, Object> param = new HashMap<>();
        param.put("matCodeList",matCodeList);
        param.put("currentDate", DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY));

        return forbiddenSingleItemDOMapper.getForbiddenDetailList(param);

    }

    public ForbiddenSingleRuleByTypeResult getRuleForCategory(Integer type,String typeValue) {
        Map<String, Object> param = new HashMap<>();
        param.put("type",type);
        param.put("typeValue",typeValue);
        List<ForbiddenSingleItemDO> list =  forbiddenSingleItemDOMapper.selectByType(param);
        ForbiddenSingleRuleByTypeResult forbiddenSingleRuleByTypeResult = new ForbiddenSingleRuleByTypeResult();
        forbiddenSingleRuleByTypeResult.setTypeValue(typeValue);
        forbiddenSingleRuleByTypeResult.setType(type);
        Set<String> shopCodesSet = new TreeSet<>();
        for (ForbiddenSingleItemDO f : list) {
            if(f.getShopCode() != null){
                shopCodesSet.add(f.getShopCode());
           }
        }
        forbiddenSingleRuleByTypeResult.setShopCodes(shopCodesSet);
        return forbiddenSingleRuleByTypeResult;
    }

    public ForbiddenRuleResult selectForbiddenRuleByParam(String ruleName, String userName, int page, int pageSize, int ruleType, Integer type) {

        //
        String operatorId = AuthUtil.getSessionUserId();
        LocalUser user = userMapper.getUser(operatorId).get(0);


        Map<String,Object> param = new HashMap<>();

        if (StringUtils.isNotBlank(ruleName)) {
            param.put("ruleName",ruleName);
        }
        if (type != null) {
            param.put("type", type);
        }
        if (StringUtils.isNotBlank(userName)) {
            param.put("operator",userName);
        } else {
            if (user.getRole() == 0) {
                param.put("operator", user.getUserName());
            }
        }

        param.put("offset",(page - 1) * pageSize);
        param.put("pageSize",pageSize);
        if (ruleType != 0) {
            if (ruleType == ForbiddenRuleDO.STATUS_SECURITY) {
                param.put("status",new int[]{ruleType});
            } else if (ruleType == ForbiddenRuleDO.STATUS_WHITE_LIST) {
                param.put("status",new int[]{ruleType, ForbiddenRuleDO.STATUS_SECURITY_AND_WHITE_LIST});
            } else {
                param.put("status",new int[]{ruleType});
            }
        }


        int total = forbiddenRuleDOMapper.getForbiddenRuleCount(param);
        List<ForbiddenRuleDO> list = forbiddenRuleDOMapper.selectFrobiddenRuleByPage(param);

        ForbiddenRuleResult result = new ForbiddenRuleResult();

        result.setTotal(total);
        result.setList(list);

        return result;
    }


    public ForbiddenSingleRuleResult selectForbiddenSingleRuleByParam(String shopName, String largeCategory, String midCategory, String smallCategory,
                                                                String typeValue, String userName, int page, int pageSize) {
        String operatorId = AuthUtil.getSessionUserId();
        LocalUser user = userMapper.getUser(operatorId).get(0);

        Map<String,Object> param = new HashMap<>();
        List<String> shopIds = new ArrayList<>();
        if (StringUtils.isNotBlank(shopName)) {
            String[] shopNames = shopName.split(ATTR_VALUE_SPLIT);
            for (String sn : shopNames) {
                shopIds.add(globalConfigRuleService.getShopNameMap().get(sn));
            }
            param.put("shopIds",shopIds);
        } else {
            throw new BusinessException("12000", "找不到对应店铺");
        }

        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategory)) {
            param.put("smallCategory", smallCategory.split(ATTR_VALUE_SPLIT));
        }

        if (!StringUtils.isEmpty(typeValue)) {
            param.put("typeValue", typeValue);
        }


        if (StringUtils.isNotBlank(userName)) {
            param.put("operatorLike", userName);
        }
        if (user.getRole() == 0) {
            param.put("operator", user.getUserName());
        }

        //查询单店
        param.put("typeForbidden", ForbiddenSingleItemDO.TYPE_FORBIDDEN_SINGLE);
        param.put("offset",(page - 1) * pageSize);
        param.put("pageSize",pageSize);

        logger.info("selectForbiddenSingleRuleByParam, param: {}", JSONObject.toJSONString(param));

        int total = forbiddenSingleItemDOMapper.getCount(param);
        List<ForbiddenSingleItemDO> list = forbiddenSingleItemDOMapper.selectByPage(param);

        for (ForbiddenSingleItemDO f : list) {
            f.setShopName(globalConfigRuleService.getShopIdMap().get(f.getShopId()));
        }

        ForbiddenSingleRuleResult result = new ForbiddenSingleRuleResult();

        result.setTotal(total);
        result.setList(list);

        return result;

    }


    public void exportSingleList(String shopName, String largeCategory, String midCategory, String smallCategory,
                                                                String typeValue, String userName, HttpServletRequest request, HttpServletResponse response) {

        String operatorId = AuthUtil.getSessionUserId();
        LocalUser user = userMapper.getUser(operatorId).get(0);

        Map<String,Object> param = new HashMap<>();
        List<String> shopIds = new ArrayList<>();
        if (StringUtils.isNotBlank(shopName)) {
            String[] shopNames = shopName.split(ATTR_VALUE_SPLIT);
            for (String sn : shopNames) {
                shopIds.add(globalConfigRuleService.getShopNameMap().get(sn));
            }
            param.put("shopIds",shopIds);
        }

        if (!StringUtils.isEmpty(largeCategory)) {
            param.put("largeCategory", largeCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategory)) {
            param.put("midCategory", midCategory.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategory)) {
            param.put("smallCategory", smallCategory.split(ATTR_VALUE_SPLIT));
        }

        if (!StringUtils.isEmpty(typeValue)) {
            param.put("typeValue", typeValue);
        }

        if (!StringUtils.isNotBlank(userName)) {
            param.put("operatorLike", userName);
        }
        if (user.getRole() == 0) {
            param.put("operator", user.getUserName());
        }
        //查询单店
        param.put("typeForbidden", ForbiddenSingleItemDO.TYPE_FORBIDDEN_SINGLE);

        List<ForbiddenSingleItemDO> list = forbiddenSingleItemDOMapper.selectByPage(param);
        for (ForbiddenSingleItemDO f : list) {
            f.setShopName(globalConfigRuleService.getShopIdMap().get(f.getShopId()));
        }

        if (list.size() > 0) {
            try {
                ExcelUtil.exportForbiddenSingleRuleData(shopName,list, request, response);
            } catch (Exception e) {
                throw new BusinessException("12000","导出单店禁配明细异常");
            }
        } else {
            throw new BusinessException("12000","没有需要导出的明细");
        }
    }


    @Transactional(value = "allocationTransactionManager")
    public ForbiddenRuleDetailResult uploadAndSaveDetail(String ruleName, Integer ruleId, MultipartFile file) {

        String operatorId = AuthUtil.getSessionUserId();
        LocalUser localUser = userMapper.getUser(operatorId).get(0);

        ruleName = ruleName == null ? "禁配-"+DateUtil.format(DateUtil.getCurrentDate(),DateUtil.DATE_ONLY) : ruleName;

        ExcelUtil.checkTableHead(file);

        Map<Integer, Set<String>> limitValMap = new HashMap<>();
        limitValMap.put(1, new HashSet<>(Arrays.asList("大类", "中类", "小类", "skc", "sku")));
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2, 3, 4, 6),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING, CellType.NUMERIC, CellType.NUMERIC, CellType.STRING),
                Arrays.asList("门店代码", "类型", "对象", "开始日期", "结束日期", "最后修改人"),
                limitValMap,
                new HashSet<>(Arrays.asList(0, 1, 2)));

        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        ForbiddenRuleDetailResult forbiddenRuleDetailResult = new ForbiddenRuleDetailResult();
        //
        List<ForbiddenSingleItemDO> list = uploadSignleItemData(ruleName,file);

        Map<String,Object> param = null;
        try {
            if (ruleId != null) {
                // update
                ForbiddenRuleDO forbiddenRule = new ForbiddenRuleDO();
                forbiddenRule.setId(ruleId);
                forbiddenRule.setUpdatedAt(new Date());
                forbiddenRule.setUpdatedBy(localUser.getUserName());

                forbiddenRuleDOMapper.updateById(forbiddenRule);

                forbiddenSingleItemDOMapper.deleteByRuleId(ruleId);
            } else {
                ForbiddenRuleDO forbiddenRule = new ForbiddenRuleDO();
                forbiddenRule.setName(ruleName);
                forbiddenRule.setType(ForbiddenRuleDO.SINGLE_TYPE);
                forbiddenRule.setStatus(1);
                forbiddenRule.setCreatedAt(new Date());
                forbiddenRule.setCreatedBy(localUser.getUserName());
                forbiddenRuleDOMapper.insertSelective(forbiddenRule);

                ruleId = forbiddenRule.getId();
            }


            for (ForbiddenSingleItemDO item : list) {
                item.setRuleId(ruleId);
                item.setTypeForbidden(ForbiddenSingleItemDO.TYPE_FORBIDDEN_SINGLE);
                item.setCreatedAt(new Date());

                item.setCreatedBy(localUser.getUserName());
            }

            int index = 0;
            int size = 1000;

            while(true) {
                List<ForbiddenSingleItemDO> subList = list.stream().skip(index).limit(size).collect(Collectors.toList());

                if (CollectionUtils.isEmpty(subList)) {
                    break;
                }
                param = new ParameterMap<>();
                param.put("singleList",subList);
                forbiddenSingleItemDOMapper.batchInsert(param);

                index += size;
            }
        }catch (Exception e) {
            throw new BusinessException("12000","表格内容有误，请检查后再导入！");
        }

        List<ForbiddenSingleItemDO> subList = list.stream().skip(0).limit(PAGE_SIZE).collect(Collectors.toList());

        param = new HashMap<>();
        param.put("shopIdList", subList.stream().map(ForbiddenSingleItemDO::getShopId).collect(Collectors.toList()));
        List<DwsDimShopDO> shoplist = dwsShopDOMapper.selectShopListById(param);

        for (ForbiddenSingleItemDO item : subList) {
            for (DwsDimShopDO shop : shoplist) {
                if (shop.getShopId().equals(item.getShopId())) {
                    item.setShopName(shop.getShopName());
                    item.setShopCode(shop.getShopCode());
                }
            }
        }

        forbiddenRuleDetailResult.setRuleId(ruleId);
        forbiddenRuleDetailResult.setCurrentPage(1);
        forbiddenRuleDetailResult.setTotal(list.size());
        forbiddenRuleDetailResult.setList(subList);

        return forbiddenRuleDetailResult;
    }

    @Transactional(value = "allocationTransactionManager", rollbackFor = Exception.class)
    public int delDetail(MultipartFile file) {
        String operatorId = AuthUtil.getSessionUserId();
        LocalUser localUser = userMapper.getUser(operatorId).get(0);

        Map<Integer, Set<String>> limitValMap = new HashMap<>();
        limitValMap.put(1, new HashSet<>(Arrays.asList("大类", "中类", "小类", "skc", "sku")));
        String msg = ExcelUtil.checkExcel(file, 1, null,
                Arrays.asList(0, 1, 2),
                Arrays.asList(CellType.STRING, CellType.STRING, CellType.STRING),
                Arrays.asList("门店代码", "类型", "对象"),
                limitValMap,
                new HashSet<>(Arrays.asList(0, 1, 2)));

        if (!StringUtils.isEmpty(msg)) {
            throw new BusinessException("12000", msg);
        }

        //
        List<ForbiddenSingleItemDO> list = uploadSignleItemData("",file);

        List<String> importShops = list.stream().map(ForbiddenSingleItemDO::getShopCode).collect(Collectors.toList());
        List<String> importTypeValues = list.stream().map(ForbiddenSingleItemDO::getTypeValue).collect(Collectors.toList());

        Set<String> importData = list.stream().map(forbiddenSingleItemDO -> forbiddenSingleItemDO.getShopCode() + "_" + forbiddenSingleItemDO.getTypeValue()).collect(Collectors.toSet());
        Set<String> notExistData = new HashSet<>(importData);

        List<ForbiddenSingleItemDO> existForbidden = forbiddenSingleItemDOMapper.getForbiddenByTypeValueAndShopCode(importShops, importTypeValues);
        Set<String> existData = existForbidden.stream().map(ForbiddenSingleItemDO::getConcatShopCodeTypeValue).collect(Collectors.toSet());

        notExistData.removeAll(existData);

        if (!notExistData.isEmpty()) {
            String errorMsg = notExistData.stream().map(str -> "店铺代码为:" + str.split("_")[0] + ",对象值为:" + str.split("_")[1] + "的禁配信息不存在!\r\n").collect(Collectors.joining());
            throw new BusinessException("12000", errorMsg);
        }

        Set<Integer> existDataId = existForbidden.stream().map(ForbiddenSingleItemDO::getId).collect(Collectors.toSet());

        forbiddenSingleItemDOMapper.bakDelSingleForbidden(existDataId, localUser.getUserName());

        return forbiddenSingleItemDOMapper.delForbiddenByTypeValueAndShopCode(importShops, importTypeValues);
    }

    @Transactional(value = "allocationTransactionManager")
    public void singleUpdate(Integer id, Integer type, String typeValue, String remark, String startDateStr, String endDateStr) {

        if (id == null || type == null || StringUtils.isEmpty(typeValue) || StringUtils.isEmpty(startDateStr) || StringUtils.isEmpty(endDateStr)) {
            throw new BusinessException("12000", "缺少必填参数");
        }
        Date startDate;
        Date endDate;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        } catch (Exception e) {
            throw new BusinessException("12000", "日期转换错误");
        }

        String operatorId = AuthUtil.getSessionUserId();
        LocalUser localUser = userMapper.getUser(operatorId).get(0);

        ForbiddenSingleItemDO singleItem = new ForbiddenSingleItemDO();
        singleItem.setId(id);
        singleItem.setType(type);
        singleItem.setTypeValue(typeValue);
        singleItem.setStartDate(startDate);
        singleItem.setEndDate(endDate);
        singleItem.setRemark(remark);
        singleItem.setCreatedAt(new Date());
        singleItem.setCreatedBy(localUser.getUserName());
        singleItem.setUpdatedAt(new Date());
        singleItem.setUpdatedBy(localUser.getUserName());

        try {
            forbiddenSingleItemDOMapper.updateById(singleItem);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BusinessException("12000", "已存在对应规则, 请检查");
        }

    }

    @Transactional(value = "allocationTransactionManager")
    public void singleDel(int id) {
        forbiddenSingleItemDOMapper.deleteById(id);
//        return
    }

    public void downloadForbiddenRuleDetail(ForbiddenRuleDetailParam forbiddenRuleDetailParam, HttpServletRequest request, HttpServletResponse response) {

        ForbiddenRuleDetailResult result = queryForbiddenRuleDetail(forbiddenRuleDetailParam);

        if (result.getTotal() > 0) {
            try {
                ExcelUtil.exportForbiddenDetailData(result.getRuleName(),result.getList(),request,response);
            } catch (Exception e) {
                throw new BusinessException("12000","导出禁配明细异常");
            }
        }
    }

    public boolean checkIfIsForbiddenProduct(List<Map<String,String>> forbiddenSingleItemDOList, String shopId, String matCode) {
        for (Map<String,String> map : forbiddenSingleItemDOList) {

            String forbidShopId = map.get("shopId");
            String forbidMatCode = map.get("matCode");

            // 基础禁配
            if ((forbidShopId.equals(shopId) && forbidMatCode.equals(matCode))) {
                return true;
            }

        }
        return false;
    }

    public ForbiddenRuleDetailResult queryForbiddenRuleDetail(ForbiddenRuleDetailParam forbiddenRuleDetailParam) {

        List<String> shopIdList = this.getShopIdList(forbiddenRuleDetailParam);

        Map<String,Object> param = new HashMap<>();

        param.put("ruleId",forbiddenRuleDetailParam.getRuleId());
        if (shopIdList != null) {
            param.put("shopIdList",shopIdList);
        }
        if (forbiddenRuleDetailParam.getCategoryLevel() != 0) {
            param.put("type",forbiddenRuleDetailParam.getCategoryLevel());
            param.put("typeValue",forbiddenRuleDetailParam.getCategoryCode());
        }
        if (StringUtils.isNotBlank(forbiddenRuleDetailParam.getQueryName())) {
            param.put("queryName",forbiddenRuleDetailParam.getQueryName());
        }
        if (StringUtils.isNotBlank(forbiddenRuleDetailParam.getModifiedBy())) {
            param.put("modifiedBy",forbiddenRuleDetailParam.getModifiedBy());
        }

        param.put("offset",(forbiddenRuleDetailParam.getPage() - 1) * forbiddenRuleDetailParam.getPageSize());
        param.put("pageSize",forbiddenRuleDetailParam.getPageSize());

        int total = forbiddenSingleItemDOMapper.countByParam(param);
        List<ForbiddenSingleItemDO> list = forbiddenSingleItemDOMapper.selectBySelective(param);

        List<DwsDimShopDO> shoplist = null;
        if (shopIdList != null) {
            param = new HashMap<>();
            param.put("shopIdList", shopIdList);
            shoplist = dwsShopDOMapper.selectShopListById(param);
        } else {
            shoplist = dwsShopDOMapper.selectAllShopList();
        }

        for (ForbiddenSingleItemDO item : list) {
            for (DwsDimShopDO shop : shoplist) {
                if (shop.getShopId().equals(item.getShopId())) {
                    item.setShopName(shop.getShopName());
                    item.setShopCode(shop.getShopCode());
                }
            }
            item.setTypeName(ForbiddenSingleItemDO.getTypeName(item.getType()));
        }

        int totalPage = 0;
        if (total % forbiddenRuleDetailParam.getPageSize() == 0) {
            totalPage = total / forbiddenRuleDetailParam.getPageSize();
        } else {
            totalPage = (total / forbiddenRuleDetailParam.getPageSize()) + 1;
        }

        ForbiddenRuleDetailResult result = new ForbiddenRuleDetailResult();

        result.setRuleName(list.get(0).getRuleName());
        result.setCurrentPage(forbiddenRuleDetailParam.getPage());
        result.setTotal(total);
        result.setTotalPage(totalPage);
        result.setList(list);

        return result;
    }

    public List<RuleTree> getAreaLatitudeTreeByParam(int level, String parentCode) {
        List<RuleTree> allAreaList = null;
        try {
            allAreaList = areaLatitudeCache.get("area");
        } catch (ExecutionException e) {
        }

        List<RuleTree> resultList = null;
        // 大区
        if (level == 1) {
            resultList = allAreaList.stream().map(area->{
                RuleTree ruleTree = new RuleTree();

                ruleTree.setCode(area.getCode());
                ruleTree.setName(area.getName());
                ruleTree.setLevel(area.getLevel());

                return ruleTree;
            }).sorted(Comparator.comparing(RuleTree::getCode)).collect(Collectors.toList());

            Map<String,List<String>> regionMap = resultList.stream().collect(Collectors.groupingBy(RuleTree::getName,Collectors.mapping(RuleTree::getCode,Collectors.toList())));
            resultList.forEach(rule->{
                if (regionMap.containsKey(rule.getName())) {
                    String codes = regionMap.get(rule.getName()).stream().collect(Collectors.joining(","));
                    rule.setCode(codes);
                }
            });

            resultList = resultList.stream().distinct().collect(Collectors.toList());
        }
        // 省份
        else if(level == 2) {
            Predicate<RuleTree> filter = rule->1==1;
            if (StringUtils.isNotBlank(parentCode)) {
                List<String> codes = Stream.of(parentCode.split(",")).collect(Collectors.toList());
                filter = rule->codes.contains(rule.getCode());
            }
            resultList = filterListByParentCode(allAreaList,filter);
        }
        // 城市
        else if(level == 3) {
            List<RuleTree> provinceList = allAreaList.stream()
                    // 省份列表
                    .map(area->{
                        return area.getChildList();
                    })
                    .flatMap(list->list.stream())
                    .collect(Collectors.toList());

            Predicate<RuleTree> filter = rule->1==1;
            if (StringUtils.isNotBlank(parentCode)) {
                filter = rule->rule.getCode().trim().equals(parentCode.trim());
            }

            resultList = filterListByParentCode(provinceList,filter);
        }
        // 门店
        else if(level == 4) {
            List<RuleTree> cityList = allAreaList.stream()
                    // 城市列表
                    .map(area->{
                        return area.getChildList();
                    })
                    .flatMap(list->list.stream())
                    .map(area->{
                        return area.getChildList();
                    })
                    .flatMap(list->list.stream())
                    .collect(Collectors.toList());

            Predicate<RuleTree> filter = rule->1==1;
            if (StringUtils.isNotBlank(parentCode)) {
                filter =  rule->rule.getCode().trim().equals(parentCode.trim());
            }

            resultList = filterListByParentCode(cityList,filter);
        }

        return resultList;
    }

    public List<RuleTree> getGoodsLatitudeTreeByParam(int level, String parentCode) {

        List<RuleTree> categoryList = null;
        try {
            categoryList = goodsLatitudeCache.get("categoryList");
        } catch (ExecutionException e) {
            return null;
        }

        if (level == 1) {
            // 大类
            return  categoryList.stream().map(goods->{
                RuleTree ruleTree = RuleTree.buildGoodsRuleTree();
                ruleTree.setCode(goods.getCode());
                ruleTree.setName(goods.getName());
                ruleTree.setLevel(goods.getLevel());
                return ruleTree;
            }).collect(Collectors.toList());

        } else if (level == 2) {

            Predicate<RuleTree> filter = rule->1==1;
            if (StringUtils.isNotBlank(parentCode)) {
                filter = rule->parentCode.equals(rule.getCode());
            }

            // 中类
            return filterListByParentCode(categoryList,filter);

        } else if (level == 3) {

            // 小类
            List<RuleTree> middleList = categoryList.stream()
                    // 中类
                    .map(category->{
                        return category.getChildList();
                    })
                    .flatMap(list->list.stream())
                    .collect(Collectors.toList());

            Predicate<RuleTree> filter = rule->1==1;
            if (StringUtils.isNotBlank(parentCode)) {
                filter = rule->rule.getCode().trim().equals(parentCode.trim());
            }

            return filterListByParentCode(middleList,filter);

        }

        return null;
    }

    public List<String> selectOperatorList() {
        List<LocalUser> userList = userMapper.getAllocationOperatorList();
        if (userList != null) {
            return userList.stream().map(LocalUser::getUserName).collect(Collectors.toList());
        }
        return null;
    }

    public List<String> selectModifiedByList() {
        return forbiddenSingleItemDOMapper.selectModifiedByList();
    }

    @Transactional(value = "allocationTransactionManager", rollbackFor = Exception.class)
    public void deleteForbiddenRuleAndDetail(int ruleId) {
        ForbiddenRuleDO forbiddenRuleDO = forbiddenRuleDOMapper.selectById(ruleId);
        if (forbiddenRuleDO == null) {
            return;
        }
        Integer syncId = forbiddenRuleDO.getSyncId();
        if (syncId != null && syncId != 0) {
            throw new BusinessException("12000", "同步生成的白名单不允许直接删除");
        }
        forbiddenRuleDOMapper.deleteById(ruleId);
        forbiddenGlobalItemDOMapper.deleteByFRuleId(ruleId);

        if (forbiddenRuleDO.getStatus() == ForbiddenRuleDO.STATUS_FORBIDDEN) {
            forbiddenSingleItemDOMapper.deleteByRuleId(ruleId);
        } else if (forbiddenRuleDO.getStatus() == ForbiddenRuleDO.STATUS_SECURITY) {
            securitySingleRuleDOMapper.deleteByRuleId(ruleId);
        } else if (forbiddenRuleDO.getStatus() == ForbiddenRuleDO.STATUS_WHITE_LIST) {
            whiteListSingleItemDOMapper.deleteByRuleId(ruleId);
        }

        //删除同步生成的白名单
        ForbiddenRuleDO syncForbiddenRuleDo = forbiddenRuleDOMapper.selectBySyncId(ruleId);
        if (syncForbiddenRuleDo != null) {
            forbiddenRuleDOMapper.deleteById(syncForbiddenRuleDo.getId());
            forbiddenGlobalItemDOMapper.deleteByFRuleId(syncForbiddenRuleDo.getId());
            whiteListSingleItemDOMapper.deleteByRuleId(syncForbiddenRuleDo.getId());
        }

    }

    private List<ForbiddenSingleItemDO> uploadSignleItemData(String ruleName,MultipartFile file) {

        List<UploadDetailData> dataList = null;
        try {
            dataList = ExcelUtil.readForbiddenDetailData(file);
        } catch (Exception e) {
            LoggerUtil.error(e,logger,"[IMPOT_EXCEL] ");
            throw new BusinessException("10002","excel导入失败，请检查后再导入！");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            throw new BusinessException("10002", "表格内容为空，请检查后再导入！");
        }

        LoggerUtil.info(logger,"[IMPORT]|size:{0}",dataList.size());

        return convert(ruleName,dataList);
    }

    private List<ForbiddenSingleItemDO> convert(String ruleName,List<UploadDetailData> dataList) {
        List<String> shopCodes = dataList.stream().filter(data->data.getShopCode() != null).map(UploadDetailData::getShopCode).distinct().collect(Collectors.toList());

        Map<String,Object> param = new HashMap<>();
        param.put("shopCodes",shopCodes);

        List<ForbiddenSingleItemDO> list = null;
        try {
            List<Map<String, String>> shopMapList = dwsShopDOMapper.selectShopIdByCode(param);

            LoggerUtil.info(logger, "[IMPORT]|shopMapList:{0}", shopMapList.size());

            Date now = new Date();

            list = dataList.stream().filter(data -> data.getShopCode() != null)
                    .map(data -> {
                        ForbiddenSingleItemDO item = new ForbiddenSingleItemDO();

                        item.setRuleName(ruleName);
                        item.setShopId(data.getShopCode()); // default value

                        for (Map<String, String> shopMap : shopMapList) {

                            if (shopMap.get("ShopCode").equals(data.getShopCode())) {
                                item.setShopId(shopMap.get("ShopID"));
                                continue;
                            }
                        }

                        item.setType(ForbiddenSingleItemDO.getType(data.getType().trim()));
                        item.setTypeValue(data.getTypeValue());
                        item.setShopCode(data.getShopCode());
                        item.setRemark(data.getRemark());
                        item.setStartDate(data.getStartDate());
                        item.setEndDate(data.getEndDate());
                        item.setModifiedBy(data.getUpdateUser());
                        item.setCreatedAt(now);

                        return item;
                    }).collect(Collectors.toList());


            LoggerUtil.info(logger, "[IMPORT]|size:{1}", dataList.size());
        } catch (Exception e) {
            throw new BusinessException("10002","excel导入失败，请检查后再导入！");
        }

        return list;
    }

    private List<String> getShopIdList(ForbiddenRuleDetailParam forbiddenRuleDetailParam) {

        if (forbiddenRuleDetailParam.getRegionLevel() == 0) {
            return null;
        }

        List<String> shopIdList = new ArrayList<>();
        if (forbiddenRuleDetailParam.getRegionLevel() == RuleTree.SHOP_LEVEL) {
            shopIdList.add(forbiddenRuleDetailParam.getRegionCode());
            return shopIdList;
        }

        Map<String,Object> param = new HashMap<>();

        switch (forbiddenRuleDetailParam.getRegionLevel()) {

            case RuleTree.REGION_LEVEL :
                List<String> list = null;
                list = Stream.of(forbiddenRuleDetailParam.getRegionCode().split(",")).collect(Collectors.toList());
                param.put("regioneNo",list);
                break;
            case RuleTree.PROVINCE_LEVEL :
                param.put("provinceCode",forbiddenRuleDetailParam.getRegionCode());
                break;
            case RuleTree.CITY_LEVEL :
                param.put("cityCode",forbiddenRuleDetailParam.getRegionCode());
                break;
        }

        return dwsShopDOMapper.selectShopIdByParam(param);
    }

    private List<RuleTree> filterListByParentCode(List<RuleTree> ruleList, Predicate<RuleTree> filter) {
        return ruleList.stream()
                .filter(filter)
                .map(area->{
                    return area.getChildList();
                })
                .flatMap(list->list.stream())
                .map(area->{
                    RuleTree ruleTree = new RuleTree();

                    ruleTree.setType(area.getType());
                    ruleTree.setCode(area.getCode().trim());
                    ruleTree.setName(area.getName().trim());
                    ruleTree.setLevel(area.getLevel());

                    return ruleTree;
                })
                .distinct()
                .sorted(Comparator.comparing(RuleTree::getCode))
                .collect(Collectors.toList());
    }

    public ForbiddenGlobalItem getGlobalDetail2(int ruleId) {
        ForbiddenRuleDO forbiddenRule = forbiddenRuleDOMapper.selectById(ruleId);
        if (forbiddenRule == null) {
            throw new BusinessException("查无数据");
        }
        ForbiddenGlobalItem forbiddenGlobalItem = forbiddenGlobalItemDOMapper.selectByFRuleId(ruleId);
        if (forbiddenGlobalItem == null) {
            throw new BusinessException("查无数据");
        }

        forbiddenGlobalItem.setRuleName(forbiddenRule.getName());
        forbiddenGlobalItem.setStartDate(forbiddenRule.getStartDate());
        forbiddenGlobalItem.setEndDate(forbiddenRule.getEndDate());
        forbiddenGlobalItem.setRuleType(forbiddenRule.getStatus());
        if (forbiddenRuleDOMapper.selectBySyncId(ruleId) != null) {
            forbiddenGlobalItem.setSyncWhiteFlag(1);
        }

        if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcInclude())) {
            List<ImportRuleFileVo> includeList = globalConfigRuleService.analyseRuleFile(new File(UPLOAD_XLS_FORBIDDEN_DIR + forbiddenGlobalItem.getSkcInclude()), forbiddenRule.getStatus());
            forbiddenGlobalItem.setIncludeList(includeList);
        }
        if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcExclude())) {
            List<ImportRuleFileVo> excludeList = globalConfigRuleService.analyseRuleFile(new File(UPLOAD_XLS_FORBIDDEN_DIR + forbiddenGlobalItem.getSkcExclude()), forbiddenRule.getStatus());
            forbiddenGlobalItem.setExcludeList(excludeList);
        }

        return forbiddenGlobalItem;
    }


    @Transactional(value = "allocationTransactionManager")
    public ForbiddenRuleDO uploadAndSaveSignleItemData2(String ruleName, Integer ruleId, MultipartFile ruleFileApply, int fileInDelFlag,
                                                        MultipartFile ruleFileExclude, int fileExDelFlag,
                                                      String areasApply, String provincesApply, String citysApply, String saleLevelsApply, String displayLevelsApply,
                                                      String shopsApply,
                                                      String attrFirValInclude, String attrSecValInclude, String attrThiValInclude, String attrFourValInclude, String attrFifValInclude,
                                                      String areasExclude, String provincesExclude, String citysExclude, String saleLevelsExclude, String displayLevelsExclude,
                                                      String shopsExclude,
                                                      String attrFirValExclude, String attrSecValExclude, String attrThiValExclude, String attrFourValExclude, String attrFifValExclude,
                                                      String startDateStr,String endDateStr,String updateUser) {

        try {
            if (ruleId != null && forbiddenProceedMap.get(ruleId.toString()) != null) {
                throw new BusinessException("12000", "对应的禁配规则正在生成中, 请稍后修改");
            }

            if (ruleId != null) {
                forbiddenProceedMap.put(ruleId.toString(), "1");
            }

            Date startDate, endDate;
            try {
                startDate = new SimpleDateFormat("yyyyMMdd").parse(startDateStr.trim());
                endDate = new SimpleDateFormat("yyyyMMdd").parse(endDateStr.trim());
                if (startDate.after(endDate)) {
                    throw new BusinessException("12000", "开始日期不能晚于结束日期, 请检查日期");
                }
            } catch (Exception e) {
                LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|日期转换出错,startDate:{0},endDate:{1}",startDateStr, endDateStr);
                throw new BusinessException("12000", "日期转换出错, 请检查日期");
            }

            //校验参数
            globalConfigRuleService.checkParam(areasApply, provincesApply, citysApply, saleLevelsApply, displayLevelsApply,
                    shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude, provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude
            );

            // TODO debug模式避开真实的上传逻辑
            if (Constant.DEBUG_FLAG_GLOBAL_CONFIG_UPLOAD) {
                LoggerUtil.info(logger, "[forbiddenRule upload simulation], ruleId: {0}, ruleName: {0}", ruleId, ruleName);
                return null;
            } else {
                LoggerUtil.info(logger, "[forbiddenRule upload operation], ruleId: {0}, ruleName: {0}", ruleId, ruleName);
            }

            ForbiddenRuleDO forbiddenRule = null;
            ForbiddenGlobalItem forbiddenGlobalItem = null;
            if (ruleId != null) {
                forbiddenRule = forbiddenRuleDOMapper.selectById(ruleId);
                if (forbiddenRule == null) {
                    throw new BusinessException("查询不到对应的禁配规则, 请确认");
                }
                forbiddenGlobalItem = forbiddenGlobalItemDOMapper.selectByFRuleId(ruleId);
            }
            List<ImportRuleFileVo> iVosApply = new ArrayList<>();
            if (ruleId != null && ruleFileApply == null) {
                try {
                    if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcInclude())) {
                        File file;
                        String fileName;
//                        fileName = forbiddenRule.getName().trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "FORBIDDEN_APPLY" + ".xlsx";
                        fileName = forbiddenGlobalItem.getSkcInclude();
                        file = new File(UPLOAD_XLS_FORBIDDEN_DIR + fileName);
                        iVosApply = globalConfigRuleService.analyseRuleFile(file, ForbiddenRuleDO.STATUS_FORBIDDEN);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException("12000", e.getMessage());
                } catch (Exception e) {
                    throw new BusinessException("12000", "存量文件报错, 请重新导入");
                }
            } else {
                iVosApply = globalConfigRuleService.analyseRuleFile(ruleFileApply, ForbiddenRuleDO.STATUS_FORBIDDEN);
            }
            LoggerUtil.info(logger, "[uploadAndSaveSignleItemData2]|ruleId:{0},iVosApply:{1}", ruleId, iVosApply.stream().map(ImportRuleFileVo::getObj).collect(Collectors.toSet()));

            List<ImportRuleFileVo> iVosExclude = new ArrayList<>();
            if (ruleId != null && ruleFileExclude == null) {
                try {
                    if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcExclude())) {
                        File file;
                        String fileName;
//                        fileName = forbiddenRule.getName().trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "FORBIDDEN_EXCLUDE" + ".xlsx";
                        fileName = forbiddenGlobalItem.getSkcExclude();
                        file = new File(UPLOAD_XLS_FORBIDDEN_DIR + fileName);
                        iVosExclude = globalConfigRuleService.analyseRuleFile(file, ForbiddenRuleDO.STATUS_FORBIDDEN);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException("12000", e.getMessage());
                } catch (Exception e) {
                    throw new BusinessException("12000", "存量文件报错, 请重新导入");
                }
            } else {
                iVosExclude = globalConfigRuleService.analyseRuleFile(ruleFileExclude, ForbiddenRuleDO.STATUS_FORBIDDEN);
            }
            LoggerUtil.info(logger, "[uploadAndSaveSignleItemData2]|ruleId:{0},iVosExclude:{1}", ruleId, iVosExclude.stream().map(ImportRuleFileVo::getObj).collect(Collectors.toSet()));

            Map<String, List<ImportRuleFileVo>> typeMapApply =  globalConfigRuleService.getTypeMap(iVosApply);
            String bigCategoryApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_LARGE_CATEGORY).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            String midCategoryApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_MID_CATEGORY).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            String smallCategoryApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_SMALL_CATEGORY).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            Set<String> csvListSkcApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKC).stream().map(ImportRuleFileVo::getObj).collect(Collectors.toSet());
            Set<String> csvListSkuApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKU).stream().map(ImportRuleFileVo::getObj).collect(Collectors.toSet());

            Map<String, List<ImportRuleFileVo>> typeMapExclude =  globalConfigRuleService.getTypeMap(iVosExclude);
            String bigCategoryExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_LARGE_CATEGORY).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            String midCategoryExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_MID_CATEGORY).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            String smallCategoryExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_SMALL_CATEGORY).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            String skcsExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKC).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));
            String skusExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKU).stream().map(ImportRuleFileVo::getObj).collect(Collectors.joining(","));

            ForbiddenGlobalItem forbiddenGlobalItemI = new ForbiddenGlobalItem();
//            forbiddenGlobalItemI.setRuleType(GlobalConfigRuleDO.TYPE_FORBIDDEN);
            forbiddenGlobalItemI.setRegionInclude(areasApply);
            forbiddenGlobalItemI.setProvinceInclude(provincesApply);
            forbiddenGlobalItemI.setCityInclude(citysApply);
            forbiddenGlobalItemI.setLargeInclude(bigCategoryApply);
            forbiddenGlobalItemI.setMiddleInclude(midCategoryApply);
            forbiddenGlobalItemI.setSmallInclude(smallCategoryApply);
            forbiddenGlobalItemI.setShopInclude(shopsApply);
            forbiddenGlobalItemI.setSaleLvInclude(saleLevelsApply);
            forbiddenGlobalItemI.setDisplayLvInclude(displayLevelsApply);

            forbiddenGlobalItemI.setAttrFirValInclude(attrFirValInclude);
            forbiddenGlobalItemI.setAttrSecValInclude(attrSecValInclude);
            forbiddenGlobalItemI.setAttrThiValInclude(attrThiValInclude);
            forbiddenGlobalItemI.setAttrFourValInclude(attrFourValInclude);
            forbiddenGlobalItemI.setAttrFifValInclude(attrFifValInclude);

            forbiddenGlobalItemI.setRegionExclude(areasExclude);
            forbiddenGlobalItemI.setProvinceExclude(provincesExclude);
            forbiddenGlobalItemI.setCityExclude(citysExclude);
            forbiddenGlobalItemI.setLargeExclude(bigCategoryExclude);
            forbiddenGlobalItemI.setMiddleExclude(midCategoryExclude);
            forbiddenGlobalItemI.setSmallExclude(smallCategoryExclude);
            forbiddenGlobalItemI.setShopExclude(shopsExclude);
            forbiddenGlobalItemI.setSaleLvExclude(saleLevelsExclude);
            forbiddenGlobalItemI.setDisplayLvExclude(displayLevelsExclude);

            forbiddenGlobalItemI.setAttrFirValExclude(attrFirValExclude);
            forbiddenGlobalItemI.setAttrSecValExclude(attrSecValExclude);
            forbiddenGlobalItemI.setAttrThiValExclude(attrThiValExclude);
            forbiddenGlobalItemI.setAttrFourValExclude(attrFourValExclude);
            forbiddenGlobalItemI.setAttrFifValExclude(attrFifValExclude);

            //上传文件
            forbiddenGlobalItemI.setSkcInclude(globalConfigRuleService.uploadFile(ruleFileApply, ruleName, startDateStr, endDateStr, "FORBIDDEN_APPLY"));
            forbiddenGlobalItemI.setSkcExclude(globalConfigRuleService.uploadFile(ruleFileExclude, ruleName, startDateStr, endDateStr, "FORBIDDEN_EXCLUDE"));

            //获取shop应用与排除的差集
            Map<String, String> shopApplyMap =
                    globalConfigRuleService.getShopApplyMap(areasApply, provincesApply, citysApply, saleLevelsApply, displayLevelsApply, shopsApply,
                                                                attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                                                                areasExclude, provincesExclude, citysExclude, saleLevelsExclude, displayLevelsExclude, shopsExclude,
                                                                attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude);
            LoggerUtil.info(logger, "[uploadAndSaveSignleItemData2]|ruleId:{0},shopApplyMap:{1}", ruleId, shopApplyMap.keySet());
            //获取skc应用与排除的差集
            Set<String> skcApplySet = globalConfigRuleService.getSkcApplySet(bigCategoryApply, midCategoryApply, smallCategoryApply,
                    bigCategoryExclude, midCategoryExclude, smallCategoryExclude,
                    csvListSkcApply, skcsExclude);
            LoggerUtil.info(logger, "[uploadAndSaveSignleItemData2]|ruleId:{0},skcApplySet:{1}", ruleId, skcApplySet);
            //获取sku应用与排除的差集
            Set<String> skuApplySet = globalConfigRuleService.getSkuApplySet(csvListSkuApply, skusExclude);
            LoggerUtil.info(logger, "[uploadAndSaveSignleItemData2]|ruleId:{0},skuApplySet:{1}", ruleId, skuApplySet);

            Date now = new Date();
            ForbiddenRuleDO forbiddenRuleI = new ForbiddenRuleDO();
            if (ruleId != null) {
                forbiddenRuleI.setStartDate(startDate);
                forbiddenRuleI.setEndDate(endDate);
                forbiddenRuleI.setId(forbiddenRule.getId());
                forbiddenRuleI.setName(ruleName);
                forbiddenRuleI.setStatus(ForbiddenRuleDO.STATUS_FORBIDDEN);
                forbiddenRuleI.setUpdatedAt(now);
                forbiddenRuleDOMapper.updateById(forbiddenRuleI);
                forbiddenGlobalItemI.setfRuleId(ruleId.toString());
                forbiddenGlobalItemDOMapper.updateByFRuleId(forbiddenGlobalItemI);
            } else {
                forbiddenRuleI.setName(ruleName);
                forbiddenRuleI.setStartDate(startDate);
                forbiddenRuleI.setEndDate(endDate);
                forbiddenRuleI.setType(ForbiddenRuleDO.GLOBAL_TYPE);
                forbiddenRuleI.setCreatedAt(now);
                forbiddenRuleI.setUpdatedAt(now);
                forbiddenRuleI.setCreatedBy(updateUser);
                forbiddenRuleI.setUpdatedBy(updateUser);
                forbiddenRuleI.setStatus(ForbiddenRuleDO.STATUS_FORBIDDEN);
                forbiddenRuleDOMapper.insertSelective(forbiddenRuleI);
                ruleId = forbiddenRuleI.getId();
                forbiddenGlobalItemI.setfRuleId(ruleId.toString());
                forbiddenGlobalItemDOMapper.insertSelective(forbiddenGlobalItemI);
                forbiddenProceedMap.put(ruleId.toString(), "1");
            }

            List<ForbiddenSingleItemDO> forbiddenSingleItems = new ArrayList<>();
            //设置skc纬度
            for (String skcStr : skcApplySet) {
                for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
                    ForbiddenSingleItemDO item = new ForbiddenSingleItemDO();
                    item.setRuleName(ruleName);
                    item.setShopCode(entry.getValue());
                    item.setShopId(entry.getKey());

                    item.setRuleId(ruleId);
                    item.setType(ForbiddenSingleItem.SKC_TYPE);
                    item.setTypeForbidden(ForbiddenSingleItemDO.TYPE_FORBIDDEN_GLOBAL);
                    item.setTypeValue(skcStr);
//                item.setRemark(data.getRemark());
                    item.setStartDate(startDate);
                    item.setEndDate(endDate);
                    item.setModifiedBy(updateUser);
                    item.setCreatedAt(now);
                    item.setCreatedBy(updateUser);
                    forbiddenSingleItems.add(item);
                }
            }

            //设置sku纬度
            for (String skuStr : skuApplySet) {
                for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
                    ForbiddenSingleItemDO item = new ForbiddenSingleItemDO();
                    item.setRuleName(ruleName);
                    item.setShopCode(entry.getValue());
                    item.setShopId(entry.getKey());
                    item.setRuleId(ruleId);
                    item.setType(ForbiddenSingleItem.SKU_TYPE);
                    item.setTypeForbidden(ForbiddenSingleItemDO.TYPE_FORBIDDEN_GLOBAL);
                    item.setTypeValue(skuStr);
//                item.setRemark(data.getRemark());
                    item.setStartDate(startDate);
                    item.setEndDate(endDate);
                    item.setModifiedBy(updateUser);
                    item.setCreatedAt(now);
                    item.setCreatedBy(updateUser);
                    forbiddenSingleItems.add(item);
                }
            }

            //插入前清除以前数据
            forbiddenSingleItemDOMapper.deleteByRuleId(ruleId);
            securitySingleRuleDOMapper.deleteByRuleId(ruleId);

            int index = 0;
            int size = 1000;
            while(true) {
                List<ForbiddenSingleItemDO> subList = forbiddenSingleItems.stream().skip(index).limit(size).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(subList)) {
                    break;
                }
                ParameterMap param = new ParameterMap<>();
                param.put("singleList",subList);
                forbiddenSingleItemDOMapper.batchInsert(param);
                index += size;
            }

            logger.info("[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA] msg = SUCCESS. count:{}, ruleId:{}", forbiddenSingleItems.size(), ruleId);

            return forbiddenRuleI;
        } catch (BusinessException be) {
            LoggerUtil.error(be, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|catch exception {0}", be.getMessage());
            throw new BusinessException(be.getCode(), be.getMessage());
        } catch (Exception e) {
            LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|catch exception {0}", e.getMessage());
            throw e;
        } finally {
            if (ruleId != null) {
                forbiddenProceedMap.remove(ruleId.toString());
            }
        }
    }

    public User getRole() {
        String userId = AuthUtil.getSessionUserId();
        User user = new User();
        user.setUserid(userId);
        UserAdminDO userAdminDo = forbiddenRuleDOMapper.getAdmin(userId);
        logger.info("{} is admin:{}", userId, userAdminDo != null);
        if (userAdminDo != null) {
            user.setRole(1);
            user.setName(userAdminDo.getUserName());
        }
        return user;
    }

    public void batchInsert() {
        File file = new File("/Users/fanguiming/Documents/work-folders/NOME/智能配补调/全局禁配/forbidden_single_rule.sql");

        List<String> dataList = new ArrayList<>();
        BufferedReader br=null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        }catch (Exception e) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int index = 0;
        int size = 3000;
        int times = 1;

        List<ForbiddenSingleItemDO> list = new ArrayList<>();

        Map<String,Object> param = new HashMap<>();



        for (String sql : dataList) {

            try {
                String subSql = sql.substring(sql.indexOf("VALUES") + 6).trim();
                subSql = subSql.substring(2, subSql.length() - 3);
                subSql = subSql.replaceAll("'","").trim();
                String[] array = subSql.split(",");
                ForbiddenSingleItemDO item = new ForbiddenSingleItemDO();
                item.setId(Integer.parseInt(array[0]));
                item.setRuleName(array[1]);
                item.setRuleId(Integer.parseInt(array[2].trim()));
                item.setShopId(array[3]);
                item.setShopCode(array[4]);
                item.setShopName(array[5]);
                item.setType(Integer.parseInt(array[6].trim()));
                item.setTypeValue(array[7]);
                item.setRemark(array[8]);
                item.setStartDate(DateUtil.parse("2019-06-11 19:00:00", DateUtil.DATE_AND_TIME));
                item.setEndDate(DateUtil.parse("2029-06-11 19:00:00", DateUtil.DATE_AND_TIME));
                item.setModifiedBy(array[11]);
                item.setCreatedAt(array[12].equals("NULL") ? DateUtil.parse(array[12], DateUtil.DATE_AND_TIME) : null);
                item.setCreatedBy(array[13]);
                list.add(item);

                if (index > 0 && index % size == 0) {
                    param = new ParameterMap<>();
                    param.put("singleList",list);
                    forbiddenSingleItemDOMapper.batchInsert(param);
                    list.clear();
                    System.out.print("批量插入，当前插入批次:"+times);
                    times++;
                }

                index++;

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(list.size()>0) {
            param = new ParameterMap<>();
            param.put("singleList",list);
            forbiddenSingleItemDOMapper.batchInsert(param);
        }

    }

}
