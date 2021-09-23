package cn.nome.saas.allocation.service.rule;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.GoodsInfoCache;
import cn.nome.saas.allocation.cache.ShopInfoCache;
import cn.nome.saas.allocation.cache.ShopListCache;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.feign.api.WarnClient;
import cn.nome.saas.allocation.model.allocation.GoodsInfoTask;
import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;
import cn.nome.saas.allocation.model.allocation.ShopInfoTask;
import cn.nome.saas.allocation.model.issue.ShopInfoData;
import cn.nome.saas.allocation.model.rule.ForbiddenGlobalItem;
import cn.nome.saas.allocation.repository.dao.allocation.*;
import cn.nome.saas.allocation.repository.entity.allocation.*;
import cn.nome.saas.allocation.service.basic.WarningService;
import cn.nome.saas.allocation.utils.FileUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import cn.nome.saas.allocation.repository.dao.allocation.DwsDimShopDOMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ForbiddenRuleService
 *
 * @author Bruce01.fan
 * @date 2019/6/18
 */
@Service
public class GlobalConfigRuleService {

    private static Logger logger = LoggerFactory.getLogger(GlobalConfigRuleService.class);

    public final static String ATTR_VALUE_SPLIT = ",";

    private final static String UPLOAD_FILE_SUFFIX = ".xlsx";

    public final static String RULE_FILE_TYPE_LARGE_CATEGORY = "大类";
    public final static String RULE_FILE_TYPE_MID_CATEGORY = "中类";
    public final static String RULE_FILE_TYPE_SMALL_CATEGORY = "小类";
    public final static String RULE_FILE_TYPE_SKU = "sku";
    public final static String RULE_FILE_TYPE_SKU_UPPER = "SKU";
    public final static String RULE_FILE_TYPE_SKC = "skc";
    public final static String RULE_FILE_TYPE_SKC_UPPER = "SKC";

    @Value("${upload.xls.forbidden.dir}")
    private String UPLOAD_XLS_FORBIDDEN_DIR;

    @Autowired
    private ForbiddenRuleDOMapper forbiddenRuleDOMapper;
    @Autowired
    private DwsDimGoodsDOMapper dwsDimGoodsDOMapper;

    @Autowired
    private ForbiddenRuleService forbiddenRuleService;

    @Autowired
    private WhiteListRuleService whiteListRuleService;

    @Autowired
    private SecurityRuleService securityRuleService;

    @Autowired
    private DwsDimShopDOMapper dwsShopDOMapper;

    @Autowired
    ShopListCache shopListCache;

    @Autowired
    ShopInfoCache shopInfoCache;

    @Autowired
    ShopInfoDOMapper shopInfoDOMapper;

    @Autowired
    NewIssueDOMapper newIssueDOMapper;

    @Autowired
    ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper;

    @Autowired
    GoodsInfoDOMapper goodsInfoDOMapper;

    @Autowired
    GoodsInfoCache goodsInfoCache;

    //@Autowired
    //WarnClient warnClient;
    //WarningService warningService;

    @Transactional(value = "allocationTransactionManager", rollbackFor = Exception.class)
    public ForbiddenRuleDO uploadAndSave(Integer ruleType, String ruleName, Integer ruleId, MultipartFile fileSkc, int fileInDelFlag, MultipartFile fileSku, int fileExDelFlag,
                                            String areasApply, String provincesApply, String citysApply, String saleLevelsApply, String displayLevelsApply, String shopsApply,
                                            String attrFirValInclude, String attrSecValInclude, String attrThiValInclude, String attrFourValInclude, String attrFifValInclude,
                                            String areasExclude, String provincesExclude, String citysExclude, String saleLevelsExclude, String displayLevelsExclude,
                                            String shopsExclude,
                                            String attrFirValExclude, String attrSecValExclude, String attrThiValExclude, String attrFourValExclude, String attrFifValExclude,
                                            String startDate, String endDate, String updateUser) {
        if (ruleName != null && ruleName.contains(File.separator)) {
            throw new BusinessException("12000", "规则名称请不要包含系统文件分隔符\"" + File.separator + "\"");
        }
        ForbiddenRuleDO forbiddenRuleDO = new ForbiddenRuleDO();
        Boolean syncCreate = false;

        if (ruleType == ForbiddenRuleDO.STATUS_FORBIDDEN) {
            forbiddenRuleDO = forbiddenRuleService.uploadAndSaveSignleItemData2(ruleName, ruleId, fileSkc, fileInDelFlag, fileSku, fileExDelFlag,
                    areasApply,  provincesApply,  citysApply,  saleLevelsApply,  displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude,  provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude, shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser);
        } else if (ruleType == ForbiddenRuleDO.STATUS_SECURITY) {
            ForbiddenRuleDO syncRule = new ForbiddenRuleDO();
            if (ruleId != null) {
                syncRule = forbiddenRuleDOMapper.selectBySyncId(ruleId);
                if (syncRule != null) {
                    syncCreate = true;
                }
            }
            securityRuleService.uploadAndSaveSignleItemData2(ruleName, ruleId, fileSkc, fileInDelFlag, fileSku, fileExDelFlag,
                    areasApply,  provincesApply,  citysApply,  saleLevelsApply,  displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude,  provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser, syncCreate);

            //修改保底时, 若有同时生成的白名单需同时修改
            if (syncCreate) {
                    whiteListRuleService.uploadAndSaveSignleItemData2(ruleName, syncRule.getId(), fileSkc, fileInDelFlag, fileSku, fileExDelFlag,
                            areasApply,  provincesApply,  citysApply,  saleLevelsApply,  displayLevelsApply, shopsApply,
                            attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                            areasExclude,  provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                            shopsExclude,
                            attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                            startDate, endDate, updateUser, true, syncRule.getId());
            }

        }  else if (ruleType == ForbiddenRuleDO.STATUS_SECURITY_AND_WHITE_LIST) {
            if (ruleId != null && SecurityRuleService.securityProceedMap.containsKey(ruleId)) {
                throw new BusinessException("12000", "对应的保底规则正在生成中, 请稍后修改");
            }
            if (ruleId != null && WhiteListRuleService.whiteListProceedMap.containsKey(ruleId)) {
                throw new BusinessException("12000", "对应的白名单规则正在生成中, 请稍后修改");
            }
            forbiddenRuleDO = securityRuleService.uploadAndSaveSignleItemData2(ruleName, ruleId, fileSkc, fileInDelFlag, fileSku, fileExDelFlag,
                    areasApply,  provincesApply,  citysApply,  saleLevelsApply,  displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude,  provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser, true);
            int securityRuleId = forbiddenRuleDO.getId();

            whiteListRuleService.uploadAndSaveSignleItemData2(ruleName, ruleId, fileSkc, fileInDelFlag, fileSku, fileExDelFlag,
                    areasApply,  provincesApply,  citysApply,  saleLevelsApply,  displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude,  provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser, true, securityRuleId);

        } else if (ruleType == ForbiddenRuleDO.STATUS_WHITE_LIST) {
            forbiddenRuleDO = whiteListRuleService.uploadAndSaveSignleItemData2(ruleName, ruleId, fileSkc, fileInDelFlag, fileSku, fileExDelFlag,
                    areasApply,  provincesApply,  citysApply,  saleLevelsApply,  displayLevelsApply, shopsApply,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    areasExclude,  provincesExclude,  citysExclude,  saleLevelsExclude,  displayLevelsExclude,
                    shopsExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser, false, null);
        } else {
            throw new BusinessException("12000", "规则类型无匹配");
        }

        return forbiddenRuleDO;
    }

