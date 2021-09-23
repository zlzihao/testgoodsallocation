package cn.nome.saas.allocation.rest.sys;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.AsyncTask;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ShopToStock;
import cn.nome.saas.allocation.model.old.allocation.IssueTask;
import cn.nome.saas.allocation.model.old.issue.MatDetailTree;
import cn.nome.saas.allocation.service.allocation.*;
import cn.nome.saas.allocation.service.basic.GoodsService;
import cn.nome.saas.allocation.service.basic.ShopService;
import cn.nome.saas.allocation.service.old.allocation.IssueRestService;
import cn.nome.saas.allocation.service.portal.UserService;
import cn.nome.saas.allocation.service.rule.GlobalConfigRuleService;
import cn.nome.saas.allocation.service.rule.ShopExpressService;
import cn.nome.saas.allocation.utils.ExcelUtil;
import cn.nome.saas.allocation.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StoreHelperInternalController
 *
 * @author Bruce01.fan
 * @date 2019/4/12
 */
@RestController
@RequestMapping("/sys/allocation")
public class SyncInternalController {

    private static Logger logger = LoggerFactory.getLogger(SyncInternalController.class);

    @Autowired
    NewGoodsRangeService newGoodsRangeService;

    @Autowired
    private DisplayService displayService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonImportService commonImportService;

    @Autowired
    ShopToStockService shopToStockService;
    IssueRestService issueRestService;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    NewIssueMatchService newIssueMatchService;

    @Autowired
    ShopExpressService shopExpressService;

    @Autowired
    OutOfStockGoodsService outOfStockGoodsService;

    @Autowired
    GlobalConfigRuleService globalConfigRuleService;

    @RequestMapping(value = "/load/dwsDimGoods", method = RequestMethod.GET)
    public Result dwsDimGoodsLoad() {
        return ResultUtil.handleSuccessReturn(goodsService.syncDataCenterData());
    }

    @RequestMapping(value = "/load/dwsDimShop", method = RequestMethod.GET)
    public Result dwsDimShopLoad() {
        return ResultUtil.handleSuccessReturn(shopService.syncDataCenterData());
    }

    @RequestMapping(value = "/sync/user", method = RequestMethod.GET)
    public Result syncUserData() {
        userService.syncUserData();
        return ResultUtil.handleSuccessReturn();
    }

    //二次加工处理
    @GetMapping(value = "/secProcess")
    public Result secProcess(HttpServletRequest request) throws Exception {
        return commonImportService.secProcess(request);
    }


    @RequestMapping(value = "/getIssueDayV2", method = RequestMethod.GET)
    public Result getIssueDayV2(@RequestParam("date") String date,
            @RequestParam("onRoadDays") int onRoadDays,
                                @RequestParam("issueTime") String issueTime) {
        return ResultUtil.handleSuccessReturn(IssueDayUtil.getIssueDayV2(date,onRoadDays,issueTime));
    }


    @RequestMapping(value = "/getTime", method = RequestMethod.GET)
    public Result getTime() {
        return ResultUtil.handleSuccessReturn(new Date());
    }

    //最小陈列量列表接口
    @RequestMapping(value = "/getMinDisplayList", method = RequestMethod.GET)
    public Result selectByParam(@RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize) {
        try {
            return ResultUtil.handleSuccessReturn(displayService.minDisplayList(page, pageSize));
        } catch (BusinessException e) {
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }


    //门店调仓申请
    @RequestMapping(value = "/shopToStock/add", method = RequestMethod.POST)
    public Result shopToStockAdd(@RequestBody ShopToStock shopToStock) {
        try {
            shopToStockService.shopToStockAdd(shopToStock);
            return ResultUtil.handleSuccessReturn(1);
        } catch (BusinessException e) {
            LoggerUtil.error(e, logger, e.getMessage());
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LoggerUtil.error(e, logger, e.getMessage());
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    //商品列表
    @RequestMapping(value = "/getGoodsInfoList", method = RequestMethod.GET)
    public Result getGoodsInfoList(@RequestParam(value = "largeCategory",required = false) String largeCategory,
                                   @RequestParam(value = "midCategory",required = false) String midCategory,
                                   @RequestParam(value = "smallCategory",required = false) String smallCategory,
                                   @RequestParam(value = "matCode",required = false) String matCode,
                                   @RequestParam(value = "matName",required = false) String matName,
                                   @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "50") int pageSize) {
        try {
            return ResultUtil.handleSuccessReturn(goodsService.selectByParam(largeCategory,midCategory,smallCategory,matCode,matName,page, pageSize));
        } catch (BusinessException e) {
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    //门店列表
    @RequestMapping(value = "/getShopInfoList", method = RequestMethod.GET)
    public Result getShopInfoList() {
        try {
            return ResultUtil.handleSuccessReturn(shopService.getShopInfoData());
        } catch (BusinessException e) {
            return ResultUtil.handleBizFailtureReturn(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return ResultUtil.handleSysFailtureReturn(e.getMessage());
        }
    }

    @GetMapping(value = "/downloadMatDetail")
    public String downloadMatDetail(@RequestParam("taskId") int taskId,
                                    @RequestParam("shopIds") String shopIds,HttpServletRequest request, HttpServletResponse response) {
        List<MatDetailTree> list = issueRestService.downloadMatDetail(taskId, Stream.of(shopIds.split(",")).collect(Collectors.toList()));

        try {
            ExcelUtil.exportMatDetailData(list,request,response);
        } catch (Exception e) {
            LoggerUtil.error(e,logger,"ERROR");
            return "fail";
        }
        return "886";
    }

    @RequestMapping(value = "/reRunIssueById", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueById(@RequestParam("taskId") int taskId,
                                 @RequestParam("shopIds") String shopIds) throws Exception {

        IssueTask issueTask = issueRestService.getTaskById(taskId);
        asyncTask.runIssueTask(issueTask, Stream.of(shopIds.split(",")).collect(Collectors.toList()));
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/new/runIssueTaskById", method = RequestMethod.GET)
    @ResponseBody
    public Result reRunIssueTask(@RequestParam(value = "taskId", required = false, defaultValue = "-1") int taskId) throws Exception {
        newIssueMatchService.processCategorySkcData(taskId);
        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/loadNewShopExpress", method = RequestMethod.GET)
    @ResponseBody
    public Result loadNewShopExpress() {

        shopExpressService.loaddingAllNewShopExpress();

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/matchShopFee", method = RequestMethod.GET)
    @ResponseBody
    public Result matchShopFee() {

        try {
            shopExpressService.matchShopFee();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ResultUtil.handleSuccessReturn();
    }

    @RequestMapping(value = "/calc/saleqty", method = RequestMethod.GET)
    @ResponseBody
    public Result saleqty() {
        outOfStockGoodsService.processOutStockSaleQty();

        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/syncGlobalConfigData", method = RequestMethod.GET)
    @ResponseBody
    public Result syncGlobalConfigData() {

        if (Constant.DEBUG_FLAG_STOP_CRONTAB_TASK) {
            logger.info("[DEBUG_FLAG_STOP_CRONTAB_TASK], syncGlobalConfigData stop");
            return ResultUtil.handleSuccessReturn();
        }

        return  globalConfigRuleService.syncGlobalConfigData();
    }

}
