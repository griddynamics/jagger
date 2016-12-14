package com.griddynamics.jagger.engine.e1.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/31/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmptySessionInfoService implements SessionInfoService {

    private static Logger log = LoggerFactory.getLogger(EmptyMetricService.class);

    private JaggerPlace jaggerPlace;

    public EmptySessionInfoService(JaggerPlace jaggerPlace) {
        this.jaggerPlace = jaggerPlace;
    }

    @Override
    public String getComment() {
        log.warn("Can't get comment. SessionInfoService is not supported in {}", jaggerPlace);
        return null;
    }

    @Override
    public void setComment(String comment) {
        log.warn("Can't set comment '{}'. SessionInfoService is not supported in {}", comment, jaggerPlace);
    }

    @Override
    public void appendToComment(String st) {
        log.warn("Can't append '{}' to comment. SessionInfoService is not supported in {}", st, jaggerPlace);
    }

    @Override
    public void saveOrUpdateTag(String tagName, String tagDescription) {
        log.warn("Can't save tag {}. SessionInfoService is not supported in {}", tagName, jaggerPlace);
    }

    @Override
    public void markSessionWithTag(String tagName) {
        log.warn("Can't mark session with tag {}. SessionInfoService is not supported in {}", tagName, jaggerPlace);
    }

    @Override
    public Set<String> getSessionTags() {
        log.warn("Can't return session tags. SessionInfoService is not supported in {}", jaggerPlace);
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
