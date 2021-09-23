package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.allocation.StoreInfoAttrValReq;
import cn.nome.saas.allocation.service.allocation.DisplayService;
import cn.nome.saas.allocation.service.allocation.ImportExportService;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ForbiddenRuleInternalController
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/allocation/display")
public class DisplayController {

    private static Logger LOGGER = LoggerFactory.getLogger(DisplayController.class);

    @Autowired
    DisplayService displayService;

    @Autowired
    ImportExportService importExportService;

    @Autowired
    ShopService shopService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    GlobalConfigRuleService globalConfigRuleService;


    //******************************************陈列类别*********************************************************************

    @RequestMapping(value = "/getLargeCategoryList", method = RequestMethod.GET)
    public Result getLargeCategoryList() {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getLargeCategoryList());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getLargeCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getLargeCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/getMidCategoryList", method = RequestMethod.GET)
    public Result getMidCategoryList(@RequestParam(value = "largeCategory",required = false) String largeCategory) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getMidCategoryList(largeCategory));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getMidCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getMidCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }
    @RequestMapping(value = "/getSmallCategoryList", method = RequestMethod.GET)
    public Result getSmallCategoryList(@RequestParam(value = "midCategory",required = false) String midCategory) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getSmallCategoryList(midCategory));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getSmallCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getSmallCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/selectByParam", method = RequestMethod.GET)
    public Result selectByParam(@RequestParam(value = "largeCategory",required = false) String largeCategory,
                                @RequestParam(value = "midCategory",required = false) String midCategory,
                                @RequestParam(value = "smallCategory",required = false) String smallCategory,
                                @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.selectByParam(largeCategory, midCategory, smallCategory, page, pageSize));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[selectByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[selectByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/exportByParam", method = RequestMethod.GET)
    public Result exportByParam(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "largeCategory",required = false) String largeCategory,
                                @RequestParam(value = "midCategory",required = false) String midCategory,
                                @RequestParam(value = "smallCategory",required = false) String smallCategory) {
        try {
            importExportService.exportDisplayByParam(largeCategory, midCategory, smallCategory, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[exportByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[exportByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    //******************************************商品陈列*********************************************************************
    
    @RequestMapping(value = "/goods/getLargeCategoryList", method = RequestMethod.GET)
    public Result getGoodsLargeCategoryList() {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getGoodsLargeCategoryList());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getGoodsLargeCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getGoodsLargeCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }
    @RequestMapping(value = "/goods/getMidCategoryList", method = RequestMethod.GET)
    public Result getGoodsMidCategoryList(@RequestParam(value = "largeCategory",required = false) String largeCategory) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getGoodsMidCategoryList(largeCategory));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getGoodsMidCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getGoodsMidCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }
    @RequestMapping(value = "/goods/getSmallCategoryList", method = RequestMethod.GET)
    public Result getGoodsSmallCategoryList(@RequestParam(value = "midCategory",required = false) String midCategory) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getGoodsSmallCategoryList(midCategory));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getGoodsSmallCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getGoodsSmallCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/goods/selectByParam", method = RequestMethod.GET)
    public Result goodsSelectByParam(@RequestParam(value = "largeCategory",required = false) String largeCategory,
                                @RequestParam(value = "midCategory",required = false) String midCategory,
                                @RequestParam(value = "smallCategory",required = false) String smallCategory,
                                @RequestParam(value = "matCode",required = false) String matCode,
                                @RequestParam(value = "matName",required = false) String matName,
                                @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize) {
        try {
            return ResultUtil.handleSuccessReturn(goodsService.selectByParam(largeCategory, midCategory, smallCategory, matCode, matName, page, pageSize));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[goodsSelectByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[goodsSelectByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/goods/exportByParam", method = RequestMethod.GET)
    public Result goodsExportByParam(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "largeCategory",required = false) String largeCategory,
                                @RequestParam(value = "midCategory",required = false) String midCategory,
                                @RequestParam(value = "smallCategory",required = false) String smallCategory,
                                @RequestParam(value = "matCode",required = false) String matCode,
                                @RequestParam(value = "matName",required = false) String matName) {
        try {
            importExportService.exportDisplayGoodsByParam(largeCategory, midCategory, smallCategory, matCode, matName, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[goodsExportByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[goodsExportByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    //******************************************门店信息配置*********************************************************************

    @RequestMapping(value = "/storeInfo/getShopNameList", method = RequestMethod.GET)
    public Result storeInfoGetShopNameList() {
        try {
            return ResultUtil.handleSuccessReturn(globalConfigRuleService.getShopNameMap().keySet());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoGetShopNameList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoGetShopNameList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/getGoodsAreaList", method = RequestMethod.GET)
    public Result storeInfoGetGoodsAreaList() {
        try {
            return ResultUtil.handleSuccessReturn(shopService.getGoodsAreaList());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoGetGoodsAreaList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoGetGoodsAreaList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/getShopLvList", method = RequestMethod.GET)
    public Result storeInfoGetShopLvList() {
        try {
            return ResultUtil.handleSuccessReturn(shopService.getShopLvList());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoGetShopLvList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoGetShopLvList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/selectByParam", method = RequestMethod.GET)
    public Result storeInfoSelectByParam(@RequestParam(value = "goodsAreas",required = false) String goodsAreas,
                                     @RequestParam(value = "shopLvs",required = false) String shopLvs,
                                     @RequestParam(value = "shopNames",required = false) String shopNames,
                                     @RequestParam(value = "shopCode",required = false) String shopCode,
                                     @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                     @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize) {
        try {
            return ResultUtil.handleSuccessReturn(shopService.selectByParam(goodsAreas, shopLvs, shopNames, shopCode, page, pageSize));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoSelectByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoSelectByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/exportByParam", method = RequestMethod.GET)
    public Result storeInfoExportByParam(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "goodsAreas",required = false) String goodsAreas,
                                     @RequestParam(value = "shopLvs",required = false) String shopLvs,
                                     @RequestParam(value = "shopNames",required = false) String shopNames,
                                     @RequestParam(value = "shopCode",required = false) String shopCode) {
        try {
            importExportService.exportShopInfoByParam(goodsAreas, shopLvs, shopNames, shopCode, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoExportByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoExportByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/getAttrKeys", method = RequestMethod.GET)
    public Result getAttrKeys() {
        try {
            return ResultUtil.handleSuccessReturn(shopService.getShopInfoAttrKeys());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getAttrKeys] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getAttrKeys] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/updateAttrKeys", method = RequestMethod.POST)
    @ResponseBody
    public Result updateAttrKeys(@RequestBody StoreInfoAttrValReq storeInfoAttrValReq) {
        try {
            shopService.updateShopInfoAttrKeys(storeInfoAttrValReq.getAttrKey1(), storeInfoAttrValReq.getAttrKey2(), storeInfoAttrValReq.getAttrKey3(), storeInfoAttrValReq.getAttrKey4(), storeInfoAttrValReq.getAttrKey5());
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[updateAttrKeys] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[updateAttrKeys] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/add", method = RequestMethod.POST)
    public Result storeInfoAdd(
                                 @RequestParam(value = "shopCode") String shopCode,
                                 @RequestParam(value = "goodsArea") String goodsArea,
                                 @RequestParam(value = "userName") String userName,
                                 @RequestParam(value = "issueTime") String issueTime,
                                 @RequestParam(value = "shopLevel") String shopLevel,
                                 @RequestParam(value = "maxDays", required = false, defaultValue = "0") Integer maxDays,
                                 @RequestParam(value = "haveChild") Integer haveChild,
                                 @RequestParam(value = "commodityLevel") String commodityLevel,
                                 @RequestParam(value = "womenLevel") String womenLevel,
                                 @RequestParam(value = "menLevel") String menLevel,
                                 @RequestParam(value = "roadDay") Integer roadDay,
                                 @RequestParam(value = "safeDay",required = false,defaultValue = "0") Integer safeDay,
                                 @RequestParam(value = "status") Integer status,
                                 @RequestParam(value = "commoditySpace") String commoditySpace,
                                 @RequestParam(value = "clothSpace") String clothSpace,
                                 @RequestParam(value = "sheetHeadSpaceNum",required = false) String sheetHeadSpaceNum,
                                 @RequestParam(value = "cosmeticsTable") Integer cosmeticsTable,
                                 @RequestParam(value = "stationeryTable") Integer stationeryTable,
//                                 @RequestParam(value = "singleDisplayChild") Integer singleDisplayChild,
                                 @RequestParam(value = "attrFirVal",required = false) String attrFirVal,
                                 @RequestParam(value = "attrSecVal",required = false) String attrSecVal,
                                 @RequestParam(value = "attrThiVal",required = false) String attrThiVal,
                                 @RequestParam(value = "attrFourVal",required = false) String attrFourVal,
                                 @RequestParam(value = "attrFifVal",required = false) String attrFifVal) {
        try {
            if (status < 1 || status > 8) {
                throw new BusinessException("12000", "店铺状态值不在限定值内");
            }
            shopService.
                    shopInfoAdd(shopCode, goodsArea, userName, issueTime,
                                        shopLevel, maxDays, haveChild, commodityLevel, womenLevel, menLevel,
                                        roadDay, status,
                                        commoditySpace, clothSpace, sheetHeadSpaceNum, cosmeticsTable, stationeryTable,
//                                        singleDisplayChild,
                                        attrFirVal, attrSecVal, attrThiVal, attrFourVal, attrFifVal,safeDay);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getAttrKeys] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getAttrKeys] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/storeInfo/update", method = RequestMethod.POST)
    public Result storeInfoUpdate(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "shopCode") String shopCode,
            @RequestParam(value = "goodsArea") String goodsArea,
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "issueTime") String issueTime,
            @RequestParam(value = "shopLevel") String shopLevel,
            @RequestParam(value = "maxDays") Integer maxDays,
            @RequestParam(value = "haveChild") Integer haveChild,
            @RequestParam(value = "commodityLevel") String commodityLevel,
            @RequestParam(value = "womenLevel") String womenLevel,
            @RequestParam(value = "menLevel") String menLevel,
            @RequestParam(value = "roadDay") Integer roadDay,
            @RequestParam(value = "safeDay",required = false,defaultValue = "0") Integer safeDay,
            @RequestParam(value = "status") Integer status,
            @RequestParam(value = "commoditySpace") String commoditySpace,
            @RequestParam(value = "clothSpace") String clothSpace,
            @RequestParam(value = "sheetHeadSpaceNum",required = false) String sheetHeadSpaceNum,
            @RequestParam(value = "cosmeticsTable") Integer cosmeticsTable,
            @RequestParam(value = "stationeryTable") Integer stationeryTable,
//            @RequestParam(value = "singleDisplayChild") Integer singleDisplayChild,
            @RequestParam(value = "attrFirVal",required = false) String attrFirVal,
            @RequestParam(value = "attrSecVal",required = false) String attrSecVal,
            @RequestParam(value = "attrThiVal",required = false) String attrThiVal,
            @RequestParam(value = "attrFourVal",required = false) String attrFourVal,
            @RequestParam(value = "attrFifVal",required = false) String attrFifVal) {
        try {
            if (status < 1 || status > 8) {
                throw new BusinessException("12000", "店铺状态值不在限定值内");
            }

            shopService.shopInfoUpdate(id, shopCode, goodsArea, userName, issueTime,
                    shopLevel, maxDays, haveChild, commodityLevel, womenLevel, menLevel,
                    roadDay, status,
                    commoditySpace, clothSpace, sheetHeadSpaceNum, cosmeticsTable, stationeryTable,
//                    singleDisplayChild,
                    attrFirVal, attrSecVal, attrThiVal, attrFourVal, attrFifVal,safeDay);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoUpdate] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[storeInfoUpdate] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }


    //******************************************门店陈列配置*********************************************************************

    @RequestMapping(value = "/shop/getLargeCategoryList", method = RequestMethod.GET)
    public Result getShopLargeCategoryList() {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getShopDisplayLargeCategoryList());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getShopLargeCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getShopLargeCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }
    @RequestMapping(value = "/shop/getMidCategoryList", method = RequestMethod.GET)
    public Result getShopMidCategoryList(@RequestParam(value = "largeCategory",required = false) String largeCategory) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.getShopDisplayMidCategoryList(largeCategory));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[getShopMidCategoryList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[getShopMidCategoryList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }
    @RequestMapping(value = "/shop/getShopNameList", method = RequestMethod.GET)
    public Result shopGetShopNameList() {
        try {
            return ResultUtil.handleSuccessReturn(globalConfigRuleService.getShopNameMap().keySet());
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[shopGetShopNameList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[shopGetShopNameList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/shop/selectByParam", method = RequestMethod.GET)
    public Result shopSelectByParam(@RequestParam(value = "largeCategory",required = false) String largeCategory,
                                         @RequestParam(value = "midCategory",required = false) String midCategory,
                                         @RequestParam(value = "shopNames",required = false) String shopNames,
                                         @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                         @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.shopSelectByParam(largeCategory, midCategory, shopNames, page, pageSize));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[shopSelectByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[shopSelectByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/shop/exportByParam", method = RequestMethod.GET)
    public Result shopExportByParam(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(value = "largeCategory",required = false) String largeCategory,
                                         @RequestParam(value = "midCategory",required = false) String midCategory,
                                         @RequestParam(value = "shopNames",required = false) String shopNames) {
        try {
            importExportService.exportShopDisplayByParam(largeCategory, midCategory, shopNames, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[shopExportByParam] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[shopExportByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "shop/check", method = RequestMethod.GET)
    public Result checkShopSheetSpace() {
        try {
            displayService.checkShopSheetSpace();
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.info(e, LOGGER, "[checkShopSheetSpace] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[checkShopSheetSpace] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }




}
