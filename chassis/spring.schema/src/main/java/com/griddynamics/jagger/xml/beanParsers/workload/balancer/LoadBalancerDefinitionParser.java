package com.griddynamics.jagger.xml.beanParsers.workload.balancer;

import com.griddynamics.jagger.invoker.RandomLoadBalancer;
import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public abstract class LoadBalancerDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        if (element.hasAttribute(XMLConstants.RANDOM_SEED))
            return RandomLoadBalancer.class;
        else
            return SimpleCircularLoadBalancer.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(XMLConstants.RANDOM_SEED)) {
            builder.addPropertyValue(XMLConstants.RANDOM_SEED, element.getAttribute(XMLConstants.RANDOM_SEED));
        }
    }
}