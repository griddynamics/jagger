package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LegendProvider {
    private EntityManager entityManager;

    public LegendProvider() {
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String getPlotHeader(Set<Long> taskIds, String plotName) {
        @SuppressWarnings("unchecked")
        List<String> sessionList = (List<String>) entityManager.createQuery("select distinct td.sessionId from TaskData as td where td.id in (:taskIds)").
                setParameter("taskIds", taskIds).getResultList();

        String taskName = (String) entityManager.createQuery("select td.taskName from TaskData as td where td.id=(:taskId)")
                .setParameter("taskId", taskIds.iterator().next())
                .getSingleResult();

        return generatePlotHeader(sessionList, taskName, plotName);
    }

    public String generatePlotLegend(String sessionId, String description, boolean addSessionPrefix) {
        if (!addSessionPrefix) {
            return description;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("#").append(sessionId).append(": ").append(description);

        return builder.toString();
    }

    public String generatePlotHeader(TaskData taskData, String plotName) {
        return generatePlotHeader(taskData.getSessionId(), taskData.getTaskName(), plotName);
    }


    //============================
    //===========Auxiliary Methods
    //============================

    private String generatePlotHeader(String sessionId, String taskName, String plotName) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Session #")
                .append(sessionId)
                .append(", ")
                .append(taskName)
                .append(", ")
                .append(plotName);

        return builder.toString();
    }

    private String generatePlotHeader(List<String> sessionIds, String taskName, String plotName) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return plotName;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Session ");
        for (String sessionId : sessionIds) {
            builder.append("#").append(sessionId).append(", ");
        }

        builder
                .append(taskName)
                .append(", ")
                .append(plotName);

        return builder.toString();
    }
}
