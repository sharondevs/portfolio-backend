package com.moonraft.search.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.moonraft.search"})
@EnableJpaRepositories(basePackages = {"com.moonraft.search.domain.repository"})
@EntityScan("com.moonraft.search.domain.model")
public class SearchApplication implements AsyncConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1000);
//        taskExecutor.setMaxPoolSize(500);
//        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Async-Custom-Config-");
        taskExecutor.initialize();
        return taskExecutor;

    }
}