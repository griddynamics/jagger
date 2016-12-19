package com.griddynamics.jagger.xml.beanParsers.workload.scenario;

import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class QueryPoolScenarioDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return QueryPoolScenarioFactory.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        //parse invoker
        Element invokerElement = DomUtils.getChildElementByTagName(element, XMLConstants.INVOKER);
        setBeanProperty(XMLConstants.INVOKER_CLAZZ, invokerElement, parserContext, builder.getBeanDefinition());

        //parse balancer
        Element balancerElement = DomUtils.getChildElementByTagName(element, XMLConstants.QUERY_DISTRIBUTOR);
        setBeanProperty(XMLConstants.LOAD_BALANCER, balancerElement, parserContext, builder.getBeanDefinition());

        //parse endpointProvider
        Element endpointProviderElement = DomUtils.getChildElementByTagName(element, XMLConstants.ENDPOINT_PROVIDER_ELEMENT);
        setBeanProperty(XMLConstants.ENDPOINT_PROVIDER, endpointProviderElement, parserContext, builder.getBeanDefinition());

        //parse queryProvider
        Element queryProviderElement = DomUtils.getChildElementByTagName(element, XMLConstants.QUERY_PROVIDER_ELEMENT);
        setBeanProperty(XMLConstants.QUERY_PROVIDER, queryProviderElement, parserContext, builder.getBeanDefinition());

    }

}
