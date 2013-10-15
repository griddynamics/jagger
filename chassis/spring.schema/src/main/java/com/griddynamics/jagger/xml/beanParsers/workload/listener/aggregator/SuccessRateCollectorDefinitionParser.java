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
package com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator;

import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.List;

public class SuccessRateCollectorDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return SuccessRateCollectorProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        String name = XMLConstants.DEFAULT_METRIC_NAME;
        // If user have defined name - we will use it
        if (!element.getAttribute(XMLConstants.ID).isEmpty())
            name = element.getAttribute(XMLConstants.ID);

        Boolean plotData = false;
        if (element.hasAttribute(XMLConstants.PLOT_DATA)) {
            plotData = Boolean.valueOf(element.getAttribute(XMLConstants.PLOT_DATA));
        }
        Boolean saveSummary = true;
        if (element.hasAttribute(XMLConstants.SAVE_SUMMARY)) {
            saveSummary = Boolean.valueOf(element.getAttribute(XMLConstants.SAVE_SUMMARY));
        }

        List aggregators = CustomBeanDefinitionParser.parseCustomListElement(element, parserContext, builder.getBeanDefinition());
        List entries = new ManagedList();
        if (aggregators != null) {
            for (Object aggregator: aggregators) {
                entries.add(getAggregatorEntry(aggregator, plotData, saveSummary));
            }
        }
        if (entries.size() == 0) {
            entries.add(getAggregatorEntry(new SuccessRateAggregatorProvider(), plotData, saveSummary));
            entries.add(getAggregatorEntry(new SuccessRateFailsAggregatorProvider(), plotData,saveSummary));

            // If user have not defined any name
            // And we are using metric with default aggregators => we will use default name
            if (name.equals(XMLConstants.DEFAULT_METRIC_NAME) == true)
                name = XMLConstants.DEFAULT_METRIC_SUCCESS_RATE_NAME;
        }

        builder.addPropertyValue(XMLConstants.NAME, name);
        builder.addPropertyValue(XMLConstants.AGGREGATORS, entries);
    }

    private BeanDefinition getAggregatorEntry(Object aggregatorProvider, boolean plotData, boolean saveSummary) {
        BeanDefinitionBuilder entry = BeanDefinitionBuilder.genericBeanDefinition(SuccessRateCollectorProvider.MetricDescriptionEntry.class);
        entry.addPropertyValue(XMLConstants.NEED_PLOT_DATA, plotData);
        entry.addPropertyValue(XMLConstants.NEED_SAVE_SUMMARY, saveSummary);
        entry.addPropertyValue(XMLConstants.METRIC_AGGREGATOR_PROVIDER, aggregatorProvider);
        return entry.getBeanDefinition();
    }
}
