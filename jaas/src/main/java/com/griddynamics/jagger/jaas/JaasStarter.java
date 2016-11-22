package com.griddynamics.jagger.jaas;

import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot based starter.
 */
@EnableConfigurationProperties(DbConfigEntity.class)
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
@EnableAsync
@EnableScheduling
public class JaasStarter {
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(JaasStarter.class, args);
    }
}
