package cn.nome.saas.allocation.feign.api;

import cn.nome.saas.allocation.feign.model.DepartmentList;
import cn.nome.saas.allocation.feign.model.Token;
import cn.nome.saas.allocation.feign.model.User;
import cn.nome.saas.allocation.feign.model.WxUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "wx", url = "https://qyapi.weixin.qq.com")
public interface WeixinClient {
	@RequestMapping(value = "/cgi-bin/gettoken", method = RequestMethod.GET)
	public Token gettoken(@RequestParam("corpid") String corpid, @RequestParam("corpsecret") String corpsecret);

	@RequestMapping(value = "/cgi-bin/user/get", method = RequestMethod.GET)
	public User getUser(@RequestParam("access_token") String access_token, @RequestParam("userid") String userid);

	@RequestMapping(value = "/cgi-bin/department/list", method = RequestMethod.GET)
	public DepartmentList getDepartmentList(@RequestParam("access_token") String access_token, @RequestParam("id") int id);
	
	@RequestMapping(value = "/cgi-bin/user/getuserinfo", method = RequestMethod.GET)
	public WxUser getUserinfo(@RequestParam("access_token") String access_token, @RequestParam("code") String code);
	
	@RequestMapping(value = "/cgi-bin/user/getuserinfo", method = RequestMethod.GET)
	public String getUserinfo2(@RequestParam("access_token") String access_token, @RequestParam("code") String code);

}
