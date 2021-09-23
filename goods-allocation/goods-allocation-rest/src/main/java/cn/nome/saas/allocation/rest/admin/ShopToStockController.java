package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.Page;
import cn.nome.platform.common.web.controller.Table;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ShopToStockVo;
import cn.nome.saas.allocation.model.common.SelectByPageResult;
import cn.nome.saas.allocation.model.protal.LocalUser;
import cn.nome.saas.allocation.model.req.ShopToStockReq;
import cn.nome.saas.allocation.repository.dao.portal.UserMapper;
import cn.nome.saas.allocation.repository.entity.allocation.ShopToStockDo;
import cn.nome.saas.allocation.service.allocation.ImportExportService;
import cn.nome.saas.allocation.service.allocation.ShopToStockService;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * ForbiddenRuleInternalController
 *
 * @author Bruce01.fan
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/allocation/shopToStock")
public class ShopToStockController {

    private static Logger LOGGER = LoggerFactory.getLogger(ShopToStockController.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    ShopToStockService shopToStockService;

    @Autowired
    ShopService shopService;

    @Autowired
    ImportExportService importExportService;


    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public Result selectByParam(@RequestParam(value = "status", required = false) Integer status,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize) {
        try {
            String operatorId = AuthUtil.getSessionUserId();
            LocalUser user = userMapper.getUser(operatorId).get(0);
            Set shopCode = null;
            if (user.getRole() == 0) {
                shopCode = shopService.getShopInfoUserName(user.getUserName());

                if (CollectionUtils.isEmpty(shopCode)) {
                    SelectByPageResult<ShopToStockDo> result = new SelectByPageResult<>();
                    List<ShopToStockDo> list = new ArrayList<>(0);
                    result.setList(list);
                    result.setTotal(0);
                    return ResultUtil.handleSuccessReturn(result);
                }
            }
            return ResultUtil.handleSuccessReturn(shopToStockService.selectByParam(status, shopCode, page, pageSize));
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[selectByParam] catch exception,{0}", e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[selectByParam] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ResponseBody
    public Result update(@RequestParam(value = "orderNo") String orderNo,
                         @RequestParam(value = "shopCode") String shopCode) {
        try {
            shopToStockService.update(orderNo, shopCode);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[update] catch exception,{0}", e.getMsg());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[update] catch exception,{0}", e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @RequestMapping(value = "/exportByParam", method = RequestMethod.GET)
    public void exportByParam(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "orderNo") String orderNo) {
        try {
            importExportService.exportShopToStockByParam(orderNo, request, response);
//            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, LOGGER, "[exportByParam] catch exception,{0}", e.getMsg());
//            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "[exportByParam] catch exception,{0}", e.getMessage());
//            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }


    /*
     * @describe 所有门店仓位数报表
     * */
    @RequestMapping(value = "/stock/getReport", method = RequestMethod.GET)
    public Result<Table<ShopToStockVo>> getReport(@RequestParam(value = "shopName", required = false) String shopName,
                                                  @RequestParam(value = "categoryId", required = false) List<Integer> categoryIds,
                                                  @RequestParam(value = "midCategoryId", required = false) List<Integer> midCategoryIds,
                                                  @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(value = "pageSize", defaultValue = "50") Integer pageSize) {


        ShopToStockReq req = new ShopToStockReq();
        if (!StringUtils.isEmpty(shopName)) {
            req.setShopNameReq(Arrays.asList(URLDecoder.decode(shopName).split(",")));
        }
        if (!CollectionUtils.isEmpty(categoryIds)) {
            req.setCategoryIds(categoryIds);
        }
        if (!CollectionUtils.isEmpty(midCategoryIds)) {
            req.setMidCategoryIds(midCategoryIds);
        }
        req.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        req.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        Page page = new Page();
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        return ResultUtil.handleSuccessReturn(shopToStockService.selectByCondition(req, page), page);
    }

    /*
     * @describe 所有门店仓位数报表导出
     * */
    @RequestMapping(value = "/stock/reportExport", method = RequestMethod.GET)
    public Result<?> export(HttpServletResponse response,
                            @RequestParam(value = "shopName", required = false) String shopName,
                            @RequestParam(value = "categoryName", required = false) String categoryName,
                            @RequestParam(value = "midCategoryName", required = false) String midCategoryName) {
        ShopToStockReq req = new ShopToStockReq();
        if (!StringUtils.isEmpty(shopName)) {
            req.setShopNameReq(Arrays.asList(URLDecoder.decode(shopName).split(",")));
        }
        if (!StringUtils.isEmpty(categoryName)) {
            req.setCategoryName(Arrays.asList(URLDecoder.decode(categoryName).split(",")));
        }
        if (!StringUtils.isEmpty(midCategoryName)) {
            req.setMidCategoryName(Arrays.asList(URLDecoder.decode(midCategoryName).split(",")));
        }
        req.setStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        req.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        shopToStockService.reportExport(response, req);
        return ResultUtil.handleSuccessReturn();
    }
}
