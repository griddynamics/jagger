package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import java.util.List;


public class TaskDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        if (!DomUtils.getChildElementsByTagName(element, XMLConstants.USER).isEmpty()) {
            element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.User.class.getCanonicalName());
            List<Element> list = DomUtils.getChildElementsByTagName(element, XMLConstants.USER);
            ManagedList values = new ManagedList();
            for (Element el : list){
                if (el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                    values.add(parserContext.getDelegate().parseCustomElement(el, builder.getBeanDefinition()));
                }else{
                    values.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.ATTRIBUTE_REF)));
                }
            }
            builder.addPropertyValue(XMLConstants.USERS, values);
        } else {
            if(!DomUtils.getChildElementsByTagName(element, XMLConstants.TPS).isEmpty()){
                Element tps = DomUtils.getChildElementByTagName(element, XMLConstants.TPS);
                if (!tps.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                    builder.addPropertyReference(XMLConstants.TPS, tps.getAttribute(XMLConstants.ATTRIBUTE_REF));
                }else{
                    builder.addPropertyValue(XMLConstants.TPS,parserContext.getDelegate().parseCustomElement(tps, builder.getBeanDefinition()));
                }
            }else{
                if(!DomUtils.getChildElementsByTagName(element, XMLConstants.VIRTUAL_USER).isEmpty()){
                    Element vu = DomUtils.getChildElementByTagName(element, XMLConstants.VIRTUAL_USER);
                    if (!vu.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                        builder.addPropertyReference(XMLConstants.VIRTUAL_USER_CLASS_FIELD, vu.getAttribute(XMLConstants.ATTRIBUTE_REF));
                    }else{
                        builder.addPropertyValue(XMLConstants.VIRTUAL_USER_CLASS_FIELD, parserContext.getDelegate().parseCustomElement(vu, builder.getBeanDefinition()));
                    }
                }else{
                    if(!DomUtils.getChildElementsByTagName(element, XMLConstants.INVOCATION).isEmpty()){
                        Element inv = DomUtils.getChildElementByTagName(element, XMLConstants.INVOCATION);
                        if (!inv.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                            builder.addPropertyReference(XMLConstants.INVOCATION, inv.getAttribute(XMLConstants.ATTRIBUTE_REF));
                        }else{
                            builder.addPropertyValue(XMLConstants.INVOCATION,parserContext.getDelegate().parseCustomElement(inv, builder.getBeanDefinition()));
                        }
                    }
                }
            }
        }
    }
}
