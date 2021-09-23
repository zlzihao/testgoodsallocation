package cn.nome.saas.cart.rest.user;

import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.cart.constant.CommonHeader;
import cn.nome.saas.cart.manager.CalcServiceManager;
import cn.nome.saas.cart.model.CalcModel;
import cn.nome.saas.cart.model.CalcSkuModel;
import cn.nome.saas.cart.model.CartCalcWrap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 结算控制器入口
 *
 * @author chentaikuang
 */
@Api(description = "结算控制器v2入口")
@RestController
@RequestMapping("/user/{uid}/checkout/v2")
@Validated
public class CalcV2Controller extends BaseController {

    @Autowired
    private CalcServiceManager calcServiceManager;


    @ApiOperation("购物车预结算")
    @RequestMapping(value = "/calc", method = RequestMethod.POST)
    public Result doCalc(@RequestHeader(CommonHeader.corpId) Integer corpId,
                         @RequestHeader(CommonHeader.appId) Integer appId,
                         @RequestBody @Valid List<CalcSkuModel> calcSku) throws Exception {
        CalcModel calcModel = new CalcModel(getThisUid(), appId, corpId, calcSku);
        CartCalcWrap resultModel = calcServiceManager.calcV2(calcModel);
        if (resultModel == null) {
            return ResultUtil.handleEmptyBizReturn();
        }
        return ResultUtil.handleSuccessReturn(resultModel);
    }

    private Integer getThisUid() {
        return this.getUid();
//		return 196;
    }

}
