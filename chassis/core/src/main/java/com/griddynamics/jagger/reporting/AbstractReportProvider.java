package com.griddynamics.jagger.reporting;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;

//TODO: replace obsoleted HibernateDaoSupport by direct Hibernate session manipulations

/**
 * AbstractReportProvider provides support for concrete providers.
 * Implementation must provide correct getDataSource() implementation.
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/21/12
 */
public abstract class AbstractReportProvider extends AbstractReportProviderBean implements ReportProvider {

    //==========Constructors

    public AbstractReportProvider() {
    }

    //==========Getters & Setters

    /**
     * Return compiled JasperReports report
     *
     * @return compiled report
     */
    @Override
    public JasperReport getReport() {
        return getContext().getReport(getTemplate());
    }

    /**
     * Returns JRDataSource for JasperReports report filling
     *
     * @return dataSource
     */
    @Override
    public abstract JRDataSource getDataSource();
}
