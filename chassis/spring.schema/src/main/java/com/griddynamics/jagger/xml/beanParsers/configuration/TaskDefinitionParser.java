package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.util.Parser;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;


public class TaskDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return TestConfiguration.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        setBeanProperty(XMLConstants.LOAD, DomUtils.getChildElementByTagName(element, XMLConstants.LOAD), parserContext, builder.getBeanDefinition());
        setBeanProperty(XMLConstants.TERMINATION_STRATEGY, DomUtils.getChildElementByTagName(element, XMLConstants.TERMINATION), parserContext, builder.getBeanDefinition());
        setBeanProperty(XMLConstants.LISTENERS, DomUtils.getChildElementByTagName(element, XMLConstants.TEST_LISTENERS), parserContext, builder.getBeanDefinition());
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String id = element.getAttribute(XMLConstants.ID);
        builder.addPropertyValue(XMLConstants.NAME, id);
        String testDescription = element.getAttribute(XMLConstants.TEST_DESCRIPTION);
        if (!testDescription.isEmpty()) {
            builder.addPropertyValue(XMLConstants.TEST_DESCRIPTION_CLASS_FIELD, new RuntimeBeanReference(testDescription));
            element.removeAttribute(XMLConstants.TEST_DESCRIPTION);
        }

        if (element.hasAttribute(XMLConstants.START_DELAY_ATTRIBUTE)) {
            builder.addPropertyValue(XMLConstants.START_DELAY_ATTRIBUTE,
                    Parser.parseTimeMillis(element.getAttribute(XMLConstants.START_DELAY_ATTRIBUTE)));

            element.removeAttribute(XMLConstants.START_DELAY_ATTRIBUTE);
        }

    }
}