    /**
     * 同步全局配置信息
     */
    public Result syncGlobalConfigData() {

        /**
         * 门店信息
         */
        List<ShopInfoTask> shopInfoRunningTaskList = shopInfoDOMapper.getShopTaskRunningTask();

        // 超过30分钟的任务未执行完成的，放入重试任务
        if (shopInfoRunningTaskList.size() > 0) {
            for (ShopInfoTask shopInfoTask : shopInfoRunningTaskList) {
                shopInfoDOMapper.updateShopInfoTaskRetry(shopInfoTask.getId());
            }
        }

        int runningTaskCnt = shopInfoDOMapper.checkShopTaskRunningTask();
        if (runningTaskCnt > 0) {
            LoggerUtil.info(logger,"[SYNC_CONFIG_RUNNING_TASK] msg=skip the task");
            return ResultUtil.handleFailtureReturn("12000","Task is Running!!");
        }

        // 获取当前最前的一个任务执行
        List<ShopInfoTask> shopInfoTaskList = shopInfoDOMapper.getLastShopTask();

        if (CollectionUtils.isNotEmpty(shopInfoTaskList)) {

            LoggerUtil.info(logger,"[SYNC_CONFIG_TASK] msg=start task:{0}",shopInfoTaskList.size());

            List<cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO> dwsDimShopDOList = shopListCache.getShopList();
            List<ShopInfoData> shopInfoDataList = shopInfoCache.getShopList();

            for (ShopInfoTask shopInfoTask : shopInfoTaskList) {

                // 判断当前是否有配发任务
                Integer runningTaskCount = newIssueDOMapper.getRunningIssueTask();

                if (runningTaskCount > 0) {
                    return ResultUtil.handleFailtureReturn("12000","Issue Task is Running!!");
                }

                if (shopInfoTask.getStatus() == 3) {
                    if (shopInfoTask.getRetry() >=3) {
                        // 重试超过3次，发起微信告警
                        //warningService.sendWechatWarnMsg("pbd","markdown","","各单位注意，门店禁配任务:["+shopInfoTask.getId()+"],已重试超过3次，请及时处理！！！");
                        LoggerUtil.warn(logger,"[SNY_CONFIG_WARN] msg=task:{0}",shopInfoTask.getId());
                        continue;
                    }
                    shopInfoDOMapper.updateShopInfoTaskToReRun(shopInfoTask.getId());
                } else {
                    shopInfoDOMapper.updateShopInfoTaskToRun(shopInfoTask.getId());
                }

                // 根据当前shopid获取出所有的禁配、白名单、保底规则
                cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO dwsDimShopDO = dwsDimShopDOList.stream().filter(shopDO -> shopDO.getShopId().equals(shopInfoTask.getShopId())).findFirst().orElse(null);
                ShopInfoData shopInfo = shopInfoDataList.stream().filter(shopInfoData -> shopInfoData.getShopID().equals(shopInfoTask.getShopId())).findFirst().orElse(null);

                if (dwsDimShopDO == null || shopInfo == null) {
                    shopInfoDOMapper.updateShopInfoTaskFinish(shopInfoTask.getId());
                    continue;
                }

                try {
                    //  禁配
                    int fbFlag = this.syncGlobalConfigRule(dwsDimShopDO, shopInfo, ForbiddenRuleDO.STATUS_FORBIDDEN);
                    // 白名单
                    int wFlag = this.syncGlobalConfigRule(dwsDimShopDO, shopInfo, ForbiddenRuleDO.STATUS_WHITE_LIST);
                    // 保底
                    int sFlag = this.syncGlobalConfigRule(dwsDimShopDO, shopInfo, ForbiddenRuleDO.STATUS_SECURITY);

                    LoggerUtil.info(logger,"[SHOP_CONFIG_SYNC_STATUS] msg=fb:{0},white:{1},security:{2},id:{3}",fbFlag,wFlag,sFlag,shopInfoTask.getId());

                    // 三个数据处理完成，更新任务状态
                    if (fbFlag ==0 && wFlag == 0 && sFlag==0) {
                        shopInfoDOMapper.updateShopInfoTaskFinish(shopInfoTask.getId());
                    } else {
                        shopInfoDOMapper.updateShopInfoTaskToReady(shopInfoTask.getId());
                    }
                } catch (Exception e ) {
                    LoggerUtil.error(e,logger,"[SYNC_CONFIG_TASK_ERROR],msg=task:{0}",shopInfoTask.getId());
                    shopInfoDOMapper.updateShopInfoTaskRetry(shopInfoTask.getId());
                }

                LoggerUtil.info(logger,"[SYNC_CONFIG_TASK_FINISH] msg=task:{0}",shopInfoTask.getId());
            }
        }

        /**
         * 商品信息
         */
        int runningGoodsTaskCnt = goodsInfoDOMapper.checkGoodsTaskRunningTask();
        if (runningGoodsTaskCnt > 0) {
            return ResultUtil.handleFailtureReturn("12000","Task is Running!!");
        }

        List<GoodsInfoDO> goodsInfoDOList = goodsInfoCache.getGoodsInfo();

        List<GoodsInfoTask> goodsInfoTaskList = goodsInfoDOMapper.getLastGoodsTask();

        if (CollectionUtils.isNotEmpty(goodsInfoTaskList)) {

            for (GoodsInfoTask goodsInfoTask : goodsInfoTaskList) {
                // 判断当前是否有配发任务
                Integer runningTaskCount = newIssueDOMapper.getRunningIssueTask();

                if (runningTaskCount > 0) {
                    return ResultUtil.handleFailtureReturn("12000","Issue Task is Running!!");
                }

                goodsInfoDOMapper.updateGoodsInfoTaskToRun(goodsInfoTask.getId());


                GoodsInfoDO goodsInfoDO = goodsInfoDOList.stream()
                        .filter(goodsInfo -> goodsInfo.getMatCode().equals(goodsInfoTask.getMatCode()))
                        .findFirst()
                        .orElse(null);

                try {
                    int fbFlag = this.syncGoodsGlobalConfigRule(goodsInfoDO,ForbiddenRuleDO.STATUS_FORBIDDEN);
                    int wFlag = this.syncGoodsGlobalConfigRule(goodsInfoDO,ForbiddenRuleDO.STATUS_WHITE_LIST);
                    int sFlag = this.syncGoodsGlobalConfigRule(goodsInfoDO,ForbiddenRuleDO.STATUS_SECURITY);

                    LoggerUtil.info(logger,"[CONFIG_SYNC_STATUS] msg=fb:{0},white:{1},security:{2}",fbFlag,wFlag,sFlag);

                    if (fbFlag ==0 && wFlag == 0 && sFlag==0) {
                        goodsInfoDOMapper.updateGoodsInfoTaskFinish(goodsInfoTask.getId());
                    } else {
                        goodsInfoDOMapper.updateGoodsInfoTaskToReady(goodsInfoTask.getId());
                    }

                } catch (Exception e) {
                    LoggerUtil.error(e, logger,"[SYNC_CONFIG_TASK_ERROR], goods task, syncGoodsGlobalConfigRule, taskId: {0}", goodsInfoTask.getId());
                    goodsInfoDOMapper.updateGoodsInfoTaskToRetry(goodsInfoTask.getId());
                }
            }
        }


        return ResultUtil.handleSuccessReturn();
    }

