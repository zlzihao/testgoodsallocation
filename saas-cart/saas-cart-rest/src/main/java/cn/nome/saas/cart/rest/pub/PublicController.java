package cn.nome.saas.cart.rest.pub;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.cart.manager.CartServiceManager;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 购物车控制器入口
 */
@Api("购物车")
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private CartServiceManager cartServiceManager;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result test(String tips)
            throws Exception {
        Map map = Maps.newHashMap();
        map.put("tips", tips);
        cartServiceManager.testInsertOrUpdate();
        return ResultUtil.handleSuccessReturn(map);
    }
}
