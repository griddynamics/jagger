package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.user.TestGroupConfiguration;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

//Test-Group
public class TestDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return TestGroupConfiguration.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, TestConfiguration.class.getCanonicalName());

        List<Element> tests = DomUtils.getChildElementsByTagName(element, XMLConstants.TEST);
        builder.addPropertyValue(XMLConstants.TESTS, parseCustomElements(tests, parserContext, builder.getBeanDefinition()));

        Element listenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.TEST_GROUP_LISTENERS);
        if (listenerGroup != null){
            List<Element> listenerElements = DomUtils.getChildElements(listenerGroup);
            List listeners = parseCustomElements(listenerElements, parserContext, builder.getBeanDefinition());
            builder.addPropertyValue(XMLConstants.LISTENERS, listeners);
        }
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String id = element.getAttribute(XMLConstants.ID);
        builder.addPropertyValue(XMLConstants.NAME, id);
    }
}
