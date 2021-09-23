package cn.nome.saas.cart;

import cn.nome.platform.common.config.NomeKafkaAutoConfig;
import cn.nome.platform.common.shard.config.DbShardingBeanConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @author chentaikuang
 */
@EnableApolloConfig
@SpringBootApplication
@MapperScan({"cn.nome.saas.cart.repository.dao"})
//@ComponentScan({"cn.nome.saas.cart.rest.*","cn.nome.saas.cart.manager.*"})
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableFeignClients
@Import({DbShardingBeanConfig.class, NomeKafkaAutoConfig.class})
public class CartBootstrap {

    private static Logger logger = LoggerFactory.getLogger(CartBootstrap.class);

    public static void main(String[] args) {
        SpringApplication.run(CartBootstrap.class, args);
    }
}
