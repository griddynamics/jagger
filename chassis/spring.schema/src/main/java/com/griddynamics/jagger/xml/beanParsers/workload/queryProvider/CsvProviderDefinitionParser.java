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

package com.griddynamics.jagger.xml.beanParsers.workload.queryProvider;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.providers.CsvProvider;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.apache.commons.csv.CSVStrategy;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author Nikolay Musienko
 *         Date: 23.04.13
 */

public class CsvProviderDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return CsvProvider.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Preconditions.checkArgument(element.hasAttribute("path"));
        builder.addPropertyValue("path", element.getAttribute("path"));

        if(element.hasAttribute("readHeader")){
            builder.addPropertyValue("readHeader", element.getAttribute("readHeader").equals("true"));
        }
        if(element.hasAttribute("strategy")){
            if(element.getAttribute("strategy").equals("DEFAULT")){
                builder.addPropertyValue("strategy", CSVStrategy.DEFAULT_STRATEGY);
            } else if(element.getAttribute("strategy").equals("EXCEL")){
                builder.addPropertyValue("strategy", CSVStrategy.EXCEL_STRATEGY);
            } else if(element.getAttribute("strategy").equals("TDF")){
                builder.addPropertyValue("strategy", CSVStrategy.TDF_STRATEGY);
            } else {
                throw new TechnicalException("Strategy '" + element.getAttribute("strategy") + "' not found!");
            }
        }
        List childes =  parseCustomListElement(element, parserContext, builder.getBeanDefinition());
        Preconditions.checkState(childes!=null, "Must specify objectCreator in CSVProvider");
        builder.addPropertyValue("objectCreator", childes.get(0));
    }
}
