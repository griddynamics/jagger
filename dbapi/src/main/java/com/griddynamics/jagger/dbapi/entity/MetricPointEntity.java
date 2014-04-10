package com.griddynamics.jagger.dbapi.entity;

import javax.persistence.*;

@Entity
public class MetricPointEntity {

    public static final int ALLOCATION_SIZE = 100;
    public static final String METRIC_ID = "MetricPointEntity_ID";

    @TableGenerator(name="GENERATOR",
            table="IdGeneratorEntity",

            pkColumnName="tableName",
            valueColumnName="idValue",
            pkColumnValue=METRIC_ID,

            //do not change allocationSize value, it will cause duplicated key problem
            allocationSize=ALLOCATION_SIZE,
            initialValue = 0
    )
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="GENERATOR")
    private Long id;

    @Column
    private long time;

    @Column
    private Double value;

    @ManyToOne
    private MetricDescriptionEntity metricDescription;


    public MetricPointEntity(long time, Double value, MetricDescriptionEntity metricDescription) {
        this.time = time;
        this.value = value;
        this.metricDescription = metricDescription;
    }

    public MetricPointEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public MetricDescriptionEntity getMetricDescription() {
        return metricDescription;
    }

    public void setMetricDescription(MetricDescriptionEntity metricDescription) {
        this.metricDescription = metricDescription;
    }

    public String getDisplay() {
        return metricDescription.getDisplay();
    }
}

