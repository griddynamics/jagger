package com.griddynamics.jagger.dbapi;

import com.griddynamics.jagger.dbapi.dto.TagDto;
import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.dbapi.entity.TagEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 3/14/14
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSaverServiceImpl implements DataSaverService {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
       this.entityManager = entityManager.getEntityManagerFactory().createEntityManager();
    }

    //synchronized because we use not thread safe entity manager
    @Override
    public synchronized void saveUserComment(Long sessionData_id, String userComment) {

        Number number = (Number) entityManager.createQuery(
                "select count(*) from SessionMetaDataEntity as sm where sm.sessionData.id=:sessionData_id")
                .setParameter("sessionData_id", sessionData_id)
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
                        "insert into SessionMetaDataEntity (userComment, sessionData_id) " +
                                "values (:userComment, :sessionData_id)")
                        .setParameter("userComment", userComment)
                        .setParameter("sessionData_id", sessionData_id)
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
                            .setParameter("sessionData_id", sessionData_id)
                            .executeUpdate();
                } finally {
                    entityManager.getTransaction().commit();
                }
            } else {

                // update
                try {
                    entityManager.getTransaction().begin();
                    entityManager.createNativeQuery(
                            "update SessionMetaDataEntity smd set smd.userComment=:userComment " +
                                    "where smd.sessionData_id=:sessionData_id")
                            .setParameter("userComment", userComment)
                            .setParameter("sessionData_id", sessionData_id)
                            .executeUpdate();
                } finally {
                    entityManager.getTransaction().commit();
                }
            }
        }
    }

    @Override
    public synchronized void saveTags(Long sessionData_id, List<TagDto> tags) {
        Set<TagEntity> tagEntities = new HashSet<TagEntity>();
        SessionData sessionData;
        for (TagDto tagDto : tags) {
            tagEntities.add(new TagEntity(tagDto.getName(), tagDto.getDescription()));
        }
        try {
            entityManager.getTransaction().begin();
            sessionData = entityManager.find(SessionData.class,sessionData_id);
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
