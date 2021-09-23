package cn.nome.saas.sdc.rest.pub;

import cn.nome.platform.common.web.controller.BaseController;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.constant.Constant;
import cn.nome.saas.sdc.manager.ShopsServiceManager;
import cn.nome.saas.sdc.model.vo.ShopsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lizihao@nome.com
 */
@RestController
@RequestMapping(value = "/public/shops")
public class ShopBaseController extends BaseController {

    private final ShopsServiceManager shopsServiceManager;

    @Autowired
    public ShopBaseController(ShopsServiceManager shopsServiceManager) {
        this.shopsServiceManager = shopsServiceManager;
    }

    @GetMapping(value = "/base")
    public Result<List<ShopsVO>> getBaseInfo() {
        List<ShopsVO> shops = shopsServiceManager.getAllShopsBase(Constant.DEFAULT_CORP_ID);
        return ResultUtil.handleSuccessReturn(shops);
    }
}
