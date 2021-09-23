package cn.nome.saas.cart.rest.user;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.nome.platform.common.constant.Constants;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.cart.constant.CommonHeader;
import cn.nome.saas.cart.manager.CartServiceManager;
import cn.nome.saas.cart.model.AddModel;
import cn.nome.saas.cart.model.AddSkuModel;
import cn.nome.saas.cart.model.CartSkuModel;
import cn.nome.saas.cart.model.CartWrap;
import cn.nome.saas.cart.model.CountModel;
import cn.nome.saas.cart.model.LoadModel;
import cn.nome.saas.cart.model.MergeModel;
import cn.nome.saas.cart.model.RefreshModel;
import cn.nome.saas.cart.model.SyncCookieSkuModel;
import cn.nome.saas.cart.model.SyncSkuModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 购物车控制器入口
 *
 * @author chentaikuang
 */
@Api(description = "购物车控制器入口")
@RestController
@RequestMapping("/user/{uid}/cart")
@Validated
public class CartController extends BaseController {

    @Autowired
    private CartServiceManager cartServiceManager;

    @Deprecated
    @ApiOperation("根据uid获取购物车")
    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public Result<?> load(@RequestHeader(CommonHeader.corpId) Integer corpId,
                          @RequestHeader(CommonHeader.appId) Integer appId)
            throws Exception {

        LoadModel loadModel = new LoadModel(thisUid(), appId, corpId);
        try {
            CartWrap cartWrap = cartServiceManager.loadCart(loadModel);
            return ResultUtil.handleSuccessReturn(cartWrap);
        } catch (Exception e) {
            return ResultUtil.handleEmptyBizReturn();
        }
    }

    private Integer thisUid() {
        return this.getUid();
//        return 196;
    }

    @ApiOperation("添加购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<?> add(@RequestHeader(CommonHeader.corpId) Integer corpId,
                         @RequestHeader(CommonHeader.appId) Integer appId,
                         @RequestBody @Valid AddSkuModel addSkuModel) throws Exception {

        AddModel addModel = new AddModel(thisUid(), appId, corpId, addSkuModel);
        return cartServiceManager.addCart(addModel);
    }

    @ApiOperation("删除购物车商品")
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public Result<?> del(@RequestHeader(CommonHeader.corpId) Integer corpId,
                         @RequestHeader(CommonHeader.appId) Integer appId,
                         @RequestBody @NotEmpty(message = "skuCodes不可空") List<String> skuCodes) throws Exception {
        try {
            int rtn = cartServiceManager.delSkuCodes(corpId, appId, thisUid(), skuCodes);
            if (rtn == -1) {
                return ResultUtil.handleAuthFailtureReturn("删除失败:" + rtn);
            }
            return ResultUtil.handleSuccessReturn(rtn);
        } catch (BusinessException be) {
            return ResultUtil.handleBizFailtureReturn(be.getCode(), be.getMessage());
        }
    }

    @ApiOperation("购物车同步cookie商品")
    @RequestMapping(value = "/syncCookieSkus", method = RequestMethod.POST)
    public Result<?> syncCookieSkus(@RequestHeader(CommonHeader.corpId) Integer corpId,
                                    @RequestHeader(CommonHeader.appId) Integer appId,
                                    @RequestBody @Valid List<SyncSkuModel> syncSkuModels) throws Exception {

        SyncCookieSkuModel cookieSkuModel = new SyncCookieSkuModel(thisUid(), appId, corpId);
        int sync = cartServiceManager.syncCookieSkus(cookieSkuModel, syncSkuModels);
        return ResultUtil.handleSuccessReturn(sync);
    }

    @ApiOperation("购物车更新商品")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result<?> modify(@RequestHeader(CommonHeader.corpId) Integer corpId,
                            @RequestHeader(CommonHeader.appId) Integer appId,
                            @RequestBody Map<String, SyncSkuModel> map) throws Exception {
        try {
            SyncSkuModel oldModel = (SyncSkuModel) map.get("oldModifyModel");
            SyncSkuModel newModel = (SyncSkuModel) map.get("newModifyModel");
            if (oldModel == null || newModel == null) {
                throw new BusinessException(Constants.RESULT_CODE);
            }
            CartSkuModel cartSkuModel = cartServiceManager.modifyCart(corpId, appId, thisUid(), oldModel, newModel);
            if (cartSkuModel == null) {
                return ResultUtil.handleEmptyBizReturn();
            }
            return ResultUtil.handleSuccessReturn(cartSkuModel);
        } catch (BusinessException be) {
            return ResultUtil.handleBizFailtureReturn(be.getCode(), be.getMessage());
        }
    }

    @ApiOperation("用户购物车合并")
    @RequestMapping(value = "/merge", method = RequestMethod.POST)
    public Result<?> merge(@RequestHeader(CommonHeader.corpId) Integer corpId,
                           @RequestHeader(CommonHeader.appId) Integer appId)
            throws Exception {

        MergeModel mergeModel = new MergeModel(thisUid(), appId, corpId);
        int sync = cartServiceManager.mergeCart(mergeModel);
        return ResultUtil.handleSuccessReturn(sync);
    }

    @ApiOperation("用户购物车刷新")
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public Result<?> refresh(@RequestHeader(CommonHeader.corpId) Integer corpId,
                             @RequestHeader(CommonHeader.appId) Integer appId)
            throws Exception {
        RefreshModel refreshModel = new RefreshModel(thisUid(), appId, corpId);
        int sync = cartServiceManager.refreshCart(refreshModel);
        return ResultUtil.handleSuccessReturn(sync);
    }

    @ApiOperation("查询用户购物车商品数量")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Result<?> count(@RequestHeader(CommonHeader.corpId) Integer corpId,
                           @RequestHeader(CommonHeader.appId) Integer appId)
            throws Exception {

        CountModel countModel = new CountModel(thisUid(), appId, corpId);
        int um = cartServiceManager.count(countModel);
        return ResultUtil.handleSuccessReturn(um);
    }

}
