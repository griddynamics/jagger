package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.dbapi.entity.TagEntity;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/13
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
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
