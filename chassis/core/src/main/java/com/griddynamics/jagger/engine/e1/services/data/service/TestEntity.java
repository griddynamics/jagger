package com.griddynamics.jagger.engine.e1.services.data.service;

import com.griddynamics.jagger.util.Decision;

import java.util.Date;

/** Class is a model of test
 *
 * @details
 * TestEntity is used to get test results from database with use of @ref DataService
 *
 * @author
 * Gribov Kirill
 */
public class TestEntity {
    private Long id;
    private String name;
    private String description;

    /** Description of the load for this test */
    private String load;
    private String terminationStrategy;
    private Date startDate;
    private Integer testGroupIndex;
    private Decision testExecutionStatus;
    private Decision decision;

    /** Get test name in format [test group name] [test name] */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Get description of the load for this test */
    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    /** Get description of the termination strategy for this test */
    public String getTerminationStrategy() {
        return terminationStrategy;
    }

    public void setTerminationStrategy(String terminationStrategy) {
        this.terminationStrategy = terminationStrategy;
    }

    /** Get test id (aka task id) - unique id of this test */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Get test description */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** Get start date */
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /** Get index of test group where this test was executed */
    public Integer getTestGroupIndex() {
        return testGroupIndex;
    }

    public void setTestGroupIndex(Integer testGroupIndex) {
        this.testGroupIndex = testGroupIndex;
    }

    /** Get status of execution of this test. FATAL when test failed during execution (f.e. due to some workload configuration timeout) */
    public Decision getTestExecutionStatus() {
        return testExecutionStatus;
    }

    public void setTestExecutionStatus(Decision testExecutionStatus) {
        this.testExecutionStatus = testExecutionStatus;
    }

    /** Get decision per test if limits based decision maker was used during this test */
    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEntity that = (TestEntity) o;

        if (decision != that.decision) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (load != null ? !load.equals(that.load) : that.load != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (terminationStrategy != null ? !terminationStrategy.equals(that.terminationStrategy) : that.terminationStrategy != null)
            return false;
        if (testExecutionStatus != that.testExecutionStatus) return false;
        if (testGroupIndex != null ? !testGroupIndex.equals(that.testGroupIndex) : that.testGroupIndex != null)
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
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (testGroupIndex != null ? testGroupIndex.hashCode() : 0);
        result = 31 * result + (testExecutionStatus != null ? testExecutionStatus.hashCode() : 0);
        result = 31 * result + (decision != null ? decision.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", load='" + load + '\'' +
                ", terminationStrategy='" + terminationStrategy + '\'' +
                ", startDate='" + startDate + '\'' +
                ", testGroupIndex=" + testGroupIndex +
                ", testExecutionStatus=" + testExecutionStatus +
                ", decision=" + decision +
                '}';
    }
}
