package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/22/14
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagDto implements Serializable {
    private String name;
    private String description;

    public TagDto(){
    }

    public TagDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String tagDescription) {
        this.description = tagDescription;
    }
}
