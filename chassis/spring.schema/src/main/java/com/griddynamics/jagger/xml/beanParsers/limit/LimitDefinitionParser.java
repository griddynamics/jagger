package com.griddynamics.jagger.xml.beanParsers.limit;

import com.griddynamics.jagger.engine.e1.collector.limits.Limit;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Deprecated
// TODO: GD 11/25/16 Should be removed as soon as limits with java configuration are implemented
public class LimitDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return Limit.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.LIMIT_METRIC_NAME, element.getAttribute(XMLConstants.LIMIT_METRIC_NAME));

        builder.addPropertyValue(XMLConstants.LIMIT_LWT_PROP, element.getAttribute(XMLConstants.LIMIT_LWT_TAG));
        builder.addPropertyValue(XMLConstants.LIMIT_UWT_PROP, element.getAttribute(XMLConstants.LIMIT_UWT_TAG));
        builder.addPropertyValue(XMLConstants.LIMIT_LET_PROP, element.getAttribute(XMLConstants.LIMIT_LET_TAG));
        builder.addPropertyValue(XMLConstants.LIMIT_UET_PROP, element.getAttribute(XMLConstants.LIMIT_UET_TAG));
        element.removeAttribute(XMLConstants.LIMIT_LWT_TAG);
        element.removeAttribute(XMLConstants.LIMIT_UWT_TAG);
        element.removeAttribute(XMLConstants.LIMIT_LET_TAG);
        element.removeAttribute(XMLConstants.LIMIT_UET_TAG);

        String description = element.getAttribute(XMLConstants.LIMIT_DESCRIPTION);
        if (description != null) {
            builder.addPropertyValue(XMLConstants.LIMIT_DESCRIPTION,description);
        }
        String refValue = element.getAttribute(XMLConstants.LIMIT_REFVALUE);
        if ((refValue != null) && (!refValue.equals(""))) {
            builder.addPropertyValue(XMLConstants.LIMIT_REFVALUE, refValue);
        }

    }
}
