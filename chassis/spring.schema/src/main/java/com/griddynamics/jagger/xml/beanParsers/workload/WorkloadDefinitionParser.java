package com.griddynamics.jagger.xml.beanParsers.workload;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/21/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadDefinitionParser extends CustomBeanDefinitionParser{

    @Override
    protected Class getBeanClass(Element element) {
        return WorkloadTask.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //parse listeners
        ManagedList listeners = new ManagedList();

        //add standard listeners
        for (String listenerBeanName : XMLConstants.STANDARD_WORKLOAD_LISTENERS){
            listeners.add(new RuntimeBeanReference(listenerBeanName));
        }
        builder.addPropertyValue(XMLConstants.WORKLOAD_LISTENERS_CLASS, listeners);

        //add user's listeners
        Element listenersGroup = DomUtils.getChildElementByTagName(element, XMLConstants.WORKLOAD_LISTENERS_ELEMENT);
        setBeanListProperty(XMLConstants.WORKLOAD_LISTENERS_CLASS, true, listenersGroup, parserContext, builder.getBeanDefinition());

        //add scenario
        Element scenarioElement = DomUtils.getChildElementByTagName(element, XMLConstants.SCENARIO);
        setBeanProperty(XMLConstants.SCENARIO_FACTORY, scenarioElement, parserContext, builder.getBeanDefinition());

        //add calibration
        Element calibrationElement = DomUtils.getChildElementByTagName(element, XMLConstants.CALIBRATOR);
        setBeanProperty(XMLConstants.CALIBRATOR, calibrationElement, parserContext, builder.getBeanDefinition());
    }
}
