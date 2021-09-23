package cn.nome.saas.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cn.nome.platform.common.config.BaseApiConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
 
/**
 * @author lxk on 2017/12/18
 */

@Configuration
@EnableSwagger2
@Profile({"dev","pre"})
public class ApiConfig extends BaseApiConfig {

	@Override
	public String description() {
		return "search project 接口api";
	}

	@Override
	public String title() {
		return "search swagger的接口api";
	}

	@Override
	public String basePackage() {
		return "cn.nome.saas.search";
	}

	@Override
	public String authorName() {
		return "蒋南星";
	}
 
}