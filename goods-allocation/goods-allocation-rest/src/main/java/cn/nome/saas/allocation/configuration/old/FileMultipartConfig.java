package cn.nome.saas.allocation.configuration.old;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileMultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 设置文件大小限制KB,MB ,超出此大小页面会抛出异常信息
        factory.setMaxFileSize("100MB");
        // 设置总上传数据总大小
        factory.setMaxRequestSize("100MB");

        return factory.createMultipartConfig();
    }
}