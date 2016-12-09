package com.griddynamics.jagger.xml.beanParsers.workload;

import com.griddynamics.jagger.user.TestDescription;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/21/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDescriptionDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return TestDescription.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        builder.addPropertyValue(XMLConstants.DESCRIPTION, element.getAttribute(XMLConstants.ID));

        ManagedList metrics = new ManagedList();
        metrics.setMergeEnabled(true);

        ManagedList validators = new ManagedList();
        validators.setMergeEnabled(true);

        ManagedList listeners = new ManagedList();
        listeners.setMergeEnabled(true);

        ManagedList standardCollectors = new ManagedList();

        //add user's metrics and validators
        Element listenersGroup = DomUtils.getChildElementByTagName(element, XMLConstants.WORKLOAD_LISTENERS_ELEMENT);
        if (listenersGroup != null){
            List<Element> metricElements = DomUtils.getChildElementsByTagName(listenersGroup, XMLConstants.METRIC);
            if (metricElements != null && !metricElements.isEmpty())
                metrics.addAll(parseCustomElements(metricElements, parserContext, builder.getBeanDefinition()));

            List<Element> validatorsElements = DomUtils.getChildElementsByTagName(listenersGroup, XMLConstants.VALIDATOR);
            if (validatorsElements != null && !validatorsElements.isEmpty())
                validators.addAll(parseCustomElements(validatorsElements, parserContext, builder.getBeanDefinition()));

            List<Element> listenersElements = DomUtils.getChildElementsByTagName(listenersGroup, XMLConstants.INVOCATION_LISTENER);
            if (listenersElements != null && !listenersElements.isEmpty())
                listeners.addAll(parseCustomElements(listenersElements, parserContext, builder.getBeanDefinition()));
        }

        for (String standardCollector : XMLConstants.STANDARD_WORKLOAD_LISTENERS){
            standardCollectors.add(new RuntimeBeanReference(standardCollector));
        }

        builder.addPropertyValue(XMLConstants.VALIDATORS, validators);
        builder.addPropertyValue(XMLConstants.STANDARD_COLLECTORS, standardCollectors);
        builder.addPropertyValue(XMLConstants.METRICS, metrics);
        builder.addPropertyValue(XMLConstants.LISTENERS, listeners);

        //add scenario
        Element scenarioElement = DomUtils.getChildElementByTagName(element, XMLConstants.SCENARIO);
        setBeanProperty(XMLConstants.SCENARIO_FACTORY, scenarioElement, parserContext, builder.getBeanDefinition());
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        element.removeAttribute("calibration");
    }
}
