package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.saas.allocation.feign.api.WeixinClient;
import cn.nome.saas.allocation.service.portal.UserService;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bare
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("/cas")
public class CasController {

	private static Logger logger = LoggerFactory.getLogger(CasController.class);

	@Value("${nome.wx.corpId}")
	private String corpId;
	@Value("${nome.wx.secret}")
	private String secret;

	@Value("${nome.portal.url}")
	private String url;

	@Autowired
	UserService userService;

	@Autowired
	WeixinClient weixinClient;

	@RequestMapping(value = "/casLogin", method = RequestMethod.GET)
	public void casLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userid = null;
		Assertion a = org.jasig.cas.client.util.AssertionHolder.getAssertion();
		if (a != null && a.getPrincipal() != null) {
			LoggerUtil.info(logger, "casLogin结果为：{0}", userid);
			userid = a.getPrincipal().getName();
			// 登陆成功
			request.getSession().setAttribute("userid", userid);
		}

		response.sendRedirect(url);
	}

}
