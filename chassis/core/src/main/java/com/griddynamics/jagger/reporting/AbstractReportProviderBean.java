package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.master.SessionIdProvider;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/21/12
 */
public abstract class AbstractReportProviderBean extends HibernateDaoSupport {
    private SessionIdProvider sessionIdProvider;
    private ReportingContext context;
    private String template;

    //==========Constructors

    protected AbstractReportProviderBean() {
    }

    protected AbstractReportProviderBean(String template, SessionIdProvider sessionIdProvider, ReportingContext context) {
        this.sessionIdProvider = sessionIdProvider;
        this.context = context;
        this.template = template;
    }

    //==========Getters & Setters

    public SessionIdProvider getSessionIdProvider() {
        return sessionIdProvider;
    }

    public void setSessionIdProvider(SessionIdProvider sessionIdProvider) {
        this.sessionIdProvider = sessionIdProvider;
    }

    public ReportingContext getContext() {
        return context;
    }

    @Required
    public void setContext(ReportingContext context) {
        this.context = context;
    }

    public String getTemplate() {
        return template;
    }

    @Required
    public void setTemplate(String template) {
        this.template = template;
    }
}
