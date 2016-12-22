package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.engine.e1.Provider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Processor, which set Prototype scope to all Providers in spring context
 * @author kgribov
 */
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class ProviderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForType(Provider.class);
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            // if user doesn't use scope attribute, we set prototype scope as default
            if ("".equals(beanDefinition.getScope())){
                beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            }
        }
    }
}
