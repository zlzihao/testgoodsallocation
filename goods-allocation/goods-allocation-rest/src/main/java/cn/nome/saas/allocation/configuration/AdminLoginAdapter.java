package cn.nome.saas.allocation.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootConfiguration
public class AdminLoginAdapter extends WebMvcConfigurerAdapter {
 
    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminLoginInterceptor).addPathPatterns("/**").excludePathPatterns("/auth/**","/cas/**","/public/**","/sys/**");
        super.addInterceptors(registry);
    }
}