    /**
     * 禁配/白名单的规则
     * @param dwsDimShopDO
     * @param shopInfo
     * @param status
     */
    private int syncGlobalConfigRule(cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO dwsDimShopDO,ShopInfoData shopInfo,int status) {

        List<Integer> ruleIdList = null;
        if (status == ForbiddenRuleDO.STATUS_FORBIDDEN) {
            // 禁配
            ruleIdList = forbiddenGlobalItemDOMapper.selectForbiddenRule(shopInfo.getShopID());
        } else if (status == ForbiddenRuleDO.STATUS_WHITE_LIST) {
            // 白名单
            ruleIdList = forbiddenGlobalItemDOMapper.selectWhiteListRule(shopInfo.getShopID());
        } else if (status == ForbiddenRuleDO.STATUS_SECURITY) {
            // 保底
            ruleIdList = forbiddenGlobalItemDOMapper.selectSecurityRule(shopInfo.getShopID());
        }


        Map<String,Object> param = new HashMap<>();
        param.put("region",shopInfo.getGoodsArea());
        param.put("province",dwsDimShopDO.getProvinceName());
        param.put("city",dwsDimShopDO.getCityName());
        param.put("shopName",dwsDimShopDO.getShopName());
        param.put("saleLv",shopInfo.getShopLevel());
        param.put("displayLv",shopInfo.getDisplayLevel());
        param.put("type",1);
        param.put("status", status);
        if (CollectionUtils.isNotEmpty(ruleIdList)) {
            param.put("ruleIdList", ruleIdList);
        }

        List<ForbiddenGlobalItem> globalItemList = forbiddenGlobalItemDOMapper.selectBySelective(param);

        if (CollectionUtils.isEmpty(globalItemList)){
            return 0;
        }

        LoggerUtil.debug(logger,"[SYNC_CONFIG_FB] msg=fb rule size:{0}",globalItemList.size());

        for (ForbiddenGlobalItem globalItem : globalItemList) {

            LoggerUtil.debug(logger,"[SYNC_CONFIG_FB] msg=fb rule name:{0}",globalItem.getRuleName());

            try {
                this.uploadAndSave(status,globalItem.getRuleName(),Integer.parseInt(globalItem.getfRuleId()),null,-1,
                    null,-1,globalItem.getRegionInclude(),globalItem.getProvinceInclude(),globalItem.getCityInclude(),
                    globalItem.getSaleLvInclude(),globalItem.getDisplayLvInclude(),globalItem.getShopInclude(),
                    globalItem.getAttrFirValInclude(),globalItem.getAttrSecValInclude(),globalItem.getAttrThiValInclude(),
                    globalItem.getAttrFourValInclude(),globalItem.getAttrFifValInclude(),globalItem.getRegionExclude(),
                    globalItem.getProvinceExclude(),globalItem.getCityExclude(),globalItem.getSaleLvExclude(),globalItem.getDisplayLvExclude(),
                    globalItem.getShopExclude(),globalItem.getAttrFirValExclude(),globalItem.getAttrSecValExclude(),globalItem.getAttrThiValExclude(),
                    globalItem.getAttrFourValExclude(),globalItem.getAttrFifValExclude(), DateUtil.format(globalItem.getStartDate(),"yyyyMMdd"),DateUtil.format(globalItem.getEndDate(),"yyyyMMdd"),"admin");
            } catch (BusinessException e) {
                LoggerUtil.info(e, logger, "[SYNC_CONFIG_FB], syncGlobalConfigRule, uploadAndSave catch exception, fRuleId: {0}, ruleName: {1}", globalItem.getfRuleId(), globalItem.getRuleName());
            }
        }

        return 0;
    }


    private int syncGoodsGlobalConfigRule(GoodsInfoDO goodsInfoDO,int status) {

        List<Integer> ruleIdList = null;
        if (status == ForbiddenRuleDO.STATUS_FORBIDDEN) {
            // 禁配
            ruleIdList = forbiddenGlobalItemDOMapper.selectForbiddenRuleByMatCode(goodsInfoDO.getMatCode());
        } else if (status == ForbiddenRuleDO.STATUS_WHITE_LIST) {
            // 白名单
            ruleIdList = forbiddenGlobalItemDOMapper.selectWhiteListRuleByMatCode(goodsInfoDO.getMatCode());
        } else if (status == ForbiddenRuleDO.STATUS_SECURITY) {
            // 保底
            ruleIdList = forbiddenGlobalItemDOMapper.selectSecurityRuleByMatCode(goodsInfoDO.getMatCode());
        }

        Map<String,Object> param = new HashMap<>();
        param.put("largeInclude",goodsInfoDO.getCategoryName());
        param.put("middleInclude",goodsInfoDO.getMidCategoryName());
        param.put("smallInclude",goodsInfoDO.getSmallCategoryName());
        param.put("type",1);
        param.put("status", status);
        if (CollectionUtils.isNotEmpty(ruleIdList)) {
            param.put("ruleIdList", ruleIdList);
        }

        List<ForbiddenGlobalItem> globalItemList = forbiddenGlobalItemDOMapper.selectBySelective(param);

        if (CollectionUtils.isEmpty(globalItemList)){
            LoggerUtil.warn(logger,"[CONFIG_FB_EMPTY] msg=goodsInfoDO:{0}",goodsInfoDO);
            return 0;
        }

        LoggerUtil.debug(logger,"[SYNC_CONFIG_FB] msg=fb rule size:{0}",globalItemList.size());

        for (ForbiddenGlobalItem globalItem : globalItemList) {

            LoggerUtil.debug(logger,"[SYNC_CONFIG_FB] msg=fb rule name:{0}",globalItem.getRuleName());

            try {
                this.uploadAndSave(status,globalItem.getRuleName(),Integer.parseInt(globalItem.getfRuleId()),null,-1,
                    null,-1,globalItem.getRegionInclude(),globalItem.getProvinceInclude(),globalItem.getCityInclude(),
                    globalItem.getSaleLvInclude(),globalItem.getDisplayLvInclude(),globalItem.getShopInclude(),
                    globalItem.getAttrFirValInclude(),globalItem.getAttrSecValInclude(),globalItem.getAttrThiValInclude(),
                    globalItem.getAttrFourValInclude(),globalItem.getAttrFifValInclude(),globalItem.getRegionExclude(),
                    globalItem.getProvinceExclude(),globalItem.getCityExclude(),globalItem.getSaleLvExclude(),globalItem.getDisplayLvExclude(),
                    globalItem.getShopExclude(),globalItem.getAttrFirValExclude(),globalItem.getAttrSecValExclude(),globalItem.getAttrThiValExclude(),
                    globalItem.getAttrFourValExclude(),globalItem.getAttrFifValExclude(), DateUtil.format(globalItem.getStartDate(),"yyyyMMdd"),DateUtil.format(globalItem.getEndDate(),"yyyyMMdd"),"admin");
            } catch (BusinessException e) {
                LoggerUtil.info(e, logger, "[SYNC_CONFIG_FB], syncGoodsGlobalConfigRule, uploadAndSave catch exception, fRuleId: {0}, ruleName: {1}", globalItem.getfRuleId(), globalItem.getRuleName());
            }
        }

        return 0;
    }


