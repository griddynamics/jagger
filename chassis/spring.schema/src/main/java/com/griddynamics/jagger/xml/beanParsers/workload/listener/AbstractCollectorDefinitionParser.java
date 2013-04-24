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
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
/**
 * @author Nikolay Musienko
 *         Date: 22.03.13
 */
public abstract class AbstractCollectorDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    protected abstract void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);

    @Override
    protected Class getBeanClass(Element element) {
        if(isComposite(element)){
            return CompositeMetricCollectorProvider.class;
        } else {
            return DiagnosticCollectorProvider.class;
        }
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if(isComposite(element)){
            parseCompositeBean(element, parserContext, builder);
        } else{
            parse(element, parserContext, builder);
        }
    }

    private void parseCompositeBean(Element element, ParserContext parserContext, BeanDefinitionBuilder builder){
        BeanDefinitionBuilder diagnosticCollectorBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(DiagnosticCollectorProvider.class);
        parse(element, parserContext, diagnosticCollectorBuilder);
        String diagnosticName="diagnostic-" + getID(element,XMLConstants.DEFAULT_METRIC_NAME);
        parserContext.getRegistry().registerBeanDefinition(diagnosticName,diagnosticCollectorBuilder.getBeanDefinition());

        BeanDefinitionBuilder metricCollectorBuilder =
                BeanDefinitionBuilder.rootBeanDefinition (MetricCollectorProvider.class);
        parse(element, parserContext, metricCollectorBuilder);
        String metricName="metric-" + getID(element,XMLConstants.DEFAULT_METRIC_NAME);
        parserContext.getRegistry().registerBeanDefinition(metricName, metricCollectorBuilder.getBeanDefinition());

        builder.addPropertyValue("diagnosticCollectorProvider", new RuntimeBeanReference(diagnosticName));
        builder.addPropertyValue("metricCollectorProvider", new RuntimeBeanReference(metricName));
    }

    private boolean isComposite(Element element){
        return element.getAttribute(XMLConstants.PLOT_DATA).equals("true");
    }


    /**
     * Returns attribute ID.
     * @param element - element
     * @param defaultString - default return string
     * @return ID. If ID is empty - returns default string.
     */
    protected String getID(Element element, String defaultString){
        if(element.getAttribute(XMLConstants.ID).isEmpty()){
            return defaultString;
        }
        return element.getAttribute(XMLConstants.ID);
    }
}
