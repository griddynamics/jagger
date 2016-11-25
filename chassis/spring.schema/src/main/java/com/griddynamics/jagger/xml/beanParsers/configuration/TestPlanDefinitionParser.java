package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class TestPlanDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.class.getCanonicalName());
        setBeanListProperty(XMLConstants.TESTS, false, false, element, parserContext, builder.getBeanDefinition());
        setBeanProperty(XMLConstants.TEST_GROUP_LISTENERS, element, parserContext, builder.getBeanDefinition());
    }
}
