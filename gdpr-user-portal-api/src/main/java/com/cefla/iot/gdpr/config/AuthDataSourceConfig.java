package com.cefla.iot.gdpr.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager",
        basePackages = {"com.cefla.iot.gdpr.repository.auth"}
)
public class AuthDataSourceConfig {

    @Bean(name = "authDataSourceProperties")
    @ConfigurationProperties("spring.datasource.auth")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "authDataSource")
    @ConfigurationProperties("spring.datasource.auth.configuration")
    public DataSource authDataSource(@Qualifier("authDataSourceProperties") DataSourceProperties authDataSourceProperties) {
        return authDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("authDataSource") DataSource authDataSource) {
        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(authDataSource)
                .packages("com.cefla.iot.gdpr.entity.auth")
                .persistenceUnit("auth")
                .properties(properties)
                .build();
    }

    @Bean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory authEntityManagerFactory) {
        return new JpaTransactionManager(authEntityManagerFactory);
    }
}
