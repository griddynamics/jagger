package com.griddynamics.jagger.xml.beanParsers.workload.balancer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBalancerDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Class balancerClass = getBalancerClass();
        return BeanDefinitionBuilder.genericBeanDefinition(balancerClass).getBeanDefinition();
    }

    public abstract Class getBalancerClass();
}
