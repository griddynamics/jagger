package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;


public class TestDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.class.getCanonicalName());

        List<Element> list = DomUtils.getChildElements(element);
        ManagedList tasks = new ManagedList(list.size());

        for (Element el : list){
            if (!el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                tasks.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.ATTRIBUTE_REF)));
            }else{
                tasks.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
            }
        }

        builder.addPropertyValue(XMLConstants.TASKS,tasks);
    }
}
