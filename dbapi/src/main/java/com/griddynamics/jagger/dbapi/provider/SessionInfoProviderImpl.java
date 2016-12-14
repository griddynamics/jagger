package com.griddynamics.jagger.dbapi.provider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.DataSaverService;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;
import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.dbapi.entity.TagEntity;
import com.griddynamics.jagger.dbapi.util.HTMLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kgribov on 4/7/14.
 */
@SuppressWarnings("unchecked")
@Component
public class SessionInfoProviderImpl implements SessionInfoProvider {
    private static final Logger log = LoggerFactory.getLogger(SessionInfoProviderImpl.class);

    private EntityManager entityManager;
    private DataSaverService dataSaverService;

    private boolean isUserCommentStorageAvailable = false;
    private boolean isTagsStorageAvailable = false;


    public void setIsUserCommentStorageAvailable(Boolean isUserCommentStorageAvailable) {
        this.isUserCommentStorageAvailable = isUserCommentStorageAvailable;
    }

    public void setIsTagsStorageAvailable(Boolean isTagsStorageAvailable) {
        this.isTagsStorageAvailable = isTagsStorageAvailable;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setDataSaverService(DataSaverService dataSaverService) {
        this.dataSaverService = dataSaverService;
    }

    public List<TagDto> getAllTags() {

        List<TagDto> allTags = new ArrayList<>();
        if (isTagsStorageAvailable) {
            List<TagEntity> tags = entityManager.createQuery("select te from TagEntity as te").getResultList();

            if (!tags.isEmpty()) {
                allTags.addAll(tags.stream().map(tagEntity -> new TagDto(tagEntity.getName(), tagEntity.getDescription()))
                        .collect(Collectors.toList()));
            }
        }
        return allTags;
    }

    public void saveTags(Long sessionDataId, List<TagDto> tags) {
        dataSaverService.saveTags(sessionDataId, tags);
    }

    public void saveUserComment(Long sessionDataId, String userComment) throws RuntimeException {
        dataSaverService.saveUserComment(sessionDataId, userComment);
    }

    public Long getTotalSize() throws RuntimeException {
        return (Long) entityManager.createQuery("select count(sessionData.id) from SessionData as sessionData").getSingleResult();
    }

    public Long getTotalSizeByDate(Date from, Date to) {
        return (Long) entityManager.createQuery("select count(sd.id) from SessionData as sd where sd.startTime between :from and :to")
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
    }

    public Long getTotalSizeByIds(Set<String> sessionIds) {
        return (Long) entityManager.createQuery("select count(sd.id) from SessionData as sd where sd.sessionId in (:sessionIds)")
                .setParameter("sessionIds", new ArrayList<>(sessionIds))
                .getSingleResult();
    }

    public Long getTotalSizeByTags(Set<String> sessionTagNames) {
        if (isTagsStorageAvailable) {
            return ((BigInteger) entityManager.createNativeQuery("SELECT count(DISTINCT ste.sessions_id) FROM SessionTagEntity AS ste WHERE ste" +
                    ".tags_name IN (:sessionTagNames)")
                    .setParameter("sessionTagNames", new ArrayList<>(sessionTagNames))
                    .getSingleResult()).longValue();
        } else {
            return 0L;
        }
    }

    public Long getFirstPosition(Set<String> selectedIds) throws RuntimeException {
        if (selectedIds.isEmpty()) {
            return 0L;
        }

        List<Date> startTimeList = (List<Date>) entityManager.createQuery("select ses.startTime from SessionData ses where ses.sessionId in " +
                "(:sessionIds) order by ses.startTime asc")
                .setMaxResults(1)
                .setParameter("sessionIds", selectedIds).getResultList();

        if (startTimeList.isEmpty()) {
            return 0L;
        }

        Date startTime = startTimeList.iterator().next();

        Long lastPosition = (Long) entityManager.createQuery("select count(ses.id) from SessionData ses where startTime<=:startTime")
                .setParameter("startTime", startTime).getSingleResult();

        return lastPosition - 1;
    }

    public List<SessionDataDto> getAll(int start, int length) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");

        long timestamp = System.currentTimeMillis();
        List<SessionDataDto> sessionDataDtoList;
        try {
            sessionDataDtoList = getAllWithMetaData(start, length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (sessionDataDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("There was loaded {} sessions data for {} ms", sessionDataDtoList.size(), System.currentTimeMillis() - timestamp);

        return sessionDataDtoList;
    }

    private List<SessionDataDto> getAllWithMetaData(int start, int length) {

        List<SessionData> sessionDataList = (List<SessionData>)
                entityManager.createQuery("select sd from SessionData as sd order by sd.startTime asc").setFirstResult(start).setMaxResults(length)
                        .getResultList();

        if (sessionDataList == null || sessionDataList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> sessionIds = sessionDataList.stream().map(SessionData::getId).collect(Collectors.toList());

        Map<Long, String> userCommentMap = Collections.EMPTY_MAP;
        if (isUserCommentStorageAvailable) {
            List<Object[]> userComments = entityManager.createQuery(
                    "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                    .setParameter("sessionDataList", sessionDataList)
                    .getResultList();

            if (!userComments.isEmpty()) {
                userCommentMap = new HashMap<>(userComments.size());
                for (Object[] objects : userComments) {
                    userCommentMap.put((Long) objects[0], (String) objects[1]);
                }
            }

        }
        Multimap<Long, TagDto> tagMap = HashMultimap.create();
        if (isTagsStorageAvailable) {
            List<Object[]> sessionTags = entityManager.createNativeQuery("SELECT a.sessions_id, a.tags_name, te.description " +
                    "FROM  TagEntity AS te, (SELECT DISTINCT ste.sessions_id, ste.tags_name FROM SessionTagEntity AS ste WHERE ste.sessions_id IN " +
                    "(:sessionIds)) AS a " +
                    "WHERE a.tags_name=te.name")
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();
            for (Object[] tags : sessionTags) {
                Long sessionId = ((BigInteger) tags[0]).longValue();
                tagMap.put(sessionId, new TagDto((String) tags[1], (String) tags[2]));
            }
        }

        List<SessionDataDto> sessionDataDtoList = new ArrayList<>(sessionDataList.size());

        for (SessionData sessionData : sessionDataList) {
            sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()),
                    new ArrayList<>(tagMap.get(sessionData.getId()))));
        }

        return sessionDataDtoList;
    }

    public List<SessionDataDto> getByDatePeriod(int start, int length, Date from, Date to) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(from, "from is null");
        checkNotNull(to, "to is null");

        long timestamp = System.currentTimeMillis();

        List<SessionDataDto> sessionDataDtoList;
        try {
            List<SessionData> sessionDataList = (List<SessionData>)
                    entityManager.createQuery("select sd from SessionData as sd where sd.startTime between :from and :to order by sd.startTime asc")
                            .setParameter("from", from)
                            .setParameter("to", to)
                            .setFirstResult(start)
                            .setMaxResults(length)
                            .getResultList();

            if (sessionDataList.isEmpty()) {
                return Collections.emptyList();
            }

            List<Long> sessionIds = sessionDataList.stream().map(SessionData::getId).collect(Collectors.toList());
            Map<Long, String> userCommentMap = Collections.emptyMap();

            if (isUserCommentStorageAvailable) {
                List<Object[]> userComments = entityManager.createQuery(
                        "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                        .setParameter("sessionDataList", sessionDataList)
                        .getResultList();

                if (!userComments.isEmpty()) {
                    userCommentMap = new HashMap<>(userComments.size());
                    for (Object[] objects : userComments) {
                        userCommentMap.put((Long) objects[0], (String) objects[1]);
                    }
                }
            }
            Multimap<Long, TagDto> tagMap = HashMultimap.create();

            if (isTagsStorageAvailable) {
                List<Object[]> sessionTags = entityManager.createNativeQuery("SELECT a.sessions_id, a.tags_name, te.description " +
                        "FROM  TagEntity AS te, (SELECT DISTINCT ste.sessions_id, ste.tags_name FROM SessionTagEntity AS ste WHERE ste.sessions_id " +
                        "IN (:sessionIds)) AS a " +
                        "WHERE a.tags_name=te.name")
                        .setParameter("sessionIds", sessionIds)
                        .getResultList();
                for (Object[] tags : sessionTags) {
                    Long sessionId = ((BigInteger) tags[0]).longValue();
                    tagMap.put(sessionId, new TagDto((String) tags[1], (String) tags[2]));
                }
            }

            sessionDataDtoList = new ArrayList<>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()),
                        new ArrayList<>(tagMap.get(sessionData.getId()))));
            }

