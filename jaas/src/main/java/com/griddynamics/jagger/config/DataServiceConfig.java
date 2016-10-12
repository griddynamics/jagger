package com.griddynamics.jagger.config;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring config for {@link com.griddynamics.jagger.engine.e1.services.DataService}.
 */
@PropertySources({@PropertySource("classpath:/basic/default.environment.properties")})
@ImportResource({"classpath:/common/hibernate.conf.xml", "classpath:/spring/dbapi.config.xml"})
@Configuration
public class DataServiceConfig {
    
    @Autowired
    DatabaseService databaseService;
    
    @Bean
    public DataService dataService() {
        return new DefaultDataService(databaseService);
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
