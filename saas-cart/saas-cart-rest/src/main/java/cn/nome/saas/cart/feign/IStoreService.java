package cn.nome.saas.cart.feign;

import cn.nome.saas.cart.constant.CommonHeader;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 外部接口调用
 *
 * @author chentaikuang
 */
@FeignClient(value = "store")
public interface IStoreService {
    @RequestMapping(value = "/user/{uid}/products/addShoppingList/{skuCode}", method = RequestMethod.GET)
    public Object addShoppingList(@RequestHeader(CommonHeader.corpId) Integer corpId,
                                  @RequestHeader(CommonHeader.appId) Integer appId, @PathVariable("uid") @RequestHeader(CommonHeader.uid) Integer uid,
                                  @PathVariable("skuCode") String skuCode);

    @RequestMapping(value = "/user/{uid}/products/delShoppingList", method = RequestMethod.POST)
    public String delShoppingList(@RequestHeader(CommonHeader.corpId) Integer corpId,
                                  @RequestHeader(CommonHeader.appId) Integer appId, @PathVariable("uid") @RequestHeader(CommonHeader.uid) Integer uid,
                                  @RequestParam("skuCodes[]") List<String> skuCodes);
}
