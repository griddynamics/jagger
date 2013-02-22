package com.griddynamics.jagger.xml.beanParsers.workload;

import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.SkipCalibration;
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

        //add user's listeners
        Element listenersGroup = DomUtils.getChildElementByTagName(element, XMLConstants.WORKLOAD_LISTENERS_ELEMENT);

        if (builder.getBeanDefinition().getParentName() == null){
            ManagedList listeners = new ManagedList();

            //add standard listeners
            for (String listenerBeanName : XMLConstants.STANDARD_WORKLOAD_LISTENERS){
                listeners.add(new RuntimeBeanReference(listenerBeanName));
            }
            builder.addPropertyValue(XMLConstants.WORKLOAD_LISTENERS_CLASS, listeners);

            if (listenersGroup != null){

                setBeanListProperty(XMLConstants.WORKLOAD_LISTENERS_CLASS, true, listenersGroup, parserContext, builder.getBeanDefinition());
            }
        }else{
            if (listenersGroup != null){

                ManagedList listeners = new ManagedList();

                //add standard listeners
                for (String listenerBeanName : XMLConstants.STANDARD_WORKLOAD_LISTENERS){
                    listeners.add(new RuntimeBeanReference(listenerBeanName));
                }

                builder.addPropertyValue(XMLConstants.WORKLOAD_LISTENERS_CLASS, listeners);

                setBeanListProperty(XMLConstants.WORKLOAD_LISTENERS_CLASS, true, listenersGroup, parserContext, builder.getBeanDefinition());
            }
        }


        //add scenario
        Element scenarioElement = DomUtils.getChildElementByTagName(element, XMLConstants.SCENARIO);
        setBeanProperty(XMLConstants.SCENARIO_FACTORY, scenarioElement, parserContext, builder.getBeanDefinition());
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Boolean calibration = false;
        if (!element.getAttribute(XMLConstants.CALIBRATION).isEmpty())
            calibration = Boolean.parseBoolean(element.getAttribute(XMLConstants.CALIBRATION));
        element.removeAttribute(XMLConstants.CALIBRATION);

        if (calibration)
            builder.addPropertyValue(XMLConstants.CALIBRATOR, new OneNodeCalibrator());
        else
            builder.addPropertyValue(XMLConstants.CALIBRATOR, new SkipCalibration());
    }
}
