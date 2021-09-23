package cn.nome.saas.allocation.configuration;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CasConfigure {
	
	@Value("${nome.portal.url}")
	private String url;
	
	@Bean
	public FilterRegistrationBean authenticationFilterRegistrationBean() {
		FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
		authenticationFilter.setFilter(new AuthenticationFilter());
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("casServerLoginUrl", "http://it.nome.cn/login");
		initParameters.put("serverName", url + "cas/casLogin");
		authenticationFilter.setInitParameters(initParameters);
		authenticationFilter.setOrder(2);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/cas/*");// 设置匹配的url
		authenticationFilter.setUrlPatterns(urlPatterns);
		return authenticationFilter;
	}
	
	@Bean
	public FilterRegistrationBean ValidationFilterRegistrationBean(){
		FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
		authenticationFilter.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("casServerUrlPrefix", "http://it.nome.cn");
		initParameters.put("serverName", url + "cas/casLogin");
		authenticationFilter.setInitParameters(initParameters);
		authenticationFilter.setOrder(1);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/cas/*");// 设置匹配的url
		authenticationFilter.setUrlPatterns(urlPatterns);
		
		
		return authenticationFilter;
	}
	
	@Bean
	public FilterRegistrationBean casHttpServletRequestWrapperFilter(){
		FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
		authenticationFilter.setFilter(new HttpServletRequestWrapperFilter());
		authenticationFilter.setOrder(3);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/cas/*");// 设置匹配的url
		authenticationFilter.setUrlPatterns(urlPatterns);
		return authenticationFilter;
	}
	
	@Bean
	public FilterRegistrationBean casAssertionThreadLocalFilter(){
		FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
		authenticationFilter.setFilter(new AssertionThreadLocalFilter());
		authenticationFilter.setOrder(4);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/cas/*");// 设置匹配的url
		authenticationFilter.setUrlPatterns(urlPatterns);
		return authenticationFilter;
	}
}