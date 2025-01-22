package com.easyads.component.db_config;

import com.easyads.component.mybatis.JsonArrayTypeHandler;
import com.easyads.management.adn.model.bean.ParamMeta;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.TypeReference;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@MapperScan(basePackages = {"com.easyads.component.mapper"},
        sqlSessionFactoryRef = "easyadsDbSessionFactory")
public class EasyAdsDbConfig {
    private static final String MAPPER_LOCATION = "classpath:mapper/*.xml";

    @Bean(name = "easyadsDbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.easyads")
    @Primary
    public DataSource easyadsDbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "easyadsDbSessionFactory")
    @Primary
    public SqlSessionFactory easyadsDbSqlSessionFactory(@Qualifier("easyadsDbDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        bean.setDataSource(dataSource);

        // 创建 SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = bean.getObject();
        // 获取配置对象并注册 TypeHandler
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        typeHandlerRegistry.register(
                List.class,
                new JsonArrayTypeHandler<>(new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {})
        );

        return sqlSessionFactory;
    }

    @Bean(name = "easyadsDbTransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("easyadsDbDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "easyadsDbSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate easyadsDbSqlSessionTemplate(@Qualifier("easyadsDbSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}



