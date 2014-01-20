package com.griddynamics.jagger.dbapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestEntity {
    private Long id;
    private String name;
    private String description;
    private String load;
    private String terminationStrategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getTerminationStrategy() {
        return terminationStrategy;
    }

    public void setTerminationStrategy(String terminationStrategy) {
        this.terminationStrategy = terminationStrategy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEntity that = (TestEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (load != null ? !load.equals(that.load) : that.load != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (terminationStrategy != null ? !terminationStrategy.equals(that.terminationStrategy) : that.terminationStrategy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (load != null ? load.hashCode() : 0);
        result = 31 * result + (terminationStrategy != null ? terminationStrategy.hashCode() : 0);
        return result;
    }
}
