package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;

import javax.persistence.*;
import java.util.List;

/**
 * @author Sergey Pulyaev
 * Date: 24.10.12
 */
@Entity
public class DurationMetricStatistics {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "durationMetricStatistic")
    private List<DurationMetricLatencyPercentile> percentiles;

    @ManyToOne
    private TaskData taskData;

    @Column
    private String name;

    @Column
    private int count;

    @Column
    private long maxTime;

    @Column
    private long minTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DurationMetricLatencyPercentile> getPercentiles() {
        return percentiles;
    }

    public void setPercentiles(List<DurationMetricLatencyPercentile> percentiles) {
        this.percentiles = percentiles;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public int getCount() {
        return count;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinTime() {
        return minTime;
    }
}
