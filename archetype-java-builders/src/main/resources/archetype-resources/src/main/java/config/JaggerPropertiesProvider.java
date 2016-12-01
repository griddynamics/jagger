package ${package}.config;

import com.griddynamics.jagger.util.JaggerXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * This class is needed to provide Jagger environment properties.
 * It must be injected to class where properties are needed, or this class can extend JaggerPropertiesProvider.
 * In both cases that class must be a valid spring bean.
 */
public class JaggerPropertiesProvider {

    @Autowired
    private ApplicationContext context;

    public String getPropertyValue(String key) {
        return ((JaggerXmlApplicationContext) context).getEnvironmentProperties().getProperty(key);
    }
}
