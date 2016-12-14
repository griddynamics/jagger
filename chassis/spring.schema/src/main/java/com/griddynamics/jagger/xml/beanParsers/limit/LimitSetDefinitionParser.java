package com.griddynamics.jagger.xml.beanParsers.limit;

import com.griddynamics.jagger.engine.e1.collector.limits.LimitSet;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

@Deprecated
// TODO: GD 11/25/16 Should be removed as soon as limits with java configuration are implemented
public class LimitSetDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return LimitSet.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List<Element> limits = DomUtils.getChildElementsByTagName(element, XMLConstants.LIMIT);
        builder.addPropertyValue(XMLConstants.LIMITS, parseCustomElements(limits, parserContext, builder.getBeanDefinition()));
        // inject bean of baseline session provider
        builder.addPropertyReference("baselineSessionProvider","baselineSessionProvider");
        // inject config bean
        builder.addPropertyReference("limitSetConfig","limitSetConfig");
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String id = element.getAttribute(XMLConstants.ID);
        builder.addPropertyValue(XMLConstants.ID, id);
    }
}
