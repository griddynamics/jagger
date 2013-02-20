package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.master.configuration.Configuration;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/30/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentConfigurationReportProvider implements ReportingServiceProvider{

    private String configName;

    @Required
    public void setConfigurationName(String configName){
        this.configName = configName;
    }

    @Override
    public ReportingService getReportingService(Configuration configuration, ApplicationContext context) {
        String defaultService = "reportingService";
        if (configuration.getReport() != null){
            return configuration.getReport();
        }else{
            return (ReportingService)context.getBean(getReportBeanName());
        }
    }

    protected String getReportBeanName(){
        return configName+"-report";
    }
}
