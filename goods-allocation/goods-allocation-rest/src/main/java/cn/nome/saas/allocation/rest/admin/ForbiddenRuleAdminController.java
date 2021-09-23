package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.feign.model.User;
import cn.nome.saas.allocation.model.rule.*;
import cn.nome.saas.allocation.repository.entity.allocation.ForbiddenRuleDO;
import cn.nome.saas.allocation.service.rule.ForbiddenRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Set;
import java.util.Date;

/**
 * ForbiddenRuleInternalController
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/allocation/forbidden/rule")
public class ForbiddenRuleAdminController {

    private static Logger LOGGER = LoggerFactory.getLogger(ForbiddenRuleAdminController.class);

    @Autowired
    ForbiddenRuleService forbiddenRuleService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(@RequestParam(value = "ruleName",required = false) String ruleName,
                           @RequestParam(value = "operator",required = false) String operator,
                           @RequestParam(value = "ruleType",required = false,defaultValue = "0") int ruleType,
                           @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                           @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize
                       ) {

        ForbiddenRuleResult result = forbiddenRuleService.selectForbiddenRuleByParam(ruleName,operator,page,pageSize, ruleType, null);

        return ResultUtil.handleSuccessReturn(result);
    }

    @RequestMapping(value = "/getSingleList", method = RequestMethod.GET)
    public Result list(@RequestParam(value = "shopName") String shopName,
                           @RequestParam(value = "largeCategory",required = false) String largeCategory,
                           @RequestParam(value = "midCategory",required = false) String midCategory,
                           @RequestParam(value = "smallCategory",required = false) String smallCategory,
                           @RequestParam(value = "typeValue",required = false) String typeValue,
                           @RequestParam(value = "userName",required = false) String userName,
                           @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                           @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize
                       ) {
        ForbiddenSingleRuleResult result = forbiddenRuleService.selectForbiddenSingleRuleByParam(shopName, largeCategory, midCategory, smallCategory, typeValue, userName, page, pageSize);

        return ResultUtil.handleSuccessReturn(result);
    }

    //门店信息导出
    @GetMapping(value = "/exportSingleList")
    public String exportForbiddenSingleList(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "shopName") String shopName,
                                @RequestParam(value = "largeCategory",required = false) String largeCategory,
                                @RequestParam(value = "midCategory",required = false) String midCategory,
                                @RequestParam(value = "smallCategory",required = false) String smallCategory,
                                @RequestParam(value = "typeValue",required = false) String typeValue,
                                @RequestParam(value = "userName",required = false) String userName) throws Exception {

        forbiddenRuleService.exportSingleList(shopName, largeCategory, midCategory, smallCategory, typeValue, userName, request, response);
        return "886";
    }

    @RequestMapping(value = "/del/detail", method = RequestMethod.POST)
    public Result delDetail(@RequestParam("file") MultipartFile file){
        try {
            return ResultUtil.handleSuccessReturn(forbiddenRuleService.delDetail(file));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[DEL_DETAIL] catch exception");
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[DEL_DETAIL] catch exception");
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/upload/detail", method = RequestMethod.POST)
    public Result uploadDetail(@RequestParam(value = "ruleName",required = false) String ruleName,
                               @RequestParam(value = "ruleId",required = false) Integer ruleId,
                               @RequestParam("file") MultipartFile file){

        ForbiddenRuleDetailResult result = forbiddenRuleService.uploadAndSaveDetail(ruleName,ruleId,file);

        return ResultUtil.handleSuccessReturn(result);
    }

    @RequestMapping(value = "/single/update", method = RequestMethod.POST)
    public Result singleUpdate(@RequestParam(value = "singleRuleId",required = false) Integer singleRuleId,
                               @RequestParam(value = "type",required = false) Integer type,
                               @RequestParam(value = "typeValue",required = false) String typeValue,
                               @RequestParam(value = "remark",required = false) String remark,
                               @RequestParam(value = "startDate",required = false) String startDate,
                               @RequestParam(value = "endDate",required = false) String endDate){

        forbiddenRuleService.singleUpdate(singleRuleId, type, typeValue, remark, startDate, endDate);

        return ResultUtil.handleSuccessReturn(1);
    }
    @RequestMapping(value = "/single/del", method = RequestMethod.POST)
    public Result singleDel(@RequestParam(value = "singleRuleId",required = false) Integer singleRuleId){
        forbiddenRuleService.singleDel(singleRuleId);
        return ResultUtil.handleSuccessReturn(1);
    }


    @RequestMapping(value = "/export/detail")
    public void exportDetail(@RequestParam(value = "ruleId",required = false) Integer ruleId,
                               HttpServletRequest request, HttpServletResponse response) {

        ForbiddenRuleDetailParam param = new ForbiddenRuleDetailParam();
        param.setRuleId(ruleId);
        param.setPageSize(Integer.MAX_VALUE);

        forbiddenRuleService.downloadForbiddenRuleDetail(param,request,response);

    }

    @RequestMapping(value = "/get/detail")
    public Result list(@RequestParam(value = "ruleId",required = false) Integer ruleId,
                       @RequestParam(value = "regionCode",required = false) String regionCode,
                       @RequestParam(value = "regionLevel",required = false,defaultValue = "0") Integer regionLevel,
                       @RequestParam(value = "categoryCode",required = false) String categoryCode,
                       @RequestParam(value = "categoryLevel",required = false,defaultValue = "0") Integer categoryLevel,
                       @RequestParam(value = "ruleName",required = false) String ruleName,
                       @RequestParam(value = "modifiedBy",required = false) String modifiedBy,
                       @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                       @RequestParam(value = "pageSize",required = false,defaultValue = "500") int pageSize){

        ForbiddenRuleDetailParam param = new ForbiddenRuleDetailParam();

        param.setRuleId(ruleId);
        param.setRegionCode(regionCode);
        param.setRegionLevel(regionLevel);
        param.setCategoryCode(categoryCode);
        param.setCategoryLevel(categoryLevel);
        param.setQueryName(ruleName);
        param.setModifiedBy(modifiedBy);
        param.setPage(page);
        param.setPageSize(pageSize);

        ForbiddenRuleDetailResult result = forbiddenRuleService.queryForbiddenRuleDetail(param);

        return ResultUtil.handleSuccessReturn(result);
    }

    @RequestMapping(value = "/get/region", method = RequestMethod.GET)
    public Result getRegion(@RequestParam(value = "level",required = false,defaultValue = "1") int level,
                            @RequestParam(value = "code",required = false) String code) {

        return ResultUtil.handleSuccessReturn(forbiddenRuleService.getAreaLatitudeTreeByParam(level,code));
    }

    @RequestMapping(value = "/get/category", method = RequestMethod.GET)
    public Result getCategory(@RequestParam(value = "level",required = false,defaultValue = "1") int level,
                              @RequestParam(value = "code",required = false) String code) {

        return ResultUtil.handleSuccessReturn(forbiddenRuleService.getGoodsLatitudeTreeByParam(level,code));
    }

    @RequestMapping(value = "/get/user", method = RequestMethod.GET)
    public Result getUser(@RequestParam(value = "type",required = false,defaultValue = "1") int type) {
        if (type == 1) {
            return ResultUtil.handleSuccessReturn(forbiddenRuleService.selectOperatorList());
        } else if (type == 2) {
            return ResultUtil.handleSuccessReturn(forbiddenRuleService.selectModifiedByList());
        }

         return null;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result deleteRule(@RequestParam(value = "ruleId",required = false) Integer ruleId) {

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

    @RequestMapping(value = "/getDetail2", method = RequestMethod.GET)
    public Result getDetail2(@RequestParam(value = "ruleId") Integer ruleId){
        ForbiddenGlobalItem forbiddenGlobalItem = forbiddenRuleService.getGlobalDetail2(ruleId);
        if (forbiddenGlobalItem == null) {
            throw new BusinessException("12000", "查无数据");
        }
        return ResultUtil.handleSuccessReturn(forbiddenGlobalItem);
    }

    @RequestMapping(value = "/upload2/detail", method = RequestMethod.POST)
    public Result upload2Detail(@RequestParam(value = "ruleName",required = false) String ruleName,
                                @RequestParam(value = "ruleId",required = false) Integer ruleId,
                                @RequestParam(value = "fileSkc",required = false) MultipartFile fileSkc,
                                @RequestParam(value = "fileSku",required = false) MultipartFile fileSku,
                                @RequestParam(value = "regionInclude",required = false) String regionInclude,
                                @RequestParam(value = "provinceInclude",required = false) String provinceInclude,
                                @RequestParam(value = "cityInclude",required = false) String cityInclude,
                                @RequestParam(value = "saleLvInclude",required = false) String saleLvInclude,
                                @RequestParam(value = "displayLvInclude",required = false) String displayLvInclude,
                                @RequestParam(value = "shopInclude",required = false) String shopInclude,
                                @RequestParam(value = "largeInclude",required = false) String largeInclude,
                                @RequestParam(value = "middleInclude",required = false) String middleInclude ,
                                @RequestParam(value = "smallInclude",required = false) String smallInclude,
                                @RequestParam(value = "regionExclude",required = false) String regionExclude,
                                @RequestParam(value = "provinceExclude",required = false) String provinceExclude,
                                @RequestParam(value = "cityExclude",required = false) String cityExclude,
                                @RequestParam(value = "saleLvExclude",required = false) String saleLvExclude,
                                @RequestParam(value = "displayLvExclude",required = false) String displayLvExclude,
                                @RequestParam(value = "shopExclude",required = false) String shopExclude,
                                @RequestParam(value = "largeExclude",required = false) String largeExclude,
                                @RequestParam(value = "middleExclude",required = false) String middleExclude,
                                @RequestParam(value = "smallExclude",required = false) String smallExclude,
                                @RequestParam(value = "skcExclude",required = false) String skcExclude,
                                @RequestParam(value = "skuExclude",required = false) String skuExclude,
                                @RequestParam(value = "startDate",required = false) String startDate,
                                @RequestParam(value = "endDate",required = false) String endDate,
                                @RequestParam(value = "updateUser",required = false) String updateUser) throws ParseException {
        try {
            LoggerUtil.info(LOGGER, "[UPLOAD2_DETAIL] param ruleName:{0},ruleId:{1}, fileSkc:{2}, fileSku:{3}," +
                            "regionInclude:{4},  provinceInclude:{5},  cityInclude:{6},  saleLvInclude:{7},  displayLvInclude:{8}," +
                            "shopInclude:{9},  largeInclude:{10},  middleInclude:{11} ,  smallInclude:{12}," +
                            "regionExclude:{13},  provinceExclude:{14},  cityExclude:{15},  saleLvExclude:{16},  displayLvExclude:{17}," +
                            "shopExclude:{18},   largeExclude:{19},  middleExclude:{20},  smallExclude:{21}," +
                            "skcExclude:{22},   skuExclude:{23}," +
                            "startDate:{24}, endDate:{25}, updateUser:{26}",
                    ruleName,  ruleId, fileSkc, fileSku,
                    regionInclude,  provinceInclude,  cityInclude,  saleLvInclude,  displayLvInclude,
                    shopInclude,  largeInclude,  middleInclude ,  smallInclude,
                    regionExclude,  provinceExclude,  cityExclude,  saleLvExclude,  displayLvExclude,
                    shopExclude,   largeExclude,  middleExclude,  smallExclude,
                    skcExclude,   skuExclude ,
                    startDate, endDate, updateUser);
//            ForbiddenRuleDO forbiddenRule = forbiddenRuleService.uploadAndSaveSignleItemData2(ruleName,  ruleId, fileSkc, fileSku,
//                    regionInclude,  provinceInclude,  cityInclude,  saleLvInclude,  displayLvInclude, shopInclude,
//                    regionExclude,  provinceExclude,  cityExclude,  saleLvExclude,  displayLvExclude, shopExclude,
//                    startDate, endDate, updateUser);
            ForbiddenRuleDO forbiddenRule = null;
            return ResultUtil.handleSuccessReturn(forbiddenRule);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[UPLOAD2_DETAIL] catch exception UserActivityForm:{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[UPLOAD2_DETAIL] catch exception UserActivityForm:{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/getRole", method = RequestMethod.GET)
    public Result getRole(){
        User user = forbiddenRuleService.getRole();
        return ResultUtil.handleSuccessReturn(user);
    }
}
