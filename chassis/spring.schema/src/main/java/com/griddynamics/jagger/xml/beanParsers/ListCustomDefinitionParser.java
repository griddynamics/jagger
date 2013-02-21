package com.griddynamics.jagger.xml.beanParsers;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/1/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListCustomDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ArrayList.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(parseCustomListElement(element, parserContext, builder.getBeanDefinition()));
    }
}
