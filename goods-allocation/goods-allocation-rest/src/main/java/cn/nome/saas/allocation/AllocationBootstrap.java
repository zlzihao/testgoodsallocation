package cn.nome.saas.allocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class AllocationBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(AllocationBootstrap.class, args);
    }
}

@EnableAsync
@Configuration
class TaskPoolConfig {


    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(5);
        executor.setThreadNamePrefix("taskExecutor-");
        return executor;
    }
}
