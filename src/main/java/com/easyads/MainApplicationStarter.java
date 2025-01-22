package com.easyads;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class MainApplicationStarter extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationStarter.class, args);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(10);
        return taskScheduler;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(MainApplicationStarter.class);
    }

    // 确保线程池正确关闭，避免内存泄漏
    @Bean
    public DisposableBean taskSchedulerShutdown(ThreadPoolTaskScheduler taskScheduler) {
        return new DisposableBean() {
            @Override
            public void destroy() throws Exception {
                // 显式关闭线程池
                if (taskScheduler != null) {
                    taskScheduler.shutdown();
                }
            }
        };
    }
}
