package com.griddynamics.jagger.dbapi.util;

import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.MonitoringIdUtils;

import java.util.*;

/**
 * Created by kgribov on 3/17/14.
 */
public class CommonUtils {

    /** Filter metrics to find standard monitoring metrics and dismiss not used monitoring metrics
     *
     * @param origin - a list of such arrays: [(String)metricId, ... , ...]
     * @param monitoringPlotGroups - monitoringPlotGroups from reporting.conf.xml
     * @return metrics without unused monitoring metrics
     */
    public static List<Object[]> filterMonitoring(List<Object[]> origin, Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups){
        List<Object[]> result = new ArrayList<Object[]>(origin.size());

        // monitoring ids in reporting
        Set<String> reportingMonitoring = new HashSet<String>();
        for (DefaultMonitoringParameters[] value : monitoringPlotGroups.values()){
            for (DefaultMonitoringParameters parameter : value){
                reportingMonitoring.add(parameter.getId());
            }
        }

        // all monitoring ids
        Set<String> allMonitoring = new HashSet<String>();
        for (DefaultMonitoringParameters parameter : DefaultMonitoringParameters.values()){
            allMonitoring.add(parameter.getId());
        }

        for (Object[] row : origin){
            String metricId = getMonitoringId((String)row[0]);
            if (allMonitoring.contains(metricId) && !reportingMonitoring.contains(metricId)){
                // ignore this metrics
                continue;
            }
            result.add(row);
        }

        return result;
    }

    private static String getMonitoringId(String origin){
        MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(origin);
        if (monitoringId != null) {
            return monitoringId.getMonitoringName();
        }

        return origin;
    }


    /**
     * @param tests - a list of TaskDataDtos
     * @return a set of TaskDataDto's ids
     */
    public static Set<Long> getTestsIds(List<TaskDataDto> tests){
        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
        }
        return taskIds;
    }

    /** Return true if origin collection contains at least one element from another collection
     * @param origin - origin collection
     * @param elements - another collection
     */
    public static boolean containsAtLeastOne(Collection origin, Collection elements){
        boolean result = false;
        for (Object element : elements){
            if (origin.contains(element)){
                result = true;
                break;
            }
        }

        return result;
    }


    /**
     * generate unique id from given parameters
     * @param params list of parameters
     * @return unique id
     */
    public static int generateUniqueId(List<String> params) {
        int result = 0;
        for (String param : params) {
            result = 31 * result + param.hashCode();
        }
        return result;
    }

}
