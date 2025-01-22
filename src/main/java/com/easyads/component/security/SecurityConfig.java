package com.easyads.component.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()  // 所有请求都不需要认证
                )
                .csrf().disable()  // 禁用 CSRF 防护（适用于无状态应用）;
                .cors();  // 启用跨域资源共享

        return http.build();
    }
}