    private static Set<String> REGION_SET = new HashSet<>();
    private static Set<String> PROVINCE_SET = new HashSet<>();
    private static Set<String> CITY_SET = new HashSet<>();
//    private static Set<String> SHOP_CODE_SET = new HashSet<>();
    private static Map<String, String> SHOP_ID_MAP = new HashMap<>();
    private static Map<String, String> SHOP_NAME_MAP = new HashMap<>();
    private static Set<String> CATEGORY_SET = new HashSet<>();
    private static Set<String> MID_CATEGORY_SET = new HashSet<>();
    private static Set<String> SMALL_CATEGORY_SET = new HashSet<>();
    private static Set<String> SALE_LV_SET = new HashSet<>();
    private static Set<String> DISPLAY_LV_SET = new HashSet<>();

    private static Map<String, String> GOODS_NAME_MAP = new HashMap<>();

    public Set<String> getRegionSet() {
        if (REGION_SET == null || REGION_SET.size() == 0) {
            try {
                REGION_SET = new HashSet<>(forbiddenRuleDOMapper.getRegioneNameList());
                REGION_SET.remove("");
            } catch (Exception e) {
                REGION_SET = new HashSet<>();
            }
        }
        return REGION_SET;
    }
    public Set<String> getProvinceSet() {
        if (PROVINCE_SET == null || PROVINCE_SET.size() == 0) {
            try {
                PROVINCE_SET = new HashSet<>(forbiddenRuleDOMapper.getProvinceNameList());
                PROVINCE_SET.remove("");
            } catch (Exception e) {
                PROVINCE_SET = new HashSet<>();
            }
        }
        return PROVINCE_SET;
    }
    public Set<String> getCitySet() {
        if (CITY_SET == null || CITY_SET.size() == 0) {
            try {
                CITY_SET = new HashSet<>(forbiddenRuleDOMapper.getCityNameList());
                CITY_SET.remove("");
            } catch (Exception e) {
                CITY_SET = new HashSet<>();
            }
        }
        return CITY_SET;
    }
    public Map<String, String> getShopIdMap() {
        try {
            List<cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO> dwsDimShopDOS = shopListCache.getShopList();
            return dwsDimShopDOS.stream().collect(Collectors.toMap(cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopId, cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopName));
        } catch (Exception e) {
        }
        return new HashMap<>();
    }


    /**
     * getShopCodeMap
     * @return Map<shopCode, shopId>
     */
    public Map<String, String> getShopCodeMap() {
        try {
            List<cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO> dwsDimShopDOS = shopListCache.getShopList();
            return dwsDimShopDOS.stream().collect(Collectors.toMap(cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopCode, cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopId));
        } catch (Exception e) {
        }
        return new HashMap<>();
    }

    public Map<String, String> getShopNameMap() {
        if (SHOP_NAME_MAP == null || SHOP_NAME_MAP.size() == 0) {
            try {
                List<cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO> dwsDimShopDOS = shopListCache.getShopList();
                SHOP_NAME_MAP = dwsDimShopDOS.stream().collect(Collectors.toMap(cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopName, cn.nome.saas.allocation.repository.entity.allocation.DwsDimShopDO::getShopId, (v1, v2) -> v2));
            } catch (Exception e) {
                SHOP_NAME_MAP = new HashMap<>();
            }
        }
        return SHOP_NAME_MAP;
    }
    public Set<String> getCategorySet() {
        if (CATEGORY_SET == null || CATEGORY_SET.size() == 0) {
            try {
                CATEGORY_SET = new HashSet<>(forbiddenRuleDOMapper.getCategoryNameList());
                CATEGORY_SET.remove("");
            } catch (Exception e) {
                CATEGORY_SET = new HashSet<>();
            }
        }
        return CATEGORY_SET;
    }
    public Set<String> getMidCategorySet() {
        if (MID_CATEGORY_SET == null || MID_CATEGORY_SET.size() == 0) {
            try {
                MID_CATEGORY_SET = new HashSet<>(forbiddenRuleDOMapper.getMidCategoryNameList());
                MID_CATEGORY_SET.remove("");
            } catch (Exception e) {
                MID_CATEGORY_SET = new HashSet<>();
            }
        }
        return MID_CATEGORY_SET;
    }
    public Set<String> getSmallCategorySet() {
        if (SMALL_CATEGORY_SET == null || SMALL_CATEGORY_SET.size() == 0) {
            try {
                SMALL_CATEGORY_SET = new HashSet<>(forbiddenRuleDOMapper.getSmallCategoryNameList());
                SMALL_CATEGORY_SET.remove("");
            } catch (Exception e) {
                SMALL_CATEGORY_SET = new HashSet<>();
            }
        }
        return SMALL_CATEGORY_SET;
    }
    public Set<String> getSaleLvSet() {
        if (SALE_LV_SET == null || SALE_LV_SET.size() == 0) {
            try {
                SALE_LV_SET = new HashSet<>(forbiddenRuleDOMapper.getShopSaleLvList());
                SALE_LV_SET.remove("");
            } catch (Exception e) {
                SALE_LV_SET = new HashSet<>();
            }
        }
        return SALE_LV_SET;
    }
    public Set<String> getDisplayLvSet() {
        if (DISPLAY_LV_SET == null || DISPLAY_LV_SET.size() == 0) {
            try {
                DISPLAY_LV_SET = new HashSet<>(forbiddenRuleDOMapper.getShopDisplayLvList());
                DISPLAY_LV_SET.remove("");
            } catch (Exception e) {
                DISPLAY_LV_SET = new HashSet<>();
            }
        }
        return DISPLAY_LV_SET;
    }
//    public Map<String, String> getGoodsNameMap() {
//        if (GOODS_NAME_MAP == null || GOODS_NAME_MAP.size() == 0) {
//            try {
//                List<DwsDimGoodsDO> dwsDimShopDOS = dwsDimGoodsDOMapper.getList();
//                GOODS_NAME_MAP = dwsDimShopDOS.stream().collect(Collectors.toMap(DwsDimGoodsDO::getMatCode,
//                        DwsDimGoodsDO -> {
//                            if (StringUtils.isBlank(DwsDimGoodsDO.getMatName())) {
//                                return "";
//                            } else {
//                                return DwsDimGoodsDO.getMatName();
//                            }
//                }));
//            } catch (Exception e) {
//                GOODS_NAME_MAP = new HashMap<>();
//            }
//        }
//        return GOODS_NAME_MAP;
//    }
    public Map<String, String> getGoodsNameMap() {
        return GOODS_NAME_MAP;
    }


