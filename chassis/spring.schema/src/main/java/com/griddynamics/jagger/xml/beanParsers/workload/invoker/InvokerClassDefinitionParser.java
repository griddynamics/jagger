package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class InvokerClassDefinitionParser implements BeanDefinitionParser {

    protected BeanDefinition getClassNameBeanDefinition(Class clazz){
        BeanDefinitionBuilder className = BeanDefinitionBuilder.genericBeanDefinition(String.class);
        className.addConstructorArgValue(clazz.getCanonicalName());
        return  className.getBeanDefinition();
    }
}
