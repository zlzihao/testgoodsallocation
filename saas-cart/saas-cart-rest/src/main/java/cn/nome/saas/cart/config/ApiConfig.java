package cn.nome.saas.cart.config;

import cn.nome.platform.common.config.BaseApiConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lxk on 2017/12/18
 */
@Configuration
@EnableSwagger2
@Profile({ "dev","pre" })
public class ApiConfig extends BaseApiConfig{
	@Override
	public String description() {
		return "";
	}

	@Override
	public String title() {
		return "";
	}

	@Override
	public String basePackage() {
		return "cn.nome.saas.cart";
	}

	@Override
	public String authorName() {
		return "";
	}

}