package com.griddynamics.jagger.xml.beanParsers;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/4/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapCustomDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return HashMap.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ManagedMap map = new ManagedMap();
        PrimitiveDefinitionParser parser = new PrimitiveDefinitionParser();
        List<Element> paramElements = DomUtils.getChildElements(element);
        if (paramElements==null) return;
        for (Element paramElement : paramElements){
            String key = paramElement.getAttribute("key");
            Element valueElement = DomUtils.getChildElementByTagName(paramElement, "value");
            Object result = parser.parse(valueElement, parserContext);
            map.put(key, result);
        }
        builder.addConstructorArgValue(map);
    }
}
