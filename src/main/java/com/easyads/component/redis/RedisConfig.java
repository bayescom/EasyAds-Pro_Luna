package com.easyads.component.redis;

import jakarta.annotation.PreDestroy;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.pool")
    @Scope(value = "prototype")
    public GenericObjectPoolConfig redisPool(){
        return new GenericObjectPoolConfig();
    }

    private LettuceConnectionFactory redisConnectionFactory(GenericObjectPoolConfig poolConfig, RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).commandTimeout(Duration.ofMillis(poolConfig.getMaxWaitMillis())).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    // primary conf redis
    // this redis conf just for primary setting which will not be used
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.primary")
    @Primary
    public RedisStandaloneConfiguration primaryRedisConf() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("primaryRedisConnectionFactory")
    @Primary
    public LettuceConnectionFactory primaryRedisConnectionFactory(GenericObjectPoolConfig poolConfig,
                                                                   @Qualifier("primaryRedisConf") RedisStandaloneConfiguration primaryRedisConf) {
        return redisConnectionFactory(poolConfig, primaryRedisConf);
    }

    @Bean("primaryConfRedisTemplate")
    @Primary
    public StringRedisTemplate primaryConfRedisTemplate(
            @Qualifier("primaryRedisConnectionFactory")RedisConnectionFactory primaryRedisConnectionFactory) {
        return new StringRedisTemplate(primaryRedisConnectionFactory);
    }

    // ------------------------------------------------ online redis settings ---------------------------------------------
    // easy ads conf redis
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.easyads")
    public RedisStandaloneConfiguration easyadsRedisConf() {
        return new RedisStandaloneConfiguration();
    }

    @Bean(name = "easyadsRedisConnectionFactory")
    public LettuceConnectionFactory easyadsRedisConnectionFactory(GenericObjectPoolConfig poolConfig,
                                                                      @Qualifier("easyadsRedisConf") RedisStandaloneConfiguration easyadsRedisConf) {
        return redisConnectionFactory(poolConfig, easyadsRedisConf);
    }

    @Bean("easyadsConfRedisTemplate")
    public StringRedisTemplate easyadsConfRedisTemplate(
            @Qualifier("easyadsRedisConnectionFactory") RedisConnectionFactory easyadsRedisConnectionFactory) {
        return new StringRedisTemplate(easyadsRedisConnectionFactory);
    }

    // ----------------------------------------- 优雅退出关闭连接池 -----------------------------------------
    @PreDestroy
    public void gracefulShutdown() {
        // 获取 primary Redis 连接池并关闭
        LettuceConnectionFactory primaryConnectionFactory = primaryRedisConnectionFactory(redisPool(), primaryRedisConf());
        if (primaryConnectionFactory != null) {
            primaryConnectionFactory.destroy();
            System.out.println("Primary Redis client has been shut down gracefully.");
        }

        // 获取 easyads Redis 连接池并关闭
        LettuceConnectionFactory easyadsConnectionFactory = easyadsRedisConnectionFactory(redisPool(), easyadsRedisConf());
        if (easyadsConnectionFactory != null) {
            easyadsConnectionFactory.destroy();
            System.out.println("EasyAds Redis client has been shut down gracefully.");
        }
    }
}
