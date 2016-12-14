package ${package}.util;

import com.griddynamics.jagger.util.JaggerXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * This class is needed to provide Jagger environment properties and test properties.<br>
 * It must be injected to class where properties are needed, or this class can extend JaggerPropertiesProvider.
 * In both cases that class must be a valid spring bean.<p>
 * Properties from test.properties do not override properties from environment.properties.
 */
@Configuration
@PropertySource("classpath:test.properties")
public class JaggerPropertiesProvider {

    @Autowired
    private ApplicationContext jaggerContext;

    @Autowired
    private Environment testEnv;

    public String getEnvPropertyValue(String key) {
        return ((JaggerXmlApplicationContext) jaggerContext).getEnvironmentProperties().getProperty(key);
    }

    public String getTestPropertyValue(String key) {
        return testEnv.getProperty(key);
    }
}
