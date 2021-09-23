package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;
import cn.nome.saas.allocation.model.rule.ForbiddenGlobalItem;
import cn.nome.saas.allocation.model.rule.ForbiddenSingleItem;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.GlobalConfigRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.SecuritySingleRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.WhiteListSingleItemDO;
import org.apache.catalina.util.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * SecurityRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/23
 */
@Service
public class WhiteListRuleService {

    private static Logger logger = LoggerFactory.getLogger(WhiteListRuleService.class);

    static ConcurrentHashMap<String, String> whiteListProceedMap = new ConcurrentHashMap();

    @Autowired
    SecuritySingleRuleDOMapper securitySingleRuleDOMapper;

    @Autowired
    private GlobalConfigRuleService globalConfigRuleService;

    @Autowired
    private ForbiddenRuleDOMapper forbiddenRuleDOMapper;

    @Autowired
    private ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper;

    @Autowired
    private WhiteListSingleItemDOMapper whiteListSingleItemDOMapper;
    @Value("${upload.xls.forbidden.dir}")
    private String UPLOAD_XLS_FORBIDDEN_DIR;
    public List<SecuritySingleRuleDO> selectSecurityList(Set<String> shopIdList, Set<String> matCodeList) {

        Map<String,Object> param = new HashMap<>();
        param.put("shopIdList",shopIdList);
        param.put("matCodeList",matCodeList);

        return securitySingleRuleDOMapper.selectSecurityList(param);

    }

    public ForbiddenRuleDO uploadAndSaveSignleItemData2(String ruleName, Integer ruleId, MultipartFile ruleFileApply, int fileInDelFlag,
                                                        MultipartFile ruleFileExclude, int fileExDelFlag,
                                                        String areasApply, String provincesApply, String citysApply, String saleLevelsApply, String displayLevelsApply,
                                                        String shopsApply,
                                                        String attrFirValInclude, String attrSecValInclude, String attrThiValInclude, String attrFourValInclude, String attrFifValInclude,
                                                        String areasExclude, String provincesExclude, String citysExclude, String saleLevelsExclude, String displayLevelsExclude,
                                                        String shopsExclude,
                                                        String attrFirValExclude, String attrSecValExclude, String attrThiValExclude, String attrFourValExclude, String attrFifValExclude,
                                                        String startDateStr,String endDateStr,String updateUser, boolean syncCreate, Integer securityRuleId) {

        try {
            //若为同步生成的白名单, 获取同步生成的白名单id
            if (securityRuleId != null) {
                ForbiddenRuleDO syncRule = forbiddenRuleDOMapper.selectBySyncId(securityRuleId);
                if (syncRule != null && syncRule.getId() != null) {
                    ruleId = syncRule.getId();
                }
            }

            if (ruleId != null && whiteListProceedMap.get(ruleId.toString()) != null) {
                throw new BusinessException("12000", "对应的白名单规则正在生成中, 请稍后修改");
            }

            if (ruleId != null) {
                whiteListProceedMap.put(ruleId.toString(), "1");
            }

            ForbiddenRuleDO forbiddenRule = null;
            ForbiddenGlobalItem forbiddenGlobalItem = null;
            if (ruleId != null) {
                forbiddenRule = forbiddenRuleDOMapper.selectById(ruleId);
                if (forbiddenRule == null) {
                    throw new BusinessException("12000", "查询不到对应的白名单规则, 请确认");
                }
                forbiddenGlobalItem = forbiddenGlobalItemDOMapper.selectByFRuleId(ruleId);
            }
            List<ImportRuleFileVo> iVosApply = new ArrayList<>();
            if (ruleId != null && ruleFileApply == null) {
                try {
                    if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcInclude())) {
                        File file;
                        String fileName;
//                        fileName = forbiddenRule.getName().trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "WHITELIST_APPLY" + ".xlsx";
                        fileName = forbiddenGlobalItem.getSkcInclude();
                        file = new File(UPLOAD_XLS_FORBIDDEN_DIR + fileName);
                        iVosApply = globalConfigRuleService.analyseRuleFile(file, ForbiddenRuleDO.STATUS_WHITE_LIST);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException("12000", e.getMessage());
                } catch (Exception e) {
                    throw new BusinessException("12000", "存量文件报错, 请重新导入");
                }
            } else {
                iVosApply = globalConfigRuleService.analyseRuleFile(ruleFileApply, ForbiddenRuleDO.STATUS_WHITE_LIST);
            }
            List<ImportRuleFileVo> iVosExclude = new ArrayList<>();
            if (ruleId != null && ruleFileExclude == null) {
                try {
                    if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcExclude())) {
                        File file;
                        String fileName;
//                        fileName = forbiddenRule.getName().trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "WHITELIST_EXCLUDE" + ".xlsx";
                        fileName = forbiddenGlobalItem.getSkcExclude();
                        file = new File(UPLOAD_XLS_FORBIDDEN_DIR + fileName);
                        iVosExclude = globalConfigRuleService.analyseRuleFile(file, ForbiddenRuleDO.STATUS_WHITE_LIST);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException("12000", e.getMessage());
                } catch (Exception e) {
                    throw new BusinessException("12000", "存量文件报错, 请重新导入");
                }
            } else {
                iVosExclude = globalConfigRuleService.analyseRuleFile(ruleFileExclude, ForbiddenRuleDO.STATUS_WHITE_LIST);
            }

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


