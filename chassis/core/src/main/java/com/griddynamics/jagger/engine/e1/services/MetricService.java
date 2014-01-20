package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.collector.MetricDescription;

/** Service gives an ability to create and describe metrics, save metric values.
 * @author Gribov Kirill
 * @n
 * @ingroup Main_Services_group */
 public interface MetricService extends JaggerService{

    /** Creates metric
     * @author Gribov Kirill
     * @n
     * @par Details:
     * @details Registers in Jagger a metric define by \e metricDescription. Registration (creation) should be provided single time. @n
     * After metric is created you can save some test data to it with help of @ref saveValue
     * @n
     * @par Example:
     * @dontinclude  ProviderOfTestListener.java
     * @skip  begin: following section is used for docu generation - example of metric creation
     * @until end: following section is used for docu generation - example of metric creation
     * @n
     *
     * @param metricDescription - describes how to store metric */
    void createMetric(MetricDescription metricDescription);

    /** Saves metric value during test run
     * @author Gribov Kirill
     * @n
     * @param metricId - metric id
     * @param value - metric value*/
    void saveValue(String metricId, Number value);

    /** Saves metric value with specific timestamp during test run
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
