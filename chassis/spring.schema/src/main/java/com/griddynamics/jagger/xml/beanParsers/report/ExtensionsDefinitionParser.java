package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 29.11.12
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class ExtensionsDefinitionParser implements BeanDefinitionParser{

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        List <Element> extensionElements = DomUtils.getChildElementsByTagName(element, XMLConstants.EXTENSION);
        for(Element el: extensionElements){
            parserContext.getDelegate().parseCustomElement(el);
        }
        return null;
    }
}
