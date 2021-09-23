package cn.nome.saas.allocation.service.allocation;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.DateUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.rule.ForbiddenGlobalItem;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenGlobalItemDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenRuleDOMapper;
import cn.nome.saas.allocation.repository.dao.allocation.ForbiddenSingleItemDOMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengdewu@nome.com
 */
@SuppressWarnings("Duplicates")
@Service
public class RefreshForbiddenService {

    private final static int recentlyDays = -2;
    private final static String dateFormat = "yyyy-MM-dd";
    private final static String dateFormatSimple = "yyyyMMdd";
    private final static String updateUser = "RefreshUser";
    private final static Logger logger = LoggerFactory.getLogger(RefreshForbiddenService.class);

    private final ForbiddenRuleDOMapper forbiddenRuleDOMapper;
    private final ForbiddenSingleItemDOMapper forbiddenSingleItemDOMapper;
    private final ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper;
    private final GlobalConfigRuleService globalConfigRuleService;

    @Autowired
    public RefreshForbiddenService(ForbiddenRuleDOMapper forbiddenRuleDOMapper,
                                   ForbiddenSingleItemDOMapper forbiddenSingleItemDOMapper,
                                   ForbiddenGlobalItemDOMapper forbiddenGlobalItemDOMapper,
                                   GlobalConfigRuleService globalConfigRuleService) {
        this.forbiddenRuleDOMapper = forbiddenRuleDOMapper;
        this.forbiddenSingleItemDOMapper = forbiddenSingleItemDOMapper;
        this.forbiddenGlobalItemDOMapper = forbiddenGlobalItemDOMapper;
        this.globalConfigRuleService = globalConfigRuleService;
    }

    public void refreshForbiddenAndSecurityAndWhiteList(int type) {

        Set<Integer> refreshTypeSet = new HashSet<>();
        if (type == -1) {
            // 刷新全部
            refreshTypeSet.add(ForbiddenRuleDO.STATUS_FORBIDDEN);
            refreshTypeSet.add(ForbiddenRuleDO.STATUS_WHITE_LIST);
            refreshTypeSet.add(ForbiddenRuleDO.STATUS_SECURITY_AND_WHITE_LIST);
            refreshTypeSet.add(ForbiddenRuleDO.STATUS_SECURITY);
        } else {
            refreshTypeSet.add(type);
        }

        if (refreshTypeSet.contains(ForbiddenRuleDO.STATUS_FORBIDDEN)) {
            // 全局禁配
            refreshGlobalSingleRule(ForbiddenRuleDO.STATUS_FORBIDDEN);
            // 单店禁配 TODO 暂时不支持
            refreshShopSingleRule();
        }

        if (refreshTypeSet.contains(ForbiddenRuleDO.STATUS_WHITE_LIST)) {
            refreshGlobalSingleRule(ForbiddenRuleDO.STATUS_WHITE_LIST);
        }

        if (refreshTypeSet.contains(ForbiddenRuleDO.STATUS_SECURITY_AND_WHITE_LIST)) {
            refreshGlobalSingleRule(ForbiddenRuleDO.STATUS_SECURITY_AND_WHITE_LIST);
        }

        if (refreshTypeSet.contains(ForbiddenRuleDO.STATUS_SECURITY)) {
            refreshGlobalSingleRule(ForbiddenRuleDO.STATUS_SECURITY);
        }
    }

    private void refreshShopSingleRule() {
        logger.info("[R_S_S_R], refreshShopSingleRule, start");

        logger.info("[R_S_S_R], refreshShopSingleRule, end");
    }

