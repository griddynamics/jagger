package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;

import java.util.List;

public interface TestEnvironmentDao extends CrudDao<TestEnvironmentEntity, String> {

    /**
     * @param testEnvironmentId id of test environment.
     * @param sessionId         session id of test environment.
     * @return true if test environment with such id and sessionId exists, false otherwise.
     */
    boolean existsWithSessionId(String testEnvironmentId, String sessionId);

    /**
     * Retrieves test environments which expirationTimestamp < timestamp.
     * Parameter timestamp must be in UTC timezone.
     *
     * @param timestamp timestamp to compare with test environments' expirationTimestamps.
     * @return list of expired test environments.
     */
    List<TestEnvironmentEntity> readExpired(long timestamp);

    /**
     * Deletes test environments which expirationTimestamp < timestamp.
     * Parameter timestamp must be in UTC timezone.
     *
     * @param timestamp the timestamp to compare with test environments' expirationTimestamps.
     * @return the number of deleted environments.
     */
    int deleteExpired(long timestamp);
}
