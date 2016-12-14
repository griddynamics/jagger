package com.griddynamics.jagger.agent;

import com.griddynamics.jagger.agent.model.MonitoringInfoService;
import com.griddynamics.jagger.util.GeneralInfoCollector;
import com.griddynamics.jagger.util.GeneralNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/30/13
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralAgentInfoCollector extends GeneralInfoCollector {

    private MonitoringInfoService monitoringInfoService;

    @Override
    public GeneralNodeInfo getGeneralNodeInfo() {
        GeneralNodeInfo generalNodeInfo = super.getGeneralNodeInfo();

        //Get system properties from all SUT's
        Map<String, Map<String, String>> props = monitoringInfoService.getSystemProperties();
        Map<String, String> resultProps = new HashMap<String, String>();

        String javaVerProp = "java.version";
        for (String identifier : props.keySet()){
            resultProps.put(getJmxPort(identifier)+";"+javaVerProp, props.get(identifier).get(javaVerProp));
        }
        generalNodeInfo.setProperties(resultProps);

        return generalNodeInfo;
    }

    private String getJmxPort(String identifier){
        String[] temp = identifier.split(":");
        return temp[temp.length-1];
    }

    public MonitoringInfoService getMonitoringInfoService() {
        return monitoringInfoService;
    }

    public void setMonitoringInfoService(MonitoringInfoService monitoringInfoService) {
        this.monitoringInfoService = monitoringInfoService;
    }
}
