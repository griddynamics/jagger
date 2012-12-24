package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.MonitoringFeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.StdDevMonitoringParameterDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.ThroughputWorkloadDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.WorkloadFeatureComparator;
//import com.sun.xml.internal.ws.api.FeatureConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 30.11.12
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class ComparatorDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String type=element.getAttribute(XMLConstants.DECISION_MAKER_TYPE);

        if(element.getAttribute(XMLConstants.COMPARATOR_TYPE).equals(XMLConstants.WORKLOAD)){
            builder.setParentName(XMLConstants.WORKLOAD_FEATURE_COMPARATOR);
            if(StringUtils.hasText(type)){
                BeanDefinition decisionMaker=getThroughputWorkloadDecisionMaker(element);
                builder.addPropertyValue(XMLConstants.WORKLOAD_DECISION_MAKER,decisionMaker);
            } else{
                builder.addPropertyReference(XMLConstants.WORKLOAD_DECISION_MAKER,element.getAttribute(XMLConstants.DECISION_MAKER_REF));
            }
        } else if(element.getAttribute(XMLConstants.COMPARATOR_TYPE).equals(XMLConstants.MONITORING)){
            builder.setParentName(XMLConstants.MONITORING_FEATURE_COMPARATOR);
            if(StringUtils.hasText(type)){
                BeanDefinition decisionMaker=getStdDevMonitoringParameterDecisionMaker(element);
                builder.addPropertyValue(XMLConstants.MONITORING_PARAMETER_DECISION_MAKER,decisionMaker);
            } else{
                builder.addPropertyReference(XMLConstants.MONITORING_PARAMETER_DECISION_MAKER,element.getAttribute(XMLConstants.DECISION_MAKER_REF));
            }
        }
    }

    private BeanDefinition getStdDevMonitoringParameterDecisionMaker(Element element){
        BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(StdDevMonitoringParameterDecisionMaker.class);
        String fatal=element.getAttribute(XMLConstants.FATAL_DEVIATION_THRESHOLD);
        String warning=element.getAttribute(XMLConstants.WARNING_DEVIATION_THRESHOLD);
        builder.addPropertyValue(XMLConstants.FATAL_DEVIATION_THRESHOLD,fatal);
        builder.addPropertyValue(XMLConstants.WARNING_DEVIATION_THRESHOLD, warning);
        return builder.getBeanDefinition();
    }

    private BeanDefinition getThroughputWorkloadDecisionMaker(Element element){
        BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(ThroughputWorkloadDecisionMaker.class);
        String fatal=element.getAttribute(XMLConstants.FATAL_DEVIATION_THRESHOLD);
        String warning=element.getAttribute(XMLConstants.WARNING_DEVIATION_THRESHOLD);
        builder.addPropertyValue(XMLConstants.FATAL_DEVIATION_THRESHOLD,fatal);
        builder.addPropertyValue(XMLConstants.WARNING_DEVIATION_THRESHOLD,warning);
        return builder.getBeanDefinition();
    }


}