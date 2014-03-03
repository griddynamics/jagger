package com.griddynamics.jagger.webclient.client.dto;

import java.util.Set;

/**
 * User: amikryukov
 * Date: 12/17/13
 */
public class SessionPlotNameDto extends MetricName {

    private Set<String> sessionIds;

    public SessionPlotNameDto() {}

    public SessionPlotNameDto(Set<String> sessionIds, String plotName) {
        this.sessionIds = sessionIds;
        this.metricName = plotName;
    }

    public Set<String> getSessionId() {
        return sessionIds;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    @Override
    public String toString() {
        return "SessionPlotNameDto{" +
                "sessionIds='" + sessionIds + '\'' +
                ", metricName=" + metricName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionPlotNameDto that = (SessionPlotNameDto) o;

        if (metricName != null ? !metricName.equals(that.metricName) : that.metricName != null) return false;
        if (sessionIds != null ? !sessionIds.equals(that.sessionIds) : that.sessionIds != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sessionIds != null ? sessionIds.hashCode() : 0;
        result = 31 * result + (metricName != null ? metricName.hashCode() : 0);
        return result;
    }
}
