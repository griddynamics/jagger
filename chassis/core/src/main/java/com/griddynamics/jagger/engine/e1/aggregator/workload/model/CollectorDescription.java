package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: amikryukov
 * Date: 11/15/13
 */

@Entity
public class CollectorDescription {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String displayName;

    public CollectorDescription() {}

    public CollectorDescription(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public CollectorDescription(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return displayName if it's not null, and name otherwise.
     */
    public String getDisplay() {
        if (displayName == null) {
            return name;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectorDescription that = (CollectorDescription) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }
}