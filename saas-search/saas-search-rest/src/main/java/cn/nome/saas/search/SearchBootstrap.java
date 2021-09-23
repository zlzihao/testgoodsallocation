package cn.nome.saas.search;

import cn.nome.platform.common.config.RedisConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("cn.nome.saas.search.repository.dao")
@EnableDiscoveryClient
@EnableFeignClients
@Import({RedisConfig.class})
@EnableApolloConfig
public class SearchBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(SearchBootstrap.class, args);
    }
}
