package cn.nome.saas.cart.feign;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.nome.platform.common.web.controller.protocol.Result;

/**
 * 外部接口调用
 */
@FeignClient(value = "pd-goods")
public interface IGoodsService {
	@RequestMapping(value = "/sys/product/getProductSetIds", method = RequestMethod.POST)
	public Result<Map<Integer, Set<Integer>>> getProductSetIds(@RequestParam("corpId") Integer corpId,
			@RequestParam("appId") Integer appId, @RequestParam("uid") Integer uid,
			@RequestParam("productIds") List<Integer> productIds);

	@RequestMapping(value = "/sys/product/getSkuByIdsOrCodes", method = RequestMethod.POST)
	public Result<List<SkuModel>> getSkuByIdsOrCodes(@RequestParam("corpId") Integer corpId,
			@RequestParam("appId") Integer appId, @RequestParam("skuCodes") List<String> skuCodes);
}
