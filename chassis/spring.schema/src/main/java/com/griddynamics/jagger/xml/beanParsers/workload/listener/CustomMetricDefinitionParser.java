package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.MetricCollectorProvider;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/20/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class CustomMetricDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return MetricCollectorProvider.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //nothing to do yet
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(XMLConstants.ID)) {
            builder.addPropertyValue(XMLConstants.NAME, element.getAttribute(XMLConstants.ID));
        }

        builder.addPropertyValue(XMLConstants.METRIC_CALCULATOR, new RuntimeBeanReference(element.getAttribute(XMLConstants.CALCULATOR)));
        element.removeAttribute(XMLConstants.CALCULATOR);

        if (element.hasAttribute(XMLConstants.AGGREGATOR)) {
            builder.addPropertyValue(XMLConstants.METRIC_AGGREGATOR_PROVIDER,
                    new RuntimeBeanReference(getAggregatorBeanIdByAttribute(element.getAttribute(XMLConstants.AGGREGATOR))));
            element.removeAttribute(XMLConstants.AGGREGATOR);
        }
    }

    private static String getAggregatorBeanIdByAttribute(String aggregatorAttribute) {
        Aggregator aggregator = Aggregator.getByName(aggregatorAttribute);
        if (aggregator == null) {
            return aggregatorAttribute;
        }
        return aggregator.getBeanId();
    }

    private String getAttribute(Element element, String attributeName, String defaultValue){
        if (element.hasAttribute(attributeName)) {
            return element.getAttribute(attributeName);
        }
        return defaultValue;
    }

    private static enum Aggregator {
        SUM("sum" ,"sumMetricAggregatorProvider"), AVG("avg", "avgMetricAggregatorProvider"), STD_DEV("std_dev", "stdDevMetricAggregatorProvider");

        String name;
        String beanId;

        public String getBeanId() {
            return beanId;
        }

        public String getName() {
            return name;
        }

        private Aggregator(String name, String beanId){
            this.name = name;
            this.beanId = beanId;
        }

        public static Aggregator getByName(String name) {
            for (Aggregator aggr: values()) {
                if (aggr.getName().equals(name)) {
                    return aggr;
                }
            }
            return null;
        }
    }
}
