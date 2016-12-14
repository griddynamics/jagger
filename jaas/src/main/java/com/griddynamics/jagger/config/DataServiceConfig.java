package com.griddynamics.jagger.config;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.jaas.service.ReportingServiceFactory;
import com.griddynamics.jagger.reporting.ReportingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring config for {@link com.griddynamics.jagger.engine.e1.services.DataService}.
 */
@PropertySources({@PropertySource("classpath:/basic/default.environment.properties"),
                  @PropertySource("classpath:/reporter/default.reporting.properties"),
                  @PropertySource("classpath:/jagger.properties")
                 })
@ImportResource({"classpath:/common/hibernate.conf.xml", "classpath:/reporter/reporting.conf.xml",
                 "classpath:/reporter/session.comparison.conf.xml"
                })
@Configuration
public class DataServiceConfig {
    
    @Autowired
    DatabaseService databaseService;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public DataService dataService() {
        return new DefaultDataService(databaseService);
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ReportingServiceFactory reportingServiceFactory(@Autowired ReportingContext context,
                    @Value("${chassis.master.reporting.root.report.template.location}") String rootTemplateLocation
    ) {
        return new ReportingServiceFactory(context, rootTemplateLocation);
    }
}
