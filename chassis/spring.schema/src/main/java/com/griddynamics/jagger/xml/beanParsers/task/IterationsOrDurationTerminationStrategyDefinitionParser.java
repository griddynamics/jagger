package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


public class IterationsOrDurationTerminationStrategyDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return IterationsOrDurationStrategyConfiguration.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(XMLConstants.DURATION)){
            builder.addPropertyValue(XMLConstants.ITERATIONS, -1);
            builder.addPropertyValue(XMLConstants.DURATION, element.getAttribute(XMLConstants.DURATION));
        }else{
            if (element.hasAttribute(XMLConstants.MAX_DURATION)){
                builder.addPropertyValue(XMLConstants.DURATION, element.getAttribute(XMLConstants.MAX_DURATION));
            }else{
                builder.addPropertyValue(XMLConstants.DURATION, null);
            }
            builder.addPropertyValue(XMLConstants.ITERATIONS, element.getAttribute(XMLConstants.ITERATIONS));
        }
    }
}