            //校验参数
            globalConfigRuleService.checkParam(areasApply, provincesApply, citysApply, saleLevelsApply, displayLevelsApply,
                    shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude, provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude
            );

            // TODO debug模式避开真实的上传逻辑
            if (Constant.DEBUG_FLAG_GLOBAL_CONFIG_UPLOAD) {
                LoggerUtil.info(logger, "[whiteListRule upload simulation], ruleId: {0}, ruleName: {0}", ruleId, ruleName);
                return null;
            } else {
                LoggerUtil.info(logger, "[whiteListRule upload operation], ruleId: {0}, ruleName: {0}", ruleId, ruleName);
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

            ForbiddenGlobalItem forbiddenGlobalItemI = new ForbiddenGlobalItem();
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
            if (!syncCreate) {
                forbiddenGlobalItemI.setSkcInclude(globalConfigRuleService.uploadFile(ruleFileApply, ruleName, startDateStr, endDateStr, "WHITELIST_APPLY"));
                forbiddenGlobalItemI.setSkcExclude(globalConfigRuleService.uploadFile(ruleFileExclude, ruleName, startDateStr, endDateStr, "WHITELIST_EXCLUDE"));
            } else {
                forbiddenGlobalItemI.setSkcInclude(globalConfigRuleService.uploadFile(ruleFileApply, ruleName, startDateStr, endDateStr, "SECURITY_APPLY"));
                forbiddenGlobalItemI.setSkcExclude(globalConfigRuleService.uploadFile(ruleFileExclude, ruleName, startDateStr, endDateStr, "SECURITY_EXCLUDE"));
            }

            //获取shop应用与排除的差集
            Map<String, String> shopApplyMap = globalConfigRuleService.getShopApplyMap(areasApply, provincesApply, citysApply, saleLevelsApply, displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude, provincesExclude, citysExclude, saleLevelsExclude, displayLevelsExclude, shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude);
            //获取skc应用与排除的差集
            Set<String> skcApplySet = globalConfigRuleService.getSkcApplySet(bigCategoryApply, midCategoryApply, smallCategoryApply,
                    bigCategoryExclude, midCategoryExclude, smallCategoryExclude,
                    csvListSkcApply, skcsExclude);
            //获取sku应用与排除的差集
            Set<String> skuApplySet = globalConfigRuleService.getSkuApplySet(csvListSkuApply, skusExclude);

            Date now = new Date();
            ForbiddenRuleDO forbiddenRuleI = new ForbiddenRuleDO();
            if (ruleId != null) {
                forbiddenRuleI.setStartDate(startDate);
                forbiddenRuleI.setEndDate(endDate);
                forbiddenRuleI.setId(forbiddenRule.getId());
                forbiddenRuleI.setName(ruleName);
                forbiddenRuleI.setStatus(ForbiddenRuleDO.STATUS_WHITE_LIST);
                forbiddenRuleI.setUpdatedAt(now);
                forbiddenRuleDOMapper.updateById(forbiddenRuleI);
                forbiddenGlobalItemI.setfRuleId(ruleId.toString());
                forbiddenGlobalItemDOMapper.updateByFRuleId(forbiddenGlobalItemI);
            } else {

                forbiddenRuleI.setStartDate(startDate);
                forbiddenRuleI.setEndDate(endDate);
                forbiddenRuleI.setType(ForbiddenRuleDO.GLOBAL_TYPE);
                forbiddenRuleI.setCreatedAt(now);
                forbiddenRuleI.setUpdatedAt(now);
                forbiddenRuleI.setCreatedBy(updateUser);
                forbiddenRuleI.setUpdatedBy(updateUser);
                int statusRule = ForbiddenRuleDO.STATUS_WHITE_LIST;
                if (syncCreate) {
                    ruleName = "由“" + ruleName + "”生成的白名单";
                    forbiddenRuleI.setSyncId(securityRuleId);
                    statusRule = ForbiddenRuleDO.STATUS_SECURITY_AND_WHITE_LIST;
                }
                forbiddenRuleI.setStatus(statusRule);
                forbiddenRuleI.setName(ruleName);
                forbiddenRuleDOMapper.insertSelective(forbiddenRuleI);
                ruleId = forbiddenRuleI.getId();
                forbiddenGlobalItemI.setfRuleId(ruleId.toString());
                forbiddenGlobalItemDOMapper.insertSelective(forbiddenGlobalItemI);
                whiteListProceedMap.put(ruleId.toString(), "1");
            }

            List<ForbiddenSingleItem> forbiddenSingleItems = new ArrayList<>();
            //设置skc纬度
            for (String skcStr : skcApplySet) {
                for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
                    ForbiddenSingleItem item = new ForbiddenSingleItem();
                    item.setRuleName(ruleName);
                    item.setShopCode(entry.getValue());
                    item.setShopId(entry.getKey());

                    item.setRuleId(ruleId);
                    item.setType(ForbiddenSingleItem.SKC_TYPE);
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
                    ForbiddenSingleItem item = new ForbiddenSingleItem();
                    item.setRuleName(ruleName);
                    item.setShopCode(entry.getValue());
                    item.setShopId(entry.getKey());
                    item.setRuleId(ruleId);
                    item.setType(ForbiddenSingleItem.SKU_TYPE);
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
            whiteListSingleItemDOMapper.deleteByRuleId(ruleId);

            int index = 0;
            int size = 1000;
            while(true) {
                List<ForbiddenSingleItem> subList = forbiddenSingleItems.stream().skip(index).limit(size).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(subList)) {
                    break;
                }
                ParameterMap param = new ParameterMap<>();
                param.put("singleList",subList);
                whiteListSingleItemDOMapper.batchInsert(param);
                index += size;
            }

            return forbiddenRuleI;
        } catch (BusinessException be) {
            throw new BusinessException(be.getCode(), be.getMessage());
        } catch (Exception e) {
            LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|catch exception {0}", e.getMessage());
            throw e;
        } finally {
            if (ruleId != null) {
                whiteListProceedMap.remove(ruleId.toString());
            }
        }
    }

    public List<WhiteListSingleItemDO> getQdIssueWhitelist() {

        Map<String,Object> param = new HashMap<>();

        return whiteListSingleItemDOMapper.getQdIssueWhitelist(param);
    }
}
