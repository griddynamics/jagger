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

package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.engine.e1.reporting.PlotsReporter;
import com.griddynamics.jagger.engine.e1.reporting.SummaryReporter;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.extension.ExtensionRegistry;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportingContext implements ApplicationContextAware {
    
    public static final String CONTEXT_NAME = "context";
    
    private ResourceLoader resourceLoader;
    private String rootPath = "";
    private SummaryReporter summaryReporter;
    private PlotsReporter plotsReporter;
    private BaselineSessionProvider baselineSessionProvider;
    
    private
    ExtensionRegistry<ReportProvider> providerRegistry = new ExtensionRegistry<>(ReportProvider.class);
    private ExtensionRegistry<MappedReportProvider> mappedProviderRegistry =
            new ExtensionRegistry<>(MappedReportProvider.class);
    
    private Map<String, Object> parameters = Maps.newHashMap();
    
    private boolean removeFrame = true;
    
    public InputStream getResource(String location) {
        try {
            return resourceLoader.getResource(getPath(location)).getInputStream();
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }
    
    public Map<String, ReportingContext> getAsMap() {
        Map<String, ReportingContext> map = new HashMap<>();
        map.put(CONTEXT_NAME, this);
        return map;
    }
    
    public ReportProvider getProvider(String name) {
        return providerRegistry.getExtension(name);
    }
    
    public MappedReportProvider getMappedProvider(String name) {
        return mappedProviderRegistry.getExtension(name);
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public JasperReport getReport(String location) {
        try {
            return JasperCompileManager.compileReport(
                    new ReportInputStream(resourceLoader.getResource(getPath(location)).getInputStream(), removeFrame));
        } catch (JRException e) {
            throw new TechnicalException(e);
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }
    
    //------------------------------------------------------------------------------------------------------------------
    
    public void setProviderRegistry(ExtensionRegistry<ReportProvider> providerRegistry) {
        for (ReportProvider reportProvider : providerRegistry.getExtensions().values()) {
            reportProvider.setContext(this);
        }
        this.providerRegistry = providerRegistry;
    }
    
    public void setMappedProviderRegistry(ExtensionRegistry<MappedReportProvider> providerRegistry) {
        for (MappedReportProvider reportProvider : providerRegistry.getExtensions().values()) {
            reportProvider.setContext(this);
        }
        this.mappedProviderRegistry = providerRegistry;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        resourceLoader = applicationContext;
    }
    
    public boolean isRemoveFrame() {
        return removeFrame;
    }
    
    public void setRemoveFrame(boolean removeFrame) {
        this.removeFrame = removeFrame;
    }
    
    public String getRootPath() {
        return rootPath;
    }
    
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    private String getPath(String relativePath) {
        return rootPath + relativePath;
    }
    
    public SummaryReporter getSummaryReporter() {
        return summaryReporter;
    }
    
    @Required
    public void setSummaryReporter(SummaryReporter summaryReporter) {
        this.summaryReporter = summaryReporter;
    }
    
    public PlotsReporter getPlotsReporter() {
        return plotsReporter;
    }
    
    @Required
    public void setPlotsReporter(PlotsReporter plotsReporter) {
        this.plotsReporter = plotsReporter;
    }
    
    public BaselineSessionProvider getBaselineSessionProvider() {
        return baselineSessionProvider;
    }
    
    @Required
    public void setBaselineSessionProvider(BaselineSessionProvider baselineSessionProvider) {
        this.baselineSessionProvider = baselineSessionProvider;
    }
}