package com.griddynamics.jagger.webclient.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TagEntity;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.dto.*;
import org.slf4j.Logger;
import com.griddynamics.jagger.webclient.client.dto.TagDto;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
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
    private final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    private CommonDataServiceImpl commonDataService;
    private EntityManager entityManager;
    private DataSaverServiceImpl dataSaverService;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        // have it's own Entity manager to store data.
        this.entityManager = entityManager;

    }

    public void setCommonDataService(CommonDataServiceImpl commonDataService) {
        this.commonDataService = commonDataService;
    }

    public void setDataSaverService(DataSaverServiceImpl dataSaverService) {
        this.dataSaverService = dataSaverService;
    }

    @Override
    public List<TagDto> getAllTags() {

        List<TagDto> allTags = new ArrayList<TagDto>();
        if (commonDataService.getWebClientProperties().isTagsStoreAvailable()) {
            List<TagEntity> tags = entityManager.createQuery("select te from TagEntity as te").getResultList();

            if (!tags.isEmpty()) {
                for (TagEntity tagEntity : tags) {
                    allTags.add(new TagDto(tagEntity.getName(), tagEntity.getDescription()));
                }
            }
        }
        return allTags;
    }

    @Override
    public void saveTags(Long sessionData_id, List<TagDto> tags) {
        dataSaverService.saveTags(sessionData_id, tags);
    }

    @Override
    public void saveUserComment(Long sessionData_id, String userComment) throws RuntimeException {
        dataSaverService.saveUserComment(sessionData_id, userComment);
    }

    @Override
    public Long getStartPosition(Set<String> selectedIds) throws RuntimeException {
        if (selectedIds.isEmpty()){
            return 0L;
        }

        List<Date> startTimeList = (List<Date>)entityManager.createQuery("select ses.startTime from SessionData ses where ses.sessionId in (:sessionIds) order by ses.startTime asc")
                                        .setMaxResults(1)
                                        .setParameter("sessionIds", selectedIds).getResultList();

        if (startTimeList.isEmpty()){
            return 0L;
        }

        Date startTime = startTimeList.iterator().next();

        Long lastPosition = (Long)entityManager.createQuery("select count(ses.id) from SessionData ses where startTime<=:startTime").setParameter("startTime", startTime).getSingleResult();

        return lastPosition - 1;
    }

    @Override
    public PagedSessionDataDto getAll(int start, int length) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");

        long timestamp = System.currentTimeMillis();
        long totalSize;
        List<SessionDataDto> sessionDataDtoList;
        totalSize = (Long) entityManager.createQuery("select count(sessionData.id) from SessionData as sessionData").getSingleResult();

        try {
            sessionDataDtoList = getAllWithMetaData(start, length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (sessionDataDtoList.isEmpty()) {
            return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
        }

        log.info("There was loaded {} sessions data from {} for {} ms", new Object[]{sessionDataDtoList.size(), totalSize, System.currentTimeMillis() - timestamp});

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }

    private List<SessionDataDto> getAllWithMetaData(int start, int length) {

        @SuppressWarnings("unchecked")
        List<SessionData> sessionDataList = (List<SessionData>)
                entityManager.createQuery("select sd from SessionData as sd order by sd.startTime asc").setFirstResult(start).setMaxResults(length).getResultList();

        if (sessionDataList == null || sessionDataList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Long> sessionIds = new ArrayList<Long>();
        for (int i = 0; i < sessionDataList.size(); i++) {
            sessionIds.add(sessionDataList.get(i).getId());
        }

        Map<Long, String> userCommentMap = Collections.EMPTY_MAP;
        if (commonDataService.getWebClientProperties().isUserCommentStoreAvailable()) {
            List<Object[]> userComments = entityManager.createQuery(
                    "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                    .setParameter("sessionDataList", sessionDataList)
                    .getResultList();

            if (!userComments.isEmpty()) {
                userCommentMap = new HashMap<Long, String>(userComments.size());
                for (Object[] objects : userComments) {
                    userCommentMap.put((Long) objects[0], (String) objects[1]);
                }
            }

        }
        Multimap<Long, TagDto> tagMap = HashMultimap.create();
        if (commonDataService.getWebClientProperties().isTagsStoreAvailable()) {
            List<Object[]> sessionTags = entityManager.createNativeQuery("select a.sessions_id, a.tags_name, te.description " +
                    "from  TagEntity as te, (select distinct ste.sessions_id, ste.tags_name from SessionTagEntity as ste where ste.sessions_id in (:sessionIds)) as a " +
                    "where a.tags_name=te.name")
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();
            for (Object[] tags : sessionTags) {
                Long sessionId = ((BigInteger) tags[0]).longValue();
                tagMap.put(sessionId, new TagDto((String) tags[1], (String) tags[2]));
            }
        }

        List<SessionDataDto> sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());

        for (SessionData sessionData : sessionDataList) {
            sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()), new ArrayList<TagDto>(tagMap.get(sessionData.getId()))));
        }

        return sessionDataDtoList;
    }

    @Override
    public SessionDataDto getBySessionId(String sessionId) {
        checkNotNull(sessionId, "sessionId is null");

        long timestamp = System.currentTimeMillis();

        SessionDataDto sessionDataDto;
        try {
            SessionData sessionData = (SessionData) entityManager.createQuery("select sd from SessionData as sd where sd.sessionId = (:sessionId)").setParameter("sessionId", sessionId).getSingleResult();

            String userComment = null;
            List<TagDto> sessionTagsDto = Collections.EMPTY_LIST;

            if (commonDataService.getWebClientProperties().isUserCommentStoreAvailable()) {
                try {
                    userComment = (String) entityManager.createQuery(
                            "select smd.userComment from SessionMetaDataEntity as smd where smd.sessionData.sessionId=:sessionId")
                            .setParameter("sessionId", sessionId)
                            .getSingleResult();
                } catch (NoResultException e) {
                    // no user comment for this session
                } catch (PersistenceException e) {
                    log.warn("Could not fetch data from SessionMetaDataEntity", e);
                }
            }
            Multimap<Long, TagDto> tagMap = HashMultimap.create();
            if (commonDataService.getWebClientProperties().isTagsStoreAvailable()) {
                List<Object[]> sessionTags = entityManager.createNativeQuery("select a.sessions_id, a.tags_name, te.description " +
                        "from  TagEntity as te, (select distinct ste.sessions_id, ste.tags_name from SessionTagEntity as ste where ste.sessions_id=:sessionIds) as a " +
                        "where a.tags_name=te.name")
                        .setParameter("sessionId", sessionId)
                        .getResultList();
                for (Object[] tags : sessionTags) {
                    tagMap.put(((BigInteger) tags[0]).longValue(), new TagDto((String) tags[1], (String) tags[2]));
                }
            }


            sessionDataDto = createSessionDataDto(sessionData, userComment, new ArrayList<TagDto>(tagMap.get(sessionData.getId())));

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

            if (sessionDataList.isEmpty()) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            List<Long> sessionIds = new ArrayList<Long>();
            for (int i = 0; i < sessionDataList.size(); i++) {
                sessionIds.add(sessionDataList.get(i).getId());
            }
            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (commonDataService.getWebClientProperties().isUserCommentStoreAvailable()) {
                List<Object[]> userComments = entityManager.createQuery(
                        "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                        .setParameter("sessionDataList", sessionDataList)
                        .getResultList();

                if (!userComments.isEmpty()) {
                    userCommentMap = new HashMap<Long, String>(userComments.size());
                    for (Object[] objects : userComments) {
                        userCommentMap.put((Long) objects[0], (String) objects[1]);
                    }
                }
            }
            Multimap<Long, TagDto> tagMap = HashMultimap.create();

            if (commonDataService.getWebClientProperties().isTagsStoreAvailable()) {
                List<Object[]> sessionTags = entityManager.createNativeQuery("select a.sessions_id, a.tags_name, te.description " +
                        "from  TagEntity as te, (select distinct ste.sessions_id, ste.tags_name from SessionTagEntity as ste where ste.sessions_id in (:sessionIds)) as a " +
                        "where a.tags_name=te.name")
                        .setParameter("sessionIds", sessionIds)
                        .getResultList();
                for (Object[] tags : sessionTags) {
                    Long sessionId = ((BigInteger) tags[0]).longValue();
                    tagMap.put(sessionId, new TagDto((String) tags[1], (String) tags[2]));
                }
            }

            sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()), new ArrayList<TagDto>(tagMap.get(sessionData.getId()))));
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

            if (sessionDataList.isEmpty()) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (commonDataService.getWebClientProperties().isUserCommentStoreAvailable()) {

                List<Object[]> userComments = entityManager.createQuery(
                        "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                        .setParameter("sessionDataList", sessionDataList)
                        .getResultList();

                if (!userComments.isEmpty()) {
                    userCommentMap = new HashMap<Long, String>(userComments.size());
                    for (Object[] objects : userComments) {
                        userCommentMap.put((Long) objects[0], (String) objects[1]);
                    }
                }
            }
            Multimap<Long, TagDto> tagMap = HashMultimap.create();

            Set<Long> ids = new HashSet<Long>();
            for (SessionData sd : sessionDataList) {
                ids.add(sd.getId());
            }


            if (commonDataService.getWebClientProperties().isTagsStoreAvailable()) {
                List<Object[]> sessionTags = entityManager.createNativeQuery("select a.sessions_id, a.tags_name, te.description " +
                        "from  TagEntity as te, (select distinct ste.sessions_id, ste.tags_name from SessionTagEntity as ste where ste.sessions_id in (:ids)) as a " +
                        "where a.tags_name=te.name")
                        .setParameter("ids", ids)
                        .getResultList();
                for (Object[] tags : sessionTags) {
                    Long sessionId = ((BigInteger) tags[0]).longValue();
                    tagMap.put(sessionId, new TagDto((String) tags[1], (String) tags[2]));
                }
            }
            sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()), new ArrayList<TagDto>(tagMap.get(sessionData.getId()))));
            }

            log.info("There was loaded {} sessions data from {} for {} ms", new Object[]{sessionDataDtoList.size(), totalSize, System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during session data fetching for session Ids " + sessionIds + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }

    @Override
    public PagedSessionDataDto getBySessionTagsName(int start, int length, Set<String> sessionTagNames) {
        if (!commonDataService.getWebClientProperties().isTagsStoreAvailable())
            return new PagedSessionDataDto(Collections.EMPTY_LIST, 0);
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(sessionTagNames, "sessionTagNames is null");

        long timestamp = System.currentTimeMillis();

        long totalSize;
        List<SessionDataDto> sessionDataDtoList;
        List<Long> sessionIds = new ArrayList<Long>();
        List<BigInteger> ids;

        try {
            totalSize = ((BigInteger) entityManager.createNativeQuery("select count(distinct ste.sessions_id) from SessionTagEntity as ste where ste.tags_name in (:sessionTagNames)")
                    .setParameter("sessionTagNames", new ArrayList<String>(sessionTagNames))
                    .getSingleResult()).longValue();
            if (totalSize == 0) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            ids = entityManager.createNativeQuery("select distinct sd.sessions_id from SessionTagEntity as sd where sd.tags_name in (:sessionTagNames)")
                    .setParameter("sessionTagNames", new ArrayList<String>(sessionTagNames)).getResultList();

            for (BigInteger id : ids) {
                sessionIds.add(id.longValue());
            }

            @SuppressWarnings("unchecked")
            List<SessionData> sessionDataList = (List<SessionData>) entityManager.createQuery("SELECT sd from SessionData as sd where sd.id in (:sessionIds)")
                    .setParameter("sessionIds", sessionIds)
                    .setFirstResult(start)
                    .setMaxResults(length)
                    .getResultList();

            if (sessionDataList.isEmpty()) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (commonDataService.getWebClientProperties().isUserCommentStoreAvailable()) {

                List<Object[]> userComments = entityManager.createQuery(
                        "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                        .setParameter("sessionDataList", sessionDataList)
                        .getResultList();

                if (!userComments.isEmpty()) {
                    userCommentMap = new HashMap<Long, String>(userComments.size());
                    for (Object[] objects : userComments) {
                        userCommentMap.put((Long) objects[0], (String) objects[1]);
                    }
                }
            }
            Map<Long, ArrayList<TagDto>> tagMap = Collections.EMPTY_MAP;

            if (commonDataService.getWebClientProperties().isTagsStoreAvailable()) {
                List<Object[]> sessionTags = entityManager.createNativeQuery("select a.sessions_id, a.tags_name, te.description " +
                        "from  TagEntity as te, (select distinct ste.sessions_id, ste.tags_name from SessionTagEntity as ste where ste.sessions_id in (:sessionIds)) as a " +
                        "where a.tags_name=te.name")
                        .setParameter("sessionIds", sessionIds)
                        .getResultList();
                tagMap = new HashMap<Long, ArrayList<TagDto>>();
                for (Object[] tags : sessionTags) {
                    if (!tagMap.containsKey(((BigInteger) tags[0]).longValue())) {
                        tagMap.put(((BigInteger) tags[0]).longValue(), new ArrayList<TagDto>());
                    }
                    tagMap.get(((BigInteger) tags[0]).longValue()).add(new TagDto((String) tags[1], (String) tags[2]));
                }

            }

            sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()), tagMap.get(sessionData.getId())));
            }

            log.info("There was loaded {} sessions data from {} for {} ms", new Object[]{sessionDataDtoList.size(), totalSize, System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during session data fetching for session tags " + sessionTagNames + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }


    private SessionDataDto createSessionDataDto(SessionData sessionData, String userComment, List<TagDto> tags) {
        return new SessionDataDto(
                sessionData.getId(),
                sessionData.getSessionId(),
                dateFormatter.format(sessionData.getStartTime()),
                dateFormatter.format(sessionData.getEndTime()),
                sessionData.getActiveKernels(),
                sessionData.getTaskExecuted(),
                sessionData.getTaskFailed(),
                HTMLFormatter.format(sessionData.getComment()),
                userComment,
                tags);
    }
}
