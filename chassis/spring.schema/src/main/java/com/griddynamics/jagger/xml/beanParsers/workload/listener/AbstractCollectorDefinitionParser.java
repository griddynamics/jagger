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
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
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

        List aggregators = CustomBeanDefinitionParser.parseCustomListElement(element, parserContext, builder.getBeanDefinition());
        List entries = new ManagedList();
        if (aggregators != null) {
            for (Object aggregator: aggregators) {
                entries.add(getAggregatorEntry(aggregator, plotData, saveSummary));
            }
        }
        if (entries.size() == 0) {
            for (MetricAggregatorProvider aggregatorProvider : getAggregators()){
                entries.add(getAggregatorEntry(aggregatorProvider, plotData, saveSummary));
            }

            if (name == null){
                name = getDefaultCollectorName();
            }
        }

        if (name == null){
            name = XMLConstants.DEFAULT_METRIC_NAME;
        }

        builder.addPropertyValue(XMLConstants.NAME, name);
        builder.addPropertyValue(XMLConstants.AGGREGATORS, entries);
    }

    protected abstract Collection<MetricAggregatorProvider> getAggregators();

    protected String getDefaultCollectorName(){
        return XMLConstants.DEFAULT_METRIC_NAME;
    }

    private BeanDefinition getAggregatorEntry(Object aggregatorProvider, boolean plotData, boolean saveSummary) {
        BeanDefinitionBuilder entry = BeanDefinitionBuilder.genericBeanDefinition(MetricCollectorProvider.MetricDescriptionEntry.class);
        entry.addPropertyValue(XMLConstants.NEED_PLOT_DATA, plotData);
        entry.addPropertyValue(XMLConstants.NEED_SAVE_SUMMARY, saveSummary);
        entry.addPropertyValue(XMLConstants.METRIC_AGGREGATOR_PROVIDER, aggregatorProvider);
        return entry.getBeanDefinition();
    }
}
