package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/30/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class NodePropertyEntity {

    @Id
    // Identity strategy is not supported by Oracle DB from the box
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(length=1000)
    private String name;

    @Column(length=2000)
    private String value;

    @ManyToOne
    private NodeInfoEntity nodeInfoEntity;

    public NodePropertyEntity(String name, String value, NodeInfoEntity nodeInfoEntity) {
        this.name = name;
        this.value = value;
        this.nodeInfoEntity = nodeInfoEntity;
    }

    public NodePropertyEntity() {}

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NodeInfoEntity getNodeInfoEntity() {
        return nodeInfoEntity;
    }

    public void setNodeInfoEntity(NodeInfoEntity nodeInfoEntity) {
        this.nodeInfoEntity = nodeInfoEntity;
    }
}
