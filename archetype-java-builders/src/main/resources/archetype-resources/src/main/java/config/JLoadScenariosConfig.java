package ${package}.config;

import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import ${package}.ExampleJLoadScenarioProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Java config for @{@link com.griddynamics.jagger.user.test.configurations.JLoadScenario} instances.
 *
 * Created by Andrey Badaev
 * Date: 18/11/16
 */
@Configuration
public class JLoadScenariosConfig {
    
    @Bean
    public JLoadScenario firstJaggerLoadScenario() {
        return ExampleJLoadScenarioProvider.getFirstJaggerLoadScenario();
    }
    
    @Bean
    public JLoadScenario exampleJaggerLoadScenario() {
        return ExampleJLoadScenarioProvider.getExampleJaggerLoadScenario();
    }
}