    public void checkParam(String areasApply, String provincesApply, String citysApply, String saleLevelsApply, String displayLevelsApply,
                           String shopsApply,
//                    String bigCategoryApply, String midCategoryApply , String smallCategoryApply,
                           String attrFirValInclude, String attrSecValInclude, String attrThiValInclude, String attrFourValInclude, String attrFifValInclude,
                           String areasExclude, String provincesExclude, String citysExclude, String saleLevelsExclude, String displayLevelsExclude,
                           String shopsExclude
//                    ,  String bigCategoryExclude, String midCategoryExclude, String smallCategoryExclude
    ) {

        if (StringUtils.isEmpty(areasApply)
                && StringUtils.isEmpty(provincesApply)
                && StringUtils.isEmpty(citysApply)
                && StringUtils.isEmpty(saleLevelsApply)
                && StringUtils.isEmpty(displayLevelsApply)
                && StringUtils.isEmpty(shopsApply)
                && StringUtils.isEmpty(attrFirValInclude)
                && StringUtils.isEmpty(attrSecValInclude)
                && StringUtils.isEmpty(attrThiValInclude)
                && StringUtils.isEmpty(attrFourValInclude)
                && StringUtils.isEmpty(attrFifValInclude)) {

            throw new BusinessException("12000", "'应用门店范围'需至少一项有值, 请检查");
        }

        Set<String> regionSet = getRegionSet();
        if (!StringUtils.isEmpty(areasApply)) {
            for (String region : areasApply.split(ATTR_VALUE_SPLIT)) {
                if (regionSet.size() > 0 && !regionSet.contains(region)) {
                    logger.warn("[checkParam], 应用门店范围-大区, 异常值：{}", region);
                    // throw new BusinessException("12000", String.format("'应用门店范围-大区'(%s)输入有误, 请检查", region));
                }
            }
        }
        if (!StringUtils.isEmpty(areasExclude)) {
            for (String region : areasExclude.split(ATTR_VALUE_SPLIT)) {
                if (regionSet.size() > 0 && !regionSet.contains(region)) {
                    logger.warn("[checkParam], 排除门店范围-大区, 异常值：{}", region);
                    // throw new BusinessException("12000", String.format("'排除门店范围-大区'(%s)输入有误, 请检查", region));
                }
            }
        }

        Set<String> provinceSet = getProvinceSet();
        if (!StringUtils.isEmpty(provincesApply)) {
            for (String province : provincesApply.split(ATTR_VALUE_SPLIT)) {
                if (provinceSet.size() > 0 && !provinceSet.contains(province)) {
                    logger.warn("[checkParam], 应用门店范围-省份, 异常值：{}", province);
                    // throw new BusinessException("12000", String.format("'应用门店范围-省份'(%s)输入有误, 请检查", province));
                }
            }
        }
        if (!StringUtils.isEmpty(provincesExclude)) {
            for (String province : provincesExclude.split(ATTR_VALUE_SPLIT)) {
                if (provinceSet.size() > 0 && !provinceSet.contains(province)) {
                    logger.warn("[checkParam], 排除门店范围-省份, 异常值：{}", province);
                    // throw new BusinessException("12000", String.format("'排除门店范围-省份'(%s)输入有误, 请检查", province));
                }
            }
        }

        Set<String> citySet = getCitySet();
        if (!StringUtils.isEmpty(citysApply)) {
            for (String city : citysApply.split(ATTR_VALUE_SPLIT)) {
                if (citySet.size() > 0 && !citySet.contains(city)) {
                    logger.warn("[checkParam], 应用门店范围-城市, 异常值：{}", city);
                    // throw new BusinessException("12000", String.format("'应用门店范围-城市'(%s)输入有误, 请检查", city));
                }
            }
        }
        if (!StringUtils.isEmpty(citysExclude)) {
            for (String city : citysExclude.split(ATTR_VALUE_SPLIT)) {
                if (citySet.size() > 0 && !citySet.contains(city)) {
                    logger.warn("[checkParam], 排除门店范围-城市, 异常值：{}", city);
                    // throw new BusinessException("12000", String.format("'排除门店范围-城市'(%s)输入有误, 请检查", city));
                }
            }
        }

        Set<String> shopNameSet = getShopNameMap().keySet();
        if (!StringUtils.isEmpty(shopsApply)) {
            for (String shopName : shopsApply.split(ATTR_VALUE_SPLIT)) {
                if (shopNameSet.size() > 0 && !shopNameSet.contains(shopName)) {
                    logger.warn("[checkParam], 应用门店范围-门店名称, 异常值：{}", shopName);
                    // throw new BusinessException("12000", String.format("'应用门店范围-门店名称'(%s)输入有误, 请检查", shopName));
                }
            }
        }
        if (!StringUtils.isEmpty(shopsExclude)) {
            for (String shopName : shopsExclude.split(ATTR_VALUE_SPLIT)) {
                if (shopNameSet.size() > 0 && !shopNameSet.contains(shopName)) {
                    logger.warn("[checkParam], 排除门店范围-门店名称, 异常值：{}", shopName);
                    // throw new BusinessException("12000", String.format("'排除门店范围-门店名称'(%s)输入有误, 请检查", shopName));
                }
            }
        }

//        Set<String> categorySet = getCategorySet();
//        if (!StringUtils.isEmpty(bigCategoryApply)) {
//            for (String category : bigCategoryApply.split(ATTR_VALUE_SPLIT)) {
//                if (categorySet.size() > 0 && !categorySet.contains(category)) {
//                    throw new BusinessException("12000", "'应用商品范围-大类'输入有误, 请检查");
//                }
//            }
//        }
//        if (!StringUtils.isEmpty(bigCategoryExclude)) {
//            for (String category : bigCategoryExclude.split(ATTR_VALUE_SPLIT)) {
//                if (categorySet.size() > 0 && !categorySet.contains(category)) {
//                    throw new BusinessException("12000", "'排除商品范围-大类'输入有误, 请检查");
//                }
//            }
//        }
//
//        Set<String> midCategorySet = getMidCategorySet();
//        if (!StringUtils.isEmpty(midCategoryApply)) {
//            for (String midCategory : midCategoryApply.split(ATTR_VALUE_SPLIT)) {
//                if (midCategorySet.size() > 0 && !midCategorySet.contains(midCategory)) {
//                    throw new BusinessException("12000", "'应用商品范围-中类'输入有误, 请检查");
//                }
//            }
//        }
//        if (!StringUtils.isEmpty(midCategoryExclude)) {
//            for (String midCategory : midCategoryExclude.split(ATTR_VALUE_SPLIT)) {
//                if (midCategorySet.size() > 0 && !midCategorySet.contains(midCategory)) {
//                    throw new BusinessException("12000", "'排除商品范围-中类'输入有误, 请检查");
//                }
//            }
//        }
//
//        Set<String> smallCategorySet = getSmallCategorySet();
//        if (!StringUtils.isEmpty(smallCategoryApply)) {
//            for (String smallCategory : smallCategoryApply.split(ATTR_VALUE_SPLIT)) {
//                if (smallCategorySet.size() > 0 && !smallCategorySet.contains(smallCategory)) {
//                    throw new BusinessException("12000", "'应用商品范围-小类'输入有误, 请检查");
//                }
//            }
//        }
//        if (!StringUtils.isEmpty(smallCategoryExclude)) {
//            for (String smallCategory : smallCategoryExclude.split(ATTR_VALUE_SPLIT)) {
//                if (smallCategorySet.size() > 0 && !smallCategorySet.contains(smallCategory)) {
//                    throw new BusinessException("12000", "'排除商品范围-小类'输入有误, 请检查");
//                }
//            }
//        }
        Set<String> saleLvSet = getSaleLvSet();
        if (!StringUtils.isEmpty(saleLevelsApply)) {
            for (String s : saleLevelsApply.split(ATTR_VALUE_SPLIT)) {
                if (saleLvSet.size() > 0 && !saleLvSet.contains(s)) {
                    logger.warn("[checkParam], 应用商品范围-门店销售等级, 异常值：{}", s);
                    // throw new BusinessException("12000", "'应用商品范围-门店销售等级'输入有误, 请检查");
                }
            }
        }
        if (!StringUtils.isEmpty(saleLevelsExclude)) {
            for (String s : saleLevelsExclude.split(ATTR_VALUE_SPLIT)) {
                if (saleLvSet.size() > 0 && !saleLvSet.contains(s)) {
                    logger.warn("[checkParam], 排除商品范围-门店销售等级, 异常值：{}", s);
                    // throw new BusinessException("12000", "'排除商品范围-门店销售等级'输入有误, 请检查");
                }
            }
        }
        Set<String> displayLvSet = getDisplayLvSet();
        if (!StringUtils.isEmpty(displayLevelsApply)) {
            for (String d : displayLevelsApply.split(ATTR_VALUE_SPLIT)) {
                if (displayLvSet.size() > 0 && !displayLvSet.contains(d)) {
                    logger.warn("[checkParam], 应用商品范围-门店陈列等级, 异常值：{}", d);
                    // throw new BusinessException("12000", "'应用商品范围-门店陈列等级'输入有误, 请检查");
                }
            }
        }
        if (!StringUtils.isEmpty(displayLevelsExclude)) {
            for (String d : displayLevelsExclude.split(ATTR_VALUE_SPLIT)) {
                if (displayLvSet.size() > 0 && !displayLvSet.contains(d)) {
                    logger.warn("[checkParam], 排除商品范围-门店陈列等级, 异常值：{}", d);
                    // throw new BusinessException("12000", "'排除商品范围-门店陈列等级'输入有误, 请检查");
                }
            }
        }

    }

