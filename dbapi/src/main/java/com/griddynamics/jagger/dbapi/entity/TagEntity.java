package com.griddynamics.jagger.dbapi.entity;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 1/17/14
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class TagEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String name;
    @Column(length = 100)
    private String description;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    private Set<SessionData> sessions = Collections.EMPTY_SET;

    public TagEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public TagEntity() {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SessionData> getSessions() {
        return sessions;
    }

    public void setSessions(Set<SessionData> sessions) {
        this.sessions = sessions;
    }
}
