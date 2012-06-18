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

    public String getPlotHeader(long taskId, String plotName) {
        Object[] legendData;
        legendData = (Object[]) entityManager.createQuery("select td.sessionId, td.taskName from TaskData as td where td.id=:taskId").
                setParameter("taskId", taskId).getSingleResult();

        return generatePlotHeader(legendData[0].toString(), legendData[1].toString(), plotName);
    }

    public String getPlotHeader(Set<Long> taskIds, String plotName) {
        @SuppressWarnings("unchecked")
        List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery("select td from TaskData as td where td.id in (:taskIds)").
                setParameter("taskIds", taskIds).getResultList();

        return generatePlotHeader(taskDataList, plotName);
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

    private String generatePlotHeader(List<TaskData> taskDataList, String plotName) {
        if (taskDataList == null || taskDataList.isEmpty()) {
            return plotName;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Session ");
        for (TaskData taskData : taskDataList) {
            builder.append("#").append(taskData.getSessionId()).append(", ");
        }

        builder
                .append(taskDataList.iterator().next().getTaskName())
                .append(", ")
                .append(plotName);

        return builder.toString();
    }
}
