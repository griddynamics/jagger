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
package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;

@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class SuccessRateCollectorDefinitionParser extends AbstractCollectorDefinitionParser{

    @Override
    protected Class getBeanClass(Element element) {
        return SuccessRateCollectorProvider.class;
    }

    @Override
    protected Collection<MetricAggregatorProvider> getAggregators() {
        Collection<MetricAggregatorProvider> result = new ArrayList<MetricAggregatorProvider>(1);
        result.add(new SuccessRateAggregatorProvider());
        result.add(new SuccessRateFailsAggregatorProvider());

        return result;
    }

    @Override
    protected String getDefaultCollectorName() {
        return XMLConstants.DEFAULT_METRIC_SUCCESS_RATE_NAME;
    }
}
