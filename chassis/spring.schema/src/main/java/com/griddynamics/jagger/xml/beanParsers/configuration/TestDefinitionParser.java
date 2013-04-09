package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.user.TestGroupConfiguration;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

//Test-Group
public class TestDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return TestGroupConfiguration.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, TestConfiguration.class.getCanonicalName());
        setBeanListProperty(XMLConstants.TESTS, false, false, element, parserContext, builder.getBeanDefinition());
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String id = element.getAttribute(XMLConstants.ID);
        builder.addPropertyValue(XMLConstants.NAME, id);
    }
}
