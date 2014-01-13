package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.collector.MetricDescription;

/** A kind of service, that gives an ability to create and describe metrics, save metric values.
 * @author Gribov Kirill
 * @n
 * */
public interface MetricService extends JaggerService{

    /** Creates metric. You can define metric name, metric aggregators
     * @author Gribov Kirill
     * @n
     *
     * @param metricDescription - describes how to store metric */
    void createMetric(MetricDescription metricDescription);

    /** Saves metric value
     * @author Gribov Kirill
     * @n
     * @param metricId - metric id
     * @param value - metric value*/
    void saveValue(String metricId, Number value);

    /** Saves metric value with specific timestamp
     * @author Gribov Kirill
     * @n
     * @param metricId - metric id
     * @param value - metric value
     * @param timeStamp - value timestamp*/
    void saveValue(String metricId, Number value, long timeStamp);

    /** Writes all values to file system
     * @author Grid Dynamics
     * @n*/
    void flush();
}
