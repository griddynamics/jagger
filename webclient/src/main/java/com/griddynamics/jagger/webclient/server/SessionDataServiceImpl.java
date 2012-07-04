package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Preconditions.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class SessionDataServiceImpl /*extends RemoteServiceServlet*/ implements SessionDataService {
    private static final Logger log = LoggerFactory.getLogger(SessionDataServiceImpl.class);
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PagedSessionDataDto getAll(int start, int length) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");

        long timestamp = System.currentTimeMillis();
        long totalSize;
        List<SessionDataDto> sessionDataDtoList;
        totalSize = (Long) entityManager.createQuery("select count(sessionData.id) from SessionData as sessionData").getSingleResult();

        @SuppressWarnings("unchecked")
        List<SessionData> sessionDataList = (List<SessionData>)
                entityManager.createQuery("select sd from SessionData as sd order by sd.startTime asc").setFirstResult(start).setMaxResults(length).getResultList();

        if (sessionDataList == null) {
            return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
        }

        sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
        DateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        for (SessionData sessionData : sessionDataList) {
            sessionDataDtoList.add(new SessionDataDto(
                    sessionData.getSessionId(),
                    dateFormatter.format(sessionData.getStartTime()),
                    dateFormatter.format(sessionData.getEndTime()),
                    sessionData.getActiveKernels(),
                    sessionData.getTaskExecuted(),
                    sessionData.getTaskFailed())
            );
        }

        log.info("There was loaded {} sessions data from {} for {} ms", new Object[]{sessionDataDtoList.size(), totalSize, System.currentTimeMillis() - timestamp});

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }

    @Override
    public SessionDataDto getBySessionId(String sessionId) {
        checkNotNull(sessionId, "sessionId is null");

        long timestamp = System.currentTimeMillis();

        SessionDataDto sessionDataDto = null;
        try {
            SessionData sessionData = (SessionData) entityManager.createQuery("select sd from SessionData as sd where sd.sessionId = (:sessionId)").setParameter("sessionId", sessionId).getSingleResult();

            DateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            sessionDataDto = new SessionDataDto(
                    sessionData.getSessionId(),
                    dateFormatter.format(sessionData.getStartTime()),
                    dateFormatter.format(sessionData.getEndTime()),
                    sessionData.getActiveKernels(),
                    sessionData.getTaskExecuted(),
                    sessionData.getTaskFailed()
            );
            log.info("There was loaded session data with id {} for {} ms", sessionId, System.currentTimeMillis() - timestamp);
        } catch (NoResultException e) {
            log.info("No session data was found for session ID=" + sessionId, e);
            return null;
        } catch (Exception e) {
            log.error("Error was occurred during session data with id=" + sessionId + " loading", e);
            throw new RuntimeException(e);
        }

        return sessionDataDto;
    }

    @Override
    public PagedSessionDataDto getByDatePeriod(int start, int length, Date from, Date to) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(from, "from is null");
        checkNotNull(to, "to is null");

        long timestamp = System.currentTimeMillis();

        long totalSize;
        List<SessionDataDto> sessionDataDtoList;
        try {
            totalSize = (Long) entityManager.createQuery("select count(sd.id) from SessionData as sd where sd.startTime between :from and :to")
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getSingleResult();

            @SuppressWarnings("unchecked")
            List<SessionData> sessionDataList = (List<SessionData>)
                    entityManager.createQuery("select sd from SessionData as sd where sd.startTime between :from and :to order by sd.startTime asc")
                            .setParameter("from", from)
                            .setParameter("to", to)
                            .setFirstResult(start)
                            .setMaxResults(length)
                            .getResultList();

            if (sessionDataList == null) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
            DateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(new SessionDataDto(
                        sessionData.getSessionId(),
                        dateFormatter.format(sessionData.getStartTime()),
                        dateFormatter.format(sessionData.getEndTime()),
                        sessionData.getActiveKernels(),
                        sessionData.getTaskExecuted(),
                        sessionData.getTaskFailed())
                );
            }

            log.info("There was loaded {} sessions data from {} for {} ms", new Object[]{sessionDataDtoList.size(), totalSize, System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during session data between " + from + " to " + to + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }

    @Override
    public PagedSessionDataDto getBySessionIds(int start, int length, Set<String> sessionIds) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(sessionIds, "sessionIds is null");

        long timestamp = System.currentTimeMillis();

        long totalSize;
        List<SessionDataDto> sessionDataDtoList;

        try {
            totalSize = (Long) entityManager.createQuery("select count(sd.id) from SessionData as sd where sd.sessionId in (:sessionIds)")
                    .setParameter("sessionIds", new ArrayList<String>(sessionIds))
                    .getSingleResult();

            @SuppressWarnings("unchecked")
            List<SessionData> sessionDataList = (List<SessionData>)
                    entityManager.createQuery("select sd from SessionData as sd where sd.sessionId in (:sessionIds) order by sd.startTime asc")
                            .setParameter("sessionIds", new ArrayList<String>(sessionIds))
                            .setFirstResult(start)
                            .setMaxResults(length)
                            .getResultList();

            if (sessionDataList == null) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
            DateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(new SessionDataDto(
                        sessionData.getSessionId(),
                        dateFormatter.format(sessionData.getStartTime()),
                        dateFormatter.format(sessionData.getEndTime()),
                        sessionData.getActiveKernels(),
                        sessionData.getTaskExecuted(),
                        sessionData.getTaskFailed())
                );
            }

            log.info("There was loaded {} sessions data from {} for {} ms", new Object[]{sessionDataDtoList.size(), totalSize, System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during session data fetching for session Ids " + sessionIds + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }
}
