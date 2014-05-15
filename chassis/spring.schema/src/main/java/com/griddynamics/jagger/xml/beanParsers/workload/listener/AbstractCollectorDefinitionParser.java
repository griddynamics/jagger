/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.util.TimeUnits;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.List;

/**
 * @author Nikolay Musienko
 *         Date: 22.03.13
 */
public abstract class AbstractCollectorDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

//      Use "XMLConstants.DEFAULT_METRIC_NAME" (No name)
//      If user have defined custom Id => use this name
//      If user have not defined custom Id & default aggregators are used => use default collector name

        String name = null;

        if (!element.getAttribute(XMLConstants.ID).isEmpty()) {
            name = element.getAttribute(XMLConstants.ID);
        }

        Boolean plotData = false;
        if (element.hasAttribute(XMLConstants.PLOT_DATA)) {
            plotData = Boolean.valueOf(element.getAttribute(XMLConstants.PLOT_DATA));
        }

        Boolean saveSummary = true;
        if (element.hasAttribute(XMLConstants.SAVE_SUMMARY)) {
            saveSummary = Boolean.valueOf(element.getAttribute(XMLConstants.SAVE_SUMMARY));
        }

        ManagedMap aggregatorsWithSettings = getAggregatorsWithSettings(element, parserContext, builder);

        if (aggregatorsWithSettings.isEmpty()) {
            for (MetricAggregatorProvider aggregatorProvider : getAggregators()) {
                aggregatorsWithSettings.put(aggregatorProvider, MetricAggregatorSettings.EMPTY_SETTINGS);
            }

            if (name == null) {
                name = getDefaultCollectorName();
            }
        }

        if (name == null) {
            name = XMLConstants.DEFAULT_METRIC_NAME;
        }

        builder.addPropertyValue(XMLConstants.NAME, name);

        String displayName = element.getAttribute(XMLConstants.DISPLAY_NAME);

        BeanDefinitionBuilder metricDescription = BeanDefinitionBuilder.genericBeanDefinition(MetricDescription.class);
        metricDescription.addConstructorArgValue(name);
        metricDescription.addPropertyValue(XMLConstants.NEED_PLOT_DATA, plotData);
        metricDescription.addPropertyValue(XMLConstants.NEED_SAVE_SUMMARY, saveSummary);
        metricDescription.addPropertyValue(XMLConstants.AGGREGATORS_WITH_SETTINGS, aggregatorsWithSettings);
        metricDescription.addPropertyValue(XMLConstants.DISPLAY_NAME, displayName.isEmpty() ? null : displayName);

        builder.addPropertyValue(XMLConstants.METRIC_DESCRIPTION, metricDescription.getBeanDefinition());

    }


    private MetricAggregatorSettings getAggregatorsSettings(Element element) {

        MetricAggregatorSettings settings = new MetricAggregatorSettings();
        if (element.hasAttribute(XMLConstants.NORMALIZE_BY)) {
            settings.setNormalizationBy(TimeUnits.valueOf(element.getAttribute(XMLConstants.NORMALIZE_BY)));
            element.removeAttribute(XMLConstants.NORMALIZE_BY);
        }
        if (element.hasAttribute(XMLConstants.POINT_COUNT)) {
            settings.setPointCount(Integer.valueOf(element.getAttribute(XMLConstants.POINT_COUNT)));
            element.removeAttribute(XMLConstants.POINT_COUNT);
        }
        if (element.hasAttribute(XMLConstants.POINT_INTERVAL)) {
            settings.setPointInterval(Integer.valueOf(element.getAttribute(XMLConstants.POINT_INTERVAL)));
            element.removeAttribute(XMLConstants.POINT_INTERVAL);
        }
        return settings;
    }

    private ManagedMap getAggregatorsWithSettings(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        ManagedMap map = new ManagedMap();

        List<Element> elements = DomUtils.getChildElements(element);
        if (elements != null && !elements.isEmpty()){
            for (Element el : elements){
                MetricAggregatorSettings settings = getAggregatorsSettings(el);

                Object aggregator;
                if (el.hasAttribute(XMLConstants.ATTRIBUTE_REF)) {
                    String ref = el.getAttribute(XMLConstants.ATTRIBUTE_REF);
                    if (!ref.isEmpty()) {
                        aggregator = new RuntimeBeanReference(ref);
                    } else {
                        aggregator = parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition());
                    }
                } else {
                    aggregator = parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition());
                }

                map.put(aggregator, settings);
            }
        }
        return map;
    }

    protected abstract Collection<MetricAggregatorProvider> getAggregators();

    protected String getDefaultCollectorName(){
        return XMLConstants.DEFAULT_METRIC_NAME;
    }
}
