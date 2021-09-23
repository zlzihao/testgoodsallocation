package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.cache.GoodsCategoryTreeCache;
import cn.nome.saas.allocation.model.allocation.ImportRuleFileVo;
import cn.nome.saas.allocation.model.rule.ForbiddenGlobalItem;
import cn.nome.saas.allocation.model.rule.ForbiddenRuleResult;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.service.rule.ForbiddenRuleService;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * ForbiddenRuleInternalController
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/allocation/globalConfigRule")
public class GlobalConfigRuleController {

    private static Logger LOGGER = LoggerFactory.getLogger(GlobalConfigRuleController.class);

    @Autowired
    GlobalConfigRuleService globalConfigRuleService;
    @Autowired
    ForbiddenRuleService forbiddenRuleService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(@RequestParam(value = "ruleName",required = false) String ruleName,
                       @RequestParam(value = "operator",required = false) String operator,
                       @RequestParam(value = "ruleType",required = false,defaultValue = "0") int ruleType,
                       @RequestParam(value = "type",required = false) Integer type,
                       @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                       @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize
    ) {

        ForbiddenRuleResult result = forbiddenRuleService.selectForbiddenRuleByParam(ruleName,operator,page,pageSize, ruleType, type);

        return ResultUtil.handleSuccessReturn(result);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result deleteRule(@RequestParam(value = "ruleId") Integer ruleId) {

        try {
            forbiddenRuleService.deleteForbiddenRuleAndDetail(ruleId);
            return ResultUtil.handleSuccessReturn();
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[DELETE] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[DELETE] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    public Result getDetail(@RequestParam(value = "ruleId",required = false) Integer ruleId){
        try {
            ForbiddenGlobalItem forbiddenGlobalItem = forbiddenRuleService.getGlobalDetail2(ruleId);
            if (forbiddenGlobalItem == null) {
                throw new BusinessException("12000", "查无数据");
            }
            return ResultUtil.handleSuccessReturn(forbiddenGlobalItem);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[GET_DETAIL] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[GET_DETAIL] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/uploadAndSave", method = RequestMethod.POST)
    public Result uploadAndSave(@RequestParam(value = "ruleType") Integer ruleType,
                                @RequestParam(value = "ruleName",required = false) String ruleName,
                                @RequestParam(value = "ruleId",required = false) Integer ruleId,
                                @RequestParam(value = "fileInclude",required = false) MultipartFile fileInclude,
                                @RequestParam(value = "fileInDelFlag",required = false, defaultValue = "0") int fileInDelFlag,
                                @RequestParam(value = "fileExclude",required = false) MultipartFile fileExclude,
                                @RequestParam(value = "fileExDelFlag",required = false, defaultValue = "0") int fileExDelFlag,
                                @RequestParam(value = "regionInclude",required = false) String regionInclude,
                                @RequestParam(value = "provinceInclude",required = false) String provinceInclude,
                                @RequestParam(value = "cityInclude",required = false) String cityInclude,
                                @RequestParam(value = "saleLvInclude",required = false) String saleLvInclude,
                                @RequestParam(value = "displayLvInclude",required = false) String displayLvInclude,
                                @RequestParam(value = "shopInclude",required = false) String shopInclude,
                                @RequestParam(value = "attrFirValInclude",required = false) String attrFirValInclude,
                                @RequestParam(value = "attrSecValInclude",required = false) String attrSecValInclude,
                                @RequestParam(value = "attrThiValInclude",required = false) String attrThiValInclude,
                                @RequestParam(value = "attrFourValInclude",required = false) String attrFourValInclude,
                                @RequestParam(value = "attrFifValInclude",required = false) String attrFifValInclude,
                                @RequestParam(value = "regionExclude",required = false) String regionExclude,
                                @RequestParam(value = "provinceExclude",required = false) String provinceExclude,
                                @RequestParam(value = "cityExclude",required = false) String cityExclude,
                                @RequestParam(value = "saleLvExclude",required = false) String saleLvExclude,
                                @RequestParam(value = "displayLvExclude",required = false) String displayLvExclude,
                                @RequestParam(value = "shopExclude",required = false) String shopExclude,
                                @RequestParam(value = "attrFirValExclude",required = false) String attrFirValExclude,
                                @RequestParam(value = "attrSecValExclude",required = false) String attrSecValExclude,
                                @RequestParam(value = "attrThiValExclude",required = false) String attrThiValExclude,
                                @RequestParam(value = "attrFourValExclude",required = false) String attrFourValExclude,
                                @RequestParam(value = "attrFifValExclude",required = false) String attrFifValExclude,
                                @RequestParam(value = "startDate",required = false) String startDate,
                                @RequestParam(value = "endDate",required = false) String endDate,
                                @RequestParam(value = "updateUser",required = false) String updateUser) {
        try {
            LoggerUtil.info(LOGGER, "[GLOBAL_CONFIG_UPLOAD_AND_SAVE]|param ruleName:{0},ruleId:{1}, fileInclude:{2}, fileExclude:{3}," +
                            "regionInclude:{4},  provinceInclude:{5},  cityInclude:{6},  saleLvInclude:{7},  displayLvInclude:{8}," +
                            "shopInclude:{9}, " +
                            "attrFirValInclude:{10}, attrSecValInclude:{11}, attrThiValInclude:{12}, attrFourValInclude:{13}, attrFifValInclude:{14}, " +
                            "regionExclude:{15},  provinceExclude:{16},  cityExclude:{17},  saleLvExclude:{18},  displayLvExclude:{19}," +
                            "shopExclude:{20}, " +
                            "attrFirValExclude:{21}, attrSecValExclude:{22}, attrThiValExclude:{23}, attrFourValExclude:{24}, attrFifValExclude:{25}, " +
                            "startDate:{26}, endDate:{27}, updateUser:{28}, ruleType:{29}, fileInDelFlag:{30}, fileExDelFlag:{31}",
                    ruleName,  ruleId, fileInclude, fileExclude,
                    regionInclude,  provinceInclude,  cityInclude,  saleLvInclude,  displayLvInclude,
                    shopInclude,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    regionExclude,  provinceExclude,  cityExclude,  saleLvExclude,  displayLvExclude,
                    shopExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser, ruleType, fileInDelFlag, fileExDelFlag);
            ForbiddenRuleDO forbiddenRuleDO = globalConfigRuleService.uploadAndSave(ruleType, ruleName,  ruleId, fileInclude, fileInDelFlag, fileExclude, fileExDelFlag,
                    regionInclude,  provinceInclude,  cityInclude,  saleLvInclude,  displayLvInclude, shopInclude,
                    attrFirValInclude, attrSecValInclude, attrThiValInclude, attrFourValInclude, attrFifValInclude,
                    regionExclude,  provinceExclude,  cityExclude,  saleLvExclude,  displayLvExclude, shopExclude,
                    attrFirValExclude, attrSecValExclude, attrThiValExclude, attrFourValExclude, attrFifValExclude,
                    startDate, endDate, updateUser);
            return ResultUtil.handleSuccessReturn(forbiddenRuleDO);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[UPLOAD2_DETAIL] catch exception UserActivityForm:{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[UPLOAD2_DETAIL] catch exception UserActivityForm:{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/analyseRuleFile", method = RequestMethod.POST)
    public Result analyseRuleFile(@RequestParam(value = "ruleFile") MultipartFile ruleFile, @RequestParam(value = "ruleType",required = true) Integer ruleType){
        try {
            List<ImportRuleFileVo> iVos = globalConfigRuleService.analyseRuleFile(ruleFile, ruleType);
    //        if (iVos == null) {
    //            throw new BusinessException("查无数据");
    //        }
            return ResultUtil.handleSuccessReturn(iVos);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[UPLOAD2_DETAIL] catch exception UserActivityForm:{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[UPLOAD2_DETAIL] catch exception UserActivityForm:{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }


    @RequestMapping(value = "/getRegionList", method = RequestMethod.GET)
    public Result getRegionList(){
        Set<String> regionSet = globalConfigRuleService.getRegionSet();
        if (regionSet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(regionSet);
    }
    @RequestMapping(value = "/getProvinceList", method = RequestMethod.GET)
    public Result getProvinceList(){
        Set<String> provinceSet = globalConfigRuleService.getProvinceSet();
        if (provinceSet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(provinceSet);
    }
    @RequestMapping(value = "/getCityList", method = RequestMethod.GET)
    public Result getCityList(){
        Set<String> citySet = globalConfigRuleService.getCitySet();
        if (citySet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(citySet);
    }
    @RequestMapping(value = "/getStoreList", method = RequestMethod.GET)
    public Result getStoreList(){
        Set<String> shopCodeSet = globalConfigRuleService.getShopNameMap().keySet();
        if (shopCodeSet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(shopCodeSet);
    }
    @RequestMapping(value = "/getStoreSaleLvList", method = RequestMethod.GET)
    public Result getStoreSaleLvList(){
        Set<String> saleLvSet = globalConfigRuleService.getSaleLvSet();
        if (saleLvSet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(saleLvSet);
    }
    @RequestMapping(value = "/getStoreDisplayLvList", method = RequestMethod.GET)
    public Result getStoreDisplayLvList(){
        Set<String> displayLvSet = globalConfigRuleService.getDisplayLvSet();
        if (displayLvSet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(displayLvSet);
    }
    @RequestMapping(value = "/getLargeCategoryList", method = RequestMethod.GET)
    public Result getLargeCategoryList(){
        Set<String> categorySet = globalConfigRuleService.getCategorySet();
        if (categorySet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(categorySet);
    }
    @RequestMapping(value = "/getMiddleCategoryList", method = RequestMethod.GET)
    public Result getMiddleCategoryList(){
        Set<String> midCategorySet = globalConfigRuleService.getMidCategorySet();
        if (midCategorySet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(midCategorySet);
    }
    @RequestMapping(value = "/getSmallCategoryList", method = RequestMethod.GET)
    public Result getSmallCategoryList(){
        Set<String> smallCategorySet = globalConfigRuleService.getSmallCategorySet();
        if (smallCategorySet == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(smallCategorySet);
    }

}
