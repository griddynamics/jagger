package com.griddynamics.jagger.webclient.server;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * @author asokol
 *         created 9/21/16
 */
@PropertySources({
        @PropertySource("classpath:webclient.properties")
//        , @PropertySource("file://${jagger.webclient.properties")
})
@Configuration
public class PersistenceConfig {

    @Value("${jdbc.driver}")
    String driverClassName;

    @Value("${jdbc.url}")
    String url;

    @Value("${jdbc.user}")
    String userName;

    @Value("${password}")
    String password;


    @Bean
    public static PropertyPlaceholderConfigurer configurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setSearchSystemEnvironment(true);
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return configurer;
    }

    @Bean(destroyMethod = "close")
    public BasicDataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(driverClassName);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(userName);
        basicDataSource.setPassword(password);
        basicDataSource.setInitialSize(5);
        basicDataSource.setMaxActive(20);
        basicDataSource.setMaxIdle(25);
        basicDataSource.setMinIdle(2);
        basicDataSource.setPoolPreparedStatements(true);
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("SELECT 1");
        return basicDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        bean.setPersistenceUnitName("jagger");
        return bean;
    }

}
