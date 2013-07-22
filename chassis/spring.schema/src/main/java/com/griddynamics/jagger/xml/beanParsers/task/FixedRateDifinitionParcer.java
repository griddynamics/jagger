package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.FixedRateClockConfiguration;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class FixedRateDifinitionParcer  extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return FixedRateClockConfiguration.class;
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.getAttribute(XMLConstants.TICK_INTERVAL).isEmpty()) {
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, XMLConstants.DEFAULT_TICK_INTERVAL);
        } else {
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, element.getAttribute(XMLConstants.TICK_INTERVAL));
        }
        if (!element.getAttribute(XMLConstants.THREAD_COUNT).isEmpty()) {
            builder.addPropertyValue(XMLConstants.THREAD_COUNT, element.getAttribute(XMLConstants.THREAD_COUNT));
        } else {
            builder.addPropertyValue(XMLConstants.THREAD_COUNT, XMLConstants.DEFAULT_THREAD_COUNT);
        }
        if (element.getAttribute(XMLConstants.RATE).isEmpty()) {
            builder.addPropertyValue(XMLConstants.RATE, XMLConstants.DEFAULT_RATE);
        } else {
            builder.addPropertyValue(XMLConstants.RATE, element.getAttribute(XMLConstants.RATE));
        }
        if (!element.getAttribute(XMLConstants.DELAY).isEmpty()) {
            builder.addPropertyValue(XMLConstants.DELAY, element.getAttribute(XMLConstants.DELAY));
        }

    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }
}
