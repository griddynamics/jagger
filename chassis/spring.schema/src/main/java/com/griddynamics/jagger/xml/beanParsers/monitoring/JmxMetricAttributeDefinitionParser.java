package com.griddynamics.jagger.xml.beanParsers.monitoring;

import com.griddynamics.jagger.agent.model.JmxMetricAttribute;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/20/13
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class JmxMetricAttributeDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return JmxMetricAttribute.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.NAME, element.getTextContent());
    }
}
