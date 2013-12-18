package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricCollectorProvider;
import com.griddynamics.jagger.engine.e1.collector.SumMetricAggregatorProvider;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/18/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCalculatorBasedDefinitionParser extends AbstractCollectorDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return MetricCollectorProvider.class;
    }

    protected abstract Object getMetricCalculator(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue(XMLConstants.METRIC_CALCULATOR, getMetricCalculator(element, parserContext, builder));
    }

    @Override
    protected Collection<MetricAggregatorProvider> getAggregators(){
        Collection<MetricAggregatorProvider> result = new ArrayList<MetricAggregatorProvider>(1);
        result.add(new SumMetricAggregatorProvider());

        return result;
    }
}
