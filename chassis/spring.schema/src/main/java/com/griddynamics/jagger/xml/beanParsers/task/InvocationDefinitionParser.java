package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.ExactInvocationsClockConfiguration;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


public class InvocationDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ExactInvocationsClockConfiguration.class;
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.getAttribute(XMLConstants.TICK_INTERVAL).isEmpty()){
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, XMLConstants.DEFAULT_TICK_INTERVAL);
        }
        if (element.getAttribute(XMLConstants.DELAY).isEmpty()){
            builder.addPropertyValue(XMLConstants.DELAY, 0);
        }
        if (!element.getAttribute(XMLConstants.PERIOD).isEmpty()){
            builder.addPropertyValue(XMLConstants.PERIOD, element.getAttribute(XMLConstants.PERIOD));
        }
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
