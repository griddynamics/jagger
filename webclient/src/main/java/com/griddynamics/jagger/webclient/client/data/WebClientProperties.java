package com.griddynamics.jagger.webclient.client.data;

import java.io.Serializable;

public class WebClientProperties implements Serializable {

    private boolean tagsAvailable = false;
    private boolean userCommentEditAvailable = false;
    private boolean userCommentStoreAvailable = false;
    private boolean showOnlyMatchedTests = true;




    private int userCommentMaxLength = 1000;

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

    public boolean isShowOnlyMatchedTests() {
        return showOnlyMatchedTests;
    }

    public void setShowOnlyMatchedTests(boolean showOnlyMatchedTests) {
        this.showOnlyMatchedTests = showOnlyMatchedTests;
    }
}