    Map<String, Integer> getRuleFileDataMap(MultipartFile file, String type, Integer ruleType) {
        if (file != null) {
            try {
                Workbook wb;
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                String  extString = fileName.substring(fileName.lastIndexOf("."));
                if (UPLOAD_FILE_SUFFIX.equalsIgnoreCase(extString)) {
                    InputStream is = file.getInputStream();
                    wb = new XSSFWorkbook(is);
                } else {
                    throw new BusinessException("10001", "文件类型错误!");
                }
                Sheet sheet = wb.getSheetAt(0);
                int totalRows = sheet.getPhysicalNumberOfRows();
                Map<String, Integer> ruleFileMap = new HashMap<>(totalRows);
                for (int i = 1; i < totalRows; i++) {
                    try {
                        Row row = sheet.getRow(i);
                        if (row.getRowNum() < 1) {
                            continue;
                        }
                        if (ruleType == ForbiddenRuleDO.STATUS_SECURITY) {
                            double typeNum = row.getCell(2).getNumericCellValue();
                            ruleFileMap.put(row.getCell(0).getStringCellValue(), (int) typeNum);
                        } else {
                            ruleFileMap.put(row.getCell(0).getStringCellValue(), 0);
                        }
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|" + type + "文件转换第{0}行出错",i);
                        throw new BusinessException("12000", type + "文件转换第" + i + "行出错, 请检查文件");
                    }
                }
                return ruleFileMap;
            } catch (BusinessException be) {
                throw new BusinessException("12000", be.getMessage());
            }  catch (Exception e) {
                LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|" + type + "文件转换出错:{0}",file);
                throw new BusinessException("12000", type + "文件转换出错, 请检查文件");
            }
        }
        return new HashMap<>(0);
    }

    public String uploadFile(MultipartFile file, String ruleName, String startDate, String endDate, String type) {
        if (file != null) {
            try {
                String fileName = ruleName.trim() + "-" + startDate.trim() + "-" + endDate.trim() + "-" + type + ".xlsx";
                FileUtil.uploadFile(file, UPLOAD_XLS_FORBIDDEN_DIR, fileName);
                return fileName;
            } catch (Exception e) {
                LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|导入" + type + "文件上传失败,file:{0}",file);
                throw new BusinessException("12000", "导入" + type + "文件上传失败, 请检查文件");
            }
        }
        return null;
    }


