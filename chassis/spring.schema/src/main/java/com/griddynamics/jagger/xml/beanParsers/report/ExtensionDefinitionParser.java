package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.extension.ExtensionExporter;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 29.11.12
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class ExtensionDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ExtensionExporter.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String ref=element.getAttribute(XMLConstants.ATTRIBUTE_REF);
        if (!StringUtils.hasText(ref)){
            ref= DomUtils.getChildElementByTagName(element, XMLConstants.ATTRIBUTE_REF).getAttribute(XMLConstants.BEAN);
        }
        builder.addPropertyReference(XMLConstants.EXTENSION,ref);
        element.setAttribute(XMLConstants.ID,XMLConstants.EXTENSION_PREFIX+ref);
    }
}
