package cn.nome.saas.sdc;

import cn.nome.platform.common.config.RedisConfig;
import cn.nome.platform.common.config.WechatWarnMsgConfig;
import cn.nome.platform.common.mybatis.config.MyBatisComposeConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
@SpringBootApplication
@MapperScan("cn.nome.saas.sdc.repository.dao")
@EnableDiscoveryClient
@EnableFeignClients
@Import({RedisConfig.class, MyBatisComposeConfig.class, WechatWarnMsgConfig.class})
@EnableAsync
public class SdcBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(SdcBootstrap.class, args);
    }
}
