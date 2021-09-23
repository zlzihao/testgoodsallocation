package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;
import cn.nome.saas.allocation.model.rule.ForbiddenGlobalItem;
import cn.nome.saas.allocation.model.rule.ForbiddenSingleItem;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenGlobalItemDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenRuleDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenSingleItemDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.SecuritySingleRuleDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.GlobalConfigRuleDO;
import cn.nome.saas.allocation.repository.entity.allocation.SecuritySingleRuleDO;
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
public class SecurityRuleService {

    private static Logger logger = LoggerFactory.getLogger(SecurityRuleService.class);

    static ConcurrentHashMap<String, String> securityProceedMap = new ConcurrentHashMap();

    @Autowired
    SecuritySingleRuleDOMapper securitySingleRuleDOMapper;

    @Autowired
    private GlobalConfigRuleService globalConfigRuleService;

    @Autowired
    private ForbiddenRuleDOMapper forbiddenRuleDOMapper;

    @Autowired
    private ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper;

    @Autowired
    private ForbiddenSingleItemDOMapper forbiddenSingleItemDOMapper;
    @Value("${upload.xls.forbidden.dir}")
    private String UPLOAD_XLS_FORBIDDEN_DIR;
    public List<SecuritySingleRuleDO> selectSecurityList(Set<String> shopIdList, Set<String> matCodeList) {

        Map<String,Object> param = new HashMap<>();
        param.put("shopIdList",shopIdList);
        param.put("matCodeList",matCodeList);

        return securitySingleRuleDOMapper.selectSecurityList(param);

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
                                                        String startDateStr, String endDateStr, String updateUser, boolean syncCreate) {

        try {
            if (ruleId != null && securityProceedMap.get(ruleId.toString()) != null) {
                throw new BusinessException("12000", "对应的保底规则正在生成中, 请稍后修改");
            }

            if (ruleId != null) {
                securityProceedMap.put(ruleId.toString(), "1");
            }

            ForbiddenRuleDO forbiddenRule = null;
            ForbiddenGlobalItem forbiddenGlobalItem = null;
            if (ruleId != null) {
                forbiddenRule = forbiddenRuleDOMapper.selectById(ruleId);
                if (forbiddenRule == null) {
                    throw new BusinessException("12000", "查询不到对应的保底规则, 请确认");
                }
                forbiddenGlobalItem = forbiddenGlobalItemDOMapper.selectByFRuleId(ruleId);
            }
            List<ImportRuleFileVo> iVosApply = new ArrayList<>();
            if (ruleId != null && ruleFileApply == null) {
                try {
                    if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcInclude())) {
                        File file;
                        String fileName;
//                        fileName = forbiddenRule.getName().trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "SECURITY_APPLY" + ".xlsx";
                        fileName = forbiddenGlobalItem.getSkcInclude();
                        file = new File(UPLOAD_XLS_FORBIDDEN_DIR + fileName);
                        iVosApply = globalConfigRuleService.analyseRuleFile(file, ForbiddenRuleDO.STATUS_SECURITY);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException("12000", e.getMessage());
                } catch (Exception e) {
                    throw new BusinessException("12000", "存量文件报错, 请重新导入");
                }
            } else {
                iVosApply = globalConfigRuleService.analyseRuleFile(ruleFileApply, ForbiddenRuleDO.STATUS_SECURITY);
            }
            List<ImportRuleFileVo> iVosExclude = new ArrayList<>();
            if (ruleId != null && ruleFileExclude == null) {
                try {
                    if (!StringUtils.isEmpty(forbiddenGlobalItem.getSkcExclude())) {
                        File file;
                        String fileName;
//                        fileName = forbiddenRule.getName().trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "SECURITY_EXCLUDE" + ".xlsx";
                        fileName = forbiddenGlobalItem.getSkcExclude();
                        file = new File(UPLOAD_XLS_FORBIDDEN_DIR + fileName);
                        iVosExclude = globalConfigRuleService.analyseRuleFile(file, ForbiddenRuleDO.STATUS_SECURITY);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException("12000", e.getMessage());
                } catch (Exception e) {
                    throw new BusinessException("12000", "存量文件报错, 请重新导入");
                }
            } else {
                iVosExclude = globalConfigRuleService.analyseRuleFile(ruleFileExclude, ForbiddenRuleDO.STATUS_SECURITY);
            }

            Map<String, List<ImportRuleFileVo>> typeMapApply =  globalConfigRuleService.getTypeMap(iVosApply);
            Map<String, Integer> bigCategoryApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_LARGE_CATEGORY).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> midCategoryApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_MID_CATEGORY).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> smallCategoryApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_SMALL_CATEGORY).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> skcApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKC).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> skuApply = typeMapApply.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKU).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));

            Map<String, List<ImportRuleFileVo>> typeMapExclude =  globalConfigRuleService.getTypeMap(iVosExclude);
            Map<String, Integer> bigCategoryExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_LARGE_CATEGORY).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> midCategoryExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_MID_CATEGORY).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> smallCategoryExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_SMALL_CATEGORY).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> skcExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKC).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));
            Map<String, Integer> skuExclude = typeMapExclude.get(GlobalConfigRuleService.RULE_FILE_TYPE_SKU).stream().collect(Collectors.toMap(ImportRuleFileVo::getObj, ImportRuleFileVo::getNum));


            //校验参数
            globalConfigRuleService.checkParam(areasApply, provincesApply, citysApply, saleLevelsApply, displayLevelsApply,
                    shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude, provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude
            );

            // TODO debug模式避开真实的上传逻辑
            if (Constant.DEBUG_FLAG_GLOBAL_CONFIG_UPLOAD) {
                LoggerUtil.info(logger, "[securityRule upload simulation], ruleId: {0}, ruleName: {0}", ruleId, ruleName);
                return null;
            } else {
                LoggerUtil.info(logger, "[securityRule upload operation], ruleId: {0}, ruleName: {0}", ruleId, ruleName);
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
                forbiddenGlobalItemI.setSkcInclude(globalConfigRuleService.uploadFile(ruleFileApply, ruleName, startDateStr, endDateStr, "SECURITY_APPLY"));
                forbiddenGlobalItemI.setSkcExclude(globalConfigRuleService.uploadFile(ruleFileExclude, ruleName, startDateStr, endDateStr, "SECURITY_EXCLUDE"));
            } else {
                //只保存数据库 不上传文件
                if (ruleFileApply != null) {
                    forbiddenGlobalItemI.setSkcInclude(ruleName.trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "SECURITY_APPLY" + ".xlsx");
                }
                if (ruleFileExclude != null) {
                    forbiddenGlobalItemI.setSkcExclude(ruleName.trim() + "-" + startDateStr.trim() + "-" + endDateStr.trim() + "-" + "SECURITY_EXCLUDE" + ".xlsx");
                }
            }


            //获取shop应用与排除的差集
            Map<String, String> shopApplyMap = globalConfigRuleService.getShopApplyMap(areasApply, provincesApply, citysApply, saleLevelsApply, displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude, provincesExclude, citysExclude, saleLevelsExclude, displayLevelsExclude, shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude);
            //获取skc应用与排除的差集
            Map<String, Integer> skcApplyMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry : bigCategoryApply.entrySet()) {
                skcApplyMap.putAll(globalConfigRuleService.getSkcMap(entry.getKey(), "", "", entry.getValue()));
            }
            for (Map.Entry<String, Integer> entry : midCategoryApply.entrySet()) {
                skcApplyMap.putAll(globalConfigRuleService.getSkcMap( "", entry.getKey(),"", entry.getValue()));
            }
            for (Map.Entry<String, Integer> entry : smallCategoryApply.entrySet()) {
                skcApplyMap.putAll(globalConfigRuleService.getSkcMap("", "", entry.getKey(), entry.getValue()));
            }
            skcApplyMap.putAll(skcApply);

            for (Map.Entry<String, Integer> entry : bigCategoryExclude.entrySet()) {
                for (String str : globalConfigRuleService.getSkcMap(entry.getKey(), "", "", entry.getValue()).keySet()) {
                    skcApplyMap.remove(str);
                }
            }
            for (Map.Entry<String, Integer> entry : midCategoryExclude.entrySet()) {
                for (String str : globalConfigRuleService.getSkcMap( "", entry.getKey(),"", entry.getValue()).keySet()) {
                    skcApplyMap.remove(str);
                }
            }
            for (Map.Entry<String, Integer> entry : smallCategoryExclude.entrySet()) {
                for (String str : globalConfigRuleService.getSkcMap( "", "", entry.getKey(), entry.getValue()).keySet()) {
                    skcApplyMap.remove(str);
                }
            }
            for (String str : skcExclude.keySet()) {
                skcApplyMap.remove(str);
            }

            Map<String, Integer> skuApplyMap = new HashMap<>(skuApply);
            for (String str : skuExclude.keySet()) {
                skuApplyMap.remove(str);
            }

