package com.griddynamics.jagger.xml.beanParsers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/29/13
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimitiveDefinitionParser implements BeanDefinitionParser {


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String typeOfElement = element.getSchemaTypeInfo().getTypeName();
        Class type = getClassByType(typeOfElement);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(type);
        builder.addConstructorArgValue(parserContext.getDelegate().parseValueElement(element, null));
        return builder.getBeanDefinition();
    }

    protected static Class getClassByType(String type){
        if (type.equals("integer")){
            return Integer.class;
        }else
        if (type.equals("double")){
            return Double.class;
        }else
        if (type.equals("long")){
            return Long.class;
        }else
        if (type.equals("boolean")){
            return Boolean.class;
        }else
        if (type.equals("float")){
            return Float.class;
        }else
            return String.class;
    }
}
