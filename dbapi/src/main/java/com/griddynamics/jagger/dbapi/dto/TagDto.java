package com.griddynamics.jagger.dbapi.dto;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagDto tagDto = (TagDto) o;

        if (!description.equals(tagDto.description)) return false;
        if (!name.equals(tagDto.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
