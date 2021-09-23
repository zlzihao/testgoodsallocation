package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.allocation.NewGoodsIssueRangeSaveReq;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.repository.entity.allocation.NewGoodsIssueRangeDO;
import cn.nome.saas.allocation.service.allocation.ImportExportService;
import cn.nome.saas.allocation.service.allocation.NewGoodsRangeService;
import cn.nome.saas.allocation.service.rule.ForbiddenRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ForbiddenRuleInternalController
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/allocation/newGoods")
public class NewGoodsRangeController {

    private static Logger LOGGER = LoggerFactory.getLogger(NewGoodsRangeController.class);

    @Autowired
    NewGoodsRangeService newGoodsRangeService;
    @Autowired
    ForbiddenRuleService forbiddenRuleService;
    @Autowired
    ImportExportService importExportService;

    /**
     * list
     * @param matCode
     * @param sizeId
     * @param matName
     * @param createdStart
     * @param createdEnd
     * @param updatedBy
     * @param validFlag
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/range/list", method = RequestMethod.GET)
    public Result list(@RequestParam(value = "matCode",required = false) String matCode,
                           @RequestParam(value = "sizeId",required = false) String sizeId,
                           @RequestParam(value = "matName",required = false) String matName,
                           @RequestParam(value = "createdStart",required = false) String createdStart,
                           @RequestParam(value = "createdEnd",required = false) String createdEnd,
                           @RequestParam(value = "updatedBy",required = false) String updatedBy,
                           @RequestParam(value = "validFlag",required = false,defaultValue = "0") Integer validFlag,
                           @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                           @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize ) {
        try {
            SelectByPageResult result = newGoodsRangeService.selectByParamRangeDo(matCode, sizeId, matName, createdStart, createdEnd, updatedBy, validFlag, page,pageSize);
            return ResultUtil.handleSuccessReturn(result);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    /**
     * 失效时间导入
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/range/importInvalid")
    public Result importRangeInvalid(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            return importExportService.importRangeDoInvalid(file);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeImportInvalid] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeImportInvalid] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    /**
     * 铺货范围导入
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/range/importRange")
    public Result importRangeRange(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            return importExportService.importNewGoodsRangeRange(file);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeImportInvalid] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeImportInvalid] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    /**
     * 铺货计划列表导出
     * @param matCode
     * @param matName
     * @param createStart
     * @param createEnd
     * @param updatedBy
     * @param validFlag
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/range/export")
    public Result exportRangeDo(@RequestParam(value = "matCode",required = false) String matCode,
                                 @RequestParam(value = "sizeId",required = false) String sizeId,
                                 @RequestParam(value = "matName",required = false) String matName,
                                 @RequestParam(value = "createStart",required = false) String createStart,
                                 @RequestParam(value = "createEnd",required = false) String createEnd,
                                 @RequestParam(value = "updatedBy",required = false) String updatedBy,
                                 @RequestParam(value = "validFlag",required = false) Integer validFlag,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            importExportService.exportRangeDo(matCode, sizeId, matName, createStart, createEnd, updatedBy, validFlag, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

    }

    /**
     * 铺货计划明细查看
     * @param id
     * @return
     */
    @GetMapping(value = "/rangeDetail/get")
    public Result getRangeDetailDo(@RequestParam(value = "id") Integer id) {
        try {
            NewGoodsIssueRangeDO result = newGoodsRangeService.selectByParamRangeDo(id);
            return ResultUtil.handleSuccessReturn(result);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }

    }

    /**
     * 铺货计划导出
     * @param id
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/rangeDetail/export")
    public Result exportRangeDetailDo(@RequestParam(value = "id") Integer id,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            importExportService.exportRangeDetail(id, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeList] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    /**
     * 铺货计划明细店铺导入
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/rangeDetail/importShop")
    public Result importRangeDetailShop(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) throws Exception {
        return importExportService.importRangeDetailShop(id, file);
    }

    /**
     * 铺货计划明细店铺导出
     * @param id
     * @param includeFlag
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/rangeDetail/exportShop")
    public Result exportRangeDetailDoShop(@RequestParam(value = "id") Integer id,
                                      @RequestParam(value = "includeFlag") Integer includeFlag,
                                      HttpServletRequest request, HttpServletResponse response) {
        try {
            importExportService.exportRangeShop(id, includeFlag, request, response);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeExportShop] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeExportShop] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    /**
     * 铺货计划明细保存
     * @param newGoodsIssueRangeSaveReq
     * @return
     */
    @RequestMapping(value = "/rangeDetail/save", method = RequestMethod.POST)
    public Result saveRangeDetailDo(@RequestBody NewGoodsIssueRangeSaveReq newGoodsIssueRangeSaveReq) {
        try {
            newGoodsRangeService.updNewGoodsIssueRangeDO(newGoodsIssueRangeSaveReq.getId(), newGoodsIssueRangeSaveReq.getInvalidAt(),
                    newGoodsIssueRangeSaveReq.getRegionInclude(), newGoodsIssueRangeSaveReq.getProvinceInclude(), newGoodsIssueRangeSaveReq.getCityInclude(),
                    newGoodsIssueRangeSaveReq.getSaleLvInclude(), newGoodsIssueRangeSaveReq.getDisplayLvInclude(), newGoodsIssueRangeSaveReq.getShopIdInclude(),
                    newGoodsIssueRangeSaveReq.getAttrVal1In(), newGoodsIssueRangeSaveReq.getAttrVal2In(), newGoodsIssueRangeSaveReq.getAttrVal3In(),
                    newGoodsIssueRangeSaveReq.getAttrVal4In(), newGoodsIssueRangeSaveReq.getAttrVal5In(),
                    newGoodsIssueRangeSaveReq.getRegionExclude(), newGoodsIssueRangeSaveReq.getProvinceExclude(), newGoodsIssueRangeSaveReq.getCityExclude(),
                    newGoodsIssueRangeSaveReq.getSaleLvExclude(), newGoodsIssueRangeSaveReq.getDisplayLvExclude(), newGoodsIssueRangeSaveReq.getShopIdExclude(),
                    newGoodsIssueRangeSaveReq.getAttrVal1Ex(), newGoodsIssueRangeSaveReq.getAttrVal2Ex(), newGoodsIssueRangeSaveReq.getAttrVal3Ex(),
                    newGoodsIssueRangeSaveReq.getAttrVal4Ex(), newGoodsIssueRangeSaveReq.getAttrVal5Ex());
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeDetailSave] catch exception,{0}",e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[NewGoodsRangeDetailSave] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }
}
