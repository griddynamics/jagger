package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.FixedDelay;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class UserGroupsDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return UserGroupsClockConfiguration.class;
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.getAttribute(XMLConstants.TICK_INTERVAL).isEmpty()){
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, XMLConstants.DEFAULT_TICK_INTERVAL);
        }
        if (!element.getAttribute(XMLConstants.DELAY).isEmpty()){
            builder.addPropertyValue(XMLConstants.DELAY, new FixedDelay(Integer.parseInt(element.getAttribute(XMLConstants.DELAY))));
        }
        element.removeAttribute(XMLConstants.DELAY);
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.User.class.getCanonicalName());
        setBeanListProperty(XMLConstants.USERS, false, false, element, parserContext, builder.getBeanDefinition());
    }
}