    public Map<String, String>  getShopApplyMap(String areasApply, String provincesApply, String citysApply, String saleLevelsApply, String displayLevelsApply, String shopsApply,
                                         String attrFirValApply, String attrSecValApply, String attrThiValApply, String attrFourValApply, String attrFifValApply,
                                                 String areasExclude, String provincesExclude, String citysExclude, String saleLevelsExclude, String displayLevelsExclude, String shopsExclude,
                                         String attrFirValEx, String attrSecValEx, String attrThiValEx, String attrFourValEx, String attrFifValEx) {
        //应用
        List<DwsDimShopDO> dwsDimShopDosApply = new ArrayList<>();
        Map<String, Object> paramMapShop = new HashMap<>(3);
//        if (!StringUtils.isEmpty(areasApply)) {
//            paramMapShop.put("regioneNames", areasApply.split(ATTR_VALUE_SPLIT));
//        }
        if (!StringUtils.isEmpty(provincesApply)) {
            paramMapShop.put("provinceNames", provincesApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(citysApply)) {
            paramMapShop.put("cityNames", citysApply.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapShop.size() > 0) {
            dwsDimShopDosApply = dwsShopDOMapper.selectShopListByRegioneProvinceCity(paramMapShop);
        }

        List<ShopInfoData> shopInfoDatasApply = new ArrayList<>();
        Map<String, Object> paramMapShopLv = new HashMap<>(2);
        if (!StringUtils.isEmpty(saleLevelsApply)) {
            paramMapShopLv.put("shopLevels", saleLevelsApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(displayLevelsApply)) {
            paramMapShopLv.put("displayLevels", displayLevelsApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(areasApply)) {
            paramMapShopLv.put("regions", areasApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrFirValApply)) {
            paramMapShopLv.put("attrFirVals", attrFirValApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrSecValApply)) {
            paramMapShopLv.put("attrSecVals", attrSecValApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrThiValApply)) {
            paramMapShopLv.put("attrThiVals", attrThiValApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrFourValApply)) {
            paramMapShopLv.put("attrFourVals", attrFourValApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrFifValApply)) {
            paramMapShopLv.put("attrFifVals", attrFifValApply.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapShopLv.size() > 0) {
            shopInfoDatasApply = forbiddenRuleDOMapper.getShopBySaleDisplayLv(paramMapShopLv);
        }

        //排除
        List<DwsDimShopDO> dwsDimShopDOSExclude = new ArrayList<>();
        Map<String, Object> paramMapShopExclude = new HashMap<>(3);
//        if (!StringUtils.isEmpty(areasExclude)) {
//            paramMapShopExclude.put("regioneNames", areasExclude.split(ATTR_VALUE_SPLIT));
//        }
        if (!StringUtils.isEmpty(provincesExclude)) {
            paramMapShopExclude.put("provinceNames", provincesExclude.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(citysExclude)) {
            paramMapShopExclude.put("cityNames", citysExclude.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapShopExclude.size() > 0) {
            dwsDimShopDOSExclude = dwsShopDOMapper.selectShopListByRegioneProvinceCity(paramMapShopExclude);
        }

        List<ShopInfoData> shopInfoDatasExclude = new ArrayList<>();
        Map<String, Object> paramMapShopLvExclude = new HashMap<>(2);
        if (!StringUtils.isEmpty(saleLevelsExclude)) {
            paramMapShopLvExclude.put("shopLevels", saleLevelsExclude.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(displayLevelsExclude)) {
            paramMapShopLvExclude.put("displayLevels", displayLevelsExclude.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(areasExclude)) {
            paramMapShopLvExclude.put("regions", areasExclude.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrFirValEx)) {
            paramMapShopLvExclude.put("attrFirVals", attrFirValEx.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrSecValEx)) {
            paramMapShopLvExclude.put("attrSecVals", attrSecValEx.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrThiValEx)) {
            paramMapShopLvExclude.put("attrThiVals", attrThiValEx.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrFourValEx)) {
            paramMapShopLvExclude.put("attrFourVals", attrFourValEx.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(attrFifValEx)) {
            paramMapShopLvExclude.put("attrFifVals", attrFifValEx.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapShopLvExclude.size() > 0) {
            shopInfoDatasExclude = forbiddenRuleDOMapper.getShopBySaleDisplayLv(paramMapShopLvExclude);
        }

        //应用店铺的并集 //与排除的差集
        Map<String, String> shopApplyMap = dwsDimShopDosApply.stream().collect(Collectors.toMap(DwsDimShopDO::getShopId, DwsDimShopDO::getShopCode));
        for (ShopInfoData shopInfoData : shopInfoDatasApply) {
            shopApplyMap.put(shopInfoData.getShopID(), shopInfoData.getShopCode());
        }
        if (!StringUtils.isEmpty(shopsApply)) {
            for (String shops : shopsApply.split(ATTR_VALUE_SPLIT)) {
                Map<String, String>  shopNameMap = getShopNameMap();
                //如果传进来的是店铺名称, 判断店铺名称, 否则判断shopid
                if (shopNameMap.containsKey(shops)) {
                    String shopId = shopNameMap.get(shops);
                    if (!shopApplyMap.containsKey(shopId)) {
                        shopApplyMap.put(shopId, null);
                    }
                } else {
                    if (getShopIdMap().containsKey(shops) && !shopApplyMap.containsKey(shops)) {
                        shopApplyMap.put(shops, null);
                    }
                }
            }
        }
        for (DwsDimShopDO dwsDimShopDO : dwsDimShopDOSExclude) {
            shopApplyMap.remove(dwsDimShopDO.getShopId());
        }
        for (ShopInfoData shopInfoData : shopInfoDatasExclude) {
            shopApplyMap.remove(shopInfoData.getShopID());
        }
        if (!StringUtils.isEmpty(shopsExclude)) {
            for (String shops : shopsExclude.split(ATTR_VALUE_SPLIT)) {
                //如果传进来的是shopId, 则直接删除, 否则传进来的是shopName, 查询店铺id后删除
                if (shopApplyMap.containsKey(shops)) {
                    shopApplyMap.remove(shops);
                } else {
                    String shopId = getShopNameMap().get(shops);
                    shopApplyMap.remove(shopId);
                }

            }
        }

        return shopApplyMap;
    }

    public Set<String> getSkcApplySet(String bigCategoryApply, String midCategoryApply, String smallCategoryApply,
                                       String bigCategoryExclude, String midCategoryExclude, String smallCategoryExclude,
                                       Set<String> csvListSkcApply, String skcsExclude) {

        List<String> skcsCategoryApply = new ArrayList<>();
        Map<String, Object> paramMapSkc = new HashMap<>(3);
        if (!StringUtils.isEmpty(bigCategoryApply)) {
            paramMapSkc.put("bigCategoryNames", bigCategoryApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategoryApply)) {
            paramMapSkc.put("midCategoryNames", midCategoryApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategoryApply)) {
            paramMapSkc.put("smallCategoryNames", smallCategoryApply.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapSkc.size() > 0) {
            skcsCategoryApply = forbiddenRuleDOMapper.getSkcByCategory(paramMapSkc);
        }

        List<String> skcsCategoryExclude = new ArrayList<>();
        Map<String, Object> paramMapSkcExclude = new HashMap<>(3);
        if (!StringUtils.isEmpty(bigCategoryExclude)) {
            paramMapSkcExclude.put("bigCategoryNames", bigCategoryExclude.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategoryExclude)) {
            paramMapSkcExclude.put("midCategoryNames", midCategoryExclude.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategoryExclude)) {
            paramMapSkcExclude.put("smallCategoryNames", smallCategoryExclude.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapSkcExclude.size() > 0) {
            skcsCategoryExclude = forbiddenRuleDOMapper.getSkcByCategory(paramMapSkcExclude);
        }

        //skc的并集 //与排除的差集
        Set<String> skcApplySet = new HashSet<>(skcsCategoryApply);
        skcApplySet.addAll(csvListSkcApply);
        skcApplySet.removeAll(skcsCategoryExclude);
        if (!StringUtils.isEmpty(skcsExclude)) {
            skcApplySet.removeAll(Arrays.asList(skcsExclude.split(ATTR_VALUE_SPLIT)));
        }
        return skcApplySet;
    }

    public Set<String> getSkuApplySet(Set<String> csvListSkuApply, String skusExclude) {
        //sku的并集 //与排除的差集
        Set<String> skuApplySet = new HashSet<>(csvListSkuApply);
        if (!StringUtils.isEmpty(skusExclude)) {
            skuApplySet.removeAll(Arrays.asList(skusExclude.split(ATTR_VALUE_SPLIT)));
        }
        return skuApplySet;
    }


    public List<ImportRuleFileVo> analyseRuleFile(MultipartFile file, Integer ruleType) {
        if (file != null) {
            try {
                Workbook wb;
                String fileName = file.getOriginalFilename();
                String  extString = fileName.substring(fileName.lastIndexOf("."));
                if (UPLOAD_FILE_SUFFIX.equalsIgnoreCase(extString)) {
                    InputStream is = file.getInputStream();
                    wb = new XSSFWorkbook(is);
                } else {
                    throw new BusinessException("10001", "文件类型错误!");
                }
                Sheet sheet = wb.getSheetAt(0);
                int totalRows = sheet.getPhysicalNumberOfRows();
                List<ImportRuleFileVo> iVos = new ArrayList<>();
                for (int i = 1; i < totalRows; i++) {
                    try {
                        Row row = sheet.getRow(i);
                        if (row.getRowNum() < 1) {
                            continue;
                        }
                        ImportRuleFileVo iVo = new ImportRuleFileVo();
                        String type = row.getCell(0).getStringCellValue();
                        String obj = row.getCell(1).getStringCellValue();
                        checkFileContent(type, obj, i);
                        iVo.setType(type);
                        iVo.setObj(obj);
                        Cell cell2;
                        if ((cell2 = row.getCell(2)) != null && cell2.getCellTypeEnum().equals(CellType.STRING) && StringUtils.isEmpty(cell2.getStringCellValue())) {
                            iVo.setName(row.getCell(2).getStringCellValue());
                        } else {
                            iVo.setName(getGoodsNameMap().get(obj));
                        }
                        if (ruleType == ForbiddenRuleDO.STATUS_SECURITY) {
                            int num = (int) row.getCell(3).getNumericCellValue();
                            if (num <= 0) {
                                throw new BusinessException("12000", "文件转换第" + (i+1) + "行出错, 保底数量需大于0");
                            }
                            iVo.setNum(num);
                        }
                        iVos.add(iVo);
                    } catch (BusinessException bz) {
                        throw new BusinessException(bz.getCode(), bz.getMessage());
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|文件转换第{0}行出错",i+1);
                        throw new BusinessException("12000", "文件转换第" + (i+1) + "行出错, 请检查文件");
                    }
                }
                return iVos;
            } catch (BusinessException be) {
                throw new BusinessException(be.getCode(), be.getMessage());
            }  catch (Exception e) {
                LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|文件转换出错:{0}",file);
                throw new BusinessException("12000", "文件转换出错, 请检查文件");
            }
        }
        return new ArrayList<>();
    }

    public List<ImportRuleFileVo> analyseRuleFile(File file, Integer ruleType) {
        if (file != null) {
            try {
                Workbook wb;
                String fileName = file.getName();
                String  extString = fileName.substring(fileName.lastIndexOf("."));
                if (UPLOAD_FILE_SUFFIX.equalsIgnoreCase(extString)) {
                    InputStream is = new FileInputStream(file);
                    wb = new XSSFWorkbook(is);
                } else {
                    throw new BusinessException("10001", "文件类型错误!");
                }
                Sheet sheet = wb.getSheetAt(0);
                int totalRows = sheet.getPhysicalNumberOfRows();
                List<ImportRuleFileVo> iVos = new ArrayList<>();
                for (int i = 1; i < totalRows; i++) {
                    try {
                        Row row = sheet.getRow(i);
                        if (row.getRowNum() < 1) {
                            continue;
                        }
                        ImportRuleFileVo iVo = new ImportRuleFileVo();
                        String type = row.getCell(0).getStringCellValue();
                        String obj = row.getCell(1).getStringCellValue();
                        checkFileContent(type, obj, i);
                        iVo.setType(type);
                        iVo.setObj(obj);
                        Cell cell2;
                        if ((cell2 = row.getCell(2)) != null && cell2.getCellTypeEnum().equals(CellType.STRING) && StringUtils.isEmpty(cell2.getStringCellValue())) {
                            iVo.setName(row.getCell(2).getStringCellValue());
                        } else {
                            iVo.setName(getGoodsNameMap().get(obj));
                        }
                        if (ruleType == ForbiddenRuleDO.STATUS_SECURITY) {
                            int num = (int) row.getCell(3).getNumericCellValue();
                            if (num <= 0) {
                                throw new BusinessException("12000", "文件转换第" + (i+1) + "行出错, 保底数量需大于0");
                            }
                            iVo.setNum(num);
                        }
                        iVos.add(iVo);
                    } catch (BusinessException bz) {
                        throw new BusinessException(bz.getCode(), bz.getMessage());
                    } catch (Exception e) {
                        LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|文件转换第{0}行出错",i+1);
                        throw new BusinessException("12000", "文件转换第" + (i+1) + "行出错, 请检查文件");
                    }
                }
                return iVos;
            } catch (BusinessException be) {
                throw new BusinessException(be.getCode(), be.getMessage());
            }  catch (Exception e) {
                LoggerUtil.error(e, logger,"[UPLOAD_AND_SAVE_SINGLE_ITEM_DATA2]|文件转换出错:{0}",file);
                throw new BusinessException("12000", "文件转换出错, 请检查文件");
            }
        }
        return new ArrayList<>();
    }

    public Map<String, List<ImportRuleFileVo>> getTypeMap(List<ImportRuleFileVo> iVos) {
        Map<String, List<ImportRuleFileVo>> typeMap = new HashMap<>(5);
        List<ImportRuleFileVo> bigVos = new ArrayList<>();
        List<ImportRuleFileVo> midVos = new ArrayList<>();
        List<ImportRuleFileVo> smallVos = new ArrayList<>();
        List<ImportRuleFileVo> skcVos = new ArrayList<>();
        List<ImportRuleFileVo> skuVos = new ArrayList<>();
        for (ImportRuleFileVo iVo : iVos) {
            String type = iVo.getType();
            if (RULE_FILE_TYPE_LARGE_CATEGORY.equals(iVo.getType())) {
                bigVos.add(iVo);
            } else if (RULE_FILE_TYPE_MID_CATEGORY.equals(type)) {
                midVos.add(iVo);
            } else if (RULE_FILE_TYPE_SMALL_CATEGORY.equals(type)) {
                smallVos.add(iVo);
            } else if (RULE_FILE_TYPE_SKC.equals(type) || RULE_FILE_TYPE_SKC_UPPER.equals(type)){
                skcVos.add(iVo);
            } else if (RULE_FILE_TYPE_SKU.equals(type) || RULE_FILE_TYPE_SKU_UPPER.equals(type)) {
                skuVos.add(iVo);
            }
        }
        typeMap.put(RULE_FILE_TYPE_LARGE_CATEGORY, bigVos);
        typeMap.put(RULE_FILE_TYPE_MID_CATEGORY, midVos);
        typeMap.put(RULE_FILE_TYPE_SMALL_CATEGORY, smallVos);
        typeMap.put(RULE_FILE_TYPE_SKC, skcVos);
        typeMap.put(RULE_FILE_TYPE_SKU, skuVos);
        return typeMap;
    }

    private void checkFileContent(String type, String obj, int rowNum) {
        if (RULE_FILE_TYPE_LARGE_CATEGORY.equals(type)) {
            for (String objVal : obj.split(ATTR_VALUE_SPLIT)) {
                if (!getCategorySet().contains(objVal)) {
                    throw new BusinessException("12000", "文件转换第" + (rowNum+1) + "行, 大类类型对象内容出错, 请检查文件");
                }
            }
        } else if (RULE_FILE_TYPE_MID_CATEGORY.equals(type)) {
            for (String objVal : obj.split(ATTR_VALUE_SPLIT)) {
                if (!getMidCategorySet().contains(objVal)) {
                    throw new BusinessException("12000", "文件转换第" + (rowNum+1) + "行, 中类类型对象内容出错, 请检查文件");
                }
            }
        } else if (RULE_FILE_TYPE_SMALL_CATEGORY.equals(type)) {
            for (String objVal : obj.split(ATTR_VALUE_SPLIT)) {
                if (!getSmallCategorySet().contains(objVal)) {
                    throw new BusinessException("12000", "文件转换第" + (rowNum+1) + "行, 小类类型对象内容出错, 请检查文件");
                }
            }
        } else if (!(RULE_FILE_TYPE_SKC.equals(type) || RULE_FILE_TYPE_SKU.equals(type))){
//            if (getSmallCategorySet().contains(obj)) {
                throw new BusinessException("12000", "文件转换第" + (rowNum+1) + "行, 类型不匹配, 请检查文件");
//            }
        }
    }

    Map<String, Integer> getSkcMap(String bigCategoryApply, String midCategoryApply, String smallCategoryApply, int num) {
        List<String> skcsCategoryApply = new ArrayList<>();
        Map<String, Object> paramMapSkc = new HashMap<>(3);
        if (!StringUtils.isEmpty(bigCategoryApply)) {
            paramMapSkc.put("bigCategoryNames", bigCategoryApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(midCategoryApply)) {
            paramMapSkc.put("midCategoryNames", midCategoryApply.split(ATTR_VALUE_SPLIT));
        }
        if (!StringUtils.isEmpty(smallCategoryApply)) {
            paramMapSkc.put("smallCategoryNames", smallCategoryApply.split(ATTR_VALUE_SPLIT));
        }
        if (paramMapSkc.size() > 0) {
            skcsCategoryApply = forbiddenRuleDOMapper.getSkcByCategory(paramMapSkc);
        }

        Map<String, Integer> map = new HashMap<>();
        for (String str : skcsCategoryApply) {
            map.put(str, num);
        }
        return map;
    }

}
