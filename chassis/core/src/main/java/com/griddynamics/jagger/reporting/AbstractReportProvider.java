package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.master.SessionIdProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

//TODO: replace obsoleted HibernateDaoSupport by direct Hibernate session manipulations

/**
 * AbstractReportProvider provides support for concrete providers.
 * Implementation must provide correct getDataSource() implementation.
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/21/12
 */
public abstract class AbstractReportProvider extends HibernateDaoSupport implements ReportProvider {
    private SessionIdProvider sessionIdProvider;
    private ReportingContext context;
    private String template;

    //==========Constructors

    public AbstractReportProvider() {
    }

    //==========Getters & Setters


    public SessionIdProvider getSessionIdProvider() {
        return sessionIdProvider;
    }

    public void setSessionIdProvider(SessionIdProvider sessionIdProvider) {
        this.sessionIdProvider = sessionIdProvider;
    }

    /**
     * Returns JasperReports report layout name
     *
     * @return report layout name
     */
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Return compiled JasperReports report
     *
     * @return compiled report
     */
    @Override
    public JasperReport getReport() {
        return context.getReport(template);
    }

    @Override
    public void setContext(ReportingContext context) {
        this.context = context;
    }

    protected ReportingContext getContext() {
        return context;
    }

    /**
     * Returns JRDataSource for JasperReports report filling
     *
     * @return dataSource
     */
    @Override
    public abstract JRDataSource getDataSource();
}
