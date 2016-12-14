package com.griddynamics.jagger.dbapi;

import com.griddynamics.jagger.dbapi.dto.TagDto;
import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.dbapi.entity.TagEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: mnovozhilov
 * Date: 3/14/14
 * Time: 6:01 PM
 */
@Component
public class DataSaverServiceImpl implements DataSaverService {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager.getEntityManagerFactory().createEntityManager();
    }

    //synchronized because we use not thread safe entity manager
    @Override
    public synchronized void saveUserComment(Long sessionDataId, String userComment) {

        Number number = (Number) entityManager.createQuery(
                "select count(*) from SessionMetaDataEntity as sm where sm.sessionData.id=:sessionData_id")
                .setParameter("sessionData_id", sessionDataId)
                .getSingleResult();

        if (number.intValue() == 0) {
            // create new SessionMetaInfo
            // do not save empty comments
            if (userComment.isEmpty()) {
                return;
            }

            try {
                entityManager.getTransaction().begin();
                entityManager.createNativeQuery(
                        "INSERT INTO SessionMetaDataEntity (userComment, sessionData_id) " +
                                "VALUES (:userComment, :sessionData_id)")
                        .setParameter("userComment", userComment)
                        .setParameter("sessionData_id", sessionDataId)
                        .executeUpdate();

            } finally {
                entityManager.getTransaction().commit();
            }
        } else {
            // update/delete

            if (userComment.isEmpty()) {
                // delete
                try {
                    entityManager.getTransaction().begin();
                    entityManager.createQuery(
                            "delete SessionMetaDataEntity where sessionData.id=:sessionData_id")
                            .setParameter("sessionData_id", sessionDataId)
                            .executeUpdate();
                } finally {
                    entityManager.getTransaction().commit();
                }
            } else {

                // update
                try {
                    entityManager.getTransaction().begin();
                    entityManager.createNativeQuery(
                            "UPDATE SessionMetaDataEntity smd SET smd.userComment=:userComment " +
                                    "WHERE smd.sessionData_id=:sessionData_id")
                            .setParameter("userComment", userComment)
                            .setParameter("sessionData_id", sessionDataId)
                            .executeUpdate();
                } finally {
                    entityManager.getTransaction().commit();
                }
            }
        }
    }

    @Override
    public synchronized void saveTags(Long sessionDataId, List<TagDto> tags) {
        Set<TagEntity> tagEntities = new HashSet<>();
        SessionData sessionData;
        tagEntities.addAll(tags.stream().map(tagDto -> new TagEntity(tagDto.getName(), tagDto.getDescription())).collect(Collectors.toList()));
        try {
            entityManager.getTransaction().begin();
            sessionData = entityManager.find(SessionData.class, sessionDataId);
            if (sessionData != null) {
                sessionData.setTags(tagEntities);
                entityManager.merge(sessionData);
                entityManager.flush();
            }
        } finally {
            entityManager.getTransaction().commit();
        }
    }
}