    private void refreshGlobalSingleRule(int type) {
        logger.info("[R_F_G_S_W], refreshGlobalSingleRule, start, type: {}", type);

        Map<String,Object> param = new HashMap<>();

        // 傻逼用法
        param.put("type", 1);
        param.put("status", type);

        List<ForbiddenGlobalItem> globalItemList = forbiddenGlobalItemDOMapper.selectBySelective(param);
        logger.info("[R_F_G_S_W], refreshGlobalSingleRule, get items, size: {}", globalItemList.size());

        int successCount = 0;
        for (ForbiddenGlobalItem globalItem : globalItemList) {
            logger.info("[R_F_G_S_W], refreshGlobalSingleRule, get one item, fRuleId: {}, name: {}", globalItem.getfRuleId(), globalItem.getRuleName());
            try {
                globalConfigRuleService.uploadAndSave(
                    type,
                    globalItem.getRuleName(),
                    Integer.parseInt(globalItem.getfRuleId()),
                    null,
                    -1,
                    null,
                    -1,
                    globalItem.getRegionInclude(),
                    globalItem.getProvinceInclude(),
                    globalItem.getCityInclude(),
                    globalItem.getSaleLvInclude(),
                    globalItem.getDisplayLvInclude(),
                    globalItem.getShopInclude(),
                    globalItem.getAttrFirValInclude(),
                    globalItem.getAttrSecValInclude(),
                    globalItem.getAttrThiValInclude(),
                    globalItem.getAttrFourValInclude(),
                    globalItem.getAttrFifValInclude(),
                    globalItem.getRegionExclude(),
                    globalItem.getProvinceExclude(),
                    globalItem.getCityExclude(),
                    globalItem.getSaleLvExclude(),
                    globalItem.getDisplayLvExclude(),
                    globalItem.getShopExclude(),
                    globalItem.getAttrFirValExclude(),
                    globalItem.getAttrSecValExclude(),
                    globalItem.getAttrThiValExclude(),
                    globalItem.getAttrFourValExclude(),
                    globalItem.getAttrFifValExclude(),
                    DateUtil.format(globalItem.getStartDate(),"yyyyMMdd"),
                    DateUtil.format(globalItem.getEndDate(),"yyyyMMdd"),
                    "admin");
                successCount ++;
            } catch (BusinessException e) {
                LoggerUtil.info(e, logger, "[R_F_G_S_W], refreshGlobalSingleRule, one item exception, fRuleId: {0}, name: {1}", globalItem.getfRuleId(), globalItem.getRuleName());
            }
        }

        logger.info("[R_F_G_S_W], refreshGlobalSingleRule, end, type: {}, itemSize: {}, successCount: {}", type, globalItemList.size(), successCount);
    }

    public void autoRefreshRules() {
        /**
         * 1. 限定了type和status都是1，
         *    其中ForbiddenRuleType = 1是全局
         *    ForbiddenRuleStatus = 1是禁止
         * 2. 在forbiddenSingleItem里的都是已完成刷新的
         */
        List<Integer> ids = getUnFinishedSettingRuleIds();
        if (!CollectionUtils.isEmpty(ids)) {
            logger.info("[RefreshForbiddenService] autoRefreshRules unfinished setting rule ids = {}", JSON.toJSONString(ids));
            ids.forEach(this::refreshRuleSilent);
        }


        ids = getRecentlyChangedRuleIds();
        if (!CollectionUtils.isEmpty(ids)) {
            logger.info("[RefreshForbiddenService] autoRefreshRules recently changed rule ids = {}", JSON.toJSONString(ids));
            ids.forEach(this::refreshRuleSilent);
            return;
        }
        logger.info("[RefreshForbiddenService] autoRefreshRules no rule ids need to refresh");
    }

    public void refreshRuleSilent(Integer ruleId) {
        try {
            refreshRule(ruleId);
        } catch (Exception e) {
            LoggerUtil.error(e, logger, "[RefreshForbiddenService] [refreshRuleSilent] catch exception, ruleId: {0}", ruleId);
        }
    }

