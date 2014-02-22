package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.CommonDataService;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;
import org.springframework.beans.factory.annotation.Required;

public class CommonDataServiceImpl implements CommonDataService {

    private boolean userCommentAvailable;
    private boolean tagsAvailable;
    private int userCommentMaxLength;

    @Required
    public void setUserCommentAvailable(boolean userCommentAvailable) {
        this.userCommentAvailable = userCommentAvailable;
    }

    @Required
    public void setUserCommentMaxLength(int userCommentMaxLength) {
        this.userCommentMaxLength = userCommentMaxLength;
    }

    @Required
    public void setTagsAvailable(boolean tagsAvailable){
        this.tagsAvailable=tagsAvailable;
    }

    @Override
    public WebClientProperties getWebClientProperties() {
        WebClientProperties webClientProperties = new WebClientProperties();

        webClientProperties.setUserCommentAvailable(userCommentAvailable);
        webClientProperties.setUserCommentMaxLength(userCommentMaxLength);
        webClientProperties.setTagsAvailable(tagsAvailable);
        return webClientProperties;
    }
}
