package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;


public class TaskDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        if (!DomUtils.getChildElementsByTagName(element, XMLConstants.USER).isEmpty()) {
            setBeanListProperty(XMLConstants.USERS, false, element, parserContext, builder.getBeanDefinition());
        } else {
            if(DomUtils.getChildElementByTagName(element, XMLConstants.TPS) != null){
                setBeanProperty(XMLConstants.TPS, DomUtils.getChildElementByTagName(element, XMLConstants.TPS), parserContext, builder.getBeanDefinition());
            }else{
                if(DomUtils.getChildElementByTagName(element, XMLConstants.VIRTUAL_USER) != null){
                    setBeanProperty(XMLConstants.VIRTUAL_USER_CLASS_FIELD, DomUtils.getChildElementByTagName(element, XMLConstants.VIRTUAL_USER), parserContext, builder.getBeanDefinition());
                }else{
                    setBeanProperty(XMLConstants.INVOCATION, DomUtils.getChildElementByTagName(element, XMLConstants.INVOCATION), parserContext, builder.getBeanDefinition());
                }
            }
        }
    }
}