            log.debug("There was loaded {} sessions data for {} ms", sessionDataDtoList.size(), System.currentTimeMillis() - timestamp);
        } catch (Exception e) {
            log.error("Error was occurred during session data between " + from + " to " + to + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return sessionDataDtoList;
    }

    public List<SessionDataDto> getBySessionIds(int start, int length, Set<String> sessionIds) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(sessionIds, "sessionIds is null");

        boolean isFetchAll = sessionIds.size() == 0; // if input set is empty - fetch all session data.

        long timestamp = System.currentTimeMillis();
        List<SessionDataDto> sessionDataDtoList;
        try {
            Query query = entityManager.createQuery(
                    "select sd from SessionData as sd " +
                            (isFetchAll ? " " : "where sd.sessionId in (:sessionIds) ") +
                            "order by sd.startTime asc"
            ).setFirstResult(start);
            if (!isFetchAll) {
                query.setParameter("sessionIds", new ArrayList<>(sessionIds))
                        .setMaxResults(length);
            }
            List<SessionData> sessionDataList = (List<SessionData>) query.getResultList();
            if (sessionDataList.isEmpty()) {
                return Collections.emptyList();
            }

            Map<Long, String> userCommentMap = Collections.emptyMap();
            if (isUserCommentStorageAvailable) {
                query = entityManager.createQuery(
                        "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd "
                                + (isFetchAll ? " " : " where smd.sessionData in (:sessionDataList)")
                );
                if (!isFetchAll) {
                    query.setParameter("sessionDataList", sessionDataList);
                }
                List<Object[]> userComments = query.getResultList();
                if (!userComments.isEmpty()) {
                    userCommentMap = new HashMap<>(userComments.size());
                    for (Object[] objects : userComments) {
                        userCommentMap.put((Long) objects[0], (String) objects[1]);
                    }
                }
            }

            Multimap<Long, TagDto> tagMap = HashMultimap.create();
            if (isTagsStorageAvailable) {
                query = entityManager.createNativeQuery(
                        "select a.sessions_id, a.tags_name, te.description " +
                                "from  TagEntity as te, " +
                                " (" +
                                "select distinct ste.sessions_id, ste.tags_name " +
                                "from SessionTagEntity as ste " +
                                (isFetchAll ? " " : " where ste.sessions_id in (:ids) ") +
                                ") as a " +
                                "where a.tags_name=te.name "
                );
                if (!isFetchAll) {
                    Set<Long> ids = sessionDataList.stream().map(SessionData::getId).collect(Collectors.toSet());
                    query.setParameter("ids", ids);
                }
                List<Object[]> sessionTags = query.getResultList();
                for (Object[] tags : sessionTags) {
                    Long sessionId = ((BigInteger) tags[0]).longValue();
                    tagMap.put(sessionId, new TagDto((String) tags[1], (String) tags[2]));
                }
            }
            sessionDataDtoList = new ArrayList<>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()),
                        new ArrayList<>(tagMap.get(sessionData.getId()))));
            }

            log.debug("There was loaded {} sessions data for {} ms", sessionDataDtoList.size(), System.currentTimeMillis() - timestamp);
        } catch (Exception e) {
            log.error(
                    "Error was occurred during session data fetching for session Ids {}; start {}, length {}", e,
                    new Object[]{sessionIds, start, length}
            );
            throw new RuntimeException(e);
        }

        return sessionDataDtoList;
    }

    public List<SessionDataDto> getBySessionTagsName(int start, int length, Set<String> sessionTagNames) {
        if (!isTagsStorageAvailable) {
            return Collections.emptyList();
        }

        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(sessionTagNames, "sessionTagNames is null");

        long timestamp = System.currentTimeMillis();

        List<SessionDataDto> sessionDataDtoList;
        List<Long> sessionIds = new ArrayList<>();
        List<BigInteger> ids;

        try {
            ids = entityManager.createNativeQuery("SELECT DISTINCT sd.sessions_id FROM SessionTagEntity AS sd WHERE sd.tags_name IN " +
                    "(:sessionTagNames)")
                    .setParameter("sessionTagNames", new ArrayList<>(sessionTagNames)).getResultList();

            if (ids.isEmpty()) {
                return Collections.emptyList();
            }
            sessionIds.addAll(ids.stream().map(BigInteger::longValue).collect(Collectors.toList()));
            List<SessionData> sessionDataList = (List<SessionData>) entityManager.createQuery("SELECT sd from SessionData as sd where sd.id in " +
                    "(:sessionIds)")
                    .setParameter("sessionIds", sessionIds)
                    .setFirstResult(start)
                    .setMaxResults(length)
                    .getResultList();

            if (sessionDataList.isEmpty()) {
                return Collections.emptyList();
            }

            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (isUserCommentStorageAvailable) {

                List<Object[]> userComments = entityManager.createQuery(
                        "select smd.sessionData.id, smd.userComment from SessionMetaDataEntity as smd where smd.sessionData in (:sessionDataList)")
                        .setParameter("sessionDataList", sessionDataList)
                        .getResultList();

                if (!userComments.isEmpty()) {
                    userCommentMap = new HashMap<>(userComments.size());
                    for (Object[] objects : userComments) {
                        userCommentMap.put((Long) objects[0], (String) objects[1]);
                    }
                }
            }
            Map<Long, ArrayList<TagDto>> tagMap = Collections.EMPTY_MAP;

            if (isTagsStorageAvailable) {
                List<Object[]> sessionTags = entityManager.createNativeQuery("SELECT a.sessions_id, a.tags_name, te.description " +
                        "FROM  TagEntity AS te, (SELECT DISTINCT ste.sessions_id, ste.tags_name FROM SessionTagEntity AS ste WHERE ste.sessions_id " +
                        "IN (:sessionIds)) AS a " +
                        "WHERE a.tags_name=te.name")
                        .setParameter("sessionIds", sessionIds)
                        .getResultList();
                tagMap = new HashMap<>();
                for (Object[] tags : sessionTags) {
                    if (!tagMap.containsKey(((BigInteger) tags[0]).longValue())) {
                        tagMap.put(((BigInteger) tags[0]).longValue(), new ArrayList<>());
                    }
                    tagMap.get(((BigInteger) tags[0]).longValue()).add(new TagDto((String) tags[1], (String) tags[2]));
                }

            }

            sessionDataDtoList = new ArrayList<>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(createSessionDataDto(sessionData, userCommentMap.get(sessionData.getId()), tagMap.get(sessionData.getId())));
            }

            log.debug("There was loaded {} sessions data for {} ms", sessionDataDtoList.size(), System.currentTimeMillis() - timestamp);
        } catch (Exception e) {
            log.error("Error was occurred during session data fetching for session tags " + sessionTagNames + "; start " + start + ", length " +
                    length, e);
            throw new RuntimeException(e);
        }

        return sessionDataDtoList;
    }


    private SessionDataDto createSessionDataDto(SessionData sessionData, String userComment, List<TagDto> tags) {
        return new SessionDataDto(
                sessionData.getId(),
                sessionData.getSessionId(),
                sessionData.getStartTime(),
                sessionData.getEndTime(),
                sessionData.getActiveKernels(),
                sessionData.getTaskExecuted(),
                sessionData.getTaskFailed(),
                HTMLFormatter.format(sessionData.getComment()),
                userComment,
                tags);
    }
}
