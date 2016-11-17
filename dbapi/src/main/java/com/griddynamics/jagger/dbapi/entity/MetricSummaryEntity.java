package com.griddynamics.jagger.dbapi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class MetricSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total;

    @OneToOne
    private MetricDescriptionEntity metricDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
