package cn.nome.saas.allocation.feign.api;

import cn.nome.saas.allocation.feign.model.Token;
import cn.nome.saas.allocation.feign.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "gy", url = "http://lh.nome.cn")
public interface GuanYuanClient {
	@RequestMapping(value = "/public-api/data-source/c9bec50dc88f440308f05f50/refresh", method = RequestMethod.GET)
	public String syncAllocationTask(@RequestParam("token") String token);

	@RequestMapping(value = "/public-api/data-source/d4670e21e47ce48559a43a96/refresh", method = RequestMethod.GET)
	public String syncAllocationDetail(@RequestParam("token") String token);

}
