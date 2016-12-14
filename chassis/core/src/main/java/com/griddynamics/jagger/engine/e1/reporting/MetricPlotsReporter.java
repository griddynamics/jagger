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
package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author Nikolay Musienko
 *         Date: 19.03.13
 */
public class MetricPlotsReporter extends AbstractMappedReportProvider<String> {
    private Logger log = LoggerFactory.getLogger(MetricPlotsReporter.class);

    @Override
    public JRDataSource getDataSource(String id, String sessionId) {
    
        Map<Long, PlotsReporter.MetricPlotDTOs> testIdToPlotsMap =
                getContext().getPlotsReporter().getTestIdToPlotsMap(sessionId);

        Long testId = Long.valueOf(id);
        if (!testIdToPlotsMap.containsKey(testId)) {
            log.warn("No metrics plot data found for test with id {}", testId);
            return new JRBeanCollectionDataSource(Collections.emptyList());
        }

        return new JRBeanCollectionDataSource(Collections.singleton(testIdToPlotsMap.get(testId)));
    }
}
