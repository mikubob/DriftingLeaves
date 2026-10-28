package com.xuan.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Tomcat、业务异步任务及 MVC 异步请求的虚拟线程配置。
 */
@Slf4j
@Configuration
@EnableAsync
public class VirtualThreadConfiguration implements WebMvcConfigurer {

    /**
     * 配置Tomcat使用虚拟线程
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> tomcatVirtualThreadExecutor() {
        return protocolHandler -> protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 配置Spring异步任务使用虚拟线程
     */
    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }


    /**
     * 配置MVC异步请求支持
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new TaskExecutorAdapter(
                Executors.newVirtualThreadPerTaskExecutor()
        ));
        configurer.setDefaultTimeout(30000L);  // 30秒超时
    }

}