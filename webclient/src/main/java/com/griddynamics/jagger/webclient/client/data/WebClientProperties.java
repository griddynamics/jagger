package com.griddynamics.jagger.webclient.client.data;

import java.io.Serializable;

public class WebClientProperties implements Serializable {

    private boolean userCommentAvailable = false;
    private boolean tagsAvailable = false;

    private int userCommentMaxLength = 1000;

    public boolean isUserCommentAvailable() {
        return userCommentAvailable;
    }

    public void setUserCommentAvailable(boolean userCommentAvailable) {
        this.userCommentAvailable = userCommentAvailable;
    }

    public int getUserCommentMaxLength() {
        return userCommentMaxLength;
    }

    public void setUserCommentMaxLength(int userCommentMaxLength) {
        this.userCommentMaxLength = userCommentMaxLength;
    }

    public boolean isTagsAvailable() {
        return tagsAvailable;
    }

    public void setTagsAvailable(boolean tagsAvailable) {
        this.tagsAvailable = tagsAvailable;
    }
}
