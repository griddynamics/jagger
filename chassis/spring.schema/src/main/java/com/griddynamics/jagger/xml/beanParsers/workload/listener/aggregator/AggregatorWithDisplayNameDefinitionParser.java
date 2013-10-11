package com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator;

import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * User: amikryukov
 * Date: 10/9/13
 */
public abstract class AggregatorWithDisplayNameDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(XMLConstants.DISPLAY_NAME)) {
            String displayName = element.getAttribute(XMLConstants.DISPLAY_NAME);
            builder.addPropertyValue(XMLConstants.DISPLAY_NAME, displayName);
        }
    }
}
