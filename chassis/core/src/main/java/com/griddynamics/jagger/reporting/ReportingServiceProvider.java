package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.master.configuration.Configuration;
import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/6/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReportingServiceProvider {
    public ReportingService getReportingService(Configuration configuration, ApplicationContext context);
}
