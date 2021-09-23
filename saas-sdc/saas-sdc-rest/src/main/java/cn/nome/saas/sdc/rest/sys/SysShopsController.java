package cn.nome.saas.sdc.rest.sys;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.manager.ShopMappingPositionManager;
import cn.nome.saas.sdc.manager.ShopsServiceManager;
import cn.nome.saas.sdc.model.form.ShopMappingPositionForm;
import cn.nome.saas.sdc.model.vo.PureListVO;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import cn.nome.saas.sdc.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/11/25 10:36
 */
@RestController
@RequestMapping(value = "/sys/shops")
public class SysShopsController extends BaseController {

    private ShopsServiceManager shopsServiceManager;

    private ShopMappingPositionManager shopMappingPositionManager;


    @Autowired
    public SysShopsController(ShopsServiceManager shopsServiceManager, ShopMappingPositionManager shopMappingPositionManager) {
        this.shopsServiceManager = shopsServiceManager;
        this.shopMappingPositionManager = shopMappingPositionManager;
    }


    @GetMapping(value = "/base")
    public Result<?> getBaseInfo() {
        List<ShopsVO> shops = shopsServiceManager.getAllShopsBase(getCorpId());
        PureListVO<ShopsVO> list = new PureListVO<>();
        list.setList(shops);
        return ResultUtil.handleSuccessReturn(list);
    }

    @GetMapping(value = "/initShopsChannel")
    public Result<?> initShopsChannel() {
        shopsServiceManager.initShopsChannelArea(getCorpId());
        return ResultUtil.handleSuccessReturn();
    }


    @RequestMapping(value = "/changePosition", method = RequestMethod.POST)
    public Result<?> changePositionShopCode(@RequestBody ShopMappingPositionForm form) {
        shopMappingPositionManager.changePositionCoefficient(form.getList());
        return ResultUtil.handleSuccessReturn();
    }
}
