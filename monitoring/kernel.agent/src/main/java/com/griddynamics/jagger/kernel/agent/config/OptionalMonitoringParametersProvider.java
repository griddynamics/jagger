package com.griddynamics.jagger.kernel.agent.config;

import com.griddynamics.jagger.agent.model.MonitoringParametersProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Vladimir Kondrashchenko
 */
public class OptionalMonitoringParametersProvider implements FactoryBean<MonitoringParametersProvider>, BeanFactoryAware {

    private BeanFactory beanFactory;
    private String optionalBeanName;
    private Class<? extends MonitoringParametersProvider> optionalBeanClassName;

    @Required
    public void setOptionalBeanClassName(Class<? extends MonitoringParametersProvider> optionalBeanClassName) {
        this.optionalBeanClassName = optionalBeanClassName;
    }

    @Required
    public void setOptionalBeanName(String optionalBeanName) {
        this.optionalBeanName = optionalBeanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public MonitoringParametersProvider getObject() throws Exception {
        return beanFactory.getBean(optionalBeanName, optionalBeanClassName);
    }

    @Override
    public Class<?> getObjectType() {
        return optionalBeanClassName;

    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
