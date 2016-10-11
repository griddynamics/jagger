package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.MonitoringFeatureComparator;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * User: kgribov
 * Date: 2/25/13
 * Time: 5:02 PM
 */
public class MonitoringComparatorDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return MonitoringFeatureComparator.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setParentName(XMLConstants.MONITORING_FEATURE_COMPARATOR);
        CustomBeanDefinitionParser.setBeanProperty(XMLConstants.MONITORING_PARAMETER_DECISION_MAKER, DomUtils.getChildElements(element).get(0), parserContext, builder.getBeanDefinition());
    }
}
