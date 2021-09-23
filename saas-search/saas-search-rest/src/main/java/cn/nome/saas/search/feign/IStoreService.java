package cn.nome.saas.search.feign;

import cn.nome.saas.search.constant.CommonHeader;
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

    @RequestMapping(value = "/sys/products/get-discounts-info", method = RequestMethod.POST)
    public String getDiscountsInfo(@RequestHeader(CommonHeader.corpId) Integer corpId,
                                  @RequestHeader(CommonHeader.appId) Integer appId, @PathVariable("uid") @RequestHeader(CommonHeader.uid) Integer uid,
                                  @RequestParam("productIds[]") List<Integer> productIds);
}
