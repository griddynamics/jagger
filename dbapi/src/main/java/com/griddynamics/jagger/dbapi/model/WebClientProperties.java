package com.griddynamics.jagger.dbapi.model;

import java.io.Serializable;

/**
 * Web Ui properties holder.
 */
public class WebClientProperties implements Serializable {

    private boolean tagsAvailable = false;
    private boolean tagsStoreAvailable = false;
    private boolean userCommentEditAvailable = false;
    private boolean userCommentStoreAvailable = false;
    private boolean showOnlyMatchedTests = true;
    private int userCommentMaxLength = 1000;
    private boolean enableDecisionsPerMetricHighlighting = true;
    private boolean combineSynonymsInSummary = true;

    public boolean isTagsAvailable() {
        return tagsAvailable;
    }

    public void setTagsAvailable(boolean tagsAvailable) {
        this.tagsAvailable = tagsAvailable;
    }

    public boolean isUserCommentEditAvailable() {
        return userCommentEditAvailable;
    }

    public void setUserCommentEditAvailable(boolean userCommentEditAvailable) {
        this.userCommentEditAvailable = userCommentEditAvailable;
    }

    public boolean isUserCommentStoreAvailable() {
        return userCommentStoreAvailable;
    }

    public void setUserCommentStoreAvailable(boolean userCommentStoreAvailable) {
        this.userCommentStoreAvailable = userCommentStoreAvailable;
    }

    public int getUserCommentMaxLength() {
        return userCommentMaxLength;
    }

    public void setUserCommentMaxLength(int userCommentMaxLength) {
        this.userCommentMaxLength = userCommentMaxLength;
    }

    public boolean isTagsStoreAvailable() {
        return tagsStoreAvailable;
    }

    public void setTagsStoreAvailable(boolean tagsStoreAvailable) {
        this.tagsStoreAvailable = tagsStoreAvailable;
    }

    public boolean isShowOnlyMatchedTests() {
        return showOnlyMatchedTests;
    }

    public void setShowOnlyMatchedTests(boolean showOnlyMatchedTests) {
        this.showOnlyMatchedTests = showOnlyMatchedTests;
    }

    public boolean isEnableDecisionsPerMetricHighlighting() {
        return enableDecisionsPerMetricHighlighting;
    }

    public void setEnableDecisionsPerMetricHighlighting(boolean enableDecisionsPerMetricHighlighting) {
        this.enableDecisionsPerMetricHighlighting = enableDecisionsPerMetricHighlighting;
    }

    /**
     * Combine summary/trend values within one MetricNode for synonyms.
     */
    public boolean isCombineSynonymsInSummary() {
        return combineSynonymsInSummary;
    }

    public void setCombineSynonymsInSummary(boolean combineSynonymsInSummary) {
        this.combineSynonymsInSummary = combineSynonymsInSummary;
    }
}
