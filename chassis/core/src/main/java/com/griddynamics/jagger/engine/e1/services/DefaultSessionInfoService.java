package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.dbapi.entity.TagEntity;

import java.util.Set;

/** Implementation of the @ref SessionInfoService
 * @n
 * @par Details:
 * @details  Service gives ability to create and modify session metadata (such as session comment) @n
 * Where this service is available you can find in chapter: @ref section_listeners_services @n
 * @n
 * @ingroup Main_Services_group */
public class DefaultSessionInfoService implements SessionInfoService {

    private SessionMetaDataStorage metaDataStorage;

    public DefaultSessionInfoService(NodeContext context) {
        metaDataStorage = context.getService(SessionMetaDataStorage.class);
    }

    @Override
    public String getComment() {
        return metaDataStorage.getComment();
    }

    @Override
    public void setComment(String comment) {
        metaDataStorage.setComment(comment);
    }

    @Override
    public void appendToComment(String st) {
        metaDataStorage.appendToComment(st);
    }

    @Override
    public void saveOrUpdateTag(String tagName, String tagDescription) {
        metaDataStorage.addNewOrUpdateTag(new TagEntity(tagName, tagDescription));
    }

    @Override
    public void markSessionWithTag(String tagName) {
        metaDataStorage.addSessionTag(tagName);
    }

    @Override
    public Set<String> getSessionTags() {
        return metaDataStorage.getSessionTags();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
