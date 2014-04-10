package com.griddynamics.jagger.dbapi.provider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.DataSaverService;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;
import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.dbapi.entity.TagEntity;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.dbapi.util.HTMLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kgribov on 4/7/14.
 */
public class SessionInfoProviderImpl implements SessionInfoProvider {
    private static final Logger log = LoggerFactory.getLogger(SessionInfoProviderImpl.class);
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    private EntityManager entityManager;
    private WebClientProperties webClientProperties;
    private DataSaverService dataSaverService;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Required
    public void setWebClientProperties(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

    @Required
    public void setDataSaverService(DataSaverService dataSaverService) {
        this.dataSaverService = dataSaverService;
    }

    public List<TagDto> getAllTags() {

        List<TagDto> allTags = new ArrayList<TagDto>();
        if (webClientProperties.isTagsStoreAvailable()) {
            List<TagEntity> tags = entityManager.createQuery("select te from TagEntity as te").getResultList();

            if (!tags.isEmpty()) {
                for (TagEntity tagEntity : tags) {
                    allTags.add(new TagDto(tagEntity.getName(), tagEntity.getDescription()));
                }
            }
        }
        return allTags;
    }

    public void saveTags(Long sessionData_id, List<TagDto> tags) {
        dataSaverService.saveTags(sessionData_id, tags);
    }

    public void saveUserComment(Long sessionData_id, String userComment) throws RuntimeException {
        dataSaverService.saveUserComment(sessionData_id, userComment);
    }

    public Long getTotalSize() throws RuntimeException{
        return (Long) entityManager.createQuery("select count(sessionData.id) from SessionData as sessionData").getSingleResult();
    }

    public Long getTotalSizeByDate(Date from, Date to){
        return (Long)entityManager.createQuery("select count(sd.id) from SessionData as sd where sd.startTime between :from and :to")
                                        .setParameter("from", from)
                                        .setParameter("to", to)
                                        .getSingleResult();
    }

    public Long getTotalSizeByIds(Set<String> sessionIds){
        return (Long)entityManager.createQuery("select count(sd.id) from SessionData as sd where sd.sessionId in (:sessionIds)")
                                        .setParameter("sessionIds", new ArrayList<String>(sessionIds))
                                        .getSingleResult();
    }

    public Long getTotalSizeByTags(Set<String> sessionTagNames){
        return ((Long) entityManager.createNativeQuery("select count(distinct ste.sessions_id) from SessionTagEntity as ste where ste.tags_name in (:sessionTagNames)")
                                            .setParameter("sessionTagNames", new ArrayList<String>(sessionTagNames))
                                            .getSingleResult()).longValue();
    }

    public Long getFirstPosition(Set<String> selectedIds) throws RuntimeException {
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
            return Collections.<SessionDataDto>emptyList();
        }

        log.info("There was loaded {} sessions data for {} ms", new Object[]{sessionDataDtoList.size(), System.currentTimeMillis() - timestamp});

        return sessionDataDtoList;
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
        if (webClientProperties.isUserCommentStoreAvailable()) {
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
        if (webClientProperties.isTagsStoreAvailable()) {
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

    public List<SessionDataDto> getByDatePeriod(int start, int length, Date from, Date to) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(from, "from is null");
        checkNotNull(to, "to is null");

        long timestamp = System.currentTimeMillis();

        List<SessionDataDto> sessionDataDtoList;
        try {
            @SuppressWarnings("unchecked")
            List<SessionData> sessionDataList = (List<SessionData>)
                    entityManager.createQuery("select sd from SessionData as sd where sd.startTime between :from and :to order by sd.startTime asc")
                            .setParameter("from", from)
                            .setParameter("to", to)
                            .setFirstResult(start)
                            .setMaxResults(length)
                            .getResultList();

            if (sessionDataList.isEmpty()) {
                return Collections.<SessionDataDto>emptyList();
            }

            List<Long> sessionIds = new ArrayList<Long>();
            for (int i = 0; i < sessionDataList.size(); i++) {
                sessionIds.add(sessionDataList.get(i).getId());
            }
            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (webClientProperties.isUserCommentStoreAvailable()) {
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

            if (webClientProperties.isTagsStoreAvailable()) {
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

            log.info("There was loaded {} sessions data for {} ms", new Object[]{sessionDataDtoList.size(), System.currentTimeMillis() - timestamp});
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

        long timestamp = System.currentTimeMillis();

        List<SessionDataDto> sessionDataDtoList;

        try {
            @SuppressWarnings("unchecked")
            List<SessionData> sessionDataList = (List<SessionData>)
                    entityManager.createQuery("select sd from SessionData as sd where sd.sessionId in (:sessionIds) order by sd.startTime asc")
                            .setParameter("sessionIds", new ArrayList<String>(sessionIds))
                            .setFirstResult(start)
                            .setMaxResults(length)
                            .getResultList();

            if (sessionDataList.isEmpty()) {
                return Collections.<SessionDataDto>emptyList();
            }

            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (webClientProperties.isUserCommentStoreAvailable()) {

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


            if (webClientProperties.isTagsStoreAvailable()) {
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

            log.info("There was loaded {} sessions data for {} ms", new Object[]{sessionDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during session data fetching for session Ids " + sessionIds + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return sessionDataDtoList;
    }

    public List<SessionDataDto> getBySessionTagsName(int start, int length, Set<String> sessionTagNames) {
        checkArgument(start >= 0, "start is negative");
        checkArgument(length >= 0, "length is negative");
        checkNotNull(sessionTagNames, "sessionTagNames is null");

        long timestamp = System.currentTimeMillis();

        long totalSize;
        List<SessionDataDto> sessionDataDtoList;
        List<Long> sessionIds = new ArrayList<Long>();
        List<BigInteger> ids;

        try {
            ids = entityManager.createNativeQuery("select distinct sd.sessions_id from SessionTagEntity as sd where sd.tags_name in (:sessionTagNames)")
                    .setParameter("sessionTagNames", new ArrayList<String>(sessionTagNames)).getResultList();

            if (ids.isEmpty()){
                return Collections.<SessionDataDto>emptyList();
            }

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
                return Collections.<SessionDataDto>emptyList();
            }

            Map<Long, String> userCommentMap = Collections.EMPTY_MAP;

            if (webClientProperties.isUserCommentStoreAvailable()) {

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

            if (webClientProperties.isTagsStoreAvailable()) {
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

            log.info("There was loaded {} sessions data for {} ms", new Object[]{sessionDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during session data fetching for session tags " + sessionTagNames + "; start " + start + ", length " + length, e);
            throw new RuntimeException(e);
        }

        return sessionDataDtoList;
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
