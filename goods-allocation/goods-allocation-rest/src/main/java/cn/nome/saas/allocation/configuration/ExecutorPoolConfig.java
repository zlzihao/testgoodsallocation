package cn.nome.saas.allocation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorPoolConfig
 *
 * @author Bruce01.fan
 * @date 2019/10/17
 */
@Configuration
public class ExecutorPoolConfig {

    @Bean
    public ExecutorService commonPool() {
        return Executors.newFixedThreadPool(4);
    }

}