//            Set<String> skcApplySet = globalConfigRuleService.getSkcApplySet(bigCategoryApply, midCategoryApply, smallCategoryApply,
//                    bigCategoryExclude, midCategoryExclude, smallCategoryExclude,
//                    csvListSkcApply, skcsExclude);
//            //获取sku应用与排除的差集
//            Set<String> skuApplySet = globalConfigRuleService.getSkuApplySet(csvListSkuApply, skusExclude);

            Date now = new Date();
            ForbiddenRuleDO forbiddenRuleI = new ForbiddenRuleDO();
            if (ruleId != null) {
                forbiddenRuleI.setStartDate(startDate);
                forbiddenRuleI.setEndDate(endDate);
                forbiddenRuleI.setId(forbiddenRule.getId());
                forbiddenRuleI.setName(ruleName);
                forbiddenRuleI.setStatus(ForbiddenRuleDO.STATUS_SECURITY);
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
                forbiddenRuleI.setStatus(ForbiddenRuleDO.STATUS_SECURITY);
                forbiddenRuleDOMapper.insertSelective(forbiddenRuleI).toString();
                ruleId = forbiddenRuleI.getId();
                forbiddenGlobalItemI.setfRuleId(ruleId.toString());
                forbiddenGlobalItemDOMapper.insertSelective(forbiddenGlobalItemI);
                securityProceedMap.put(ruleId.toString(), "1");
            }


            //保底
            List<SecuritySingleRuleDO> securitySingleItemDOS = new ArrayList<>();
            //设置skc纬度
            for (Map.Entry<String, Integer> entrySkc : skcApplyMap.entrySet()) {
                for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
                    SecuritySingleRuleDO item = new SecuritySingleRuleDO();
                    item.setRuleName(ruleName);
                    item.setShopCode(entry.getValue());
                    item.setShopId(entry.getKey());
                    item.setRuleId(ruleId);
                    item.setType(SecuritySingleRuleDO.SKC_TYPE);
                    item.setTypeValue(entrySkc.getKey());
                    item.setNum(entrySkc.getValue());
//                item.setRemark(data.getRemark());
                    item.setStartDate(startDate);
                    item.setEndDate(endDate);
                    item.setModifiedBy(updateUser);
                    item.setCreatedAt(now);
                    item.setCreatedBy(updateUser);
                    securitySingleItemDOS.add(item);
                }
            }

            //设置sku纬度
            for (Map.Entry<String, Integer> entrySku : skuApplyMap.entrySet()) {
                for (Map.Entry<String, String> entry : shopApplyMap.entrySet()) {
                    SecuritySingleRuleDO item = new SecuritySingleRuleDO();
                    item.setRuleName(ruleName);
                    item.setShopCode(entry.getValue());
                    item.setShopId(entry.getKey());
                    item.setRuleId(ruleId);
                    item.setType(SecuritySingleRuleDO.SKU_TYPE);
                    item.setTypeValue(entrySku.getKey());
                    item.setNum(entrySku.getValue());
//                item.setRemark(data.getRemark());
                    item.setStartDate(startDate);
                    item.setEndDate(endDate);
                    item.setModifiedBy(updateUser);
                    item.setCreatedAt(now);
                    item.setCreatedBy(updateUser);
                    securitySingleItemDOS.add(item);
                }
            }

            //插入前清除以前数据
            securitySingleRuleDOMapper.deleteByRuleId(ruleId);

            //
            int indexSec = 0;
            int sizeSec = 1000;
            while(true) {
                List<SecuritySingleRuleDO> subList = securitySingleItemDOS.stream().skip(indexSec).limit(sizeSec).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(subList)) {
                    break;
                }
                ParameterMap param = new ParameterMap<>();
                param.put("singleList",subList);
                securitySingleRuleDOMapper.batchInsert(param);
                indexSec += sizeSec;
            }

            return forbiddenRuleI;
        } catch (BusinessException be) {
            throw new BusinessException(be.getCode(), be.getMessage());
        } catch (Exception e) {
            LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|catch exception {0}", e.getMessage());
            throw e;
        } finally {
            if (ruleId != null) {
                securityProceedMap.remove(ruleId.toString());
            }
        }
    }
}
