package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.util.FetchUtil;

import java.util.*;

/**
 * Created by kgribov on 3/17/14.
 */
public class CustomTestGroupMetricPlotFetcher extends CustomMetricPlotFetcher {
    private FetchUtil fetchUtil;

    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    @Override
    protected List<Object[]> getRawData(Set<Long> taskIds, Set<String> metricIds) {

        if (taskIds.isEmpty() || metricIds.isEmpty()) {
            log.warn("Empty data for getRawData() method {}; {}" + taskIds, metricIds);
            return Collections.emptyList();
        }

        List<Object[]> resultList = new ArrayList<Object[]>();

        Multimap<Long, Long> testGroupMap = fetchUtil.getTestsInTestGroup(taskIds);

        if (testGroupMap.isEmpty()) {
            log.warn("Could not find testGroupTaskData for workloadTask ids {} ", taskIds);
            return Collections.emptyList();
        }
        Collection<? extends Object[]> testGroupValues = getPlotDataNewModel(testGroupMap.keySet(), metricIds);

        for (Object[] row : testGroupValues){
            Long testGroupId = (Long) row[0];
            for (Long testId : testGroupMap.get(testGroupId)){
                resultList.add(new Object[]{testId, row[1], row[2], row[3], row[4]});
            }
        }

        return resultList;
    }
}
