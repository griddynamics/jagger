package com.griddynamics.jagger.xml.beanParsers.workload.queryProvider;

import com.griddynamics.jagger.invoker.http.HttpQuery;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/24/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class HttpQueryDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return HttpQuery.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Element clientParamsElement = DomUtils.getChildElementByTagName(element, XMLConstants.CLIENT_PARAMS_ELEMENT);
        Element methodParamsElement = DomUtils.getChildElementByTagName(element, XMLConstants.METHOD_PARAMS_ELEMENT);
        String method = element.getAttribute(XMLConstants.METHOD);

        builder.addPropertyValue(XMLConstants.METHOD, method);

        setBeanProperty(XMLConstants.CLIENT_PARAMS, clientParamsElement, parserContext, builder.getBeanDefinition());

        setBeanProperty(XMLConstants.METHOD_PARAMS, methodParamsElement, parserContext, builder.getBeanDefinition());
    }
}