    public void refreshRule(Integer ruleId) {
        logger.info("[RefreshForbiddenService] refresh rule id = {}", ruleId);
        ForbiddenRuleDO fr = forbiddenRuleDOMapper.selectById(ruleId);
        ForbiddenGlobalItem fgi = forbiddenGlobalItemDOMapper.selectByFRuleId(ruleId);
        if (fr == null || fgi == null) {
            logger.warn("[RefreshForbiddenService] ForbiddenRuleDO or ForbiddenGlobalItem is null, rule id = {}", ruleId);
            return;
        }
        filterShops(fgi);
        logger.info("[RefreshForbiddenService] refresh fr = {}, fgi = {}", JSON.toJSONString(fr), JSON.toJSONString(fgi));
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatSimple);
        globalConfigRuleService.uploadAndSave(
                fr.getType(),
                fr.getName(),
                fr.getId(),
                null,
                0,
                null,
                0,
                fgi.getRegionInclude(),
                fgi.getProvinceInclude(),
                fgi.getCityInclude(),
                fgi.getSaleLvInclude(),
                fgi.getDisplayLvInclude(),
                fgi.getShopInclude(),
                fgi.getAttrFirValInclude(),
                fgi.getAttrSecValInclude(),
                fgi.getAttrThiValInclude(),
                fgi.getAttrFourValInclude(),
                fgi.getAttrFifValInclude(),
                fgi.getRegionExclude(),
                fgi.getProvinceExclude(),
                fgi.getCityExclude(),
                fgi.getSaleLvExclude(),
                fgi.getDisplayLvExclude(),
                fgi.getShopExclude(),
                fgi.getAttrFirValExclude(),
                fgi.getAttrSecValExclude(),
                fgi.getAttrThiValExclude(),
                fgi.getAttrFourValExclude(),
                fgi.getAttrFifValExclude(),
                sdf.format(fr.getStartDate()),
                sdf.format(fr.getEndDate()),
                updateUser
        );
    }

    private List<Integer> getValidRuleIds() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return forbiddenRuleDOMapper.getValidRuleIds(Constant.ForbiddenRuleType.Global, Constant.ForbiddenRuleStatus.Enable, sdf.format((new Date())));
    }

    private List<Integer> getFinishedSettingRuleIds(List<Integer> ruleIds) {
        return forbiddenSingleItemDOMapper.getFinishedSettingRuleIds(ruleIds);
    }

    private List<Integer> getUnFinishedSettingRuleIds() {
        List<Integer> allValidRuleIds = getValidRuleIds();
        logger.info("[RefreshForbiddenService] getValidRuleIds = {}", JSON.toJSONString(allValidRuleIds));
        List<Integer> finishedSettingRuleIds = getFinishedSettingRuleIds(allValidRuleIds);
        logger.info("[RefreshForbiddenService] getFinishedSettingRuleIds = {}", JSON.toJSONString(finishedSettingRuleIds));
        List<Integer> diffIds = allValidRuleIds.stream().filter(id -> !finishedSettingRuleIds.contains(id)).collect(Collectors.toList());
        logger.info("[RefreshForbiddenService] diffIds = {}", JSON.toJSONString(diffIds));
        return diffIds;
    }

    private List<Integer> getRecentlyChangedRuleIds() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, recentlyDays);
        return forbiddenRuleDOMapper.getRecentlyChangedRuleIds(Constant.ForbiddenRuleType.Global, Constant.ForbiddenRuleStatus.Enable, sdf.format((new Date())), sdf.format(calendar.getTime()));
    }

    private void filterShops(ForbiddenGlobalItem item) {
        Set<String> shopNameSet = globalConfigRuleService.getShopNameMap().keySet();
        if (shopNameSet.size() <= 0) {
            return;
        }
        if (!StringUtils.isEmpty(item.getShopExclude())) {
            List<String> names = new ArrayList<>();
            for (String shopName : item.getShopExclude().split(GlobalConfigRuleService.ATTR_VALUE_SPLIT)) {
                if (shopNameSet.contains(shopName)) {
                    names.add(shopName);
                } else {
                    logger.warn("[RefreshForbiddenService] [filterShops] shop exclude {} maybe already closed.", shopName);
                }
            }
            item.setShopExclude(String.join(GlobalConfigRuleService.ATTR_VALUE_SPLIT, names));
        }
        if (!StringUtils.isEmpty(item.getShopInclude())) {
            List<String> names = new ArrayList<>();
            for (String shopName : item.getShopInclude().split(GlobalConfigRuleService.ATTR_VALUE_SPLIT)) {
                if (shopNameSet.contains(shopName)) {
                    names.add(shopName);
                } else {
                    logger.warn("[RefreshForbiddenService] [filterShops] shop include {} maybe already closed.", shopName);
                }
            }
            item.setShopInclude(String.join(GlobalConfigRuleService.ATTR_VALUE_SPLIT, names));
        }
    }
}
