package cn.nome.saas.allocation.rest.sys;

import cn.nome.platform.common.utils.BaseConvertor;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.model.allocation.ShopToStockVo;
import cn.nome.saas.allocation.model.form.ShopToStockForm;
import cn.nome.saas.allocation.model.req.ShopToStockReq;
import cn.nome.saas.allocation.service.allocation.ShopOperateService;
import cn.nome.saas.allocation.service.allocation.ShopToStockService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
@RestController
@RequestMapping("/sys")
public class MopPositionCoefficientController {

    private static final Logger logger = LoggerFactory.getLogger(MopPositionCoefficientController.class);

    @Autowired
    private ShopToStockService stockService;


    @Autowired
    private ShopOperateService operateService;

    /*
     * 根据门店查询门店仓位数据
     * */
    @RequestMapping(value = "/stock/getByShop", method = RequestMethod.GET)
    public Result<List<ShopToStockVo>> getByShop(@RequestParam("shopCode") String shopCode) {
        ShopToStockReq vo = new ShopToStockReq();
        vo.setShopCode(shopCode);
        vo.setIsNewStatus(Constant.WAREHOUSE_APPLY_STATUS_OPEN);
        return ResultUtil.handleSuccessReturn(stockService.selectByCondition(vo, null));
    }

    /*
     * 更新门店仓位数列表
     * */
    @RequestMapping(value = "/stock/batchEdit", method = RequestMethod.POST)
    public Result<?> batchEdit(@RequestBody @NotNull(message = "list不能为空") List<ShopToStockForm> list) {
        String response = stockService.batchUpdate(BaseConvertor.convertList(list,
                ShopToStockVo.class));
        if (Strings.isNullOrEmpty(response)) {
            return ResultUtil.handleSuccessReturn();
        } else {
            return ResultUtil.handleFailtureReturn("BIZ", response);
        }
    }

    /*
     * @describe  根据门店编码查看未读消息
     * */
    @RequestMapping(value = "/stock/getMessageByShop", method = RequestMethod.GET)
    public Result<List<ShopToStockVo>> getMessageByShop(String shopCode) {
        return ResultUtil.handleSuccessReturn(operateService.getMessageByShop(shopCode));
    }

    /*
     *@describe 根据门店消息已读
     * */
    @RequestMapping(value = "/stock/readMessageByShop", method = RequestMethod.POST)
    public Result<?> readMessageByShop(String shopCode) {
        operateService.readMessageByShop(shopCode);
        return ResultUtil.handleSuccessReturn();
    }
}
