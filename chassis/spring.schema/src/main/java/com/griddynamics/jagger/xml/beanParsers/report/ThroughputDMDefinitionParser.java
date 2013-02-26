package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.StdDevMonitoringParameterDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.ThroughputWorkloadDecisionMaker;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/25/13
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThroughputDMDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ThroughputWorkloadDecisionMaker.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String fatal=element.getAttribute(XMLConstants.FATAL_DEVIATION_THRESHOLD);
        String warning=element.getAttribute(XMLConstants.WARNING_DEVIATION_THRESHOLD);
        if(!fatal.isEmpty()) builder.addPropertyValue(XMLConstants.FATAL_DEVIATION_THRESHOLD,fatal);
        if(!fatal.isEmpty()) builder.addPropertyValue(XMLConstants.WARNING_DEVIATION_THRESHOLD, warning);
    }
}
