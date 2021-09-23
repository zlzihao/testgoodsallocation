package cn.nome.saas.allocation.rest.admin;


import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.utils.JsonUtils;
import cn.nome.platform.common.web.controller.annotation.Resulted;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.feign.api.WeixinClient;
import cn.nome.saas.allocation.feign.model.Token;
import cn.nome.saas.allocation.feign.model.WxUser;
import cn.nome.saas.allocation.service.portal.UserService;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/auth")
public class 	AuthController {

	private static Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Value("${nome.wx.corpId}")
	private String corpId;
	@Value("${nome.wx.secret}")
	private String secret;

	@Value("${nome.portal.url}")
	private String url;
	
	final
	UserService userService;

	final
	WeixinClient weixinClient;

	@Autowired
	public AuthController(UserService userService, WeixinClient weixinClient) {
		this.userService = userService;
		this.weixinClient = weixinClient;
	}

	@RequestMapping(value = "/wxLogin", method = RequestMethod.GET)
	@ResponseBody
	@Resulted
	public Result wxLogin(HttpServletRequest request, String code) throws Exception {
		Token token = weixinClient.gettoken(corpId, secret);
		LoggerUtil.info(logger, "wxLogin结果为：{0}", token);
		String str = weixinClient.getUserinfo2(token.getAccess_token(), code);
		
		String resultData = str.replace("UserId", "userId");
		WxUser user = JsonUtils.parse(resultData, WxUser.class);
		
		LoggerUtil.info(logger, "wxLogin user结果为：{0}", user);
		if (user.getErrcode() == 0 && user.getUserId() != null) {
			// 登陆成功
			LoggerUtil.info(logger, "wxLogin 登陆成功：{0}", user);
			request.getSession().setAttribute("userid", user.getUserId());
			request.getSession().setMaxInactiveInterval(86400);
		}
		return ResultUtil.handleSuccessReturn(user);
	}

	@RequestMapping(value = "/casLogin", method = RequestMethod.GET)
	@ResponseBody
	public Result casLogin(HttpServletRequest request) throws Exception {

		String userid = null;
		Assertion a = org.jasig.cas.client.util.AssertionHolder.getAssertion();
		if(a != null && a.getPrincipal() != null) {
			userid = a.getPrincipal().getName();
		}
		
		if (userid != null) {
			// 登陆成功
			request.getSession().setAttribute("userid", userid);
		}
		return ResultUtil.handleSuccessReturn(userid);
	}

	@RequestMapping(value = "/exchange/user", method = RequestMethod.GET)
	@ResponseBody
	public Result exchange(HttpServletRequest request, @RequestParam("token") String token) throws Exception {

		String[] array = token.split("\\|");
		if (array.length != 3) {
			return ResultUtil.handleFailtureReturn("BIZ", "登录调拨系统失败，请重新登录");
		}

		request.getSession().setAttribute("userid", array[1]);

		return ResultUtil.handleSuccessReturn();
	}

	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Result login(HttpServletRequest request, String userid, String password) throws Exception {
		if(password.equals("nome3344")) {
			request.getSession().setAttribute("userid", userid);
		}
		return ResultUtil.handleSuccessReturn(userid);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public Result logout(HttpServletRequest request) throws Exception {
		request.getSession().invalidate();
		return ResultUtil.handleSuccessReturn(null);
	}
	
	@RequestMapping(value = "/redirect", method = RequestMethod.GET)
	public void redirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.sendRedirect(url);

	}
}
