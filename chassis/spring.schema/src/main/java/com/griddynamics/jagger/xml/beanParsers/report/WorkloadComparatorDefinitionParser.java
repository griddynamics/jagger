package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.WorkloadFeatureComparator;
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
public class WorkloadComparatorDefinitionParser extends AbstractSimpleBeanDefinitionParser{
    @Override
    protected Class getBeanClass(Element element) {
        return WorkloadFeatureComparator.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setParentName(XMLConstants.WORKLOAD_FEATURE_COMPARATOR);
        CustomBeanDefinitionParser.setBeanProperty(XMLConstants.WORKLOAD_DECISION_MAKER, DomUtils.getChildElements(element).get(0), parserContext, builder.getBeanDefinition());
    }
}
