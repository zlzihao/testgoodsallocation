package cn.nome.saas.cart.rest.sys;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.cart.constant.Constant;
import cn.nome.saas.cart.enums.StatusCode;
import cn.nome.saas.cart.manager.CartServiceManager;
import cn.nome.saas.cart.model.RefreshCacheSkuModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 购物车对内控制器入口
 *
 * @author chentaikuang
 */
@Api(description = "购物车对内控制器入口")
@RestController
@RequestMapping("/sys/cart")
@Validated
public class CartSysController extends BaseController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CartServiceManager cartServiceManager;

    @ApiOperation("刷新缓存sku")
    @RequestMapping(value = "/refreshCacheSku", method = RequestMethod.POST)
    public Result refreshCacheSku(@NotNull Integer uid, @NotNull Integer corpId, @NotNull Integer appId,
                                  @RequestBody @NotEmpty(message = "skuCodes不可空") List<String> skuCodes) throws Exception {

        RefreshCacheSkuModel skuModel = new RefreshCacheSkuModel(uid, appId, corpId, skuCodes);
        int um = cartServiceManager.refreshCacheSku(skuModel);
        return ResultUtil.handleSuccessReturn(um);
    }

    @ApiOperation("删除购物车商品")
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public Result del(Integer corpId, Integer appId, Integer uid,
                      @RequestBody @NotEmpty(message = "skuCodes不可空") List<String> skuCodes) throws Exception {
        try {
            int rtn = cartServiceManager.delSkuCodes(corpId, appId, uid, skuCodes);
            if (rtn == -1) {
                return ResultUtil.handleAuthFailtureReturn("删除失败:" + rtn);
            }
            return ResultUtil.handleSuccessReturn(rtn);
        } catch (BusinessException be) {
            return ResultUtil.handleBizFailtureReturn(be.getCode(), be.getMessage());
        }
    }

    @ApiOperation("删除缓存商品")
    @RequestMapping(value = "/delCacheSku", method = RequestMethod.GET)
    public Result delCacheSku(@RequestParam("skuCode") String skuCode) throws Exception {
        return cartServiceManager.delCacheSku(skuCode);
    }

    @ApiOperation("路由表")
    @RequestMapping(value = "/routeTab", method = RequestMethod.GET)
    public Result routeTab(String tabNm, int shareSize, String shareKey) {
        String shareIndex = getShardingIndex(tabNm, shareSize, shareKey);
        LOGGER.info("ROUTE_TAB:{}", shareIndex);
        return ResultUtil.handleSuccessReturn(shareIndex);
    }

    @ApiOperation("全量同步购物车")
    @RequestMapping(value = "/syncByPage", method = RequestMethod.GET)
    public Result syncByPage(@RequestParam("curPage") int curPage, @RequestParam("pageSize") int pageSize) {
        int count = cartServiceManager.syncByPage(curPage, pageSize);
        return ResultUtil.handleSuccessReturn(count);
    }

    @ApiOperation("删除冗余购物车SKU")
    @RequestMapping(value = "/delCartRedunSkus", method = RequestMethod.GET)
    public Result delCartRedunSkus(@RequestParam("doSide") String doSide, @RequestParam(value = "uids", required = false) String uids) {
        Assert.isTrue(Constant.DO_SIDE_LIST.contains(doSide), StatusCode.PARAMS_ERR.getMsg());
        return cartServiceManager.delCartRedunSkus(doSide, uids);
    }

    @ApiOperation("同步购物车别名alias")
    @RequestMapping(value = "/syncCartAlias", method = RequestMethod.GET)
    public Result syncCartAlias(@RequestParam(value = "uids", required = false) String uids) {
        return cartServiceManager.syncCartAlias(uids);
    }

}
