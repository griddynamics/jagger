package com.griddynamics.jagger.test.javabuilders.config;

import com.griddynamics.jagger.test.javabuilders.JaasSmokeTest;
import com.griddynamics.jagger.test.javabuilders.JaggerSmokeTest;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JLoadScenariosConfig {
    
    @Bean
    public JLoadScenario jaasSmokeTest() {
        return new JaasSmokeTest().getHttpScenario();
    }

    @Bean
    public JLoadScenario jaagerSmokeTest() {
        return new JaggerSmokeTest().getJaggerScenario();
    }

}
