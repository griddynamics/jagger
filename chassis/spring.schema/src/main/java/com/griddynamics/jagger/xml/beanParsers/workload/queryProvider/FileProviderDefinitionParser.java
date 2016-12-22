/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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

import com.griddynamics.jagger.providers.FileProvider;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * @author Nikolay Musienko
 *         Date: 23.04.13
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class FileProviderDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return FileProvider.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Preconditions.checkArgument(element.hasAttribute("path"));
        builder.addConstructorArgValue(element.getAttribute("path"));

        if(element.hasAttribute("delimeter")) {
            builder.addPropertyValue("delimeter", element.getAttribute("delimeter"));
        }
        List childes =  parseCustomListElement(element, parserContext, builder.getBeanDefinition());
        if(childes!= null && childes.size() > 0) {
            builder.addPropertyValue("objectCreator", childes.get(0));
        }
    }
}
