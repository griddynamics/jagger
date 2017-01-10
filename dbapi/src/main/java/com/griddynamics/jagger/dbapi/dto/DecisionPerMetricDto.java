package com.griddynamics.jagger.dbapi.dto;


import com.griddynamics.jagger.dbapi.entity.DecisionPerMetricEntity;
import com.griddynamics.jagger.dbapi.entity.MetricDescriptionEntity;
import com.griddynamics.jagger.util.Decision;

/**
 * This class is used for /session/{id}/decision rest call in JaaS.
 * It represents a part of decision JSON - decision for metric.
 * It's the last element in JSON tree. Its parent element is {@link DecisionPerTestDto} or {@link DecisionPerTestGroupDto}.
 */
public class DecisionPerMetricDto {
    private Long id;
    private MetricDescriptionEntity metricDescription;
    private Decision decision;

    public DecisionPerMetricDto(DecisionPerMetricEntity decisionPerMetricEntity) {
        this.id = decisionPerMetricEntity.getId();
        this.metricDescription = decisionPerMetricEntity.getMetricDescriptionEntity();
        this.decision = Decision.valueOf(decisionPerMetricEntity.getDecision());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetricDescriptionEntity getMetricDescription() {
        return metricDescription;
    }

    public void setMetricDescription(MetricDescriptionEntity metricDescription) {
        this.metricDescription = metricDescription;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
