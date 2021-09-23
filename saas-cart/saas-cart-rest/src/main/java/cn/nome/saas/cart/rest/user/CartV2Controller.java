package cn.nome.saas.cart.rest.user;

import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.cart.constant.CommonHeader;
import cn.nome.saas.cart.manager.CartServiceManager;
import cn.nome.saas.cart.model.CartWrap;
import cn.nome.saas.cart.model.LoadModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购物车控制器入口
 *
 * @author chentaikuang
 */
@Api(description = "购物车控制器v2入口")
@RestController
@RequestMapping("/user/{uid}/cart/v2")
@Validated
public class CartV2Controller extends BaseController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CartServiceManager cartServiceManager;

    @ApiOperation("根据uid获取购物车")
    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public Result load(@RequestHeader(CommonHeader.corpId) Integer corpId,
                       @RequestHeader(CommonHeader.appId) Integer appId)
            throws Exception {
        LoadModel loadModel = new LoadModel(getThisUid(), appId, corpId);
        try {
            CartWrap cartWrap = cartServiceManager.loadCartV2(loadModel);
            return ResultUtil.handleSuccessReturn(cartWrap);
        } catch (Exception e) {
            LOGGER.error("load err msg:{},uid:{}", e.getMessage(), loadModel.getUid());
            return ResultUtil.handleEmptyBizReturn();
        }
    }

    private Integer getThisUid() {
        return this.getUid();
//        return 196;
    }
}
