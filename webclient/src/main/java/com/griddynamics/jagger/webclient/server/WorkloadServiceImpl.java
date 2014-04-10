package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.entity.WorkloadDetails;
import com.griddynamics.jagger.webclient.client.WorkloadService;
import com.griddynamics.jagger.webclient.client.dto.WorkloadDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@Deprecated
public class WorkloadServiceImpl /*extends RemoteServiceServlet*/ implements WorkloadService {
    private static final Logger log = LoggerFactory.getLogger(WorkloadServiceImpl.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<WorkloadDetailsDto> getWorkloadDetailsForSession(List<String> sessionIds) {
        List<WorkloadDetailsDto> workloadDetailsDtoList;

        @SuppressWarnings("unchecked")
        List<WorkloadDetails> workloadDetailsList = (List<WorkloadDetails>) entityManager.createQuery(
                "select distinct workloadData.scenario from WorkloadData as workloadData where workloadData.sessionId in (:sessionIds)").setParameter("sessionIds", sessionIds).getResultList();

        if (workloadDetailsList == null) {
            return Collections.emptyList();
        }

        workloadDetailsDtoList = new ArrayList<WorkloadDetailsDto>(workloadDetailsList.size());
        for (WorkloadDetails workloadDetails : workloadDetailsList) {
            workloadDetailsDtoList.add(new WorkloadDetailsDto(workloadDetails.getId(), workloadDetails.getName()));
        }

        log.info("WorkloadDetails for session id = {} are: {}", sessionIds, workloadDetailsDtoList);

        return workloadDetailsDtoList;
    }
}
