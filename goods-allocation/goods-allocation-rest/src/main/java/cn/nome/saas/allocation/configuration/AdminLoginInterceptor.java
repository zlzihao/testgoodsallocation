package cn.nome.saas.allocation.configuration;

import cn.nome.saas.allocation.constant.Constant;
import cn.nome.saas.allocation.service.portal.UserService;
import cn.nome.saas.allocation.utils.AuthUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {

		if (Constant.DEBUG_FLAG_USER) {
			return true;
		}

		String userid = AuthUtil.getUserid(httpServletRequest);

		if (StringUtils.isNotBlank(userid)) {
			String servlet_info = httpServletRequest.getServletPath();

			if (servlet_info.indexOf("/user") == 0) {
				return true;
			}

			List<String> appcode_list = userService.getUserApplicationCodeList(userid);
			for(String appcode : appcode_list) {

				int index = servlet_info.indexOf(appcode);
				if(index >= 0) {
					return true;
				}
			}
		}

	    httpServletResponse.setCharacterEncoding("utf-8");
		httpServletResponse.setContentType("application/json; charset=utf-8");
		httpServletResponse.getWriter().append("{\"code\":\"AUTH\"}");
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {

	}
}
