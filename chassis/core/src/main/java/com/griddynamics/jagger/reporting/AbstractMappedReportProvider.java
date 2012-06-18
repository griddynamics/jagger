package com.griddynamics.jagger.reporting;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/21/12
 */
public abstract class AbstractMappedReportProvider<T> extends AbstractReportProviderBean implements MappedReportProvider<T> {
    @Override
    public abstract JRDataSource getDataSource(T key);

    @Override
    public JasperReport getReport(T key) {
        return getContext().getReport(getTemplate());
    }
}
